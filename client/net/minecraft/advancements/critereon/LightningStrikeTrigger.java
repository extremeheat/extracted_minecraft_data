package net.minecraft.advancements.critereon;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.mojang.serialization.codecs.RecordCodecBuilder.Instance;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.advancements.Criterion;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.ExtraCodecs;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LightningBolt;
import net.minecraft.world.level.storage.loot.LootContext;

public class LightningStrikeTrigger extends SimpleCriterionTrigger<LightningStrikeTrigger.TriggerInstance> {
   public LightningStrikeTrigger() {
      super();
   }

   @Override
   public Codec<LightningStrikeTrigger.TriggerInstance> codec() {
      return LightningStrikeTrigger.TriggerInstance.CODEC;
   }

   public void trigger(ServerPlayer var1, LightningBolt var2, List<Entity> var3) {
      List var4 = var3.stream().map(var1x -> EntityPredicate.createContext(var1, var1x)).collect(Collectors.toList());
      LootContext var5 = EntityPredicate.createContext(var1, var2);
      this.trigger(var1, var2x -> var2x.matches(var5, var4));
   }

   public static record TriggerInstance(Optional<ContextAwarePredicate> b, Optional<ContextAwarePredicate> c, Optional<ContextAwarePredicate> d)
      implements SimpleCriterionTrigger.SimpleInstance {
      private final Optional<ContextAwarePredicate> player;
      private final Optional<ContextAwarePredicate> lightning;
      private final Optional<ContextAwarePredicate> bystander;
      public static final Codec<LightningStrikeTrigger.TriggerInstance> CODEC = RecordCodecBuilder.create(
         var0 -> var0.group(
                  ExtraCodecs.strictOptionalField(EntityPredicate.ADVANCEMENT_CODEC, "player").forGetter(LightningStrikeTrigger.TriggerInstance::player),
                  ExtraCodecs.strictOptionalField(EntityPredicate.ADVANCEMENT_CODEC, "lightning").forGetter(LightningStrikeTrigger.TriggerInstance::lightning),
                  ExtraCodecs.strictOptionalField(EntityPredicate.ADVANCEMENT_CODEC, "bystander").forGetter(LightningStrikeTrigger.TriggerInstance::bystander)
               )
               .apply(var0, LightningStrikeTrigger.TriggerInstance::new)
      );

      public TriggerInstance(Optional<ContextAwarePredicate> var1, Optional<ContextAwarePredicate> var2, Optional<ContextAwarePredicate> var3) {
         super();
         this.player = var1;
         this.lightning = var2;
         this.bystander = var3;
      }

      public static Criterion<LightningStrikeTrigger.TriggerInstance> lightningStrike(Optional<EntityPredicate> var0, Optional<EntityPredicate> var1) {
         return CriteriaTriggers.LIGHTNING_STRIKE
            .createCriterion(new LightningStrikeTrigger.TriggerInstance(Optional.empty(), EntityPredicate.wrap(var0), EntityPredicate.wrap(var1)));
      }

      public boolean matches(LootContext var1, List<LootContext> var2) {
         if (this.lightning.isPresent() && !this.lightning.get().matches(var1)) {
            return false;
         } else {
            return !this.bystander.isPresent() || !var2.stream().noneMatch(this.bystander.get()::matches);
         }
      }

      @Override
      public void validate(CriterionValidator var1) {
         SimpleCriterionTrigger.SimpleInstance.super.validate(var1);
         var1.validateEntity(this.lightning, ".lightning");
         var1.validateEntity(this.bystander, ".bystander");
      }
   }
}
