package net.minecraft.world.level.block;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;
import java.util.Map;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;

public class WallBannerBlock extends AbstractBannerBlock {
   public static final DirectionProperty FACING;
   private static final Map<Direction, VoxelShape> SHAPES;

   public WallBannerBlock(DyeColor var1, BlockBehaviour.Properties var2) {
      super(var1, var2);
      this.registerDefaultState((BlockState)((BlockState)this.stateDefinition.any()).setValue(FACING, Direction.NORTH));
   }

   public String getDescriptionId() {
      return this.asItem().getDescriptionId();
   }

   public boolean canSurvive(BlockState var1, LevelReader var2, BlockPos var3) {
      return var2.getBlockState(var3.relative(((Direction)var1.getValue(FACING)).getOpposite())).getMaterial().isSolid();
   }

   public BlockState updateShape(BlockState var1, Direction var2, BlockState var3, LevelAccessor var4, BlockPos var5, BlockPos var6) {
      return var2 == ((Direction)var1.getValue(FACING)).getOpposite() && !var1.canSurvive(var4, var5) ? Blocks.AIR.defaultBlockState() : super.updateShape(var1, var2, var3, var4, var5, var6);
   }

   public VoxelShape getShape(BlockState var1, BlockGetter var2, BlockPos var3, CollisionContext var4) {
      return (VoxelShape)SHAPES.get(var1.getValue(FACING));
   }

   public BlockState getStateForPlacement(BlockPlaceContext var1) {
      BlockState var2 = this.defaultBlockState();
      Level var3 = var1.getLevel();
      BlockPos var4 = var1.getClickedPos();
      Direction[] var5 = var1.getNearestLookingDirections();
      Direction[] var6 = var5;
      int var7 = var5.length;

      for(int var8 = 0; var8 < var7; ++var8) {
         Direction var9 = var6[var8];
         if (var9.getAxis().isHorizontal()) {
            Direction var10 = var9.getOpposite();
            var2 = (BlockState)var2.setValue(FACING, var10);
            if (var2.canSurvive(var3, var4)) {
               return var2;
            }
         }
      }

      return null;
   }

   public BlockState rotate(BlockState var1, Rotation var2) {
      return (BlockState)var1.setValue(FACING, var2.rotate((Direction)var1.getValue(FACING)));
   }

   public BlockState mirror(BlockState var1, Mirror var2) {
      return var1.rotate(var2.getRotation((Direction)var1.getValue(FACING)));
   }

   protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> var1) {
      var1.add(FACING);
   }

   static {
      FACING = HorizontalDirectionalBlock.FACING;
      SHAPES = Maps.newEnumMap(ImmutableMap.of(Direction.NORTH, Block.box(0.0D, 0.0D, 14.0D, 16.0D, 12.5D, 16.0D), Direction.SOUTH, Block.box(0.0D, 0.0D, 0.0D, 16.0D, 12.5D, 2.0D), Direction.WEST, Block.box(14.0D, 0.0D, 0.0D, 16.0D, 12.5D, 16.0D), Direction.EAST, Block.box(0.0D, 0.0D, 0.0D, 2.0D, 12.5D, 16.0D)));
   }
}
