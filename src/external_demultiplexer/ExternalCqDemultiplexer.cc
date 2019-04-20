#include "external_demultiplexer/ExternalCqDemultiplexer.h"
#include "core/FiStack.h"
#include "core/FiConnection.h"
#include "demultiplexer/EventType.h"
#include "demultiplexer/Handle.h"

ExternalCqDemultiplexer::ExternalCqDemultiplexer(FiStack *stack_, fid_cq *cq_) : stack(stack_), cq(cq_), start(0), end(0) {}

ExternalCqDemultiplexer::~ExternalCqDemultiplexer() {
  close(epfd);
}

int ExternalCqDemultiplexer::init() {
  fabric = stack->get_fabric();
  if ((epfd = epoll_create1(0)) == -1) {
    perror("epoll_create1");
    return -1;
  }
  memset((void*)&event, 0, sizeof event);
  if (fi_control(&cq->fid, FI_GETWAIT, (void*)&fd)) {
    perror("fi_control");
    return -1;
  }
  event.events = EPOLLIN;
  event.data.ptr = &cq->fid;
  if (epoll_ctl(epfd, EPOLL_CTL_ADD, fd, &event) < 0) {
    perror("epoll_ctl");
    return -1;
  }
  return 0;
}

int ExternalCqDemultiplexer::wait_event(fid_eq** eq, Chunk** ck, int* buffer_id, int* block_buffer_size) {
  struct fid *fids[1];
  fids[0] = &cq->fid;
  int ret = 0;
  if (end - start >= 200) {
    if (fi_trywait(fabric, fids, 1) == FI_SUCCESS) {
      int epoll_ret = epoll_wait(epfd, &event, 1, 200);
      if (epoll_ret > 0) {
        assert(event.data.ptr == (void*)&cq->fid);
      } else if (epoll_ret < 0) {
        if (errno != EINTR) {
          perror("epoll_wait");
          return -1;
        }
        return 0;
      } else {
        return 0; 
      }
    }
    start = std::chrono::high_resolution_clock::now().time_since_epoch() / std::chrono::microseconds(1);
  }
  fi_cq_msg_entry entry;
  ret = fi_cq_read(cq, &entry, 1);
  if (ret == -FI_EAVAIL) {
    fi_cq_err_entry err_entry;
    fi_cq_readerr(cq, &err_entry, entry.flags);
    end = std::chrono::high_resolution_clock::now().time_since_epoch() / std::chrono::microseconds(1);
    perror("fi_cq_read");
    if (err_entry.err == FI_EOVERRUN) {
      return -1;
    }
    return 0;
  } else if (ret == -FI_EAGAIN) {
    end = std::chrono::high_resolution_clock::now().time_since_epoch() / std::chrono::microseconds(1);
    return 0;
  } else {
    end = start;
    *ck = (Chunk*)entry.op_context;
    *buffer_id = (*ck)->buffer_id;
    FiConnection *con = (FiConnection*)(*ck)->con;
    if (!con) {
      return 0;
    }
    fid_eq *eq_tmp = (fid_eq*)con->get_eqhandle()->get_ctx();
    *eq = eq_tmp;
    if (entry.flags & FI_RECV) {
      std::unique_lock<std::mutex> l(con->con_mtx);
      con->con_cv.wait(l, [con] { return con->status >= CONNECTED; });
      l.unlock();
      con->recv((char*)(*ck)->buffer, entry.len);
      *block_buffer_size = entry.len;
      return RECV_EVENT;
    } else if (entry.flags & FI_SEND) {
      return SEND_EVENT;
    } else if (entry.flags & FI_READ) {
      return READ_EVENT;
    } else if (entry.flags & FI_WRITE) {
      return WRITE_EVENT;
    } else {
      return 0;
    }
  }
  return 0;
}
