package net.minecraft.advancements.critereon;

import com.google.gson.JsonObject;
import java.util.Optional;
import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.advancements.Criterion;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.storage.loot.LootContext;

public class SummonedEntityTrigger extends SimpleCriterionTrigger<SummonedEntityTrigger.TriggerInstance> {
   public SummonedEntityTrigger() {
      super();
   }

   public SummonedEntityTrigger.TriggerInstance createInstance(JsonObject var1, Optional<ContextAwarePredicate> var2, DeserializationContext var3) {
      Optional var4 = EntityPredicate.fromJson(var1, "entity", var3);
      return new SummonedEntityTrigger.TriggerInstance(var2, var4);
   }

   public void trigger(ServerPlayer var1, Entity var2) {
      LootContext var3 = EntityPredicate.createContext(var1, var2);
      this.trigger(var1, var1x -> var1x.matches(var3));
   }

   public static class TriggerInstance extends AbstractCriterionTriggerInstance {
      private final Optional<ContextAwarePredicate> entity;

      public TriggerInstance(Optional<ContextAwarePredicate> var1, Optional<ContextAwarePredicate> var2) {
         super(var1);
         this.entity = var2;
      }

      public static Criterion<SummonedEntityTrigger.TriggerInstance> summonedEntity(EntityPredicate.Builder var0) {
         return CriteriaTriggers.SUMMONED_ENTITY
            .createCriterion(new SummonedEntityTrigger.TriggerInstance(Optional.empty(), Optional.of(EntityPredicate.wrap(var0))));
      }

      public boolean matches(LootContext var1) {
         return this.entity.isEmpty() || this.entity.get().matches(var1);
      }

      @Override
      public JsonObject serializeToJson() {
         JsonObject var1 = super.serializeToJson();
         this.entity.ifPresent(var1x -> var1.add("entity", var1x.toJson()));
         return var1;
      }
   }
}
