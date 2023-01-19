package net.minecraft.advancements.critereon;

import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;
import javax.annotation.Nullable;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;

public class SlideDownBlockTrigger extends SimpleCriterionTrigger<SlideDownBlockTrigger.TriggerInstance> {
   static final ResourceLocation ID = new ResourceLocation("slide_down_block");

   public SlideDownBlockTrigger() {
      super();
   }

   @Override
   public ResourceLocation getId() {
      return ID;
   }

   public SlideDownBlockTrigger.TriggerInstance createInstance(JsonObject var1, EntityPredicate.Composite var2, DeserializationContext var3) {
      Block var4 = deserializeBlock(var1);
      StatePropertiesPredicate var5 = StatePropertiesPredicate.fromJson(var1.get("state"));
      if (var4 != null) {
         var5.checkState(var4.getStateDefinition(), var1x -> {
            throw new JsonSyntaxException("Block " + var4 + " has no property " + var1x);
         });
      }

      return new SlideDownBlockTrigger.TriggerInstance(var2, var4, var5);
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

   public void trigger(ServerPlayer var1, BlockState var2) {
      this.trigger(var1, var1x -> var1x.matches(var2));
   }

   public static class TriggerInstance extends AbstractCriterionTriggerInstance {
      @Nullable
      private final Block block;
      private final StatePropertiesPredicate state;

      public TriggerInstance(EntityPredicate.Composite var1, @Nullable Block var2, StatePropertiesPredicate var3) {
         super(SlideDownBlockTrigger.ID, var1);
         this.block = var2;
         this.state = var3;
      }

      public static SlideDownBlockTrigger.TriggerInstance slidesDownBlock(Block var0) {
         return new SlideDownBlockTrigger.TriggerInstance(EntityPredicate.Composite.ANY, var0, StatePropertiesPredicate.ANY);
      }

      @Override
      public JsonObject serializeToJson(SerializationContext var1) {
         JsonObject var2 = super.serializeToJson(var1);
         if (this.block != null) {
            var2.addProperty("block", BuiltInRegistries.BLOCK.getKey(this.block).toString());
         }

         var2.add("state", this.state.serializeToJson());
         return var2;
      }

      public boolean matches(BlockState var1) {
         if (this.block != null && !var1.is(this.block)) {
            return false;
         } else {
            return this.state.matches(var1);
         }
      }
   }
}
