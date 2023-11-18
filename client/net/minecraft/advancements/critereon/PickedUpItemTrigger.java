package net.minecraft.advancements.critereon;

import com.google.gson.JsonObject;
import java.util.Optional;
import javax.annotation.Nullable;
import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.advancements.Criterion;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.storage.loot.LootContext;

public class PickedUpItemTrigger extends SimpleCriterionTrigger<PickedUpItemTrigger.TriggerInstance> {
   public PickedUpItemTrigger() {
      super();
   }

   protected PickedUpItemTrigger.TriggerInstance createInstance(JsonObject var1, Optional<ContextAwarePredicate> var2, DeserializationContext var3) {
      Optional var4 = ItemPredicate.fromJson(var1.get("item"));
      Optional var5 = EntityPredicate.fromJson(var1, "entity", var3);
      return new PickedUpItemTrigger.TriggerInstance(var2, var4, var5);
   }

   public void trigger(ServerPlayer var1, ItemStack var2, @Nullable Entity var3) {
      LootContext var4 = EntityPredicate.createContext(var1, var3);
      this.trigger(var1, var3x -> var3x.matches(var1, var2, var4));
   }

   public static class TriggerInstance extends AbstractCriterionTriggerInstance {
      private final Optional<ItemPredicate> item;
      private final Optional<ContextAwarePredicate> entity;

      public TriggerInstance(Optional<ContextAwarePredicate> var1, Optional<ItemPredicate> var2, Optional<ContextAwarePredicate> var3) {
         super(var1);
         this.item = var2;
         this.entity = var3;
      }

      public static Criterion<PickedUpItemTrigger.TriggerInstance> thrownItemPickedUpByEntity(
         ContextAwarePredicate var0, Optional<ItemPredicate> var1, Optional<ContextAwarePredicate> var2
      ) {
         return CriteriaTriggers.THROWN_ITEM_PICKED_UP_BY_ENTITY.createCriterion(new PickedUpItemTrigger.TriggerInstance(Optional.of(var0), var1, var2));
      }

      public static Criterion<PickedUpItemTrigger.TriggerInstance> thrownItemPickedUpByPlayer(
         Optional<ContextAwarePredicate> var0, Optional<ItemPredicate> var1, Optional<ContextAwarePredicate> var2
      ) {
         return CriteriaTriggers.THROWN_ITEM_PICKED_UP_BY_PLAYER.createCriterion(new PickedUpItemTrigger.TriggerInstance(var0, var1, var2));
      }

      public boolean matches(ServerPlayer var1, ItemStack var2, LootContext var3) {
         if (this.item.isPresent() && !this.item.get().matches(var2)) {
            return false;
         } else {
            return !this.entity.isPresent() || this.entity.get().matches(var3);
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
