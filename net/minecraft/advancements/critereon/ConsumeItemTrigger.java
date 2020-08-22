package net.minecraft.advancements.critereon;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import net.minecraft.advancements.CriterionTriggerInstance;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.tags.Tag;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.alchemy.Potion;
import net.minecraft.world.level.ItemLike;

public class ConsumeItemTrigger extends SimpleCriterionTrigger {
   private static final ResourceLocation ID = new ResourceLocation("consume_item");

   public ResourceLocation getId() {
      return ID;
   }

   public ConsumeItemTrigger.TriggerInstance createInstance(JsonObject var1, JsonDeserializationContext var2) {
      return new ConsumeItemTrigger.TriggerInstance(ItemPredicate.fromJson(var1.get("item")));
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
         super(ConsumeItemTrigger.ID);
         this.item = var1;
      }

      public static ConsumeItemTrigger.TriggerInstance usedItem() {
         return new ConsumeItemTrigger.TriggerInstance(ItemPredicate.ANY);
      }

      public static ConsumeItemTrigger.TriggerInstance usedItem(ItemLike var0) {
         return new ConsumeItemTrigger.TriggerInstance(new ItemPredicate((Tag)null, var0.asItem(), MinMaxBounds.Ints.ANY, MinMaxBounds.Ints.ANY, EnchantmentPredicate.NONE, EnchantmentPredicate.NONE, (Potion)null, NbtPredicate.ANY));
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
