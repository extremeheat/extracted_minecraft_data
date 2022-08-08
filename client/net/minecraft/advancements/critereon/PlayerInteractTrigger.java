package net.minecraft.advancements.critereon;

import com.google.gson.JsonObject;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.storage.loot.LootContext;

public class PlayerInteractTrigger extends SimpleCriterionTrigger<TriggerInstance> {
   static final ResourceLocation ID = new ResourceLocation("player_interacted_with_entity");

   public PlayerInteractTrigger() {
      super();
   }

   public ResourceLocation getId() {
      return ID;
   }

   protected TriggerInstance createInstance(JsonObject var1, EntityPredicate.Composite var2, DeserializationContext var3) {
      ItemPredicate var4 = ItemPredicate.fromJson(var1.get("item"));
      EntityPredicate.Composite var5 = EntityPredicate.Composite.fromJson(var1, "entity", var3);
      return new TriggerInstance(var2, var4, var5);
   }

   public void trigger(ServerPlayer var1, ItemStack var2, Entity var3) {
      LootContext var4 = EntityPredicate.createContext(var1, var3);
      this.trigger(var1, (var2x) -> {
         return var2x.matches(var2, var4);
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
         super(PlayerInteractTrigger.ID, var1);
         this.item = var2;
         this.entity = var3;
      }

      public static TriggerInstance itemUsedOnEntity(EntityPredicate.Composite var0, ItemPredicate.Builder var1, EntityPredicate.Composite var2) {
         return new TriggerInstance(var0, var1.build(), var2);
      }

      public static TriggerInstance itemUsedOnEntity(ItemPredicate.Builder var0, EntityPredicate.Composite var1) {
         return itemUsedOnEntity(EntityPredicate.Composite.ANY, var0, var1);
      }

      public boolean matches(ItemStack var1, LootContext var2) {
         return !this.item.matches(var1) ? false : this.entity.matches(var2);
      }

      public JsonObject serializeToJson(SerializationContext var1) {
         JsonObject var2 = super.serializeToJson(var1);
         var2.add("item", this.item.serializeToJson());
         var2.add("entity", this.entity.toJson(var1));
         return var2;
      }
   }
}
