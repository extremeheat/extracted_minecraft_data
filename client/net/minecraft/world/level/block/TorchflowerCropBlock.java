package net.minecraft.world.level.block;

import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.Items;
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

public class TorchflowerCropBlock extends CropBlock {
   public static final MapCodec<TorchflowerCropBlock> CODEC = simpleCodec(TorchflowerCropBlock::new);
   public static final int MAX_AGE = 2;
   public static final IntegerProperty AGE = BlockStateProperties.AGE_1;
   private static final float AABB_OFFSET = 3.0F;
   private static final VoxelShape[] SHAPE_BY_AGE = new VoxelShape[]{Block.box(5.0, 0.0, 5.0, 11.0, 6.0, 11.0), Block.box(5.0, 0.0, 5.0, 11.0, 10.0, 11.0)};
   private static final int BONEMEAL_INCREASE = 1;

   @Override
   public MapCodec<TorchflowerCropBlock> codec() {
      return CODEC;
   }

   public TorchflowerCropBlock(BlockBehaviour.Properties var1) {
      super(var1);
   }

   @Override
   protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> var1) {
      var1.add(AGE);
   }

   @Override
   public VoxelShape getShape(BlockState var1, BlockGetter var2, BlockPos var3, CollisionContext var4) {
      return SHAPE_BY_AGE[this.getAge(var1)];
   }

   @Override
   protected IntegerProperty getAgeProperty() {
      return AGE;
   }

   @Override
   public int getMaxAge() {
      return 2;
   }

   @Override
   protected ItemLike getBaseSeedId() {
      return Items.TORCHFLOWER_SEEDS;
   }

   @Override
   public BlockState getStateForAge(int var1, BlockState var2) {
      return var1 == 2 ? Blocks.TORCHFLOWER.defaultBlockState() : super.getStateForAge(var1, var2);
   }

   @Override
   public void randomTick(BlockState var1, ServerLevel var2, BlockPos var3, RandomSource var4) {
      if (var4.nextInt(3) != 0) {
         super.randomTick(var1, var2, var3, var4);
      }
   }

   @Override
   protected int getBonemealAgeIncrease(Level var1) {
      return 1;
   }
}
