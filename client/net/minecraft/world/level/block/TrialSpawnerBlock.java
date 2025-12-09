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

   public TrialSpawnerBlock(BlockBehaviour.Properties var1) {
      super(var1);
      this.registerDefaultState((BlockState)((BlockState)((BlockState)this.stateDefinition.any()).setValue(STATE, TrialSpawnerState.INACTIVE)).setValue(OMINOUS, false));
   }

   protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> var1) {
      var1.add(STATE, OMINOUS);
   }

   public @Nullable BlockEntity newBlockEntity(BlockPos var1, BlockState var2) {
      return new TrialSpawnerBlockEntity(var1, var2);
   }

   public <T extends BlockEntity> @Nullable BlockEntityTicker<T> getTicker(Level var1, BlockState var2, BlockEntityType<T> var3) {
      BlockEntityTicker var10000;
      if (var1 instanceof ServerLevel var4) {
         var10000 = createTickerHelper(var3, BlockEntityType.TRIAL_SPAWNER, (var1x, var2x, var3x, var4x) -> var4x.getTrialSpawner().tickServer(var4, var2x, (Boolean)var3x.getOptionalValue(BlockStateProperties.OMINOUS).orElse(false)));
      } else {
         var10000 = createTickerHelper(var3, BlockEntityType.TRIAL_SPAWNER, (var0, var1x, var2x, var3x) -> var3x.getTrialSpawner().tickClient(var0, var1x, (Boolean)var2x.getOptionalValue(BlockStateProperties.OMINOUS).orElse(false)));
      }

      return var10000;
   }

   static {
      STATE = BlockStateProperties.TRIAL_SPAWNER_STATE;
      OMINOUS = BlockStateProperties.OMINOUS;
   }
}
