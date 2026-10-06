package com.mojang.renderpearl.util.dx.dxgi;

import com.mojang.renderpearl.util.dx.COMUnknown;
import java.lang.foreign.MemorySegment;

public class DXGIObject extends COMUnknown {
   public DXGIObject(final MemorySegment address) {
      super(address);
   }
}
