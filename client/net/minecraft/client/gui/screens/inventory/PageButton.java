package net.minecraft.client.gui.screens.inventory;

import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.GuiPipeline;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.WidgetSprites;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.client.sounds.SoundManager;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.sounds.SoundEvents;

public class PageButton extends Button {
   private static final WidgetSprites PAGE_FORWARD_SPRITES = new WidgetSprites(Identifier.withDefaultNamespace("widget/page_forward"), Identifier.withDefaultNamespace("widget/page_forward_highlighted"));
   private static final WidgetSprites PAGE_BACKWARD_SPRITES = new WidgetSprites(Identifier.withDefaultNamespace("widget/page_backward"), Identifier.withDefaultNamespace("widget/page_backward_highlighted"));
   private static final Component PAGE_BUTTON_NEXT = Component.translatable("book.page_button.next");
   private static final Component PAGE_BUTTON_PREVIOUS = Component.translatable("book.page_button.previous");
   private final boolean isForward;
   private final boolean playTurnSound;

   public PageButton(final int x, final int y, final boolean isForward, final Button.OnPress onPress, final boolean playTurnSound) {
      super(x, y, 23, 13, isForward ? PAGE_BUTTON_NEXT : PAGE_BUTTON_PREVIOUS, onPress, DEFAULT_NARRATION);
      this.isForward = isForward;
      this.playTurnSound = playTurnSound;
   }

   public void extractContents(final GuiGraphicsExtractor graphics) {
      graphics.blitSprite(GuiPipeline.TEXTURED, (Identifier)(this.isForward ? PAGE_FORWARD_SPRITES.get(this.isActive(), this.isHighlighted()) : PAGE_BACKWARD_SPRITES.get(this.isActive(), this.isHighlighted())), this.getX(), this.getY(), 23, 13);
   }

   public void playDownSound(final SoundManager soundManager) {
      if (this.playTurnSound) {
         soundManager.play(SimpleSoundInstance.forUI(SoundEvents.BOOK_PAGE_TURN, 1.0F));
      }

   }

   public boolean shouldTakeFocusAfterInteraction() {
      return false;
   }
}
