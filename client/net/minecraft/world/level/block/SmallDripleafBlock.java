package net.minecraft.world.level.block;

import com.mojang.serialization.MapCodec;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.ScheduledTickAccess;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.DoubleBlockHalf;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;

public class SmallDripleafBlock extends DoublePlantBlock implements BonemealableBlock, SimpleWaterloggedBlock {
   public static final MapCodec<SmallDripleafBlock> CODEC = simpleCodec(SmallDripleafBlock::new);
   private static final BooleanProperty WATERLOGGED;
   public static final EnumProperty<Direction> FACING;
   protected static final float AABB_OFFSET = 6.0F;
   protected static final VoxelShape SHAPE;

   public MapCodec<SmallDripleafBlock> codec() {
      return CODEC;
   }

   public SmallDripleafBlock(BlockBehaviour.Properties var1) {
      super(var1);
      this.registerDefaultState((BlockState)((BlockState)((BlockState)((BlockState)this.stateDefinition.any()).setValue(HALF, DoubleBlockHalf.LOWER)).setValue(WATERLOGGED, false)).setValue(FACING, Direction.NORTH));
   }

   protected VoxelShape getShape(BlockState var1, BlockGetter var2, BlockPos var3, CollisionContext var4) {
      return SHAPE;
   }

   protected boolean mayPlaceOn(BlockState var1, BlockGetter var2, BlockPos var3) {
      return var1.is(BlockTags.SMALL_DRIPLEAF_PLACEABLE) || var2.getFluidState(var3.above()).isSourceOfType(Fluids.WATER) && super.mayPlaceOn(var1, var2, var3);
   }

   @Nullable
   public BlockState getStateForPlacement(BlockPlaceContext var1) {
      BlockState var2 = super.getStateForPlacement(var1);
      return var2 != null ? copyWaterloggedFrom(var1.getLevel(), var1.getClickedPos(), (BlockState)var2.setValue(FACING, var1.getHorizontalDirection().getOpposite())) : null;
   }

   public void setPlacedBy(Level var1, BlockPos var2, BlockState var3, LivingEntity var4, ItemStack var5) {
      if (!var1.isClientSide()) {
         BlockPos var6 = var2.above();
         BlockState var7 = DoublePlantBlock.copyWaterloggedFrom(var1, var6, (BlockState)((BlockState)this.defaultBlockState().setValue(HALF, DoubleBlockHalf.UPPER)).setValue(FACING, (Direction)var3.getValue(FACING)));
         var1.setBlock(var6, var7, 3);
      }

   }

   protected FluidState getFluidState(BlockState var1) {
      return (Boolean)var1.getValue(WATERLOGGED) ? Fluids.WATER.getSource(false) : super.getFluidState(var1);
   }

   protected boolean canSurvive(BlockState var1, LevelReader var2, BlockPos var3) {
      if (var1.getValue(HALF) == DoubleBlockHalf.UPPER) {
         return super.canSurvive(var1, var2, var3);
      } else {
         BlockPos var4 = var3.below();
         BlockState var5 = var2.getBlockState(var4);
         return this.mayPlaceOn(var5, var2, var4);
      }
   }

   protected BlockState updateShape(BlockState var1, LevelReader var2, ScheduledTickAccess var3, BlockPos var4, Direction var5, BlockPos var6, BlockState var7, RandomSource var8) {
      if ((Boolean)var1.getValue(WATERLOGGED)) {
         var3.scheduleTick(var4, (Fluid)Fluids.WATER, Fluids.WATER.getTickDelay(var2));
      }

      return super.updateShape(var1, var2, var3, var4, var5, var6, var7, var8);
   }

   protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> var1) {
      var1.add(HALF, WATERLOGGED, FACING);
   }

   public boolean isValidBonemealTarget(LevelReader var1, BlockPos var2, BlockState var3) {
      return true;
   }

   public boolean isBonemealSuccess(Level var1, RandomSource var2, BlockPos var3, BlockState var4) {
      return true;
   }

   public void performBonemeal(ServerLevel var1, RandomSource var2, BlockPos var3, BlockState var4) {
      BlockPos var5;
      if (var4.getValue(DoublePlantBlock.HALF) == DoubleBlockHalf.LOWER) {
         var5 = var3.above();
         var1.setBlock(var5, var1.getFluidState(var5).createLegacyBlock(), 18);
         BigDripleafBlock.placeWithRandomHeight(var1, var2, var3, (Direction)var4.getValue(FACING));
      } else {
         var5 = var3.below();
         this.performBonemeal(var1, var2, var5, var1.getBlockState(var5));
      }

   }

   protected BlockState rotate(BlockState var1, Rotation var2) {
      return (BlockState)var1.setValue(FACING, var2.rotate((Direction)var1.getValue(FACING)));
   }

   protected BlockState mirror(BlockState var1, Mirror var2) {
      return var1.rotate(var2.getRotation((Direction)var1.getValue(FACING)));
   }

   protected float getMaxVerticalOffset() {
      return 0.1F;
   }

   static {
      WATERLOGGED = BlockStateProperties.WATERLOGGED;
      FACING = BlockStateProperties.HORIZONTAL_FACING;
      SHAPE = Block.box(2.0, 0.0, 2.0, 14.0, 13.0, 14.0);
   }
}
