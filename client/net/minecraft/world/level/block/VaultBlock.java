package net.minecraft.world.level.block;

import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.entity.vault.VaultBlockEntity;
import net.minecraft.world.level.block.entity.vault.VaultState;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.phys.BlockHitResult;
import org.jspecify.annotations.Nullable;

public class VaultBlock extends BaseEntityBlock {
   public static final MapCodec<VaultBlock> CODEC = simpleCodec(VaultBlock::new);
   public static final Property<VaultState> STATE;
   public static final EnumProperty<Direction> FACING;
   public static final BooleanProperty OMINOUS;

   public MapCodec<VaultBlock> codec() {
      return CODEC;
   }

   public VaultBlock(final BlockBehaviour.Properties properties) {
      super(properties);
      this.registerDefaultState((BlockState)((BlockState)((BlockState)((BlockState)this.stateDefinition.any()).setValue(FACING, Direction.NORTH)).setValue(STATE, VaultState.INACTIVE)).setValue(OMINOUS, false));
   }

   public InteractionResult useItemOn(final ItemStack itemStack, final BlockState state, final Level level, final BlockPos pos, final Player player, final InteractionHand hand, final BlockHitResult hitResult) {
      if (!itemStack.isEmpty() && state.getValue(STATE) == VaultState.ACTIVE) {
         if (level instanceof ServerLevel) {
            ServerLevel serverLevel = (ServerLevel)level;
            BlockEntity var10 = serverLevel.getBlockEntity(pos);
            if (!(var10 instanceof VaultBlockEntity)) {
               return InteractionResult.TRY_WITH_EMPTY_HAND;
            }

            VaultBlockEntity vault = (VaultBlockEntity)var10;
            VaultBlockEntity.Server.tryInsertKey(serverLevel, pos, state, vault.getConfig(), vault.getServerData(), vault.getSharedData(), player, itemStack);
         }

         return InteractionResult.SUCCESS_SERVER;
      } else {
         return InteractionResult.TRY_WITH_EMPTY_HAND;
      }
   }

   public @Nullable BlockEntity newBlockEntity(final BlockPos pos, final BlockState state) {
      return new VaultBlockEntity(pos, state);
   }

   protected void createBlockStateDefinition(final StateDefinition.Builder<Block, BlockState> builder) {
      builder.add(FACING, STATE, OMINOUS);
   }

   public <T extends BlockEntity> @Nullable BlockEntityTicker<T> getTicker(final Level level, final BlockState blockState, final BlockEntityType<T> type) {
      BlockEntityTicker var10000;
      if (level instanceof ServerLevel serverLevel) {
         var10000 = createTickerHelper(type, BlockEntityType.VAULT, (innerLevel, pos, state, entity) -> VaultBlockEntity.Server.tick(serverLevel, pos, state, entity.getConfig(), entity.getServerData(), entity.getSharedData()));
      } else {
         var10000 = createTickerHelper(type, BlockEntityType.VAULT, (innerLevel, pos, state, entity) -> VaultBlockEntity.Client.tick(innerLevel, pos, state, entity.getClientData(), entity.getSharedData()));
      }

      return var10000;
   }

   public BlockState getStateForPlacement(final BlockPlaceContext context) {
      return (BlockState)this.defaultBlockState().setValue(FACING, context.getHorizontalDirection().getOpposite());
   }

   public BlockState rotate(final BlockState state, final Rotation rotation) {
      return (BlockState)state.setValue(FACING, rotation.rotate((Direction)state.getValue(FACING)));
   }

   public BlockState mirror(final BlockState state, final Mirror mirror) {
      return state.rotate(mirror.getRotation((Direction)state.getValue(FACING)));
   }

   static {
      STATE = BlockStateProperties.VAULT_STATE;
      FACING = HorizontalDirectionalBlock.FACING;
      OMINOUS = BlockStateProperties.OMINOUS;
   }
}
