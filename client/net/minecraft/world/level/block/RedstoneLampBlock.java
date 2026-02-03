package net.minecraft.world.level.block;

import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.redstone.Orientation;
import org.jspecify.annotations.Nullable;

public class RedstoneLampBlock extends Block {
   public static final MapCodec<RedstoneLampBlock> CODEC = simpleCodec(RedstoneLampBlock::new);
   public static final BooleanProperty LIT;

   public MapCodec<RedstoneLampBlock> codec() {
      return CODEC;
   }

   public RedstoneLampBlock(final BlockBehaviour.Properties properties) {
      super(properties);
      this.registerDefaultState((BlockState)this.defaultBlockState().setValue(LIT, false));
   }

   public @Nullable BlockState getStateForPlacement(final BlockPlaceContext context) {
      return (BlockState)this.defaultBlockState().setValue(LIT, context.getLevel().hasNeighborSignal(context.getClickedPos()));
   }

   protected void neighborChanged(final BlockState state, final Level level, final BlockPos pos, final Block block, final @Nullable Orientation orientation, final boolean movedByPiston) {
      if (!level.isClientSide()) {
         boolean isLit = (Boolean)state.getValue(LIT);
         if (isLit != level.hasNeighborSignal(pos)) {
            if (isLit) {
               level.scheduleTick(pos, this, 4);
            } else {
               level.setBlock(pos, (BlockState)state.cycle(LIT), 2);
            }
         }

      }
   }

   protected void tick(final BlockState state, final ServerLevel level, final BlockPos pos, final RandomSource random) {
      if ((Boolean)state.getValue(LIT) && !level.hasNeighborSignal(pos)) {
         level.setBlock(pos, (BlockState)state.cycle(LIT), 2);
      }

   }

   protected void createBlockStateDefinition(final StateDefinition.Builder<Block, BlockState> builder) {
      builder.add(LIT);
   }

   static {
      LIT = RedstoneTorchBlock.LIT;
   }
}
