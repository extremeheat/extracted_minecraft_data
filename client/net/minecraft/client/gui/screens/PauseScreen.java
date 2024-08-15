package net.minecraft.client.gui.screens;

import com.mojang.realmsclient.RealmsMainScreen;
import java.net.URI;
import java.util.function.Supplier;
import javax.annotation.Nullable;
import net.minecraft.SharedConstants;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.StringWidget;
import net.minecraft.client.gui.layouts.FrameLayout;
import net.minecraft.client.gui.layouts.GridLayout;
import net.minecraft.client.gui.layouts.HeaderAndFooterLayout;
import net.minecraft.client.gui.screens.achievement.StatsScreen;
import net.minecraft.client.gui.screens.advancements.AdvancementsScreen;
import net.minecraft.client.gui.screens.multiplayer.JoinMultiplayerScreen;
import net.minecraft.client.gui.screens.multiplayer.ServerLinksScreen;
import net.minecraft.client.gui.screens.options.OptionsScreen;
import net.minecraft.client.gui.screens.social.SocialInteractionsScreen;
import net.minecraft.client.multiplayer.ServerData;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.ServerLinks;
import net.minecraft.util.CommonLinks;

public class PauseScreen extends Screen {
   private static final ResourceLocation DRAFT_REPORT_SPRITE = ResourceLocation.withDefaultNamespace("icon/draft_report");
   private static final int COLUMNS = 2;
   private static final int MENU_PADDING_TOP = 50;
   private static final int BUTTON_PADDING = 4;
   private static final int BUTTON_WIDTH_FULL = 204;
   private static final int BUTTON_WIDTH_HALF = 98;
   private static final Component RETURN_TO_GAME = Component.translatable("menu.returnToGame");
   private static final Component ADVANCEMENTS = Component.translatable("gui.advancements");
   private static final Component STATS = Component.translatable("gui.stats");
   private static final Component SEND_FEEDBACK = Component.translatable("menu.sendFeedback");
   private static final Component REPORT_BUGS = Component.translatable("menu.reportBugs");
   private static final Component FEEDBACK_SUBSCREEN = Component.translatable("menu.feedback");
   private static final Component SERVER_LINKS = Component.translatable("menu.server_links");
   private static final Component OPTIONS = Component.translatable("menu.options");
   private static final Component SHARE_TO_LAN = Component.translatable("menu.shareToLan");
   private static final Component PLAYER_REPORTING = Component.translatable("menu.playerReporting");
   private static final Component RETURN_TO_MENU = Component.translatable("menu.returnToMenu");
   private static final Component SAVING_LEVEL = Component.translatable("menu.savingLevel");
   private static final Component GAME = Component.translatable("menu.game");
   private static final Component PAUSED = Component.translatable("menu.paused");
   private final boolean showPauseMenu;
   @Nullable
   private Button disconnectButton;

   public PauseScreen(boolean var1) {
      super(var1 ? GAME : PAUSED);
      this.showPauseMenu = var1;
   }

   public boolean showsPauseMenu() {
      return this.showPauseMenu;
   }

   @Override
   protected void init() {
      if (this.showPauseMenu) {
         this.createPauseMenu();
      }

      this.addRenderableWidget(new StringWidget(0, this.showPauseMenu ? 40 : 10, this.width, 9, this.title, this.font));
   }

   private void createPauseMenu() {
      GridLayout var1 = new GridLayout();
      var1.defaultCellSetting().padding(4, 4, 4, 0);
      GridLayout.RowHelper var2 = var1.createRowHelper(2);
      var2.addChild(Button.builder(RETURN_TO_GAME, var1x -> {
         this.minecraft.setScreen(null);
         this.minecraft.mouseHandler.grabMouse();
      }).width(204).build(), 2, var1.newCellSettings().paddingTop(50));
      var2.addChild(this.openScreenButton(ADVANCEMENTS, () -> new AdvancementsScreen(this.minecraft.player.connection.getAdvancements(), this)));
      var2.addChild(this.openScreenButton(STATS, () -> new StatsScreen(this, this.minecraft.player.getStats())));
      ServerLinks var3 = this.minecraft.player.connection.serverLinks();
      if (var3.isEmpty()) {
         addFeedbackButtons(this, var2);
      } else {
         var2.addChild(this.openScreenButton(FEEDBACK_SUBSCREEN, () -> new PauseScreen.FeedbackSubScreen(this)));
         var2.addChild(this.openScreenButton(SERVER_LINKS, () -> new ServerLinksScreen(this, var3)));
      }

      var2.addChild(this.openScreenButton(OPTIONS, () -> new OptionsScreen(this, this.minecraft.options)));
      if (this.minecraft.hasSingleplayerServer() && !this.minecraft.getSingleplayerServer().isPublished()) {
         var2.addChild(this.openScreenButton(SHARE_TO_LAN, () -> new ShareToLanScreen(this)));
      } else {
         var2.addChild(this.openScreenButton(PLAYER_REPORTING, () -> new SocialInteractionsScreen(this)));
      }

      Component var4 = this.minecraft.isLocalServer() ? RETURN_TO_MENU : CommonComponents.GUI_DISCONNECT;
      this.disconnectButton = var2.addChild(Button.builder(var4, var1x -> {
         var1x.active = false;
         this.minecraft.getReportingContext().draftReportHandled(this.minecraft, this, this::onDisconnect, true);
      }).width(204).build(), 2);
      var1.arrangeElements();
      FrameLayout.alignInRectangle(var1, 0, 0, this.width, this.height, 0.5F, 0.25F);
      var1.visitWidgets(this::addRenderableWidget);
   }

   static void addFeedbackButtons(Screen var0, GridLayout.RowHelper var1) {
      var1.addChild(
         openLinkButton(var0, SEND_FEEDBACK, SharedConstants.getCurrentVersion().isStable() ? CommonLinks.RELEASE_FEEDBACK : CommonLinks.SNAPSHOT_FEEDBACK)
      );
      var1.addChild(openLinkButton(var0, REPORT_BUGS, CommonLinks.SNAPSHOT_BUGS_FEEDBACK)).active = !SharedConstants.getCurrentVersion()
         .getDataVersion()
         .isSideSeries();
   }

   private void onDisconnect() {
      boolean var1 = this.minecraft.isLocalServer();
      ServerData var2 = this.minecraft.getCurrentServer();
      this.minecraft.level.disconnect();
      if (var1) {
         this.minecraft.disconnect(new GenericMessageScreen(SAVING_LEVEL));
      } else {
         this.minecraft.disconnect();
      }

      TitleScreen var3 = new TitleScreen();
      if (var1) {
         this.minecraft.setScreen(var3);
      } else if (var2 != null && var2.isRealm()) {
         this.minecraft.setScreen(new RealmsMainScreen(var3));
      } else {
         this.minecraft.setScreen(new JoinMultiplayerScreen(var3));
      }
   }

   @Override
   public void tick() {
      super.tick();
   }

   @Override
   public void render(GuiGraphics var1, int var2, int var3, float var4) {
      super.render(var1, var2, var3, var4);
      if (this.showPauseMenu && this.minecraft != null && this.minecraft.getReportingContext().hasDraftReport() && this.disconnectButton != null) {
         var1.blitSprite(
            RenderType::guiTextured,
            DRAFT_REPORT_SPRITE,
            this.disconnectButton.getX() + this.disconnectButton.getWidth() - 17,
            this.disconnectButton.getY() + 3,
            15,
            15
         );
      }
   }

   @Override
   public void renderBackground(GuiGraphics var1, int var2, int var3, float var4) {
      if (this.showPauseMenu) {
         super.renderBackground(var1, var2, var3, var4);
      }
   }

   private Button openScreenButton(Component var1, Supplier<Screen> var2) {
      return Button.builder(var1, var2x -> this.minecraft.setScreen((Screen)var2.get())).width(98).build();
   }

   private static Button openLinkButton(Screen var0, Component var1, URI var2) {
      return Button.builder(var1, ConfirmLinkScreen.confirmLink(var0, var2)).width(98).build();
   }

   static class FeedbackSubScreen extends Screen {
      private static final Component TITLE = Component.translatable("menu.feedback.title");
      public final Screen parent;
      private final HeaderAndFooterLayout layout = new HeaderAndFooterLayout(this);

      protected FeedbackSubScreen(Screen var1) {
         super(TITLE);
         this.parent = var1;
      }

      @Override
      protected void init() {
         this.layout.addTitleHeader(TITLE, this.font);
         GridLayout var1 = this.layout.addToContents(new GridLayout());
         var1.defaultCellSetting().padding(4, 4, 4, 0);
         GridLayout.RowHelper var2 = var1.createRowHelper(2);
         PauseScreen.addFeedbackButtons(this, var2);
         this.layout.addToFooter(Button.builder(CommonComponents.GUI_BACK, var1x -> this.onClose()).width(200).build());
         this.layout.visitWidgets(this::addRenderableWidget);
         this.repositionElements();
      }

      @Override
      protected void repositionElements() {
         this.layout.arrangeElements();
      }

      @Override
      public void onClose() {
         this.minecraft.setScreen(this.parent);
      }
   }
}
