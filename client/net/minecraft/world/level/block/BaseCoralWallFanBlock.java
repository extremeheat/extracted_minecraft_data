package net.minecraft.world.level.block;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;
import com.mojang.serialization.MapCodec;
import java.util.Map;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.ScheduledTickAccess;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;

public class BaseCoralWallFanBlock extends BaseCoralFanBlock {
   public static final MapCodec<BaseCoralWallFanBlock> CODEC = simpleCodec(BaseCoralWallFanBlock::new);
   public static final EnumProperty<Direction> FACING;
   private static final Map<Direction, VoxelShape> SHAPES;

   public MapCodec<? extends BaseCoralWallFanBlock> codec() {
      return CODEC;
   }

   protected BaseCoralWallFanBlock(BlockBehaviour.Properties var1) {
      super(var1);
      this.registerDefaultState((BlockState)((BlockState)((BlockState)this.stateDefinition.any()).setValue(FACING, Direction.NORTH)).setValue(WATERLOGGED, true));
   }

   protected VoxelShape getShape(BlockState var1, BlockGetter var2, BlockPos var3, CollisionContext var4) {
      return (VoxelShape)SHAPES.get(var1.getValue(FACING));
   }

   protected BlockState rotate(BlockState var1, Rotation var2) {
      return (BlockState)var1.setValue(FACING, var2.rotate((Direction)var1.getValue(FACING)));
   }

   protected BlockState mirror(BlockState var1, Mirror var2) {
      return var1.rotate(var2.getRotation((Direction)var1.getValue(FACING)));
   }

   protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> var1) {
      var1.add(FACING, WATERLOGGED);
   }

   protected BlockState updateShape(BlockState var1, LevelReader var2, ScheduledTickAccess var3, BlockPos var4, Direction var5, BlockPos var6, BlockState var7, RandomSource var8) {
      if ((Boolean)var1.getValue(WATERLOGGED)) {
         var3.scheduleTick(var4, (Fluid)Fluids.WATER, Fluids.WATER.getTickDelay(var2));
      }

      return var5.getOpposite() == var1.getValue(FACING) && !var1.canSurvive(var2, var4) ? Blocks.AIR.defaultBlockState() : var1;
   }

   protected boolean canSurvive(BlockState var1, LevelReader var2, BlockPos var3) {
      Direction var4 = (Direction)var1.getValue(FACING);
      BlockPos var5 = var3.relative(var4.getOpposite());
      BlockState var6 = var2.getBlockState(var5);
      return var6.isFaceSturdy(var2, var5, var4);
   }

   @Nullable
   public BlockState getStateForPlacement(BlockPlaceContext var1) {
      BlockState var2 = super.getStateForPlacement(var1);
      Level var3 = var1.getLevel();
      BlockPos var4 = var1.getClickedPos();
      Direction[] var5 = var1.getNearestLookingDirections();

      for(Direction var9 : var5) {
         if (var9.getAxis().isHorizontal()) {
            var2 = (BlockState)var2.setValue(FACING, var9.getOpposite());
            if (var2.canSurvive(var3, var4)) {
               return var2;
            }
         }
      }

      return null;
   }

   static {
      FACING = HorizontalDirectionalBlock.FACING;
      SHAPES = Maps.newEnumMap(ImmutableMap.of(Direction.NORTH, Block.box(0.0, 4.0, 5.0, 16.0, 12.0, 16.0), Direction.SOUTH, Block.box(0.0, 4.0, 0.0, 16.0, 12.0, 11.0), Direction.WEST, Block.box(5.0, 4.0, 0.0, 16.0, 12.0, 16.0), Direction.EAST, Block.box(0.0, 4.0, 0.0, 11.0, 12.0, 16.0)));
   }
}
