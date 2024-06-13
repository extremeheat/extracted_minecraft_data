package net.minecraft.world.level.block.entity.vault;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.dispenser.DefaultDispenseItemBehavior;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.Vec3;

public enum VaultState implements StringRepresentable {
   INACTIVE("inactive", VaultState.LightLevel.HALF_LIT) {
      @Override
      protected void onEnter(ServerLevel var1, BlockPos var2, VaultConfig var3, VaultSharedData var4, boolean var5) {
         var4.setDisplayItem(ItemStack.EMPTY);
         var1.levelEvent(3016, var2, var5 ? 1 : 0);
      }
   },
   ACTIVE("active", VaultState.LightLevel.LIT) {
      @Override
      protected void onEnter(ServerLevel var1, BlockPos var2, VaultConfig var3, VaultSharedData var4, boolean var5) {
         if (!var4.hasDisplayItem()) {
            VaultBlockEntity.Server.cycleDisplayItemFromLootTable(var1, this, var3, var4, var2);
         }

         var1.levelEvent(3015, var2, var5 ? 1 : 0);
      }
   },
   UNLOCKING("unlocking", VaultState.LightLevel.LIT) {
      @Override
      protected void onEnter(ServerLevel var1, BlockPos var2, VaultConfig var3, VaultSharedData var4, boolean var5) {
         var1.playSound(null, var2, SoundEvents.VAULT_INSERT_ITEM, SoundSource.BLOCKS);
      }
   },
   EJECTING("ejecting", VaultState.LightLevel.LIT) {
      @Override
      protected void onEnter(ServerLevel var1, BlockPos var2, VaultConfig var3, VaultSharedData var4, boolean var5) {
         var1.playSound(null, var2, SoundEvents.VAULT_OPEN_SHUTTER, SoundSource.BLOCKS);
      }

      @Override
      protected void onExit(ServerLevel var1, BlockPos var2, VaultConfig var3, VaultSharedData var4) {
         var1.playSound(null, var2, SoundEvents.VAULT_CLOSE_SHUTTER, SoundSource.BLOCKS);
      }
   };

   private static final int UPDATE_CONNECTED_PLAYERS_TICK_RATE = 20;
   private static final int DELAY_BETWEEN_EJECTIONS_TICKS = 20;
   private static final int DELAY_AFTER_LAST_EJECTION_TICKS = 20;
   private static final int DELAY_BEFORE_FIRST_EJECTION_TICKS = 20;
   private final String stateName;
   private final VaultState.LightLevel lightLevel;

   VaultState(String var3, VaultState.LightLevel var4) {
      this.stateName = var3;
      this.lightLevel = var4;
   }

   @Override
   public String getSerializedName() {
      return this.stateName;
   }

   public int lightLevel() {
      return this.lightLevel.value;
   }

   public VaultState tickAndGetNext(ServerLevel var1, BlockPos var2, VaultConfig var3, VaultServerData var4, VaultSharedData var5) {
      return switch (this) {
         case INACTIVE -> updateStateForConnectedPlayers(var1, var2, var3, var4, var5, var3.activationRange());
         case ACTIVE -> updateStateForConnectedPlayers(var1, var2, var3, var4, var5, var3.deactivationRange());
         case UNLOCKING -> {
            var4.pauseStateUpdatingUntil(var1.getGameTime() + 20L);
            yield EJECTING;
         }
         case EJECTING -> {
            if (var4.getItemsToEject().isEmpty()) {
               var4.markEjectionFinished();
               yield updateStateForConnectedPlayers(var1, var2, var3, var4, var5, var3.deactivationRange());
            } else {
               float var6 = var4.ejectionProgress();
               this.ejectResultItem(var1, var2, var4.popNextItemToEject(), var6);
               var5.setDisplayItem(var4.getNextItemToEject());
               boolean var7 = var4.getItemsToEject().isEmpty();
               int var8 = var7 ? 20 : 20;
               var4.pauseStateUpdatingUntil(var1.getGameTime() + (long)var8);
               yield EJECTING;
            }
         }
      };
   }

   private static VaultState updateStateForConnectedPlayers(
      ServerLevel var0, BlockPos var1, VaultConfig var2, VaultServerData var3, VaultSharedData var4, double var5
   ) {
      var4.updateConnectedPlayersWithinRange(var0, var1, var3, var2, var5);
      var3.pauseStateUpdatingUntil(var0.getGameTime() + 20L);
      return var4.hasConnectedPlayers() ? ACTIVE : INACTIVE;
   }

   public void onTransition(ServerLevel var1, BlockPos var2, VaultState var3, VaultConfig var4, VaultSharedData var5, boolean var6) {
      this.onExit(var1, var2, var4, var5);
      var3.onEnter(var1, var2, var4, var5, var6);
   }

   protected void onEnter(ServerLevel var1, BlockPos var2, VaultConfig var3, VaultSharedData var4, boolean var5) {
   }

   protected void onExit(ServerLevel var1, BlockPos var2, VaultConfig var3, VaultSharedData var4) {
   }

   private void ejectResultItem(ServerLevel var1, BlockPos var2, ItemStack var3, float var4) {
      DefaultDispenseItemBehavior.spawnItem(var1, var3, 2, Direction.UP, Vec3.atBottomCenterOf(var2).relative(Direction.UP, 1.2));
      var1.levelEvent(3017, var2, 0);
      var1.playSound(null, var2, SoundEvents.VAULT_EJECT_ITEM, SoundSource.BLOCKS, 1.0F, 0.8F + 0.4F * var4);
   }

   static enum LightLevel {
      HALF_LIT(6),
      LIT(12);

      final int value;

      private LightLevel(int var3) {
         this.value = var3;
      }
   }
}
