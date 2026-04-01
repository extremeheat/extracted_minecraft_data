package net.minecraft.world.entity.livingblock.behavior;

import java.util.function.BiPredicate;
import java.util.function.Function;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.livingblock.LivingBlock;
import net.minecraft.world.entity.livingblock.cognition.Action;
import net.minecraft.world.entity.livingblock.cognition.Desire;
import net.minecraft.world.entity.livingblock.cognition.Intent;
import net.minecraft.world.entity.livingblock.cognition.Prize;

public record ActOnDesireBehavior<P, I>(Prize<P> prize, Intent<I> intent, Function<P, I> adapter, BiPredicate<LivingBlock, P> conditions, boolean isFinal) implements LivingBlockBehavior {
   public ActOnDesireBehavior {
      super();
   }

   public static <V> LivingBlockBehaviorType actOnDesire(final Desire<V> desire, final Action<V> response) {
      return actOnDesire(desire, response, Function.identity());
   }

   public static <P, I> LivingBlockBehaviorType actOnDesire(final Desire<P> desire, final Action<I> response, final Function<P, I> adapter) {
      return actOnDesire(desire, response, adapter, (e, p) -> true);
   }

   public static <P, I> LivingBlockBehaviorType actOnDesire(final Desire<P> desire, final Action<I> response, final Function<P, I> adapter, final BiPredicate<LivingBlock, P> conditions) {
      return actOnDesire(desire, response, adapter, conditions, true);
   }

   public static <P, I> LivingBlockBehaviorType actOnDesire(final Desire<P> desire, final Action<I> response, final Function<P, I> adapter, final BiPredicate<LivingBlock, P> conditions, boolean isFinal) {
      return LivingBlockBehaviorType.behaviorType((Function)((a) -> new ActOnDesireBehavior(a.inPursuitOf(desire), a.withIntentTo(response), adapter, conditions, isFinal)));
   }

   public boolean canStartUsing(final LivingBlock entity) {
      return this.conditions.test(entity, this.prize.get());
   }

   public boolean tick(final LivingBlock entity, final ServerLevel level, final int tickCount) {
      this.intent.update(this.adapter.apply(this.prize.get()));
      if (this.isFinal) {
         this.prize.forget();
      }

      return false;
   }
}
