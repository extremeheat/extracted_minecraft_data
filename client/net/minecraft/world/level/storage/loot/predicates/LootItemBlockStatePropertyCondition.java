package net.minecraft.world.level.storage.loot.predicates;

import com.mojang.serialization.DataResult;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
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

public record LootItemBlockStatePropertyCondition(Holder<Block> block, Optional<StatePropertiesPredicate> properties) implements LootItemCondition {
   public static final MapCodec<LootItemBlockStatePropertyCondition> CODEC = RecordCodecBuilder.mapCodec((var0) -> {
      return var0.group(BuiltInRegistries.BLOCK.holderByNameCodec().fieldOf("block").forGetter(LootItemBlockStatePropertyCondition::block), StatePropertiesPredicate.CODEC.optionalFieldOf("properties").forGetter(LootItemBlockStatePropertyCondition::properties)).apply(var0, LootItemBlockStatePropertyCondition::new);
   }).validate(LootItemBlockStatePropertyCondition::validate);

   public LootItemBlockStatePropertyCondition(Holder<Block> block, Optional<StatePropertiesPredicate> properties) {
      super();
      this.block = block;
      this.properties = properties;
   }

   private static DataResult<LootItemBlockStatePropertyCondition> validate(LootItemBlockStatePropertyCondition var0) {
      return (DataResult)var0.properties().flatMap((var1) -> {
         return var1.checkState(((Block)var0.block().value()).getStateDefinition());
      }).map((var1) -> {
         return DataResult.error(() -> {
            String var10000 = String.valueOf(var0.block());
            return "Block " + var10000 + " has no property" + var1;
         });
      }).orElse(DataResult.success(var0));
   }

   public LootItemConditionType getType() {
      return LootItemConditions.BLOCK_STATE_PROPERTY;
   }

   public Set<LootContextParam<?>> getReferencedContextParams() {
      return Set.of(LootContextParams.BLOCK_STATE);
   }

   public boolean test(LootContext var1) {
      BlockState var2 = (BlockState)var1.getParamOrNull(LootContextParams.BLOCK_STATE);
      return var2 != null && var2.is(this.block) && (this.properties.isEmpty() || ((StatePropertiesPredicate)this.properties.get()).matches(var2));
   }

   public static Builder hasBlockStateProperties(Block var0) {
      return new Builder(var0);
   }

   public Holder<Block> block() {
      return this.block;
   }

   public Optional<StatePropertiesPredicate> properties() {
      return this.properties;
   }

   // $FF: synthetic method
   public boolean test(final Object var1) {
      return this.test((LootContext)var1);
   }

   public static class Builder implements LootItemCondition.Builder {
      private final Holder<Block> block;
      private Optional<StatePropertiesPredicate> properties = Optional.empty();

      public Builder(Block var1) {
         super();
         this.block = var1.builtInRegistryHolder();
      }

      public Builder setProperties(StatePropertiesPredicate.Builder var1) {
         this.properties = var1.build();
         return this;
      }

      public LootItemCondition build() {
         return new LootItemBlockStatePropertyCondition(this.block, this.properties);
      }
   }
}
