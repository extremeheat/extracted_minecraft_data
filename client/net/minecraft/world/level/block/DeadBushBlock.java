package net.minecraft.world.level.block;

import net.minecraft.core.BlockPos;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;

public class DeadBushBlock extends BushBlock {
   protected static final float AABB_OFFSET = 6.0F;
   protected static final VoxelShape SHAPE = Block.box(2.0D, 0.0D, 2.0D, 14.0D, 13.0D, 14.0D);

   protected DeadBushBlock(BlockBehaviour.Properties var1) {
      super(var1);
   }

   public VoxelShape getShape(BlockState var1, BlockGetter var2, BlockPos var3, CollisionContext var4) {
      return SHAPE;
   }

   protected boolean mayPlaceOn(BlockState var1, BlockGetter var2, BlockPos var3) {
      return var1.is(Blocks.SAND) || var1.is(Blocks.RED_SAND) || var1.is(Blocks.TERRACOTTA) || var1.is(Blocks.WHITE_TERRACOTTA) || var1.is(Blocks.ORANGE_TERRACOTTA) || var1.is(Blocks.MAGENTA_TERRACOTTA) || var1.is(Blocks.LIGHT_BLUE_TERRACOTTA) || var1.is(Blocks.YELLOW_TERRACOTTA) || var1.is(Blocks.LIME_TERRACOTTA) || var1.is(Blocks.PINK_TERRACOTTA) || var1.is(Blocks.GRAY_TERRACOTTA) || var1.is(Blocks.LIGHT_GRAY_TERRACOTTA) || var1.is(Blocks.CYAN_TERRACOTTA) || var1.is(Blocks.PURPLE_TERRACOTTA) || var1.is(Blocks.BLUE_TERRACOTTA) || var1.is(Blocks.BROWN_TERRACOTTA) || var1.is(Blocks.GREEN_TERRACOTTA) || var1.is(Blocks.RED_TERRACOTTA) || var1.is(Blocks.BLACK_TERRACOTTA) || var1.is(BlockTags.DIRT);
   }
}
