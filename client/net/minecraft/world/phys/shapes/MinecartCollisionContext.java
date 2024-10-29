package net.minecraft.world.phys.shapes;

import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.vehicle.AbstractMinecart;
import net.minecraft.world.level.CollisionGetter;
import net.minecraft.world.level.block.BaseRailBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.RailShape;

public class MinecartCollisionContext extends EntityCollisionContext {
   @Nullable
   private BlockPos ingoreBelow;
   @Nullable
   private BlockPos slopeIgnore;

   protected MinecartCollisionContext(AbstractMinecart var1, boolean var2) {
      super(var1, var2);
      this.setupContext(var1);
   }

   private void setupContext(AbstractMinecart var1) {
      BlockPos var2 = var1.getCurrentBlockPosOrRailBelow();
      BlockState var3 = var1.level().getBlockState(var2);
      boolean var4 = BaseRailBlock.isRail(var3);
      if (var4) {
         this.ingoreBelow = var2.below();
         RailShape var5 = (RailShape)var3.getValue(((BaseRailBlock)var3.getBlock()).getShapeProperty());
         if (var5.isSlope()) {
            BlockPos var10001;
            switch (var5) {
               case ASCENDING_EAST -> var10001 = var2.east();
               case ASCENDING_WEST -> var10001 = var2.west();
               case ASCENDING_NORTH -> var10001 = var2.north();
               case ASCENDING_SOUTH -> var10001 = var2.south();
               default -> var10001 = null;
            }

            this.slopeIgnore = var10001;
         }
      }

   }

   public VoxelShape getCollisionShape(BlockState var1, CollisionGetter var2, BlockPos var3) {
      return !var3.equals(this.ingoreBelow) && !var3.equals(this.slopeIgnore) ? super.getCollisionShape(var1, var2, var3) : Shapes.empty();
   }
}
