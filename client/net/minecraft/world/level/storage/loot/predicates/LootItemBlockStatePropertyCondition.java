package net.minecraft.world.level.storage.loot.predicates;

import com.google.common.collect.ImmutableSet;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSyntaxException;
import java.util.Set;
import net.minecraft.advancements.critereon.StatePropertiesPredicate;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.parameters.LootContextParam;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;

public class LootItemBlockStatePropertyCondition implements LootItemCondition {
   final Block block;
   final StatePropertiesPredicate properties;

   LootItemBlockStatePropertyCondition(Block var1, StatePropertiesPredicate var2) {
      super();
      this.block = var1;
      this.properties = var2;
   }

   @Override
   public LootItemConditionType getType() {
      return LootItemConditions.BLOCK_STATE_PROPERTY;
   }

   @Override
   public Set<LootContextParam<?>> getReferencedContextParams() {
      return ImmutableSet.of(LootContextParams.BLOCK_STATE);
   }

   public boolean test(LootContext var1) {
      BlockState var2 = var1.getParamOrNull(LootContextParams.BLOCK_STATE);
      return var2 != null && var2.is(this.block) && this.properties.matches(var2);
   }

   public static LootItemBlockStatePropertyCondition.Builder hasBlockStateProperties(Block var0) {
      return new LootItemBlockStatePropertyCondition.Builder(var0);
   }

   public static class Builder implements LootItemCondition.Builder {
      private final Block block;
      private StatePropertiesPredicate properties = StatePropertiesPredicate.ANY;

      public Builder(Block var1) {
         super();
         this.block = var1;
      }

      public LootItemBlockStatePropertyCondition.Builder setProperties(StatePropertiesPredicate.Builder var1) {
         this.properties = var1.build();
         return this;
      }

      @Override
      public LootItemCondition build() {
         return new LootItemBlockStatePropertyCondition(this.block, this.properties);
      }
   }

   public static class Serializer implements net.minecraft.world.level.storage.loot.Serializer<LootItemBlockStatePropertyCondition> {
      public Serializer() {
         super();
      }

      public void serialize(JsonObject var1, LootItemBlockStatePropertyCondition var2, JsonSerializationContext var3) {
         var1.addProperty("block", Registry.BLOCK.getKey(var2.block).toString());
         var1.add("properties", var2.properties.serializeToJson());
      }

      public LootItemBlockStatePropertyCondition deserialize(JsonObject var1, JsonDeserializationContext var2) {
         ResourceLocation var3 = new ResourceLocation(GsonHelper.getAsString(var1, "block"));
         Block var4 = Registry.BLOCK.getOptional(var3).orElseThrow(() -> new IllegalArgumentException("Can't find block " + var3));
         StatePropertiesPredicate var5 = StatePropertiesPredicate.fromJson(var1.get("properties"));
         var5.checkState(var4.getStateDefinition(), var1x -> {
            throw new JsonSyntaxException("Block " + var4 + " has no property " + var1x);
         });
         return new LootItemBlockStatePropertyCondition(var4, var5);
      }
   }
}
