package net.minecraft.advancements.critereon;

import com.google.gson.JsonObject;
import java.util.Optional;
import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.advancements.Criterion;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;

public class EnchantedItemTrigger extends SimpleCriterionTrigger<EnchantedItemTrigger.TriggerInstance> {
   public EnchantedItemTrigger() {
      super();
   }

   public EnchantedItemTrigger.TriggerInstance createInstance(JsonObject var1, Optional<ContextAwarePredicate> var2, DeserializationContext var3) {
      Optional var4 = ItemPredicate.fromJson(var1.get("item"));
      MinMaxBounds.Ints var5 = MinMaxBounds.Ints.fromJson(var1.get("levels"));
      return new EnchantedItemTrigger.TriggerInstance(var2, var4, var5);
   }

   public void trigger(ServerPlayer var1, ItemStack var2, int var3) {
      this.trigger(var1, var2x -> var2x.matches(var2, var3));
   }

   public static class TriggerInstance extends AbstractCriterionTriggerInstance {
      private final Optional<ItemPredicate> item;
      private final MinMaxBounds.Ints levels;

      public TriggerInstance(Optional<ContextAwarePredicate> var1, Optional<ItemPredicate> var2, MinMaxBounds.Ints var3) {
         super(var1);
         this.item = var2;
         this.levels = var3;
      }

      public static Criterion<EnchantedItemTrigger.TriggerInstance> enchantedItem() {
         return CriteriaTriggers.ENCHANTED_ITEM
            .createCriterion(new EnchantedItemTrigger.TriggerInstance(Optional.empty(), Optional.empty(), MinMaxBounds.Ints.ANY));
      }

      public boolean matches(ItemStack var1, int var2) {
         if (this.item.isPresent() && !this.item.get().matches(var1)) {
            return false;
         } else {
            return this.levels.matches(var2);
         }
      }

      @Override
      public JsonObject serializeToJson() {
         JsonObject var1 = super.serializeToJson();
         this.item.ifPresent(var1x -> var1.add("item", var1x.serializeToJson()));
         var1.add("levels", this.levels.serializeToJson());
         return var1;
      }
   }
}
