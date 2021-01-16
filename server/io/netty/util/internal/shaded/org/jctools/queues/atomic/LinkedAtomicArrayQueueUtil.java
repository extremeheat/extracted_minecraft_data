package io.netty.util.internal.shaded.org.jctools.queues.atomic;

import java.util.concurrent.atomic.AtomicReferenceArray;

final class LinkedAtomicArrayQueueUtil {
   private LinkedAtomicArrayQueueUtil() {
      super();
   }

   public static <E> E lvElement(AtomicReferenceArray<E> var0, int var1) {
      return AtomicReferenceArrayQueue.lvElement(var0, var1);
   }

   public static <E> E lpElement(AtomicReferenceArray<E> var0, int var1) {
      return AtomicReferenceArrayQueue.lpElement(var0, var1);
   }

   public static <E> void spElement(AtomicReferenceArray<E> var0, int var1, E var2) {
      AtomicReferenceArrayQueue.spElement(var0, var1, var2);
   }

   public static <E> void svElement(AtomicReferenceArray<E> var0, int var1, E var2) {
      AtomicReferenceArrayQueue.svElement(var0, var1, var2);
   }

   static <E> void soElement(AtomicReferenceArray var0, int var1, Object var2) {
      var0.lazySet(var1, var2);
   }

   static int calcElementOffset(long var0, long var2) {
      return (int)(var0 & var2);
   }

   static <E> AtomicReferenceArray<E> allocate(int var0) {
      return new AtomicReferenceArray(var0);
   }

   static int length(AtomicReferenceArray<?> var0) {
      return var0.length();
   }

   static int modifiedCalcElementOffset(long var0, long var2) {
      return (int)(var0 & var2) >> 1;
   }

   static int nextArrayOffset(AtomicReferenceArray<?> var0) {
      return length(var0) - 1;
   }
}
