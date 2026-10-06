package com.mojang.renderpearl.util.dx.d3d12;

import com.mojang.renderpearl.util.dx.COMUnknown;
import java.lang.foreign.MemorySegment;

public class D3D12Object extends COMUnknown {
   public D3D12Object(final MemorySegment address) {
      super(address);
   }
}
