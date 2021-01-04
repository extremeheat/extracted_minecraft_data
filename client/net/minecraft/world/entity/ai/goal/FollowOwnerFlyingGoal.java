package net.minecraft.world.entity.ai.goal;

import net.minecraft.core.BlockPos;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.entity.TamableAnimal;
import net.minecraft.world.level.block.state.BlockState;

public class FollowOwnerFlyingGoal extends FollowOwnerGoal {
   public FollowOwnerFlyingGoal(TamableAnimal var1, double var2, float var4, float var5) {
      super(var1, var2, var4, var5);
   }

   protected boolean isTeleportFriendlyBlock(BlockPos var1) {
      BlockState var2 = this.level.getBlockState(var1);
      return (var2.entityCanStandOn(this.level, var1, this.tamable) || var2.is(BlockTags.LEAVES)) && this.level.isEmptyBlock(var1.above()) && this.level.isEmptyBlock(var1.above(2));
   }
}
