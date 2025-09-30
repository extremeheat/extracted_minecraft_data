package net.minecraft.world.level.block.piston;

import com.mojang.serialization.MapCodec;
import java.util.Map;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.ScheduledTickAccess;
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
import net.minecraft.world.level.redstone.ExperimentalRedstoneUtils;
import net.minecraft.world.level.redstone.Orientation;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

public class PistonHeadBlock extends DirectionalBlock {
   public static final MapCodec<PistonHeadBlock> CODEC = simpleCodec(PistonHeadBlock::new);
   public static final EnumProperty<PistonType> TYPE;
   public static final BooleanProperty SHORT;
   public static final int PLATFORM_THICKNESS = 4;
   private static final VoxelShape SHAPE_PLATFORM;
   private static final Map<Direction, VoxelShape> SHAPES_SHORT;
   private static final Map<Direction, VoxelShape> SHAPES;

   protected MapCodec<PistonHeadBlock> codec() {
      return CODEC;
   }

   public PistonHeadBlock(BlockBehaviour.Properties var1) {
      super(var1);
      this.registerDefaultState((BlockState)((BlockState)((BlockState)((BlockState)this.stateDefinition.any()).setValue(FACING, Direction.NORTH)).setValue(TYPE, PistonType.DEFAULT)).setValue(SHORT, false));
   }

   protected boolean useShapeForLightOcclusion(BlockState var1) {
      return true;
   }

   protected VoxelShape getShape(BlockState var1, BlockGetter var2, BlockPos var3, CollisionContext var4) {
      return (VoxelShape)((Boolean)var1.getValue(SHORT) ? SHAPES_SHORT : SHAPES).get(var1.getValue(FACING));
   }

   private boolean isFittingBase(BlockState var1, BlockState var2) {
      Block var3 = var1.getValue(TYPE) == PistonType.DEFAULT ? Blocks.PISTON : Blocks.STICKY_PISTON;
      return var2.is(var3) && (Boolean)var2.getValue(PistonBaseBlock.EXTENDED) && var2.getValue(FACING) == var1.getValue(FACING);
   }

   public BlockState playerWillDestroy(Level var1, BlockPos var2, BlockState var3, Player var4) {
      if (!var1.isClientSide() && var4.preventsBlockDrops()) {
         BlockPos var5 = var2.relative(((Direction)var3.getValue(FACING)).getOpposite());
         if (this.isFittingBase(var3, var1.getBlockState(var5))) {
            var1.destroyBlock(var5, false);
         }
      }

      return super.playerWillDestroy(var1, var2, var3, var4);
   }

   protected void affectNeighborsAfterRemoval(BlockState var1, ServerLevel var2, BlockPos var3, boolean var4) {
      BlockPos var5 = var3.relative(((Direction)var1.getValue(FACING)).getOpposite());
      if (this.isFittingBase(var1, var2.getBlockState(var5))) {
         var2.destroyBlock(var5, true);
      }

   }

   protected BlockState updateShape(BlockState var1, LevelReader var2, ScheduledTickAccess var3, BlockPos var4, Direction var5, BlockPos var6, BlockState var7, RandomSource var8) {
      return var5.getOpposite() == var1.getValue(FACING) && !var1.canSurvive(var2, var4) ? Blocks.AIR.defaultBlockState() : super.updateShape(var1, var2, var3, var4, var5, var6, var7, var8);
   }

   protected boolean canSurvive(BlockState var1, LevelReader var2, BlockPos var3) {
      BlockState var4 = var2.getBlockState(var3.relative(((Direction)var1.getValue(FACING)).getOpposite()));
      return this.isFittingBase(var1, var4) || var4.is(Blocks.MOVING_PISTON) && var4.getValue(FACING) == var1.getValue(FACING);
   }

   protected void neighborChanged(BlockState var1, Level var2, BlockPos var3, Block var4, @Nullable Orientation var5, boolean var6) {
      if (var1.canSurvive(var2, var3)) {
         var2.neighborChanged(var3.relative(((Direction)var1.getValue(FACING)).getOpposite()), var4, ExperimentalRedstoneUtils.withFront(var5, ((Direction)var1.getValue(FACING)).getOpposite()));
      }

   }

   protected ItemStack getCloneItemStack(LevelReader var1, BlockPos var2, BlockState var3, boolean var4) {
      return new ItemStack(var3.getValue(TYPE) == PistonType.STICKY ? Blocks.STICKY_PISTON : Blocks.PISTON);
   }

   protected BlockState rotate(BlockState var1, Rotation var2) {
      return (BlockState)var1.setValue(FACING, var2.rotate((Direction)var1.getValue(FACING)));
   }

   protected BlockState mirror(BlockState var1, Mirror var2) {
      return var1.rotate(var2.getRotation((Direction)var1.getValue(FACING)));
   }

   protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> var1) {
      var1.add(FACING, TYPE, SHORT);
   }

   protected boolean isPathfindable(BlockState var1, PathComputationType var2) {
      return false;
   }

   static {
      TYPE = BlockStateProperties.PISTON_TYPE;
      SHORT = BlockStateProperties.SHORT;
      SHAPE_PLATFORM = Block.boxZ(16.0, 0.0, 4.0);
      SHAPES_SHORT = Shapes.rotateAll(Shapes.or(SHAPE_PLATFORM, Block.boxZ(4.0, 4.0, 16.0)));
      SHAPES = Shapes.rotateAll(Shapes.or(SHAPE_PLATFORM, Block.boxZ(4.0, 4.0, 20.0)));
   }
}
