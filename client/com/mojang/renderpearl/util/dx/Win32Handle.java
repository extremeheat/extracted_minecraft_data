package com.mojang.renderpearl.util.dx;

import com.mojang.renderpearl.util.UncheckedAutoCloseable;
import java.lang.foreign.FunctionDescriptor;
import java.lang.foreign.Linker;
import java.lang.foreign.MemorySegment;
import java.lang.invoke.MethodHandle;
import org.lwjgl.system.windows.Kernel32;

public record Win32Handle(long handle) implements UncheckedAutoCloseable {
   private static final MethodHandle CloseHandle;
   private static final MethodHandle WaitForSingleObjectEx;

   public Win32Handle {
      super();
   }

   public static MethodHandle loadKernelFunc(final String functionName, final FunctionDescriptor descriptor) {
      long address = Kernel32.getLibrary().getFunctionAddress(functionName);
      return Linker.nativeLinker().downcallHandle(MemorySegment.ofAddress(address), descriptor);
   }

   public void close() {
      try {
         CloseHandle.invoke(this.handle);
      } catch (Throwable e) {
         throw new RuntimeException(e);
      }
   }

   public int waitSingleObject(final int timeout, final boolean alertable) {
      try {
         return WaitForSingleObjectEx.invokeExact(this.handle, timeout, alertable ? 1 : 0);
      } catch (Throwable e) {
         throw new RuntimeException(e);
      }
   }

   static {
      CloseHandle = loadKernelFunc("CloseHandle", FunctionDescriptor.of(COMUnknown.Types.C_BOOL, COMUnknown.Types.HANDLE.withName("handle")));
      WaitForSingleObjectEx = loadKernelFunc("WaitForSingleObjectEx", FunctionDescriptor.of(COMUnknown.Types.UINT32, COMUnknown.Types.HANDLE.withName("handle"), COMUnknown.Types.UINT32.withName("ms"), COMUnknown.Types.C_BOOL.withName("alertable")));
   }
}
