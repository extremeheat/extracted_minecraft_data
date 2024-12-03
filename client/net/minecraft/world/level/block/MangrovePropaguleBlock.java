package net.minecraft.world.level.block;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.ScheduledTickAccess;
import net.minecraft.world.level.block.grower.TreeGrower;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;

public class MangrovePropaguleBlock extends SaplingBlock implements SimpleWaterloggedBlock {
   public static final MapCodec<MangrovePropaguleBlock> CODEC = RecordCodecBuilder.mapCodec((var0) -> var0.group(TreeGrower.CODEC.fieldOf("tree").forGetter((var0x) -> var0x.treeGrower), propertiesCodec()).apply(var0, MangrovePropaguleBlock::new));
   public static final IntegerProperty AGE;
   public static final int MAX_AGE = 4;
   private static final VoxelShape[] SHAPE_PER_AGE;
   private static final BooleanProperty WATERLOGGED;
   public static final BooleanProperty HANGING;

   public MapCodec<MangrovePropaguleBlock> codec() {
      return CODEC;
   }

   public MangrovePropaguleBlock(TreeGrower var1, BlockBehaviour.Properties var2) {
      super(var1, var2);
      this.registerDefaultState((BlockState)((BlockState)((BlockState)((BlockState)((BlockState)this.stateDefinition.any()).setValue(STAGE, 0)).setValue(AGE, 0)).setValue(WATERLOGGED, false)).setValue(HANGING, false));
   }

   protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> var1) {
      var1.add(STAGE).add(AGE).add(WATERLOGGED).add(HANGING);
   }

   protected boolean mayPlaceOn(BlockState var1, BlockGetter var2, BlockPos var3) {
      return super.mayPlaceOn(var1, var2, var3) || var1.is(Blocks.CLAY);
   }

   @Nullable
   public BlockState getStateForPlacement(BlockPlaceContext var1) {
      FluidState var2 = var1.getLevel().getFluidState(var1.getClickedPos());
      boolean var3 = var2.getType() == Fluids.WATER;
      return (BlockState)((BlockState)super.getStateForPlacement(var1).setValue(WATERLOGGED, var3)).setValue(AGE, 4);
   }

   protected VoxelShape getShape(BlockState var1, BlockGetter var2, BlockPos var3, CollisionContext var4) {
      Vec3 var5 = var1.getOffset(var3);
      VoxelShape var6;
      if (!(Boolean)var1.getValue(HANGING)) {
         var6 = SHAPE_PER_AGE[4];
      } else {
         var6 = SHAPE_PER_AGE[(Integer)var1.getValue(AGE)];
      }

      return var6.move(var5.x, var5.y, var5.z);
   }

   protected boolean canSurvive(BlockState var1, LevelReader var2, BlockPos var3) {
      return isHanging(var1) ? var2.getBlockState(var3.above()).is(Blocks.MANGROVE_LEAVES) : super.canSurvive(var1, var2, var3);
   }

   protected BlockState updateShape(BlockState var1, LevelReader var2, ScheduledTickAccess var3, BlockPos var4, Direction var5, BlockPos var6, BlockState var7, RandomSource var8) {
      if ((Boolean)var1.getValue(WATERLOGGED)) {
         var3.scheduleTick(var4, (Fluid)Fluids.WATER, Fluids.WATER.getTickDelay(var2));
      }

      return var5 == Direction.UP && !var1.canSurvive(var2, var4) ? Blocks.AIR.defaultBlockState() : super.updateShape(var1, var2, var3, var4, var5, var6, var7, var8);
   }

   protected FluidState getFluidState(BlockState var1) {
      return (Boolean)var1.getValue(WATERLOGGED) ? Fluids.WATER.getSource(false) : super.getFluidState(var1);
   }

   protected void randomTick(BlockState var1, ServerLevel var2, BlockPos var3, RandomSource var4) {
      if (!isHanging(var1)) {
         if (var4.nextInt(7) == 0) {
            this.advanceTree(var2, var3, var1, var4);
         }

      } else {
         if (!isFullyGrown(var1)) {
            var2.setBlock(var3, (BlockState)var1.cycle(AGE), 2);
         }

      }
   }

   public boolean isValidBonemealTarget(LevelReader var1, BlockPos var2, BlockState var3) {
      return !isHanging(var3) || !isFullyGrown(var3);
   }

   public boolean isBonemealSuccess(Level var1, RandomSource var2, BlockPos var3, BlockState var4) {
      return isHanging(var4) ? !isFullyGrown(var4) : super.isBonemealSuccess(var1, var2, var3, var4);
   }

   public void performBonemeal(ServerLevel var1, RandomSource var2, BlockPos var3, BlockState var4) {
      if (isHanging(var4) && !isFullyGrown(var4)) {
         var1.setBlock(var3, (BlockState)var4.cycle(AGE), 2);
      } else {
         super.performBonemeal(var1, var2, var3, var4);
      }

   }

   private static boolean isHanging(BlockState var0) {
      return (Boolean)var0.getValue(HANGING);
   }

   private static boolean isFullyGrown(BlockState var0) {
      return (Integer)var0.getValue(AGE) == 4;
   }

   public static BlockState createNewHangingPropagule() {
      return createNewHangingPropagule(0);
   }

   public static BlockState createNewHangingPropagule(int var0) {
      return (BlockState)((BlockState)Blocks.MANGROVE_PROPAGULE.defaultBlockState().setValue(HANGING, true)).setValue(AGE, var0);
   }

   static {
      AGE = BlockStateProperties.AGE_4;
      SHAPE_PER_AGE = new VoxelShape[]{Block.box(7.0, 13.0, 7.0, 9.0, 16.0, 9.0), Block.box(7.0, 10.0, 7.0, 9.0, 16.0, 9.0), Block.box(7.0, 7.0, 7.0, 9.0, 16.0, 9.0), Block.box(7.0, 3.0, 7.0, 9.0, 16.0, 9.0), Block.box(7.0, 0.0, 7.0, 9.0, 16.0, 9.0)};
      WATERLOGGED = BlockStateProperties.WATERLOGGED;
      HANGING = BlockStateProperties.HANGING;
   }
}
