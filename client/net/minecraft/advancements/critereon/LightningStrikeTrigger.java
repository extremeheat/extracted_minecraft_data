package net.minecraft.advancements.critereon;

import com.google.gson.JsonObject;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.advancements.Criterion;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LightningBolt;
import net.minecraft.world.level.storage.loot.LootContext;

public class LightningStrikeTrigger extends SimpleCriterionTrigger<LightningStrikeTrigger.TriggerInstance> {
   public LightningStrikeTrigger() {
      super();
   }

   public LightningStrikeTrigger.TriggerInstance createInstance(JsonObject var1, Optional<ContextAwarePredicate> var2, DeserializationContext var3) {
      Optional var4 = EntityPredicate.fromJson(var1, "lightning", var3);
      Optional var5 = EntityPredicate.fromJson(var1, "bystander", var3);
      return new LightningStrikeTrigger.TriggerInstance(var2, var4, var5);
   }

   public void trigger(ServerPlayer var1, LightningBolt var2, List<Entity> var3) {
      List var4 = var3.stream().map(var1x -> EntityPredicate.createContext(var1, var1x)).collect(Collectors.toList());
      LootContext var5 = EntityPredicate.createContext(var1, var2);
      this.trigger(var1, var2x -> var2x.matches(var5, var4));
   }

   public static class TriggerInstance extends AbstractCriterionTriggerInstance {
      private final Optional<ContextAwarePredicate> lightning;
      private final Optional<ContextAwarePredicate> bystander;

      public TriggerInstance(Optional<ContextAwarePredicate> var1, Optional<ContextAwarePredicate> var2, Optional<ContextAwarePredicate> var3) {
         super(var1);
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
      public JsonObject serializeToJson() {
         JsonObject var1 = super.serializeToJson();
         this.lightning.ifPresent(var1x -> var1.add("lightning", var1x.toJson()));
         this.bystander.ifPresent(var1x -> var1.add("bystander", var1x.toJson()));
         return var1;
      }
   }
}
