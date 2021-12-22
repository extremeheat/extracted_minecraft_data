package net.minecraft.advancements.critereon;

import com.google.common.collect.ImmutableSet;
import com.google.gson.JsonObject;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.tags.Tag;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.alchemy.Potion;
import net.minecraft.world.level.ItemLike;

public class ConsumeItemTrigger extends SimpleCriterionTrigger<ConsumeItemTrigger.TriggerInstance> {
   // $FF: renamed from: ID net.minecraft.resources.ResourceLocation
   static final ResourceLocation field_85 = new ResourceLocation("consume_item");

   public ConsumeItemTrigger() {
      super();
   }

   public ResourceLocation getId() {
      return field_85;
   }

   public ConsumeItemTrigger.TriggerInstance createInstance(JsonObject var1, EntityPredicate.Composite var2, DeserializationContext var3) {
      return new ConsumeItemTrigger.TriggerInstance(var2, ItemPredicate.fromJson(var1.get("item")));
   }

   public void trigger(ServerPlayer var1, ItemStack var2) {
      this.trigger(var1, (var1x) -> {
         return var1x.matches(var2);
      });
   }

   // $FF: synthetic method
   public AbstractCriterionTriggerInstance createInstance(JsonObject var1, EntityPredicate.Composite var2, DeserializationContext var3) {
      return this.createInstance(var1, var2, var3);
   }

   public static class TriggerInstance extends AbstractCriterionTriggerInstance {
      private final ItemPredicate item;

      public TriggerInstance(EntityPredicate.Composite var1, ItemPredicate var2) {
         super(ConsumeItemTrigger.field_85, var1);
         this.item = var2;
      }

      public static ConsumeItemTrigger.TriggerInstance usedItem() {
         return new ConsumeItemTrigger.TriggerInstance(EntityPredicate.Composite.ANY, ItemPredicate.ANY);
      }

      public static ConsumeItemTrigger.TriggerInstance usedItem(ItemPredicate var0) {
         return new ConsumeItemTrigger.TriggerInstance(EntityPredicate.Composite.ANY, var0);
      }

      public static ConsumeItemTrigger.TriggerInstance usedItem(ItemLike var0) {
         return new ConsumeItemTrigger.TriggerInstance(EntityPredicate.Composite.ANY, new ItemPredicate((Tag)null, ImmutableSet.of(var0.asItem()), MinMaxBounds.Ints.ANY, MinMaxBounds.Ints.ANY, EnchantmentPredicate.NONE, EnchantmentPredicate.NONE, (Potion)null, NbtPredicate.ANY));
      }

      public boolean matches(ItemStack var1) {
         return this.item.matches(var1);
      }

      public JsonObject serializeToJson(SerializationContext var1) {
         JsonObject var2 = super.serializeToJson(var1);
         var2.add("item", this.item.serializeToJson());
         return var2;
      }
   }
}
