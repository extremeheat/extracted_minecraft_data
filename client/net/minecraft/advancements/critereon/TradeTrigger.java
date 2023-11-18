package net.minecraft.advancements.critereon;

import com.google.gson.JsonObject;
import java.util.Optional;
import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.advancements.Criterion;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.npc.AbstractVillager;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.storage.loot.LootContext;

public class TradeTrigger extends SimpleCriterionTrigger<TradeTrigger.TriggerInstance> {
   public TradeTrigger() {
      super();
   }

   public TradeTrigger.TriggerInstance createInstance(JsonObject var1, Optional<ContextAwarePredicate> var2, DeserializationContext var3) {
      Optional var4 = EntityPredicate.fromJson(var1, "villager", var3);
      Optional var5 = ItemPredicate.fromJson(var1.get("item"));
      return new TradeTrigger.TriggerInstance(var2, var4, var5);
   }

   public void trigger(ServerPlayer var1, AbstractVillager var2, ItemStack var3) {
      LootContext var4 = EntityPredicate.createContext(var1, var2);
      this.trigger(var1, var2x -> var2x.matches(var4, var3));
   }

   public static class TriggerInstance extends AbstractCriterionTriggerInstance {
      private final Optional<ContextAwarePredicate> villager;
      private final Optional<ItemPredicate> item;

      public TriggerInstance(Optional<ContextAwarePredicate> var1, Optional<ContextAwarePredicate> var2, Optional<ItemPredicate> var3) {
         super(var1);
         this.villager = var2;
         this.item = var3;
      }

      public static Criterion<TradeTrigger.TriggerInstance> tradedWithVillager() {
         return CriteriaTriggers.TRADE.createCriterion(new TradeTrigger.TriggerInstance(Optional.empty(), Optional.empty(), Optional.empty()));
      }

      public static Criterion<TradeTrigger.TriggerInstance> tradedWithVillager(EntityPredicate.Builder var0) {
         return CriteriaTriggers.TRADE
            .createCriterion(new TradeTrigger.TriggerInstance(Optional.of(EntityPredicate.wrap(var0)), Optional.empty(), Optional.empty()));
      }

      public boolean matches(LootContext var1, ItemStack var2) {
         if (this.villager.isPresent() && !this.villager.get().matches(var1)) {
            return false;
         } else {
            return !this.item.isPresent() || this.item.get().matches(var2);
         }
      }

      @Override
      public JsonObject serializeToJson() {
         JsonObject var1 = super.serializeToJson();
         this.item.ifPresent(var1x -> var1.add("item", var1x.serializeToJson()));
         this.villager.ifPresent(var1x -> var1.add("villager", var1x.toJson()));
         return var1;
      }
   }
}
