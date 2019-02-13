package com.intel.hpnl.core;

import java.nio.ByteBuffer;
import java.util.concurrent.LinkedBlockingQueue;

public class Connection {

  public Connection(long eqId, long nativeCon, EqService service, String peer_addr, int peer_port) {
    this.service = service;
    this.sendBufferList = new LinkedBlockingQueue<RdmaBuffer>();
    this.peer_addr = peer_addr;
    this.peer_port = peer_port;
    this.eqId = eqId;
    init(nativeCon);
    connected = true;
  }

  public void shutdown(long eq) {
    if(!connected){
      return;
    }
    synchronized (this) {
      if (!connected) {
        return;
      }
      this.service.deregCon(eq);
      this.service.shutdown(eq);
      if (shutdownCallback != null) {
        shutdownCallback.handle(null, 0, 0);
      }
      connected = false;
    }
  }

  public void close(){
    if(!connected){
      return;
    }
    synchronized (this) {
      if(!connected){
        return;
      }
      this.service.deregCon(eqId);
      this.service.shutdown(eqId);
      if (shutdownCallback != null) {
        shutdownCallback.handle(null, 0, 0);
      }
      connected = false;
    }
  }

  public native void recv(ByteBuffer buffer, int id);
  public native void send(int blockBufferSize, int rdmaBufferId);
  public native int read(int rdmaBufferId, int localOffset, long len, long remoteAddr, long remoteMr);
  private native void init(long eq);
  public native void finalize();

  public Handler getConnectedCallback() {
    return connectedCallback;
  }

  public void setConnectedCallback(Handler callback) {
    connectedCallback = callback; 
  }

  public Handler getRecvCallback() {
    return recvCallback; 
  }

  public void setRecvCallback(Handler callback) {
    recvCallback = callback; 
  }

  public Handler getSendCallback() {
    return sendCallback; 
  }

  public void setSendCallback(Handler callback) {
    sendCallback = callback; 
  }

  public void setReadCallback(Handler callback) {
    readCallback = callback; 
  }

  public Handler getShutdownCallback() {
    return shutdownCallback; 
  }

  public void setShutdownCallback(Handler callback) {
    shutdownCallback = callback; 
  }

  public void putSendBuffer(RdmaBuffer buffer) {
    try {
      sendBufferList.put(buffer);
    } catch (InterruptedException e) {
      e.printStackTrace(); 
    }
  }

  public RdmaBuffer takeSendBuffer(boolean wait) {
    if (wait) {
      try {
        return sendBufferList.take();
      } catch (InterruptedException e) {
        e.printStackTrace(); 
      }
      return null;
    } else {
      return sendBufferList.poll();
    }
  }

  public RdmaBuffer getSendBuffer(int rdmaBufferId){
    return service.getSendBuffer(rdmaBufferId);
  }

  public RdmaBuffer getRecvBuffer(int rdmaBufferId) {
    return service.getRecvBuffer(rdmaBufferId);
  }

  public ByteBuffer getRmaBuffer(int rmaBufferId) {
    return service.getRmaBufferByBufferId(rmaBufferId);
  }

  public String getPeerAddr() {
    return this.peer_addr;
  }

  public int getPeerPort() {
    return this.peer_port;
  }

  public void handleCallback(int eventType, int rdmaBufferId, int blockBufferSize) {
    Exception e = null;
    if (eventType == EventType.CONNECTED_EVENT) {
      e = executeCallback(connectedCallback, rdmaBufferId, 0);
    } else if (eventType == EventType.RECV_EVENT) {
      e = executeCallback(recvCallback, rdmaBufferId, blockBufferSize);
    } else if (eventType == EventType.SEND_EVENT) {
      e = executeCallback(sendCallback, rdmaBufferId, blockBufferSize);
      putSendBuffer(service.getSendBuffer(rdmaBufferId));
    } else if (eventType == EventType.READ_EVENT) {
      e = executeCallback(readCallback, rdmaBufferId, blockBufferSize);
    }
    if(e != null){
      e.printStackTrace();
    }
  }

  private Exception executeCallback(Handler handler, int rdmaBufferId, int blockBufferSize){
    if(handler == null){
      return null;
    }
    try{
      handler.handle(this, rdmaBufferId, blockBufferSize);
    }catch(Exception e){
      return e;
    }
    return null;
  }

  private EqService service;
 
  private LinkedBlockingQueue<RdmaBuffer> sendBufferList;

  private String peer_addr;
  private int peer_port;
  private boolean connected;

  private Handler connectedCallback = null;
  private Handler recvCallback = null;
  private Handler sendCallback = null;
  private Handler readCallback = null;
  private Handler shutdownCallback = null;

  private long nativeHandle;
  private final long eqId;
}
