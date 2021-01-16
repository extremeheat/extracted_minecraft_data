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
   private final Block block;
   private final StatePropertiesPredicate properties;

   private LootItemBlockStatePropertyCondition(Block var1, StatePropertiesPredicate var2) {
      super();
      this.block = var1;
      this.properties = var2;
   }

   public LootItemConditionType getType() {
      return LootItemConditions.BLOCK_STATE_PROPERTY;
   }

   public Set<LootContextParam<?>> getReferencedContextParams() {
      return ImmutableSet.of(LootContextParams.BLOCK_STATE);
   }

   public boolean test(LootContext var1) {
      BlockState var2 = (BlockState)var1.getParamOrNull(LootContextParams.BLOCK_STATE);
      return var2 != null && this.block == var2.getBlock() && this.properties.matches(var2);
   }

   public static LootItemBlockStatePropertyCondition.Builder hasBlockStateProperties(Block var0) {
      return new LootItemBlockStatePropertyCondition.Builder(var0);
   }

   // $FF: synthetic method
   public boolean test(Object var1) {
      return this.test((LootContext)var1);
   }

   // $FF: synthetic method
   LootItemBlockStatePropertyCondition(Block var1, StatePropertiesPredicate var2, Object var3) {
      this(var1, var2);
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
         Block var4 = (Block)Registry.BLOCK.getOptional(var3).orElseThrow(() -> {
            return new IllegalArgumentException("Can't find block " + var3);
         });
         StatePropertiesPredicate var5 = StatePropertiesPredicate.fromJson(var1.get("properties"));
         var5.checkState(var4.getStateDefinition(), (var1x) -> {
            throw new JsonSyntaxException("Block " + var4 + " has no property " + var1x);
         });
         return new LootItemBlockStatePropertyCondition(var4, var5);
      }

      // $FF: synthetic method
      public Object deserialize(JsonObject var1, JsonDeserializationContext var2) {
         return this.deserialize(var1, var2);
      }
   }

   public static class Builder implements LootItemCondition.Builder {
      private final Block block;
      private StatePropertiesPredicate properties;

      public Builder(Block var1) {
         super();
         this.properties = StatePropertiesPredicate.ANY;
         this.block = var1;
      }

      public LootItemBlockStatePropertyCondition.Builder setProperties(StatePropertiesPredicate.Builder var1) {
         this.properties = var1.build();
         return this;
      }

      public LootItemCondition build() {
         return new LootItemBlockStatePropertyCondition(this.block, this.properties);
      }
   }
}
