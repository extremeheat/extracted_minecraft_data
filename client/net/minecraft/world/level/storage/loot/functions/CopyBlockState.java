package net.minecraft.world.level.storage.loot.functions;

import com.google.common.collect.ImmutableSet;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import net.minecraft.core.Holder;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.util.context.ContextKey;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.BlockItemStateProperties;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;

public class CopyBlockState extends LootItemConditionalFunction {
   public static final MapCodec<CopyBlockState> CODEC = RecordCodecBuilder.mapCodec((var0) -> {
      return commonFields(var0).and(var0.group(BuiltInRegistries.BLOCK.holderByNameCodec().fieldOf("block").forGetter((var0x) -> {
         return var0x.block;
      }), Codec.STRING.listOf().fieldOf("properties").forGetter((var0x) -> {
         return var0x.properties.stream().map(Property::getName).toList();
      }))).apply(var0, CopyBlockState::new);
   });
   private final Holder<Block> block;
   private final Set<Property<?>> properties;

   CopyBlockState(List<LootItemCondition> var1, Holder<Block> var2, Set<Property<?>> var3) {
      super(var1);
      this.block = var2;
      this.properties = var3;
   }

   private CopyBlockState(List<LootItemCondition> var1, Holder<Block> var2, List<String> var3) {
      Stream var10003 = var3.stream();
      StateDefinition var10004 = ((Block)var2.value()).getStateDefinition();
      Objects.requireNonNull(var10004);
      this(var1, var2, (Set)var10003.map(var10004::getProperty).filter(Objects::nonNull).collect(Collectors.toSet()));
   }

   public LootItemFunctionType<CopyBlockState> getType() {
      return LootItemFunctions.COPY_STATE;
   }

   public Set<ContextKey<?>> getReferencedContextParams() {
      return Set.of(LootContextParams.BLOCK_STATE);
   }

   protected ItemStack run(ItemStack var1, LootContext var2) {
      BlockState var3 = (BlockState)var2.getOptionalParameter(LootContextParams.BLOCK_STATE);
      if (var3 != null) {
         var1.update(DataComponents.BLOCK_STATE, BlockItemStateProperties.EMPTY, (var2x) -> {
            Iterator var3x = this.properties.iterator();

            while(var3x.hasNext()) {
               Property var4 = (Property)var3x.next();
               if (var3.hasProperty(var4)) {
                  var2x = var2x.with(var4, var3);
               }
            }

            return var2x;
         });
      }

      return var1;
   }

   public static Builder copyState(Block var0) {
      return new Builder(var0);
   }

   public static class Builder extends LootItemConditionalFunction.Builder<Builder> {
      private final Holder<Block> block;
      private final ImmutableSet.Builder<Property<?>> properties = ImmutableSet.builder();

      Builder(Block var1) {
         super();
         this.block = var1.builtInRegistryHolder();
      }

      public Builder copy(Property<?> var1) {
         if (!((Block)this.block.value()).getStateDefinition().getProperties().contains(var1)) {
            String var10002 = String.valueOf(var1);
            throw new IllegalStateException("Property " + var10002 + " is not present on block " + String.valueOf(this.block));
         } else {
            this.properties.add(var1);
            return this;
         }
      }

      protected Builder getThis() {
         return this;
      }

      public LootItemFunction build() {
         return new CopyBlockState(this.getConditions(), this.block, this.properties.build());
      }

      // $FF: synthetic method
      protected LootItemConditionalFunction.Builder getThis() {
         return this.getThis();
      }
   }
}
