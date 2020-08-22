package net.minecraft.advancements.critereon;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.advancements.CriterionTriggerInstance;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockState;

public class ItemUsedOnBlockTrigger extends SimpleCriterionTrigger {
   private final ResourceLocation id;

   public ItemUsedOnBlockTrigger(ResourceLocation var1) {
      this.id = var1;
   }

   public ResourceLocation getId() {
      return this.id;
   }

   public ItemUsedOnBlockTrigger.TriggerInstance createInstance(JsonObject var1, JsonDeserializationContext var2) {
      BlockPredicate var3 = BlockPredicate.fromJson(var1.get("block"));
      StatePropertiesPredicate var4 = StatePropertiesPredicate.fromJson(var1.get("state"));
      ItemPredicate var5 = ItemPredicate.fromJson(var1.get("item"));
      return new ItemUsedOnBlockTrigger.TriggerInstance(this.id, var3, var4, var5);
   }

   public void trigger(ServerPlayer var1, BlockPos var2, ItemStack var3) {
      BlockState var4 = var1.getLevel().getBlockState(var2);
      this.trigger(var1.getAdvancements(), (var4x) -> {
         return var4x.matches(var4, var1.getLevel(), var2, var3);
      });
   }

   // $FF: synthetic method
   public CriterionTriggerInstance createInstance(JsonObject var1, JsonDeserializationContext var2) {
      return this.createInstance(var1, var2);
   }

   public static class TriggerInstance extends AbstractCriterionTriggerInstance {
      private final BlockPredicate block;
      private final StatePropertiesPredicate state;
      private final ItemPredicate item;

      public TriggerInstance(ResourceLocation var1, BlockPredicate var2, StatePropertiesPredicate var3, ItemPredicate var4) {
         super(var1);
         this.block = var2;
         this.state = var3;
         this.item = var4;
      }

      public static ItemUsedOnBlockTrigger.TriggerInstance safelyHarvestedHoney(BlockPredicate.Builder var0, ItemPredicate.Builder var1) {
         return new ItemUsedOnBlockTrigger.TriggerInstance(CriteriaTriggers.SAFELY_HARVEST_HONEY.id, var0.build(), StatePropertiesPredicate.ANY, var1.build());
      }

      public boolean matches(BlockState var1, ServerLevel var2, BlockPos var3, ItemStack var4) {
         if (!this.block.matches(var2, var3)) {
            return false;
         } else if (!this.state.matches(var1)) {
            return false;
         } else {
            return this.item.matches(var4);
         }
      }

      public JsonElement serializeToJson() {
         JsonObject var1 = new JsonObject();
         var1.add("block", this.block.serializeToJson());
         var1.add("state", this.state.serializeToJson());
         var1.add("item", this.item.serializeToJson());
         return var1;
      }
   }
}
