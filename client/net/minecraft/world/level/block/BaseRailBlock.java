package net.minecraft.world.level.block;

import net.minecraft.core.BlockPos;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.BlockLayer;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.level.block.state.properties.RailShape;
import net.minecraft.world.level.material.PushReaction;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;

public abstract class BaseRailBlock extends Block {
   protected static final VoxelShape FLAT_AABB = Block.box(0.0D, 0.0D, 0.0D, 16.0D, 2.0D, 16.0D);
   protected static final VoxelShape HALF_BLOCK_AABB = Block.box(0.0D, 0.0D, 0.0D, 16.0D, 8.0D, 16.0D);
   private final boolean isStraight;

   public static boolean isRail(Level var0, BlockPos var1) {
      return isRail(var0.getBlockState(var1));
   }

   public static boolean isRail(BlockState var0) {
      return var0.is(BlockTags.RAILS);
   }

   protected BaseRailBlock(boolean var1, Block.Properties var2) {
      super(var2);
      this.isStraight = var1;
   }

   public boolean isStraight() {
      return this.isStraight;
   }

   public VoxelShape getShape(BlockState var1, BlockGetter var2, BlockPos var3, CollisionContext var4) {
      RailShape var5 = var1.getBlock() == this ? (RailShape)var1.getValue(this.getShapeProperty()) : null;
      return var5 != null && var5.isAscending() ? HALF_BLOCK_AABB : FLAT_AABB;
   }

   public boolean canSurvive(BlockState var1, LevelReader var2, BlockPos var3) {
      return canSupportRigidBlock(var2, var3.below());
   }

   public void onPlace(BlockState var1, Level var2, BlockPos var3, BlockState var4, boolean var5) {
      if (var4.getBlock() != var1.getBlock()) {
         if (!var2.isClientSide) {
            var1 = this.updateDir(var2, var3, var1, true);
            if (this.isStraight) {
               var1.neighborChanged(var2, var3, this, var3, var5);
            }
         }

      }
   }

   public void neighborChanged(BlockState var1, Level var2, BlockPos var3, Block var4, BlockPos var5, boolean var6) {
      if (!var2.isClientSide) {
         RailShape var7 = (RailShape)var1.getValue(this.getShapeProperty());
         boolean var8 = false;
         BlockPos var9 = var3.below();
         if (!canSupportRigidBlock(var2, var9)) {
            var8 = true;
         }

         BlockPos var10 = var3.east();
         if (var7 == RailShape.ASCENDING_EAST && !canSupportRigidBlock(var2, var10)) {
            var8 = true;
         } else {
            BlockPos var11 = var3.west();
            if (var7 == RailShape.ASCENDING_WEST && !canSupportRigidBlock(var2, var11)) {
               var8 = true;
            } else {
               BlockPos var12 = var3.north();
               if (var7 == RailShape.ASCENDING_NORTH && !canSupportRigidBlock(var2, var12)) {
                  var8 = true;
               } else {
                  BlockPos var13 = var3.south();
                  if (var7 == RailShape.ASCENDING_SOUTH && !canSupportRigidBlock(var2, var13)) {
                     var8 = true;
                  }
               }
            }
         }

         if (var8 && !var2.isEmptyBlock(var3)) {
            if (!var6) {
               dropResources(var1, var2, var3);
            }

            var2.removeBlock(var3, var6);
         } else {
            this.updateState(var1, var2, var3, var4);
         }

      }
   }

   protected void updateState(BlockState var1, Level var2, BlockPos var3, Block var4) {
   }

   protected BlockState updateDir(Level var1, BlockPos var2, BlockState var3, boolean var4) {
      return var1.isClientSide ? var3 : (new RailState(var1, var2, var3)).place(var1.hasNeighborSignal(var2), var4).getState();
   }

   public PushReaction getPistonPushReaction(BlockState var1) {
      return PushReaction.NORMAL;
   }

   public BlockLayer getRenderLayer() {
      return BlockLayer.CUTOUT;
   }

   public void onRemove(BlockState var1, Level var2, BlockPos var3, BlockState var4, boolean var5) {
      if (!var5) {
         super.onRemove(var1, var2, var3, var4, var5);
         if (((RailShape)var1.getValue(this.getShapeProperty())).isAscending()) {
            var2.updateNeighborsAt(var3.above(), this);
         }

         if (this.isStraight) {
            var2.updateNeighborsAt(var3, this);
            var2.updateNeighborsAt(var3.below(), this);
         }

      }
   }

   public abstract Property<RailShape> getShapeProperty();
}
