package org.apache.commons.lang3.concurrent;

import java.util.EnumMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;

public class EventCountCircuitBreaker extends AbstractCircuitBreaker<Integer> {
   private static final Map<AbstractCircuitBreaker.State, EventCountCircuitBreaker.StateStrategy> STRATEGY_MAP = createStrategyMap();
   private final AtomicReference<EventCountCircuitBreaker.CheckIntervalData> checkIntervalData;
   private final int openingThreshold;
   private final long openingInterval;
   private final int closingThreshold;
   private final long closingInterval;

   public EventCountCircuitBreaker(int var1, long var2, TimeUnit var4, int var5, long var6, TimeUnit var8) {
      super();
      this.checkIntervalData = new AtomicReference(new EventCountCircuitBreaker.CheckIntervalData(0, 0L));
      this.openingThreshold = var1;
      this.openingInterval = var4.toNanos(var2);
      this.closingThreshold = var5;
      this.closingInterval = var8.toNanos(var6);
   }

   public EventCountCircuitBreaker(int var1, long var2, TimeUnit var4, int var5) {
      this(var1, var2, var4, var5, var2, var4);
   }

   public EventCountCircuitBreaker(int var1, long var2, TimeUnit var4) {
      this(var1, var2, var4, var1);
   }

   public int getOpeningThreshold() {
      return this.openingThreshold;
   }

   public long getOpeningInterval() {
      return this.openingInterval;
   }

   public int getClosingThreshold() {
      return this.closingThreshold;
   }

   public long getClosingInterval() {
      return this.closingInterval;
   }

   public boolean checkState() {
      return this.performStateCheck(0);
   }

   public boolean incrementAndCheckState(Integer var1) throws CircuitBreakingException {
      return this.performStateCheck(1);
   }

   public boolean incrementAndCheckState() {
      return this.incrementAndCheckState(1);
   }

   public void open() {
      super.open();
      this.checkIntervalData.set(new EventCountCircuitBreaker.CheckIntervalData(0, this.now()));
   }

   public void close() {
      super.close();
      this.checkIntervalData.set(new EventCountCircuitBreaker.CheckIntervalData(0, this.now()));
   }

   private boolean performStateCheck(int var1) {
      EventCountCircuitBreaker.CheckIntervalData var2;
      EventCountCircuitBreaker.CheckIntervalData var3;
      AbstractCircuitBreaker.State var4;
      do {
         long var5 = this.now();
         var4 = (AbstractCircuitBreaker.State)this.state.get();
         var2 = (EventCountCircuitBreaker.CheckIntervalData)this.checkIntervalData.get();
         var3 = this.nextCheckIntervalData(var1, var2, var4, var5);
      } while(!this.updateCheckIntervalData(var2, var3));

      if (stateStrategy(var4).isStateTransition(this, var2, var3)) {
         var4 = var4.oppositeState();
         this.changeStateAndStartNewCheckInterval(var4);
      }

      return !isOpen(var4);
   }

   private boolean updateCheckIntervalData(EventCountCircuitBreaker.CheckIntervalData var1, EventCountCircuitBreaker.CheckIntervalData var2) {
      return var1 == var2 || this.checkIntervalData.compareAndSet(var1, var2);
   }

   private void changeStateAndStartNewCheckInterval(AbstractCircuitBreaker.State var1) {
      this.changeState(var1);
      this.checkIntervalData.set(new EventCountCircuitBreaker.CheckIntervalData(0, this.now()));
   }

   private EventCountCircuitBreaker.CheckIntervalData nextCheckIntervalData(int var1, EventCountCircuitBreaker.CheckIntervalData var2, AbstractCircuitBreaker.State var3, long var4) {
      EventCountCircuitBreaker.CheckIntervalData var6;
      if (stateStrategy(var3).isCheckIntervalFinished(this, var2, var4)) {
         var6 = new EventCountCircuitBreaker.CheckIntervalData(var1, var4);
      } else {
         var6 = var2.increment(var1);
      }

      return var6;
   }

   long now() {
      return System.nanoTime();
   }

   private static EventCountCircuitBreaker.StateStrategy stateStrategy(AbstractCircuitBreaker.State var0) {
      EventCountCircuitBreaker.StateStrategy var1 = (EventCountCircuitBreaker.StateStrategy)STRATEGY_MAP.get(var0);
      return var1;
   }

   private static Map<AbstractCircuitBreaker.State, EventCountCircuitBreaker.StateStrategy> createStrategyMap() {
      EnumMap var0 = new EnumMap(AbstractCircuitBreaker.State.class);
      var0.put(AbstractCircuitBreaker.State.CLOSED, new EventCountCircuitBreaker.StateStrategyClosed());
      var0.put(AbstractCircuitBreaker.State.OPEN, new EventCountCircuitBreaker.StateStrategyOpen());
      return var0;
   }

   private static class StateStrategyOpen extends EventCountCircuitBreaker.StateStrategy {
      private StateStrategyOpen() {
         super(null);
      }

      public boolean isStateTransition(EventCountCircuitBreaker var1, EventCountCircuitBreaker.CheckIntervalData var2, EventCountCircuitBreaker.CheckIntervalData var3) {
         return var3.getCheckIntervalStart() != var2.getCheckIntervalStart() && var2.getEventCount() < var1.getClosingThreshold();
      }

      protected long fetchCheckInterval(EventCountCircuitBreaker var1) {
         return var1.getClosingInterval();
      }

      // $FF: synthetic method
      StateStrategyOpen(Object var1) {
         this();
      }
   }

   private static class StateStrategyClosed extends EventCountCircuitBreaker.StateStrategy {
      private StateStrategyClosed() {
         super(null);
      }

      public boolean isStateTransition(EventCountCircuitBreaker var1, EventCountCircuitBreaker.CheckIntervalData var2, EventCountCircuitBreaker.CheckIntervalData var3) {
         return var3.getEventCount() > var1.getOpeningThreshold();
      }

      protected long fetchCheckInterval(EventCountCircuitBreaker var1) {
         return var1.getOpeningInterval();
      }

      // $FF: synthetic method
      StateStrategyClosed(Object var1) {
         this();
      }
   }

   private abstract static class StateStrategy {
      private StateStrategy() {
         super();
      }

      public boolean isCheckIntervalFinished(EventCountCircuitBreaker var1, EventCountCircuitBreaker.CheckIntervalData var2, long var3) {
         return var3 - var2.getCheckIntervalStart() > this.fetchCheckInterval(var1);
      }

      public abstract boolean isStateTransition(EventCountCircuitBreaker var1, EventCountCircuitBreaker.CheckIntervalData var2, EventCountCircuitBreaker.CheckIntervalData var3);

      protected abstract long fetchCheckInterval(EventCountCircuitBreaker var1);

      // $FF: synthetic method
      StateStrategy(Object var1) {
         this();
      }
   }

   private static class CheckIntervalData {
      private final int eventCount;
      private final long checkIntervalStart;

      public CheckIntervalData(int var1, long var2) {
         super();
         this.eventCount = var1;
         this.checkIntervalStart = var2;
      }

      public int getEventCount() {
         return this.eventCount;
      }

      public long getCheckIntervalStart() {
         return this.checkIntervalStart;
      }

      public EventCountCircuitBreaker.CheckIntervalData increment(int var1) {
         return var1 != 0 ? new EventCountCircuitBreaker.CheckIntervalData(this.getEventCount() + var1, this.getCheckIntervalStart()) : this;
      }
   }
}
