package net.minecraft.world.level.storage.loot.functions;

import com.google.common.collect.ImmutableSet;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;
import net.minecraft.core.Holder;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.BlockItemStateProperties;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.parameters.LootContextParam;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;

public class CopyBlockState extends LootItemConditionalFunction {
   public static final MapCodec<CopyBlockState> CODEC = RecordCodecBuilder.mapCodec(
      var0 -> commonFields(var0)
            .and(
               var0.group(
                  BuiltInRegistries.BLOCK.holderByNameCodec().fieldOf("block").forGetter(var0x -> var0x.block),
                  Codec.STRING.listOf().fieldOf("properties").forGetter(var0x -> var0x.properties.stream().map(Property::getName).toList())
               )
            )
            .apply(var0, CopyBlockState::new)
   );
   private final Holder<Block> block;
   private final Set<Property<?>> properties;

   CopyBlockState(List<LootItemCondition> var1, Holder<Block> var2, Set<Property<?>> var3) {
      super(var1);
      this.block = var2;
      this.properties = var3;
   }

   private CopyBlockState(List<LootItemCondition> var1, Holder<Block> var2, List<String> var3) {
      this(var1, var2, var3.stream().map(((Block)var2.value()).getStateDefinition()::getProperty).filter(Objects::nonNull).collect(Collectors.toSet()));
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
         var1.update(DataComponents.BLOCK_STATE, BlockItemStateProperties.EMPTY, var2x -> {
            for (Property var4 : this.properties) {
               if (var3.hasProperty(var4)) {
                  var2x = var2x.with(var4, var3);
               }
            }

            return var2x;
         });
      }

      return var1;
   }

   public static CopyBlockState.Builder copyState(Block var0) {
      return new CopyBlockState.Builder(var0);
   }

   public static class Builder extends LootItemConditionalFunction.Builder<CopyBlockState.Builder> {
      private final Holder<Block> block;
      private final com.google.common.collect.ImmutableSet.Builder<Property<?>> properties = ImmutableSet.builder();

      Builder(Block var1) {
         super();
         this.block = var1.builtInRegistryHolder();
      }

      public CopyBlockState.Builder copy(Property<?> var1) {
         if (!this.block.value().getStateDefinition().getProperties().contains(var1)) {
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
         return new CopyBlockState(this.getConditions(), this.block, this.properties.build());
      }
   }
}
