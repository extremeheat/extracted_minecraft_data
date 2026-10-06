package com.mojang.renderpearl.util.dx.d3d12;

import com.mojang.renderpearl.util.dx.COMUnknown;
import java.lang.foreign.Arena;
import java.lang.foreign.FunctionDescriptor;
import java.lang.foreign.MemorySegment;
import java.lang.invoke.MethodHandle;

public class D3D12CommandQueue extends D3D12Pageable {
   private final MethodHandle executeCommandLists;
   private final MethodHandle signal;
   private final MethodHandle wait;

   public D3D12CommandQueue(final MemorySegment address) {
      super(address);
      this.executeCommandLists = this.vtable(10L, FunctionDescriptor.of(COMUnknown.Types.HRESULT, COMUnknown.Types.SELF_PTR, COMUnknown.Types.UINT32.withName("commandListCount"), COMUnknown.Types.POINTER.withName("ppCommandLists")));
      this.signal = this.vtable(14L, FunctionDescriptor.of(COMUnknown.Types.HRESULT, COMUnknown.Types.SELF_PTR, COMUnknown.Types.POINTER.withName("fence"), COMUnknown.Types.UINT64.withName("value")));
      this.wait = this.vtable(15L, FunctionDescriptor.of(COMUnknown.Types.HRESULT, COMUnknown.Types.SELF_PTR, COMUnknown.Types.POINTER.withName("fence"), COMUnknown.Types.UINT64.withName("value")));
   }

   public int executeCommandList(final D3D12GraphicsCommandList commandList) {
      try {
         Arena arena = Arena.ofConfined();

         int var4;
         try {
            MemorySegment listList = arena.allocate(COMUnknown.Types.POINTER);
            listList.set(COMUnknown.Types.POINTER, 0L, commandList.address());
            var4 = this.executeCommandLists.invokeExact(this.address(), 1, listList);
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

   public int signal(final D3D12Fence fence, final long value) {
      try {
         return this.signal.invokeExact(this.address(), fence.address(), value);
      } catch (Throwable e) {
         throw new RuntimeException(e);
      }
   }

   public int wait(final D3D12Fence fence, final long value) {
      try {
         return this.wait.invokeExact(this.address(), fence.address(), value);
      } catch (Throwable e) {
         throw new RuntimeException(e);
      }
   }
}
