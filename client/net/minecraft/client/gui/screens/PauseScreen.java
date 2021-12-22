package net.minecraft.client.gui.screens;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.realmsclient.RealmsMainScreen;
import net.minecraft.SharedConstants;
import net.minecraft.Util;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.achievement.StatsScreen;
import net.minecraft.client.gui.screens.advancements.AdvancementsScreen;
import net.minecraft.client.gui.screens.multiplayer.JoinMultiplayerScreen;
import net.minecraft.network.chat.TranslatableComponent;

public class PauseScreen extends Screen {
   private static final String URL_FEEDBACK_SNAPSHOT = "https://aka.ms/snapshotfeedback?ref=game";
   private static final String URL_FEEDBACK_RELEASE = "https://aka.ms/javafeedback?ref=game";
   private static final String URL_BUGS = "https://aka.ms/snapshotbugs?ref=game";
   private final boolean showPauseMenu;

   public PauseScreen(boolean var1) {
      super(var1 ? new TranslatableComponent("menu.game") : new TranslatableComponent("menu.paused"));
      this.showPauseMenu = var1;
   }

   protected void init() {
      if (this.showPauseMenu) {
         this.createPauseMenu();
      }

   }

   private void createPauseMenu() {
      boolean var1 = true;
      boolean var2 = true;
      this.addRenderableWidget(new Button(this.width / 2 - 102, this.height / 4 + 24 + -16, 204, 20, new TranslatableComponent("menu.returnToGame"), (var1x) -> {
         this.minecraft.setScreen((Screen)null);
         this.minecraft.mouseHandler.grabMouse();
      }));
      this.addRenderableWidget(new Button(this.width / 2 - 102, this.height / 4 + 48 + -16, 98, 20, new TranslatableComponent("gui.advancements"), (var1x) -> {
         this.minecraft.setScreen(new AdvancementsScreen(this.minecraft.player.connection.getAdvancements()));
      }));
      this.addRenderableWidget(new Button(this.width / 2 + 4, this.height / 4 + 48 + -16, 98, 20, new TranslatableComponent("gui.stats"), (var1x) -> {
         this.minecraft.setScreen(new StatsScreen(this, this.minecraft.player.getStats()));
      }));
      String var3 = SharedConstants.getCurrentVersion().isStable() ? "https://aka.ms/javafeedback?ref=game" : "https://aka.ms/snapshotfeedback?ref=game";
      this.addRenderableWidget(new Button(this.width / 2 - 102, this.height / 4 + 72 + -16, 98, 20, new TranslatableComponent("menu.sendFeedback"), (var2x) -> {
         this.minecraft.setScreen(new ConfirmLinkScreen((var2) -> {
            if (var2) {
               Util.getPlatform().openUri(var3);
            }

            this.minecraft.setScreen(this);
         }, var3, true));
      }));
      this.addRenderableWidget(new Button(this.width / 2 + 4, this.height / 4 + 72 + -16, 98, 20, new TranslatableComponent("menu.reportBugs"), (var1x) -> {
         this.minecraft.setScreen(new ConfirmLinkScreen((var1) -> {
            if (var1) {
               Util.getPlatform().openUri("https://aka.ms/snapshotbugs?ref=game");
            }

            this.minecraft.setScreen(this);
         }, "https://aka.ms/snapshotbugs?ref=game", true));
      }));
      this.addRenderableWidget(new Button(this.width / 2 - 102, this.height / 4 + 96 + -16, 98, 20, new TranslatableComponent("menu.options"), (var1x) -> {
         this.minecraft.setScreen(new OptionsScreen(this, this.minecraft.options));
      }));
      Button var4 = (Button)this.addRenderableWidget(new Button(this.width / 2 + 4, this.height / 4 + 96 + -16, 98, 20, new TranslatableComponent("menu.shareToLan"), (var1x) -> {
         this.minecraft.setScreen(new ShareToLanScreen(this));
      }));
      var4.active = this.minecraft.hasSingleplayerServer() && !this.minecraft.getSingleplayerServer().isPublished();
      TranslatableComponent var5 = this.minecraft.isLocalServer() ? new TranslatableComponent("menu.returnToMenu") : new TranslatableComponent("menu.disconnect");
      this.addRenderableWidget(new Button(this.width / 2 - 102, this.height / 4 + 120 + -16, 204, 20, var5, (var1x) -> {
         boolean var2 = this.minecraft.isLocalServer();
         boolean var3 = this.minecraft.isConnectedToRealms();
         var1x.active = false;
         this.minecraft.level.disconnect();
         if (var2) {
            this.minecraft.clearLevel(new GenericDirtMessageScreen(new TranslatableComponent("menu.savingLevel")));
         } else {
            this.minecraft.clearLevel();
         }

         TitleScreen var4 = new TitleScreen();
         if (var2) {
            this.minecraft.setScreen(var4);
         } else if (var3) {
            this.minecraft.setScreen(new RealmsMainScreen(var4));
         } else {
            this.minecraft.setScreen(new JoinMultiplayerScreen(var4));
         }

      }));
   }

   public void tick() {
      super.tick();
   }

   public void render(PoseStack var1, int var2, int var3, float var4) {
      if (this.showPauseMenu) {
         this.renderBackground(var1);
         drawCenteredString(var1, this.font, this.title, this.width / 2, 40, 16777215);
      } else {
         drawCenteredString(var1, this.font, this.title, this.width / 2, 10, 16777215);
      }

      super.render(var1, var2, var3, var4);
   }
}
