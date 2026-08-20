package net.minecraft.client.gui.components;

import java.util.List;
import java.util.Objects;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.input.IMECandidatesEvent;
import net.minecraft.client.input.PreeditEvent;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.FormattedText;
import net.minecraft.network.chat.Style;
import net.minecraft.resources.Identifier;
import net.minecraft.util.Util;

public class IMEPreeditOverlay implements Renderable {
   private static final Identifier BACKGROUND = Identifier.withDefaultNamespace("widget/preedit");
   private static final Style FOCUSED_STYLE;
   private static final int SEPARATION_FROM_INPUT = 4;
   private static final int BORDER_MARGIN = 4;
   private static final int BORDER_WIDTH = 1;
   private static final int BORDER_OFFSET = 5;
   private static final int TEXT_COLOR = -16777216;
   private static final int SELECTED_CANDIDATE_BACKGROUND = -6700559;
   private static final int CANDIDATE_LINE_GAP = 1;
   private static final int CANDIDATE_ITEM_GAP = 6;
   private final Font font;
   private int inputLeft;
   private int inputTop;
   private final int inputHeight;
   private final long initTimeMs;
   private final Component preEditText;
   private final int preEditTextWidth;
   private final int caretPos;

   public IMEPreeditOverlay(final PreeditEvent contents, final Font font, final int inputHeight) {
      super();
      this.font = font;
      this.inputHeight = inputHeight;
      this.initTimeMs = Util.getMillis();
      this.preEditText = contents.toFormattedText(FOCUSED_STYLE).withColor(-16777216);
      this.preEditTextWidth = font.width((FormattedText)this.preEditText);
      String textBeforeCaret = contents.fullText().substring(0, contents.caretPosition());
      this.caretPos = font.width(textBeforeCaret);
   }

   public void updateInputPosition(final int inputLeft, final int inputTop) {
      this.inputLeft = inputLeft;
      this.inputTop = inputTop;
   }

   public void extractRenderState(final GuiGraphicsExtractor graphics, final int mouseX, final int mouseY, final float a) {
      int preeditLeft = this.inputLeft;
      int preeditRight = preeditLeft + this.preEditTextWidth;
      if (preeditRight > graphics.guiWidth()) {
         preeditLeft = graphics.guiWidth() - this.preEditTextWidth;
         preeditRight = preeditLeft + this.preEditTextWidth;
      }

      int inputBottom = this.inputTop + this.inputHeight;
      int var10000 = inputBottom + 4;
      Objects.requireNonNull(this.font);
      int preeditBottom = var10000 + 9;
      if (preeditBottom > graphics.guiHeight()) {
         var10000 = this.inputTop - 4;
         Objects.requireNonNull(this.font);
         preeditBottom = var10000 - 9;
      }

      Objects.requireNonNull(this.font);
      int preeditTop = preeditBottom - 9;
      Minecraft.getInstance().textInputManager().setTextInputArea(preeditLeft, preeditTop, preeditRight, preeditBottom);
      int backgroundWidth = preeditRight - preeditLeft + 10;
      int backgroundHeight = preeditBottom - preeditTop + 10;
      graphics.blitSprite(RenderPipelines.GUI_TEXTURED, BACKGROUND, preeditLeft - 5, preeditTop - 5, backgroundWidth, backgroundHeight);
      graphics.text(this.font, this.preEditText, preeditLeft, preeditTop, -16777216, false);
      if (TextCursorUtils.isCursorVisible(Util.getMillis() - this.initTimeMs)) {
         int var10001 = preeditLeft + this.caretPos;
         Objects.requireNonNull(this.font);
         TextCursorUtils.extractInsertCursor(graphics, var10001, preeditTop, -16777216, 9 + 1);
      }

      IMECandidatesEvent candidates = Minecraft.getInstance().keyboardHandler.getIMECandidates();
      if (candidates != null && !candidates.candidates().isEmpty()) {
         this.renderCandidates(graphics, candidates, preeditLeft, preeditTop, preeditBottom);
      }

   }

   private void renderCandidates(final GuiGraphicsExtractor graphics, final IMECandidatesEvent candidatesEvent, final int preeditLeft, final int preeditTop, final int preeditBottom) {
      List<String> candidates = candidatesEvent.candidates();
      boolean horizontal = candidatesEvent.horizontal();
      int selected = candidatesEvent.selectedCandidate();
      int numberOfCandidates = candidates.size();
      int contentWidth = 0;
      int contentHeight;
      if (horizontal) {
         for(int i = 0; i < numberOfCandidates; ++i) {
            if (i > 0) {
               contentWidth += 6;
            }

            contentWidth += this.font.width((String)candidates.get(i));
         }

         Objects.requireNonNull(this.font);
         contentHeight = 9;
      } else {
         for(String item : candidates) {
            contentWidth = Math.max(contentWidth, this.font.width(item));
         }

         Objects.requireNonNull(this.font);
         contentHeight = numberOfCandidates * 9 + (numberOfCandidates - 1) * 1;
      }

      int x = preeditLeft;
      if (preeditLeft + contentWidth > graphics.guiWidth()) {
         x = graphics.guiWidth() - contentWidth;
      }

      if (x < 5) {
         x = 5;
      }

      int y = preeditBottom + 5 + 4;
      if (y + contentHeight + 5 > graphics.guiHeight()) {
         y = preeditTop - 5 - 4 - contentHeight;
      }

      int backgroundWidth = contentWidth + 10;
      int backgroundHeight = contentHeight + 10;
      graphics.blitSprite(RenderPipelines.GUI_TEXTURED, BACKGROUND, x - 5, y - 5, backgroundWidth, backgroundHeight);
      if (horizontal) {
         int candidateX = x;

         for(int i = 0; i < numberOfCandidates; ++i) {
            String item = (String)candidates.get(i);
            int itemWidth = this.font.width(item);
            if (i == selected) {
               int var10001 = candidateX - 1;
               int var10002 = y - 1;
               int var10003 = candidateX + itemWidth + 1;
               Objects.requireNonNull(this.font);
               graphics.fill(var10001, var10002, var10003, y + 9, -6700559);
            }

            graphics.text(this.font, item, candidateX, y, -16777216, false);
            candidateX += itemWidth + 6;
         }
      } else {
         int candidateY = y;

         for(int i = 0; i < numberOfCandidates; ++i) {
            if (i == selected) {
               int var25 = x - 1;
               int var26 = candidateY - 1;
               int var27 = x + contentWidth + 1;
               Objects.requireNonNull(this.font);
               graphics.fill(var25, var26, var27, candidateY + 9, -6700559);
            }

            graphics.text(this.font, (String)candidates.get(i), x, candidateY, -16777216, false);
            Objects.requireNonNull(this.font);
            candidateY += 9 + 1;
         }
      }

   }

   static {
      FOCUSED_STYLE = Style.EMPTY.withUnderlined(true);
   }
}
