package net.minecraft.client.gui.screens.inventory;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.client.sounds.SoundManager;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.sounds.SoundEvents;

public class PageButton extends Button {
   private static final Identifier PAGE_FORWARD_HIGHLIGHTED_SPRITE = Identifier.withDefaultNamespace("widget/page_forward_highlighted");
   private static final Identifier PAGE_FORWARD_SPRITE = Identifier.withDefaultNamespace("widget/page_forward");
   private static final Identifier PAGE_BACKWARD_HIGHLIGHTED_SPRITE = Identifier.withDefaultNamespace("widget/page_backward_highlighted");
   private static final Identifier PAGE_BACKWARD_SPRITE = Identifier.withDefaultNamespace("widget/page_backward");
   private static final Component PAGE_BUTTON_NEXT = Component.translatable("book.page_button.next");
   private static final Component PAGE_BUTTON_PREVIOUS = Component.translatable("book.page_button.previous");
   private final boolean isForward;
   private final boolean playTurnSound;

   public PageButton(int var1, int var2, boolean var3, Button.OnPress var4, boolean var5) {
      super(var1, var2, 23, 13, var3 ? PAGE_BUTTON_NEXT : PAGE_BUTTON_PREVIOUS, var4, DEFAULT_NARRATION);
      this.isForward = var3;
      this.playTurnSound = var5;
   }

   public void renderContents(GuiGraphics var1, int var2, int var3, float var4) {
      Identifier var5;
      if (this.isForward) {
         var5 = this.isHoveredOrFocused() ? PAGE_FORWARD_HIGHLIGHTED_SPRITE : PAGE_FORWARD_SPRITE;
      } else {
         var5 = this.isHoveredOrFocused() ? PAGE_BACKWARD_HIGHLIGHTED_SPRITE : PAGE_BACKWARD_SPRITE;
      }

      var1.blitSprite(RenderPipelines.GUI_TEXTURED, (Identifier)var5, this.getX(), this.getY(), 23, 13);
   }

   public void playDownSound(SoundManager var1) {
      if (this.playTurnSound) {
         var1.play(SimpleSoundInstance.forUI(SoundEvents.BOOK_PAGE_TURN, 1.0F));
      }

   }

   public boolean shouldTakeFocusAfterInteraction() {
      return false;
   }
}
