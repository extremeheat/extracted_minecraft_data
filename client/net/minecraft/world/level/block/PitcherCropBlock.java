package net.minecraft.world.level.block;

import com.mojang.serialization.MapCodec;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.monster.Ravager;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.GameRules;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.DoubleBlockHalf;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;

public class PitcherCropBlock extends DoublePlantBlock implements BonemealableBlock {
   public static final MapCodec<PitcherCropBlock> CODEC = simpleCodec(PitcherCropBlock::new);
   public static final IntegerProperty AGE;
   public static final int MAX_AGE = 4;
   private static final int DOUBLE_PLANT_AGE_INTERSECTION = 3;
   private static final int BONEMEAL_INCREASE = 1;
   private static final VoxelShape FULL_UPPER_SHAPE;
   private static final VoxelShape FULL_LOWER_SHAPE;
   private static final VoxelShape COLLISION_SHAPE_BULB;
   private static final VoxelShape COLLISION_SHAPE_CROP;
   private static final VoxelShape[] UPPER_SHAPE_BY_AGE;
   private static final VoxelShape[] LOWER_SHAPE_BY_AGE;

   public MapCodec<PitcherCropBlock> codec() {
      return CODEC;
   }

   public PitcherCropBlock(BlockBehaviour.Properties var1) {
      super(var1);
   }

   @Nullable
   public BlockState getStateForPlacement(BlockPlaceContext var1) {
      return this.defaultBlockState();
   }

   public VoxelShape getShape(BlockState var1, BlockGetter var2, BlockPos var3, CollisionContext var4) {
      return var1.getValue(HALF) == DoubleBlockHalf.UPPER ? UPPER_SHAPE_BY_AGE[Math.min(Math.abs(4 - ((Integer)var1.getValue(AGE) + 1)), UPPER_SHAPE_BY_AGE.length - 1)] : LOWER_SHAPE_BY_AGE[(Integer)var1.getValue(AGE)];
   }

   public VoxelShape getCollisionShape(BlockState var1, BlockGetter var2, BlockPos var3, CollisionContext var4) {
      if ((Integer)var1.getValue(AGE) == 0) {
         return COLLISION_SHAPE_BULB;
      } else {
         return var1.getValue(HALF) == DoubleBlockHalf.LOWER ? COLLISION_SHAPE_CROP : super.getCollisionShape(var1, var2, var3, var4);
      }
   }

   public BlockState updateShape(BlockState var1, Direction var2, BlockState var3, LevelAccessor var4, BlockPos var5, BlockPos var6) {
      if (isDouble((Integer)var1.getValue(AGE))) {
         return super.updateShape(var1, var2, var3, var4, var5, var6);
      } else {
         return var1.canSurvive(var4, var5) ? var1 : Blocks.AIR.defaultBlockState();
      }
   }

   public boolean canSurvive(BlockState var1, LevelReader var2, BlockPos var3) {
      return isLower(var1) && !sufficientLight(var2, var3) ? false : super.canSurvive(var1, var2, var3);
   }

   protected boolean mayPlaceOn(BlockState var1, BlockGetter var2, BlockPos var3) {
      return var1.is(Blocks.FARMLAND);
   }

   protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> var1) {
      var1.add(AGE);
      super.createBlockStateDefinition(var1);
   }

   public void entityInside(BlockState var1, Level var2, BlockPos var3, Entity var4) {
      if (var4 instanceof Ravager && var2.getGameRules().getBoolean(GameRules.RULE_MOBGRIEFING)) {
         var2.destroyBlock(var3, true, var4);
      }

      super.entityInside(var1, var2, var3, var4);
   }

   public boolean canBeReplaced(BlockState var1, BlockPlaceContext var2) {
      return false;
   }

   public void setPlacedBy(Level var1, BlockPos var2, BlockState var3, LivingEntity var4, ItemStack var5) {
   }

   public boolean isRandomlyTicking(BlockState var1) {
      return var1.getValue(HALF) == DoubleBlockHalf.LOWER && !this.isMaxAge(var1);
   }

   public void randomTick(BlockState var1, ServerLevel var2, BlockPos var3, RandomSource var4) {
      float var5 = CropBlock.getGrowthSpeed(this, var2, var3);
      boolean var6 = var4.nextInt((int)(25.0F / var5) + 1) == 0;
      if (var6) {
         this.grow(var2, var1, var3, 1);
      }

   }

   private void grow(ServerLevel var1, BlockState var2, BlockPos var3, int var4) {
      int var5 = Math.min((Integer)var2.getValue(AGE) + var4, 4);
      if (this.canGrow(var1, var3, var2, var5)) {
         BlockState var6 = (BlockState)var2.setValue(AGE, var5);
         var1.setBlock(var3, var6, 2);
         if (isDouble(var5)) {
            var1.setBlock(var3.above(), (BlockState)var6.setValue(HALF, DoubleBlockHalf.UPPER), 3);
         }

      }
   }

   private static boolean canGrowInto(LevelReader var0, BlockPos var1) {
      BlockState var2 = var0.getBlockState(var1);
      return var2.isAir() || var2.is(Blocks.PITCHER_CROP);
   }

   private static boolean sufficientLight(LevelReader var0, BlockPos var1) {
      return CropBlock.hasSufficientLight(var0, var1);
   }

   private static boolean isLower(BlockState var0) {
      return var0.is(Blocks.PITCHER_CROP) && var0.getValue(HALF) == DoubleBlockHalf.LOWER;
   }

   private static boolean isDouble(int var0) {
      return var0 >= 3;
   }

   private boolean canGrow(LevelReader var1, BlockPos var2, BlockState var3, int var4) {
      return !this.isMaxAge(var3) && sufficientLight(var1, var2) && (!isDouble(var4) || canGrowInto(var1, var2.above()));
   }

   private boolean isMaxAge(BlockState var1) {
      return (Integer)var1.getValue(AGE) >= 4;
   }

   @Nullable
   private PosAndState getLowerHalf(LevelReader var1, BlockPos var2, BlockState var3) {
      if (isLower(var3)) {
         return new PosAndState(var2, var3);
      } else {
         BlockPos var4 = var2.below();
         BlockState var5 = var1.getBlockState(var4);
         return isLower(var5) ? new PosAndState(var4, var5) : null;
      }
   }

   public boolean isValidBonemealTarget(LevelReader var1, BlockPos var2, BlockState var3) {
      PosAndState var4 = this.getLowerHalf(var1, var2, var3);
      return var4 == null ? false : this.canGrow(var1, var4.pos, var4.state, (Integer)var4.state.getValue(AGE) + 1);
   }

   public boolean isBonemealSuccess(Level var1, RandomSource var2, BlockPos var3, BlockState var4) {
      return true;
   }

   public void performBonemeal(ServerLevel var1, RandomSource var2, BlockPos var3, BlockState var4) {
      PosAndState var5 = this.getLowerHalf(var1, var3, var4);
      if (var5 != null) {
         this.grow(var1, var5.state, var5.pos, 1);
      }
   }

   static {
      AGE = BlockStateProperties.AGE_4;
      FULL_UPPER_SHAPE = Block.box(3.0, 0.0, 3.0, 13.0, 15.0, 13.0);
      FULL_LOWER_SHAPE = Block.box(3.0, -1.0, 3.0, 13.0, 16.0, 13.0);
      COLLISION_SHAPE_BULB = Block.box(5.0, -1.0, 5.0, 11.0, 3.0, 11.0);
      COLLISION_SHAPE_CROP = Block.box(3.0, -1.0, 3.0, 13.0, 5.0, 13.0);
      UPPER_SHAPE_BY_AGE = new VoxelShape[]{Block.box(3.0, 0.0, 3.0, 13.0, 11.0, 13.0), FULL_UPPER_SHAPE};
      LOWER_SHAPE_BY_AGE = new VoxelShape[]{COLLISION_SHAPE_BULB, Block.box(3.0, -1.0, 3.0, 13.0, 14.0, 13.0), FULL_LOWER_SHAPE, FULL_LOWER_SHAPE, FULL_LOWER_SHAPE};
   }

   static record PosAndState(BlockPos pos, BlockState state) {
      final BlockPos pos;
      final BlockState state;

      PosAndState(BlockPos var1, BlockState var2) {
         super();
         this.pos = var1;
         this.state = var2;
      }

      public BlockPos pos() {
         return this.pos;
      }

      public BlockState state() {
         return this.state;
      }
   }
}
