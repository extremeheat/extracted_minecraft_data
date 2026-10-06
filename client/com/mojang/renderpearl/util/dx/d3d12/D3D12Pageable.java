package com.mojang.renderpearl.util.dx.d3d12;

import java.lang.foreign.MemorySegment;

public class D3D12Pageable extends D3D12DeviceChild {
   public D3D12Pageable(final MemorySegment address) {
      super(address);
   }
}
