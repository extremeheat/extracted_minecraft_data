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
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;

public class EnterBlockTrigger extends SimpleCriterionTrigger {
   private static final ResourceLocation ID = new ResourceLocation("enter_block");

   public ResourceLocation getId() {
      return ID;
   }

   public EnterBlockTrigger.TriggerInstance createInstance(JsonObject var1, JsonDeserializationContext var2) {
      Block var3 = deserializeBlock(var1);
      StatePropertiesPredicate var4 = StatePropertiesPredicate.fromJson(var1.get("state"));
      if (var3 != null) {
         var4.checkState(var3.getStateDefinition(), (var1x) -> {
            throw new JsonSyntaxException("Block " + var3 + " has no property " + var1x);
         });
      }

      return new EnterBlockTrigger.TriggerInstance(var3, var4);
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

   public void trigger(ServerPlayer var1, BlockState var2) {
      this.trigger(var1.getAdvancements(), (var1x) -> {
         return var1x.matches(var2);
      });
   }

   // $FF: synthetic method
   public CriterionTriggerInstance createInstance(JsonObject var1, JsonDeserializationContext var2) {
      return this.createInstance(var1, var2);
   }

   public static class TriggerInstance extends AbstractCriterionTriggerInstance {
      private final Block block;
      private final StatePropertiesPredicate state;

      public TriggerInstance(@Nullable Block var1, StatePropertiesPredicate var2) {
         super(EnterBlockTrigger.ID);
         this.block = var1;
         this.state = var2;
      }

      public static EnterBlockTrigger.TriggerInstance entersBlock(Block var0) {
         return new EnterBlockTrigger.TriggerInstance(var0, StatePropertiesPredicate.ANY);
      }

      public JsonElement serializeToJson() {
         JsonObject var1 = new JsonObject();
         if (this.block != null) {
            var1.addProperty("block", Registry.BLOCK.getKey(this.block).toString());
         }

         var1.add("state", this.state.serializeToJson());
         return var1;
      }

      public boolean matches(BlockState var1) {
         if (this.block != null && var1.getBlock() != this.block) {
            return false;
         } else {
            return this.state.matches(var1);
         }
      }
   }
}
