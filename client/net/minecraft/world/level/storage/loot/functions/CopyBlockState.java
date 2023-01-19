package net.minecraft.world.level.storage.loot.functions;

import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Sets;
import com.google.gson.JsonArray;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import java.util.HashSet;
import java.util.Set;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.parameters.LootContextParam;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;

public class CopyBlockState extends LootItemConditionalFunction {
   final Block block;
   final Set<Property<?>> properties;

   CopyBlockState(LootItemCondition[] var1, Block var2, Set<Property<?>> var3) {
      super(var1);
      this.block = var2;
      this.properties = var3;
   }

   @Override
   public LootItemFunctionType getType() {
      return LootItemFunctions.COPY_STATE;
   }

   @Override
   public Set<LootContextParam<?>> getReferencedContextParams() {
      return ImmutableSet.of(LootContextParams.BLOCK_STATE);
   }

   @Override
   protected ItemStack run(ItemStack var1, LootContext var2) {
      BlockState var3 = var2.getParamOrNull(LootContextParams.BLOCK_STATE);
      if (var3 != null) {
         CompoundTag var4 = var1.getOrCreateTag();
         CompoundTag var5;
         if (var4.contains("BlockStateTag", 10)) {
            var5 = var4.getCompound("BlockStateTag");
         } else {
            var5 = new CompoundTag();
            var4.put("BlockStateTag", var5);
         }

         this.properties.stream().filter(var3::hasProperty).forEach(var2x -> var5.putString(var2x.getName(), serialize(var3, var2x)));
      }

      return var1;
   }

   public static CopyBlockState.Builder copyState(Block var0) {
      return new CopyBlockState.Builder(var0);
   }

   private static <T extends Comparable<T>> String serialize(BlockState var0, Property<T> var1) {
      Comparable var2 = var0.getValue(var1);
      return var1.getName((T)var2);
   }

   public static class Builder extends LootItemConditionalFunction.Builder<CopyBlockState.Builder> {
      private final Block block;
      private final Set<Property<?>> properties = Sets.newHashSet();

      Builder(Block var1) {
         super();
         this.block = var1;
      }

      public CopyBlockState.Builder copy(Property<?> var1) {
         if (!this.block.getStateDefinition().getProperties().contains(var1)) {
            throw new IllegalStateException("Property " + var1 + " is not present on block " + this.block);
         } else {
            this.properties.add(var1);
            return this;
         }
      }

      protected CopyBlockState.Builder getThis() {
         return this;
      }

      @Override
      public LootItemFunction build() {
         return new CopyBlockState(this.getConditions(), this.block, this.properties);
      }
   }

   public static class Serializer extends LootItemConditionalFunction.Serializer<CopyBlockState> {
      public Serializer() {
         super();
      }

      public void serialize(JsonObject var1, CopyBlockState var2, JsonSerializationContext var3) {
         super.serialize(var1, var2, var3);
         var1.addProperty("block", BuiltInRegistries.BLOCK.getKey(var2.block).toString());
         JsonArray var4 = new JsonArray();
         var2.properties.forEach(var1x -> var4.add(var1x.getName()));
         var1.add("properties", var4);
      }

      public CopyBlockState deserialize(JsonObject var1, JsonDeserializationContext var2, LootItemCondition[] var3) {
         ResourceLocation var4 = new ResourceLocation(GsonHelper.getAsString(var1, "block"));
         Block var5 = BuiltInRegistries.BLOCK.getOptional(var4).orElseThrow(() -> new IllegalArgumentException("Can't find block " + var4));
         StateDefinition var6 = var5.getStateDefinition();
         HashSet var7 = Sets.newHashSet();
         JsonArray var8 = GsonHelper.getAsJsonArray(var1, "properties", null);
         if (var8 != null) {
            var8.forEach(var2x -> var7.add(var6.getProperty(GsonHelper.convertToString(var2x, "property"))));
         }

         return new CopyBlockState(var3, var5, var7);
      }
   }
}
