package net.minecraft.server.level;

import com.mojang.logging.LogUtils;
import java.util.Objects;
import javax.annotation.Nullable;
import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.protocol.game.ClientboundBlockUpdatePacket;
import net.minecraft.network.protocol.game.ClientboundPlayerInfoPacket;
import net.minecraft.network.protocol.game.ServerboundPlayerActionPacket;
import net.minecraft.server.network.ServerGamePacketListenerImpl;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.GameType;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.GameMasterBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import org.slf4j.Logger;

public class ServerPlayerGameMode {
   private static final Logger LOGGER = LogUtils.getLogger();
   protected ServerLevel level;
   protected final ServerPlayer player;
   private GameType gameModeForPlayer;
   @Nullable
   private GameType previousGameModeForPlayer;
   private boolean isDestroyingBlock;
   private int destroyProgressStart;
   private BlockPos destroyPos;
   private int gameTicks;
   private boolean hasDelayedDestroy;
   private BlockPos delayedDestroyPos;
   private int delayedTickStart;
   private int lastSentState;

   public ServerPlayerGameMode(ServerPlayer var1) {
      super();
      this.gameModeForPlayer = GameType.DEFAULT_MODE;
      this.destroyPos = BlockPos.ZERO;
      this.delayedDestroyPos = BlockPos.ZERO;
      this.lastSentState = -1;
      this.player = var1;
      this.level = var1.getLevel();
   }

   public boolean changeGameModeForPlayer(GameType var1) {
      if (var1 == this.gameModeForPlayer) {
         return false;
      } else {
         this.setGameModeForPlayer(var1, this.gameModeForPlayer);
         return true;
      }
   }

   protected void setGameModeForPlayer(GameType var1, @Nullable GameType var2) {
      this.previousGameModeForPlayer = var2;
      this.gameModeForPlayer = var1;
      var1.updatePlayerAbilities(this.player.getAbilities());
      this.player.onUpdateAbilities();
      this.player.server.getPlayerList().broadcastAll(new ClientboundPlayerInfoPacket(ClientboundPlayerInfoPacket.Action.UPDATE_GAME_MODE, new ServerPlayer[]{this.player}));
      this.level.updateSleepingPlayerList();
   }

   public GameType getGameModeForPlayer() {
      return this.gameModeForPlayer;
   }

   @Nullable
   public GameType getPreviousGameModeForPlayer() {
      return this.previousGameModeForPlayer;
   }

   public boolean isSurvival() {
      return this.gameModeForPlayer.isSurvival();
   }

   public boolean isCreative() {
      return this.gameModeForPlayer.isCreative();
   }

   public void tick() {
      ++this.gameTicks;
      BlockState var1;
      if (this.hasDelayedDestroy) {
         var1 = this.level.getBlockState(this.delayedDestroyPos);
         if (var1.isAir()) {
            this.hasDelayedDestroy = false;
         } else {
            float var2 = this.incrementDestroyProgress(var1, this.delayedDestroyPos, this.delayedTickStart);
            if (var2 >= 1.0F) {
               this.hasDelayedDestroy = false;
               this.destroyBlock(this.delayedDestroyPos);
            }
         }
      } else if (this.isDestroyingBlock) {
         var1 = this.level.getBlockState(this.destroyPos);
         if (var1.isAir()) {
            this.level.destroyBlockProgress(this.player.getId(), this.destroyPos, -1);
            this.lastSentState = -1;
            this.isDestroyingBlock = false;
         } else {
            this.incrementDestroyProgress(var1, this.destroyPos, this.destroyProgressStart);
         }
      }

   }

   private float incrementDestroyProgress(BlockState var1, BlockPos var2, int var3) {
      int var4 = this.gameTicks - var3;
      float var5 = var1.getDestroyProgress(this.player, this.player.level, var2) * (float)(var4 + 1);
      int var6 = (int)(var5 * 10.0F);
      if (var6 != this.lastSentState) {
         this.level.destroyBlockProgress(this.player.getId(), var2, var6);
         this.lastSentState = var6;
      }

      return var5;
   }

   private void debugLogging(BlockPos var1, boolean var2, int var3, String var4) {
   }

   public void handleBlockBreakAction(BlockPos var1, ServerboundPlayerActionPacket.Action var2, Direction var3, int var4, int var5) {
      if (this.player.getEyePosition().distanceToSqr(Vec3.atCenterOf(var1)) > ServerGamePacketListenerImpl.MAX_INTERACTION_DISTANCE) {
         this.debugLogging(var1, false, var5, "too far");
      } else if (var1.getY() >= var4) {
         this.player.connection.send(new ClientboundBlockUpdatePacket(var1, this.level.getBlockState(var1)));
         this.debugLogging(var1, false, var5, "too high");
      } else {
         BlockState var7;
         if (var2 == ServerboundPlayerActionPacket.Action.START_DESTROY_BLOCK) {
            if (!this.level.mayInteract(this.player, var1)) {
               this.player.connection.send(new ClientboundBlockUpdatePacket(var1, this.level.getBlockState(var1)));
               this.debugLogging(var1, false, var5, "may not interact");
               return;
            }

            if (this.isCreative()) {
               this.destroyAndAck(var1, var5, "creative destroy");
               return;
            }

            if (this.player.blockActionRestricted(this.level, var1, this.gameModeForPlayer)) {
               this.player.connection.send(new ClientboundBlockUpdatePacket(var1, this.level.getBlockState(var1)));
               this.debugLogging(var1, false, var5, "block action restricted");
               return;
            }

            this.destroyProgressStart = this.gameTicks;
            float var6 = 1.0F;
            var7 = this.level.getBlockState(var1);
            if (!var7.isAir()) {
               var7.attack(this.level, var1, this.player);
               var6 = var7.getDestroyProgress(this.player, this.player.level, var1);
            }

            if (!var7.isAir() && var6 >= 1.0F) {
               this.destroyAndAck(var1, var5, "insta mine");
            } else {
               if (this.isDestroyingBlock) {
                  this.player.connection.send(new ClientboundBlockUpdatePacket(this.destroyPos, this.level.getBlockState(this.destroyPos)));
                  this.debugLogging(var1, false, var5, "abort destroying since another started (client insta mine, server disagreed)");
               }

               this.isDestroyingBlock = true;
               this.destroyPos = var1.immutable();
               int var8 = (int)(var6 * 10.0F);
               this.level.destroyBlockProgress(this.player.getId(), var1, var8);
               this.debugLogging(var1, true, var5, "actual start of destroying");
               this.lastSentState = var8;
            }
         } else if (var2 == ServerboundPlayerActionPacket.Action.STOP_DESTROY_BLOCK) {
            if (var1.equals(this.destroyPos)) {
               int var9 = this.gameTicks - this.destroyProgressStart;
               var7 = this.level.getBlockState(var1);
               if (!var7.isAir()) {
                  float var10 = var7.getDestroyProgress(this.player, this.player.level, var1) * (float)(var9 + 1);
                  if (var10 >= 0.7F) {
                     this.isDestroyingBlock = false;
                     this.level.destroyBlockProgress(this.player.getId(), var1, -1);
                     this.destroyAndAck(var1, var5, "destroyed");
                     return;
                  }

                  if (!this.hasDelayedDestroy) {
                     this.isDestroyingBlock = false;
                     this.hasDelayedDestroy = true;
                     this.delayedDestroyPos = var1;
                     this.delayedTickStart = this.destroyProgressStart;
                  }
               }
            }

            this.debugLogging(var1, true, var5, "stopped destroying");
         } else if (var2 == ServerboundPlayerActionPacket.Action.ABORT_DESTROY_BLOCK) {
            this.isDestroyingBlock = false;
            if (!Objects.equals(this.destroyPos, var1)) {
               LOGGER.warn("Mismatch in destroy block pos: {} {}", this.destroyPos, var1);
               this.level.destroyBlockProgress(this.player.getId(), this.destroyPos, -1);
               this.debugLogging(var1, true, var5, "aborted mismatched destroying");
            }

            this.level.destroyBlockProgress(this.player.getId(), var1, -1);
            this.debugLogging(var1, true, var5, "aborted destroying");
         }

      }
   }

   public void destroyAndAck(BlockPos var1, int var2, String var3) {
      if (this.destroyBlock(var1)) {
         this.debugLogging(var1, true, var2, var3);
      } else {
         this.player.connection.send(new ClientboundBlockUpdatePacket(var1, this.level.getBlockState(var1)));
         this.debugLogging(var1, false, var2, var3);
      }

   }

   public boolean destroyBlock(BlockPos var1) {
      BlockState var2 = this.level.getBlockState(var1);
      if (!this.player.getMainHandItem().getItem().canAttackBlock(var2, this.level, var1, this.player)) {
         return false;
      } else {
         BlockEntity var3 = this.level.getBlockEntity(var1);
         Block var4 = var2.getBlock();
         if (var4 instanceof GameMasterBlock && !this.player.canUseGameMasterBlocks()) {
            this.level.sendBlockUpdated(var1, var2, var2, 3);
            return false;
         } else if (this.player.blockActionRestricted(this.level, var1, this.gameModeForPlayer)) {
            return false;
         } else {
            var4.playerWillDestroy(this.level, var1, var2, this.player);
            boolean var5 = this.level.removeBlock(var1, false);
            if (var5) {
               var4.destroy(this.level, var1, var2);
            }

            if (this.isCreative()) {
               return true;
            } else {
               ItemStack var6 = this.player.getMainHandItem();
               ItemStack var7 = var6.copy();
               boolean var8 = this.player.hasCorrectToolForDrops(var2);
               var6.mineBlock(this.level, var2, var1, this.player);
               if (var5 && var8) {
                  var4.playerDestroy(this.level, this.player, var1, var2, var3, var7);
               }

               return true;
            }
         }
      }
   }

   public InteractionResult useItem(ServerPlayer var1, Level var2, ItemStack var3, InteractionHand var4) {
      if (this.gameModeForPlayer == GameType.SPECTATOR) {
         return InteractionResult.PASS;
      } else if (var1.getCooldowns().isOnCooldown(var3.getItem())) {
         return InteractionResult.PASS;
      } else {
         int var5 = var3.getCount();
         int var6 = var3.getDamageValue();
         InteractionResultHolder var7 = var3.use(var2, var1, var4);
         ItemStack var8 = (ItemStack)var7.getObject();
         if (var8 == var3 && var8.getCount() == var5 && var8.getUseDuration() <= 0 && var8.getDamageValue() == var6) {
            return var7.getResult();
         } else if (var7.getResult() == InteractionResult.FAIL && var8.getUseDuration() > 0 && !var1.isUsingItem()) {
            return var7.getResult();
         } else {
            if (var3 != var8) {
               var1.setItemInHand(var4, var8);
            }

            if (this.isCreative()) {
               var8.setCount(var5);
               if (var8.isDamageableItem() && var8.getDamageValue() != var6) {
                  var8.setDamageValue(var6);
               }
            }

            if (var8.isEmpty()) {
               var1.setItemInHand(var4, ItemStack.EMPTY);
            }

            if (!var1.isUsingItem()) {
               var1.inventoryMenu.sendAllDataToRemote();
            }

            return var7.getResult();
         }
      }
   }

   public InteractionResult useItemOn(ServerPlayer var1, Level var2, ItemStack var3, InteractionHand var4, BlockHitResult var5) {
      BlockPos var6 = var5.getBlockPos();
      BlockState var7 = var2.getBlockState(var6);
      if (this.gameModeForPlayer == GameType.SPECTATOR) {
         MenuProvider var14 = var7.getMenuProvider(var2, var6);
         if (var14 != null) {
            var1.openMenu(var14);
            return InteractionResult.SUCCESS;
         } else {
            return InteractionResult.PASS;
         }
      } else {
         boolean var8 = !var1.getMainHandItem().isEmpty() || !var1.getOffhandItem().isEmpty();
         boolean var9 = var1.isSecondaryUseActive() && var8;
         ItemStack var10 = var3.copy();
         if (!var9) {
            InteractionResult var11 = var7.use(var2, var1, var4, var5);
            if (var11.consumesAction()) {
               CriteriaTriggers.ITEM_USED_ON_BLOCK.trigger(var1, var6, var10);
               return var11;
            }
         }

         if (!var3.isEmpty() && !var1.getCooldowns().isOnCooldown(var3.getItem())) {
            UseOnContext var15 = new UseOnContext(var1, var4, var5);
            InteractionResult var12;
            if (this.isCreative()) {
               int var13 = var3.getCount();
               var12 = var3.useOn(var15);
               var3.setCount(var13);
            } else {
               var12 = var3.useOn(var15);
            }

            if (var12.consumesAction()) {
               CriteriaTriggers.ITEM_USED_ON_BLOCK.trigger(var1, var6, var10);
            }

            return var12;
         } else {
            return InteractionResult.PASS;
         }
      }
   }

   public void setLevel(ServerLevel var1) {
      this.level = var1;
   }
}
