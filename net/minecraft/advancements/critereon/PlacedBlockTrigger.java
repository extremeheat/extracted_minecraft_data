package net.minecraft.advancements.critereon;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;
import javax.annotation.Nullable;
import net.minecraft.advancements.CriterionTriggerInstance;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;

public class PlacedBlockTrigger extends SimpleCriterionTrigger {
   private static final ResourceLocation ID = new ResourceLocation("placed_block");

   public ResourceLocation getId() {
      return ID;
   }

   public PlacedBlockTrigger.TriggerInstance createInstance(JsonObject var1, JsonDeserializationContext var2) {
      Block var3 = deserializeBlock(var1);
      StatePropertiesPredicate var4 = StatePropertiesPredicate.fromJson(var1.get("state"));
      if (var3 != null) {
         var4.checkState(var3.getStateDefinition(), (var1x) -> {
            throw new JsonSyntaxException("Block " + var3 + " has no property " + var1x + ":");
         });
      }

      LocationPredicate var5 = LocationPredicate.fromJson(var1.get("location"));
      ItemPredicate var6 = ItemPredicate.fromJson(var1.get("item"));
      return new PlacedBlockTrigger.TriggerInstance(var3, var4, var5, var6);
   }

   @Nullable
   private static Block deserializeBlock(JsonObject var0) {
      if (var0.has("block")) {
         ResourceLocation var1 = new ResourceLocation(GsonHelper.getAsString(var0, "block"));
         return (Block)Registry.BLOCK.getOptional(var1).orElseThrow(() -> {
            return new JsonSyntaxException("Unknown block type '" + var1 + "'");
         });
      } else {
         return null;
      }
   }

   public void trigger(ServerPlayer var1, BlockPos var2, ItemStack var3) {
      BlockState var4 = var1.getLevel().getBlockState(var2);
      this.trigger(var1.getAdvancements(), (var4x) -> {
         return var4x.matches(var4, var2, var1.getLevel(), var3);
      });
   }

   // $FF: synthetic method
   public CriterionTriggerInstance createInstance(JsonObject var1, JsonDeserializationContext var2) {
      return this.createInstance(var1, var2);
   }

   public static class TriggerInstance extends AbstractCriterionTriggerInstance {
      private final Block block;
      private final StatePropertiesPredicate state;
      private final LocationPredicate location;
      private final ItemPredicate item;

      public TriggerInstance(@Nullable Block var1, StatePropertiesPredicate var2, LocationPredicate var3, ItemPredicate var4) {
         super(PlacedBlockTrigger.ID);
         this.block = var1;
         this.state = var2;
         this.location = var3;
         this.item = var4;
      }

      public static PlacedBlockTrigger.TriggerInstance placedBlock(Block var0) {
         return new PlacedBlockTrigger.TriggerInstance(var0, StatePropertiesPredicate.ANY, LocationPredicate.ANY, ItemPredicate.ANY);
      }

      public boolean matches(BlockState var1, BlockPos var2, ServerLevel var3, ItemStack var4) {
         if (this.block != null && var1.getBlock() != this.block) {
            return false;
         } else if (!this.state.matches(var1)) {
            return false;
         } else if (!this.location.matches(var3, (float)var2.getX(), (float)var2.getY(), (float)var2.getZ())) {
            return false;
         } else {
            return this.item.matches(var4);
         }
      }

      public JsonElement serializeToJson() {
         JsonObject var1 = new JsonObject();
         if (this.block != null) {
            var1.addProperty("block", Registry.BLOCK.getKey(this.block).toString());
         }

         var1.add("state", this.state.serializeToJson());
         var1.add("location", this.location.serializeToJson());
         var1.add("item", this.item.serializeToJson());
         return var1;
      }
   }
}
