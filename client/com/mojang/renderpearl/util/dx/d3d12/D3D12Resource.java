package com.mojang.renderpearl.util.dx.d3d12;

import java.lang.foreign.MemorySegment;

public final class D3D12Resource extends D3D12Pageable {
   public D3D12Resource(final MemorySegment address) {
      super(address);
   }
}
