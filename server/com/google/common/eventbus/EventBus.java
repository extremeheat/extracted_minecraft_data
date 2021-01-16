package com.google.common.eventbus;

import com.google.common.annotations.Beta;
import com.google.common.base.MoreObjects;
import com.google.common.base.Preconditions;
import com.google.common.util.concurrent.MoreExecutors;
import java.lang.reflect.Method;
import java.util.Iterator;
import java.util.Locale;
import java.util.concurrent.Executor;
import java.util.logging.Level;
import java.util.logging.Logger;

@Beta
public class EventBus {
   private static final Logger logger = Logger.getLogger(EventBus.class.getName());
   private final String identifier;
   private final Executor executor;
   private final SubscriberExceptionHandler exceptionHandler;
   private final SubscriberRegistry subscribers;
   private final Dispatcher dispatcher;

   public EventBus() {
      this("default");
   }

   public EventBus(String var1) {
      this(var1, MoreExecutors.directExecutor(), Dispatcher.perThreadDispatchQueue(), EventBus.LoggingHandler.INSTANCE);
   }

   public EventBus(SubscriberExceptionHandler var1) {
      this("default", MoreExecutors.directExecutor(), Dispatcher.perThreadDispatchQueue(), var1);
   }

   EventBus(String var1, Executor var2, Dispatcher var3, SubscriberExceptionHandler var4) {
      super();
      this.subscribers = new SubscriberRegistry(this);
      this.identifier = (String)Preconditions.checkNotNull(var1);
      this.executor = (Executor)Preconditions.checkNotNull(var2);
      this.dispatcher = (Dispatcher)Preconditions.checkNotNull(var3);
      this.exceptionHandler = (SubscriberExceptionHandler)Preconditions.checkNotNull(var4);
   }

   public final String identifier() {
      return this.identifier;
   }

   final Executor executor() {
      return this.executor;
   }

   void handleSubscriberException(Throwable var1, SubscriberExceptionContext var2) {
      Preconditions.checkNotNull(var1);
      Preconditions.checkNotNull(var2);

      try {
         this.exceptionHandler.handleException(var1, var2);
      } catch (Throwable var4) {
         logger.log(Level.SEVERE, String.format(Locale.ROOT, "Exception %s thrown while handling exception: %s", var4, var1), var4);
      }

   }

   public void register(Object var1) {
      this.subscribers.register(var1);
   }

   public void unregister(Object var1) {
      this.subscribers.unregister(var1);
   }

   public void post(Object var1) {
      Iterator var2 = this.subscribers.getSubscribers(var1);
      if (var2.hasNext()) {
         this.dispatcher.dispatch(var1, var2);
      } else if (!(var1 instanceof DeadEvent)) {
         this.post(new DeadEvent(this, var1));
      }

   }

   public String toString() {
      return MoreObjects.toStringHelper((Object)this).addValue(this.identifier).toString();
   }

   static final class LoggingHandler implements SubscriberExceptionHandler {
      static final EventBus.LoggingHandler INSTANCE = new EventBus.LoggingHandler();

      LoggingHandler() {
         super();
      }

      public void handleException(Throwable var1, SubscriberExceptionContext var2) {
         Logger var3 = logger(var2);
         if (var3.isLoggable(Level.SEVERE)) {
            var3.log(Level.SEVERE, message(var2), var1);
         }

      }

      private static Logger logger(SubscriberExceptionContext var0) {
         return Logger.getLogger(EventBus.class.getName() + "." + var0.getEventBus().identifier());
      }

      private static String message(SubscriberExceptionContext var0) {
         Method var1 = var0.getSubscriberMethod();
         return "Exception thrown by subscriber method " + var1.getName() + '(' + var1.getParameterTypes()[0].getName() + ')' + " on subscriber " + var0.getSubscriber() + " when dispatching event: " + var0.getEvent();
      }
   }
}
