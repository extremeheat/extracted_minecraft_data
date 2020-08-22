package net.minecraft.advancements.critereon;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import net.minecraft.advancements.CriterionTriggerInstance;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.npc.AbstractVillager;
import net.minecraft.world.item.ItemStack;

public class TradeTrigger extends SimpleCriterionTrigger {
   private static final ResourceLocation ID = new ResourceLocation("villager_trade");

   public ResourceLocation getId() {
      return ID;
   }

   public TradeTrigger.TriggerInstance createInstance(JsonObject var1, JsonDeserializationContext var2) {
      EntityPredicate var3 = EntityPredicate.fromJson(var1.get("villager"));
      ItemPredicate var4 = ItemPredicate.fromJson(var1.get("item"));
      return new TradeTrigger.TriggerInstance(var3, var4);
   }

   public void trigger(ServerPlayer var1, AbstractVillager var2, ItemStack var3) {
      this.trigger(var1.getAdvancements(), (var3x) -> {
         return var3x.matches(var1, var2, var3);
      });
   }

   // $FF: synthetic method
   public CriterionTriggerInstance createInstance(JsonObject var1, JsonDeserializationContext var2) {
      return this.createInstance(var1, var2);
   }

   public static class TriggerInstance extends AbstractCriterionTriggerInstance {
      private final EntityPredicate villager;
      private final ItemPredicate item;

      public TriggerInstance(EntityPredicate var1, ItemPredicate var2) {
         super(TradeTrigger.ID);
         this.villager = var1;
         this.item = var2;
      }

      public static TradeTrigger.TriggerInstance tradedWithVillager() {
         return new TradeTrigger.TriggerInstance(EntityPredicate.ANY, ItemPredicate.ANY);
      }

      public boolean matches(ServerPlayer var1, AbstractVillager var2, ItemStack var3) {
         if (!this.villager.matches(var1, var2)) {
            return false;
         } else {
            return this.item.matches(var3);
         }
      }

      public JsonElement serializeToJson() {
         JsonObject var1 = new JsonObject();
         var1.add("item", this.item.serializeToJson());
         var1.add("villager", this.villager.serializeToJson());
         return var1;
      }
   }
}
