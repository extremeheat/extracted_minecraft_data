package net.minecraft.advancements.critereon;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import net.minecraft.advancements.CriterionTriggerInstance;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;

public class EnchantedItemTrigger extends SimpleCriterionTrigger {
   private static final ResourceLocation ID = new ResourceLocation("enchanted_item");

   public ResourceLocation getId() {
      return ID;
   }

   public EnchantedItemTrigger.TriggerInstance createInstance(JsonObject var1, JsonDeserializationContext var2) {
      ItemPredicate var3 = ItemPredicate.fromJson(var1.get("item"));
      MinMaxBounds.Ints var4 = MinMaxBounds.Ints.fromJson(var1.get("levels"));
      return new EnchantedItemTrigger.TriggerInstance(var3, var4);
   }

   public void trigger(ServerPlayer var1, ItemStack var2, int var3) {
      this.trigger(var1.getAdvancements(), (var2x) -> {
         return var2x.matches(var2, var3);
      });
   }

   // $FF: synthetic method
   public CriterionTriggerInstance createInstance(JsonObject var1, JsonDeserializationContext var2) {
      return this.createInstance(var1, var2);
   }

   public static class TriggerInstance extends AbstractCriterionTriggerInstance {
      private final ItemPredicate item;
      private final MinMaxBounds.Ints levels;

      public TriggerInstance(ItemPredicate var1, MinMaxBounds.Ints var2) {
         super(EnchantedItemTrigger.ID);
         this.item = var1;
         this.levels = var2;
      }

      public static EnchantedItemTrigger.TriggerInstance enchantedItem() {
         return new EnchantedItemTrigger.TriggerInstance(ItemPredicate.ANY, MinMaxBounds.Ints.ANY);
      }

      public boolean matches(ItemStack var1, int var2) {
         if (!this.item.matches(var1)) {
            return false;
         } else {
            return this.levels.matches(var2);
         }
      }

      public JsonElement serializeToJson() {
         JsonObject var1 = new JsonObject();
         var1.add("item", this.item.serializeToJson());
         var1.add("levels", this.levels.serializeToJson());
         return var1;
      }
   }
}
