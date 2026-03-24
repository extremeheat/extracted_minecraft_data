package net.minecraft.client.gui.screens;

import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.CycleButton;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.client.server.IntegratedServer;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.server.commands.PublishCommand;
import net.minecraft.util.HttpUtil;
import net.minecraft.world.level.GameType;
import org.jspecify.annotations.Nullable;

public class ShareToLanScreen extends Screen {
   private static final int PORT_LOWER_BOUND = 1024;
   private static final int PORT_HIGHER_BOUND = 65535;
   private static final Component ALLOW_COMMANDS_LABEL = Component.translatable("selectWorld.allowCommands");
   private static final Component GAME_MODE_LABEL = Component.translatable("selectWorld.gameMode");
   private static final Component INFO_TEXT = Component.translatable("lanServer.otherPlayers");
   private static final Component PORT_INFO_TEXT = Component.translatable("lanServer.port");
   private static final Component PORT_UNAVAILABLE = Component.translatable("lanServer.port.unavailable", 1024, 65535);
   private static final Component INVALID_PORT = Component.translatable("lanServer.port.invalid", 1024, 65535);
   private final Screen lastScreen;
   private GameType gameMode;
   private boolean commands;
   private int port;
   private @Nullable EditBox portEdit;

   public ShareToLanScreen(final Screen lastScreen) {
      super(Component.translatable("lanServer.title"));
      this.gameMode = GameType.SURVIVAL;
      this.port = HttpUtil.getAvailablePort();
      this.lastScreen = lastScreen;
   }

   protected void init() {
      IntegratedServer singleplayerServer = this.minecraft.getSingleplayerServer();
      this.gameMode = singleplayerServer.getDefaultGameType();
      this.commands = singleplayerServer.getWorldData().isAllowCommands();
      this.addRenderableWidget(CycleButton.builder(GameType::getShortDisplayName, this.gameMode).withValues(GameType.SURVIVAL, GameType.SPECTATOR, GameType.CREATIVE, GameType.ADVENTURE).create(this.width / 2 - 155, 100, 150, 20, GAME_MODE_LABEL, (button, value) -> this.gameMode = value));
      this.addRenderableWidget(CycleButton.onOffBuilder(this.commands).create(this.width / 2 + 5, 100, 150, 20, ALLOW_COMMANDS_LABEL, (button, value) -> this.commands = value));
      Button startButton = Button.builder(Component.translatable("lanServer.start"), (button) -> {
         this.minecraft.setScreen((Screen)null);
         Component message;
         if (singleplayerServer.publishServer(this.gameMode, this.commands, this.port)) {
            message = PublishCommand.getSuccessMessage(this.port);
         } else {
            message = Component.translatable("commands.publish.failed");
         }

         this.minecraft.gui.getChat().addClientSystemMessage(message);
         this.minecraft.getNarrator().saySystemQueued(message);
         this.minecraft.updateTitle();
      }).bounds(this.width / 2 - 155, this.height - 28, 150, 20).build();
      this.portEdit = new EditBox(this.font, this.width / 2 - 75, 160, 150, 20, Component.translatable("lanServer.port"));
      this.portEdit.setResponder((value) -> {
         Component errorMessage = this.tryParsePort(value);
         this.portEdit.setHint(Component.literal("" + this.port));
         if (errorMessage == null) {
            this.portEdit.setTextColor(-2039584);
            this.portEdit.setTooltip((Tooltip)null);
            startButton.active = true;
         } else {
            this.portEdit.setTextColor(-2142128);
            this.portEdit.setTooltip(Tooltip.create(errorMessage));
            startButton.active = false;
         }

      });
      this.portEdit.setHint(Component.literal("" + this.port));
      this.addRenderableWidget(this.portEdit);
      this.addRenderableWidget(startButton);
      this.addRenderableWidget(Button.builder(CommonComponents.GUI_CANCEL, (button) -> this.onClose()).bounds(this.width / 2 + 5, this.height - 28, 150, 20).build());
   }

   public void onClose() {
      this.minecraft.setScreen(this.lastScreen);
   }

   private @Nullable Component tryParsePort(final String value) {
      if (value.isBlank()) {
         this.port = HttpUtil.getAvailablePort();
         return null;
      } else {
         try {
            this.port = Integer.parseInt(value);
            if (this.port >= 1024 && this.port <= 65535) {
               return !HttpUtil.isPortAvailable(this.port) ? PORT_UNAVAILABLE : null;
            } else {
               return INVALID_PORT;
            }
         } catch (NumberFormatException var3) {
            this.port = HttpUtil.getAvailablePort();
            return INVALID_PORT;
         }
      }
   }

   public void extractRenderState(final GuiGraphicsExtractor graphics, final int mouseX, final int mouseY, final float a) {
      super.extractRenderState(graphics, mouseX, mouseY, a);
      graphics.centeredText(this.font, (Component)this.title, this.width / 2, 50, -1);
      graphics.centeredText(this.font, (Component)INFO_TEXT, this.width / 2, 82, -1);
      graphics.centeredText(this.font, (Component)PORT_INFO_TEXT, this.width / 2, 142, -1);
   }
}
