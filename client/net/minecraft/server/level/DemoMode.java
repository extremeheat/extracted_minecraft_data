package net.minecraft.server.level;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.network.protocol.game.ClientboundGameEventPacket;
import net.minecraft.network.protocol.game.ServerboundPlayerActionPacket;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.BlockHitResult;

public class DemoMode extends ServerPlayerGameMode {
   private boolean displayedIntro;
   private boolean demoHasEnded;
   private int demoEndedReminder;
   private int gameModeTicks;

   public DemoMode(ServerLevel var1) {
      super(var1);
   }

   public void tick() {
      super.tick();
      ++this.gameModeTicks;
      long var1 = this.level.getGameTime();
      long var3 = var1 / 24000L + 1L;
      if (!this.displayedIntro && this.gameModeTicks > 20) {
         this.displayedIntro = true;
         this.player.connection.send(new ClientboundGameEventPacket(5, 0.0F));
      }

      this.demoHasEnded = var1 > 120500L;
      if (this.demoHasEnded) {
         ++this.demoEndedReminder;
      }

      if (var1 % 24000L == 500L) {
         if (var3 <= 6L) {
            if (var3 == 6L) {
               this.player.connection.send(new ClientboundGameEventPacket(5, 104.0F));
            } else {
               this.player.sendMessage(new TranslatableComponent("demo.day." + var3, new Object[0]));
            }
         }
      } else if (var3 == 1L) {
         if (var1 == 100L) {
            this.player.connection.send(new ClientboundGameEventPacket(5, 101.0F));
         } else if (var1 == 175L) {
            this.player.connection.send(new ClientboundGameEventPacket(5, 102.0F));
         } else if (var1 == 250L) {
            this.player.connection.send(new ClientboundGameEventPacket(5, 103.0F));
         }
      } else if (var3 == 5L && var1 % 24000L == 22000L) {
         this.player.sendMessage(new TranslatableComponent("demo.day.warning", new Object[0]));
      }

   }

   private void outputDemoReminder() {
      if (this.demoEndedReminder > 100) {
         this.player.sendMessage(new TranslatableComponent("demo.reminder", new Object[0]));
         this.demoEndedReminder = 0;
      }

   }

   public void handleBlockBreakAction(BlockPos var1, ServerboundPlayerActionPacket.Action var2, Direction var3, int var4) {
      if (this.demoHasEnded) {
         this.outputDemoReminder();
      } else {
         super.handleBlockBreakAction(var1, var2, var3, var4);
      }
   }

   public InteractionResult useItem(Player var1, Level var2, ItemStack var3, InteractionHand var4) {
      if (this.demoHasEnded) {
         this.outputDemoReminder();
         return InteractionResult.PASS;
      } else {
         return super.useItem(var1, var2, var3, var4);
      }
   }

   public InteractionResult useItemOn(Player var1, Level var2, ItemStack var3, InteractionHand var4, BlockHitResult var5) {
      if (this.demoHasEnded) {
         this.outputDemoReminder();
         return InteractionResult.PASS;
      } else {
         return super.useItemOn(var1, var2, var3, var4, var5);
      }
   }
}
