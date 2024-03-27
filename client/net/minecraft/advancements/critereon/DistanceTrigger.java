package net.minecraft.advancements.critereon;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.mojang.serialization.codecs.RecordCodecBuilder.Instance;
import java.util.Optional;
import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.advancements.Criterion;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.phys.Vec3;

public class DistanceTrigger extends SimpleCriterionTrigger<DistanceTrigger.TriggerInstance> {
   public DistanceTrigger() {
      super();
   }

   @Override
   public Codec<DistanceTrigger.TriggerInstance> codec() {
      return DistanceTrigger.TriggerInstance.CODEC;
   }

   public void trigger(ServerPlayer var1, Vec3 var2) {
      Vec3 var3 = var1.position();
      this.trigger(var1, var3x -> var3x.matches(var1.serverLevel(), var2, var3));
   }

   public static record TriggerInstance(Optional<ContextAwarePredicate> b, Optional<LocationPredicate> c, Optional<DistancePredicate> d)
      implements SimpleCriterionTrigger.SimpleInstance {
      private final Optional<ContextAwarePredicate> player;
      private final Optional<LocationPredicate> startPosition;
      private final Optional<DistancePredicate> distance;
      public static final Codec<DistanceTrigger.TriggerInstance> CODEC = RecordCodecBuilder.create(
         var0 -> var0.group(
                  EntityPredicate.ADVANCEMENT_CODEC.optionalFieldOf("player").forGetter(DistanceTrigger.TriggerInstance::player),
                  LocationPredicate.CODEC.optionalFieldOf("start_position").forGetter(DistanceTrigger.TriggerInstance::startPosition),
                  DistancePredicate.CODEC.optionalFieldOf("distance").forGetter(DistanceTrigger.TriggerInstance::distance)
               )
               .apply(var0, DistanceTrigger.TriggerInstance::new)
      );

      public TriggerInstance(Optional<ContextAwarePredicate> var1, Optional<LocationPredicate> var2, Optional<DistancePredicate> var3) {
         super();
         this.player = var1;
         this.startPosition = var2;
         this.distance = var3;
      }

      public static Criterion<DistanceTrigger.TriggerInstance> fallFromHeight(
         EntityPredicate.Builder var0, DistancePredicate var1, LocationPredicate.Builder var2
      ) {
         return CriteriaTriggers.FALL_FROM_HEIGHT
            .createCriterion(new DistanceTrigger.TriggerInstance(Optional.of(EntityPredicate.wrap(var0)), Optional.of(var2.build()), Optional.of(var1)));
      }

      public static Criterion<DistanceTrigger.TriggerInstance> rideEntityInLava(EntityPredicate.Builder var0, DistancePredicate var1) {
         return CriteriaTriggers.RIDE_ENTITY_IN_LAVA_TRIGGER
            .createCriterion(new DistanceTrigger.TriggerInstance(Optional.of(EntityPredicate.wrap(var0)), Optional.empty(), Optional.of(var1)));
      }

      public static Criterion<DistanceTrigger.TriggerInstance> travelledThroughNether(DistancePredicate var0) {
         return CriteriaTriggers.NETHER_TRAVEL.createCriterion(new DistanceTrigger.TriggerInstance(Optional.empty(), Optional.empty(), Optional.of(var0)));
      }

      public boolean matches(ServerLevel var1, Vec3 var2, Vec3 var3) {
         if (this.startPosition.isPresent() && !((LocationPredicate)this.startPosition.get()).matches(var1, var2.x, var2.y, var2.z)) {
            return false;
         } else {
            return !this.distance.isPresent() || ((DistancePredicate)this.distance.get()).matches(var2.x, var2.y, var2.z, var3.x, var3.y, var3.z);
         }
      }
   }
}
