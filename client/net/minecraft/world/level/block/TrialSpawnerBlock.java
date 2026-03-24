package net.minecraft.world.level.block;

import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.entity.TrialSpawnerBlockEntity;
import net.minecraft.world.level.block.entity.trialspawner.TrialSpawnerState;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import org.jspecify.annotations.Nullable;

public class TrialSpawnerBlock extends BaseEntityBlock {
   public static final MapCodec<TrialSpawnerBlock> CODEC = simpleCodec(TrialSpawnerBlock::new);
   public static final EnumProperty<TrialSpawnerState> STATE;
   public static final BooleanProperty OMINOUS;

   public MapCodec<TrialSpawnerBlock> codec() {
      return CODEC;
   }

   public TrialSpawnerBlock(final BlockBehaviour.Properties properties) {
      super(properties);
      this.registerDefaultState((BlockState)((BlockState)((BlockState)this.stateDefinition.any()).setValue(STATE, TrialSpawnerState.INACTIVE)).setValue(OMINOUS, false));
   }

   protected void createBlockStateDefinition(final StateDefinition.Builder<Block, BlockState> builder) {
      builder.add(STATE, OMINOUS);
   }

   public @Nullable BlockEntity newBlockEntity(final BlockPos worldPosition, final BlockState blockState) {
      return new TrialSpawnerBlockEntity(worldPosition, blockState);
   }

   public <T extends BlockEntity> @Nullable BlockEntityTicker<T> getTicker(final Level level, final BlockState blockState, final BlockEntityType<T> type) {
      BlockEntityTicker var10000;
      if (level instanceof ServerLevel serverLevel) {
         var10000 = createTickerHelper(type, BlockEntityType.TRIAL_SPAWNER, (innerLevel, pos, state, entity) -> entity.getTrialSpawner().tickServer(serverLevel, pos, (Boolean)state.getOptionalValue(BlockStateProperties.OMINOUS).orElse(false)));
      } else {
         var10000 = createTickerHelper(type, BlockEntityType.TRIAL_SPAWNER, (innerLevel, pos, state, entity) -> entity.getTrialSpawner().tickClient(innerLevel, pos, (Boolean)state.getOptionalValue(BlockStateProperties.OMINOUS).orElse(false)));
      }

      return var10000;
   }

   static {
      STATE = BlockStateProperties.TRIAL_SPAWNER_STATE;
      OMINOUS = BlockStateProperties.OMINOUS;
   }
}
