package net.minecraft.client.gui.screens.social;

import java.net.URI;
import java.util.List;
import java.util.Objects;
import java.util.function.Consumer;
import java.util.function.UnaryOperator;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.ImageWidget;
import net.minecraft.client.gui.components.MultiLineTextWidget;
import net.minecraft.client.gui.layouts.LinearLayout;
import net.minecraft.client.gui.layouts.SpacerElement;
import net.minecraft.client.gui.screens.PrivacyConfirmLinkScreen;
import net.minecraft.client.gui.screens.friends.AbstractFriendsTab;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.network.chat.ClickEvent;
import net.minecraft.network.chat.Component;
import net.minecraft.util.CommonLinks;

class PlayerInteractionsTab extends AbstractFriendsTab {
   private static final Component EMPTY;
   public static final Component EMPTY_SEARCH;
   public static final Component EMPTY_MUTED;
   private static final Component MICROSOFT_ACCOUNT_LINK;
   public static final Component EMPTY_BLOCKED;
   private static final Component MANAGE_ACCOUNT_FOOTER;
   private static final int HEADER_VERTICAL_PADDING = 5;
   private final SocialInteractionsScreen screen;
   private final SocialInteractionsScreen.Page page;
   private boolean isEmpty;

   public PlayerInteractionsTab(final Minecraft minecraft, final SocialInteractionsScreen screen, final SocialInteractionsScreen.Page page, final int width, final int height, final LinearLayout widgets) {
      super(minecraft, width, height);
      this.screen = screen;
      this.page = page;
      LinearLayout header = (LinearLayout)this.headerFrame.addChild(LinearLayout.vertical());
      header.addChild(widgets, (Consumer)((settings) -> settings.paddingVertical(5).alignHorizontallyCenter()));
      header.addChild(ImageWidget.sprite(width, 2, LIST_SEPARATOR_TOP));
      this.rearrangeElements();
   }

   public Component getTabExtraNarration() {
      return Component.empty();
   }

   public SocialInteractionsScreen.Page getPage() {
      return this.page;
   }

   public void replaceEntries(final List<PlayerEntry> entries, final boolean filterEmpty) {
      if (entries.isEmpty()) {
         this.showEmpty(EMPTY_SEARCH);
      } else {
         this.scrollableLayout.alignVerticallyTop();
         this.scrollableContent.removeChildren();
         this.scrollableContent.addChild(SpacerElement.height(3));
         LinearLayout var10001 = this.scrollableContent;
         Objects.requireNonNull(var10001);
         entries.forEach(var10001::addChild);
         this.isEmpty = false;
         if (filterEmpty && this.page == SocialInteractionsScreen.Page.BLOCKED) {
            this.showManageAccountFooter(this.screen, MANAGE_ACCOUNT_FOOTER);
         }
      }

   }

   public void addEntry(final PlayerEntry entry, final boolean filterEmpty) {
      if (this.isEmpty) {
         this.replaceEntries(List.of(entry), filterEmpty);
      } else {
         this.scrollableContent.addChild(entry);
      }

   }

   private void showEmpty(final Component message) {
      MultiLineTextWidget text = this.createCenteredText(message, this.screen.getFont(), this.getListContentWidth());
      text.setComponentClickHandler((style) -> {
         ClickEvent patt1$temp = style.getClickEvent();
         if (patt1$temp instanceof ClickEvent.OpenUrl $b$0) {
            ClickEvent.OpenUrl var10000 = $b$0;

            try {
               var7 = var10000.uri();
            } catch (Throwable var6) {
               throw new MatchException(var6.toString(), var6);
            }

            URI patt2$temp = var7;
            PrivacyConfirmLinkScreen.confirmLinkNow(this.screen, patt2$temp);
         }

      });
      this.showCenteredContent(text);
      this.isEmpty = true;
   }

   public void showEmpty() {
      switch (this.page) {
         case ALL -> this.showEmpty(EMPTY);
         case MUTED -> this.showEmpty(EMPTY_MUTED);
         case BLOCKED -> this.showEmpty(EMPTY_BLOCKED);
      }

   }

   public void extractBackground(final GuiGraphicsExtractor graphics, final int mouseX, final int mouseY, final float a) {
      graphics.blitSprite(RenderPipelines.GUI_TEXTURED, BACKGROUND_LIGHT_SPRITE, this.headerFrame.getX(), this.headerFrame.getY(), this.headerFrame.getWidth(), this.headerFrame.getHeight() - 2);
   }

   static {
      EMPTY = Component.translatable("gui.player_interactions.empty").withStyle(ChatFormatting.GRAY);
      EMPTY_SEARCH = Component.translatable("gui.player_interactions.search.no_results").withStyle(ChatFormatting.GRAY);
      EMPTY_MUTED = Component.translatable("gui.player_interactions.empty_muted").withStyle(ChatFormatting.GRAY);
      MICROSOFT_ACCOUNT_LINK = Component.translatable("gui.player_interactions.empty_blocked.link").withStyle((UnaryOperator)((style) -> style.withUnderlined(true).withColor(ChatFormatting.GRAY).withClickEvent(new ClickEvent.OpenUrl(CommonLinks.BLOCKING_HELP))));
      EMPTY_BLOCKED = Component.translatable("gui.player_interactions.empty_blocked", MICROSOFT_ACCOUNT_LINK).withStyle(ChatFormatting.GRAY);
      MANAGE_ACCOUNT_FOOTER = Component.translatable("gui.player_interactions.manage_account_footer", MICROSOFT_ACCOUNT_LINK).withStyle(ChatFormatting.GRAY);
   }
}
