package com.mojang.renderpearl.util.dx.d3d12;

import com.mojang.renderpearl.util.dx.COMUnknown;
import com.mojang.renderpearl.util.dx.DXUtils;
import com.mojang.renderpearl.util.dx.IID;
import com.mojang.renderpearl.util.dx.OutPtrHelper;
import com.mojang.renderpearl.util.dx.Win32Handle;
import java.lang.foreign.Arena;
import java.lang.foreign.FunctionDescriptor;
import java.lang.foreign.MemorySegment;
import java.lang.invoke.MethodHandle;

public class D3D12Device extends D3D12Object {
   private final MethodHandle createCommandQueue;
   private final MethodHandle createCommandAllocator;
   private final MethodHandle createCommandList;
   private final MethodHandle createCommittedResource;
   private final MethodHandle createSharedHandle;
   private final MethodHandle openSharedHandle;
   private final MethodHandle createFence;

   public D3D12Device(final MemorySegment address) {
      super(address);
      this.createCommandQueue = this.vtable(8L, FunctionDescriptor.of(COMUnknown.Types.HRESULT, COMUnknown.Types.SELF_PTR, COMUnknown.Types.POINTER.withName("desc"), COMUnknown.Types.IID_PTR, COMUnknown.Types.COM_OUTPTR.withName("ppCommandQueue")));
      this.createCommandAllocator = this.vtable(9L, FunctionDescriptor.of(COMUnknown.Types.HRESULT, COMUnknown.Types.SELF_PTR, COMUnknown.Types.C_ENUM.withName("type"), COMUnknown.Types.IID_PTR, COMUnknown.Types.COM_OUTPTR.withName("ppCommandQueue")));
      this.createCommandList = this.vtable(12L, FunctionDescriptor.of(COMUnknown.Types.HRESULT, COMUnknown.Types.SELF_PTR, COMUnknown.Types.UINT32.withName("nodeMask"), COMUnknown.Types.C_ENUM.withName("type"), COMUnknown.Types.POINTER.withName("commandAllocator"), COMUnknown.Types.POINTER.withName("initialPipelineState"), COMUnknown.Types.IID_PTR, COMUnknown.Types.COM_OUTPTR.withName("ppCommandQueue")));
      this.createCommittedResource = this.vtable(27L, FunctionDescriptor.of(COMUnknown.Types.HRESULT, COMUnknown.Types.SELF_PTR, COMUnknown.Types.POINTER.withName("HeapProperties"), COMUnknown.Types.C_ENUM.withName("HeapFlags"), COMUnknown.Types.POINTER.withName("Desc"), COMUnknown.Types.C_ENUM.withName("InitialResourceState"), COMUnknown.Types.POINTER.withName("OptimizedClearValue"), COMUnknown.Types.IID_PTR, COMUnknown.Types.COM_OUTPTR.withName("Resource")));
      this.createSharedHandle = this.vtable(31L, FunctionDescriptor.of(COMUnknown.Types.HRESULT, COMUnknown.Types.SELF_PTR, COMUnknown.Types.POINTER.withName("object"), COMUnknown.Types.POINTER.withName("attributes"), COMUnknown.Types.UINT32.withName("access"), COMUnknown.Types.POINTER.withName("name"), COMUnknown.Types.POINTER.withName("handle").withTargetLayout(COMUnknown.Types.HANDLE)));
      this.openSharedHandle = this.vtable(32L, FunctionDescriptor.of(COMUnknown.Types.HRESULT, COMUnknown.Types.SELF_PTR, COMUnknown.Types.HANDLE.withName("handle"), COMUnknown.Types.IID_PTR, COMUnknown.Types.COM_OUTPTR.withName("object")));
      this.createFence = this.vtable(36L, FunctionDescriptor.of(COMUnknown.Types.HRESULT, COMUnknown.Types.SELF_PTR, COMUnknown.Types.UINT64.withName("initialValue"), COMUnknown.Types.C_ENUM.withName("flags"), COMUnknown.Types.IID_PTR, COMUnknown.Types.COM_OUTPTR.withName("fence")));
   }

   public D3D12CommandQueue createCommandQueue(final D3D12CommandQueueDesc desc) {
      try (OutPtrHelper outPtr = new OutPtrHelper()) {
         DXUtils.crashIfFailure(this.createCommandQueue.invokeExact(this.address(), desc.address(), outPtr.allocate(IID.ID3D12CommandQueue), outPtr.target()));
         return new D3D12CommandQueue(outPtr.result());
      } catch (Throwable e) {
         throw new RuntimeException(e);
      }
   }

   public D3D12CommandAllocator createCommandAllocator(final int type) {
      try (OutPtrHelper outPtr = new OutPtrHelper()) {
         DXUtils.crashIfFailure(this.createCommandAllocator.invokeExact(this.address(), type, outPtr.allocate(IID.ID3D12CommandAllocator), outPtr.target()));
         return new D3D12CommandAllocator(outPtr.result());
      } catch (Throwable e) {
         throw new RuntimeException(e);
      }
   }

   public D3D12GraphicsCommandList createCommandList(final int nodeMask, final int type, final D3D12CommandAllocator commandAllocator) {
      try (OutPtrHelper outPtr = new OutPtrHelper()) {
         DXUtils.crashIfFailure(this.createCommandList.invokeExact(this.address(), nodeMask, type, commandAllocator.address(), MemorySegment.NULL, outPtr.allocate(IID.ID3D12CommandList), outPtr.target()));
         return new D3D12GraphicsCommandList(outPtr.result());
      } catch (Throwable e) {
         throw new RuntimeException(e);
      }
   }

   public D3D12Resource createCommittedResource(final D3D12HeapProperties heapProperties, final int heapFlags, final D3D12ResourceDesc desc, final int initialResourceState) {
      try (OutPtrHelper outPtr = new OutPtrHelper()) {
         DXUtils.crashIfFailure(this.createCommittedResource.invokeExact(this.address(), heapProperties.address(), heapFlags, desc.address(), initialResourceState, MemorySegment.NULL, outPtr.allocate(IID.ID3D12Resource), outPtr.target()));
         return new D3D12Resource(outPtr.result());
      } catch (Throwable e) {
         throw new RuntimeException(e);
      }
   }

   public Win32Handle createSharedHandle(final D3D12DeviceChild object) {
      try {
         Arena arena = Arena.ofConfined();

         Win32Handle var4;
         try {
            MemorySegment handlePtr = arena.allocate(COMUnknown.Types.HANDLE);
            DXUtils.crashIfFailure(this.createSharedHandle.invokeExact(this.address(), object.address(), MemorySegment.NULL, 268435456, MemorySegment.NULL, handlePtr));
            var4 = new Win32Handle(handlePtr.get(COMUnknown.Types.HANDLE, 0L));
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

         return var4;
      } catch (Throwable e) {
         throw new RuntimeException(e);
      }
   }

   public D3D12Resource openSharedHandle(final Win32Handle handle) {
      try (OutPtrHelper outPtr = new OutPtrHelper()) {
         DXUtils.crashIfFailure(this.openSharedHandle.invokeExact(this.address(), handle.handle(), outPtr.allocate(IID.ID3D12Resource), outPtr.target()));
         return new D3D12Resource(outPtr.result());
      } catch (Throwable e) {
         throw new RuntimeException(e);
      }
   }

   public D3D12Fence createFence(final long initialValue, final int flags) {
      try (OutPtrHelper outPtr = new OutPtrHelper()) {
         DXUtils.crashIfFailure(this.createFence.invokeExact(this.address(), initialValue, flags, outPtr.allocate(IID.ID3D12Fence), outPtr.target()));
         return new D3D12Fence(outPtr.result());
      } catch (Throwable e) {
         throw new RuntimeException(e);
      }
   }
}
