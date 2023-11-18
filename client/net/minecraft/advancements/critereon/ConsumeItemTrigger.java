package net.minecraft.advancements.critereon;

import com.google.common.collect.ImmutableSet;
import com.google.gson.JsonObject;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ItemLike;

public class ConsumeItemTrigger extends SimpleCriterionTrigger<ConsumeItemTrigger.TriggerInstance> {
   static final ResourceLocation ID = new ResourceLocation("consume_item");

   public ConsumeItemTrigger() {
      super();
   }

   @Override
   public ResourceLocation getId() {
      return ID;
   }

   public ConsumeItemTrigger.TriggerInstance createInstance(JsonObject var1, ContextAwarePredicate var2, DeserializationContext var3) {
      return new ConsumeItemTrigger.TriggerInstance(var2, ItemPredicate.fromJson(var1.get("item")));
   }

   public void trigger(ServerPlayer var1, ItemStack var2) {
      this.trigger(var1, var1x -> var1x.matches(var2));
   }

   public static class TriggerInstance extends AbstractCriterionTriggerInstance {
      private final ItemPredicate item;

      public TriggerInstance(ContextAwarePredicate var1, ItemPredicate var2) {
         super(ConsumeItemTrigger.ID, var1);
         this.item = var2;
      }

      public static ConsumeItemTrigger.TriggerInstance usedItem() {
         return new ConsumeItemTrigger.TriggerInstance(ContextAwarePredicate.ANY, ItemPredicate.ANY);
      }

      public static ConsumeItemTrigger.TriggerInstance usedItem(ItemPredicate var0) {
         return new ConsumeItemTrigger.TriggerInstance(ContextAwarePredicate.ANY, var0);
      }

      public static ConsumeItemTrigger.TriggerInstance usedItem(ItemLike var0) {
         return new ConsumeItemTrigger.TriggerInstance(
            ContextAwarePredicate.ANY,
            new ItemPredicate(
               null,
               ImmutableSet.of(var0.asItem()),
               MinMaxBounds.Ints.ANY,
               MinMaxBounds.Ints.ANY,
               EnchantmentPredicate.NONE,
               EnchantmentPredicate.NONE,
               null,
               NbtPredicate.ANY
            )
         );
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
