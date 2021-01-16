package io.netty.util.internal.shaded.org.jctools.queues;

public final class IndexedQueueSizeUtil {
   public IndexedQueueSizeUtil() {
      super();
   }

   public static int size(IndexedQueueSizeUtil.IndexedQueue var0) {
      long var1 = var0.lvConsumerIndex();

      long var5;
      long var7;
      do {
         var5 = var1;
         var7 = var0.lvProducerIndex();
         var1 = var0.lvConsumerIndex();
      } while(var5 != var1);

      long var3 = var7 - var1;
      return var3 > 2147483647L ? 2147483647 : (int)var3;
   }

   public static boolean isEmpty(IndexedQueueSizeUtil.IndexedQueue var0) {
      return var0.lvConsumerIndex() == var0.lvProducerIndex();
   }

   public interface IndexedQueue {
      long lvConsumerIndex();

      long lvProducerIndex();
   }
}
