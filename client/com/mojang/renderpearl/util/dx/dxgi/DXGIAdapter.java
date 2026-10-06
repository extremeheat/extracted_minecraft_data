package com.mojang.renderpearl.util.dx.dxgi;

import com.mojang.renderpearl.util.dx.COMUnknown;
import com.mojang.renderpearl.util.dx.DXUtils;
import com.mojang.renderpearl.util.dx.IID;
import com.mojang.renderpearl.util.dx.OutPtrHelper;
import com.mojang.renderpearl.util.dx.d3d12.D3D12Device;
import java.lang.foreign.FunctionDescriptor;
import java.lang.foreign.MemorySegment;
import java.lang.invoke.MethodHandle;

public class DXGIAdapter extends DXGIObject {
   private static final MethodHandle d3d12CreateDevice;

   public DXGIAdapter(final MemorySegment address) {
      super(address);
   }

   public D3D12Device createD3D12Device(final int featureLevel) {
      try (OutPtrHelper outPtr = new OutPtrHelper()) {
         DXUtils.crashIfFailure(d3d12CreateDevice.invokeExact(this.address(), featureLevel, outPtr.allocate(IID.ID3D12Device5), outPtr.target()));
         return new D3D12Device(outPtr.result());
      } catch (Throwable e) {
         throw new RuntimeException(e);
      }
   }

   static {
      d3d12CreateDevice = loadStaticFunction("D3D12CreateDevice", FunctionDescriptor.of(COMUnknown.Types.HRESULT, COMUnknown.Types.POINTER.withName("pAdapter"), COMUnknown.Types.UINT32.withName("MinimumFeatureLevel"), COMUnknown.Types.IID_PTR, COMUnknown.Types.COM_OUTPTR.withName("ppDevice")));
   }
}
