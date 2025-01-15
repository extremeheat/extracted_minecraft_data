package net.minecraft.gametest.framework;

import com.google.common.collect.Lists;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;
import java.util.function.Supplier;
import net.minecraft.network.chat.Component;

public class GameTestSequence {
   final GameTestInfo parent;
   private final List<GameTestEvent> events = Lists.newArrayList();
   private int lastTick;

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
      this.events.add(GameTestEvent.create(() -> this.executeWithoutFail(var1)));
      return this;
   }

   public GameTestSequence thenExecuteAfter(int var1, Runnable var2) {
      this.events.add(GameTestEvent.create(() -> {
         if (this.parent.getTick() < this.lastTick + var1) {
            throw new GameTestAssertException(Component.translatable("test.error.sequence.not_completed"), this.parent.getTick());
         } else {
            this.executeWithoutFail(var2);
         }
      }));
      return this;
   }

   public GameTestSequence thenExecuteFor(int var1, Runnable var2) {
      this.events.add(GameTestEvent.create(() -> {
         if (this.parent.getTick() < this.lastTick + var1) {
            this.executeWithoutFail(var2);
            throw new GameTestAssertException(Component.translatable("test.error.sequence.not_completed"), this.parent.getTick());
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
      this.events.add(GameTestEvent.create(() -> this.parent.fail((Throwable)var1.get())));
   }

   public Condition thenTrigger() {
      Condition var1 = new Condition();
      this.events.add(GameTestEvent.create(() -> var1.trigger(this.parent.getTick())));
      return var1;
   }

   public void tickAndContinue(int var1) {
      try {
         this.tick(var1);
      } catch (GameTestAssertException var3) {
      }

   }

   public void tickAndFailIfNotComplete(int var1) {
      try {
         this.tick(var1);
      } catch (GameTestAssertException var3) {
         this.parent.fail(var3);
      }

   }

   private void executeWithoutFail(Runnable var1) {
      try {
         var1.run();
      } catch (GameTestAssertException var3) {
         this.parent.fail(var3);
      }

   }

   private void tick(int var1) {
      Iterator var2 = this.events.iterator();

      while(var2.hasNext()) {
         GameTestEvent var3 = (GameTestEvent)var2.next();
         var3.assertion.run();
         var2.remove();
         int var4 = var1 - this.lastTick;
         int var5 = this.lastTick;
         this.lastTick = var1;
         if (var3.expectedDelay != null && var3.expectedDelay != (long)var4) {
            this.parent.fail(new GameTestAssertException(Component.translatable("test.error.sequence.invalid_tick", (long)var5 + var3.expectedDelay), var1));
            break;
         }
      }

   }

   public class Condition {
      private static final int NOT_TRIGGERED = -1;
      private int triggerTime = -1;

      public Condition() {
         super();
      }

      void trigger(int var1) {
         if (this.triggerTime != -1) {
            throw new IllegalStateException("Condition already triggered at " + this.triggerTime);
         } else {
            this.triggerTime = var1;
         }
      }

      public void assertTriggeredThisTick() {
         int var1 = GameTestSequence.this.parent.getTick();
         if (this.triggerTime != var1) {
            if (this.triggerTime == -1) {
               throw new GameTestAssertException(Component.translatable("test.error.sequence.condition_not_triggered"), var1);
            } else {
               throw new GameTestAssertException(Component.translatable("test.error.sequence.condition_already_triggered", this.triggerTime), var1);
            }
         }
      }
   }
}
