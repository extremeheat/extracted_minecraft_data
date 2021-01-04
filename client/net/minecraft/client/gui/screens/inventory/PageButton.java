package net.minecraft.client.gui.screens.inventory;

import com.mojang.blaze3d.platform.GlStateManager;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.client.sounds.SoundManager;
import net.minecraft.sounds.SoundEvents;

public class PageButton extends Button {
   private final boolean isForward;
   private final boolean playTurnSound;

   public PageButton(int var1, int var2, boolean var3, Button.OnPress var4, boolean var5) {
      super(var1, var2, 23, 13, "", var4);
      this.isForward = var3;
      this.playTurnSound = var5;
   }

   public void renderButton(int var1, int var2, float var3) {
      GlStateManager.color4f(1.0F, 1.0F, 1.0F, 1.0F);
      Minecraft.getInstance().getTextureManager().bind(BookViewScreen.BOOK_LOCATION);
      int var4 = 0;
      int var5 = 192;
      if (this.isHovered()) {
         var4 += 23;
      }

      if (!this.isForward) {
         var5 += 13;
      }

      this.blit(this.x, this.y, var4, var5, 23, 13);
   }

   public void playDownSound(SoundManager var1) {
      if (this.playTurnSound) {
         var1.play(SimpleSoundInstance.forUI(SoundEvents.BOOK_PAGE_TURN, 1.0F));
      }

   }
}
