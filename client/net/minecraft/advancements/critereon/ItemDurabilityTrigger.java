package net.minecraft.advancements.critereon;

import com.google.gson.JsonObject;
import java.util.Optional;
import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.advancements.Criterion;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;

public class ItemDurabilityTrigger extends SimpleCriterionTrigger<ItemDurabilityTrigger.TriggerInstance> {
   public ItemDurabilityTrigger() {
      super();
   }

   public ItemDurabilityTrigger.TriggerInstance createInstance(JsonObject var1, Optional<ContextAwarePredicate> var2, DeserializationContext var3) {
      Optional var4 = ItemPredicate.fromJson(var1.get("item"));
      MinMaxBounds.Ints var5 = MinMaxBounds.Ints.fromJson(var1.get("durability"));
      MinMaxBounds.Ints var6 = MinMaxBounds.Ints.fromJson(var1.get("delta"));
      return new ItemDurabilityTrigger.TriggerInstance(var2, var4, var5, var6);
   }

   public void trigger(ServerPlayer var1, ItemStack var2, int var3) {
      this.trigger(var1, var2x -> var2x.matches(var2, var3));
   }

   public static class TriggerInstance extends AbstractCriterionTriggerInstance {
      private final Optional<ItemPredicate> item;
      private final MinMaxBounds.Ints durability;
      private final MinMaxBounds.Ints delta;

      public TriggerInstance(Optional<ContextAwarePredicate> var1, Optional<ItemPredicate> var2, MinMaxBounds.Ints var3, MinMaxBounds.Ints var4) {
         super(var1);
         this.item = var2;
         this.durability = var3;
         this.delta = var4;
      }

      public static Criterion<ItemDurabilityTrigger.TriggerInstance> changedDurability(Optional<ItemPredicate> var0, MinMaxBounds.Ints var1) {
         return changedDurability(Optional.empty(), var0, var1);
      }

      public static Criterion<ItemDurabilityTrigger.TriggerInstance> changedDurability(
         Optional<ContextAwarePredicate> var0, Optional<ItemPredicate> var1, MinMaxBounds.Ints var2
      ) {
         return CriteriaTriggers.ITEM_DURABILITY_CHANGED.createCriterion(new ItemDurabilityTrigger.TriggerInstance(var0, var1, var2, MinMaxBounds.Ints.ANY));
      }

      public boolean matches(ItemStack var1, int var2) {
         if (this.item.isPresent() && !this.item.get().matches(var1)) {
            return false;
         } else if (!this.durability.matches(var1.getMaxDamage() - var2)) {
            return false;
         } else {
            return this.delta.matches(var1.getDamageValue() - var2);
         }
      }

      @Override
      public JsonObject serializeToJson() {
         JsonObject var1 = super.serializeToJson();
         this.item.ifPresent(var1x -> var1.add("item", var1x.serializeToJson()));
         var1.add("durability", this.durability.serializeToJson());
         var1.add("delta", this.delta.serializeToJson());
         return var1;
      }
   }
}
