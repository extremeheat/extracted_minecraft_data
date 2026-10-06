package com.mojang.renderpearl.util.dx.d3d12;

import com.mojang.renderpearl.util.dx.COMUnknown;
import java.lang.foreign.FunctionDescriptor;
import java.lang.foreign.MemorySegment;
import java.lang.invoke.MethodHandle;

public class D3D12Fence extends D3D12Pageable {
   private final MethodHandle setEventOnCompletion;

   public D3D12Fence(final MemorySegment address) {
      super(address);
      this.setEventOnCompletion = this.vtable(9L, FunctionDescriptor.of(COMUnknown.Types.HRESULT, COMUnknown.Types.SELF_PTR, COMUnknown.Types.UINT64.withName("value"), COMUnknown.Types.HANDLE.withName("event")));
   }

   public int waitValue(final long value) {
      try {
         return this.setEventOnCompletion.invokeExact(this.address(), value, 0L);
      } catch (Throwable e) {
         throw new RuntimeException(e);
      }
   }
}
