package net.minecraft.advancements.critereon;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.mojang.serialization.codecs.RecordCodecBuilder.Instance;
import java.util.Optional;
import javax.annotation.Nullable;
import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.advancements.Criterion;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.ExtraCodecs;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.phys.Vec3;

public class FallAfterExplosionTrigger extends SimpleCriterionTrigger<FallAfterExplosionTrigger.TriggerInstance> {
   public FallAfterExplosionTrigger() {
      super();
   }

   @Override
   public Codec<FallAfterExplosionTrigger.TriggerInstance> codec() {
      return FallAfterExplosionTrigger.TriggerInstance.CODEC;
   }

   public void trigger(ServerPlayer var1, Vec3 var2, @Nullable Entity var3) {
      Vec3 var4 = var1.position();
      LootContext var5 = var3 != null ? EntityPredicate.createContext(var1, var3) : null;
      this.trigger(var1, var4x -> var4x.matches(var1.serverLevel(), var2, var4, var5));
   }

   public static record TriggerInstance(
      Optional<ContextAwarePredicate> b, Optional<LocationPredicate> c, Optional<DistancePredicate> d, Optional<ContextAwarePredicate> e
   ) implements SimpleCriterionTrigger.SimpleInstance {
      private final Optional<ContextAwarePredicate> player;
      private final Optional<LocationPredicate> startPosition;
      private final Optional<DistancePredicate> distance;
      private final Optional<ContextAwarePredicate> cause;
      public static final Codec<FallAfterExplosionTrigger.TriggerInstance> CODEC = RecordCodecBuilder.create(
         var0 -> var0.group(
                  ExtraCodecs.strictOptionalField(EntityPredicate.ADVANCEMENT_CODEC, "player").forGetter(FallAfterExplosionTrigger.TriggerInstance::player),
                  ExtraCodecs.strictOptionalField(LocationPredicate.CODEC, "start_position")
                     .forGetter(FallAfterExplosionTrigger.TriggerInstance::startPosition),
                  ExtraCodecs.strictOptionalField(DistancePredicate.CODEC, "distance").forGetter(FallAfterExplosionTrigger.TriggerInstance::distance),
                  ExtraCodecs.strictOptionalField(EntityPredicate.ADVANCEMENT_CODEC, "cause").forGetter(FallAfterExplosionTrigger.TriggerInstance::cause)
               )
               .apply(var0, FallAfterExplosionTrigger.TriggerInstance::new)
      );

      public TriggerInstance(
         Optional<ContextAwarePredicate> var1, Optional<LocationPredicate> var2, Optional<DistancePredicate> var3, Optional<ContextAwarePredicate> var4
      ) {
         super();
         this.player = var1;
         this.startPosition = var2;
         this.distance = var3;
         this.cause = var4;
      }

      public static Criterion<FallAfterExplosionTrigger.TriggerInstance> fallAfterExplosion(DistancePredicate var0, EntityPredicate.Builder var1) {
         return CriteriaTriggers.FALL_AFTER_EXPLOSION
            .createCriterion(
               new FallAfterExplosionTrigger.TriggerInstance(Optional.empty(), Optional.empty(), Optional.of(var0), Optional.of(EntityPredicate.wrap(var1)))
            );
      }

      @Override
      public void validate(CriterionValidator var1) {
         SimpleCriterionTrigger.SimpleInstance.super.validate(var1);
         var1.validateEntity(this.cause(), ".cause");
      }

      public boolean matches(ServerLevel var1, Vec3 var2, Vec3 var3, @Nullable LootContext var4) {
         if (this.startPosition.isPresent() && !((LocationPredicate)this.startPosition.get()).matches(var1, var2.x, var2.y, var2.z)) {
            return false;
         } else if (this.distance.isPresent() && !((DistancePredicate)this.distance.get()).matches(var2.x, var2.y, var2.z, var3.x, var3.y, var3.z)) {
            return false;
         } else {
            return !this.cause.isPresent() || var4 != null && this.cause.get().matches(var4);
         }
      }
   }
}
