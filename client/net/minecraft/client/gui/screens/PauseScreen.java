package net.minecraft.client.gui.screens;

import net.minecraft.SharedConstants;
import net.minecraft.Util;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.achievement.StatsScreen;
import net.minecraft.client.gui.screens.advancements.AdvancementsScreen;
import net.minecraft.client.gui.screens.multiplayer.JoinMultiplayerScreen;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.realms.RealmsBridge;

public class PauseScreen extends Screen {
   private final boolean showPauseMenu;

   public PauseScreen(boolean var1) {
      super(var1 ? new TranslatableComponent("menu.game", new Object[0]) : new TranslatableComponent("menu.paused", new Object[0]));
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
      this.addButton(new Button(this.width / 2 - 102, this.height / 4 + 24 + -16, 204, 20, I18n.get("menu.returnToGame"), (var1x) -> {
         this.minecraft.setScreen((Screen)null);
         this.minecraft.mouseHandler.grabMouse();
      }));
      this.addButton(new Button(this.width / 2 - 102, this.height / 4 + 48 + -16, 98, 20, I18n.get("gui.advancements"), (var1x) -> {
         this.minecraft.setScreen(new AdvancementsScreen(this.minecraft.player.connection.getAdvancements()));
      }));
      this.addButton(new Button(this.width / 2 + 4, this.height / 4 + 48 + -16, 98, 20, I18n.get("gui.stats"), (var1x) -> {
         this.minecraft.setScreen(new StatsScreen(this, this.minecraft.player.getStats()));
      }));
      String var3 = SharedConstants.getCurrentVersion().isStable() ? "https://aka.ms/javafeedback?ref=game" : "https://aka.ms/snapshotfeedback?ref=game";
      this.addButton(new Button(this.width / 2 - 102, this.height / 4 + 72 + -16, 98, 20, I18n.get("menu.sendFeedback"), (var2x) -> {
         this.minecraft.setScreen(new ConfirmLinkScreen((var2) -> {
            if (var2) {
               Util.getPlatform().openUri(var3);
            }

            this.minecraft.setScreen(this);
         }, var3, true));
      }));
      this.addButton(new Button(this.width / 2 + 4, this.height / 4 + 72 + -16, 98, 20, I18n.get("menu.reportBugs"), (var1x) -> {
         this.minecraft.setScreen(new ConfirmLinkScreen((var1) -> {
            if (var1) {
               Util.getPlatform().openUri("https://aka.ms/snapshotbugs?ref=game");
            }

            this.minecraft.setScreen(this);
         }, "https://aka.ms/snapshotbugs?ref=game", true));
      }));
      this.addButton(new Button(this.width / 2 - 102, this.height / 4 + 96 + -16, 98, 20, I18n.get("menu.options"), (var1x) -> {
         this.minecraft.setScreen(new OptionsScreen(this, this.minecraft.options));
      }));
      Button var4 = (Button)this.addButton(new Button(this.width / 2 + 4, this.height / 4 + 96 + -16, 98, 20, I18n.get("menu.shareToLan"), (var1x) -> {
         this.minecraft.setScreen(new ShareToLanScreen(this));
      }));
      var4.active = this.minecraft.hasSingleplayerServer() && !this.minecraft.getSingleplayerServer().isPublished();
      Button var5 = (Button)this.addButton(new Button(this.width / 2 - 102, this.height / 4 + 120 + -16, 204, 20, I18n.get("menu.returnToMenu"), (var1x) -> {
         boolean var2 = this.minecraft.isLocalServer();
         boolean var3 = this.minecraft.isConnectedToRealms();
         var1x.active = false;
         this.minecraft.level.disconnect();
         if (var2) {
            this.minecraft.clearLevel(new GenericDirtMessageScreen(new TranslatableComponent("menu.savingLevel", new Object[0])));
         } else {
            this.minecraft.clearLevel();
         }

         if (var2) {
            this.minecraft.setScreen(new TitleScreen());
         } else if (var3) {
            RealmsBridge var4 = new RealmsBridge();
            var4.switchToRealms(new TitleScreen());
         } else {
            this.minecraft.setScreen(new JoinMultiplayerScreen(new TitleScreen()));
         }

      }));
      if (!this.minecraft.isLocalServer()) {
         var5.setMessage(I18n.get("menu.disconnect"));
      }

   }

   public void tick() {
      super.tick();
   }

   public void render(int var1, int var2, float var3) {
      if (this.showPauseMenu) {
         this.renderBackground();
         this.drawCenteredString(this.font, this.title.getColoredString(), this.width / 2, 40, 16777215);
      } else {
         this.drawCenteredString(this.font, this.title.getColoredString(), this.width / 2, 10, 16777215);
      }

      super.render(var1, var2, var3);
   }
}
