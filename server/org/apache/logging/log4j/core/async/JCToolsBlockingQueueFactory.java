package org.apache.logging.log4j.core.async;

import java.util.Collection;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.LockSupport;
import org.apache.logging.log4j.core.config.plugins.Plugin;
import org.apache.logging.log4j.core.config.plugins.PluginAttribute;
import org.apache.logging.log4j.core.config.plugins.PluginFactory;
import org.jctools.queues.MpscArrayQueue;
import org.jctools.queues.MessagePassingQueue.Consumer;

@Plugin(
   name = "JCToolsBlockingQueue",
   category = "Core",
   elementType = "BlockingQueueFactory"
)
public class JCToolsBlockingQueueFactory<E> implements BlockingQueueFactory<E> {
   private final JCToolsBlockingQueueFactory.WaitStrategy waitStrategy;

   private JCToolsBlockingQueueFactory(JCToolsBlockingQueueFactory.WaitStrategy var1) {
      super();
      this.waitStrategy = var1;
   }

   public BlockingQueue<E> create(int var1) {
      return new JCToolsBlockingQueueFactory.MpscBlockingQueue(var1, this.waitStrategy);
   }

   @PluginFactory
   public static <E> JCToolsBlockingQueueFactory<E> createFactory(@PluginAttribute(value = "WaitStrategy",defaultString = "PARK") JCToolsBlockingQueueFactory.WaitStrategy var0) {
      return new JCToolsBlockingQueueFactory(var0);
   }

   private interface Idle {
      int idle(int var1);
   }

   public static enum WaitStrategy {
      SPIN(new JCToolsBlockingQueueFactory.Idle() {
         public int idle(int var1) {
            return var1 + 1;
         }
      }),
      YIELD(new JCToolsBlockingQueueFactory.Idle() {
         public int idle(int var1) {
            Thread.yield();
            return var1 + 1;
         }
      }),
      PARK(new JCToolsBlockingQueueFactory.Idle() {
         public int idle(int var1) {
            LockSupport.parkNanos(1L);
            return var1 + 1;
         }
      }),
      PROGRESSIVE(new JCToolsBlockingQueueFactory.Idle() {
         public int idle(int var1) {
            if (var1 > 200) {
               LockSupport.parkNanos(1L);
            } else if (var1 > 100) {
               Thread.yield();
            }

            return var1 + 1;
         }
      });

      private final JCToolsBlockingQueueFactory.Idle idle;

      private int idle(int var1) {
         return this.idle.idle(var1);
      }

      private WaitStrategy(JCToolsBlockingQueueFactory.Idle var3) {
         this.idle = var3;
      }
   }

   private static final class MpscBlockingQueue<E> extends MpscArrayQueue<E> implements BlockingQueue<E> {
      private final JCToolsBlockingQueueFactory.WaitStrategy waitStrategy;

      MpscBlockingQueue(int var1, JCToolsBlockingQueueFactory.WaitStrategy var2) {
         super(var1);
         this.waitStrategy = var2;
      }

      public int drainTo(Collection<? super E> var1) {
         return this.drainTo(var1, this.capacity());
      }

      public int drainTo(final Collection<? super E> var1, int var2) {
         return this.drain(new Consumer<E>() {
            public void accept(E var1x) {
               var1.add(var1x);
            }
         }, var2);
      }

      public boolean offer(E var1, long var2, TimeUnit var4) throws InterruptedException {
         int var5 = 0;
         long var6 = System.nanoTime() + var4.toNanos(var2);

         while(!this.offer(var1)) {
            if (System.nanoTime() - var6 > 0L) {
               return false;
            }

            var5 = this.waitStrategy.idle(var5);
            if (Thread.interrupted()) {
               throw new InterruptedException();
            }
         }

         return true;
      }

      public E poll(long var1, TimeUnit var3) throws InterruptedException {
         int var4 = 0;
         long var5 = System.nanoTime() + var3.toNanos(var1);

         do {
            Object var7 = this.poll();
            if (var7 != null) {
               return var7;
            }

            if (System.nanoTime() - var5 > 0L) {
               return null;
            }

            var4 = this.waitStrategy.idle(var4);
         } while(!Thread.interrupted());

         throw new InterruptedException();
      }

      public void put(E var1) throws InterruptedException {
         int var2 = 0;

         while(!this.offer(var1)) {
            var2 = this.waitStrategy.idle(var2);
            if (Thread.interrupted()) {
               throw new InterruptedException();
            }
         }

      }

      public boolean offer(E var1) {
         return this.offerIfBelowThreshold(var1, this.capacity() - 32);
      }

      public int remainingCapacity() {
         return this.capacity() - this.size();
      }

      public E take() throws InterruptedException {
         int var1 = 100;

         do {
            Object var2 = this.relaxedPoll();
            if (var2 != null) {
               return var2;
            }

            var1 = this.waitStrategy.idle(var1);
         } while(!Thread.interrupted());

         throw new InterruptedException();
      }
   }
}
