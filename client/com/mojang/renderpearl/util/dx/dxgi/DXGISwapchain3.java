package com.mojang.renderpearl.util.dx.dxgi;

import com.mojang.renderpearl.util.dx.COMUnknown;
import com.mojang.renderpearl.util.dx.DXUtils;
import com.mojang.renderpearl.util.dx.IID;
import com.mojang.renderpearl.util.dx.OutPtrHelper;
import com.mojang.renderpearl.util.dx.Win32Handle;
import com.mojang.renderpearl.util.dx.d3d12.D3D12Resource;
import java.lang.foreign.FunctionDescriptor;
import java.lang.foreign.MemorySegment;
import java.lang.invoke.MethodHandle;

public class DXGISwapchain3 extends DXGIDeviceSubObject {
   private final MethodHandle present;
   private final MethodHandle getBuffer;
   private final MethodHandle setMaximumFrameLatency;
   private final MethodHandle getFrameLatencyWaitableObject;
   private final MethodHandle getCurrentBackBufferIndex;

   DXGISwapchain3(final MemorySegment address) {
      super(address);
      this.present = this.vtable(8L, FunctionDescriptor.of(COMUnknown.Types.HRESULT, COMUnknown.Types.SELF_PTR, COMUnknown.Types.UINT32.withName("swapInterval"), COMUnknown.Types.UINT32.withName("Flags")));
      this.getBuffer = this.vtable(9L, FunctionDescriptor.of(COMUnknown.Types.HRESULT, COMUnknown.Types.SELF_PTR, COMUnknown.Types.UINT32.withName("bufferIndex"), COMUnknown.Types.IID_PTR, COMUnknown.Types.COM_OUTPTR.withName("surface")));
      this.setMaximumFrameLatency = this.vtable(31L, FunctionDescriptor.of(COMUnknown.Types.HRESULT, COMUnknown.Types.SELF_PTR, COMUnknown.Types.UINT32.withName("maxLatency")));
      this.getFrameLatencyWaitableObject = this.vtable(33L, FunctionDescriptor.of(COMUnknown.Types.HANDLE, COMUnknown.Types.SELF_PTR));
      this.getCurrentBackBufferIndex = this.vtable(36L, FunctionDescriptor.of(COMUnknown.Types.UINT32, COMUnknown.Types.SELF_PTR));
   }

   public int present(final int syncInterval, final int flags) {
      try {
         return this.present.invokeExact(this.address(), syncInterval, flags);
      } catch (Throwable e) {
         throw new RuntimeException(e);
      }
   }

   public D3D12Resource getBuffer(final int index) {
      try (OutPtrHelper outPtr = new OutPtrHelper()) {
         DXUtils.crashIfFailure(this.getBuffer.invokeExact(this.address(), index, outPtr.allocate(IID.ID3D12Resource), outPtr.target()));
         return new D3D12Resource(outPtr.result());
      } catch (Throwable e) {
         throw new RuntimeException(e);
      }
   }

   public int setMaximumLatency(final int latency) {
      try {
         return this.setMaximumFrameLatency.invokeExact(this.address(), latency);
      } catch (Throwable e) {
         throw new RuntimeException(e);
      }
   }

   public Win32Handle waitHandle() {
      try {
         return new Win32Handle(this.getFrameLatencyWaitableObject.invokeExact(this.address()));
      } catch (Throwable e) {
         throw new RuntimeException(e);
      }
   }

   public int currentBackBufferIndex() {
      try {
         return this.getCurrentBackBufferIndex.invokeExact(this.address());
      } catch (Throwable e) {
         throw new RuntimeException(e);
      }
   }
}
