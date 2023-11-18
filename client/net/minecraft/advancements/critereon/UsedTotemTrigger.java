package net.minecraft.advancements.critereon;

import com.google.gson.JsonObject;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ItemLike;

public class UsedTotemTrigger extends SimpleCriterionTrigger<UsedTotemTrigger.TriggerInstance> {
   static final ResourceLocation ID = new ResourceLocation("used_totem");

   public UsedTotemTrigger() {
      super();
   }

   @Override
   public ResourceLocation getId() {
      return ID;
   }

   public UsedTotemTrigger.TriggerInstance createInstance(JsonObject var1, ContextAwarePredicate var2, DeserializationContext var3) {
      ItemPredicate var4 = ItemPredicate.fromJson(var1.get("item"));
      return new UsedTotemTrigger.TriggerInstance(var2, var4);
   }

   public void trigger(ServerPlayer var1, ItemStack var2) {
      this.trigger(var1, var1x -> var1x.matches(var2));
   }

   public static class TriggerInstance extends AbstractCriterionTriggerInstance {
      private final ItemPredicate item;

      public TriggerInstance(ContextAwarePredicate var1, ItemPredicate var2) {
         super(UsedTotemTrigger.ID, var1);
         this.item = var2;
      }

      public static UsedTotemTrigger.TriggerInstance usedTotem(ItemPredicate var0) {
         return new UsedTotemTrigger.TriggerInstance(ContextAwarePredicate.ANY, var0);
      }

      public static UsedTotemTrigger.TriggerInstance usedTotem(ItemLike var0) {
         return new UsedTotemTrigger.TriggerInstance(ContextAwarePredicate.ANY, ItemPredicate.Builder.item().of(var0).build());
      }

      public boolean matches(ItemStack var1) {
         return this.item.matches(var1);
      }

      @Override
      public JsonObject serializeToJson(SerializationContext var1) {
         JsonObject var2 = super.serializeToJson(var1);
         var2.add("item", this.item.serializeToJson());
         return var2;
      }
   }
}
