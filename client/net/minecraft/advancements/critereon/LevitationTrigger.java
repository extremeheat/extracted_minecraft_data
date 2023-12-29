package net.minecraft.advancements.critereon;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.Optional;
import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.advancements.Criterion;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.ExtraCodecs;
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

   public static record TriggerInstance(Optional<ContextAwarePredicate> b, Optional<DistancePredicate> c, MinMaxBounds.Ints d)
      implements SimpleCriterionTrigger.SimpleInstance {
      private final Optional<ContextAwarePredicate> player;
      private final Optional<DistancePredicate> distance;
      private final MinMaxBounds.Ints duration;
      public static final Codec<LevitationTrigger.TriggerInstance> CODEC = RecordCodecBuilder.create(
         var0 -> var0.group(
                  ExtraCodecs.strictOptionalField(EntityPredicate.ADVANCEMENT_CODEC, "player").forGetter(LevitationTrigger.TriggerInstance::player),
                  ExtraCodecs.strictOptionalField(DistancePredicate.CODEC, "distance").forGetter(LevitationTrigger.TriggerInstance::distance),
                  ExtraCodecs.strictOptionalField(MinMaxBounds.Ints.CODEC, "duration", MinMaxBounds.Ints.ANY)
                     .forGetter(LevitationTrigger.TriggerInstance::duration)
               )
               .apply(var0, LevitationTrigger.TriggerInstance::new)
      );

      public TriggerInstance(Optional<ContextAwarePredicate> var1, Optional<DistancePredicate> var2, MinMaxBounds.Ints var3) {
         super();
         this.player = var1;
         this.distance = var2;
         this.duration = var3;
      }

      public static Criterion<LevitationTrigger.TriggerInstance> levitated(DistancePredicate var0) {
         return CriteriaTriggers.LEVITATION.createCriterion(new LevitationTrigger.TriggerInstance(Optional.empty(), Optional.of(var0), MinMaxBounds.Ints.ANY));
      }

      public boolean matches(ServerPlayer var1, Vec3 var2, int var3) {
         if (this.distance.isPresent() && !this.distance.get().matches(var2.x, var2.y, var2.z, var1.getX(), var1.getY(), var1.getZ())) {
            return false;
         } else {
            return this.duration.matches(var3);
         }
      }
   }
}
