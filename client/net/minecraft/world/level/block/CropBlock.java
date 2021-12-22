package net.minecraft.world.level.block;

import java.util.Random;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.monster.Ravager;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.GameRules;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;

public class CropBlock extends BushBlock implements BonemealableBlock {
   public static final int MAX_AGE = 7;
   public static final IntegerProperty AGE;
   private static final VoxelShape[] SHAPE_BY_AGE;

   protected CropBlock(BlockBehaviour.Properties var1) {
      super(var1);
      this.registerDefaultState((BlockState)((BlockState)this.stateDefinition.any()).setValue(this.getAgeProperty(), 0));
   }

   public VoxelShape getShape(BlockState var1, BlockGetter var2, BlockPos var3, CollisionContext var4) {
      return SHAPE_BY_AGE[(Integer)var1.getValue(this.getAgeProperty())];
   }

   protected boolean mayPlaceOn(BlockState var1, BlockGetter var2, BlockPos var3) {
      return var1.is(Blocks.FARMLAND);
   }

   public IntegerProperty getAgeProperty() {
      return AGE;
   }

   public int getMaxAge() {
      return 7;
   }

   protected int getAge(BlockState var1) {
      return (Integer)var1.getValue(this.getAgeProperty());
   }

   public BlockState getStateForAge(int var1) {
      return (BlockState)this.defaultBlockState().setValue(this.getAgeProperty(), var1);
   }

   public boolean isMaxAge(BlockState var1) {
      return (Integer)var1.getValue(this.getAgeProperty()) >= this.getMaxAge();
   }

   public boolean isRandomlyTicking(BlockState var1) {
      return !this.isMaxAge(var1);
   }

   public void randomTick(BlockState var1, ServerLevel var2, BlockPos var3, Random var4) {
      if (var2.getRawBrightness(var3, 0) >= 9) {
         int var5 = this.getAge(var1);
         if (var5 < this.getMaxAge()) {
            float var6 = getGrowthSpeed(this, var2, var3);
            if (var4.nextInt((int)(25.0F / var6) + 1) == 0) {
               var2.setBlock(var3, this.getStateForAge(var5 + 1), 2);
            }
         }
      }

   }

   public void growCrops(Level var1, BlockPos var2, BlockState var3) {
      int var4 = this.getAge(var3) + this.getBonemealAgeIncrease(var1);
      int var5 = this.getMaxAge();
      if (var4 > var5) {
         var4 = var5;
      }

      var1.setBlock(var2, this.getStateForAge(var4), 2);
   }

   protected int getBonemealAgeIncrease(Level var1) {
      return Mth.nextInt(var1.random, 2, 5);
   }

   protected static float getGrowthSpeed(Block var0, BlockGetter var1, BlockPos var2) {
      float var3 = 1.0F;
      BlockPos var4 = var2.below();

      for(int var5 = -1; var5 <= 1; ++var5) {
         for(int var6 = -1; var6 <= 1; ++var6) {
            float var7 = 0.0F;
            BlockState var8 = var1.getBlockState(var4.offset(var5, 0, var6));
            if (var8.is(Blocks.FARMLAND)) {
               var7 = 1.0F;
               if ((Integer)var8.getValue(FarmBlock.MOISTURE) > 0) {
                  var7 = 3.0F;
               }
            }

            if (var5 != 0 || var6 != 0) {
               var7 /= 4.0F;
            }

            var3 += var7;
         }
      }

      BlockPos var12 = var2.north();
      BlockPos var13 = var2.south();
      BlockPos var15 = var2.west();
      BlockPos var14 = var2.east();
      boolean var9 = var1.getBlockState(var15).is(var0) || var1.getBlockState(var14).is(var0);
      boolean var10 = var1.getBlockState(var12).is(var0) || var1.getBlockState(var13).is(var0);
      if (var9 && var10) {
         var3 /= 2.0F;
      } else {
         boolean var11 = var1.getBlockState(var15.north()).is(var0) || var1.getBlockState(var14.north()).is(var0) || var1.getBlockState(var14.south()).is(var0) || var1.getBlockState(var15.south()).is(var0);
         if (var11) {
            var3 /= 2.0F;
         }
      }

      return var3;
   }

   public boolean canSurvive(BlockState var1, LevelReader var2, BlockPos var3) {
      return (var2.getRawBrightness(var3, 0) >= 8 || var2.canSeeSky(var3)) && super.canSurvive(var1, var2, var3);
   }

   public void entityInside(BlockState var1, Level var2, BlockPos var3, Entity var4) {
      if (var4 instanceof Ravager && var2.getGameRules().getBoolean(GameRules.RULE_MOBGRIEFING)) {
         var2.destroyBlock(var3, true, var4);
      }

      super.entityInside(var1, var2, var3, var4);
   }

   protected ItemLike getBaseSeedId() {
      return Items.WHEAT_SEEDS;
   }

   public ItemStack getCloneItemStack(BlockGetter var1, BlockPos var2, BlockState var3) {
      return new ItemStack(this.getBaseSeedId());
   }

   public boolean isValidBonemealTarget(BlockGetter var1, BlockPos var2, BlockState var3, boolean var4) {
      return !this.isMaxAge(var3);
   }

   public boolean isBonemealSuccess(Level var1, Random var2, BlockPos var3, BlockState var4) {
      return true;
   }

   public void performBonemeal(ServerLevel var1, Random var2, BlockPos var3, BlockState var4) {
      this.growCrops(var1, var3, var4);
   }

   protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> var1) {
      var1.add(AGE);
   }

   static {
      AGE = BlockStateProperties.AGE_7;
      SHAPE_BY_AGE = new VoxelShape[]{Block.box(0.0D, 0.0D, 0.0D, 16.0D, 2.0D, 16.0D), Block.box(0.0D, 0.0D, 0.0D, 16.0D, 4.0D, 16.0D), Block.box(0.0D, 0.0D, 0.0D, 16.0D, 6.0D, 16.0D), Block.box(0.0D, 0.0D, 0.0D, 16.0D, 8.0D, 16.0D), Block.box(0.0D, 0.0D, 0.0D, 16.0D, 10.0D, 16.0D), Block.box(0.0D, 0.0D, 0.0D, 16.0D, 12.0D, 16.0D), Block.box(0.0D, 0.0D, 0.0D, 16.0D, 14.0D, 16.0D), Block.box(0.0D, 0.0D, 0.0D, 16.0D, 16.0D, 16.0D)};
   }
}
