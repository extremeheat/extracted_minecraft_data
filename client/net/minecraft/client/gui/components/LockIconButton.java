package net.minecraft.client.gui.components;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceLocation;

public class LockIconButton extends Button {
   private boolean locked;

   public LockIconButton(int var1, int var2, Button.OnPress var3) {
      super(var1, var2, 20, 20, Component.translatable("narrator.button.difficulty_lock"), var3, DEFAULT_NARRATION);
   }

   protected MutableComponent createNarrationMessage() {
      return CommonComponents.joinForNarration(super.createNarrationMessage(), this.isLocked() ? Component.translatable("narrator.button.difficulty_lock.locked") : Component.translatable("narrator.button.difficulty_lock.unlocked"));
   }

   public boolean isLocked() {
      return this.locked;
   }

   public void setLocked(boolean var1) {
      this.locked = var1;
   }

   public void renderWidget(GuiGraphics var1, int var2, int var3, float var4) {
      Icon var5;
      if (!this.active) {
         var5 = this.locked ? LockIconButton.Icon.LOCKED_DISABLED : LockIconButton.Icon.UNLOCKED_DISABLED;
      } else if (this.isHoveredOrFocused()) {
         var5 = this.locked ? LockIconButton.Icon.LOCKED_HOVER : LockIconButton.Icon.UNLOCKED_HOVER;
      } else {
         var5 = this.locked ? LockIconButton.Icon.LOCKED : LockIconButton.Icon.UNLOCKED;
      }

      var1.blitSprite(var5.sprite, this.getX(), this.getY(), this.width, this.height);
   }

   private static enum Icon {
      LOCKED(new ResourceLocation("widget/locked_button")),
      LOCKED_HOVER(new ResourceLocation("widget/locked_button_highlighted")),
      LOCKED_DISABLED(new ResourceLocation("widget/locked_button_disabled")),
      UNLOCKED(new ResourceLocation("widget/unlocked_button")),
      UNLOCKED_HOVER(new ResourceLocation("widget/unlocked_button_highlighted")),
      UNLOCKED_DISABLED(new ResourceLocation("widget/unlocked_button_disabled"));

      final ResourceLocation sprite;

      private Icon(final ResourceLocation var3) {
         this.sprite = var3;
      }

      // $FF: synthetic method
      private static Icon[] $values() {
         return new Icon[]{LOCKED, LOCKED_HOVER, LOCKED_DISABLED, UNLOCKED, UNLOCKED_HOVER, UNLOCKED_DISABLED};
      }
   }
}
