package net.minecraft.advancements.critereon;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.Optional;
import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.advancements.Criterion;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.phys.Vec3;

public class TargetBlockTrigger extends SimpleCriterionTrigger<TriggerInstance> {
   public TargetBlockTrigger() {
      super();
   }

   public Codec<TriggerInstance> codec() {
      return TargetBlockTrigger.TriggerInstance.CODEC;
   }

   public void trigger(ServerPlayer var1, Entity var2, Vec3 var3, int var4) {
      LootContext var5 = EntityPredicate.createContext(var1, var2);
      this.trigger(var1, (var3x) -> {
         return var3x.matches(var5, var3, var4);
      });
   }

   public static record TriggerInstance(Optional<ContextAwarePredicate> player, MinMaxBounds.Ints signalStrength, Optional<ContextAwarePredicate> projectile) implements SimpleCriterionTrigger.SimpleInstance {
      public static final Codec<TriggerInstance> CODEC = RecordCodecBuilder.create((var0) -> {
         return var0.group(EntityPredicate.ADVANCEMENT_CODEC.optionalFieldOf("player").forGetter(TriggerInstance::player), MinMaxBounds.Ints.CODEC.optionalFieldOf("signal_strength", MinMaxBounds.Ints.ANY).forGetter(TriggerInstance::signalStrength), EntityPredicate.ADVANCEMENT_CODEC.optionalFieldOf("projectile").forGetter(TriggerInstance::projectile)).apply(var0, TriggerInstance::new);
      });

      public TriggerInstance(Optional<ContextAwarePredicate> player, MinMaxBounds.Ints signalStrength, Optional<ContextAwarePredicate> projectile) {
         super();
         this.player = player;
         this.signalStrength = signalStrength;
         this.projectile = projectile;
      }

      public static Criterion<TriggerInstance> targetHit(MinMaxBounds.Ints var0, Optional<ContextAwarePredicate> var1) {
         return CriteriaTriggers.TARGET_BLOCK_HIT.createCriterion(new TriggerInstance(Optional.empty(), var0, var1));
      }

      public boolean matches(LootContext var1, Vec3 var2, int var3) {
         if (!this.signalStrength.matches(var3)) {
            return false;
         } else {
            return !this.projectile.isPresent() || ((ContextAwarePredicate)this.projectile.get()).matches(var1);
         }
      }

      public void validate(CriterionValidator var1) {
         SimpleCriterionTrigger.SimpleInstance.super.validate(var1);
         var1.validateEntity(this.projectile, ".projectile");
      }

      public Optional<ContextAwarePredicate> player() {
         return this.player;
      }

      public MinMaxBounds.Ints signalStrength() {
         return this.signalStrength;
      }

      public Optional<ContextAwarePredicate> projectile() {
         return this.projectile;
      }
   }
}
