package net.minecraft.world.level.block.piston;

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
   protected static final VoxelShape EAST_AABB;
   protected static final VoxelShape WEST_AABB;
   protected static final VoxelShape SOUTH_AABB;
   protected static final VoxelShape NORTH_AABB;
   protected static final VoxelShape UP_AABB;
   protected static final VoxelShape DOWN_AABB;
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

   public PistonHeadBlock(Block.Properties var1) {
      super(var1);
      this.registerDefaultState((BlockState)((BlockState)((BlockState)((BlockState)this.stateDefinition.any()).setValue(FACING, Direction.NORTH)).setValue(TYPE, PistonType.DEFAULT)).setValue(SHORT, false));
   }

   private VoxelShape getBaseShape(BlockState var1) {
      switch((Direction)var1.getValue(FACING)) {
      case DOWN:
      default:
         return DOWN_AABB;
      case UP:
         return UP_AABB;
      case NORTH:
         return NORTH_AABB;
      case SOUTH:
         return SOUTH_AABB;
      case WEST:
         return WEST_AABB;
      case EAST:
         return EAST_AABB;
      }
   }

   public boolean useShapeForLightOcclusion(BlockState var1) {
      return true;
   }

   public VoxelShape getShape(BlockState var1, BlockGetter var2, BlockPos var3, CollisionContext var4) {
      return Shapes.or(this.getBaseShape(var1), this.getArmShape(var1));
   }

   private VoxelShape getArmShape(BlockState var1) {
      boolean var2 = (Boolean)var1.getValue(SHORT);
      switch((Direction)var1.getValue(FACING)) {
      case DOWN:
      default:
         return var2 ? SHORT_DOWN_ARM_AABB : DOWN_ARM_AABB;
      case UP:
         return var2 ? SHORT_UP_ARM_AABB : UP_ARM_AABB;
      case NORTH:
         return var2 ? SHORT_NORTH_ARM_AABB : NORTH_ARM_AABB;
      case SOUTH:
         return var2 ? SHORT_SOUTH_ARM_AABB : SOUTH_ARM_AABB;
      case WEST:
         return var2 ? SHORT_WEST_ARM_AABB : WEST_ARM_AABB;
      case EAST:
         return var2 ? SHORT_EAST_ARM_AABB : EAST_ARM_AABB;
      }
   }

   public void playerWillDestroy(Level var1, BlockPos var2, BlockState var3, Player var4) {
      if (!var1.isClientSide && var4.abilities.instabuild) {
         BlockPos var5 = var2.relative(((Direction)var3.getValue(FACING)).getOpposite());
         Block var6 = var1.getBlockState(var5).getBlock();
         if (var6 == Blocks.PISTON || var6 == Blocks.STICKY_PISTON) {
            var1.removeBlock(var5, false);
         }
      }

      super.playerWillDestroy(var1, var2, var3, var4);
   }

   public void onRemove(BlockState var1, Level var2, BlockPos var3, BlockState var4, boolean var5) {
      if (var1.getBlock() != var4.getBlock()) {
         super.onRemove(var1, var2, var3, var4, var5);
         Direction var6 = ((Direction)var1.getValue(FACING)).getOpposite();
         var3 = var3.relative(var6);
         BlockState var7 = var2.getBlockState(var3);
         if ((var7.getBlock() == Blocks.PISTON || var7.getBlock() == Blocks.STICKY_PISTON) && (Boolean)var7.getValue(PistonBaseBlock.EXTENDED)) {
            dropResources(var7, var2, var3);
            var2.removeBlock(var3, false);
         }

      }
   }

   public BlockState updateShape(BlockState var1, Direction var2, BlockState var3, LevelAccessor var4, BlockPos var5, BlockPos var6) {
      return var2.getOpposite() == var1.getValue(FACING) && !var1.canSurvive(var4, var5) ? Blocks.AIR.defaultBlockState() : super.updateShape(var1, var2, var3, var4, var5, var6);
   }

   public boolean canSurvive(BlockState var1, LevelReader var2, BlockPos var3) {
      Block var4 = var2.getBlockState(var3.relative(((Direction)var1.getValue(FACING)).getOpposite())).getBlock();
      return var4 == Blocks.PISTON || var4 == Blocks.STICKY_PISTON || var4 == Blocks.MOVING_PISTON;
   }

   public void neighborChanged(BlockState var1, Level var2, BlockPos var3, Block var4, BlockPos var5, boolean var6) {
      if (var1.canSurvive(var2, var3)) {
         BlockPos var7 = var3.relative(((Direction)var1.getValue(FACING)).getOpposite());
         var2.getBlockState(var7).neighborChanged(var2, var7, var4, var5, false);
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
      EAST_AABB = Block.box(12.0D, 0.0D, 0.0D, 16.0D, 16.0D, 16.0D);
      WEST_AABB = Block.box(0.0D, 0.0D, 0.0D, 4.0D, 16.0D, 16.0D);
      SOUTH_AABB = Block.box(0.0D, 0.0D, 12.0D, 16.0D, 16.0D, 16.0D);
      NORTH_AABB = Block.box(0.0D, 0.0D, 0.0D, 16.0D, 16.0D, 4.0D);
      UP_AABB = Block.box(0.0D, 12.0D, 0.0D, 16.0D, 16.0D, 16.0D);
      DOWN_AABB = Block.box(0.0D, 0.0D, 0.0D, 16.0D, 4.0D, 16.0D);
      UP_ARM_AABB = Block.box(6.0D, -4.0D, 6.0D, 10.0D, 12.0D, 10.0D);
      DOWN_ARM_AABB = Block.box(6.0D, 4.0D, 6.0D, 10.0D, 20.0D, 10.0D);
      SOUTH_ARM_AABB = Block.box(6.0D, 6.0D, -4.0D, 10.0D, 10.0D, 12.0D);
      NORTH_ARM_AABB = Block.box(6.0D, 6.0D, 4.0D, 10.0D, 10.0D, 20.0D);
      EAST_ARM_AABB = Block.box(-4.0D, 6.0D, 6.0D, 12.0D, 10.0D, 10.0D);
      WEST_ARM_AABB = Block.box(4.0D, 6.0D, 6.0D, 20.0D, 10.0D, 10.0D);
      SHORT_UP_ARM_AABB = Block.box(6.0D, 0.0D, 6.0D, 10.0D, 12.0D, 10.0D);
      SHORT_DOWN_ARM_AABB = Block.box(6.0D, 4.0D, 6.0D, 10.0D, 16.0D, 10.0D);
      SHORT_SOUTH_ARM_AABB = Block.box(6.0D, 6.0D, 0.0D, 10.0D, 10.0D, 12.0D);
      SHORT_NORTH_ARM_AABB = Block.box(6.0D, 6.0D, 4.0D, 10.0D, 10.0D, 16.0D);
      SHORT_EAST_ARM_AABB = Block.box(0.0D, 6.0D, 6.0D, 12.0D, 10.0D, 10.0D);
      SHORT_WEST_ARM_AABB = Block.box(4.0D, 6.0D, 6.0D, 16.0D, 10.0D, 10.0D);
   }
}
