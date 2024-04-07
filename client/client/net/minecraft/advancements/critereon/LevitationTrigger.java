package net.minecraft.advancements.critereon;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.Optional;
import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.advancements.Criterion;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.phys.Vec3;

public class LevitationTrigger extends SimpleCriterionTrigger<LevitationTrigger.TriggerInstance> {
   public LevitationTrigger() {
      super();
   }

   @Override
   public Codec<LevitationTrigger.TriggerInstance> codec() {
      return LevitationTrigger.TriggerInstance.CODEC;
   }

   public void trigger(ServerPlayer var1, Vec3 var2, int var3) {
      this.trigger(var1, var3x -> var3x.matches(var1, var2, var3));
   }

   public static record TriggerInstance(Optional<ContextAwarePredicate> player, Optional<DistancePredicate> distance, MinMaxBounds.Ints duration)
      implements SimpleCriterionTrigger.SimpleInstance {
      public static final Codec<LevitationTrigger.TriggerInstance> CODEC = RecordCodecBuilder.create(
         var0 -> var0.group(
                  EntityPredicate.ADVANCEMENT_CODEC.optionalFieldOf("player").forGetter(LevitationTrigger.TriggerInstance::player),
                  DistancePredicate.CODEC.optionalFieldOf("distance").forGetter(LevitationTrigger.TriggerInstance::distance),
                  MinMaxBounds.Ints.CODEC.optionalFieldOf("duration", MinMaxBounds.Ints.ANY).forGetter(LevitationTrigger.TriggerInstance::duration)
               )
               .apply(var0, LevitationTrigger.TriggerInstance::new)
      );

      public TriggerInstance(Optional<ContextAwarePredicate> player, Optional<DistancePredicate> distance, MinMaxBounds.Ints duration) {
         super();
         this.player = player;
         this.distance = distance;
         this.duration = duration;
      }

      public static Criterion<LevitationTrigger.TriggerInstance> levitated(DistancePredicate var0) {
         return CriteriaTriggers.LEVITATION.createCriterion(new LevitationTrigger.TriggerInstance(Optional.empty(), Optional.of(var0), MinMaxBounds.Ints.ANY));
      }

      public boolean matches(ServerPlayer var1, Vec3 var2, int var3) {
         return this.distance.isPresent() && !this.distance.get().matches(var2.x, var2.y, var2.z, var1.getX(), var1.getY(), var1.getZ())
            ? false
            : this.duration.matches(var3);
      }
   }
}
