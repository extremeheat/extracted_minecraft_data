package net.minecraft.client.gui.screens.social;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import java.util.Collection;
import java.util.Locale;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;
import javax.annotation.Nullable;
import net.minecraft.ChatFormatting;
import net.minecraft.Util;
import net.minecraft.client.GameNarrator;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.screens.ConfirmLinkScreen;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.multiplayer.PlayerInfo;
import net.minecraft.client.multiplayer.ServerData;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceLocation;

public class SocialInteractionsScreen extends Screen {
   protected static final ResourceLocation SOCIAL_INTERACTIONS_LOCATION = new ResourceLocation("textures/gui/social_interactions.png");
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
   private static final String BLOCK_LINK = "https://aka.ms/javablocking";
   private static final int BG_BORDER_SIZE = 8;
   private static final int BG_UNITS = 16;
   private static final int BG_WIDTH = 236;
   private static final int SEARCH_HEIGHT = 16;
   private static final int MARGIN_Y = 64;
   public static final int LIST_START = 88;
   public static final int SEARCH_START = 78;
   private static final int IMAGE_WIDTH = 238;
   private static final int BUTTON_HEIGHT = 20;
   private static final int ITEM_HEIGHT = 36;
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
   private boolean initialized;
   @Nullable
   private Runnable postRenderRunnable;

   public SocialInteractionsScreen() {
      super(Component.translatable("gui.socialInteractions.title"));
      this.updateServerLabel(Minecraft.getInstance());
   }

   private int windowHeight() {
      return Math.max(52, this.height - 128 - 16);
   }

   private int backgroundUnits() {
      return this.windowHeight() / 16;
   }

   private int listEnd() {
      return 80 + this.backgroundUnits() * 16 - 8;
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
   public void tick() {
      super.tick();
      this.searchBox.tick();
   }

   @Override
   protected void init() {
      this.minecraft.keyboardHandler.setSendRepeatsToGui(true);
      if (this.initialized) {
         this.socialInteractionsPlayerList.updateSize(this.width, this.height, 88, this.listEnd());
      } else {
         this.socialInteractionsPlayerList = new SocialInteractionsPlayerList(this, this.minecraft, this.width, this.height, 88, this.listEnd(), 36);
      }

      int var1 = this.socialInteractionsPlayerList.getRowWidth() / 3;
      int var2 = this.socialInteractionsPlayerList.getRowLeft();
      int var3 = this.socialInteractionsPlayerList.getRowRight();
      int var4 = this.font.width(BLOCKING_HINT) + 40;
      int var5 = 64 + 16 * this.backgroundUnits();
      int var6 = (this.width - var4) / 2 + 3;
      this.allButton = this.addRenderableWidget(new Button(var2, 45, var1, 20, TAB_ALL, var1x -> this.showPage(SocialInteractionsScreen.Page.ALL)));
      this.hiddenButton = this.addRenderableWidget(
         new Button((var2 + var3 - var1) / 2 + 1, 45, var1, 20, TAB_HIDDEN, var1x -> this.showPage(SocialInteractionsScreen.Page.HIDDEN))
      );
      this.blockedButton = this.addRenderableWidget(
         new Button(var3 - var1 + 1, 45, var1, 20, TAB_BLOCKED, var1x -> this.showPage(SocialInteractionsScreen.Page.BLOCKED))
      );
      String var7 = this.searchBox != null ? this.searchBox.getValue() : "";
      this.searchBox = new EditBox(this.font, this.marginX() + 28, 78, 196, 16, SEARCH_HINT) {
         @Override
         protected MutableComponent createNarrationMessage() {
            return !SocialInteractionsScreen.this.searchBox.getValue().isEmpty() && SocialInteractionsScreen.this.socialInteractionsPlayerList.isEmpty()
               ? super.createNarrationMessage().append(", ").append(SocialInteractionsScreen.EMPTY_SEARCH)
               : super.createNarrationMessage();
         }
      };
      this.searchBox.setMaxLength(16);
      this.searchBox.setBordered(false);
      this.searchBox.setVisible(true);
      this.searchBox.setTextColor(16777215);
      this.searchBox.setValue(var7);
      this.searchBox.setResponder(this::checkSearchStringUpdate);
      this.addWidget(this.searchBox);
      this.addWidget(this.socialInteractionsPlayerList);
      this.blockingHintButton = this.addRenderableWidget(
         new Button(var6, var5, var4, 20, BLOCKING_HINT, var1x -> this.minecraft.setScreen(new ConfirmLinkScreen(var1xx -> {
               if (var1xx) {
                  Util.getPlatform().openUri("https://aka.ms/javablocking");
               }
   
               this.minecraft.setScreen(this);
            }, "https://aka.ms/javablocking", true)))
      );
      this.initialized = true;
      this.showPage(this.page);
   }

   private void showPage(SocialInteractionsScreen.Page var1) {
      this.page = var1;
      this.allButton.setMessage(TAB_ALL);
      this.hiddenButton.setMessage(TAB_HIDDEN);
      this.blockedButton.setMessage(TAB_BLOCKED);
      boolean var2 = false;
      switch(var1) {
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
   public void removed() {
      this.minecraft.keyboardHandler.setSendRepeatsToGui(false);
   }

   @Override
   public void renderBackground(PoseStack var1) {
      int var2 = this.marginX() + 3;
      super.renderBackground(var1);
      RenderSystem.setShaderTexture(0, SOCIAL_INTERACTIONS_LOCATION);
      this.blit(var1, var2, 64, 1, 1, 236, 8);
      int var3 = this.backgroundUnits();

      for(int var4 = 0; var4 < var3; ++var4) {
         this.blit(var1, var2, 72 + 16 * var4, 1, 10, 236, 16);
      }

      this.blit(var1, var2, 72 + 16 * var3, 1, 27, 236, 8);
      this.blit(var1, var2 + 10, 76, 243, 1, 12, 12);
   }

   @Override
   public void render(PoseStack var1, int var2, int var3, float var4) {
      this.updateServerLabel(this.minecraft);
      this.renderBackground(var1);
      if (this.serverLabel != null) {
         drawString(var1, this.minecraft.font, this.serverLabel, this.marginX() + 8, 35, -1);
      }

      if (!this.socialInteractionsPlayerList.isEmpty()) {
         this.socialInteractionsPlayerList.render(var1, var2, var3, var4);
      } else if (!this.searchBox.getValue().isEmpty()) {
         drawCenteredString(var1, this.minecraft.font, EMPTY_SEARCH, this.width / 2, (78 + this.listEnd()) / 2, -1);
      } else if (this.page == SocialInteractionsScreen.Page.HIDDEN) {
         drawCenteredString(var1, this.minecraft.font, EMPTY_HIDDEN, this.width / 2, (78 + this.listEnd()) / 2, -1);
      } else if (this.page == SocialInteractionsScreen.Page.BLOCKED) {
         drawCenteredString(var1, this.minecraft.font, EMPTY_BLOCKED, this.width / 2, (78 + this.listEnd()) / 2, -1);
      }

      if (!this.searchBox.isFocused() && this.searchBox.getValue().isEmpty()) {
         drawString(var1, this.minecraft.font, SEARCH_HINT, this.searchBox.x, this.searchBox.y, -1);
      } else {
         this.searchBox.render(var1, var2, var3, var4);
      }

      this.blockingHintButton.visible = this.page == SocialInteractionsScreen.Page.BLOCKED;
      super.render(var1, var2, var3, var4);
      if (this.postRenderRunnable != null) {
         this.postRenderRunnable.run();
      }
   }

   @Override
   public boolean mouseClicked(double var1, double var3, int var5) {
      if (this.searchBox.isFocused()) {
         this.searchBox.mouseClicked(var1, var3, var5);
      }

      return super.mouseClicked(var1, var3, var5) || this.socialInteractionsPlayerList.mouseClicked(var1, var3, var5);
   }

   @Override
   public boolean keyPressed(int var1, int var2, int var3) {
      if (!this.searchBox.isFocused() && this.minecraft.options.keySocialInteractions.matches(var1, var2)) {
         this.minecraft.setScreen(null);
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

   public void setPostRenderRunnable(@Nullable Runnable var1) {
      this.postRenderRunnable = var1;
   }

   public static enum Page {
      ALL,
      HIDDEN,
      BLOCKED;

      private Page() {
      }
   }
}
