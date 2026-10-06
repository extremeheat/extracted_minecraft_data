package com.mojang.renderpearl.util.dx;

import com.mojang.renderpearl.util.UncheckedAutoCloseable;
import java.lang.foreign.AddressLayout;
import java.lang.foreign.FunctionDescriptor;
import java.lang.foreign.Linker;
import java.lang.foreign.MemorySegment;
import java.lang.foreign.SymbolLookup;
import java.lang.foreign.ValueLayout;
import java.lang.invoke.MethodHandle;
import java.util.Optional;
import java.util.function.Function;
import org.jspecify.annotations.Nullable;

public class COMUnknown implements UncheckedAutoCloseable {
   private static final int MAX_VTABLE_INDEX = 128;
   private static final SymbolLookup SYMBOL_LOOKUP = SymbolLookup.loaderLookup();
   private final MemorySegment address;
   private final MemorySegment vtable;
   private final MethodHandle queryInterface;
   private final MethodHandle release;

   public COMUnknown(final MemorySegment address) {
      super();
      this.address = address;
      this.vtable = address.reinterpret(COMUnknown.Types.POINTER.byteSize()).get(ValueLayout.ADDRESS, 0L).reinterpret(COMUnknown.Types.POINTER.byteSize() * 128L);
      this.queryInterface = this.vtable(0L, FunctionDescriptor.of(COMUnknown.Types.HRESULT, COMUnknown.Types.SELF_PTR, COMUnknown.Types.IID_PTR, COMUnknown.Types.COM_OUTPTR));
      this.release = this.vtable(2L, FunctionDescriptor.of(COMUnknown.Types.UINT32, COMUnknown.Types.SELF_PTR));
   }

   public final void close() {
      try {
         this.release.invoke(this.address);
      } catch (Throwable e) {
         throw new RuntimeException(e);
      }
   }

   public <T extends COMUnknown> @Nullable T queryInterface(final IID iid, final Function<MemorySegment, T> objectConstructor) {
      try (OutPtrHelper outPtr = new OutPtrHelper()) {
         int result = this.queryInterface.invokeExact(this.address(), outPtr.allocate(iid), outPtr.target());
         if (DXUtils.failure(result)) {
            return null;
         } else {
            return (T)(objectConstructor.apply(outPtr.result()));
         }
      } catch (Throwable e) {
         throw new RuntimeException(e);
      }
   }

   public final MemorySegment address() {
      return this.address;
   }

   protected MethodHandle vtable(final long index, final FunctionDescriptor descriptor) {
      if (index < 0L) {
         throw new IllegalArgumentException("Invalid vtable index");
      } else if (index > 128L) {
         throw new IllegalArgumentException("Index too high, increase MAX_VTABLE_INDEX to fix");
      } else {
         return Linker.nativeLinker().downcallHandle(this.vtable.getAtIndex(ValueLayout.ADDRESS, index), descriptor);
      }
   }

   public static MethodHandle loadStaticFunction(final String functionName, final FunctionDescriptor descriptor) {
      Optional<MemorySegment> functionAddress = SYMBOL_LOOKUP.find(functionName);
      if (functionAddress.isEmpty()) {
         throw new IllegalArgumentException("Could not find function " + functionName);
      } else {
         return Linker.nativeLinker().downcallHandle((MemorySegment)functionAddress.get(), descriptor);
      }
   }

   protected static class Types {
      public static final AddressLayout POINTER;
      public static final AddressLayout SELF_PTR;
      public static final ValueLayout.OfInt HRESULT;
      public static final ValueLayout.OfLong HANDLE;
      public static final ValueLayout.OfLong HWND;
      public static final AddressLayout IID_PTR;
      public static final ValueLayout.OfLong UINT64;
      public static final ValueLayout.OfInt UINT32;
      public static final ValueLayout.OfInt C_ENUM;
      public static final ValueLayout.OfInt C_BOOL;
      public static final AddressLayout COM_OUTPTR;

      protected Types() {
         super();
      }

      static {
         POINTER = ValueLayout.ADDRESS;
         SELF_PTR = AddressLayout.ADDRESS.withName("This");
         HRESULT = ValueLayout.JAVA_INT.withName("HRESULT");
         HANDLE = ValueLayout.JAVA_LONG;
         HWND = HANDLE.withName("hwnd");
         IID_PTR = ValueLayout.ADDRESS.withName("iidPtr");
         UINT64 = ValueLayout.JAVA_LONG;
         UINT32 = ValueLayout.JAVA_INT;
         C_ENUM = ValueLayout.JAVA_INT;
         C_BOOL = ValueLayout.JAVA_INT;
         COM_OUTPTR = AddressLayout.ADDRESS.withTargetLayout(AddressLayout.ADDRESS);
      }
   }
}
