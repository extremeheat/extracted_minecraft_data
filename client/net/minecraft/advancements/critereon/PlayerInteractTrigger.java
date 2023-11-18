package net.minecraft.advancements.critereon;

import com.google.gson.JsonObject;
import java.util.Optional;
import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.advancements.Criterion;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.storage.loot.LootContext;

public class PlayerInteractTrigger extends SimpleCriterionTrigger<PlayerInteractTrigger.TriggerInstance> {
   public PlayerInteractTrigger() {
      super();
   }

   protected PlayerInteractTrigger.TriggerInstance createInstance(JsonObject var1, Optional<ContextAwarePredicate> var2, DeserializationContext var3) {
      Optional var4 = ItemPredicate.fromJson(var1.get("item"));
      Optional var5 = EntityPredicate.fromJson(var1, "entity", var3);
      return new PlayerInteractTrigger.TriggerInstance(var2, var4, var5);
   }

   public void trigger(ServerPlayer var1, ItemStack var2, Entity var3) {
      LootContext var4 = EntityPredicate.createContext(var1, var3);
      this.trigger(var1, var2x -> var2x.matches(var2, var4));
   }

   public static class TriggerInstance extends AbstractCriterionTriggerInstance {
      private final Optional<ItemPredicate> item;
      private final Optional<ContextAwarePredicate> entity;

      public TriggerInstance(Optional<ContextAwarePredicate> var1, Optional<ItemPredicate> var2, Optional<ContextAwarePredicate> var3) {
         super(var1);
         this.item = var2;
         this.entity = var3;
      }

      public static Criterion<PlayerInteractTrigger.TriggerInstance> itemUsedOnEntity(
         Optional<ContextAwarePredicate> var0, ItemPredicate.Builder var1, Optional<ContextAwarePredicate> var2
      ) {
         return CriteriaTriggers.PLAYER_INTERACTED_WITH_ENTITY
            .createCriterion(new PlayerInteractTrigger.TriggerInstance(var0, Optional.of(var1.build()), var2));
      }

      public static Criterion<PlayerInteractTrigger.TriggerInstance> itemUsedOnEntity(ItemPredicate.Builder var0, Optional<ContextAwarePredicate> var1) {
         return itemUsedOnEntity(Optional.empty(), var0, var1);
      }

      public boolean matches(ItemStack var1, LootContext var2) {
         if (this.item.isPresent() && !this.item.get().matches(var1)) {
            return false;
         } else {
            return this.entity.isEmpty() || this.entity.get().matches(var2);
         }
      }

      @Override
      public JsonObject serializeToJson() {
         JsonObject var1 = super.serializeToJson();
         this.item.ifPresent(var1x -> var1.add("item", var1x.serializeToJson()));
         this.entity.ifPresent(var1x -> var1.add("entity", var1x.toJson()));
         return var1;
      }
   }
}
