package com.mojang.renderpearl.util.dx.dxgi;

import com.mojang.renderpearl.util.dx.COMUnknown;
import com.mojang.renderpearl.util.dx.DXUtils;
import com.mojang.renderpearl.util.dx.IID;
import com.mojang.renderpearl.util.dx.OutPtrHelper;
import com.mojang.renderpearl.util.dx.d3d12.D3D12CommandQueue;
import java.lang.foreign.Arena;
import java.lang.foreign.FunctionDescriptor;
import java.lang.foreign.MemorySegment;
import java.lang.invoke.MethodHandle;

public class DXGIFactory5 extends DXGIObject {
   private static final MethodHandle createDxgiFactory2;
   private final MethodHandle makeWindowAssociation;
   private final MethodHandle createSwapChainForHwnd;
   private final MethodHandle enumAdapterByLuid;
   private final MethodHandle checkFeatureSupport;

   public static DXGIFactory5 create() {
      try (OutPtrHelper outPtr = new OutPtrHelper()) {
         DXUtils.crashIfFailure(createDxgiFactory2.invokeExact(0, outPtr.allocate(IID.IDXGIFactory5), outPtr.target()));
         return new DXGIFactory5(outPtr.result());
      } catch (Throwable e) {
         throw new RuntimeException(e);
      }
   }

   private DXGIFactory5(final MemorySegment address) {
      super(address);
      this.makeWindowAssociation = this.vtable(8L, FunctionDescriptor.of(COMUnknown.Types.HRESULT, COMUnknown.Types.SELF_PTR, COMUnknown.Types.HWND.withName("hwnd"), COMUnknown.Types.UINT32.withName("Flags")));
      this.createSwapChainForHwnd = this.vtable(15L, FunctionDescriptor.of(COMUnknown.Types.HRESULT, COMUnknown.Types.SELF_PTR, COMUnknown.Types.POINTER.withName("pDevice"), COMUnknown.Types.HWND.withName("hwnd"), COMUnknown.Types.POINTER.withName("pDesc"), COMUnknown.Types.POINTER.withName("pFullscreenDesc"), COMUnknown.Types.POINTER.withName("pRestrictToOutput"), COMUnknown.Types.COM_OUTPTR.withName("IDXGISwapchain1** ppSwapChain")));
      this.enumAdapterByLuid = this.vtable(26L, FunctionDescriptor.of(COMUnknown.Types.HRESULT, COMUnknown.Types.SELF_PTR, COMUnknown.Types.POINTER.withName("AdapterLuid"), COMUnknown.Types.IID_PTR, COMUnknown.Types.COM_OUTPTR.withName("ppvAdapter")));
      this.checkFeatureSupport = this.vtable(28L, FunctionDescriptor.of(COMUnknown.Types.HRESULT, COMUnknown.Types.SELF_PTR, COMUnknown.Types.C_ENUM.withName("feature"), COMUnknown.Types.POINTER.withName("supportData"), COMUnknown.Types.UINT32.withName("supportDataSize")));
   }

   public void makeWindowAssociation(final long hwnd, final int flags) {
      try {
         DXUtils.crashIfFailure(this.makeWindowAssociation.invokeExact(this.address(), hwnd, flags));
      } catch (Throwable e) {
         throw new RuntimeException(e);
      }
   }

   public boolean adapterWithLuidExists(final MemorySegment luid) {
      try (OutPtrHelper outPtr = new OutPtrHelper()) {
         int result = this.enumAdapterByLuid.invokeExact(this.address(), luid, outPtr.allocate(IID.IDXGIAdapter), outPtr.target());
         if (DXUtils.failure(result)) {
            return false;
         } else {
            (new DXGIAdapter(outPtr.result())).close();
            return true;
         }
      } catch (Throwable e) {
         throw new RuntimeException(e);
      }
   }

   public DXGIAdapter enumAdapterByLuid(final MemorySegment luid) {
      try (OutPtrHelper outPtr = new OutPtrHelper()) {
         DXUtils.crashIfFailure(this.enumAdapterByLuid.invokeExact(this.address(), luid, outPtr.allocate(IID.IDXGIAdapter), outPtr.target()));
         return new DXGIAdapter(outPtr.result());
      } catch (Throwable e) {
         throw new RuntimeException(e);
      }
   }

   public DXGISwapchain3 createSwapchain(final D3D12CommandQueue commandQueue, final long hwnd, final DXGISwapchainDesc1 description) {
      try (OutPtrHelper outPtr = new OutPtrHelper()) {
         DXUtils.crashIfFailure(this.createSwapChainForHwnd.invokeExact(this.address(), commandQueue.address(), hwnd, description.address(), MemorySegment.NULL, MemorySegment.NULL, outPtr.target()));
         return new DXGISwapchain3(outPtr.result());
      } catch (Throwable e) {
         throw new RuntimeException(e);
      }
   }

   public boolean checkTearingSupport() {
      try {
         Arena arena = Arena.ofConfined();

         boolean var8;
         label52: {
            try {
               MemorySegment boolPtr = arena.allocate(COMUnknown.Types.C_BOOL);
               int result = this.checkFeatureSupport.invokeExact(this.address(), 0, boolPtr, (int)COMUnknown.Types.C_BOOL.byteSize());
               if (DXUtils.failure(result)) {
                  var8 = false;
                  break label52;
               }

               var8 = boolPtr.get(COMUnknown.Types.C_BOOL, 0L) != 0;
            } catch (Throwable var6) {
               if (arena != null) {
                  try {
                     arena.close();
                  } catch (Throwable var5) {
                     var6.addSuppressed(var5);
                  }
               }

               throw var6;
            }

            if (arena != null) {
               arena.close();
            }

            return var8;
         }

         if (arena != null) {
            arena.close();
         }

         return var8;
      } catch (Throwable e) {
         throw new RuntimeException(e);
      }
   }

   static {
      createDxgiFactory2 = loadStaticFunction("CreateDXGIFactory2", FunctionDescriptor.of(COMUnknown.Types.HRESULT, COMUnknown.Types.UINT32.withName("Flags"), COMUnknown.Types.IID_PTR, COMUnknown.Types.COM_OUTPTR.withName("ppFactory")));
   }
}
