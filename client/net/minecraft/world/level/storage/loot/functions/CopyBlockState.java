package net.minecraft.world.level.storage.loot.functions;

import com.google.common.collect.ImmutableSet;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
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
   public static final MapCodec<CopyBlockState> MAP_CODEC = RecordCodecBuilder.mapCodec((i) -> commonFields(i).and(i.group(BuiltInRegistries.BLOCK.holderByNameCodec().fieldOf("block").forGetter((f) -> f.block), Codec.STRING.listOf().fieldOf("properties").forGetter((f) -> f.properties.stream().map(Property::getName).toList()))).apply(i, CopyBlockState::new));
   private final Holder<Block> block;
   private final Set<Property<?>> properties;

   private CopyBlockState(final Optional<Holder<LootItemCondition>> condition, final Holder<Block> block, final Set<Property<?>> properties) {
      super(condition);
      this.block = block;
      this.properties = properties;
   }

   private CopyBlockState(final Optional<Holder<LootItemCondition>> condition, final Holder<Block> block, final List<String> propertyNames) {
      Stream var10003 = propertyNames.stream();
      StateDefinition var10004 = (block.value()).getStateDefinition();
      Objects.requireNonNull(var10004);
      this(condition, block, (Set)var10003.map(var10004::getProperty).filter(Objects::nonNull).collect(Collectors.toSet()));
   }

   public MapCodec<CopyBlockState> codec() {
      return MAP_CODEC;
   }

   public Set<ContextKey<?>> getReferencedContextParams() {
      return Set.of(LootContextParams.BLOCK_STATE);
   }

   protected ItemStack run(final ItemStack itemStack, final LootContext context) {
      BlockState state = (BlockState)context.getOptionalParameter(LootContextParams.BLOCK_STATE);
      if (state != null) {
         itemStack.update(DataComponents.BLOCK_STATE, BlockItemStateProperties.EMPTY, (itemState) -> {
            for(Property<?> property : this.properties) {
               if (state.hasProperty(property)) {
                  itemState = itemState.with(property, state);
               }
            }

            return itemState;
         });
      }

      return itemStack;
   }

   public static Builder copyState(final Block block) {
      return new Builder(block);
   }

   public static class Builder extends LootItemConditionalFunction.Builder<Builder> {
      private final Holder<Block> block;
      private final ImmutableSet.Builder<Property<?>> properties = ImmutableSet.builder();

      private Builder(final Block block) {
         super();
         this.block = block.builtInRegistryHolder();
      }

      public Builder copy(final Property<?> property) {
         if (!((Block)this.block.value()).getStateDefinition().getProperties().contains(property)) {
            String var10002 = String.valueOf(property);
            throw new IllegalStateException("Property " + var10002 + " is not present on block " + String.valueOf(this.block));
         } else {
            this.properties.add(property);
            return this;
         }
      }

      protected Builder getThis() {
         return this;
      }

      public LootItemFunction build() {
         return new CopyBlockState(this.getCondition(), this.block, this.properties.build());
      }
   }
}
