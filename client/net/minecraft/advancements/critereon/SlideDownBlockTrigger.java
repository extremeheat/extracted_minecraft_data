package net.minecraft.advancements.critereon;

import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;
import java.util.Optional;
import javax.annotation.Nullable;
import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.advancements.Criterion;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;

public class SlideDownBlockTrigger extends SimpleCriterionTrigger<SlideDownBlockTrigger.TriggerInstance> {
   public SlideDownBlockTrigger() {
      super();
   }

   public SlideDownBlockTrigger.TriggerInstance createInstance(JsonObject var1, Optional<ContextAwarePredicate> var2, DeserializationContext var3) {
      Block var4 = deserializeBlock(var1);
      Optional var5 = StatePropertiesPredicate.fromJson(var1.get("state"));
      if (var4 != null) {
         var5.ifPresent(var1x -> var1x.checkState(var4.getStateDefinition(), var1xx -> {
               throw new JsonSyntaxException("Block " + var4 + " has no property " + var1xx);
            }));
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
      private final Optional<StatePropertiesPredicate> state;

      public TriggerInstance(Optional<ContextAwarePredicate> var1, @Nullable Block var2, Optional<StatePropertiesPredicate> var3) {
         super(var1);
         this.block = var2;
         this.state = var3;
      }

      public static Criterion<SlideDownBlockTrigger.TriggerInstance> slidesDownBlock(Block var0) {
         return CriteriaTriggers.HONEY_BLOCK_SLIDE.createCriterion(new SlideDownBlockTrigger.TriggerInstance(Optional.empty(), var0, Optional.empty()));
      }

      @Override
      public JsonObject serializeToJson() {
         JsonObject var1 = super.serializeToJson();
         if (this.block != null) {
            var1.addProperty("block", BuiltInRegistries.BLOCK.getKey(this.block).toString());
         }

         this.state.ifPresent(var1x -> var1.add("state", var1x.serializeToJson()));
         return var1;
      }

      public boolean matches(BlockState var1) {
         if (this.block != null && !var1.is(this.block)) {
            return false;
         } else {
            return !this.state.isPresent() || this.state.get().matches(var1);
         }
      }
   }
}
