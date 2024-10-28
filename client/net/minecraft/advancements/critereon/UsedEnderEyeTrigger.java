package net.minecraft.advancements.critereon;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.Optional;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerPlayer;

public class UsedEnderEyeTrigger extends SimpleCriterionTrigger<TriggerInstance> {
   public UsedEnderEyeTrigger() {
      super();
   }

   public Codec<TriggerInstance> codec() {
      return UsedEnderEyeTrigger.TriggerInstance.CODEC;
   }

   public void trigger(ServerPlayer var1, BlockPos var2) {
      double var3 = var1.getX() - (double)var2.getX();
      double var5 = var1.getZ() - (double)var2.getZ();
      double var7 = var3 * var3 + var5 * var5;
      this.trigger(var1, (var2x) -> {
         return var2x.matches(var7);
      });
   }

   public static record TriggerInstance(Optional<ContextAwarePredicate> player, MinMaxBounds.Doubles distance) implements SimpleCriterionTrigger.SimpleInstance {
      public static final Codec<TriggerInstance> CODEC = RecordCodecBuilder.create((var0) -> {
         return var0.group(EntityPredicate.ADVANCEMENT_CODEC.optionalFieldOf("player").forGetter(TriggerInstance::player), MinMaxBounds.Doubles.CODEC.optionalFieldOf("distance", MinMaxBounds.Doubles.ANY).forGetter(TriggerInstance::distance)).apply(var0, TriggerInstance::new);
      });

      public TriggerInstance(Optional<ContextAwarePredicate> player, MinMaxBounds.Doubles distance) {
         super();
         this.player = player;
         this.distance = distance;
      }

      public boolean matches(double var1) {
         return this.distance.matchesSqr(var1);
      }

      public Optional<ContextAwarePredicate> player() {
         return this.player;
      }

      public MinMaxBounds.Doubles distance() {
         return this.distance;
      }
   }
}
