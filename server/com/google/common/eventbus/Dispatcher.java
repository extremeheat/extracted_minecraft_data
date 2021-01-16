package com.google.common.eventbus;

import com.google.common.base.Preconditions;
import com.google.common.collect.Queues;
import java.util.Iterator;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

abstract class Dispatcher {
   Dispatcher() {
      super();
   }

   static Dispatcher perThreadDispatchQueue() {
      return new Dispatcher.PerThreadQueuedDispatcher();
   }

   static Dispatcher legacyAsync() {
      return new Dispatcher.LegacyAsyncDispatcher();
   }

   static Dispatcher immediate() {
      return Dispatcher.ImmediateDispatcher.INSTANCE;
   }

   abstract void dispatch(Object var1, Iterator<Subscriber> var2);

   private static final class ImmediateDispatcher extends Dispatcher {
      private static final Dispatcher.ImmediateDispatcher INSTANCE = new Dispatcher.ImmediateDispatcher();

      private ImmediateDispatcher() {
         super();
      }

      void dispatch(Object var1, Iterator<Subscriber> var2) {
         Preconditions.checkNotNull(var1);

         while(var2.hasNext()) {
            ((Subscriber)var2.next()).dispatchEvent(var1);
         }

      }
   }

   private static final class LegacyAsyncDispatcher extends Dispatcher {
      private final ConcurrentLinkedQueue<Dispatcher.LegacyAsyncDispatcher.EventWithSubscriber> queue;

      private LegacyAsyncDispatcher() {
         super();
         this.queue = Queues.newConcurrentLinkedQueue();
      }

      void dispatch(Object var1, Iterator<Subscriber> var2) {
         Preconditions.checkNotNull(var1);

         while(var2.hasNext()) {
            this.queue.add(new Dispatcher.LegacyAsyncDispatcher.EventWithSubscriber(var1, (Subscriber)var2.next()));
         }

         Dispatcher.LegacyAsyncDispatcher.EventWithSubscriber var3;
         while((var3 = (Dispatcher.LegacyAsyncDispatcher.EventWithSubscriber)this.queue.poll()) != null) {
            var3.subscriber.dispatchEvent(var3.event);
         }

      }

      // $FF: synthetic method
      LegacyAsyncDispatcher(Object var1) {
         this();
      }

      private static final class EventWithSubscriber {
         private final Object event;
         private final Subscriber subscriber;

         private EventWithSubscriber(Object var1, Subscriber var2) {
            super();
            this.event = var1;
            this.subscriber = var2;
         }

         // $FF: synthetic method
         EventWithSubscriber(Object var1, Subscriber var2, Object var3) {
            this(var1, var2);
         }
      }
   }

   private static final class PerThreadQueuedDispatcher extends Dispatcher {
      private final ThreadLocal<Queue<Dispatcher.PerThreadQueuedDispatcher.Event>> queue;
      private final ThreadLocal<Boolean> dispatching;

      private PerThreadQueuedDispatcher() {
         super();
         this.queue = new ThreadLocal<Queue<Dispatcher.PerThreadQueuedDispatcher.Event>>() {
            protected Queue<Dispatcher.PerThreadQueuedDispatcher.Event> initialValue() {
               return Queues.newArrayDeque();
            }
         };
         this.dispatching = new ThreadLocal<Boolean>() {
            protected Boolean initialValue() {
               return false;
            }
         };
      }

      void dispatch(Object var1, Iterator<Subscriber> var2) {
         Preconditions.checkNotNull(var1);
         Preconditions.checkNotNull(var2);
         Queue var3 = (Queue)this.queue.get();
         var3.offer(new Dispatcher.PerThreadQueuedDispatcher.Event(var1, var2));
         if (!(Boolean)this.dispatching.get()) {
            this.dispatching.set(true);

            Dispatcher.PerThreadQueuedDispatcher.Event var4;
            try {
               while((var4 = (Dispatcher.PerThreadQueuedDispatcher.Event)var3.poll()) != null) {
                  while(var4.subscribers.hasNext()) {
                     ((Subscriber)var4.subscribers.next()).dispatchEvent(var4.event);
                  }
               }
            } finally {
               this.dispatching.remove();
               this.queue.remove();
            }
         }

      }

      // $FF: synthetic method
      PerThreadQueuedDispatcher(Object var1) {
         this();
      }

      private static final class Event {
         private final Object event;
         private final Iterator<Subscriber> subscribers;

         private Event(Object var1, Iterator<Subscriber> var2) {
            super();
            this.event = var1;
            this.subscribers = var2;
         }

         // $FF: synthetic method
         Event(Object var1, Iterator var2, Object var3) {
            this(var1, var2);
         }
      }
   }
}
