package net.minecraft.world.level.storage.loot.predicates;

import com.mojang.serialization.DataResult;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.mojang.serialization.codecs.RecordCodecBuilder.Instance;
import java.util.Optional;
import java.util.Set;
import net.minecraft.advancements.critereon.StatePropertiesPredicate;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.parameters.LootContextParam;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;

public record LootItemBlockStatePropertyCondition(Holder<Block> b, Optional<StatePropertiesPredicate> c) implements LootItemCondition {
   private final Holder<Block> block;
   private final Optional<StatePropertiesPredicate> properties;
   public static final MapCodec<LootItemBlockStatePropertyCondition> CODEC = RecordCodecBuilder.mapCodec(
         var0 -> var0.group(
                  BuiltInRegistries.BLOCK.holderByNameCodec().fieldOf("block").forGetter(LootItemBlockStatePropertyCondition::block),
                  StatePropertiesPredicate.CODEC.optionalFieldOf("properties").forGetter(LootItemBlockStatePropertyCondition::properties)
               )
               .apply(var0, LootItemBlockStatePropertyCondition::new)
      )
      .validate(LootItemBlockStatePropertyCondition::validate);

   public LootItemBlockStatePropertyCondition(Holder<Block> var1, Optional<StatePropertiesPredicate> var2) {
      super();
      this.block = var1;
      this.properties = var2;
   }

   private static DataResult<LootItemBlockStatePropertyCondition> validate(LootItemBlockStatePropertyCondition var0) {
      return (DataResult<LootItemBlockStatePropertyCondition>)var0.properties()
         .flatMap(var1 -> var1.checkState(var0.block().value().getStateDefinition()))
         .map(var1 -> DataResult.error(() -> "Block " + var0.block() + " has no property" + var1))
         .orElse(DataResult.success(var0));
   }

   @Override
   public LootItemConditionType getType() {
      return LootItemConditions.BLOCK_STATE_PROPERTY;
   }

   @Override
   public Set<LootContextParam<?>> getReferencedContextParams() {
      return Set.of(LootContextParams.BLOCK_STATE);
   }

   public boolean test(LootContext var1) {
      BlockState var2 = var1.getParamOrNull(LootContextParams.BLOCK_STATE);
      return var2 != null && var2.is(this.block) && (this.properties.isEmpty() || ((StatePropertiesPredicate)this.properties.get()).matches(var2));
   }

   public static LootItemBlockStatePropertyCondition.Builder hasBlockStateProperties(Block var0) {
      return new LootItemBlockStatePropertyCondition.Builder(var0);
   }

   public static class Builder implements LootItemCondition.Builder {
      private final Holder<Block> block;
      private Optional<StatePropertiesPredicate> properties = Optional.empty();

      public Builder(Block var1) {
         super();
         this.block = var1.builtInRegistryHolder();
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
}
