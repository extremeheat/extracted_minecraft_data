package net.minecraft.world.level.block;

import java.util.Random;
import java.util.function.Supplier;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.Mth;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;

public class StemBlock extends BushBlock implements BonemealableBlock {
   public static final int MAX_AGE = 7;
   public static final IntegerProperty AGE;
   protected static final float AABB_OFFSET = 1.0F;
   protected static final VoxelShape[] SHAPE_BY_AGE;
   private final StemGrownBlock fruit;
   private final Supplier<Item> seedSupplier;

   protected StemBlock(StemGrownBlock var1, Supplier<Item> var2, BlockBehaviour.Properties var3) {
      super(var3);
      this.fruit = var1;
      this.seedSupplier = var2;
      this.registerDefaultState((BlockState)((BlockState)this.stateDefinition.any()).setValue(AGE, 0));
   }

   public VoxelShape getShape(BlockState var1, BlockGetter var2, BlockPos var3, CollisionContext var4) {
      return SHAPE_BY_AGE[(Integer)var1.getValue(AGE)];
   }

   protected boolean mayPlaceOn(BlockState var1, BlockGetter var2, BlockPos var3) {
      return var1.is(Blocks.FARMLAND);
   }

   public void randomTick(BlockState var1, ServerLevel var2, BlockPos var3, Random var4) {
      if (var2.getRawBrightness(var3, 0) >= 9) {
         float var5 = CropBlock.getGrowthSpeed(this, var2, var3);
         if (var4.nextInt((int)(25.0F / var5) + 1) == 0) {
            int var6 = (Integer)var1.getValue(AGE);
            if (var6 < 7) {
               var1 = (BlockState)var1.setValue(AGE, var6 + 1);
               var2.setBlock(var3, var1, 2);
            } else {
               Direction var7 = Direction.Plane.HORIZONTAL.getRandomDirection(var4);
               BlockPos var8 = var3.relative(var7);
               BlockState var9 = var2.getBlockState(var8.below());
               if (var2.getBlockState(var8).isAir() && (var9.is(Blocks.FARMLAND) || var9.is(BlockTags.DIRT))) {
                  var2.setBlockAndUpdate(var8, this.fruit.defaultBlockState());
                  var2.setBlockAndUpdate(var3, (BlockState)this.fruit.getAttachedStem().defaultBlockState().setValue(HorizontalDirectionalBlock.FACING, var7));
               }
            }
         }

      }
   }

   public ItemStack getCloneItemStack(BlockGetter var1, BlockPos var2, BlockState var3) {
      return new ItemStack((ItemLike)this.seedSupplier.get());
   }

   public boolean isValidBonemealTarget(BlockGetter var1, BlockPos var2, BlockState var3, boolean var4) {
      return (Integer)var3.getValue(AGE) != 7;
   }

   public boolean isBonemealSuccess(Level var1, Random var2, BlockPos var3, BlockState var4) {
      return true;
   }

   public void performBonemeal(ServerLevel var1, Random var2, BlockPos var3, BlockState var4) {
      int var5 = Math.min(7, (Integer)var4.getValue(AGE) + Mth.nextInt(var1.random, 2, 5));
      BlockState var6 = (BlockState)var4.setValue(AGE, var5);
      var1.setBlock(var3, var6, 2);
      if (var5 == 7) {
         var6.randomTick(var1, var3, var1.random);
      }

   }

   protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> var1) {
      var1.add(AGE);
   }

   public StemGrownBlock getFruit() {
      return this.fruit;
   }

   static {
      AGE = BlockStateProperties.AGE_7;
      SHAPE_BY_AGE = new VoxelShape[]{Block.box(7.0D, 0.0D, 7.0D, 9.0D, 2.0D, 9.0D), Block.box(7.0D, 0.0D, 7.0D, 9.0D, 4.0D, 9.0D), Block.box(7.0D, 0.0D, 7.0D, 9.0D, 6.0D, 9.0D), Block.box(7.0D, 0.0D, 7.0D, 9.0D, 8.0D, 9.0D), Block.box(7.0D, 0.0D, 7.0D, 9.0D, 10.0D, 9.0D), Block.box(7.0D, 0.0D, 7.0D, 9.0D, 12.0D, 9.0D), Block.box(7.0D, 0.0D, 7.0D, 9.0D, 14.0D, 9.0D), Block.box(7.0D, 0.0D, 7.0D, 9.0D, 16.0D, 9.0D)};
   }
}
