package com.mojang.renderpearl.util.dx.d3d12;

import com.mojang.renderpearl.util.dx.COMUnknown;
import java.lang.foreign.FunctionDescriptor;
import java.lang.foreign.MemorySegment;
import java.lang.invoke.MethodHandle;

public class D3D12CommandAllocator extends D3D12Pageable {
   private final MethodHandle reset;

   public D3D12CommandAllocator(final MemorySegment address) {
      super(address);
      this.reset = this.vtable(8L, FunctionDescriptor.of(COMUnknown.Types.HRESULT, COMUnknown.Types.SELF_PTR));
   }

   public int reset() {
      try {
         return this.reset.invokeExact(this.address());
      } catch (Throwable e) {
         throw new RuntimeException(e);
      }
   }
}
