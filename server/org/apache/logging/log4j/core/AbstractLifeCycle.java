package org.apache.logging.log4j.core;

import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import org.apache.logging.log4j.status.StatusLogger;

public class AbstractLifeCycle implements LifeCycle2 {
   public static final int DEFAULT_STOP_TIMEOUT = 0;
   public static final TimeUnit DEFAULT_STOP_TIMEUNIT;
   protected static final org.apache.logging.log4j.Logger LOGGER;
   private volatile LifeCycle.State state;

   public AbstractLifeCycle() {
      super();
      this.state = LifeCycle.State.INITIALIZED;
   }

   protected static org.apache.logging.log4j.Logger getStatusLogger() {
      return LOGGER;
   }

   protected boolean equalsImpl(Object var1) {
      if (this == var1) {
         return true;
      } else if (var1 == null) {
         return false;
      } else if (this.getClass() != var1.getClass()) {
         return false;
      } else {
         LifeCycle var2 = (LifeCycle)var1;
         return this.state == var2.getState();
      }
   }

   public LifeCycle.State getState() {
      return this.state;
   }

   protected int hashCodeImpl() {
      boolean var1 = true;
      byte var2 = 1;
      int var3 = 31 * var2 + (this.state == null ? 0 : this.state.hashCode());
      return var3;
   }

   public boolean isInitialized() {
      return this.state == LifeCycle.State.INITIALIZED;
   }

   public boolean isStarted() {
      return this.state == LifeCycle.State.STARTED;
   }

   public boolean isStarting() {
      return this.state == LifeCycle.State.STARTING;
   }

   public boolean isStopped() {
      return this.state == LifeCycle.State.STOPPED;
   }

   public boolean isStopping() {
      return this.state == LifeCycle.State.STOPPING;
   }

   protected void setStarted() {
      this.setState(LifeCycle.State.STARTED);
   }

   protected void setStarting() {
      this.setState(LifeCycle.State.STARTING);
   }

   protected void setState(LifeCycle.State var1) {
      this.state = var1;
   }

   protected void setStopped() {
      this.setState(LifeCycle.State.STOPPED);
   }

   protected void setStopping() {
      this.setState(LifeCycle.State.STOPPING);
   }

   public void initialize() {
      this.state = LifeCycle.State.INITIALIZED;
   }

   public void start() {
      this.setStarted();
   }

   public void stop() {
      this.stop(0L, DEFAULT_STOP_TIMEUNIT);
   }

   protected boolean stop(Future<?> var1) {
      boolean var2 = true;
      if (var1 != null) {
         if (var1.isCancelled() || var1.isDone()) {
            return true;
         }

         var2 = var1.cancel(true);
      }

      return var2;
   }

   public boolean stop(long var1, TimeUnit var3) {
      this.state = LifeCycle.State.STOPPED;
      return true;
   }

   static {
      DEFAULT_STOP_TIMEUNIT = TimeUnit.MILLISECONDS;
      LOGGER = StatusLogger.getLogger();
   }
}
