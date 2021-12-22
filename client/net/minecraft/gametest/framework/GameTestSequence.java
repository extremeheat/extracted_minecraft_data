package net.minecraft.gametest.framework;

import com.google.common.collect.Lists;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;
import java.util.function.Supplier;

public class GameTestSequence {
   final GameTestInfo parent;
   private final List<GameTestEvent> events = Lists.newArrayList();
   private long lastTick;

   GameTestSequence(GameTestInfo var1) {
      super();
      this.parent = var1;
      this.lastTick = var1.getTick();
   }

   public GameTestSequence thenWaitUntil(Runnable var1) {
      this.events.add(GameTestEvent.create(var1));
      return this;
   }

   public GameTestSequence thenWaitUntil(long var1, Runnable var3) {
      this.events.add(GameTestEvent.create(var1, var3));
      return this;
   }

   public GameTestSequence thenIdle(int var1) {
      return this.thenExecuteAfter(var1, () -> {
      });
   }

   public GameTestSequence thenExecute(Runnable var1) {
      this.events.add(GameTestEvent.create(() -> {
         this.executeWithoutFail(var1);
      }));
      return this;
   }

   public GameTestSequence thenExecuteAfter(int var1, Runnable var2) {
      this.events.add(GameTestEvent.create(() -> {
         if (this.parent.getTick() < this.lastTick + (long)var1) {
            throw new GameTestAssertException("Waiting");
         } else {
            this.executeWithoutFail(var2);
         }
      }));
      return this;
   }

   public GameTestSequence thenExecuteFor(int var1, Runnable var2) {
      this.events.add(GameTestEvent.create(() -> {
         if (this.parent.getTick() < this.lastTick + (long)var1) {
            this.executeWithoutFail(var2);
            throw new GameTestAssertException("Waiting");
         }
      }));
      return this;
   }

   public void thenSucceed() {
      List var10000 = this.events;
      GameTestInfo var10001 = this.parent;
      Objects.requireNonNull(var10001);
      var10000.add(GameTestEvent.create(var10001::succeed));
   }

   public void thenFail(Supplier<Exception> var1) {
      this.events.add(GameTestEvent.create(() -> {
         this.parent.fail((Throwable)var1.get());
      }));
   }

   public GameTestSequence.Condition thenTrigger() {
      GameTestSequence.Condition var1 = new GameTestSequence.Condition();
      this.events.add(GameTestEvent.create(() -> {
         var1.trigger(this.parent.getTick());
      }));
      return var1;
   }

   public void tickAndContinue(long var1) {
      try {
         this.tick(var1);
      } catch (GameTestAssertException var4) {
      }

   }

   public void tickAndFailIfNotComplete(long var1) {
      try {
         this.tick(var1);
      } catch (GameTestAssertException var4) {
         this.parent.fail(var4);
      }

   }

   private void executeWithoutFail(Runnable var1) {
      try {
         var1.run();
      } catch (GameTestAssertException var3) {
         this.parent.fail(var3);
      }

   }

   private void tick(long var1) {
      Iterator var3 = this.events.iterator();

      while(var3.hasNext()) {
         GameTestEvent var4 = (GameTestEvent)var3.next();
         var4.assertion.run();
         var3.remove();
         long var5 = var1 - this.lastTick;
         long var7 = this.lastTick;
         this.lastTick = var1;
         if (var4.expectedDelay != null && var4.expectedDelay != var5) {
            GameTestInfo var10000 = this.parent;
            long var10003 = var7 + var4.expectedDelay;
            var10000.fail(new GameTestAssertException("Succeeded in invalid tick: expected " + var10003 + ", but current tick is " + var1));
            break;
         }
      }

   }

   public class Condition {
      private static final long NOT_TRIGGERED = -1L;
      private long triggerTime = -1L;

      public Condition() {
         super();
      }

      void trigger(long var1) {
         if (this.triggerTime != -1L) {
            throw new IllegalStateException("Condition already triggered at " + this.triggerTime);
         } else {
            this.triggerTime = var1;
         }
      }

      public void assertTriggeredThisTick() {
         long var1 = GameTestSequence.this.parent.getTick();
         if (this.triggerTime != var1) {
            if (this.triggerTime == -1L) {
               throw new GameTestAssertException("Condition not triggered (t=" + var1 + ")");
            } else {
               throw new GameTestAssertException("Condition triggered at " + this.triggerTime + ", (t=" + var1 + ")");
            }
         }
      }
   }
}
