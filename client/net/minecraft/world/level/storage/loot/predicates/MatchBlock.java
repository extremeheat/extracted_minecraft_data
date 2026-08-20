package net.minecraft.world.level.storage.loot.predicates;

import com.mojang.serialization.MapCodec;
import java.util.Set;
import net.minecraft.advancements.predicates.BlockPredicate;
import net.minecraft.advancements.predicates.StatePropertiesPredicate;
import net.minecraft.core.HolderGetter;
import net.minecraft.util.context.ContextKey;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;

public record MatchBlock(BlockPredicate predicate) implements LootItemCondition {
   public static final MapCodec<MatchBlock> MAP_CODEC;

   public MatchBlock {
      super();
   }

   public MapCodec<MatchBlock> codec() {
      return MAP_CODEC;
   }

   public Set<ContextKey<?>> getReferencedContextParams() {
      return Set.of(LootContextParams.BLOCK_STATE, LootContextParams.BLOCK_ENTITY);
   }

   public boolean test(final LootContext context) {
      BlockState blockState = (BlockState)context.getOptionalParameter(LootContextParams.BLOCK_STATE);
      BlockEntity blockEntity = (BlockEntity)context.getOptionalParameter(LootContextParams.BLOCK_ENTITY);
      return blockState != null && this.predicate.matchesState(blockState) && this.predicate.matchesBlockEntity(context.getLevel(), blockEntity);
   }

   public static LootItemCondition.Builder blockMatches(final BlockPredicate.Builder predicate) {
      return () -> new MatchBlock(predicate.build());
   }

   public static LootItemCondition.Builder blockMatches(final HolderGetter<Block> lookup, final Block block) {
      return blockMatches(BlockPredicate.Builder.block().of(lookup, block));
   }

   public static LootItemCondition.Builder blockMatches(final HolderGetter<Block> lookup, final Block block, final StatePropertiesPredicate.Builder properties) {
      return blockMatches(BlockPredicate.Builder.block().of(lookup, block).setProperties(properties));
   }

   static {
      MAP_CODEC = BlockPredicate.MAP_CODEC.xmap(MatchBlock::new, MatchBlock::predicate);
   }
}
