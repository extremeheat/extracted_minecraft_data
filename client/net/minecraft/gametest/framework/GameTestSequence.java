package net.minecraft.gametest.framework;

import com.google.common.collect.Lists;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;
import java.util.function.Supplier;
import net.minecraft.network.chat.Component;

public class GameTestSequence {
   private final GameTestInfo parent;
   private final List<GameTestEvent> events = Lists.newArrayList();
   private int lastTick;

   GameTestSequence(final GameTestInfo parent) {
      super();
      this.parent = parent;
      this.lastTick = parent.getTick();
   }

   public GameTestSequence thenWaitUntil(final Runnable assertion) {
      this.events.add(GameTestEvent.create(assertion));
      return this;
   }

   public GameTestSequence thenWaitUntil(final long expectedDelay, final Runnable assertion) {
      this.events.add(GameTestEvent.create(expectedDelay, assertion));
      return this;
   }

   public GameTestSequence thenWaitAtLeast(final long minimumDelay, final Runnable assertion) {
      this.events.add(GameTestEvent.createWithMinimumDelay(minimumDelay, assertion));
      return this;
   }

   public GameTestSequence thenIdle(final int delta) {
      return this.thenExecuteAfter(delta, () -> {
      });
   }

   public GameTestSequence thenExecute(final Runnable assertion) {
      this.events.add(GameTestEvent.create(() -> this.executeWithoutFail(assertion)));
      return this;
   }

   public GameTestSequence thenExecuteAfter(final int delta, final Runnable after) {
      this.events.add(GameTestEvent.create(() -> {
         if (this.parent.getTick() < this.lastTick + delta) {
            throw new GameTestAssertException(Component.translatable("test.error.sequence.not_completed"), this.parent.getTick());
         } else {
            this.executeWithoutFail(after);
         }
      }));
      return this;
   }

   public GameTestSequence thenExecuteFor(final int delta, final Runnable check) {
      this.events.add(GameTestEvent.create(() -> {
         if (this.parent.getTick() < this.lastTick + delta) {
            this.executeWithoutFail(check);
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

   public void thenFail(final Supplier<GameTestException> e) {
      this.events.add(GameTestEvent.create(() -> this.parent.fail((GameTestException)e.get())));
   }

   public Condition thenTrigger() {
      Condition result = new Condition();
      this.events.add(GameTestEvent.create(() -> result.trigger(this.parent.getTick())));
      return result;
   }

   public void tickAndContinue(final int tick) {
      try {
         this.tick(tick);
      } catch (GameTestAssertException var3) {
      }

   }

   public void tickAndFailIfNotComplete(final int tick) {
      try {
         this.tick(tick);
      } catch (GameTestAssertException e) {
         this.parent.fail((GameTestException)e);
      }

   }

   private void executeWithoutFail(final Runnable assertion) {
      try {
         assertion.run();
      } catch (GameTestAssertException e) {
         this.parent.fail((GameTestException)e);
      }

   }

   private void tick(final int tick) {
      Iterator<GameTestEvent> iterator = this.events.iterator();

      while(iterator.hasNext()) {
         GameTestEvent event = (GameTestEvent)iterator.next();
         event.assertion.run();
         iterator.remove();
         int delay = tick - this.lastTick;
         int prevTick = this.lastTick;
         this.lastTick = tick;
         if (event.minimumDelay != null && event.minimumDelay > (long)delay) {
            this.parent.fail((GameTestException)(new GameTestAssertException(Component.translatable("test.error.sequence.minimum_tick", (long)prevTick + event.minimumDelay), tick)));
            break;
         }

         if (event.expectedDelay != null && event.expectedDelay != (long)delay) {
            this.parent.fail((GameTestException)(new GameTestAssertException(Component.translatable("test.error.sequence.invalid_tick", (long)prevTick + event.expectedDelay), tick)));
            break;
         }
      }

   }

   public class Condition {
      private static final int NOT_TRIGGERED = -1;
      private int triggerTime;

      public Condition() {
         Objects.requireNonNull(GameTestSequence.this);
         super();
         this.triggerTime = -1;
      }

      void trigger(final int time) {
         if (this.triggerTime != -1) {
            throw new IllegalStateException("Condition already triggered at " + this.triggerTime);
         } else {
            this.triggerTime = time;
         }
      }

      public void assertTriggeredThisTick() {
         int tick = GameTestSequence.this.parent.getTick();
         if (this.triggerTime != tick) {
            if (this.triggerTime == -1) {
               throw new GameTestAssertException(Component.translatable("test.error.sequence.condition_not_triggered"), tick);
            } else {
               throw new GameTestAssertException(Component.translatable("test.error.sequence.condition_already_triggered", this.triggerTime), tick);
            }
         }
      }
   }
}
