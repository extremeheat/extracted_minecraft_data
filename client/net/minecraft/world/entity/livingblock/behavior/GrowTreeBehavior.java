package net.minecraft.world.entity.livingblock.behavior;

import java.util.List;
import java.util.function.Supplier;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.livingblock.LivingBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.TreeGrowingBlock;
import net.minecraft.world.level.block.grower.LivingBlockTreeGrower;
import net.minecraft.world.level.block.state.BlockState;

public class GrowTreeBehavior implements LivingBlockBehavior {
   private static final int REEVALUATION_TICKS = 10;
   public static final double CHECK_RADIUS = 3.0;
   private final LivingBlockTreeGrower treeGrower;
   private int lastTriggeredTick;

   private GrowTreeBehavior(final TreeGrowingBlock sapling) {
      super();
      this.treeGrower = new LivingBlockTreeGrower(sapling);
   }

   public boolean canStartUsing(final LivingBlock entity) {
      return this.lastTriggeredTick + 10 < entity.tickCount;
   }

   public boolean tick(final LivingBlock entity, final ServerLevel level, final int tickCount) {
      this.lastTriggeredTick = tickCount;
      TreeGrowingBlock sapling = this.treeGrower.sapling();
      List<? extends LivingBlock> livingSaplings = level.getEntities(EntityType.LIVING_BLOCK, entity.getBoundingBox().inflate(3.0), (livingBlock) -> livingBlock.getBlockState().is((Block)sapling));
      if (!livingSaplings.isEmpty()) {
         LivingBlock livingSapling = (LivingBlock)livingSaplings.getFirst();
         BlockPos pos = livingSapling.blockPosition();
         BlockState state = livingSapling.getBlockState();
         if (sapling.isValidBonemealTarget(level, pos, state)) {
            switch (this.treeGrower.growTree(level, level.getChunkSource().getGenerator(), pos, state, level.getRandom())) {
               case NORMAL:
                  entity.discard();
                  livingSapling.discard();
                  break;
               case MEGA:
                  entity.discard();
                  livingSaplings.stream().limit(4L).forEach(Entity::discard);
                  break;
               case NONE:
                  return false;
            }

            return true;
         }
      }

      return false;
   }

   public static LivingBlockBehaviorType growTree(final Block block) {
      if (block instanceof TreeGrowingBlock sapling) {
         return LivingBlockBehaviorType.behaviorType((Supplier)(() -> new GrowTreeBehavior(sapling)));
      } else {
         throw new IllegalArgumentException("A tree must be grown from a sapling");
      }
   }
}
