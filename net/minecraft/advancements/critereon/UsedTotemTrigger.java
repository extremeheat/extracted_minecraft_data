package net.minecraft.advancements.critereon;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import net.minecraft.advancements.CriterionTriggerInstance;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ItemLike;

public class UsedTotemTrigger extends SimpleCriterionTrigger {
   private static final ResourceLocation ID = new ResourceLocation("used_totem");

   public ResourceLocation getId() {
      return ID;
   }

   public UsedTotemTrigger.TriggerInstance createInstance(JsonObject var1, JsonDeserializationContext var2) {
      ItemPredicate var3 = ItemPredicate.fromJson(var1.get("item"));
      return new UsedTotemTrigger.TriggerInstance(var3);
   }

   public void trigger(ServerPlayer var1, ItemStack var2) {
      this.trigger(var1.getAdvancements(), (var1x) -> {
         return var1x.matches(var2);
      });
   }

   // $FF: synthetic method
   public CriterionTriggerInstance createInstance(JsonObject var1, JsonDeserializationContext var2) {
      return this.createInstance(var1, var2);
   }

   public static class TriggerInstance extends AbstractCriterionTriggerInstance {
      private final ItemPredicate item;

      public TriggerInstance(ItemPredicate var1) {
         super(UsedTotemTrigger.ID);
         this.item = var1;
      }

      public static UsedTotemTrigger.TriggerInstance usedTotem(ItemLike var0) {
         return new UsedTotemTrigger.TriggerInstance(ItemPredicate.Builder.item().of(var0).build());
      }

      public boolean matches(ItemStack var1) {
         return this.item.matches(var1);
      }

      public JsonElement serializeToJson() {
         JsonObject var1 = new JsonObject();
         var1.add("item", this.item.serializeToJson());
         return var1;
      }
   }
}
