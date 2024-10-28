package net.minecraft.world.level.block;

import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.phys.BlockHitResult;

public class CaveVinesBlock extends GrowingPlantHeadBlock implements BonemealableBlock, CaveVines {
   public static final MapCodec<CaveVinesBlock> CODEC = simpleCodec(CaveVinesBlock::new);
   private static final float CHANCE_OF_BERRIES_ON_GROWTH = 0.11F;

   public MapCodec<CaveVinesBlock> codec() {
      return CODEC;
   }

   public CaveVinesBlock(BlockBehaviour.Properties var1) {
      super(var1, Direction.DOWN, SHAPE, false, 0.1);
      this.registerDefaultState((BlockState)((BlockState)((BlockState)this.stateDefinition.any()).setValue(AGE, 0)).setValue(BERRIES, false));
   }

   protected int getBlocksToGrowWhenBonemealed(RandomSource var1) {
      return 1;
   }

   protected boolean canGrowInto(BlockState var1) {
      return var1.isAir();
   }

   protected Block getBodyBlock() {
      return Blocks.CAVE_VINES_PLANT;
   }

   protected BlockState updateBodyAfterConvertedFromHead(BlockState var1, BlockState var2) {
      return (BlockState)var2.setValue(BERRIES, (Boolean)var1.getValue(BERRIES));
   }

   protected BlockState getGrowIntoState(BlockState var1, RandomSource var2) {
      return (BlockState)super.getGrowIntoState(var1, var2).setValue(BERRIES, var2.nextFloat() < 0.11F);
   }

   public ItemStack getCloneItemStack(LevelReader var1, BlockPos var2, BlockState var3) {
      return new ItemStack(Items.GLOW_BERRIES);
   }

   protected InteractionResult useWithoutItem(BlockState var1, Level var2, BlockPos var3, Player var4, BlockHitResult var5) {
      return CaveVines.use(var4, var1, var2, var3);
   }

   protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> var1) {
      super.createBlockStateDefinition(var1);
      var1.add(BERRIES);
   }

   public boolean isValidBonemealTarget(LevelReader var1, BlockPos var2, BlockState var3) {
      return !(Boolean)var3.getValue(BERRIES);
   }

   public boolean isBonemealSuccess(Level var1, RandomSource var2, BlockPos var3, BlockState var4) {
      return true;
   }

   public void performBonemeal(ServerLevel var1, RandomSource var2, BlockPos var3, BlockState var4) {
      var1.setBlock(var3, (BlockState)var4.setValue(BERRIES, true), 2);
   }
}
