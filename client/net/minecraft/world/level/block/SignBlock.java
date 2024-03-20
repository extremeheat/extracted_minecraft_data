package net.minecraft.world.level.block;

import com.mojang.serialization.MapCodec;
import java.util.Arrays;
import java.util.UUID;
import javax.annotation.Nullable;
import net.minecraft.Util;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.contents.PlainTextContents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.stats.Stats;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.SignApplicator;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.entity.SignBlockEntity;
import net.minecraft.world.level.block.entity.SignText;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.WoodType;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;

public abstract class SignBlock extends BaseEntityBlock implements SimpleWaterloggedBlock {
   public static final BooleanProperty WATERLOGGED = BlockStateProperties.WATERLOGGED;
   protected static final float AABB_OFFSET = 4.0F;
   protected static final VoxelShape SHAPE = Block.box(4.0, 0.0, 4.0, 12.0, 16.0, 12.0);
   private final WoodType type;

   protected SignBlock(WoodType var1, BlockBehaviour.Properties var2) {
      super(var2);
      this.type = var1;
   }

   @Override
   protected abstract MapCodec<? extends SignBlock> codec();

   @Override
   protected BlockState updateShape(BlockState var1, Direction var2, BlockState var3, LevelAccessor var4, BlockPos var5, BlockPos var6) {
      if (var1.getValue(WATERLOGGED)) {
         var4.scheduleTick(var5, Fluids.WATER, Fluids.WATER.getTickDelay(var4));
      }

      return super.updateShape(var1, var2, var3, var4, var5, var6);
   }

   @Override
   protected VoxelShape getShape(BlockState var1, BlockGetter var2, BlockPos var3, CollisionContext var4) {
      return SHAPE;
   }

   @Override
   public boolean isPossibleToRespawnInThis(BlockState var1) {
      return true;
   }

   @Override
   public BlockEntity newBlockEntity(BlockPos var1, BlockState var2) {
      return new SignBlockEntity(var1, var2);
   }

   @Override
   protected ItemInteractionResult useItemOn(ItemStack var1, BlockState var2, Level var3, BlockPos var4, Player var5, InteractionHand var6, BlockHitResult var7) {
      BlockEntity var9 = var3.getBlockEntity(var4);
      if (var9 instanceof SignBlockEntity var8) {
         Item var11 = var1.getItem();
         SignApplicator var12 = var11 instanceof SignApplicator var10 ? var10 : null;
         boolean var13 = var12 != null && var5.mayBuild();
         if (!var3.isClientSide) {
            if (var13 && !((SignBlockEntity)var8).isWaxed() && !this.otherPlayerIsEditingSign(var5, (SignBlockEntity)var8)) {
               boolean var14 = ((SignBlockEntity)var8).isFacingFrontText(var5);
               if (var12.canApplyToSign(((SignBlockEntity)var8).getText(var14), var5) && var12.tryApplyToSign(var3, (SignBlockEntity)var8, var14, var5)) {
                  ((SignBlockEntity)var8).executeClickCommandsIfPresent(var5, var3, var4, var14);
                  var5.awardStat(Stats.ITEM_USED.get(var1.getItem()));
                  var3.gameEvent(
                     GameEvent.BLOCK_CHANGE, ((SignBlockEntity)var8).getBlockPos(), GameEvent.Context.of(var5, ((SignBlockEntity)var8).getBlockState())
                  );
                  if (!var5.isCreative()) {
                     var1.shrink(1);
                  }

                  return ItemInteractionResult.SUCCESS;
               } else {
                  return ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
               }
            } else {
               return ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
            }
         } else {
            return !var13 && !((SignBlockEntity)var8).isWaxed() ? ItemInteractionResult.CONSUME : ItemInteractionResult.SUCCESS;
         }
      } else {
         return ItemInteractionResult.SKIP_DEFAULT_BLOCK_INTERACTION;
      }
   }

   @Override
   protected InteractionResult useWithoutItem(BlockState var1, Level var2, BlockPos var3, Player var4, BlockHitResult var5) {
      BlockEntity var7 = var2.getBlockEntity(var3);
      if (var7 instanceof SignBlockEntity var6) {
         if (var2.isClientSide) {
            Util.pauseInIde(new IllegalStateException("Expected to only call this on server"));
         }

         boolean var9 = ((SignBlockEntity)var6).isFacingFrontText(var4);
         boolean var8 = ((SignBlockEntity)var6).executeClickCommandsIfPresent(var4, var2, var3, var9);
         if (((SignBlockEntity)var6).isWaxed()) {
            var2.playSound(null, ((SignBlockEntity)var6).getBlockPos(), ((SignBlockEntity)var6).getSignInteractionFailedSoundEvent(), SoundSource.BLOCKS);
            return InteractionResult.SUCCESS;
         } else if (var8) {
            return InteractionResult.SUCCESS;
         } else if (!this.otherPlayerIsEditingSign(var4, (SignBlockEntity)var6) && var4.mayBuild() && this.hasEditableText(var4, (SignBlockEntity)var6, var9)) {
            this.openTextEdit(var4, (SignBlockEntity)var6, var9);
            return InteractionResult.SUCCESS;
         } else {
            return InteractionResult.PASS;
         }
      } else {
         return InteractionResult.PASS;
      }
   }

   private boolean hasEditableText(Player var1, SignBlockEntity var2, boolean var3) {
      SignText var4 = var2.getText(var3);
      return Arrays.stream(var4.getMessages(var1.isTextFilteringEnabled()))
         .allMatch(var0 -> var0.equals(CommonComponents.EMPTY) || var0.getContents() instanceof PlainTextContents);
   }

   public abstract float getYRotationDegrees(BlockState var1);

   public Vec3 getSignHitboxCenterPosition(BlockState var1) {
      return new Vec3(0.5, 0.5, 0.5);
   }

   @Override
   protected FluidState getFluidState(BlockState var1) {
      return var1.getValue(WATERLOGGED) ? Fluids.WATER.getSource(false) : super.getFluidState(var1);
   }

   public WoodType type() {
      return this.type;
   }

   public static WoodType getWoodType(Block var0) {
      WoodType var1;
      if (var0 instanceof SignBlock) {
         var1 = ((SignBlock)var0).type();
      } else {
         var1 = WoodType.OAK;
      }

      return var1;
   }

   public void openTextEdit(Player var1, SignBlockEntity var2, boolean var3) {
      var2.setAllowedPlayerEditor(var1.getUUID());
      var1.openTextEdit(var2, var3);
   }

   private boolean otherPlayerIsEditingSign(Player var1, SignBlockEntity var2) {
      UUID var3 = var2.getPlayerWhoMayEdit();
      return var3 != null && !var3.equals(var1.getUUID());
   }

   @Nullable
   @Override
   public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level var1, BlockState var2, BlockEntityType<T> var3) {
      return createTickerHelper(var3, BlockEntityType.SIGN, SignBlockEntity::tick);
   }
}