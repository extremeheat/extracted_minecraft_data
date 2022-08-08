package net.minecraft.advancements.critereon;

import com.google.gson.JsonObject;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;

public class ItemDurabilityTrigger extends SimpleCriterionTrigger<TriggerInstance> {
   static final ResourceLocation ID = new ResourceLocation("item_durability_changed");

   public ItemDurabilityTrigger() {
      super();
   }

   public ResourceLocation getId() {
      return ID;
   }

   public TriggerInstance createInstance(JsonObject var1, EntityPredicate.Composite var2, DeserializationContext var3) {
      ItemPredicate var4 = ItemPredicate.fromJson(var1.get("item"));
      MinMaxBounds.Ints var5 = MinMaxBounds.Ints.fromJson(var1.get("durability"));
      MinMaxBounds.Ints var6 = MinMaxBounds.Ints.fromJson(var1.get("delta"));
      return new TriggerInstance(var2, var4, var5, var6);
   }

   public void trigger(ServerPlayer var1, ItemStack var2, int var3) {
      this.trigger(var1, (var2x) -> {
         return var2x.matches(var2, var3);
      });
   }

   // $FF: synthetic method
   public AbstractCriterionTriggerInstance createInstance(JsonObject var1, EntityPredicate.Composite var2, DeserializationContext var3) {
      return this.createInstance(var1, var2, var3);
   }

   public static class TriggerInstance extends AbstractCriterionTriggerInstance {
      private final ItemPredicate item;
      private final MinMaxBounds.Ints durability;
      private final MinMaxBounds.Ints delta;

      public TriggerInstance(EntityPredicate.Composite var1, ItemPredicate var2, MinMaxBounds.Ints var3, MinMaxBounds.Ints var4) {
         super(ItemDurabilityTrigger.ID, var1);
         this.item = var2;
         this.durability = var3;
         this.delta = var4;
      }

      public static TriggerInstance changedDurability(ItemPredicate var0, MinMaxBounds.Ints var1) {
         return changedDurability(EntityPredicate.Composite.ANY, var0, var1);
      }

      public static TriggerInstance changedDurability(EntityPredicate.Composite var0, ItemPredicate var1, MinMaxBounds.Ints var2) {
         return new TriggerInstance(var0, var1, var2, MinMaxBounds.Ints.ANY);
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

      public JsonObject serializeToJson(SerializationContext var1) {
         JsonObject var2 = super.serializeToJson(var1);
         var2.add("item", this.item.serializeToJson());
         var2.add("durability", this.durability.serializeToJson());
         var2.add("delta", this.delta.serializeToJson());
         return var2;
      }
   }
}
