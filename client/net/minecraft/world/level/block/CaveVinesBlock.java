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

public class CaveVinesBlock extends GrowingPlantHeadBlock implements CaveVines {
   public static final MapCodec<CaveVinesBlock> CODEC = simpleCodec(CaveVinesBlock::new);
   private static final float CHANCE_OF_BERRIES_ON_GROWTH = 0.11F;

   public MapCodec<CaveVinesBlock> codec() {
      return CODEC;
   }

   public CaveVinesBlock(final BlockBehaviour.Properties properties) {
      super(properties, Direction.DOWN, SHAPE, false, 0.1);
      this.registerDefaultState((BlockState)((BlockState)((BlockState)this.stateDefinition.any()).setValue(AGE, 0)).setValue(BERRIES, false));
   }

   protected int getBlocksToGrowWhenBonemealed(final RandomSource random) {
      return 1;
   }

   protected boolean canGrowInto(final BlockState state) {
      return state.isAir();
   }

   protected Block getBodyBlock() {
      return Blocks.CAVE_VINES_PLANT;
   }

   protected BlockState updateBodyAfterConvertedFromHead(final BlockState headState, final BlockState bodyState) {
      return (BlockState)bodyState.setValue(BERRIES, (Boolean)headState.getValue(BERRIES));
   }

   protected BlockState getGrowIntoState(final BlockState growFromState, final RandomSource random) {
      return (BlockState)super.getGrowIntoState(growFromState, random).setValue(BERRIES, random.nextFloat() < 0.11F);
   }

   protected ItemStack getCloneItemStack(final LevelReader level, final BlockPos pos, final BlockState state, final boolean includeData) {
      return new ItemStack(Items.GLOW_BERRIES);
   }

   protected InteractionResult useWithoutItem(final BlockState state, final Level level, final BlockPos pos, final Player player, final BlockHitResult hitResult) {
      return CaveVines.use(player, state, level, pos);
   }

   protected void createBlockStateDefinition(final StateDefinition.Builder<Block, BlockState> builder) {
      super.createBlockStateDefinition(builder);
      builder.add(BERRIES);
   }

   public boolean isValidBonemealTarget(final LevelReader level, final BlockPos pos, final BlockState state) {
      return !(Boolean)state.getValue(BERRIES);
   }

   public boolean isBonemealSuccess(final Level level, final RandomSource random, final BlockPos pos, final BlockState state) {
      return true;
   }

   public void performBonemeal(final ServerLevel level, final RandomSource random, final BlockPos pos, final BlockState state) {
      level.setBlock(pos, (BlockState)state.setValue(BERRIES, true), 2);
   }
}
