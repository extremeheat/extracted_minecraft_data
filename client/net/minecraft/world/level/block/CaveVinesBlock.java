package net.minecraft.world.level.block;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.phys.BlockHitResult;

public class CaveVinesBlock extends GrowingPlantHeadBlock implements BonemealableBlock, CaveVines {
   private static final float CHANCE_OF_BERRIES_ON_GROWTH = 0.11F;

   public CaveVinesBlock(BlockBehaviour.Properties var1) {
      super(var1, Direction.DOWN, SHAPE, false, 0.1);
      this.registerDefaultState(this.stateDefinition.any().setValue(AGE, Integer.valueOf(0)).setValue(BERRIES, Boolean.valueOf(false)));
   }

   @Override
   protected int getBlocksToGrowWhenBonemealed(RandomSource var1) {
      return 1;
   }

   @Override
   protected boolean canGrowInto(BlockState var1) {
      return var1.isAir();
   }

   @Override
   protected Block getBodyBlock() {
      return Blocks.CAVE_VINES_PLANT;
   }

   @Override
   protected BlockState updateBodyAfterConvertedFromHead(BlockState var1, BlockState var2) {
      return var2.setValue(BERRIES, var1.getValue(BERRIES));
   }

   @Override
   protected BlockState getGrowIntoState(BlockState var1, RandomSource var2) {
      return super.getGrowIntoState(var1, var2).setValue(BERRIES, Boolean.valueOf(var2.nextFloat() < 0.11F));
   }

   @Override
   public ItemStack getCloneItemStack(BlockGetter var1, BlockPos var2, BlockState var3) {
      return new ItemStack(Items.GLOW_BERRIES);
   }

   @Override
   public InteractionResult use(BlockState var1, Level var2, BlockPos var3, Player var4, InteractionHand var5, BlockHitResult var6) {
      return CaveVines.use(var1, var2, var3);
   }

   @Override
   protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> var1) {
      super.createBlockStateDefinition(var1);
      var1.add(BERRIES);
   }

   @Override
   public boolean isValidBonemealTarget(BlockGetter var1, BlockPos var2, BlockState var3, boolean var4) {
      return !var3.getValue(BERRIES);
   }

   @Override
   public boolean isBonemealSuccess(Level var1, RandomSource var2, BlockPos var3, BlockState var4) {
      return true;
   }

   @Override
   public void performBonemeal(ServerLevel var1, RandomSource var2, BlockPos var3, BlockState var4) {
      var1.setBlock(var3, var4.setValue(BERRIES, Boolean.valueOf(true)), 2);
   }
}
