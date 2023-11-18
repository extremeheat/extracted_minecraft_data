package net.minecraft.advancements.critereon;

import com.google.gson.JsonObject;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;

public class FilledBucketTrigger extends SimpleCriterionTrigger<FilledBucketTrigger.TriggerInstance> {
   static final ResourceLocation ID = new ResourceLocation("filled_bucket");

   public FilledBucketTrigger() {
      super();
   }

   @Override
   public ResourceLocation getId() {
      return ID;
   }

   public FilledBucketTrigger.TriggerInstance createInstance(JsonObject var1, ContextAwarePredicate var2, DeserializationContext var3) {
      ItemPredicate var4 = ItemPredicate.fromJson(var1.get("item"));
      return new FilledBucketTrigger.TriggerInstance(var2, var4);
   }

   public void trigger(ServerPlayer var1, ItemStack var2) {
      this.trigger(var1, var1x -> var1x.matches(var2));
   }

   public static class TriggerInstance extends AbstractCriterionTriggerInstance {
      private final ItemPredicate item;

      public TriggerInstance(ContextAwarePredicate var1, ItemPredicate var2) {
         super(FilledBucketTrigger.ID, var1);
         this.item = var2;
      }

      public static FilledBucketTrigger.TriggerInstance filledBucket(ItemPredicate var0) {
         return new FilledBucketTrigger.TriggerInstance(ContextAwarePredicate.ANY, var0);
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
