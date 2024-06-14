package net.minecraft.client.gui.screens.social;

import java.util.Collection;
import java.util.Locale;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;
import javax.annotation.Nullable;
import net.minecraft.ChatFormatting;
import net.minecraft.client.GameNarrator;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.layouts.HeaderAndFooterLayout;
import net.minecraft.client.gui.screens.ConfirmLinkScreen;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.multiplayer.PlayerInfo;
import net.minecraft.client.multiplayer.ServerData;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.CommonLinks;

public class SocialInteractionsScreen extends Screen {
   private static final Component TITLE = Component.translatable("gui.socialInteractions.title");
   private static final ResourceLocation BACKGROUND_SPRITE = ResourceLocation.withDefaultNamespace("social_interactions/background");
   private static final ResourceLocation SEARCH_SPRITE = ResourceLocation.withDefaultNamespace("icon/search");
   private static final Component TAB_ALL = Component.translatable("gui.socialInteractions.tab_all");
   private static final Component TAB_HIDDEN = Component.translatable("gui.socialInteractions.tab_hidden");
   private static final Component TAB_BLOCKED = Component.translatable("gui.socialInteractions.tab_blocked");
   private static final Component TAB_ALL_SELECTED = TAB_ALL.plainCopy().withStyle(ChatFormatting.UNDERLINE);
   private static final Component TAB_HIDDEN_SELECTED = TAB_HIDDEN.plainCopy().withStyle(ChatFormatting.UNDERLINE);
   private static final Component TAB_BLOCKED_SELECTED = TAB_BLOCKED.plainCopy().withStyle(ChatFormatting.UNDERLINE);
   private static final Component SEARCH_HINT = Component.translatable("gui.socialInteractions.search_hint")
      .withStyle(ChatFormatting.ITALIC)
      .withStyle(ChatFormatting.GRAY);
   static final Component EMPTY_SEARCH = Component.translatable("gui.socialInteractions.search_empty").withStyle(ChatFormatting.GRAY);
   private static final Component EMPTY_HIDDEN = Component.translatable("gui.socialInteractions.empty_hidden").withStyle(ChatFormatting.GRAY);
   private static final Component EMPTY_BLOCKED = Component.translatable("gui.socialInteractions.empty_blocked").withStyle(ChatFormatting.GRAY);
   private static final Component BLOCKING_HINT = Component.translatable("gui.socialInteractions.blocking_hint");
   private static final int BG_BORDER_SIZE = 8;
   private static final int BG_WIDTH = 236;
   private static final int SEARCH_HEIGHT = 16;
   private static final int MARGIN_Y = 64;
   public static final int SEARCH_START = 72;
   public static final int LIST_START = 88;
   private static final int IMAGE_WIDTH = 238;
   private static final int BUTTON_HEIGHT = 20;
   private static final int ITEM_HEIGHT = 36;
   private final HeaderAndFooterLayout layout = new HeaderAndFooterLayout(this);
   @Nullable
   private final Screen lastScreen;
   SocialInteractionsPlayerList socialInteractionsPlayerList;
   EditBox searchBox;
   private String lastSearch = "";
   private SocialInteractionsScreen.Page page = SocialInteractionsScreen.Page.ALL;
   private Button allButton;
   private Button hiddenButton;
   private Button blockedButton;
   private Button blockingHintButton;
   @Nullable
   private Component serverLabel;
   private int playerCount;

   public SocialInteractionsScreen() {
      this(null);
   }

   public SocialInteractionsScreen(@Nullable Screen var1) {
      super(TITLE);
      this.lastScreen = var1;
      this.updateServerLabel(Minecraft.getInstance());
   }

   private int windowHeight() {
      return Math.max(52, this.height - 128 - 16);
   }

   private int listEnd() {
      return 80 + this.windowHeight() - 8;
   }

   private int marginX() {
      return (this.width - 238) / 2;
   }

   @Override
   public Component getNarrationMessage() {
      return (Component)(this.serverLabel != null
         ? CommonComponents.joinForNarration(super.getNarrationMessage(), this.serverLabel)
         : super.getNarrationMessage());
   }

   @Override
   protected void init() {
      this.layout.addTitleHeader(TITLE, this.font);
      this.socialInteractionsPlayerList = new SocialInteractionsPlayerList(this, this.minecraft, this.width, this.listEnd() - 88, 88, 36);
      int var1 = this.socialInteractionsPlayerList.getRowWidth() / 3;
      int var2 = this.socialInteractionsPlayerList.getRowLeft();
      int var3 = this.socialInteractionsPlayerList.getRowRight();
      this.allButton = this.addRenderableWidget(
         Button.builder(TAB_ALL, var1x -> this.showPage(SocialInteractionsScreen.Page.ALL)).bounds(var2, 45, var1, 20).build()
      );
      this.hiddenButton = this.addRenderableWidget(
         Button.builder(TAB_HIDDEN, var1x -> this.showPage(SocialInteractionsScreen.Page.HIDDEN)).bounds((var2 + var3 - var1) / 2 + 1, 45, var1, 20).build()
      );
      this.blockedButton = this.addRenderableWidget(
         Button.builder(TAB_BLOCKED, var1x -> this.showPage(SocialInteractionsScreen.Page.BLOCKED)).bounds(var3 - var1 + 1, 45, var1, 20).build()
      );
      String var4 = this.searchBox != null ? this.searchBox.getValue() : "";
      this.searchBox = new EditBox(this.font, this.marginX() + 28, 74, 200, 15, SEARCH_HINT) {
         @Override
         protected MutableComponent createNarrationMessage() {
            return !SocialInteractionsScreen.this.searchBox.getValue().isEmpty() && SocialInteractionsScreen.this.socialInteractionsPlayerList.isEmpty()
               ? super.createNarrationMessage().append(", ").append(SocialInteractionsScreen.EMPTY_SEARCH)
               : super.createNarrationMessage();
         }
      };
      this.searchBox.setMaxLength(16);
      this.searchBox.setVisible(true);
      this.searchBox.setTextColor(-1);
      this.searchBox.setValue(var4);
      this.searchBox.setHint(SEARCH_HINT);
      this.searchBox.setResponder(this::checkSearchStringUpdate);
      this.addRenderableWidget(this.searchBox);
      this.addWidget(this.socialInteractionsPlayerList);
      this.blockingHintButton = this.addRenderableWidget(
         Button.builder(BLOCKING_HINT, ConfirmLinkScreen.confirmLink(this, CommonLinks.BLOCKING_HELP))
            .bounds(this.width / 2 - 100, 64 + this.windowHeight(), 200, 20)
            .build()
      );
      this.showPage(this.page);
      this.layout.addToFooter(Button.builder(CommonComponents.GUI_DONE, var1x -> this.onClose()).width(200).build());
      this.layout.visitWidgets(var1x -> {
         AbstractWidget var10000 = this.addRenderableWidget(var1x);
      });
      this.repositionElements();
   }

   @Override
   protected void repositionElements() {
      this.layout.arrangeElements();
      this.socialInteractionsPlayerList.updateSizeAndPosition(this.width, this.listEnd() - 88, 88);
      this.searchBox.setPosition(this.marginX() + 28, 74);
      int var1 = this.socialInteractionsPlayerList.getRowLeft();
      int var2 = this.socialInteractionsPlayerList.getRowRight();
      int var3 = this.socialInteractionsPlayerList.getRowWidth() / 3;
      this.allButton.setPosition(var1, 45);
      this.hiddenButton.setPosition((var1 + var2 - var3) / 2 + 1, 45);
      this.blockedButton.setPosition(var2 - var3 + 1, 45);
      this.blockingHintButton.setPosition(this.width / 2 - 100, 64 + this.windowHeight());
   }

   @Override
   protected void setInitialFocus() {
      this.setInitialFocus(this.searchBox);
   }

   @Override
   public void onClose() {
      this.minecraft.setScreen(this.lastScreen);
   }

   private void showPage(SocialInteractionsScreen.Page var1) {
      this.page = var1;
      this.allButton.setMessage(TAB_ALL);
      this.hiddenButton.setMessage(TAB_HIDDEN);
      this.blockedButton.setMessage(TAB_BLOCKED);
      boolean var2 = false;
      switch (var1) {
         case ALL:
            this.allButton.setMessage(TAB_ALL_SELECTED);
            Collection var6 = this.minecraft.player.connection.getOnlinePlayerIds();
            this.socialInteractionsPlayerList.updatePlayerList(var6, this.socialInteractionsPlayerList.getScrollAmount(), true);
            break;
         case HIDDEN:
            this.hiddenButton.setMessage(TAB_HIDDEN_SELECTED);
            Set var5 = this.minecraft.getPlayerSocialManager().getHiddenPlayers();
            var2 = var5.isEmpty();
            this.socialInteractionsPlayerList.updatePlayerList(var5, this.socialInteractionsPlayerList.getScrollAmount(), false);
            break;
         case BLOCKED:
            this.blockedButton.setMessage(TAB_BLOCKED_SELECTED);
            PlayerSocialManager var3 = this.minecraft.getPlayerSocialManager();
            Set var4 = this.minecraft.player.connection.getOnlinePlayerIds().stream().filter(var3::isBlocked).collect(Collectors.toSet());
            var2 = var4.isEmpty();
            this.socialInteractionsPlayerList.updatePlayerList(var4, this.socialInteractionsPlayerList.getScrollAmount(), false);
      }

      GameNarrator var7 = this.minecraft.getNarrator();
      if (!this.searchBox.getValue().isEmpty() && this.socialInteractionsPlayerList.isEmpty() && !this.searchBox.isFocused()) {
         var7.sayNow(EMPTY_SEARCH);
      } else if (var2) {
         if (var1 == SocialInteractionsScreen.Page.HIDDEN) {
            var7.sayNow(EMPTY_HIDDEN);
         } else if (var1 == SocialInteractionsScreen.Page.BLOCKED) {
            var7.sayNow(EMPTY_BLOCKED);
         }
      }
   }

   @Override
   public void renderBackground(GuiGraphics var1, int var2, int var3, float var4) {
      super.renderBackground(var1, var2, var3, var4);
      int var5 = this.marginX() + 3;
      var1.blitSprite(BACKGROUND_SPRITE, var5, 64, 236, this.windowHeight() + 16);
      var1.blitSprite(SEARCH_SPRITE, var5 + 10, 76, 12, 12);
   }

   @Override
   public void render(GuiGraphics var1, int var2, int var3, float var4) {
      super.render(var1, var2, var3, var4);
      this.updateServerLabel(this.minecraft);
      if (this.serverLabel != null) {
         var1.drawString(this.minecraft.font, this.serverLabel, this.marginX() + 8, 35, -1);
      }

      if (!this.socialInteractionsPlayerList.isEmpty()) {
         this.socialInteractionsPlayerList.render(var1, var2, var3, var4);
      } else if (!this.searchBox.getValue().isEmpty()) {
         var1.drawCenteredString(this.minecraft.font, EMPTY_SEARCH, this.width / 2, (72 + this.listEnd()) / 2, -1);
      } else if (this.page == SocialInteractionsScreen.Page.HIDDEN) {
         var1.drawCenteredString(this.minecraft.font, EMPTY_HIDDEN, this.width / 2, (72 + this.listEnd()) / 2, -1);
      } else if (this.page == SocialInteractionsScreen.Page.BLOCKED) {
         var1.drawCenteredString(this.minecraft.font, EMPTY_BLOCKED, this.width / 2, (72 + this.listEnd()) / 2, -1);
      }

      this.blockingHintButton.visible = this.page == SocialInteractionsScreen.Page.BLOCKED;
   }

   @Override
   public boolean keyPressed(int var1, int var2, int var3) {
      if (!this.searchBox.isFocused() && this.minecraft.options.keySocialInteractions.matches(var1, var2)) {
         this.onClose();
         return true;
      } else {
         return super.keyPressed(var1, var2, var3);
      }
   }

   @Override
   public boolean isPauseScreen() {
      return false;
   }

   private void checkSearchStringUpdate(String var1) {
      var1 = var1.toLowerCase(Locale.ROOT);
      if (!var1.equals(this.lastSearch)) {
         this.socialInteractionsPlayerList.setFilter(var1);
         this.lastSearch = var1;
         this.showPage(this.page);
      }
   }

   private void updateServerLabel(Minecraft var1) {
      int var2 = var1.getConnection().getOnlinePlayers().size();
      if (this.playerCount != var2) {
         String var3 = "";
         ServerData var4 = var1.getCurrentServer();
         if (var1.isLocalServer()) {
            var3 = var1.getSingleplayerServer().getMotd();
         } else if (var4 != null) {
            var3 = var4.name;
         }

         if (var2 > 1) {
            this.serverLabel = Component.translatable("gui.socialInteractions.server_label.multiple", var3, var2);
         } else {
            this.serverLabel = Component.translatable("gui.socialInteractions.server_label.single", var3, var2);
         }

         this.playerCount = var2;
      }
   }

   public void onAddPlayer(PlayerInfo var1) {
      this.socialInteractionsPlayerList.addPlayer(var1, this.page);
   }

   public void onRemovePlayer(UUID var1) {
      this.socialInteractionsPlayerList.removePlayer(var1);
   }

   public static enum Page {
      ALL,
      HIDDEN,
      BLOCKED;

      private Page() {
      }
   }
}
