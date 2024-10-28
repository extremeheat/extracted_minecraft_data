package net.minecraft.advancements.critereon;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.Optional;
import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.advancements.Criterion;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.phys.Vec3;

public class LevitationTrigger extends SimpleCriterionTrigger<TriggerInstance> {
   public LevitationTrigger() {
      super();
   }

   public Codec<TriggerInstance> codec() {
      return LevitationTrigger.TriggerInstance.CODEC;
   }

   public void trigger(ServerPlayer var1, Vec3 var2, int var3) {
      this.trigger(var1, (var3x) -> {
         return var3x.matches(var1, var2, var3);
      });
   }

   public static record TriggerInstance(Optional<ContextAwarePredicate> player, Optional<DistancePredicate> distance, MinMaxBounds.Ints duration) implements SimpleCriterionTrigger.SimpleInstance {
      public static final Codec<TriggerInstance> CODEC = RecordCodecBuilder.create((var0) -> {
         return var0.group(EntityPredicate.ADVANCEMENT_CODEC.optionalFieldOf("player").forGetter(TriggerInstance::player), DistancePredicate.CODEC.optionalFieldOf("distance").forGetter(TriggerInstance::distance), MinMaxBounds.Ints.CODEC.optionalFieldOf("duration", MinMaxBounds.Ints.ANY).forGetter(TriggerInstance::duration)).apply(var0, TriggerInstance::new);
      });

      public TriggerInstance(Optional<ContextAwarePredicate> player, Optional<DistancePredicate> distance, MinMaxBounds.Ints duration) {
         super();
         this.player = player;
         this.distance = distance;
         this.duration = duration;
      }

      public static Criterion<TriggerInstance> levitated(DistancePredicate var0) {
         return CriteriaTriggers.LEVITATION.createCriterion(new TriggerInstance(Optional.empty(), Optional.of(var0), MinMaxBounds.Ints.ANY));
      }

      public boolean matches(ServerPlayer var1, Vec3 var2, int var3) {
         if (this.distance.isPresent() && !((DistancePredicate)this.distance.get()).matches(var2.x, var2.y, var2.z, var1.getX(), var1.getY(), var1.getZ())) {
            return false;
         } else {
            return this.duration.matches(var3);
         }
      }

      public Optional<ContextAwarePredicate> player() {
         return this.player;
      }

      public Optional<DistancePredicate> distance() {
         return this.distance;
      }

      public MinMaxBounds.Ints duration() {
         return this.duration;
      }
   }
}
