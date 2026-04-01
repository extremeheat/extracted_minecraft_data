package net.minecraft.world.level.block.grower;

import java.util.List;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.livingblock.LivingBlock;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.TreeGrowingBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;

public final class LivingBlockTreeGrower extends TreeGrower {
   private final TreeGrowingBlock sapling;

   public LivingBlockTreeGrower(final TreeGrowingBlock sapling) {
      TreeGrower delegate = sapling.treeGrower();
      super("living_block_" + delegate.name(), delegate.secondaryChance(), delegate.megaTree(), delegate.secondaryMegaTree(), delegate.tree(), delegate.secondaryTree(), delegate.flowers(), delegate.secondaryFlowers());
      this.sapling = sapling;
   }

   protected boolean isTwoByTwoSapling(final BlockState state, final BlockGetter level, final BlockPos pos, final int ox, final int oz) {
      if (level instanceof ServerLevel serverLevel) {
         return this.getSaplingsAround(pos, serverLevel).size() >= 4;
      } else {
         return super.isTwoByTwoSapling(state, level, pos, ox, oz);
      }
   }

   private List<LivingBlock> getSaplingsAround(final BlockPos pos, final ServerLevel serverLevel) {
      return serverLevel.getEntities(EntityType.LIVING_BLOCK, (new AABB(pos)).inflate(3.0), (livingBlock) -> livingBlock.getBlockState().is((Block)this.sapling));
   }

   protected boolean hasFlowers(final LevelAccessor level, final BlockPos pos) {
      if (level instanceof ServerLevel serverLevel) {
         return !this.getFlowersAround(pos, serverLevel).isEmpty();
      } else {
         return super.hasFlowers(level, pos);
      }
   }

   private List<LivingBlock> getFlowersAround(final BlockPos pos, final ServerLevel serverLevel) {
      return serverLevel.getEntities(EntityType.LIVING_BLOCK, (new AABB(pos)).inflate(4.0), (livingBlock) -> livingBlock.getBlockState().is(BlockTags.FLOWERS));
   }

   public TreeGrowingBlock sapling() {
      return this.sapling;
   }
}
