package net.minecraft.client.gui.components;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.TranslatableComponent;

public class LockIconButton extends Button {
   private boolean locked;

   public LockIconButton(int var1, int var2, Button.OnPress var3) {
      super(var1, var2, 20, 20, new TranslatableComponent("narrator.button.difficulty_lock"), var3);
   }

   protected MutableComponent createNarrationMessage() {
      return super.createNarrationMessage().append(". ").append((Component)(this.isLocked() ? new TranslatableComponent("narrator.button.difficulty_lock.locked") : new TranslatableComponent("narrator.button.difficulty_lock.unlocked")));
   }

   public boolean isLocked() {
      return this.locked;
   }

   public void setLocked(boolean var1) {
      this.locked = var1;
   }

   public void renderButton(PoseStack var1, int var2, int var3, float var4) {
      Minecraft.getInstance().getTextureManager().bind(Button.WIDGETS_LOCATION);
      RenderSystem.color4f(1.0F, 1.0F, 1.0F, 1.0F);
      LockIconButton.Icon var5;
      if (!this.active) {
         var5 = this.locked ? LockIconButton.Icon.LOCKED_DISABLED : LockIconButton.Icon.UNLOCKED_DISABLED;
      } else if (this.isHovered()) {
         var5 = this.locked ? LockIconButton.Icon.LOCKED_HOVER : LockIconButton.Icon.UNLOCKED_HOVER;
      } else {
         var5 = this.locked ? LockIconButton.Icon.LOCKED : LockIconButton.Icon.UNLOCKED;
      }

      this.blit(var1, this.x, this.y, var5.getX(), var5.getY(), this.width, this.height);
   }

   static enum Icon {
      LOCKED(0, 146),
      LOCKED_HOVER(0, 166),
      LOCKED_DISABLED(0, 186),
      UNLOCKED(20, 146),
      UNLOCKED_HOVER(20, 166),
      UNLOCKED_DISABLED(20, 186);

      private final int x;
      private final int y;

      private Icon(int var3, int var4) {
         this.x = var3;
         this.y = var4;
      }

      public int getX() {
         return this.x;
      }

      public int getY() {
         return this.y;
      }
   }
}
