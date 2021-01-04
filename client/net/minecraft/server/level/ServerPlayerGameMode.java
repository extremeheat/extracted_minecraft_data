package net.minecraft.server.level;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.protocol.game.ClientboundBlockBreakAckPacket;
import net.minecraft.network.protocol.game.ClientboundPlayerInfoPacket;
import net.minecraft.network.protocol.game.ServerboundPlayerActionPacket;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.UseOnContext;
import net.minecraft.world.level.GameType;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.CommandBlock;
import net.minecraft.world.level.block.JigsawBlock;
import net.minecraft.world.level.block.StructureBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class ServerPlayerGameMode {
   private static final Logger LOGGER = LogManager.getLogger();
   public ServerLevel level;
   public ServerPlayer player;
   private GameType gameModeForPlayer;
   private boolean isDestroyingBlock;
   private int destroyProgressStart;
   private BlockPos destroyPos;
   private int gameTicks;
   private boolean hasDelayedDestroy;
   private BlockPos delayedDestroyPos;
   private int delayedTickStart;
   private int lastSentState;

   public ServerPlayerGameMode(ServerLevel var1) {
      super();
      this.gameModeForPlayer = GameType.NOT_SET;
      this.destroyPos = BlockPos.ZERO;
      this.delayedDestroyPos = BlockPos.ZERO;
      this.lastSentState = -1;
      this.level = var1;
   }

   public void setGameModeForPlayer(GameType var1) {
      this.gameModeForPlayer = var1;
      var1.updatePlayerAbilities(this.player.abilities);
      this.player.onUpdateAbilities();
      this.player.server.getPlayerList().broadcastAll(new ClientboundPlayerInfoPacket(ClientboundPlayerInfoPacket.Action.UPDATE_GAME_MODE, new ServerPlayer[]{this.player}));
      this.level.updateSleepingPlayerList();
   }

   public GameType getGameModeForPlayer() {
      return this.gameModeForPlayer;
   }

   public boolean isSurvival() {
      return this.gameModeForPlayer.isSurvival();
   }

   public boolean isCreative() {
      return this.gameModeForPlayer.isCreative();
   }

   public void updateGameMode(GameType var1) {
      if (this.gameModeForPlayer == GameType.NOT_SET) {
         this.gameModeForPlayer = var1;
      }

      this.setGameModeForPlayer(this.gameModeForPlayer);
   }

   public void tick() {
      ++this.gameTicks;
      BlockState var1;
      if (this.hasDelayedDestroy) {
         var1 = this.level.getBlockState(this.delayedDestroyPos);
         if (var1.isAir()) {
            this.hasDelayedDestroy = false;
         } else {
            float var2 = this.incrementDestroyProgress(var1, this.delayedDestroyPos);
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
            this.incrementDestroyProgress(var1, this.destroyPos);
         }
      }

   }

   private float incrementDestroyProgress(BlockState var1, BlockPos var2) {
      int var3 = this.gameTicks - this.delayedTickStart;
      float var4 = var1.getDestroyProgress(this.player, this.player.level, var2) * (float)(var3 + 1);
      int var5 = (int)(var4 * 10.0F);
      if (var5 != this.lastSentState) {
         this.level.destroyBlockProgress(this.player.getId(), var2, var5);
         this.lastSentState = var5;
      }

      return var4;
   }

   public void handleBlockBreakAction(BlockPos var1, ServerboundPlayerActionPacket.Action var2, Direction var3, int var4) {
      double var5 = this.player.x - ((double)var1.getX() + 0.5D);
      double var7 = this.player.y - ((double)var1.getY() + 0.5D) + 1.5D;
      double var9 = this.player.z - ((double)var1.getZ() + 0.5D);
      double var11 = var5 * var5 + var7 * var7 + var9 * var9;
      if (var11 > 36.0D) {
         this.player.connection.send(new ClientboundBlockBreakAckPacket(var1, this.level.getBlockState(var1), var2, false));
      } else if (var1.getY() >= var4) {
         this.player.connection.send(new ClientboundBlockBreakAckPacket(var1, this.level.getBlockState(var1), var2, false));
      } else {
         BlockState var14;
         if (var2 == ServerboundPlayerActionPacket.Action.START_DESTROY_BLOCK) {
            if (!this.level.mayInteract(this.player, var1)) {
               this.player.connection.send(new ClientboundBlockBreakAckPacket(var1, this.level.getBlockState(var1), var2, false));
               return;
            }

            if (this.isCreative()) {
               if (!this.level.extinguishFire((Player)null, var1, var3)) {
                  this.destroyAndAck(var1, var2);
               } else {
                  this.player.connection.send(new ClientboundBlockBreakAckPacket(var1, this.level.getBlockState(var1), var2, true));
               }

               return;
            }

            if (this.player.blockActionRestricted(this.level, var1, this.gameModeForPlayer)) {
               this.player.connection.send(new ClientboundBlockBreakAckPacket(var1, this.level.getBlockState(var1), var2, false));
               return;
            }

            this.level.extinguishFire((Player)null, var1, var3);
            this.destroyProgressStart = this.gameTicks;
            float var13 = 1.0F;
            var14 = this.level.getBlockState(var1);
            if (!var14.isAir()) {
               var14.attack(this.level, var1, this.player);
               var13 = var14.getDestroyProgress(this.player, this.player.level, var1);
            }

            if (!var14.isAir() && var13 >= 1.0F) {
               this.destroyAndAck(var1, var2);
            } else {
               this.isDestroyingBlock = true;
               this.destroyPos = var1;
               int var15 = (int)(var13 * 10.0F);
               this.level.destroyBlockProgress(this.player.getId(), var1, var15);
               this.player.connection.send(new ClientboundBlockBreakAckPacket(var1, this.level.getBlockState(var1), var2, true));
               this.lastSentState = var15;
            }
         } else if (var2 == ServerboundPlayerActionPacket.Action.STOP_DESTROY_BLOCK) {
            if (var1.equals(this.destroyPos)) {
               int var16 = this.gameTicks - this.destroyProgressStart;
               var14 = this.level.getBlockState(var1);
               if (!var14.isAir()) {
                  float var17 = var14.getDestroyProgress(this.player, this.player.level, var1) * (float)(var16 + 1);
                  if (var17 >= 0.7F) {
                     this.isDestroyingBlock = false;
                     this.level.destroyBlockProgress(this.player.getId(), var1, -1);
                     this.destroyAndAck(var1, var2);
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

            this.player.connection.send(new ClientboundBlockBreakAckPacket(var1, this.level.getBlockState(var1), var2, true));
         } else if (var2 == ServerboundPlayerActionPacket.Action.ABORT_DESTROY_BLOCK) {
            this.isDestroyingBlock = false;
            this.level.destroyBlockProgress(this.player.getId(), this.destroyPos, -1);
            this.player.connection.send(new ClientboundBlockBreakAckPacket(var1, this.level.getBlockState(var1), var2, true));
         }

      }
   }

   public void destroyAndAck(BlockPos var1, ServerboundPlayerActionPacket.Action var2) {
      if (this.destroyBlock(var1)) {
         this.player.connection.send(new ClientboundBlockBreakAckPacket(var1, this.level.getBlockState(var1), var2, true));
      } else {
         this.player.connection.send(new ClientboundBlockBreakAckPacket(var1, this.level.getBlockState(var1), var2, false));
      }

   }

   public boolean destroyBlock(BlockPos var1) {
      BlockState var2 = this.level.getBlockState(var1);
      if (!this.player.getMainHandItem().getItem().canAttackBlock(var2, this.level, var1, this.player)) {
         return false;
      } else {
         BlockEntity var3 = this.level.getBlockEntity(var1);
         Block var4 = var2.getBlock();
         if ((var4 instanceof CommandBlock || var4 instanceof StructureBlock || var4 instanceof JigsawBlock) && !this.player.canUseGameMasterBlocks()) {
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
               boolean var7 = this.player.canDestroy(var2);
               var6.mineBlock(this.level, var2, var1, this.player);
               if (var5 && var7) {
                  ItemStack var8 = var6.isEmpty() ? ItemStack.EMPTY : var6.copy();
                  var4.playerDestroy(this.level, this.player, var1, var2, var3, var8);
               }

               return true;
            }
         }
      }
   }

   public InteractionResult useItem(Player var1, Level var2, ItemStack var3, InteractionHand var4) {
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
            var1.setItemInHand(var4, var8);
            if (this.isCreative()) {
               var8.setCount(var5);
               if (var8.isDamageableItem()) {
                  var8.setDamageValue(var6);
               }
            }

            if (var8.isEmpty()) {
               var1.setItemInHand(var4, ItemStack.EMPTY);
            }

            if (!var1.isUsingItem()) {
               ((ServerPlayer)var1).refreshContainer(var1.inventoryMenu);
            }

            return var7.getResult();
         }
      }
   }

   public InteractionResult useItemOn(Player var1, Level var2, ItemStack var3, InteractionHand var4, BlockHitResult var5) {
      BlockPos var6 = var5.getBlockPos();
      BlockState var7 = var2.getBlockState(var6);
      if (this.gameModeForPlayer == GameType.SPECTATOR) {
         MenuProvider var13 = var7.getMenuProvider(var2, var6);
         if (var13 != null) {
            var1.openMenu(var13);
            return InteractionResult.SUCCESS;
         } else {
            return InteractionResult.PASS;
         }
      } else {
         boolean var8 = !var1.getMainHandItem().isEmpty() || !var1.getOffhandItem().isEmpty();
         boolean var9 = var1.isSneaking() && var8;
         if (!var9 && var7.use(var2, var1, var4, var5)) {
            return InteractionResult.SUCCESS;
         } else if (!var3.isEmpty() && !var1.getCooldowns().isOnCooldown(var3.getItem())) {
            UseOnContext var10 = new UseOnContext(var1, var4, var5);
            if (this.isCreative()) {
               int var11 = var3.getCount();
               InteractionResult var12 = var3.useOn(var10);
               var3.setCount(var11);
               return var12;
            } else {
               return var3.useOn(var10);
            }
         } else {
            return InteractionResult.PASS;
         }
      }
   }

   public void setLevel(ServerLevel var1) {
      this.level = var1;
   }
}
