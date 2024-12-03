package net.minecraft.advancements.critereon;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.Optional;
import javax.annotation.Nullable;
import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.advancements.Criterion;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.phys.Vec3;

public class FallAfterExplosionTrigger extends SimpleCriterionTrigger<TriggerInstance> {
   public FallAfterExplosionTrigger() {
      super();
   }

   public Codec<TriggerInstance> codec() {
      return FallAfterExplosionTrigger.TriggerInstance.CODEC;
   }

   public void trigger(ServerPlayer var1, Vec3 var2, @Nullable Entity var3) {
      Vec3 var4 = var1.position();
      LootContext var5 = var3 != null ? EntityPredicate.createContext(var1, var3) : null;
      this.trigger(var1, (var4x) -> var4x.matches(var1.serverLevel(), var2, var4, var5));
   }

   public static record TriggerInstance(Optional<ContextAwarePredicate> player, Optional<LocationPredicate> startPosition, Optional<DistancePredicate> distance, Optional<ContextAwarePredicate> cause) implements SimpleCriterionTrigger.SimpleInstance {
      public static final Codec<TriggerInstance> CODEC = RecordCodecBuilder.create((var0) -> var0.group(EntityPredicate.ADVANCEMENT_CODEC.optionalFieldOf("player").forGetter(TriggerInstance::player), LocationPredicate.CODEC.optionalFieldOf("start_position").forGetter(TriggerInstance::startPosition), DistancePredicate.CODEC.optionalFieldOf("distance").forGetter(TriggerInstance::distance), EntityPredicate.ADVANCEMENT_CODEC.optionalFieldOf("cause").forGetter(TriggerInstance::cause)).apply(var0, TriggerInstance::new));

      public TriggerInstance(Optional<ContextAwarePredicate> var1, Optional<LocationPredicate> var2, Optional<DistancePredicate> var3, Optional<ContextAwarePredicate> var4) {
         super();
         this.player = var1;
         this.startPosition = var2;
         this.distance = var3;
         this.cause = var4;
      }

      public static Criterion<TriggerInstance> fallAfterExplosion(DistancePredicate var0, EntityPredicate.Builder var1) {
         return CriteriaTriggers.FALL_AFTER_EXPLOSION.createCriterion(new TriggerInstance(Optional.empty(), Optional.empty(), Optional.of(var0), Optional.of(EntityPredicate.wrap(var1))));
      }

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
            return !this.cause.isPresent() || var4 != null && ((ContextAwarePredicate)this.cause.get()).matches(var4);
         }
      }
   }
}
