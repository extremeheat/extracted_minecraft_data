package com.google.common.eventbus;

import com.google.common.annotations.VisibleForTesting;
import com.google.common.base.Preconditions;
import com.google.j2objc.annotations.Weak;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.concurrent.Executor;
import javax.annotation.Nullable;

class Subscriber {
   @Weak
   private EventBus bus;
   @VisibleForTesting
   final Object target;
   private final Method method;
   private final Executor executor;

   static Subscriber create(EventBus var0, Object var1, Method var2) {
      return (Subscriber)(isDeclaredThreadSafe(var2) ? new Subscriber(var0, var1, var2) : new Subscriber.SynchronizedSubscriber(var0, var1, var2));
   }

   private Subscriber(EventBus var1, Object var2, Method var3) {
      super();
      this.bus = var1;
      this.target = Preconditions.checkNotNull(var2);
      this.method = var3;
      var3.setAccessible(true);
      this.executor = var1.executor();
   }

   final void dispatchEvent(final Object var1) {
      this.executor.execute(new Runnable() {
         public void run() {
            try {
               Subscriber.this.invokeSubscriberMethod(var1);
            } catch (InvocationTargetException var2) {
               Subscriber.this.bus.handleSubscriberException(var2.getCause(), Subscriber.this.context(var1));
            }

         }
      });
   }

   @VisibleForTesting
   void invokeSubscriberMethod(Object var1) throws InvocationTargetException {
      try {
         this.method.invoke(this.target, Preconditions.checkNotNull(var1));
      } catch (IllegalArgumentException var3) {
         throw new Error("Method rejected target/argument: " + var1, var3);
      } catch (IllegalAccessException var4) {
         throw new Error("Method became inaccessible: " + var1, var4);
      } catch (InvocationTargetException var5) {
         if (var5.getCause() instanceof Error) {
            throw (Error)var5.getCause();
         } else {
            throw var5;
         }
      }
   }

   private SubscriberExceptionContext context(Object var1) {
      return new SubscriberExceptionContext(this.bus, var1, this.target, this.method);
   }

   public final int hashCode() {
      return (31 + this.method.hashCode()) * 31 + System.identityHashCode(this.target);
   }

   public final boolean equals(@Nullable Object var1) {
      if (!(var1 instanceof Subscriber)) {
         return false;
      } else {
         Subscriber var2 = (Subscriber)var1;
         return this.target == var2.target && this.method.equals(var2.method);
      }
   }

   private static boolean isDeclaredThreadSafe(Method var0) {
      return var0.getAnnotation(AllowConcurrentEvents.class) != null;
   }

   // $FF: synthetic method
   Subscriber(EventBus var1, Object var2, Method var3, Object var4) {
      this(var1, var2, var3);
   }

   @VisibleForTesting
   static final class SynchronizedSubscriber extends Subscriber {
      private SynchronizedSubscriber(EventBus var1, Object var2, Method var3) {
         super(var1, var2, var3, null);
      }

      void invokeSubscriberMethod(Object var1) throws InvocationTargetException {
         synchronized(this) {
            super.invokeSubscriberMethod(var1);
         }
      }

      // $FF: synthetic method
      SynchronizedSubscriber(EventBus var1, Object var2, Method var3, Object var4) {
         this(var1, var2, var3);
      }
   }
}
