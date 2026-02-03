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

public class CaveVinesPlantBlock extends GrowingPlantBodyBlock implements CaveVines {
   public static final MapCodec<CaveVinesPlantBlock> CODEC = simpleCodec(CaveVinesPlantBlock::new);

   public MapCodec<CaveVinesPlantBlock> codec() {
      return CODEC;
   }

   public CaveVinesPlantBlock(final BlockBehaviour.Properties properties) {
      super(properties, Direction.DOWN, SHAPE, false);
      this.registerDefaultState((BlockState)((BlockState)this.stateDefinition.any()).setValue(BERRIES, false));
   }

   protected GrowingPlantHeadBlock getHeadBlock() {
      return (GrowingPlantHeadBlock)Blocks.CAVE_VINES;
   }

   protected BlockState updateHeadAfterConvertedFromBody(final BlockState bodyState, final BlockState headState) {
      return (BlockState)headState.setValue(BERRIES, (Boolean)bodyState.getValue(BERRIES));
   }

   protected ItemStack getCloneItemStack(final LevelReader level, final BlockPos pos, final BlockState state, final boolean includeData) {
      return new ItemStack(Items.GLOW_BERRIES);
   }

   protected InteractionResult useWithoutItem(final BlockState state, final Level level, final BlockPos pos, final Player player, final BlockHitResult hitResult) {
      return CaveVines.use(player, state, level, pos);
   }

   protected void createBlockStateDefinition(final StateDefinition.Builder<Block, BlockState> builder) {
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
