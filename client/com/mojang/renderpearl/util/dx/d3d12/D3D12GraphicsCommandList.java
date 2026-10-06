package com.mojang.renderpearl.util.dx.d3d12;

import com.mojang.renderpearl.util.dx.COMUnknown;
import java.lang.foreign.FunctionDescriptor;
import java.lang.foreign.MemorySegment;
import java.lang.invoke.MethodHandle;

public class D3D12GraphicsCommandList extends D3D12DeviceChild {
   private final MethodHandle close;
   private final MethodHandle reset;
   private final MethodHandle copyResource;

   public D3D12GraphicsCommandList(final MemorySegment address) {
      super(address);
      this.close = this.vtable(9L, FunctionDescriptor.of(COMUnknown.Types.HRESULT, COMUnknown.Types.SELF_PTR));
      this.reset = this.vtable(10L, FunctionDescriptor.of(COMUnknown.Types.HRESULT, COMUnknown.Types.SELF_PTR, COMUnknown.Types.POINTER.withName("allocator"), COMUnknown.Types.POINTER.withName("initialState")));
      this.copyResource = this.vtable(17L, FunctionDescriptor.ofVoid(COMUnknown.Types.SELF_PTR, COMUnknown.Types.POINTER.withName("dstResource"), COMUnknown.Types.POINTER.withName("srcResource")));
   }

   public int end() {
      try {
         return this.close.invokeExact(this.address());
      } catch (Throwable e) {
         throw new RuntimeException(e);
      }
   }

   public int reset(final D3D12CommandAllocator allocator) {
      try {
         return this.reset.invokeExact(this.address(), allocator.address(), MemorySegment.NULL);
      } catch (Throwable e) {
         throw new RuntimeException(e);
      }
   }

   public void copyResource(final D3D12Resource dst, final D3D12Resource src) {
      try {
         this.copyResource.invokeExact(this.address(), dst.address(), src.address());
      } catch (Throwable e) {
         throw new RuntimeException(e);
      }
   }
}
