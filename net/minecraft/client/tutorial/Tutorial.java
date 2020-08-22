package net.minecraft.client.tutorial;

import javax.annotation.Nullable;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.player.Input;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.KeybindComponent;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.GameType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.HitResult;

public class Tutorial {
   private final Minecraft minecraft;
   @Nullable
   private TutorialStepInstance instance;

   public Tutorial(Minecraft var1) {
      this.minecraft = var1;
   }

   public void onInput(Input var1) {
      if (this.instance != null) {
         this.instance.onInput(var1);
      }

   }

   public void onMouse(double var1, double var3) {
      if (this.instance != null) {
         this.instance.onMouse(var1, var3);
      }

   }

   public void onLookAt(@Nullable ClientLevel var1, @Nullable HitResult var2) {
      if (this.instance != null && var2 != null && var1 != null) {
         this.instance.onLookAt(var1, var2);
      }

   }

   public void onDestroyBlock(ClientLevel var1, BlockPos var2, BlockState var3, float var4) {
      if (this.instance != null) {
         this.instance.onDestroyBlock(var1, var2, var3, var4);
      }

   }

   public void onOpenInventory() {
      if (this.instance != null) {
         this.instance.onOpenInventory();
      }

   }

   public void onGetItem(ItemStack var1) {
      if (this.instance != null) {
         this.instance.onGetItem(var1);
      }

   }

   public void stop() {
      if (this.instance != null) {
         this.instance.clear();
         this.instance = null;
      }
   }

   public void start() {
      if (this.instance != null) {
         this.stop();
      }

      this.instance = this.minecraft.options.tutorialStep.create(this);
   }

   public void tick() {
      if (this.instance != null) {
         if (this.minecraft.level != null) {
            this.instance.tick();
         } else {
            this.stop();
         }
      } else if (this.minecraft.level != null) {
         this.start();
      }

   }

   public void setStep(TutorialSteps var1) {
      this.minecraft.options.tutorialStep = var1;
      this.minecraft.options.save();
      if (this.instance != null) {
         this.instance.clear();
         this.instance = var1.create(this);
      }

   }

   public Minecraft getMinecraft() {
      return this.minecraft;
   }

   public GameType getGameMode() {
      return this.minecraft.gameMode == null ? GameType.NOT_SET : this.minecraft.gameMode.getPlayerMode();
   }

   public static Component key(String var0) {
      return (new KeybindComponent("key." + var0)).withStyle(ChatFormatting.BOLD);
   }
}
