package net.minecraft.world.level.block;

import com.mojang.serialization.MapCodec;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.ScheduledTickAccess;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.AttachFace;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.EnumProperty;

public abstract class FaceAttachedHorizontalDirectionalBlock extends HorizontalDirectionalBlock {
   public static final EnumProperty<AttachFace> FACE;

   protected FaceAttachedHorizontalDirectionalBlock(BlockBehaviour.Properties var1) {
      super(var1);
   }

   protected abstract MapCodec<? extends FaceAttachedHorizontalDirectionalBlock> codec();

   protected boolean canSurvive(BlockState var1, LevelReader var2, BlockPos var3) {
      return canAttach(var2, var3, getConnectedDirection(var1).getOpposite());
   }

   public static boolean canAttach(LevelReader var0, BlockPos var1, Direction var2) {
      BlockPos var3 = var1.relative(var2);
      return var0.getBlockState(var3).isFaceSturdy(var0, var3, var2.getOpposite());
   }

   @Nullable
   public BlockState getStateForPlacement(BlockPlaceContext var1) {
      Direction[] var2 = var1.getNearestLookingDirections();
      int var3 = var2.length;

      for(int var4 = 0; var4 < var3; ++var4) {
         Direction var5 = var2[var4];
         BlockState var6;
         if (var5.getAxis() == Direction.Axis.Y) {
            var6 = (BlockState)((BlockState)this.defaultBlockState().setValue(FACE, var5 == Direction.UP ? AttachFace.CEILING : AttachFace.FLOOR)).setValue(FACING, var1.getHorizontalDirection());
         } else {
            var6 = (BlockState)((BlockState)this.defaultBlockState().setValue(FACE, AttachFace.WALL)).setValue(FACING, var5.getOpposite());
         }

         if (var6.canSurvive(var1.getLevel(), var1.getClickedPos())) {
            return var6;
         }
      }

      return null;
   }

   protected BlockState updateShape(BlockState var1, LevelReader var2, ScheduledTickAccess var3, BlockPos var4, Direction var5, BlockPos var6, BlockState var7, RandomSource var8) {
      return getConnectedDirection(var1).getOpposite() == var5 && !var1.canSurvive(var2, var4) ? Blocks.AIR.defaultBlockState() : super.updateShape(var1, var2, var3, var4, var5, var6, var7, var8);
   }

   protected static Direction getConnectedDirection(BlockState var0) {
      switch ((AttachFace)var0.getValue(FACE)) {
         case CEILING -> {
            return Direction.DOWN;
         }
         case FLOOR -> {
            return Direction.UP;
         }
         default -> {
            return (Direction)var0.getValue(FACING);
         }
      }
   }

   static {
      FACE = BlockStateProperties.ATTACH_FACE;
   }
}
