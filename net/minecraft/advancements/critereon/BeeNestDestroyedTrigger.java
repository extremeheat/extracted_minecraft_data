package net.minecraft.advancements.critereon;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;
import javax.annotation.Nullable;
import net.minecraft.advancements.CriterionTriggerInstance;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;

public class BeeNestDestroyedTrigger extends SimpleCriterionTrigger {
   private static final ResourceLocation ID = new ResourceLocation("bee_nest_destroyed");

   public ResourceLocation getId() {
      return ID;
   }

   public BeeNestDestroyedTrigger.TriggerInstance createInstance(JsonObject var1, JsonDeserializationContext var2) {
      Block var3 = deserializeBlock(var1);
      ItemPredicate var4 = ItemPredicate.fromJson(var1.get("item"));
      MinMaxBounds.Ints var5 = MinMaxBounds.Ints.fromJson(var1.get("num_bees_inside"));
      return new BeeNestDestroyedTrigger.TriggerInstance(var3, var4, var5);
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

   public void trigger(ServerPlayer var1, Block var2, ItemStack var3, int var4) {
      this.trigger(var1.getAdvancements(), (var3x) -> {
         return var3x.matches(var2, var3, var4);
      });
   }

   // $FF: synthetic method
   public CriterionTriggerInstance createInstance(JsonObject var1, JsonDeserializationContext var2) {
      return this.createInstance(var1, var2);
   }

   public static class TriggerInstance extends AbstractCriterionTriggerInstance {
      private final Block block;
      private final ItemPredicate item;
      private final MinMaxBounds.Ints numBees;

      public TriggerInstance(Block var1, ItemPredicate var2, MinMaxBounds.Ints var3) {
         super(BeeNestDestroyedTrigger.ID);
         this.block = var1;
         this.item = var2;
         this.numBees = var3;
      }

      public static BeeNestDestroyedTrigger.TriggerInstance destroyedBeeNest(Block var0, ItemPredicate.Builder var1, MinMaxBounds.Ints var2) {
         return new BeeNestDestroyedTrigger.TriggerInstance(var0, var1.build(), var2);
      }

      public boolean matches(Block var1, ItemStack var2, int var3) {
         if (this.block != null && var1 != this.block) {
            return false;
         } else {
            return !this.item.matches(var2) ? false : this.numBees.matches(var3);
         }
      }

      public JsonElement serializeToJson() {
         JsonObject var1 = new JsonObject();
         if (this.block != null) {
            var1.addProperty("block", Registry.BLOCK.getKey(this.block).toString());
         }

         var1.add("item", this.item.serializeToJson());
         var1.add("num_bees_inside", this.numBees.serializeToJson());
         return var1;
      }
   }
}
