package net.minecraft.advancements.triggers;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.Optional;
import net.minecraft.advancements.predicates.MinMaxBounds;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;

public class UsedEnderEyeTrigger extends SimpleCriterionTrigger<TriggerInstance> {
   public UsedEnderEyeTrigger() {
      super();
   }

   public Codec<TriggerInstance> codec() {
      return UsedEnderEyeTrigger.TriggerInstance.CODEC;
   }

   public void trigger(final ServerPlayer player, final BlockPos feature) {
      double xd = player.getX() - (double)feature.getX();
      double zd = player.getZ() - (double)feature.getZ();
      double sqrDist = xd * xd + zd * zd;
      this.trigger(player, (t) -> t.matches(sqrDist));
   }

   public static record TriggerInstance(Optional<Holder<LootItemCondition>> player, MinMaxBounds.Doubles distance) implements SimpleCriterionTrigger.SimpleInstance {
      public static final Codec<TriggerInstance> CODEC = RecordCodecBuilder.create((i) -> i.group(LootItemCondition.CODEC.optionalFieldOf("player").forGetter(TriggerInstance::player), MinMaxBounds.Doubles.CODEC.optionalFieldOf("distance", MinMaxBounds.Doubles.ANY).forGetter(TriggerInstance::distance)).apply(i, TriggerInstance::new));

      public TriggerInstance {
         super();
      }

      public boolean matches(final double sqrDistance) {
         return this.distance.matchesSqr(sqrDistance);
      }
   }
}
