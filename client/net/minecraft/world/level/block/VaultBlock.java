package net.minecraft.world.level.block;

import com.mojang.serialization.MapCodec;
import javax.annotation.Nullable;
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

public class VaultBlock extends BaseEntityBlock {
   public static final MapCodec<VaultBlock> CODEC = simpleCodec(VaultBlock::new);
   public static final Property<VaultState> STATE = BlockStateProperties.VAULT_STATE;
   public static final EnumProperty<Direction> FACING = HorizontalDirectionalBlock.FACING;
   public static final BooleanProperty OMINOUS = BlockStateProperties.OMINOUS;

   @Override
   public MapCodec<VaultBlock> codec() {
      return CODEC;
   }

   public VaultBlock(BlockBehaviour.Properties var1) {
      super(var1);
      this.registerDefaultState(
         this.stateDefinition.any().setValue(FACING, Direction.NORTH).setValue(STATE, VaultState.INACTIVE).setValue(OMINOUS, Boolean.valueOf(false))
      );
   }

   @Override
   public InteractionResult useItemOn(ItemStack var1, BlockState var2, Level var3, BlockPos var4, Player var5, InteractionHand var6, BlockHitResult var7) {
      if (!var1.isEmpty() && var2.getValue(STATE) == VaultState.ACTIVE) {
         if (var3 instanceof ServerLevel var8) {
            if (!(var8.getBlockEntity(var4) instanceof VaultBlockEntity var9)) {
               return InteractionResult.TRY_WITH_EMPTY_HAND;
            }

            VaultBlockEntity.Server.tryInsertKey(var8, var4, var2, var9.getConfig(), var9.getServerData(), var9.getSharedData(), var5, var1);
         }

         return InteractionResult.SUCCESS_SERVER;
      } else {
         return InteractionResult.TRY_WITH_EMPTY_HAND;
      }
   }

   @Nullable
   @Override
   public BlockEntity newBlockEntity(BlockPos var1, BlockState var2) {
      return new VaultBlockEntity(var1, var2);
   }

   @Override
   protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> var1) {
      var1.add(FACING, STATE, OMINOUS);
   }

   @Nullable
   @Override
   public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level var1, BlockState var2, BlockEntityType<T> var3) {
      return var1 instanceof ServerLevel var4
         ? createTickerHelper(
            var3,
            BlockEntityType.VAULT,
            (var1x, var2x, var3x, var4x) -> VaultBlockEntity.Server.tick(var4, var2x, var3x, var4x.getConfig(), var4x.getServerData(), var4x.getSharedData())
         )
         : createTickerHelper(
            var3,
            BlockEntityType.VAULT,
            (var0, var1x, var2x, var3x) -> VaultBlockEntity.Client.tick(var0, var1x, var2x, var3x.getClientData(), var3x.getSharedData())
         );
   }

   @Override
   public BlockState getStateForPlacement(BlockPlaceContext var1) {
      return this.defaultBlockState().setValue(FACING, var1.getHorizontalDirection().getOpposite());
   }

   @Override
   public BlockState rotate(BlockState var1, Rotation var2) {
      return var1.setValue(FACING, var2.rotate(var1.getValue(FACING)));
   }

   @Override
   public BlockState mirror(BlockState var1, Mirror var2) {
      return var1.rotate(var2.getRotation(var1.getValue(FACING)));
   }

   @Override
   public RenderShape getRenderShape(BlockState var1) {
      return RenderShape.MODEL;
   }
}
