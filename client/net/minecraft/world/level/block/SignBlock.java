package net.minecraft.world.level.block;

import java.util.UUID;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundSource;
import net.minecraft.stats.Stats;
import net.minecraft.util.RandomSource;
import net.minecraft.util.Util;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.SignApplicator;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.ScheduledTickAccess;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.entity.BlockEntityTypes;
import net.minecraft.world.level.block.entity.SignBlockEntity;
import net.minecraft.world.level.block.entity.SignTextSlot;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.WoodType;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jspecify.annotations.Nullable;

public abstract class SignBlock extends BaseEntityBlock implements SimpleWaterloggedBlock {
   public static final BooleanProperty WATERLOGGED;
   private static final VoxelShape SHAPE;
   private final WoodType type;

   protected SignBlock(final WoodType type, final BlockBehaviour.Properties properties) {
      super(properties);
      this.type = type;
   }

   protected BlockState updateShape(final BlockState state, final LevelReader level, final ScheduledTickAccess ticks, final BlockPos pos, final Direction directionToNeighbour, final BlockPos neighbourPos, final BlockState neighbourState, final RandomSource random) {
      if ((Boolean)state.getValue(WATERLOGGED)) {
         ticks.scheduleTick(pos, (Fluid)Fluids.WATER, Fluids.WATER.getTickDelay(level));
      }

      return super.updateShape(state, level, ticks, pos, directionToNeighbour, neighbourPos, neighbourState, random);
   }

   protected VoxelShape getShape(final BlockState state, final BlockGetter level, final BlockPos pos, final CollisionContext context) {
      return SHAPE;
   }

   public boolean isPossibleToRespawnInThis(final BlockState state) {
      return true;
   }

   public BlockEntity newBlockEntity(final BlockPos worldPosition, final BlockState blockState) {
      return new SignBlockEntity(worldPosition, blockState);
   }

   protected InteractionResult useItemOn(final ItemStack itemStack, final BlockState state, final Level level, final BlockPos pos, final Player player, final InteractionHand hand, final BlockHitResult hitResult) {
      BlockEntity var9 = level.getBlockEntity(pos);
      if (var9 instanceof SignBlockEntity sign) {
         Item var11 = itemStack.getItem();
         SignApplicator var10000;
         if (var11 instanceof SignApplicator applicator) {
            var10000 = applicator;
         } else {
            var10000 = null;
         }

         SignApplicator signApplicator = var10000;
         boolean hasApplicatorToUse = signApplicator != null && player.mayBuild();
         if (level instanceof ServerLevel serverLevel) {
            if (hasApplicatorToUse && !sign.isWaxed() && !this.otherPlayerIsEditingSign(player, sign)) {
               SignTextSlot textSlot = sign.getSlotPlayerIsFacing(player);
               if (signApplicator.canApplyToSign(sign.getText(textSlot), itemStack, player) && signApplicator.tryApplyToSign(serverLevel, sign, textSlot, itemStack, player)) {
                  sign.executeClickCommandsIfPresent(serverLevel, player, pos, textSlot);
                  player.awardStat(Stats.ITEM_USED.get(itemStack.getItem()));
                  serverLevel.gameEvent(GameEvent.BLOCK_CHANGE, sign.getBlockPos(), GameEvent.Context.of(player, sign.getBlockState()));
                  itemStack.consume(1, player);
                  return InteractionResult.SUCCESS;
               } else {
                  return InteractionResult.TRY_WITH_EMPTY_HAND;
               }
            } else {
               return InteractionResult.TRY_WITH_EMPTY_HAND;
            }
         } else {
            return !hasApplicatorToUse && !sign.isWaxed() ? InteractionResult.CONSUME : InteractionResult.SUCCESS;
         }
      } else {
         return InteractionResult.PASS;
      }
   }

   protected InteractionResult useWithoutItem(final BlockState state, final Level level, final BlockPos pos, final Player player, final BlockHitResult hitResult) {
      BlockEntity var7 = level.getBlockEntity(pos);
      if (var7 instanceof SignBlockEntity sign) {
         if (level instanceof ServerLevel serverLevel) {
            SignTextSlot textSlot = sign.getSlotPlayerIsFacing(player);
            boolean executedClickCommand = sign.executeClickCommandsIfPresent(serverLevel, player, pos, textSlot);
            if (sign.isWaxed()) {
               serverLevel.playSound((Entity)null, sign.getBlockPos(), sign.getSignInteractionFailedSoundEvent(), SoundSource.BLOCKS);
               return InteractionResult.SUCCESS_SERVER;
            } else if (executedClickCommand) {
               return InteractionResult.SUCCESS_SERVER;
            } else if (!this.otherPlayerIsEditingSign(player, sign) && player.mayBuild() && sign.getText(textSlot).hasEditableText(player.isTextFilteringEnabled())) {
               this.openTextEdit(player, sign, textSlot);
               return InteractionResult.SUCCESS_SERVER;
            } else {
               return InteractionResult.PASS;
            }
         } else {
            Util.pauseInIde(new IllegalStateException("Expected to only call this on server"));
            return InteractionResult.CONSUME;
         }
      } else {
         return InteractionResult.PASS;
      }
   }

   public abstract float getYRotationDegrees(final BlockState state);

   public Vec3 getSignHitboxCenterPosition(final BlockState state) {
      return new Vec3(0.5, 0.5, 0.5);
   }

   protected FluidState getFluidState(final BlockState state) {
      return (Boolean)state.getValue(WATERLOGGED) ? Fluids.WATER.getSource(false) : super.getFluidState(state);
   }

   public WoodType type() {
      return this.type;
   }

   public static WoodType getWoodType(final Block block) {
      WoodType var10000;
      if (block instanceof SignBlock signBlock) {
         var10000 = signBlock.type();
      } else {
         var10000 = WoodType.OAK;
      }

      return var10000;
   }

   public void openTextEdit(final Player player, final SignBlockEntity sign, final SignTextSlot slot) {
      sign.setAllowedPlayerEditor(player.getUUID());
      player.openTextEdit(sign, slot);
   }

   private boolean otherPlayerIsEditingSign(final Player player, final SignBlockEntity sign) {
      UUID playerWhoMayEdit = sign.getPlayerWhoMayEdit();
      return playerWhoMayEdit != null && !playerWhoMayEdit.equals(player.getUUID());
   }

   public <T extends BlockEntity> @Nullable BlockEntityTicker<T> getTicker(final Level level, final BlockState blockState, final BlockEntityType<T> type) {
      return createTickerHelper(type, BlockEntityTypes.SIGN, SignBlockEntity::tick);
   }

   public void setPlacedBy(final Level level, final BlockPos pos, final BlockState state, final @Nullable LivingEntity by, final ItemStack itemStack) {
      if (!level.isClientSide() && by instanceof ServerPlayer player) {
         Block var9 = state.getBlock();
         if (var9 instanceof SignBlock sign) {
            BlockEntity var10 = level.getBlockEntity(pos);
            if (var10 instanceof SignBlockEntity signEntity) {
               if (!signEntity.isWaxed() && signEntity.getText(SignTextSlot.FRONT).hasEditableText(player.isTextFilteringEnabled())) {
                  sign.openTextEdit(player, signEntity, SignTextSlot.FRONT);
               }
            }
         }
      }

   }

   static {
      WATERLOGGED = BlockStateProperties.WATERLOGGED;
      SHAPE = Block.column(8.0, 0.0, 16.0);
   }
}
