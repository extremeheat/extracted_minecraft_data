package net.minecraft.client.gui.screens.reporting;

import com.mojang.authlib.GameProfile;
import com.mojang.authlib.minecraft.report.AbuseReportLimits;
import java.util.List;
import java.util.Objects;
import java.util.UUID;
import java.util.function.Consumer;
import java.util.function.Supplier;
import net.minecraft.Optionull;
import net.minecraft.client.GuiMessageTag;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.ActiveTextCollector;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.TextAlignment;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.MultiLineLabel;
import net.minecraft.client.gui.components.ObjectSelectionList;
import net.minecraft.client.gui.components.PlayerFaceRenderer;
import net.minecraft.client.gui.navigation.ScreenDirection;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.input.KeyEvent;
import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.client.multiplayer.chat.ChatTrustLevel;
import net.minecraft.client.multiplayer.chat.LoggedChatMessage;
import net.minecraft.client.multiplayer.chat.report.ChatReport;
import net.minecraft.client.multiplayer.chat.report.ReportingContext;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.locale.Language;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.FormattedText;
import net.minecraft.resources.Identifier;
import net.minecraft.util.FormattedCharSequence;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.player.PlayerSkin;
import org.jspecify.annotations.Nullable;

public class ChatSelectionScreen extends Screen {
   private static final Identifier CHECKMARK_SPRITE = Identifier.withDefaultNamespace("icon/checkmark");
   private static final Component TITLE = Component.translatable("gui.chatSelection.title");
   private static final Component CONTEXT_INFO = Component.translatable("gui.chatSelection.context");
   private final @Nullable Screen lastScreen;
   private final ReportingContext reportingContext;
   private Button confirmSelectedButton;
   private MultiLineLabel contextInfoLabel;
   private @Nullable ChatSelectionList chatSelectionList;
   private final ChatReport.Builder report;
   private final Consumer<ChatReport.Builder> onSelected;
   private ChatSelectionLogFiller chatLogFiller;

   public ChatSelectionScreen(final @Nullable Screen lastScreen, final ReportingContext reportingContext, final ChatReport.Builder report, final Consumer<ChatReport.Builder> onSelected) {
      super(TITLE);
      this.lastScreen = lastScreen;
      this.reportingContext = reportingContext;
      this.report = report.copy();
      this.onSelected = onSelected;
   }

   protected void init() {
      this.chatLogFiller = new ChatSelectionLogFiller(this.reportingContext, this::canReport);
      this.contextInfoLabel = MultiLineLabel.create(this.font, CONTEXT_INFO, this.width - 16);
      Minecraft var10005 = this.minecraft;
      int var10006 = this.contextInfoLabel.getLineCount() + 1;
      Objects.requireNonNull(this.font);
      this.chatSelectionList = (ChatSelectionList)this.addRenderableWidget(new ChatSelectionList(var10005, var10006 * 9));
      this.addRenderableWidget(Button.builder(CommonComponents.GUI_BACK, (b) -> this.onClose()).bounds(this.width / 2 - 155, this.height - 32, 150, 20).build());
      this.confirmSelectedButton = (Button)this.addRenderableWidget(Button.builder(CommonComponents.GUI_DONE, (b) -> {
         this.onSelected.accept(this.report);
         this.onClose();
      }).bounds(this.width / 2 - 155 + 160, this.height - 32, 150, 20).build());
      this.updateConfirmSelectedButton();
      this.extendLog();
      this.chatSelectionList.setScrollAmount((double)this.chatSelectionList.maxScrollAmount());
   }

   private boolean canReport(final LoggedChatMessage message) {
      return message.canReport(this.report.reportedProfileId());
   }

   private void extendLog() {
      int pageSize = this.chatSelectionList.getMaxVisibleEntries();
      this.chatLogFiller.fillNextPage(pageSize, this.chatSelectionList);
   }

   private void onReachedScrollTop() {
      this.extendLog();
   }

   private void updateConfirmSelectedButton() {
      this.confirmSelectedButton.active = !this.report.reportedMessages().isEmpty();
   }

   public void render(final GuiGraphics graphics, final int mouseX, final int mouseY, final float a) {
      super.render(graphics, mouseX, mouseY, a);
      ActiveTextCollector textRenderer = graphics.textRenderer();
      graphics.drawCenteredString(this.font, (Component)this.title, this.width / 2, 10, -1);
      AbuseReportLimits reportLimits = this.reportingContext.sender().reportLimits();
      int messageCount = this.report.reportedMessages().size();
      int maxMessageCount = reportLimits.maxReportedMessageCount();
      Component selectedText = Component.translatable("gui.chatSelection.selected", messageCount, maxMessageCount);
      graphics.drawCenteredString(this.font, (Component)selectedText, this.width / 2, 26, -1);
      int topY = this.chatSelectionList.getFooterTop();
      MultiLineLabel var10000 = this.contextInfoLabel;
      TextAlignment var10001 = TextAlignment.CENTER;
      int var10002 = this.width / 2;
      Objects.requireNonNull(this.font);
      var10000.visitLines(var10001, var10002, topY, 9, textRenderer);
   }

   public void onClose() {
      this.minecraft.setScreen(this.lastScreen);
   }

   public Component getNarrationMessage() {
      return CommonComponents.joinForNarration(super.getNarrationMessage(), CONTEXT_INFO);
   }

   public class ChatSelectionList extends ObjectSelectionList<Entry> implements ChatSelectionLogFiller.Output {
      public static final int ITEM_HEIGHT = 16;
      private @Nullable Heading previousHeading;

      public ChatSelectionList(final Minecraft minecraft, final int upperMargin) {
         Objects.requireNonNull(ChatSelectionScreen.this);
         super(minecraft, ChatSelectionScreen.this.width, ChatSelectionScreen.this.height - upperMargin - 80, 40, 16);
      }

      public void setScrollAmount(final double scrollAmount) {
         double prevScrollAmount = this.scrollAmount();
         super.setScrollAmount(scrollAmount);
         if ((float)this.maxScrollAmount() > 1.0E-5F && scrollAmount <= 9.999999747378752E-6 && !Mth.equal(scrollAmount, prevScrollAmount)) {
            ChatSelectionScreen.this.onReachedScrollTop();
         }

      }

      public void acceptMessage(final int id, final LoggedChatMessage.Player message) {
         boolean canReport = message.canReport(ChatSelectionScreen.this.report.reportedProfileId());
         ChatTrustLevel trustLevel = message.trustLevel();
         GuiMessageTag tag = trustLevel.createTag(message.message());
         Entry entry = new MessageEntry(id, message.toContentComponent(), message.toNarrationComponent(), tag, canReport, true);
         this.addEntryToTop(entry);
         this.updateHeading(message, canReport);
      }

      private void updateHeading(final LoggedChatMessage.Player message, final boolean canReport) {
         Entry entry = new MessageHeadingEntry(message.profile(), message.toHeadingComponent(), canReport);
         this.addEntryToTop(entry);
         Heading heading = new Heading(message.profileId(), entry);
         if (this.previousHeading != null && this.previousHeading.canCombine(heading)) {
            this.removeEntryFromTop(this.previousHeading.entry());
         }

         this.previousHeading = heading;
      }

      public void acceptDivider(final Component text) {
         this.addEntryToTop(new PaddingEntry());
         this.addEntryToTop(new DividerEntry(text));
         this.addEntryToTop(new PaddingEntry());
         this.previousHeading = null;
      }

      public int getRowWidth() {
         return Math.min(350, this.width - 50);
      }

      public int getMaxVisibleEntries() {
         return Mth.positiveCeilDiv(this.height, 16);
      }

      protected void renderItem(final GuiGraphics graphics, final int mouseX, final int mouseY, final float a, final Entry entry) {
         if (this.shouldHighlightEntry(entry)) {
            boolean selected = this.getSelected() == entry;
            int outlineColor = this.isFocused() && selected ? -1 : -8355712;
            this.renderSelection(graphics, entry, outlineColor);
         }

         entry.renderContent(graphics, mouseX, mouseY, this.getHovered() == entry, a);
      }

      private boolean shouldHighlightEntry(final Entry entry) {
         if (entry.canSelect()) {
            boolean entrySelected = this.getSelected() == entry;
            boolean nothingSelected = this.getSelected() == null;
            boolean entryHovered = this.getHovered() == entry;
            return entrySelected || nothingSelected && entryHovered && entry.canReport();
         } else {
            return false;
         }
      }

      protected @Nullable Entry nextEntry(final ScreenDirection dir) {
         return (Entry)this.nextEntry(dir, Entry::canSelect);
      }

      public void setSelected(final @Nullable Entry selected) {
         super.setSelected(selected);
         Entry entry = this.nextEntry(ScreenDirection.UP);
         if (entry == null) {
            ChatSelectionScreen.this.onReachedScrollTop();
         }

      }

      public boolean keyPressed(final KeyEvent event) {
         Entry selected = (Entry)this.getSelected();
         return selected != null && selected.keyPressed(event) ? true : super.keyPressed(event);
      }

      public int getFooterTop() {
         int var10000 = this.getBottom();
         Objects.requireNonNull(ChatSelectionScreen.this.font);
         return var10000 + 9;
      }

      private static record Heading(UUID sender, Entry entry) {
         private Heading {
            super();
         }

         public boolean canCombine(final Heading other) {
            return other.sender.equals(this.sender);
         }
      }

      public abstract static class Entry extends ObjectSelectionList.Entry<Entry> {
         public Entry() {
            super();
         }

         public Component getNarration() {
            return CommonComponents.EMPTY;
         }

         public boolean isSelected() {
            return false;
         }

         public boolean canSelect() {
            return false;
         }

         public boolean canReport() {
            return this.canSelect();
         }

         public boolean mouseClicked(final MouseButtonEvent event, final boolean doubleClick) {
            return this.canSelect();
         }
      }

      public class MessageEntry extends Entry {
         private static final int CHECKMARK_WIDTH = 9;
         private static final int CHECKMARK_HEIGHT = 8;
         private static final int INDENT_AMOUNT = 11;
         private static final int TAG_MARGIN_LEFT = 4;
         private final int chatId;
         private final FormattedText text;
         private final Component narration;
         private final @Nullable List<FormattedCharSequence> hoverText;
         private final GuiMessageTag.@Nullable Icon tagIcon;
         private final @Nullable List<FormattedCharSequence> tagHoverText;
         private final boolean canReport;
         private final boolean playerMessage;

         public MessageEntry(final int chatId, final Component text, final @Nullable Component narration, final GuiMessageTag tag, final boolean canReport, final boolean playerMessage) {
            Objects.requireNonNull(ChatSelectionList.this);
            super();
            this.chatId = chatId;
            this.tagIcon = (GuiMessageTag.Icon)Optionull.map(tag, GuiMessageTag::icon);
            this.tagHoverText = tag != null && tag.text() != null ? ChatSelectionScreen.this.font.split(tag.text(), ChatSelectionList.this.getRowWidth()) : null;
            this.canReport = canReport;
            this.playerMessage = playerMessage;
            FormattedText shortText = ChatSelectionScreen.this.font.substrByWidth(text, this.getMaximumTextWidth() - ChatSelectionScreen.this.font.width((FormattedText)CommonComponents.ELLIPSIS));
            if (text != shortText) {
               this.text = FormattedText.composite(shortText, CommonComponents.ELLIPSIS);
               this.hoverText = ChatSelectionScreen.this.font.split(text, ChatSelectionList.this.getRowWidth());
            } else {
               this.text = text;
               this.hoverText = null;
            }

            this.narration = narration;
         }

         public void renderContent(final GuiGraphics graphics, final int mouseX, final int mouseY, final boolean hovered, final float a) {
            if (this.isSelected() && this.canReport) {
               this.renderSelectedCheckmark(graphics, this.getContentY(), this.getContentX(), this.getContentHeight());
            }

            int textX = this.getContentX() + this.getTextIndent();
            int var10000 = this.getContentY() + 1;
            int var10001 = this.getContentHeight();
            Objects.requireNonNull(ChatSelectionScreen.this.font);
            int textY = var10000 + (var10001 - 9) / 2;
            graphics.drawString(ChatSelectionScreen.this.font, Language.getInstance().getVisualOrder(this.text), textX, textY, this.canReport ? -1 : -1593835521);
            if (this.hoverText != null && hovered) {
               graphics.setTooltipForNextFrame(this.hoverText, mouseX, mouseY);
            }

            int textWidth = ChatSelectionScreen.this.font.width(this.text);
            this.renderTag(graphics, textX + textWidth + 4, this.getContentY(), this.getContentHeight(), mouseX, mouseY);
         }

         private void renderTag(final GuiGraphics graphics, final int iconLeft, final int rowTop, final int rowHeight, final int mouseX, final int mouseY) {
            if (this.tagIcon != null) {
               int iconTop = rowTop + (rowHeight - this.tagIcon.height) / 2;
               this.tagIcon.draw(graphics, iconLeft, iconTop);
               if (this.tagHoverText != null && mouseX >= iconLeft && mouseX <= iconLeft + this.tagIcon.width && mouseY >= iconTop && mouseY <= iconTop + this.tagIcon.height) {
                  graphics.setTooltipForNextFrame(this.tagHoverText, mouseX, mouseY);
               }
            }

         }

         private void renderSelectedCheckmark(final GuiGraphics graphics, final int rowTop, final int rowLeft, final int rowHeight) {
            int top = rowTop + (rowHeight - 8) / 2;
            graphics.blitSprite(RenderPipelines.GUI_TEXTURED, (Identifier)ChatSelectionScreen.CHECKMARK_SPRITE, rowLeft, top, 9, 8);
         }

         private int getMaximumTextWidth() {
            int tagMargin = this.tagIcon != null ? this.tagIcon.width + 4 : 0;
            return ChatSelectionList.this.getRowWidth() - this.getTextIndent() - 4 - tagMargin;
         }

         private int getTextIndent() {
            return this.playerMessage ? 11 : 0;
         }

         public Component getNarration() {
            return (Component)(this.isSelected() ? Component.translatable("narrator.select", this.narration) : this.narration);
         }

         public boolean mouseClicked(final MouseButtonEvent event, final boolean doubleClick) {
            ChatSelectionList.this.setSelected((Entry)null);
            return this.toggleReport();
         }

         public boolean keyPressed(final KeyEvent event) {
            return event.isSelection() ? this.toggleReport() : false;
         }

         public boolean isSelected() {
            return ChatSelectionScreen.this.report.isReported(this.chatId);
         }

         public boolean canSelect() {
            return true;
         }

         public boolean canReport() {
            return this.canReport;
         }

         private boolean toggleReport() {
            if (this.canReport) {
               ChatSelectionScreen.this.report.toggleReported(this.chatId);
               ChatSelectionScreen.this.updateConfirmSelectedButton();
               return true;
            } else {
               return false;
            }
         }
      }

      public class MessageHeadingEntry extends Entry {
         private static final int FACE_SIZE = 12;
         private static final int PADDING = 4;
         private final Component heading;
         private final Supplier<PlayerSkin> skin;
         private final boolean canReport;

         public MessageHeadingEntry(final GameProfile profile, final Component heading, final boolean canReport) {
            Objects.requireNonNull(ChatSelectionList.this);
            super();
            this.heading = heading;
            this.canReport = canReport;
            this.skin = ChatSelectionList.this.minecraft.getSkinManager().createLookup(profile, true);
         }

         public void renderContent(final GuiGraphics graphics, final int mouseX, final int mouseY, final boolean hovered, final float a) {
            int faceX = this.getContentX() - 12 + 4;
            int faceY = this.getContentY() + (this.getContentHeight() - 12) / 2;
            PlayerFaceRenderer.draw(graphics, (PlayerSkin)this.skin.get(), faceX, faceY, 12);
            int var10000 = this.getContentY() + 1;
            int var10001 = this.getContentHeight();
            Objects.requireNonNull(ChatSelectionScreen.this.font);
            int textY = var10000 + (var10001 - 9) / 2;
            graphics.drawString(ChatSelectionScreen.this.font, this.heading, faceX + 12 + 4, textY, this.canReport ? -1 : -1593835521);
         }
      }

      public class DividerEntry extends Entry {
         private final Component text;

         public DividerEntry(final Component text) {
            Objects.requireNonNull(ChatSelectionList.this);
            super();
            this.text = text;
         }

         public void renderContent(final GuiGraphics graphics, final int mouseX, final int mouseY, final boolean hovered, final float a) {
            int centerY = this.getContentYMiddle();
            int rowRight = this.getContentRight() - 8;
            int textWidth = ChatSelectionScreen.this.font.width((FormattedText)this.text);
            int textLeft = (this.getContentX() + rowRight - textWidth) / 2;
            Objects.requireNonNull(ChatSelectionScreen.this.font);
            int textTop = centerY - 9 / 2;
            graphics.drawString(ChatSelectionScreen.this.font, this.text, textLeft, textTop, -6250336);
         }

         public Component getNarration() {
            return this.text;
         }
      }

      public static class PaddingEntry extends Entry {
         public PaddingEntry() {
            super();
         }

         public void renderContent(final GuiGraphics graphics, final int mouseX, final int mouseY, final boolean hovered, final float a) {
         }
      }
   }
}
