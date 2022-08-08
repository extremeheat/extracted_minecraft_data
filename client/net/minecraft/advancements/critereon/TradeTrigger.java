package net.minecraft.advancements.critereon;

import com.google.gson.JsonObject;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.npc.AbstractVillager;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.storage.loot.LootContext;

public class TradeTrigger extends SimpleCriterionTrigger<TriggerInstance> {
   static final ResourceLocation ID = new ResourceLocation("villager_trade");

   public TradeTrigger() {
      super();
   }

   public ResourceLocation getId() {
      return ID;
   }

   public TriggerInstance createInstance(JsonObject var1, EntityPredicate.Composite var2, DeserializationContext var3) {
      EntityPredicate.Composite var4 = EntityPredicate.Composite.fromJson(var1, "villager", var3);
      ItemPredicate var5 = ItemPredicate.fromJson(var1.get("item"));
      return new TriggerInstance(var2, var4, var5);
   }

   public void trigger(ServerPlayer var1, AbstractVillager var2, ItemStack var3) {
      LootContext var4 = EntityPredicate.createContext(var1, var2);
      this.trigger(var1, (var2x) -> {
         return var2x.matches(var4, var3);
      });
   }

   // $FF: synthetic method
   public AbstractCriterionTriggerInstance createInstance(JsonObject var1, EntityPredicate.Composite var2, DeserializationContext var3) {
      return this.createInstance(var1, var2, var3);
   }

   public static class TriggerInstance extends AbstractCriterionTriggerInstance {
      private final EntityPredicate.Composite villager;
      private final ItemPredicate item;

      public TriggerInstance(EntityPredicate.Composite var1, EntityPredicate.Composite var2, ItemPredicate var3) {
         super(TradeTrigger.ID, var1);
         this.villager = var2;
         this.item = var3;
      }

      public static TriggerInstance tradedWithVillager() {
         return new TriggerInstance(EntityPredicate.Composite.ANY, EntityPredicate.Composite.ANY, ItemPredicate.ANY);
      }

      public static TriggerInstance tradedWithVillager(EntityPredicate.Builder var0) {
         return new TriggerInstance(EntityPredicate.Composite.wrap(var0.build()), EntityPredicate.Composite.ANY, ItemPredicate.ANY);
      }

      public boolean matches(LootContext var1, ItemStack var2) {
         if (!this.villager.matches(var1)) {
            return false;
         } else {
            return this.item.matches(var2);
         }
      }

      public JsonObject serializeToJson(SerializationContext var1) {
         JsonObject var2 = super.serializeToJson(var1);
         var2.add("item", this.item.serializeToJson());
         var2.add("villager", this.villager.toJson(var1));
         return var2;
      }
   }
}
