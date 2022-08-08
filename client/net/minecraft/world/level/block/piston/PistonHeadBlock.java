package net.minecraft.world.level.block.piston;

import java.util.Arrays;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.DirectionalBlock;
import net.minecraft.world.level.block.Mirror;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.level.block.state.properties.PistonType;
import net.minecraft.world.level.pathfinder.PathComputationType;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

public class PistonHeadBlock extends DirectionalBlock {
   public static final EnumProperty<PistonType> TYPE;
   public static final BooleanProperty SHORT;
   public static final float PLATFORM = 4.0F;
   protected static final VoxelShape EAST_AABB;
   protected static final VoxelShape WEST_AABB;
   protected static final VoxelShape SOUTH_AABB;
   protected static final VoxelShape NORTH_AABB;
   protected static final VoxelShape UP_AABB;
   protected static final VoxelShape DOWN_AABB;
   protected static final float AABB_OFFSET = 2.0F;
   protected static final float EDGE_MIN = 6.0F;
   protected static final float EDGE_MAX = 10.0F;
   protected static final VoxelShape UP_ARM_AABB;
   protected static final VoxelShape DOWN_ARM_AABB;
   protected static final VoxelShape SOUTH_ARM_AABB;
   protected static final VoxelShape NORTH_ARM_AABB;
   protected static final VoxelShape EAST_ARM_AABB;
   protected static final VoxelShape WEST_ARM_AABB;
   protected static final VoxelShape SHORT_UP_ARM_AABB;
   protected static final VoxelShape SHORT_DOWN_ARM_AABB;
   protected static final VoxelShape SHORT_SOUTH_ARM_AABB;
   protected static final VoxelShape SHORT_NORTH_ARM_AABB;
   protected static final VoxelShape SHORT_EAST_ARM_AABB;
   protected static final VoxelShape SHORT_WEST_ARM_AABB;
   private static final VoxelShape[] SHAPES_SHORT;
   private static final VoxelShape[] SHAPES_LONG;

   private static VoxelShape[] makeShapes(boolean var0) {
      return (VoxelShape[])Arrays.stream(Direction.values()).map((var1) -> {
         return calculateShape(var1, var0);
      }).toArray((var0x) -> {
         return new VoxelShape[var0x];
      });
   }

   private static VoxelShape calculateShape(Direction var0, boolean var1) {
      switch (var0) {
         case DOWN:
         default:
            return Shapes.or(DOWN_AABB, var1 ? SHORT_DOWN_ARM_AABB : DOWN_ARM_AABB);
         case UP:
            return Shapes.or(UP_AABB, var1 ? SHORT_UP_ARM_AABB : UP_ARM_AABB);
         case NORTH:
            return Shapes.or(NORTH_AABB, var1 ? SHORT_NORTH_ARM_AABB : NORTH_ARM_AABB);
         case SOUTH:
            return Shapes.or(SOUTH_AABB, var1 ? SHORT_SOUTH_ARM_AABB : SOUTH_ARM_AABB);
         case WEST:
            return Shapes.or(WEST_AABB, var1 ? SHORT_WEST_ARM_AABB : WEST_ARM_AABB);
         case EAST:
            return Shapes.or(EAST_AABB, var1 ? SHORT_EAST_ARM_AABB : EAST_ARM_AABB);
      }
   }

   public PistonHeadBlock(BlockBehaviour.Properties var1) {
      super(var1);
      this.registerDefaultState((BlockState)((BlockState)((BlockState)((BlockState)this.stateDefinition.any()).setValue(FACING, Direction.NORTH)).setValue(TYPE, PistonType.DEFAULT)).setValue(SHORT, false));
   }

   public boolean useShapeForLightOcclusion(BlockState var1) {
      return true;
   }

   public VoxelShape getShape(BlockState var1, BlockGetter var2, BlockPos var3, CollisionContext var4) {
      return ((Boolean)var1.getValue(SHORT) ? SHAPES_SHORT : SHAPES_LONG)[((Direction)var1.getValue(FACING)).ordinal()];
   }

   private boolean isFittingBase(BlockState var1, BlockState var2) {
      Block var3 = var1.getValue(TYPE) == PistonType.DEFAULT ? Blocks.PISTON : Blocks.STICKY_PISTON;
      return var2.is(var3) && (Boolean)var2.getValue(PistonBaseBlock.EXTENDED) && var2.getValue(FACING) == var1.getValue(FACING);
   }

   public void playerWillDestroy(Level var1, BlockPos var2, BlockState var3, Player var4) {
      if (!var1.isClientSide && var4.getAbilities().instabuild) {
         BlockPos var5 = var2.relative(((Direction)var3.getValue(FACING)).getOpposite());
         if (this.isFittingBase(var3, var1.getBlockState(var5))) {
            var1.destroyBlock(var5, false);
         }
      }

      super.playerWillDestroy(var1, var2, var3, var4);
   }

   public void onRemove(BlockState var1, Level var2, BlockPos var3, BlockState var4, boolean var5) {
      if (!var1.is(var4.getBlock())) {
         super.onRemove(var1, var2, var3, var4, var5);
         BlockPos var6 = var3.relative(((Direction)var1.getValue(FACING)).getOpposite());
         if (this.isFittingBase(var1, var2.getBlockState(var6))) {
            var2.destroyBlock(var6, true);
         }

      }
   }

   public BlockState updateShape(BlockState var1, Direction var2, BlockState var3, LevelAccessor var4, BlockPos var5, BlockPos var6) {
      return var2.getOpposite() == var1.getValue(FACING) && !var1.canSurvive(var4, var5) ? Blocks.AIR.defaultBlockState() : super.updateShape(var1, var2, var3, var4, var5, var6);
   }

   public boolean canSurvive(BlockState var1, LevelReader var2, BlockPos var3) {
      BlockState var4 = var2.getBlockState(var3.relative(((Direction)var1.getValue(FACING)).getOpposite()));
      return this.isFittingBase(var1, var4) || var4.is(Blocks.MOVING_PISTON) && var4.getValue(FACING) == var1.getValue(FACING);
   }

   public void neighborChanged(BlockState var1, Level var2, BlockPos var3, Block var4, BlockPos var5, boolean var6) {
      if (var1.canSurvive(var2, var3)) {
         var2.neighborChanged(var3.relative(((Direction)var1.getValue(FACING)).getOpposite()), var4, var5);
      }

   }

   public ItemStack getCloneItemStack(BlockGetter var1, BlockPos var2, BlockState var3) {
      return new ItemStack(var3.getValue(TYPE) == PistonType.STICKY ? Blocks.STICKY_PISTON : Blocks.PISTON);
   }

   public BlockState rotate(BlockState var1, Rotation var2) {
      return (BlockState)var1.setValue(FACING, var2.rotate((Direction)var1.getValue(FACING)));
   }

   public BlockState mirror(BlockState var1, Mirror var2) {
      return var1.rotate(var2.getRotation((Direction)var1.getValue(FACING)));
   }

   protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> var1) {
      var1.add(FACING, TYPE, SHORT);
   }

   public boolean isPathfindable(BlockState var1, BlockGetter var2, BlockPos var3, PathComputationType var4) {
      return false;
   }

   static {
      TYPE = BlockStateProperties.PISTON_TYPE;
      SHORT = BlockStateProperties.SHORT;
      EAST_AABB = Block.box(12.0, 0.0, 0.0, 16.0, 16.0, 16.0);
      WEST_AABB = Block.box(0.0, 0.0, 0.0, 4.0, 16.0, 16.0);
      SOUTH_AABB = Block.box(0.0, 0.0, 12.0, 16.0, 16.0, 16.0);
      NORTH_AABB = Block.box(0.0, 0.0, 0.0, 16.0, 16.0, 4.0);
      UP_AABB = Block.box(0.0, 12.0, 0.0, 16.0, 16.0, 16.0);
      DOWN_AABB = Block.box(0.0, 0.0, 0.0, 16.0, 4.0, 16.0);
      UP_ARM_AABB = Block.box(6.0, -4.0, 6.0, 10.0, 12.0, 10.0);
      DOWN_ARM_AABB = Block.box(6.0, 4.0, 6.0, 10.0, 20.0, 10.0);
      SOUTH_ARM_AABB = Block.box(6.0, 6.0, -4.0, 10.0, 10.0, 12.0);
      NORTH_ARM_AABB = Block.box(6.0, 6.0, 4.0, 10.0, 10.0, 20.0);
      EAST_ARM_AABB = Block.box(-4.0, 6.0, 6.0, 12.0, 10.0, 10.0);
      WEST_ARM_AABB = Block.box(4.0, 6.0, 6.0, 20.0, 10.0, 10.0);
      SHORT_UP_ARM_AABB = Block.box(6.0, 0.0, 6.0, 10.0, 12.0, 10.0);
      SHORT_DOWN_ARM_AABB = Block.box(6.0, 4.0, 6.0, 10.0, 16.0, 10.0);
      SHORT_SOUTH_ARM_AABB = Block.box(6.0, 6.0, 0.0, 10.0, 10.0, 12.0);
      SHORT_NORTH_ARM_AABB = Block.box(6.0, 6.0, 4.0, 10.0, 10.0, 16.0);
      SHORT_EAST_ARM_AABB = Block.box(0.0, 6.0, 6.0, 12.0, 10.0, 10.0);
      SHORT_WEST_ARM_AABB = Block.box(4.0, 6.0, 6.0, 16.0, 10.0, 10.0);
      SHAPES_SHORT = makeShapes(true);
      SHAPES_LONG = makeShapes(false);
   }
}
