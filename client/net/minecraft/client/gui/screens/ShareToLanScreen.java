package net.minecraft.client.gui.screens;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.CycleButton;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.util.HttpUtil;
import net.minecraft.world.level.GameType;

public class ShareToLanScreen extends Screen {
   private static final Component ALLOW_COMMANDS_LABEL = new TranslatableComponent("selectWorld.allowCommands");
   private static final Component GAME_MODE_LABEL = new TranslatableComponent("selectWorld.gameMode");
   private static final Component INFO_TEXT = new TranslatableComponent("lanServer.otherPlayers");
   private final Screen lastScreen;
   private GameType gameMode;
   private boolean commands;

   public ShareToLanScreen(Screen var1) {
      super(new TranslatableComponent("lanServer.title"));
      this.gameMode = GameType.SURVIVAL;
      this.lastScreen = var1;
   }

   protected void init() {
      this.addRenderableWidget(CycleButton.builder(GameType::getShortDisplayName).withValues((Object[])(GameType.SURVIVAL, GameType.SPECTATOR, GameType.CREATIVE, GameType.ADVENTURE)).withInitialValue(this.gameMode).create(this.width / 2 - 155, 100, 150, 20, GAME_MODE_LABEL, (var1, var2) -> {
         this.gameMode = var2;
      }));
      this.addRenderableWidget(CycleButton.onOffBuilder(this.commands).create(this.width / 2 + 5, 100, 150, 20, ALLOW_COMMANDS_LABEL, (var1, var2) -> {
         this.commands = var2;
      }));
      this.addRenderableWidget(new Button(this.width / 2 - 155, this.height - 28, 150, 20, new TranslatableComponent("lanServer.start"), (var1) -> {
         this.minecraft.setScreen((Screen)null);
         int var2 = HttpUtil.getAvailablePort();
         TranslatableComponent var3;
         if (this.minecraft.getSingleplayerServer().publishServer(this.gameMode, this.commands, var2)) {
            var3 = new TranslatableComponent("commands.publish.started", new Object[]{var2});
         } else {
            var3 = new TranslatableComponent("commands.publish.failed");
         }

         this.minecraft.gui.getChat().addMessage(var3);
         this.minecraft.updateTitle();
      }));
      this.addRenderableWidget(new Button(this.width / 2 + 5, this.height - 28, 150, 20, CommonComponents.GUI_CANCEL, (var1) -> {
         this.minecraft.setScreen(this.lastScreen);
      }));
   }

   public void render(PoseStack var1, int var2, int var3, float var4) {
      this.renderBackground(var1);
      drawCenteredString(var1, this.font, this.title, this.width / 2, 50, 16777215);
      drawCenteredString(var1, this.font, INFO_TEXT, this.width / 2, 82, 16777215);
      super.render(var1, var2, var3, var4);
   }
}
