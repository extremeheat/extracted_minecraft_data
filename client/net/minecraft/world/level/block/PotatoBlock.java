package net.minecraft.world.level.block;

import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.Nullable;

public class PotatoBlock extends CropBlock {
   public static final MapCodec<PotatoBlock> CODEC = simpleCodec(PotatoBlock::new);
   public static final IntegerProperty TATER_BOOST = IntegerProperty.create("tater_boost", 0, 2);
   private static final VoxelShape[] SHAPE_BY_AGE = new VoxelShape[]{
      Block.box(0.0, 0.0, 0.0, 16.0, 2.0, 16.0),
      Block.box(0.0, 0.0, 0.0, 16.0, 3.0, 16.0),
      Block.box(0.0, 0.0, 0.0, 16.0, 4.0, 16.0),
      Block.box(0.0, 0.0, 0.0, 16.0, 5.0, 16.0),
      Block.box(0.0, 0.0, 0.0, 16.0, 6.0, 16.0),
      Block.box(0.0, 0.0, 0.0, 16.0, 7.0, 16.0),
      Block.box(0.0, 0.0, 0.0, 16.0, 8.0, 16.0),
      Block.box(0.0, 0.0, 0.0, 16.0, 9.0, 16.0)
   };

   @Override
   public MapCodec<PotatoBlock> codec() {
      return CODEC;
   }

   public PotatoBlock(BlockBehaviour.Properties var1) {
      super(var1);
   }

   @Override
   protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> var1) {
      super.createBlockStateDefinition(var1);
      var1.add(TATER_BOOST);
   }

   @Nullable
   @Override
   public BlockState getStateForPlacement(BlockPlaceContext var1) {
      Level var2 = var1.getLevel();
      BlockPos var3 = var1.getClickedPos().step(Direction.DOWN);
      BlockState var4 = var2.getBlockState(var3);
      BlockState var5 = super.getStateForPlacement(var1);
      return var5 == null ? null : withCorrectTaterBoost(var5, var4);
   }

   @Override
   protected ItemLike getBaseSeedId() {
      return Items.POTATO;
   }

   @Override
   protected VoxelShape getShape(BlockState var1, BlockGetter var2, BlockPos var3, CollisionContext var4) {
      return SHAPE_BY_AGE[this.getAge(var1)];
   }

   @Override
   protected boolean mayPlaceOn(BlockState var1, BlockGetter var2, BlockPos var3) {
      return var1.is(BlockTags.GROWS_POTATOES);
   }

   @Override
   protected void neighborChanged(BlockState var1, Level var2, BlockPos var3, Block var4, BlockPos var5, boolean var6) {
      super.neighborChanged(var1, var2, var3, var4, var5, var6);
      if (var3.step(Direction.DOWN).equals(var5)) {
         BlockState var7 = var2.getBlockState(var5);
         BlockState var8 = var2.getBlockState(var3);
         if (var8.getBlock() instanceof PotatoBlock) {
            var2.setBlock(var3, withCorrectTaterBoost(var8, var7), 3);
         }
      }
   }

   @Override
   public BlockState getStateForAge(int var1, BlockState var2) {
      return var2.setValue(this.getAgeProperty(), Integer.valueOf(var1));
   }

   public static BlockState withCorrectTaterBoost(BlockState var0, BlockState var1) {
      return var0.setValue(TATER_BOOST, Integer.valueOf(calculateTaterBoost(var1)));
   }

   @Override
   protected boolean canSurvive(BlockState var1, LevelReader var2, BlockPos var3) {
      if (var2.isPotato()) {
         BlockPos var4 = var3.below();
         return this.mayPlaceOn(var2.getBlockState(var4), var2, var4);
      } else {
         return super.canSurvive(var1, var2, var3);
      }
   }

   private static int calculateTaterBoost(BlockState var0) {
      if (var0.is(Blocks.PEELGRASS_BLOCK)) {
         return 1;
      } else {
         return var0.is(Blocks.CORRUPTED_PEELGRASS_BLOCK) ? 2 : 0;
      }
   }
}
