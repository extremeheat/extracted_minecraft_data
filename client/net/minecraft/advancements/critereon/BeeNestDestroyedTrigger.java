package net.minecraft.advancements.critereon;

import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;
import javax.annotation.Nullable;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;

public class BeeNestDestroyedTrigger extends SimpleCriterionTrigger<BeeNestDestroyedTrigger.TriggerInstance> {
   static final ResourceLocation ID = new ResourceLocation("bee_nest_destroyed");

   public BeeNestDestroyedTrigger() {
      super();
   }

   @Override
   public ResourceLocation getId() {
      return ID;
   }

   public BeeNestDestroyedTrigger.TriggerInstance createInstance(JsonObject var1, ContextAwarePredicate var2, DeserializationContext var3) {
      Block var4 = deserializeBlock(var1);
      ItemPredicate var5 = ItemPredicate.fromJson(var1.get("item"));
      MinMaxBounds.Ints var6 = MinMaxBounds.Ints.fromJson(var1.get("num_bees_inside"));
      return new BeeNestDestroyedTrigger.TriggerInstance(var2, var4, var5, var6);
   }

   @Nullable
   private static Block deserializeBlock(JsonObject var0) {
      if (var0.has("block")) {
         ResourceLocation var1 = new ResourceLocation(GsonHelper.getAsString(var0, "block"));
         return BuiltInRegistries.BLOCK.getOptional(var1).orElseThrow(() -> new JsonSyntaxException("Unknown block type '" + var1 + "'"));
      } else {
         return null;
      }
   }

   public void trigger(ServerPlayer var1, BlockState var2, ItemStack var3, int var4) {
      this.trigger(var1, var3x -> var3x.matches(var2, var3, var4));
   }

   public static class TriggerInstance extends AbstractCriterionTriggerInstance {
      @Nullable
      private final Block block;
      private final ItemPredicate item;
      private final MinMaxBounds.Ints numBees;

      public TriggerInstance(ContextAwarePredicate var1, @Nullable Block var2, ItemPredicate var3, MinMaxBounds.Ints var4) {
         super(BeeNestDestroyedTrigger.ID, var1);
         this.block = var2;
         this.item = var3;
         this.numBees = var4;
      }

      public static BeeNestDestroyedTrigger.TriggerInstance destroyedBeeNest(Block var0, ItemPredicate.Builder var1, MinMaxBounds.Ints var2) {
         return new BeeNestDestroyedTrigger.TriggerInstance(ContextAwarePredicate.ANY, var0, var1.build(), var2);
      }

      public boolean matches(BlockState var1, ItemStack var2, int var3) {
         if (this.block != null && !var1.is(this.block)) {
            return false;
         } else {
            return !this.item.matches(var2) ? false : this.numBees.matches(var3);
         }
      }

      @Override
      public JsonObject serializeToJson(SerializationContext var1) {
         JsonObject var2 = super.serializeToJson(var1);
         if (this.block != null) {
            var2.addProperty("block", BuiltInRegistries.BLOCK.getKey(this.block).toString());
         }

         var2.add("item", this.item.serializeToJson());
         var2.add("num_bees_inside", this.numBees.serializeToJson());
         return var2;
      }
   }
}
