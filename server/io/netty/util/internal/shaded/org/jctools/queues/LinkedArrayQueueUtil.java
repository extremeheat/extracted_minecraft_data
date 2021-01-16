package io.netty.util.internal.shaded.org.jctools.queues;

import io.netty.util.internal.shaded.org.jctools.util.UnsafeRefArrayAccess;

final class LinkedArrayQueueUtil {
   private LinkedArrayQueueUtil() {
      super();
   }

   static int length(Object[] var0) {
      return var0.length;
   }

   static long modifiedCalcElementOffset(long var0, long var2) {
      return UnsafeRefArrayAccess.REF_ARRAY_BASE + ((var0 & var2) << UnsafeRefArrayAccess.REF_ELEMENT_SHIFT - 1);
   }

   static long nextArrayOffset(Object[] var0) {
      return UnsafeRefArrayAccess.REF_ARRAY_BASE + ((long)(length(var0) - 1) << UnsafeRefArrayAccess.REF_ELEMENT_SHIFT);
   }
}
