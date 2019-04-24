package com.intel.hpnl.core;

import java.util.concurrent.atomic.AtomicBoolean;

public class CqThread extends Thread {
  public CqThread(CqService cqService, int index, long affinity) {
    this.cqService = cqService;
    this.index = index; 
    this.affinity = affinity;
    running.set(true);
    this.setDaemon(true);
  }

  public void run() {
    if (this.affinity != -1)
      Utils.setAffinity(this.affinity);
    while (running.get()) {
      if (this.cqService.wait_event(index) == -1) {
        shutdown();
      }
    }
    this.cqService.free();
  }

  public void shutdown() {
    running.set(false); 
  }

  private CqService cqService;
  private int index;
  private long affinity;
  private final AtomicBoolean running = new AtomicBoolean(false);
}
