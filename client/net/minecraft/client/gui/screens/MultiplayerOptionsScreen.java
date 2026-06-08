package net.minecraft.client.gui.screens;

import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.CycleButton;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.components.StringWidget;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.client.gui.layouts.HeaderAndFooterLayout;
import net.minecraft.client.gui.layouts.LinearLayout;
import net.minecraft.client.gui.screens.options.WorldOptionsScreen;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.client.server.IntegratedServer;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.ComponentUtils;
import net.minecraft.resources.Identifier;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.HttpUtil;
import net.minecraft.world.level.GameType;
import org.jspecify.annotations.Nullable;

public class MultiplayerOptionsScreen extends Screen {
   private static final int PORT_LOWER_BOUND = 1024;
   private static final int PORT_HIGHER_BOUND = 65535;
   private static final Component TITLE = Component.translatable("options.multiplayer.title");
   private static final Component ALLOW_COMMANDS_LABEL = Component.translatable("selectWorld.allowCommands");
   private static final Component GAME_MODE_LABEL = Component.translatable("selectWorld.gameMode");
   private static final Component PORT_INFO_TEXT = Component.translatable("lanServer.port");
   private static final Component PORT_UNAVAILABLE = Component.translatable("lanServer.port.unavailable", 1024, 65535);
   private static final Component INVALID_PORT = Component.translatable("lanServer.port.invalid", 1024, 65535);
   private static final Component OTHER_PLAYERS_HEADER;
   private static final Component APPLY_CHANGES;
   private static final Identifier INWORLD_MENU_LIST_BACKGROUND;
   private final HeaderAndFooterLayout layout = new HeaderAndFooterLayout(this);
   private final Screen lastScreen;
   private MinecraftServer.MultiplayerScope wantedMultiplayerScope;
   private GameType gameMode;
   private boolean commands;
   private int port;
   private boolean portValid;
   private @Nullable Button applyChanges;
   private @Nullable EditBox portEdit;
   private @Nullable StringWidget portLabel;
   private MinecraftServer.MultiplayerScope initialMultiplayerScope;
   private GameType initialGameMode;
   private boolean initialCommands;
   private int initialPort;

   public MultiplayerOptionsScreen(final Screen lastScreen) {
      super(TITLE);
      this.wantedMultiplayerScope = MinecraftServer.MultiplayerScope.OFF;
      this.gameMode = GameType.SURVIVAL;
      this.port = HttpUtil.getAvailablePort();
      this.portValid = true;
      this.initialMultiplayerScope = MinecraftServer.MultiplayerScope.OFF;
      this.initialGameMode = GameType.SURVIVAL;
      this.lastScreen = lastScreen;
   }

   protected void init() {
      IntegratedServer singleplayerServer = this.minecraft.getSingleplayerServer();
      if (singleplayerServer == null) {
         this.onClose();
      } else {
         this.layout.addTitleHeader(this.title, this.font);
         LinearLayout content = (LinearLayout)this.layout.addToContents(LinearLayout.vertical().spacing(8));
         content.defaultCellSetting().alignHorizontallyCenter();
         this.initialMultiplayerScope = singleplayerServer.getMultiplayerScope();
         content.addChild(CycleButton.onOffBuilder(this.initialMultiplayerScope == MinecraftServer.MultiplayerScope.LAN).withTooltip((lan) -> Tooltip.create(lan ? MinecraftServer.MultiplayerScope.LAN.getTooltip() : MinecraftServer.MultiplayerScope.OFF.getTooltip())).create(Component.translatable("menu.multiplayerOptions.lan"), (var1, value) -> {
            this.wantedMultiplayerScope = value ? MinecraftServer.MultiplayerScope.LAN : MinecraftServer.MultiplayerScope.OFF;
            this.updatePortControlsState();
            this.updateApplyChangesActiveState();
         }));
         this.initialMultiplayerScope = singleplayerServer.getMultiplayerScope();
         this.wantedMultiplayerScope = this.initialMultiplayerScope;
         if (this.initialMultiplayerScope == MinecraftServer.MultiplayerScope.LAN) {
            this.port = singleplayerServer.getPort();
            this.initialPort = this.port;
         }

         this.applyChanges = Button.builder(APPLY_CHANGES, (var2) -> {
            this.minecraft.gui.setScreen((Screen)null);
            if (this.gameMode != this.initialGameMode) {
               singleplayerServer.setGameTypeForOtherPlayers(this.gameMode);
            }

            if (this.commands != this.initialCommands) {
               singleplayerServer.setCommandsAllowedForOtherPlayers(this.commands);
            }

            if (this.wantedMultiplayerScope != this.initialMultiplayerScope || this.lanPortChanged()) {
               this.changeMultiplayerScope(singleplayerServer);
            }

         }).build();
         this.applyChanges.active = false;
         this.portEdit = new EditBox(this.font, PORT_INFO_TEXT);
         this.portEdit.setResponder((value) -> {
            this.setPortError(this.tryParsePort(value));
            this.portEdit.setHint(Component.literal(String.valueOf(this.port)));
            this.updateApplyChangesActiveState();
         });
         if (this.initialMultiplayerScope == MinecraftServer.MultiplayerScope.LAN) {
            this.portEdit.setValue(String.valueOf(this.port));
         }

         LinearLayout portRow = LinearLayout.vertical().spacing(4);
         this.portLabel = (StringWidget)portRow.addChild(new StringWidget(PORT_INFO_TEXT, this.font));
         portRow.addChild(this.portEdit);
         content.addChild(portRow);
         content.addChild(new StringWidget(OTHER_PLAYERS_HEADER, this.font));
         LinearLayout otherPlayerSettings = (LinearLayout)content.addChild(LinearLayout.horizontal().spacing(8));
         otherPlayerSettings.defaultCellSetting().alignHorizontallyCenter();
         this.gameMode = singleplayerServer.getGameTypeForOtherPlayers();
         this.initialGameMode = this.gameMode;
         CycleButton<GameType> gameModeButton = (CycleButton)otherPlayerSettings.addChild(CycleButton.builder(GameType::getShortDisplayName, this.gameMode).withValues(GameType.SURVIVAL, GameType.SPECTATOR, GameType.CREATIVE, GameType.ADVENTURE).create(GAME_MODE_LABEL, (var1, value) -> {
            this.gameMode = value;
            this.updateApplyChangesActiveState();
         }));
         this.commands = singleplayerServer.commandsAllowedForOtherPlayers();
         this.initialCommands = this.commands;
         CycleButton<Boolean> allowCommandsButton = (CycleButton)otherPlayerSettings.addChild(CycleButton.onOffBuilder(this.commands).create(ALLOW_COMMANDS_LABEL, (var1, value) -> {
            this.commands = value;
            this.updateApplyChangesActiveState();
         }));
         if (singleplayerServer.isHardcore()) {
            gameModeButton.active = false;
            gameModeButton.setTooltip(WorldOptionsScreen.GAME_MODE_DISABLED_HARDCORE_TOOLTIP);
            allowCommandsButton.active = false;
            allowCommandsButton.setTooltip(WorldOptionsScreen.ALLOW_COMMANDS_DISABLED_TOOLTIP);
         }

         LinearLayout footer = (LinearLayout)this.layout.addToFooter(LinearLayout.horizontal().spacing(8));
         footer.addChild(this.applyChanges);
         footer.addChild(Button.builder(CommonComponents.GUI_CANCEL, (var1) -> this.onClose()).build());
         this.layout.visitWidgets(this::addRenderableWidget);
         this.updatePortControlsState();
         this.repositionElements();
      }
   }

   private void updatePortControlsState() {
      boolean lanWanted = this.wantedMultiplayerScope == MinecraftServer.MultiplayerScope.LAN;
      if (this.portEdit != null) {
         String desired = lanWanted ? (this.initialMultiplayerScope == MinecraftServer.MultiplayerScope.LAN ? String.valueOf(this.initialPort) : "") : "";
         if (!this.portEdit.getValue().equals(desired)) {
            this.portEdit.setValue(desired);
         }

         this.portEdit.setEditable(lanWanted);
         this.portEdit.active = lanWanted;
         this.portEdit.setHint(lanWanted ? Component.literal(String.valueOf(this.port)) : Component.empty());
         if (!lanWanted) {
            this.portEdit.setFocused(false);
            this.setPortError((Component)null);
         }
      }

      if (this.portLabel != null) {
         this.portLabel.setMessage((Component)(lanWanted ? PORT_INFO_TEXT : PORT_INFO_TEXT.copy().withStyle(ChatFormatting.GRAY)));
      }

   }

   private void setPortError(final @Nullable Component errorMessage) {
      if (this.portEdit != null) {
         this.portValid = errorMessage == null;
         if (errorMessage == null) {
            this.portEdit.setTextColor(-2039584);
            this.portEdit.setTooltip((Tooltip)null);
         } else {
            this.portEdit.setTextColor(-2142128);
            this.portEdit.setTooltip(Tooltip.create(errorMessage));
         }

      }
   }

   private void changeMultiplayerScope(final IntegratedServer singleplayerServer) {
      if (singleplayerServer.unpublishServer()) {
         this.sendPublishMessage(Component.translatable("menu.multiplayerOptions.publish.stopped"));
      }

      if (this.wantedMultiplayerScope != MinecraftServer.MultiplayerScope.OFF) {
         this.publish(singleplayerServer, this.wantedMultiplayerScope);
      }

      this.minecraft.getPlayerSocialManager().getPresenceHandler().tryUpdatePresence();
   }

   private void updateApplyChangesActiveState() {
      if (this.applyChanges != null) {
         this.applyChanges.active = (!this.portIsRequired() || this.portValid) && this.hasSettingsChanges();
      }

   }

   private boolean portIsRequired() {
      return this.wantedMultiplayerScope == MinecraftServer.MultiplayerScope.LAN;
   }

   private boolean lanPortChanged() {
      return this.wantedMultiplayerScope == MinecraftServer.MultiplayerScope.LAN && this.initialMultiplayerScope == MinecraftServer.MultiplayerScope.LAN && this.port != this.initialPort;
   }

   private boolean hasSettingsChanges() {
      return this.wantedMultiplayerScope != this.initialMultiplayerScope || this.gameMode != this.initialGameMode || this.commands != this.initialCommands || this.lanPortChanged();
   }

   private void publish(final IntegratedServer singleplayerServer, final MinecraftServer.MultiplayerScope scope) {
      boolean published = singleplayerServer.publishServer(scope, this.port);
      if (!published) {
         this.sendPublishMessage(Component.translatable("commands.publish.failed"));
      } else {
         Component message = scope == MinecraftServer.MultiplayerScope.LAN ? Component.translatable("menu.multiplayerOptions.publish.started.lan", ComponentUtils.copyOnClickText(String.valueOf(this.port))) : Component.translatable("menu.multiplayerOptions.publish.started.online");
         this.sendPublishMessage(message);
      }
   }

   private void sendPublishMessage(final Component message) {
      this.minecraft.gui.hud.getChat().addClientSystemMessage(message);
      this.minecraft.getNarrator().saySystemQueued(message);
      this.minecraft.updateTitle();
   }

   protected void repositionElements() {
      this.layout.arrangeElements();
   }

   public void onClose() {
      this.minecraft.gui.setScreen(this.lastScreen);
   }

   private @Nullable Component tryParsePort(final String value) {
      if (value.isBlank()) {
         this.port = HttpUtil.getAvailablePort();
         return null;
      } else {
         try {
            int parsed = Integer.parseInt(value);
            if (parsed >= 1024 && parsed <= 65535) {
               if (parsed != this.initialPort && !HttpUtil.isPortAvailable(parsed)) {
                  return PORT_UNAVAILABLE;
               } else {
                  this.port = parsed;
                  return null;
               }
            } else {
               return INVALID_PORT;
            }
         } catch (NumberFormatException var3) {
            this.port = HttpUtil.getAvailablePort();
            return INVALID_PORT;
         }
      }
   }

   public void extractBackground(final GuiGraphicsExtractor graphics, final int mouseX, final int mouseY, final float a) {
      super.extractBackground(graphics, mouseX, mouseY, a);
      Identifier headerSeparator = this.minecraft.level == null ? Screen.HEADER_SEPARATOR : Screen.INWORLD_HEADER_SEPARATOR;
      Identifier footerSeparator = this.minecraft.level == null ? Screen.FOOTER_SEPARATOR : Screen.INWORLD_FOOTER_SEPARATOR;
      graphics.blit(RenderPipelines.GUI_TEXTURED, headerSeparator, 0, this.layout.getHeaderHeight() - 2, 0.0F, 0.0F, this.width, 2, 32, 2);
      graphics.blit(RenderPipelines.GUI_TEXTURED, footerSeparator, 0, this.height - this.layout.getFooterHeight() - 2, 0.0F, 0.0F, this.width, 2, 32, 2);
      graphics.blit(RenderPipelines.GUI_TEXTURED, INWORLD_MENU_LIST_BACKGROUND, 0, this.layout.getHeaderHeight(), (float)this.width, (float)(this.height - this.layout.getFooterHeight()), this.width, this.layout.getContentHeight() - 2, 32, 32);
   }

   static {
      OTHER_PLAYERS_HEADER = Component.translatable("menu.multiplayerOptions.otherPlayers.header").withStyle(ChatFormatting.UNDERLINE, ChatFormatting.BOLD);
      APPLY_CHANGES = Component.translatable("menu.multiplayerOptions.applyChanges");
      INWORLD_MENU_LIST_BACKGROUND = Identifier.withDefaultNamespace("textures/gui/inworld_menu_list_background.png");
   }
}
