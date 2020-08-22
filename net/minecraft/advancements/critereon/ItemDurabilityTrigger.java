package net.minecraft.advancements.critereon;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import net.minecraft.advancements.CriterionTriggerInstance;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;

public class ItemDurabilityTrigger extends SimpleCriterionTrigger {
   private static final ResourceLocation ID = new ResourceLocation("item_durability_changed");

   public ResourceLocation getId() {
      return ID;
   }

   public ItemDurabilityTrigger.TriggerInstance createInstance(JsonObject var1, JsonDeserializationContext var2) {
      ItemPredicate var3 = ItemPredicate.fromJson(var1.get("item"));
      MinMaxBounds.Ints var4 = MinMaxBounds.Ints.fromJson(var1.get("durability"));
      MinMaxBounds.Ints var5 = MinMaxBounds.Ints.fromJson(var1.get("delta"));
      return new ItemDurabilityTrigger.TriggerInstance(var3, var4, var5);
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
      private final MinMaxBounds.Ints durability;
      private final MinMaxBounds.Ints delta;

      public TriggerInstance(ItemPredicate var1, MinMaxBounds.Ints var2, MinMaxBounds.Ints var3) {
         super(ItemDurabilityTrigger.ID);
         this.item = var1;
         this.durability = var2;
         this.delta = var3;
      }

      public static ItemDurabilityTrigger.TriggerInstance changedDurability(ItemPredicate var0, MinMaxBounds.Ints var1) {
         return new ItemDurabilityTrigger.TriggerInstance(var0, var1, MinMaxBounds.Ints.ANY);
      }

      public boolean matches(ItemStack var1, int var2) {
         if (!this.item.matches(var1)) {
            return false;
         } else if (!this.durability.matches(var1.getMaxDamage() - var2)) {
            return false;
         } else {
            return this.delta.matches(var1.getDamageValue() - var2);
         }
      }

      public JsonElement serializeToJson() {
         JsonObject var1 = new JsonObject();
         var1.add("item", this.item.serializeToJson());
         var1.add("durability", this.durability.serializeToJson());
         var1.add("delta", this.delta.serializeToJson());
         return var1;
      }
   }
}
