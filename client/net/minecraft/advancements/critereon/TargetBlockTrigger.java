package net.minecraft.advancements.critereon;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.mojang.serialization.codecs.RecordCodecBuilder.Instance;
import java.util.Optional;
import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.advancements.Criterion;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.ExtraCodecs;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.phys.Vec3;

public class TargetBlockTrigger extends SimpleCriterionTrigger<TargetBlockTrigger.TriggerInstance> {
   public TargetBlockTrigger() {
      super();
   }

   @Override
   public Codec<TargetBlockTrigger.TriggerInstance> codec() {
      return TargetBlockTrigger.TriggerInstance.CODEC;
   }

   public void trigger(ServerPlayer var1, Entity var2, Vec3 var3, int var4) {
      LootContext var5 = EntityPredicate.createContext(var1, var2);
      this.trigger(var1, var3x -> var3x.matches(var5, var3, var4));
   }

   public static record TriggerInstance(Optional<ContextAwarePredicate> b, MinMaxBounds.Ints c, Optional<ContextAwarePredicate> d)
      implements SimpleCriterionTrigger.SimpleInstance {
      private final Optional<ContextAwarePredicate> player;
      private final MinMaxBounds.Ints signalStrength;
      private final Optional<ContextAwarePredicate> projectile;
      public static final Codec<TargetBlockTrigger.TriggerInstance> CODEC = RecordCodecBuilder.create(
         var0 -> var0.group(
                  ExtraCodecs.strictOptionalField(EntityPredicate.ADVANCEMENT_CODEC, "player").forGetter(TargetBlockTrigger.TriggerInstance::player),
                  ExtraCodecs.strictOptionalField(MinMaxBounds.Ints.CODEC, "signal_strength", MinMaxBounds.Ints.ANY)
                     .forGetter(TargetBlockTrigger.TriggerInstance::signalStrength),
                  ExtraCodecs.strictOptionalField(EntityPredicate.ADVANCEMENT_CODEC, "projectile").forGetter(TargetBlockTrigger.TriggerInstance::projectile)
               )
               .apply(var0, TargetBlockTrigger.TriggerInstance::new)
      );

      public TriggerInstance(Optional<ContextAwarePredicate> var1, MinMaxBounds.Ints var2, Optional<ContextAwarePredicate> var3) {
         super();
         this.player = var1;
         this.signalStrength = var2;
         this.projectile = var3;
      }

      public static Criterion<TargetBlockTrigger.TriggerInstance> targetHit(MinMaxBounds.Ints var0, Optional<ContextAwarePredicate> var1) {
         return CriteriaTriggers.TARGET_BLOCK_HIT.createCriterion(new TargetBlockTrigger.TriggerInstance(Optional.empty(), var0, var1));
      }

      public boolean matches(LootContext var1, Vec3 var2, int var3) {
         if (!this.signalStrength.matches(var3)) {
            return false;
         } else {
            return !this.projectile.isPresent() || this.projectile.get().matches(var1);
         }
      }

      @Override
      public void validate(CriterionValidator var1) {
         SimpleCriterionTrigger.SimpleInstance.super.validate(var1);
         var1.validateEntity(this.projectile, ".projectile");
      }
   }
}
