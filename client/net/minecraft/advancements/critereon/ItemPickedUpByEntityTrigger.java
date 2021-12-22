package net.minecraft.advancements.critereon;

import com.google.gson.JsonObject;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.storage.loot.LootContext;

public class ItemPickedUpByEntityTrigger extends SimpleCriterionTrigger<ItemPickedUpByEntityTrigger.TriggerInstance> {
   // $FF: renamed from: ID net.minecraft.resources.ResourceLocation
   static final ResourceLocation field_91 = new ResourceLocation("thrown_item_picked_up_by_entity");

   public ItemPickedUpByEntityTrigger() {
      super();
   }

   public ResourceLocation getId() {
      return field_91;
   }

   protected ItemPickedUpByEntityTrigger.TriggerInstance createInstance(JsonObject var1, EntityPredicate.Composite var2, DeserializationContext var3) {
      ItemPredicate var4 = ItemPredicate.fromJson(var1.get("item"));
      EntityPredicate.Composite var5 = EntityPredicate.Composite.fromJson(var1, "entity", var3);
      return new ItemPickedUpByEntityTrigger.TriggerInstance(var2, var4, var5);
   }

   public void trigger(ServerPlayer var1, ItemStack var2, Entity var3) {
      LootContext var4 = EntityPredicate.createContext(var1, var3);
      this.trigger(var1, (var3x) -> {
         return var3x.matches(var1, var2, var4);
      });
   }

   // $FF: synthetic method
   protected AbstractCriterionTriggerInstance createInstance(JsonObject var1, EntityPredicate.Composite var2, DeserializationContext var3) {
      return this.createInstance(var1, var2, var3);
   }

   public static class TriggerInstance extends AbstractCriterionTriggerInstance {
      private final ItemPredicate item;
      private final EntityPredicate.Composite entity;

      public TriggerInstance(EntityPredicate.Composite var1, ItemPredicate var2, EntityPredicate.Composite var3) {
         super(ItemPickedUpByEntityTrigger.field_91, var1);
         this.item = var2;
         this.entity = var3;
      }

      public static ItemPickedUpByEntityTrigger.TriggerInstance itemPickedUpByEntity(EntityPredicate.Composite var0, ItemPredicate.Builder var1, EntityPredicate.Composite var2) {
         return new ItemPickedUpByEntityTrigger.TriggerInstance(var0, var1.build(), var2);
      }

      public boolean matches(ServerPlayer var1, ItemStack var2, LootContext var3) {
         if (!this.item.matches(var2)) {
            return false;
         } else {
            return this.entity.matches(var3);
         }
      }

      public JsonObject serializeToJson(SerializationContext var1) {
         JsonObject var2 = super.serializeToJson(var1);
         var2.add("item", this.item.serializeToJson());
         var2.add("entity", this.entity.toJson(var1));
         return var2;
      }
   }
}
