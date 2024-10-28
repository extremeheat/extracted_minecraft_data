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
   public static final Property<VaultState> STATE;
   public static final EnumProperty<Direction> FACING;
   public static final BooleanProperty OMINOUS;

   public MapCodec<VaultBlock> codec() {
      return CODEC;
   }

   public VaultBlock(BlockBehaviour.Properties var1) {
      super(var1);
      this.registerDefaultState((BlockState)((BlockState)((BlockState)((BlockState)this.stateDefinition.any()).setValue(FACING, Direction.NORTH)).setValue(STATE, VaultState.INACTIVE)).setValue(OMINOUS, false));
   }

   public InteractionResult useItemOn(ItemStack var1, BlockState var2, Level var3, BlockPos var4, Player var5, InteractionHand var6, BlockHitResult var7) {
      if (!var1.isEmpty() && var2.getValue(STATE) == VaultState.ACTIVE) {
         if (var3 instanceof ServerLevel) {
            ServerLevel var8 = (ServerLevel)var3;
            BlockEntity var10 = var8.getBlockEntity(var4);
            if (!(var10 instanceof VaultBlockEntity)) {
               return InteractionResult.TRY_WITH_EMPTY_HAND;
            }

            VaultBlockEntity var9 = (VaultBlockEntity)var10;
            VaultBlockEntity.Server.tryInsertKey(var8, var4, var2, var9.getConfig(), var9.getServerData(), var9.getSharedData(), var5, var1);
         }

         return InteractionResult.SUCCESS_SERVER;
      } else {
         return InteractionResult.TRY_WITH_EMPTY_HAND;
      }
   }

   @Nullable
   public BlockEntity newBlockEntity(BlockPos var1, BlockState var2) {
      return new VaultBlockEntity(var1, var2);
   }

   protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> var1) {
      var1.add(FACING, STATE, OMINOUS);
   }

   @Nullable
   public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level var1, BlockState var2, BlockEntityType<T> var3) {
      BlockEntityTicker var10000;
      if (var1 instanceof ServerLevel var4) {
         var10000 = createTickerHelper(var3, BlockEntityType.VAULT, (var1x, var2x, var3x, var4x) -> {
            VaultBlockEntity.Server.tick(var4, var2x, var3x, var4x.getConfig(), var4x.getServerData(), var4x.getSharedData());
         });
      } else {
         var10000 = createTickerHelper(var3, BlockEntityType.VAULT, (var0, var1x, var2x, var3x) -> {
            VaultBlockEntity.Client.tick(var0, var1x, var2x, var3x.getClientData(), var3x.getSharedData());
         });
      }

      return var10000;
   }

   public BlockState getStateForPlacement(BlockPlaceContext var1) {
      return (BlockState)this.defaultBlockState().setValue(FACING, var1.getHorizontalDirection().getOpposite());
   }

   public BlockState rotate(BlockState var1, Rotation var2) {
      return (BlockState)var1.setValue(FACING, var2.rotate((Direction)var1.getValue(FACING)));
   }

   public BlockState mirror(BlockState var1, Mirror var2) {
      return var1.rotate(var2.getRotation((Direction)var1.getValue(FACING)));
   }

   public RenderShape getRenderShape(BlockState var1) {
      return RenderShape.MODEL;
   }

   static {
      STATE = BlockStateProperties.VAULT_STATE;
      FACING = HorizontalDirectionalBlock.FACING;
      OMINOUS = BlockStateProperties.OMINOUS;
   }
}
