package net.minecraft.world.entity.ai.goal;

import java.util.Iterator;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.util.LandRandomPos;
import net.minecraft.world.level.block.LeavesBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;

public class WaterAvoidingRandomFlyingGoal extends WaterAvoidingRandomStrollGoal {
   public WaterAvoidingRandomFlyingGoal(PathfinderMob var1, double var2) {
      super(var1, var2);
   }

   @Nullable
   protected Vec3 getPosition() {
      Vec3 var1 = null;
      if (this.mob.isInWater()) {
         var1 = LandRandomPos.getPos(this.mob, 15, 15);
      }

      if (this.mob.getRandom().nextFloat() >= this.probability) {
         var1 = this.getTreePos();
      }

      return var1 == null ? super.getPosition() : var1;
   }

   @Nullable
   private Vec3 getTreePos() {
      BlockPos var1 = this.mob.blockPosition();
      BlockPos.MutableBlockPos var2 = new BlockPos.MutableBlockPos();
      BlockPos.MutableBlockPos var3 = new BlockPos.MutableBlockPos();
      Iterable var4 = BlockPos.betweenClosed(Mth.floor(this.mob.getX() - 3.0D), Mth.floor(this.mob.getY() - 6.0D), Mth.floor(this.mob.getZ() - 3.0D), Mth.floor(this.mob.getX() + 3.0D), Mth.floor(this.mob.getY() + 6.0D), Mth.floor(this.mob.getZ() + 3.0D));
      Iterator var5 = var4.iterator();

      BlockPos var6;
      boolean var8;
      do {
         do {
            if (!var5.hasNext()) {
               return null;
            }

            var6 = (BlockPos)var5.next();
         } while(var1.equals(var6));

         BlockState var7 = this.mob.level.getBlockState(var3.setWithOffset(var6, Direction.DOWN));
         var8 = var7.getBlock() instanceof LeavesBlock || var7.is(BlockTags.LOGS);
      } while(!var8 || !this.mob.level.isEmptyBlock(var6) || !this.mob.level.isEmptyBlock(var2.setWithOffset(var6, Direction.UP)));

      return Vec3.atBottomCenterOf(var6);
   }
}
