package net.minecraft.world.level.block;

import com.mojang.serialization.MapCodec;
import java.util.List;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.core.RegistryAccess;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.Spawner;
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

public class TrialSpawnerBlock extends BaseEntityBlock {
   public static final MapCodec<TrialSpawnerBlock> CODEC = simpleCodec(TrialSpawnerBlock::new);
   public static final EnumProperty<TrialSpawnerState> STATE = BlockStateProperties.TRIAL_SPAWNER_STATE;
   public static final BooleanProperty OMINOUS = BlockStateProperties.OMINOUS;

   @Override
   public MapCodec<TrialSpawnerBlock> codec() {
      return CODEC;
   }

   public TrialSpawnerBlock(BlockBehaviour.Properties var1) {
      super(var1);
      this.registerDefaultState(this.stateDefinition.any().setValue(STATE, TrialSpawnerState.INACTIVE).setValue(OMINOUS, Boolean.valueOf(false)));
   }

   @Override
   protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> var1) {
      var1.add(STATE, OMINOUS);
   }

   @Override
   protected RenderShape getRenderShape(BlockState var1) {
      return RenderShape.MODEL;
   }

   @Nullable
   @Override
   public BlockEntity newBlockEntity(BlockPos var1, BlockState var2) {
      return new TrialSpawnerBlockEntity(var1, var2);
   }

   @Nullable
   @Override
   public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level var1, BlockState var2, BlockEntityType<T> var3) {
      return var1 instanceof ServerLevel var4
         ? createTickerHelper(
            var3,
            BlockEntityType.TRIAL_SPAWNER,
            (var1x, var2x, var3x, var4x) -> var4x.getTrialSpawner().tickServer(var4, var2x, var3x.getOptionalValue(BlockStateProperties.OMINOUS).orElse(false))
         )
         : createTickerHelper(
            var3,
            BlockEntityType.TRIAL_SPAWNER,
            (var0, var1x, var2x, var3x) -> var3x.getTrialSpawner().tickClient(var0, var1x, var2x.getOptionalValue(BlockStateProperties.OMINOUS).orElse(false))
         );
   }

   @Override
   public void appendHoverText(ItemStack var1, @Nullable BlockGetter var2, List<Component> var3, TooltipFlag var4, @Nullable RegistryAccess var5) {
      super.appendHoverText(var1, var2, var3, var4, var5);
      Spawner.appendHoverText(var1, var3, "spawn_data");
   }
}
