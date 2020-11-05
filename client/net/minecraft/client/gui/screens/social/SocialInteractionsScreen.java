package net.minecraft.client.gui.screens.social;

import com.google.common.collect.ImmutableList;
import com.mojang.blaze3d.vertex.PoseStack;
import java.util.Collection;
import java.util.Locale;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import javax.annotation.Nullable;
import net.minecraft.ChatFormatting;
import net.minecraft.Util;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.chat.NarratorChatListener;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.screens.ConfirmLinkScreen;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.multiplayer.PlayerInfo;
import net.minecraft.client.multiplayer.ServerData;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.FormattedText;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.resources.ResourceLocation;

public class SocialInteractionsScreen extends Screen {
   protected static final ResourceLocation SOCIAL_INTERACTIONS_LOCATION = new ResourceLocation("textures/gui/social_interactions.png");
   private static final Component TAB_ALL = new TranslatableComponent("gui.socialInteractions.tab_all");
   private static final Component TAB_HIDDEN = new TranslatableComponent("gui.socialInteractions.tab_hidden");
   private static final Component TAB_BLOCKED = new TranslatableComponent("gui.socialInteractions.tab_blocked");
   private static final Component TAB_ALL_SELECTED;
   private static final Component TAB_HIDDEN_SELECTED;
   private static final Component TAB_BLOCKED_SELECTED;
   private static final Component SEARCH_HINT;
   private static final Component EMPTY_SEARCH;
   private static final Component EMPTY_HIDDEN;
   private static final Component EMPTY_BLOCKED;
   private static final Component BLOCKING_HINT;
   private SocialInteractionsPlayerList socialInteractionsPlayerList;
   private EditBox searchBox;
   private String lastSearch = "";
   private SocialInteractionsScreen.Page page;
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
      super(new TranslatableComponent("gui.socialInteractions.title"));
      this.page = SocialInteractionsScreen.Page.ALL;
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

   public String getNarrationMessage() {
      return super.getNarrationMessage() + ". " + this.serverLabel.getString();
   }

   public void tick() {
      super.tick();
      this.searchBox.tick();
   }

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
      int var4 = this.font.width((FormattedText)BLOCKING_HINT) + 40;
      int var5 = 64 + 16 * this.backgroundUnits();
      int var6 = (this.width - var4) / 2;
      this.allButton = (Button)this.addButton(new Button(var2, 45, var1, 20, TAB_ALL, (var1x) -> {
         this.showPage(SocialInteractionsScreen.Page.ALL);
      }));
      this.hiddenButton = (Button)this.addButton(new Button((var2 + var3 - var1) / 2 + 1, 45, var1, 20, TAB_HIDDEN, (var1x) -> {
         this.showPage(SocialInteractionsScreen.Page.HIDDEN);
      }));
      this.blockedButton = (Button)this.addButton(new Button(var3 - var1 + 1, 45, var1, 20, TAB_BLOCKED, (var1x) -> {
         this.showPage(SocialInteractionsScreen.Page.BLOCKED);
      }));
      this.blockingHintButton = (Button)this.addButton(new Button(var6, var5, var4, 20, BLOCKING_HINT, (var1x) -> {
         this.minecraft.setScreen(new ConfirmLinkScreen((var1) -> {
            if (var1) {
               Util.getPlatform().openUri("https://aka.ms/javablocking");
            }

            this.minecraft.setScreen(this);
         }, "https://aka.ms/javablocking", true));
      }));
      String var7 = this.searchBox != null ? this.searchBox.getValue() : "";
      this.searchBox = new EditBox(this.font, this.marginX() + 28, 78, 196, 16, SEARCH_HINT) {
         protected MutableComponent createNarrationMessage() {
            return !SocialInteractionsScreen.this.searchBox.getValue().isEmpty() && SocialInteractionsScreen.this.socialInteractionsPlayerList.isEmpty() ? super.createNarrationMessage().append(", ").append(SocialInteractionsScreen.EMPTY_SEARCH) : super.createNarrationMessage();
         }
      };
      this.searchBox.setMaxLength(16);
      this.searchBox.setBordered(false);
      this.searchBox.setVisible(true);
      this.searchBox.setTextColor(16777215);
      this.searchBox.setValue(var7);
      this.searchBox.setResponder(this::checkSearchStringUpdate);
      this.children.add(this.searchBox);
      this.children.add(this.socialInteractionsPlayerList);
      this.initialized = true;
      this.showPage(this.page);
   }

   private void showPage(SocialInteractionsScreen.Page var1) {
      this.page = var1;
      this.allButton.setMessage(TAB_ALL);
      this.hiddenButton.setMessage(TAB_HIDDEN);
      this.blockedButton.setMessage(TAB_BLOCKED);
      Object var2;
      switch(var1) {
      case ALL:
         this.allButton.setMessage(TAB_ALL_SELECTED);
         var2 = this.minecraft.player.connection.getOnlinePlayerIds();
         break;
      case HIDDEN:
         this.hiddenButton.setMessage(TAB_HIDDEN_SELECTED);
         var2 = this.minecraft.getPlayerSocialManager().getHiddenPlayers();
         break;
      case BLOCKED:
         this.blockedButton.setMessage(TAB_BLOCKED_SELECTED);
         PlayerSocialManager var3 = this.minecraft.getPlayerSocialManager();
         Stream var10000 = this.minecraft.player.connection.getOnlinePlayerIds().stream();
         var3.getClass();
         var2 = (Collection)var10000.filter(var3::isBlocked).collect(Collectors.toSet());
         break;
      default:
         var2 = ImmutableList.of();
      }

      this.page = var1;
      this.socialInteractionsPlayerList.updatePlayerList((Collection)var2, this.socialInteractionsPlayerList.getScrollAmount());
      if (!this.searchBox.getValue().isEmpty() && this.socialInteractionsPlayerList.isEmpty() && !this.searchBox.isFocused()) {
         NarratorChatListener.INSTANCE.sayNow(EMPTY_SEARCH.getString());
      } else if (((Collection)var2).isEmpty()) {
         if (var1 == SocialInteractionsScreen.Page.HIDDEN) {
            NarratorChatListener.INSTANCE.sayNow(EMPTY_HIDDEN.getString());
         } else if (var1 == SocialInteractionsScreen.Page.BLOCKED) {
            NarratorChatListener.INSTANCE.sayNow(EMPTY_BLOCKED.getString());
         }
      }

   }

   public void removed() {
      this.minecraft.keyboardHandler.setSendRepeatsToGui(false);
   }

   public void renderBackground(PoseStack var1) {
      int var2 = this.marginX() + 3;
      super.renderBackground(var1);
      this.minecraft.getTextureManager().bind(SOCIAL_INTERACTIONS_LOCATION);
      this.blit(var1, var2, 64, 1, 1, 236, 8);
      int var3 = this.backgroundUnits();

      for(int var4 = 0; var4 < var3; ++var4) {
         this.blit(var1, var2, 72 + 16 * var4, 1, 10, 236, 16);
      }

      this.blit(var1, var2, 72 + 16 * var3, 1, 27, 236, 8);
      this.blit(var1, var2 + 10, 76, 243, 1, 12, 12);
   }

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
      } else {
         switch(this.page) {
         case HIDDEN:
            drawCenteredString(var1, this.minecraft.font, EMPTY_HIDDEN, this.width / 2, (78 + this.listEnd()) / 2, -1);
            break;
         case BLOCKED:
            drawCenteredString(var1, this.minecraft.font, EMPTY_BLOCKED, this.width / 2, (78 + this.listEnd()) / 2, -1);
         }
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

   public boolean mouseClicked(double var1, double var3, int var5) {
      if (this.searchBox.isFocused()) {
         this.searchBox.mouseClicked(var1, var3, var5);
      }

      return super.mouseClicked(var1, var3, var5) || this.socialInteractionsPlayerList.mouseClicked(var1, var3, var5);
   }

   public boolean keyPressed(int var1, int var2, int var3) {
      if (!this.searchBox.isFocused() && this.minecraft.options.keySocialInteractions.matches(var1, var2)) {
         this.minecraft.setScreen((Screen)null);
         return true;
      } else {
         return super.keyPressed(var1, var2, var3);
      }
   }

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
            this.serverLabel = new TranslatableComponent("gui.socialInteractions.server_label.multiple", new Object[]{var3, var2});
         } else {
            this.serverLabel = new TranslatableComponent("gui.socialInteractions.server_label.single", new Object[]{var3, var2});
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

   static {
      TAB_ALL_SELECTED = TAB_ALL.plainCopy().withStyle(ChatFormatting.UNDERLINE);
      TAB_HIDDEN_SELECTED = TAB_HIDDEN.plainCopy().withStyle(ChatFormatting.UNDERLINE);
      TAB_BLOCKED_SELECTED = TAB_BLOCKED.plainCopy().withStyle(ChatFormatting.UNDERLINE);
      SEARCH_HINT = (new TranslatableComponent("gui.socialInteractions.search_hint")).withStyle(ChatFormatting.ITALIC).withStyle(ChatFormatting.GRAY);
      EMPTY_SEARCH = (new TranslatableComponent("gui.socialInteractions.search_empty")).withStyle(ChatFormatting.GRAY);
      EMPTY_HIDDEN = (new TranslatableComponent("gui.socialInteractions.empty_hidden")).withStyle(ChatFormatting.GRAY);
      EMPTY_BLOCKED = (new TranslatableComponent("gui.socialInteractions.empty_blocked")).withStyle(ChatFormatting.GRAY);
      BLOCKING_HINT = new TranslatableComponent("gui.socialInteractions.blocking_hint");
   }

   public static enum Page {
      ALL,
      HIDDEN,
      BLOCKED;

      private Page() {
      }
   }
}
