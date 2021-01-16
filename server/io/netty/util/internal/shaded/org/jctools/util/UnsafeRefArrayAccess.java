package io.netty.util.internal.shaded.org.jctools.util;

public final class UnsafeRefArrayAccess {
   public static final long REF_ARRAY_BASE;
   public static final int REF_ELEMENT_SHIFT;

   public UnsafeRefArrayAccess() {
      super();
   }

   public static <E> void spElement(E[] var0, long var1, E var3) {
      UnsafeAccess.UNSAFE.putObject(var0, var1, var3);
   }

   public static <E> void soElement(E[] var0, long var1, E var3) {
      UnsafeAccess.UNSAFE.putOrderedObject(var0, var1, var3);
   }

   public static <E> E lpElement(E[] var0, long var1) {
      return UnsafeAccess.UNSAFE.getObject(var0, var1);
   }

   public static <E> E lvElement(E[] var0, long var1) {
      return UnsafeAccess.UNSAFE.getObjectVolatile(var0, var1);
   }

   public static long calcElementOffset(long var0) {
      return REF_ARRAY_BASE + (var0 << REF_ELEMENT_SHIFT);
   }

   static {
      int var0 = UnsafeAccess.UNSAFE.arrayIndexScale(Object[].class);
      if (4 == var0) {
         REF_ELEMENT_SHIFT = 2;
      } else {
         if (8 != var0) {
            throw new IllegalStateException("Unknown pointer size");
         }

         REF_ELEMENT_SHIFT = 3;
      }

      REF_ARRAY_BASE = (long)UnsafeAccess.UNSAFE.arrayBaseOffset(Object[].class);
   }
}
