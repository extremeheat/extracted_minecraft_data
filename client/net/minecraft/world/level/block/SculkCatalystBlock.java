package net.minecraft.world.level.block;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.util.valueproviders.IntProvider;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.entity.BlockEntityTypes;
import net.minecraft.world.level.block.entity.SculkCatalystBlockEntity;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import org.jspecify.annotations.Nullable;

public class SculkCatalystBlock extends DropExperienceEntityBlock {
   public static final BooleanProperty PULSE;

   public SculkCatalystBlock(final IntProvider xpRange, final BlockBehaviour.Properties properties) {
      super(xpRange, properties);
      this.registerDefaultState((BlockState)((BlockState)this.stateDefinition.any()).setValue(PULSE, false));
   }

   protected void createBlockStateDefinition(final StateDefinition.Builder<Block, BlockState> builder) {
      builder.add(PULSE);
   }

   protected void tick(final BlockState state, final ServerLevel level, final BlockPos pos, final RandomSource random) {
      if ((Boolean)state.getValue(PULSE)) {
         level.setBlockAndUpdate(pos, (BlockState)state.setValue(PULSE, false));
      }

   }

   public @Nullable BlockEntity newBlockEntity(final BlockPos worldPosition, final BlockState blockState) {
      return new SculkCatalystBlockEntity(worldPosition, blockState);
   }

   public <T extends BlockEntity> @Nullable BlockEntityTicker<T> getTicker(final Level level, final BlockState blockState, final BlockEntityType<T> type) {
      return level.isClientSide() ? null : createTickerHelper(type, BlockEntityTypes.SCULK_CATALYST, SculkCatalystBlockEntity::serverTick);
   }

   static {
      PULSE = BlockStateProperties.BLOOM;
   }
}
