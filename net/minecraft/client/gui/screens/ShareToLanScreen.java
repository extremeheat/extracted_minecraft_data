package net.minecraft.client.gui.screens;

import net.minecraft.client.gui.components.Button;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.util.HttpUtil;
import net.minecraft.world.level.GameType;

public class ShareToLanScreen extends Screen {
   private final Screen lastScreen;
   private Button commandsButton;
   private Button modeButton;
   private String gameModeName = "survival";
   private boolean commands;

   public ShareToLanScreen(Screen var1) {
      super(new TranslatableComponent("lanServer.title", new Object[0]));
      this.lastScreen = var1;
   }

   protected void init() {
      this.addButton(new Button(this.width / 2 - 155, this.height - 28, 150, 20, I18n.get("lanServer.start"), (var1) -> {
         this.minecraft.setScreen((Screen)null);
         int var2 = HttpUtil.getAvailablePort();
         TranslatableComponent var3;
         if (this.minecraft.getSingleplayerServer().publishServer(GameType.byName(this.gameModeName), this.commands, var2)) {
            var3 = new TranslatableComponent("commands.publish.started", new Object[]{var2});
         } else {
            var3 = new TranslatableComponent("commands.publish.failed", new Object[0]);
         }

         this.minecraft.gui.getChat().addMessage(var3);
      }));
      this.addButton(new Button(this.width / 2 + 5, this.height - 28, 150, 20, I18n.get("gui.cancel"), (var1) -> {
         this.minecraft.setScreen(this.lastScreen);
      }));
      this.modeButton = (Button)this.addButton(new Button(this.width / 2 - 155, 100, 150, 20, I18n.get("selectWorld.gameMode"), (var1) -> {
         if ("spectator".equals(this.gameModeName)) {
            this.gameModeName = "creative";
         } else if ("creative".equals(this.gameModeName)) {
            this.gameModeName = "adventure";
         } else if ("adventure".equals(this.gameModeName)) {
            this.gameModeName = "survival";
         } else {
            this.gameModeName = "spectator";
         }

         this.updateSelectionStrings();
      }));
      this.commandsButton = (Button)this.addButton(new Button(this.width / 2 + 5, 100, 150, 20, I18n.get("selectWorld.allowCommands"), (var1) -> {
         this.commands = !this.commands;
         this.updateSelectionStrings();
      }));
      this.updateSelectionStrings();
   }

   private void updateSelectionStrings() {
      this.modeButton.setMessage(I18n.get("selectWorld.gameMode") + ": " + I18n.get("selectWorld.gameMode." + this.gameModeName));
      this.commandsButton.setMessage(I18n.get("selectWorld.allowCommands") + ' ' + I18n.get(this.commands ? "options.on" : "options.off"));
   }

   public void render(int var1, int var2, float var3) {
      this.renderBackground();
      this.drawCenteredString(this.font, this.title.getColoredString(), this.width / 2, 50, 16777215);
      this.drawCenteredString(this.font, I18n.get("lanServer.otherPlayers"), this.width / 2, 82, 16777215);
      super.render(var1, var2, var3);
   }
}
