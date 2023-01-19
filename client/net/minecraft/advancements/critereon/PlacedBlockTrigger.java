package net.minecraft.advancements.critereon;

import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;

public class PlacedBlockTrigger extends SimpleCriterionTrigger<PlacedBlockTrigger.TriggerInstance> {
   static final ResourceLocation ID = new ResourceLocation("placed_block");

   public PlacedBlockTrigger() {
      super();
   }

   @Override
   public ResourceLocation getId() {
      return ID;
   }

   public PlacedBlockTrigger.TriggerInstance createInstance(JsonObject var1, EntityPredicate.Composite var2, DeserializationContext var3) {
      Block var4 = deserializeBlock(var1);
      StatePropertiesPredicate var5 = StatePropertiesPredicate.fromJson(var1.get("state"));
      if (var4 != null) {
         var5.checkState(var4.getStateDefinition(), var1x -> {
            throw new JsonSyntaxException("Block " + var4 + " has no property " + var1x + ":");
         });
      }

      LocationPredicate var6 = LocationPredicate.fromJson(var1.get("location"));
      ItemPredicate var7 = ItemPredicate.fromJson(var1.get("item"));
      return new PlacedBlockTrigger.TriggerInstance(var2, var4, var5, var6, var7);
   }

   @Nullable
   private static Block deserializeBlock(JsonObject var0) {
      if (var0.has("block")) {
         ResourceLocation var1 = new ResourceLocation(GsonHelper.getAsString(var0, "block"));
         return Registry.BLOCK.getOptional(var1).orElseThrow(() -> new JsonSyntaxException("Unknown block type '" + var1 + "'"));
      } else {
         return null;
      }
   }

   public void trigger(ServerPlayer var1, BlockPos var2, ItemStack var3) {
      BlockState var4 = var1.getLevel().getBlockState(var2);
      this.trigger(var1, var4x -> var4x.matches(var4, var2, var1.getLevel(), var3));
   }

   public static class TriggerInstance extends AbstractCriterionTriggerInstance {
      @Nullable
      private final Block block;
      private final StatePropertiesPredicate state;
      private final LocationPredicate location;
      private final ItemPredicate item;

      public TriggerInstance(EntityPredicate.Composite var1, @Nullable Block var2, StatePropertiesPredicate var3, LocationPredicate var4, ItemPredicate var5) {
         super(PlacedBlockTrigger.ID, var1);
         this.block = var2;
         this.state = var3;
         this.location = var4;
         this.item = var5;
      }

      public static PlacedBlockTrigger.TriggerInstance placedBlock(Block var0) {
         return new PlacedBlockTrigger.TriggerInstance(
            EntityPredicate.Composite.ANY, var0, StatePropertiesPredicate.ANY, LocationPredicate.ANY, ItemPredicate.ANY
         );
      }

      public boolean matches(BlockState var1, BlockPos var2, ServerLevel var3, ItemStack var4) {
         if (this.block != null && !var1.is(this.block)) {
            return false;
         } else if (!this.state.matches(var1)) {
            return false;
         } else if (!this.location.matches(var3, (double)var2.getX(), (double)var2.getY(), (double)var2.getZ())) {
            return false;
         } else {
            return this.item.matches(var4);
         }
      }

      @Override
      public JsonObject serializeToJson(SerializationContext var1) {
         JsonObject var2 = super.serializeToJson(var1);
         if (this.block != null) {
            var2.addProperty("block", Registry.BLOCK.getKey(this.block).toString());
         }

         var2.add("state", this.state.serializeToJson());
         var2.add("location", this.location.serializeToJson());
         var2.add("item", this.item.serializeToJson());
         return var2;
      }
   }
}
