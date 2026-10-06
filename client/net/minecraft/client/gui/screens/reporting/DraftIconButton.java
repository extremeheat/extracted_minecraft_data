package net.minecraft.client.gui.screens.reporting;

import java.util.function.Predicate;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.ScaledWidgetSprites;
import net.minecraft.client.gui.components.SpriteIconButton;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.client.multiplayer.chat.report.Report;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import org.jspecify.annotations.Nullable;

public class DraftIconButton extends SpriteIconButton {
   private static final ScaledWidgetSprites DRAFT_REPORT_SPRITE = new ScaledWidgetSprites(Identifier.withDefaultNamespace("icon/draft_report"), 15, 15);
   private final @Nullable Tooltip draftTooltip;
   private final Predicate<Report> isRelevant;

   public DraftIconButton(final int x, final int y, final int width, final int height, final Component message, final Button.OnPress onPress, final @Nullable Tooltip draftTooltip, final Predicate<Report> isRelevant) {
      super(x, y, width, height, message, DRAFT_REPORT_SPRITE, SpriteIconButton.DisplayState.TEXT_ONLY, onPress, (Tooltip)null, Button.DEFAULT_NARRATION, false);
      this.isRelevant = isRelevant;
      this.draftTooltip = draftTooltip;
   }

   public DraftIconButton(final int width, final int height, final Component message, final Button.OnPress onPress, final @Nullable Tooltip tooltip, final Predicate<Report> isRelevant) {
      this(0, 0, width, height, message, onPress, tooltip, isRelevant);
   }

   public void extractContents(final GuiGraphicsExtractor graphics, final int mouseX, final int mouseY, final float a) {
      Report report = Minecraft.getInstance().getReportingContext().getDraftReport();
      if (report != null && this.isRelevant.test(report)) {
         this.setDisplayState(SpriteIconButton.DisplayState.TEXT_AND_ICON);
         this.setTooltip(this.draftTooltip);
      } else {
         this.setDisplayState(SpriteIconButton.DisplayState.TEXT_ONLY);
         this.setTooltip((Tooltip)null);
      }

      super.extractContents(graphics, mouseX, mouseY, a);
   }
}
