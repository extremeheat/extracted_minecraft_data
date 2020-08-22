package net.minecraft.client.gui.components;

import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.language.I18n;

public class LockIconButton extends Button {
   private boolean locked;

   public LockIconButton(int var1, int var2, Button.OnPress var3) {
      super(var1, var2, 20, 20, I18n.get("narrator.button.difficulty_lock"), var3);
   }

   protected String getNarrationMessage() {
      return super.getNarrationMessage() + ". " + (this.isLocked() ? I18n.get("narrator.button.difficulty_lock.locked") : I18n.get("narrator.button.difficulty_lock.unlocked"));
   }

   public boolean isLocked() {
      return this.locked;
   }

   public void setLocked(boolean var1) {
      this.locked = var1;
   }

   public void renderButton(int var1, int var2, float var3) {
      Minecraft.getInstance().getTextureManager().bind(Button.WIDGETS_LOCATION);
      RenderSystem.color4f(1.0F, 1.0F, 1.0F, 1.0F);
      LockIconButton.Icon var4;
      if (!this.active) {
         var4 = this.locked ? LockIconButton.Icon.LOCKED_DISABLED : LockIconButton.Icon.UNLOCKED_DISABLED;
      } else if (this.isHovered()) {
         var4 = this.locked ? LockIconButton.Icon.LOCKED_HOVER : LockIconButton.Icon.UNLOCKED_HOVER;
      } else {
         var4 = this.locked ? LockIconButton.Icon.LOCKED : LockIconButton.Icon.UNLOCKED;
      }

      this.blit(this.x, this.y, var4.getX(), var4.getY(), this.width, this.height);
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
