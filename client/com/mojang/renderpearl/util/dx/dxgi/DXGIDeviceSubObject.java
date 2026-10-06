package com.mojang.renderpearl.util.dx.dxgi;

import java.lang.foreign.MemorySegment;

public class DXGIDeviceSubObject extends DXGIObject {
   public DXGIDeviceSubObject(final MemorySegment address) {
      super(address);
   }
}
