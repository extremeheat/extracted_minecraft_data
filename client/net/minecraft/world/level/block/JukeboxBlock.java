package net.minecraft.world.level.block;

import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.component.DataComponents;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.Containers;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.JukeboxPlayable;
import net.minecraft.world.item.component.TypedEntityData;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.entity.JukeboxBlockEntity;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.phys.BlockHitResult;
import org.jspecify.annotations.Nullable;

public class JukeboxBlock extends BaseEntityBlock {
   public static final MapCodec<JukeboxBlock> CODEC = simpleCodec(JukeboxBlock::new);
   public static final BooleanProperty HAS_RECORD;

   public MapCodec<JukeboxBlock> codec() {
      return CODEC;
   }

   protected JukeboxBlock(final BlockBehaviour.Properties properties) {
      super(properties);
      this.registerDefaultState((BlockState)((BlockState)this.stateDefinition.any()).setValue(HAS_RECORD, false));
   }

   public void setPlacedBy(final Level level, final BlockPos pos, final BlockState state, final @Nullable LivingEntity by, final ItemStack itemStack) {
      super.setPlacedBy(level, pos, state, by, itemStack);
      TypedEntityData<BlockEntityType<?>> blockEntityData = (TypedEntityData)itemStack.get(DataComponents.BLOCK_ENTITY_DATA);
      if (blockEntityData != null && blockEntityData.contains("RecordItem")) {
         level.setBlock(pos, (BlockState)state.setValue(HAS_RECORD, true), 2);
      }

   }

   protected InteractionResult useWithoutItem(final BlockState state, final Level level, final BlockPos pos, final Player player, final BlockHitResult hitResult) {
      if ((Boolean)state.getValue(HAS_RECORD)) {
         BlockEntity var7 = level.getBlockEntity(pos);
         if (var7 instanceof JukeboxBlockEntity) {
            JukeboxBlockEntity jukebox = (JukeboxBlockEntity)var7;
            jukebox.popOutTheItem();
            return InteractionResult.SUCCESS;
         }
      }

      return InteractionResult.PASS;
   }

   protected InteractionResult useItemOn(final ItemStack itemStack, final BlockState state, final Level level, final BlockPos pos, final Player player, final InteractionHand hand, final BlockHitResult hitResult) {
      if ((Boolean)state.getValue(HAS_RECORD)) {
         return InteractionResult.TRY_WITH_EMPTY_HAND;
      } else {
         ItemStack toInsert = player.getItemInHand(hand);
         InteractionResult result = JukeboxPlayable.tryInsertIntoJukebox(level, pos, toInsert, player);
         return (InteractionResult)(!result.consumesAction() ? InteractionResult.TRY_WITH_EMPTY_HAND : result);
      }
   }

   protected void affectNeighborsAfterRemoval(final BlockState state, final ServerLevel level, final BlockPos pos, final boolean movedByPiston) {
      Containers.updateNeighboursAfterDestroy(state, level, pos);
   }

   public BlockEntity newBlockEntity(final BlockPos worldPosition, final BlockState blockState) {
      return new JukeboxBlockEntity(worldPosition, blockState);
   }

   public boolean isSignalSource(final BlockState state) {
      return true;
   }

   public int getSignal(final BlockState state, final BlockGetter level, final BlockPos pos, final Direction direction) {
      BlockEntity var6 = level.getBlockEntity(pos);
      if (var6 instanceof JukeboxBlockEntity jukebox) {
         if (jukebox.getSongPlayer().isPlaying()) {
            return 15;
         }
      }

      return 0;
   }

   protected boolean hasAnalogOutputSignal(final BlockState state) {
      return true;
   }

   protected int getAnalogOutputSignal(final BlockState state, final Level level, final BlockPos pos, final Direction direction) {
      BlockEntity var6 = level.getBlockEntity(pos);
      if (var6 instanceof JukeboxBlockEntity jukebox) {
         return jukebox.getComparatorOutput();
      } else {
         return 0;
      }
   }

   protected void createBlockStateDefinition(final StateDefinition.Builder<Block, BlockState> builder) {
      builder.add(HAS_RECORD);
   }

   public <T extends BlockEntity> @Nullable BlockEntityTicker<T> getTicker(final Level level, final BlockState blockState, final BlockEntityType<T> type) {
      return (Boolean)blockState.getValue(HAS_RECORD) ? createTickerHelper(type, BlockEntityType.JUKEBOX, JukeboxBlockEntity::tick) : null;
   }

   static {
      HAS_RECORD = BlockStateProperties.HAS_RECORD;
   }
}
