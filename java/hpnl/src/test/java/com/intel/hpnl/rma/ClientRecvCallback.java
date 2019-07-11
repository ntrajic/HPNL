// Licensed to the Apache Software Foundation (ASF) under one
// or more contributor license agreements.  See the NOTICE file
// distributed with this work for additional information
// regarding copyright ownership.  The ASF licenses this file
// to you under the Apache License, Version 2.0 (the
// "License"); you may not use this file except in compliance
// with the License.  You may obtain a copy of the License at
//
//   http://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing,
// software distributed under the License is distributed on an
// "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
// KIND, either express or implied.  See the License for the
// specific language governing permissions and limitations
// under the License.

package com.intel.hpnl.rma;

import java.nio.ByteBuffer;

import com.intel.hpnl.core.Handler;
import com.intel.hpnl.core.HpnlBuffer;
import com.intel.hpnl.core.Connection;

public class ClientRecvCallback implements Handler {
  public ClientRecvCallback(boolean is_server, HpnlBuffer buffer) {
    this.is_server = is_server;
    this.buffer = buffer;
  }
  
  public synchronized void handle(final Connection con, int bufferId, int blockBufferSize) {
    HpnlBuffer recvBuffer = con.getRecvBuffer(bufferId);
    assert(recvBuffer != null);
    ByteBuffer recvByteBuffer = recvBuffer.get(blockBufferSize);
    assert(recvByteBuffer != null);
    if (count++ == 0) {
      System.out.println("client recv.");
    }
    long address = recvByteBuffer.getLong();
    long rkey = recvByteBuffer.getLong();
    con.read(buffer.getBufferId(), 0, 4096*1024, address, rkey);
  }
  boolean is_server = false;
  private HpnlBuffer buffer;
  private int count = 0;
}
