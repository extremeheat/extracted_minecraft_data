package net.minecraft.client.gui.screens;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.gui.components.Button;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.util.HttpUtil;
import net.minecraft.world.level.GameType;

public class ShareToLanScreen extends Screen {
   private static final Component ALLOW_COMMANDS_LABEL = new TranslatableComponent("selectWorld.allowCommands");
   private static final Component GAME_MODE_LABEL = new TranslatableComponent("selectWorld.gameMode");
   private static final Component INFO_TEXT = new TranslatableComponent("lanServer.otherPlayers");
   private final Screen lastScreen;
   private Button commandsButton;
   private Button modeButton;
   private String gameModeName = "survival";
   private boolean commands;

   public ShareToLanScreen(Screen var1) {
      super(new TranslatableComponent("lanServer.title"));
      this.lastScreen = var1;
   }

   protected void init() {
      this.addButton(new Button(this.width / 2 - 155, this.height - 28, 150, 20, new TranslatableComponent("lanServer.start"), (var1) -> {
         this.minecraft.setScreen((Screen)null);
         int var2 = HttpUtil.getAvailablePort();
         TranslatableComponent var3;
         if (this.minecraft.getSingleplayerServer().publishServer(GameType.byName(this.gameModeName), this.commands, var2)) {
            var3 = new TranslatableComponent("commands.publish.started", new Object[]{var2});
         } else {
            var3 = new TranslatableComponent("commands.publish.failed");
         }

         this.minecraft.gui.getChat().addMessage(var3);
         this.minecraft.updateTitle();
      }));
      this.addButton(new Button(this.width / 2 + 5, this.height - 28, 150, 20, CommonComponents.GUI_CANCEL, (var1) -> {
         this.minecraft.setScreen(this.lastScreen);
      }));
      this.modeButton = (Button)this.addButton(new Button(this.width / 2 - 155, 100, 150, 20, TextComponent.EMPTY, (var1) -> {
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
      this.commandsButton = (Button)this.addButton(new Button(this.width / 2 + 5, 100, 150, 20, ALLOW_COMMANDS_LABEL, (var1) -> {
         this.commands = !this.commands;
         this.updateSelectionStrings();
      }));
      this.updateSelectionStrings();
   }

   private void updateSelectionStrings() {
      this.modeButton.setMessage(new TranslatableComponent("options.generic_value", new Object[]{GAME_MODE_LABEL, new TranslatableComponent("selectWorld.gameMode." + this.gameModeName)}));
      this.commandsButton.setMessage(CommonComponents.optionStatus(ALLOW_COMMANDS_LABEL, this.commands));
   }

   public void render(PoseStack var1, int var2, int var3, float var4) {
      this.renderBackground(var1);
      drawCenteredString(var1, this.font, this.title, this.width / 2, 50, 16777215);
      drawCenteredString(var1, this.font, INFO_TEXT, this.width / 2, 82, 16777215);
      super.render(var1, var2, var3, var4);
   }
}
