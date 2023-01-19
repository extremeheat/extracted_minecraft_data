package net.minecraft.client.gui.screens.inventory;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.client.sounds.SoundManager;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.sounds.SoundEvents;

public class PageButton extends Button {
   private final boolean isForward;
   private final boolean playTurnSound;

   public PageButton(int var1, int var2, boolean var3, Button.OnPress var4, boolean var5) {
      super(var1, var2, 23, 13, CommonComponents.EMPTY, var4, DEFAULT_NARRATION);
      this.isForward = var3;
      this.playTurnSound = var5;
   }

   @Override
   public void renderButton(PoseStack var1, int var2, int var3, float var4) {
      RenderSystem.setShader(GameRenderer::getPositionTexShader);
      RenderSystem.setShaderTexture(0, BookViewScreen.BOOK_LOCATION);
      int var5 = 0;
      int var6 = 192;
      if (this.isHoveredOrFocused()) {
         var5 += 23;
      }

      if (!this.isForward) {
         var6 += 13;
      }

      this.blit(var1, this.getX(), this.getY(), var5, var6, 23, 13);
   }

   @Override
   public void playDownSound(SoundManager var1) {
      if (this.playTurnSound) {
         var1.play(SimpleSoundInstance.forUI(SoundEvents.BOOK_PAGE_TURN, 1.0F));
      }
   }
}
