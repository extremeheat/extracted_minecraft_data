package io.netty.util.internal.shaded.org.jctools.queues;

import io.netty.util.internal.shaded.org.jctools.util.UnsafeRefArrayAccess;

public final class CircularArrayOffsetCalculator {
   public CircularArrayOffsetCalculator() {
      super();
   }

   public static <E> E[] allocate(int var0) {
      return new Object[var0];
   }

   public static long calcElementOffset(long var0, long var2) {
      return UnsafeRefArrayAccess.REF_ARRAY_BASE + ((var0 & var2) << UnsafeRefArrayAccess.REF_ELEMENT_SHIFT);
   }
}
