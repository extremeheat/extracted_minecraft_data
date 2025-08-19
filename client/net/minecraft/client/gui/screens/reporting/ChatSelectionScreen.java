package net.minecraft.client.gui.screens.reporting;

import com.mojang.authlib.GameProfile;
import com.mojang.authlib.minecraft.report.AbuseReportLimits;
import java.util.List;
import java.util.Objects;
import java.util.UUID;
import java.util.function.Consumer;
import java.util.function.Supplier;
import javax.annotation.Nullable;
import net.minecraft.Optionull;
import net.minecraft.client.GuiMessageTag;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractSelectionList;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.MultiLineLabel;
import net.minecraft.client.gui.components.ObjectSelectionList;
import net.minecraft.client.gui.components.PlayerFaceRenderer;
import net.minecraft.client.gui.navigation.CommonInputs;
import net.minecraft.client.gui.navigation.ScreenDirection;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.multiplayer.chat.ChatTrustLevel;
import net.minecraft.client.multiplayer.chat.LoggedChatMessage;
import net.minecraft.client.multiplayer.chat.report.ChatReport;
import net.minecraft.client.multiplayer.chat.report.ReportingContext;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.client.resources.PlayerSkin;
import net.minecraft.locale.Language;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.FormattedText;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.FormattedCharSequence;
import net.minecraft.util.Mth;

public class ChatSelectionScreen extends Screen {
   static final ResourceLocation CHECKMARK_SPRITE = ResourceLocation.withDefaultNamespace("icon/checkmark");
   private static final Component TITLE = Component.translatable("gui.chatSelection.title");
   private static final Component CONTEXT_INFO = Component.translatable("gui.chatSelection.context");
   @Nullable
   private final Screen lastScreen;
   private final ReportingContext reportingContext;
   private Button confirmSelectedButton;
   private MultiLineLabel contextInfoLabel;
   @Nullable
   private ChatSelectionList chatSelectionList;
   final ChatReport.Builder report;
   private final Consumer<ChatReport.Builder> onSelected;
   private ChatSelectionLogFiller chatLogFiller;

   public ChatSelectionScreen(@Nullable Screen var1, ReportingContext var2, ChatReport.Builder var3, Consumer<ChatReport.Builder> var4) {
      super(TITLE);
      this.lastScreen = var1;
      this.reportingContext = var2;
      this.report = var3.copy();
      this.onSelected = var4;
   }

   protected void init() {
      this.chatLogFiller = new ChatSelectionLogFiller(this.reportingContext, this::canReport);
      this.contextInfoLabel = MultiLineLabel.create(this.font, CONTEXT_INFO, this.width - 16);
      Minecraft var10005 = this.minecraft;
      int var10006 = this.contextInfoLabel.getLineCount() + 1;
      Objects.requireNonNull(this.font);
      this.chatSelectionList = (ChatSelectionList)this.addRenderableWidget(new ChatSelectionList(var10005, var10006 * 9));
      this.addRenderableWidget(Button.builder(CommonComponents.GUI_BACK, (var1) -> this.onClose()).bounds(this.width / 2 - 155, this.height - 32, 150, 20).build());
      this.confirmSelectedButton = (Button)this.addRenderableWidget(Button.builder(CommonComponents.GUI_DONE, (var1) -> {
         this.onSelected.accept(this.report);
         this.onClose();
      }).bounds(this.width / 2 - 155 + 160, this.height - 32, 150, 20).build());
      this.updateConfirmSelectedButton();
      this.extendLog();
      this.chatSelectionList.setScrollAmount((double)this.chatSelectionList.maxScrollAmount());
   }

   private boolean canReport(LoggedChatMessage var1) {
      return var1.canReport(this.report.reportedProfileId());
   }

   private void extendLog() {
      int var1 = this.chatSelectionList.getMaxVisibleEntries();
      this.chatLogFiller.fillNextPage(var1, this.chatSelectionList);
   }

   void onReachedScrollTop() {
      this.extendLog();
   }

   void updateConfirmSelectedButton() {
      this.confirmSelectedButton.active = !this.report.reportedMessages().isEmpty();
   }

   public void render(GuiGraphics var1, int var2, int var3, float var4) {
      super.render(var1, var2, var3, var4);
      var1.drawCenteredString(this.font, (Component)this.title, this.width / 2, 10, -1);
      AbuseReportLimits var5 = this.reportingContext.sender().reportLimits();
      int var6 = this.report.reportedMessages().size();
      int var7 = var5.maxReportedMessageCount();
      MutableComponent var8 = Component.translatable("gui.chatSelection.selected", var6, var7);
      var1.drawCenteredString(this.font, (Component)var8, this.width / 2, 26, -1);
      int var9 = this.chatSelectionList.getFooterTop();
      MultiLineLabel var10000 = this.contextInfoLabel;
      MultiLineLabel.Align var10002 = MultiLineLabel.Align.CENTER;
      int var10003 = this.width / 2;
      Objects.requireNonNull(this.font);
      var10000.render(var1, var10002, var10003, var9, 9, true, -1);
   }

   public void onClose() {
      this.minecraft.setScreen(this.lastScreen);
   }

   public Component getNarrationMessage() {
      return CommonComponents.joinForNarration(super.getNarrationMessage(), CONTEXT_INFO);
   }

   public class ChatSelectionList extends ObjectSelectionList<Entry> implements ChatSelectionLogFiller.Output {
      public static final int ITEM_HEIGHT = 16;
      @Nullable
      private Heading previousHeading;

      public ChatSelectionList(final Minecraft var2, final int var3) {
         super(var2, ChatSelectionScreen.this.width, ChatSelectionScreen.this.height - var3 - 80, 40, 16);
      }

      public void setScrollAmount(double var1) {
         double var3 = this.scrollAmount();
         super.setScrollAmount(var1);
         if ((float)this.maxScrollAmount() > 1.0E-5F && var1 <= 9.999999747378752E-6 && !Mth.equal(var1, var3)) {
            ChatSelectionScreen.this.onReachedScrollTop();
         }

      }

      public void acceptMessage(int var1, LoggedChatMessage.Player var2) {
         boolean var3 = var2.canReport(ChatSelectionScreen.this.report.reportedProfileId());
         ChatTrustLevel var4 = var2.trustLevel();
         GuiMessageTag var5 = var4.createTag(var2.message());
         MessageEntry var6 = new MessageEntry(var1, var2.toContentComponent(), var2.toNarrationComponent(), var5, var3, true);
         this.addEntryToTop(var6);
         this.updateHeading(var2, var3);
      }

      private void updateHeading(LoggedChatMessage.Player var1, boolean var2) {
         MessageHeadingEntry var3 = new MessageHeadingEntry(var1.profile(), var1.toHeadingComponent(), var2);
         this.addEntryToTop(var3);
         Heading var4 = new Heading(var1.profileId(), var3);
         if (this.previousHeading != null && this.previousHeading.canCombine(var4)) {
            this.removeEntryFromTop(this.previousHeading.entry());
         }

         this.previousHeading = var4;
      }

      public void acceptDivider(Component var1) {
         this.addEntryToTop(new PaddingEntry());
         this.addEntryToTop(new DividerEntry(var1));
         this.addEntryToTop(new PaddingEntry());
         this.previousHeading = null;
      }

      public int getRowWidth() {
         return Math.min(350, this.width - 50);
      }

      public int getMaxVisibleEntries() {
         return Mth.positiveCeilDiv(this.height, 16);
      }

      protected void renderItem(GuiGraphics var1, int var2, int var3, float var4, Entry var5) {
         if (this.shouldHighlightEntry(var5)) {
            boolean var6 = this.getSelected() == var5;
            int var7 = this.isFocused() && var6 ? -1 : -8355712;
            this.renderSelection(var1, var5, var7);
         }

         var5.renderContent(var1, var2, var3, this.getHovered() == var5, var4);
      }

      private boolean shouldHighlightEntry(Entry var1) {
         if (var1.canSelect()) {
            boolean var2 = this.getSelected() == var1;
            boolean var3 = this.getSelected() == null;
            boolean var4 = this.getHovered() == var1;
            return var2 || var3 && var4 && var1.canReport();
         } else {
            return false;
         }
      }

      @Nullable
      protected Entry nextEntry(ScreenDirection var1) {
         return (Entry)this.nextEntry(var1, Entry::canSelect);
      }

      public void setSelected(@Nullable Entry var1) {
         super.setSelected(var1);
         Entry var2 = this.nextEntry(ScreenDirection.UP);
         if (var2 == null) {
            ChatSelectionScreen.this.onReachedScrollTop();
         }

      }

      public boolean keyPressed(int var1, int var2, int var3) {
         Entry var4 = (Entry)this.getSelected();
         return var4 != null && var4.keyPressed(var1, var2, var3) ? true : super.keyPressed(var1, var2, var3);
      }

      public int getFooterTop() {
         int var10000 = this.getBottom();
         Objects.requireNonNull(ChatSelectionScreen.this.font);
         return var10000 + 9;
      }

      // $FF: synthetic method
      @Nullable
      protected AbstractSelectionList.Entry nextEntry(final ScreenDirection var1) {
         return this.nextEntry(var1);
      }

      static record Heading(UUID sender, Entry entry) {
         Heading(UUID var1, Entry var2) {
            super();
            this.sender = var1;
            this.entry = var2;
         }

         public boolean canCombine(Heading var1) {
            return var1.sender.equals(this.sender);
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

         public boolean mouseClicked(double var1, double var3, int var5, boolean var6) {
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
         @Nullable
         private final List<FormattedCharSequence> hoverText;
         @Nullable
         private final GuiMessageTag.Icon tagIcon;
         @Nullable
         private final List<FormattedCharSequence> tagHoverText;
         private final boolean canReport;
         private final boolean playerMessage;

         public MessageEntry(final int var2, final Component var3, final Component var4, @Nullable final GuiMessageTag var5, final boolean var6, final boolean var7) {
            super();
            this.chatId = var2;
            this.tagIcon = (GuiMessageTag.Icon)Optionull.map(var5, GuiMessageTag::icon);
            this.tagHoverText = var5 != null && var5.text() != null ? ChatSelectionScreen.this.font.split(var5.text(), ChatSelectionList.this.getRowWidth()) : null;
            this.canReport = var6;
            this.playerMessage = var7;
            FormattedText var8 = ChatSelectionScreen.this.font.substrByWidth(var3, this.getMaximumTextWidth() - ChatSelectionScreen.this.font.width((FormattedText)CommonComponents.ELLIPSIS));
            if (var3 != var8) {
               this.text = FormattedText.composite(var8, CommonComponents.ELLIPSIS);
               this.hoverText = ChatSelectionScreen.this.font.split(var3, ChatSelectionList.this.getRowWidth());
            } else {
               this.text = var3;
               this.hoverText = null;
            }

            this.narration = var4;
         }

         public void renderContent(GuiGraphics var1, int var2, int var3, boolean var4, float var5) {
            if (this.isSelected() && this.canReport) {
               this.renderSelectedCheckmark(var1, this.getContentY(), this.getContentX(), this.getContentHeight());
            }

            int var6 = this.getContentX() + this.getTextIndent();
            int var10000 = this.getContentY() + 1;
            int var10001 = this.getContentHeight();
            Objects.requireNonNull(ChatSelectionScreen.this.font);
            int var7 = var10000 + (var10001 - 9) / 2;
            var1.drawString(ChatSelectionScreen.this.font, Language.getInstance().getVisualOrder(this.text), var6, var7, this.canReport ? -1 : -1593835521);
            if (this.hoverText != null && var4) {
               var1.setTooltipForNextFrame(this.hoverText, var2, var3);
            }

            int var8 = ChatSelectionScreen.this.font.width(this.text);
            this.renderTag(var1, var6 + var8 + 4, this.getContentY(), this.getContentHeight(), var2, var3);
         }

         private void renderTag(GuiGraphics var1, int var2, int var3, int var4, int var5, int var6) {
            if (this.tagIcon != null) {
               int var7 = var3 + (var4 - this.tagIcon.height) / 2;
               this.tagIcon.draw(var1, var2, var7);
               if (this.tagHoverText != null && var5 >= var2 && var5 <= var2 + this.tagIcon.width && var6 >= var7 && var6 <= var7 + this.tagIcon.height) {
                  var1.setTooltipForNextFrame(this.tagHoverText, var5, var6);
               }
            }

         }

         private void renderSelectedCheckmark(GuiGraphics var1, int var2, int var3, int var4) {
            int var6 = var2 + (var4 - 8) / 2;
            var1.blitSprite(RenderPipelines.GUI_TEXTURED, (ResourceLocation)ChatSelectionScreen.CHECKMARK_SPRITE, var3, var6, 9, 8);
         }

         private int getMaximumTextWidth() {
            int var1 = this.tagIcon != null ? this.tagIcon.width + 4 : 0;
            return ChatSelectionList.this.getRowWidth() - this.getTextIndent() - 4 - var1;
         }

         private int getTextIndent() {
            return this.playerMessage ? 11 : 0;
         }

         public Component getNarration() {
            return (Component)(this.isSelected() ? Component.translatable("narrator.select", this.narration) : this.narration);
         }

         public boolean mouseClicked(double var1, double var3, int var5, boolean var6) {
            ChatSelectionList.this.setSelected((Entry)null);
            return this.toggleReport();
         }

         public boolean keyPressed(int var1, int var2, int var3) {
            return CommonInputs.selected(var1) ? this.toggleReport() : false;
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

         public MessageHeadingEntry(final GameProfile var2, final Component var3, final boolean var4) {
            super();
            this.heading = var3;
            this.canReport = var4;
            this.skin = ChatSelectionList.this.minecraft.getSkinManager().createLookup(var2, true);
         }

         public void renderContent(GuiGraphics var1, int var2, int var3, boolean var4, float var5) {
            int var6 = this.getContentX() - 12 + 4;
            int var7 = this.getContentY() + (this.getContentHeight() - 12) / 2;
            PlayerFaceRenderer.draw(var1, (PlayerSkin)this.skin.get(), var6, var7, 12);
            int var10000 = this.getContentY() + 1;
            int var10001 = this.getContentHeight();
            Objects.requireNonNull(ChatSelectionScreen.this.font);
            int var8 = var10000 + (var10001 - 9) / 2;
            var1.drawString(ChatSelectionScreen.this.font, this.heading, var6 + 12 + 4, var8, this.canReport ? -1 : -1593835521);
         }
      }

      public class DividerEntry extends Entry {
         private final Component text;

         public DividerEntry(final Component var2) {
            super();
            this.text = var2;
         }

         public void renderContent(GuiGraphics var1, int var2, int var3, boolean var4, float var5) {
            int var6 = this.getContentYMiddle();
            int var7 = this.getContentRight() - 8;
            int var8 = ChatSelectionScreen.this.font.width((FormattedText)this.text);
            int var9 = (this.getContentX() + var7 - var8) / 2;
            Objects.requireNonNull(ChatSelectionScreen.this.font);
            int var10 = var6 - 9 / 2;
            var1.drawString(ChatSelectionScreen.this.font, this.text, var9, var10, -6250336);
         }

         public Component getNarration() {
            return this.text;
         }
      }

      public static class PaddingEntry extends Entry {
         public PaddingEntry() {
            super();
         }

         public void renderContent(GuiGraphics var1, int var2, int var3, boolean var4, float var5) {
         }
      }
   }
}
