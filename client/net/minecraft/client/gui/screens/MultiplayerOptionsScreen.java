package net.minecraft.client.gui.screens;

import java.util.Arrays;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.CycleButton;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.components.StringWidget;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.client.gui.layouts.CommonLayouts;
import net.minecraft.client.gui.layouts.HeaderAndFooterLayout;
import net.minecraft.client.gui.layouts.LinearLayout;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.client.server.IntegratedServer;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.ComponentUtils;
import net.minecraft.resources.Identifier;
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
   private static final Component NETWORK_HEADER;
   private static final Component OTHER_PLAYERS_HEADER;
   private static final Component APPLY_CHANGES;
   private static final Identifier INWORLD_MENU_LIST_BACKGROUND;
   private final HeaderAndFooterLayout layout = new HeaderAndFooterLayout(this);
   private final Screen lastScreen;
   private IntegratedServer.MultiplayerScope wantedMultiplayerScope;
   private GameType gameMode;
   private boolean commands;
   private int port;
   private boolean portValid;
   private @Nullable Button applyChanges;
   private @Nullable EditBox portEdit;
   private IntegratedServer.MultiplayerScope initialMultiplayerScope;
   private GameType initialGameMode;
   private boolean initialCommands;

   public MultiplayerOptionsScreen(final Screen lastScreen) {
      super(TITLE);
      this.wantedMultiplayerScope = IntegratedServer.MultiplayerScope.OFF;
      this.gameMode = GameType.SURVIVAL;
      this.port = HttpUtil.getAvailablePort();
      this.portValid = true;
      this.initialMultiplayerScope = IntegratedServer.MultiplayerScope.OFF;
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
         content.addChild(new StringWidget(NETWORK_HEADER, this.font));
         content.addChild(CycleButton.builder(IntegratedServer.MultiplayerScope::getDisplayName, singleplayerServer.getMultiplayerScope()).withValues(this.minecraft.getPlayerSocialManager().isFriendListEnabled() ? IntegratedServer.MultiplayerScope.values() : (IntegratedServer.MultiplayerScope[])Arrays.stream(IntegratedServer.MultiplayerScope.values()).filter((scope) -> scope != IntegratedServer.MultiplayerScope.ONLINE).toArray((x$0) -> new IntegratedServer.MultiplayerScope[x$0])).withTooltip(IntegratedServer.MultiplayerScope::getTooltip).create(Component.translatable("menu.multiplayerOptions.network"), (var1, value) -> {
            this.wantedMultiplayerScope = value;
            this.updateApplyChangesActiveState();
         }));
         this.initialMultiplayerScope = singleplayerServer.getMultiplayerScope();
         this.wantedMultiplayerScope = this.initialMultiplayerScope;
         this.applyChanges = Button.builder(APPLY_CHANGES, (var2) -> {
            this.minecraft.gui.setScreen((Screen)null);
            if (this.gameMode != this.initialGameMode) {
               singleplayerServer.applyDefaultGameMode(this.gameMode);
            }

            if (this.commands != this.initialCommands) {
               singleplayerServer.setCommandsAllowedForAllPlayers(this.commands);
            }

            if (this.wantedMultiplayerScope != this.initialMultiplayerScope) {
               this.changeMultiplayerScope(singleplayerServer);
            }

         }).build();
         this.applyChanges.active = false;
         this.portEdit = new EditBox(this.font, PORT_INFO_TEXT);
         this.portEdit.setResponder((value) -> {
            Component errorMessage = this.tryParsePort(value);
            this.portEdit.setHint(Component.literal("" + this.port));
            if (errorMessage == null) {
               this.portValid = true;
               this.portEdit.setTextColor(-2039584);
               this.portEdit.setTooltip((Tooltip)null);
            } else {
               this.portValid = false;
               this.portEdit.setTextColor(-2142128);
               this.portEdit.setTooltip(Tooltip.create(errorMessage));
            }

            this.updateApplyChangesActiveState();
         });
         this.portEdit.setHint(Component.literal("" + this.port));
         content.addChild(CommonLayouts.labeledElement(this.font, this.portEdit, PORT_INFO_TEXT));
         content.addChild(new StringWidget(OTHER_PLAYERS_HEADER, this.font));
         LinearLayout otherPlayerSettings = (LinearLayout)content.addChild(LinearLayout.horizontal().spacing(8));
         otherPlayerSettings.defaultCellSetting().alignHorizontallyCenter();
         GameType forcedGameMode = singleplayerServer.getForcedGameType();
         this.gameMode = forcedGameMode != null ? forcedGameMode : singleplayerServer.getDefaultGameType();
         this.initialGameMode = this.gameMode;
         otherPlayerSettings.addChild(CycleButton.builder(GameType::getShortDisplayName, this.gameMode).withValues(GameType.SURVIVAL, GameType.SPECTATOR, GameType.CREATIVE, GameType.ADVENTURE).create(GAME_MODE_LABEL, (var1, value) -> {
            this.gameMode = value;
            this.updateApplyChangesActiveState();
         }));
         this.commands = singleplayerServer.isPublished() ? singleplayerServer.getPlayerList().isAllowCommandsForAllPlayers() : singleplayerServer.getWorldData().isAllowCommands();
         this.initialCommands = this.commands;
         otherPlayerSettings.addChild(CycleButton.onOffBuilder(this.commands).create(ALLOW_COMMANDS_LABEL, (var1, value) -> {
            this.commands = value;
            this.updateApplyChangesActiveState();
         }));
         LinearLayout footer = (LinearLayout)this.layout.addToFooter(LinearLayout.horizontal().spacing(8));
         footer.addChild(this.applyChanges);
         footer.addChild(Button.builder(CommonComponents.GUI_CANCEL, (var1) -> this.onClose()).build());
         this.layout.visitWidgets(this::addRenderableWidget);
         this.repositionElements();
      }
   }

   private void changeMultiplayerScope(final IntegratedServer singleplayerServer) {
      switch (this.wantedMultiplayerScope) {
         case OFF:
            if (singleplayerServer.unpublishServer()) {
               this.sendPublishMessage(Component.translatable("menu.multiplayerOptions.publish.stopped"));
               this.minecraft.getPlayerSocialManager().getPresenceHandler().clearInvites();
            }
            break;
         case LAN:
            if (singleplayerServer.unpublishServer()) {
               this.sendPublishMessage(Component.translatable("menu.multiplayerOptions.publish.stopped"));
               this.minecraft.getPlayerSocialManager().getPresenceHandler().clearInvites();
            }

            this.publish(singleplayerServer, IntegratedServer.MultiplayerScope.LAN);
            break;
         case ONLINE:
            if (singleplayerServer.unpublishServer()) {
               this.sendPublishMessage(Component.translatable("menu.multiplayerOptions.publish.stopped"));
               this.minecraft.getPlayerSocialManager().getPresenceHandler().clearInvites();
            }

            this.publish(singleplayerServer, IntegratedServer.MultiplayerScope.ONLINE);
      }

      this.minecraft.getPlayerSocialManager().getPresenceHandler().tryUpdatePresence();
   }

   private void updateApplyChangesActiveState() {
      if (this.applyChanges != null) {
         this.applyChanges.active = this.portValid && this.hasSettingsChanges();
      }

   }

   private boolean hasSettingsChanges() {
      return this.wantedMultiplayerScope != this.initialMultiplayerScope || this.gameMode != this.initialGameMode || this.commands != this.initialCommands;
   }

   private void publish(final IntegratedServer singleplayerServer, final IntegratedServer.MultiplayerScope scope) {
      String key = scope == IntegratedServer.MultiplayerScope.LAN ? "menu.multiplayerOptions.publish.started.lan" : "menu.multiplayerOptions.publish.started.online";
      if (singleplayerServer.isPublished()) {
         singleplayerServer.setMultiplayerScope(scope);
         this.sendPublishMessage(Component.translatable(key, ComponentUtils.copyOnClickText(String.valueOf(singleplayerServer.getPort()))));
      } else {
         boolean published = singleplayerServer.publishServer(this.gameMode, this.commands, this.port);
         Component message = published ? Component.translatable(key, ComponentUtils.copyOnClickText(String.valueOf(this.port))) : Component.translatable("commands.publish.failed");
         if (published) {
            singleplayerServer.setMultiplayerScope(scope);
         }

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

   public void extractBackground(final GuiGraphicsExtractor graphics, final int mouseX, final int mouseY, final float a) {
      super.extractBackground(graphics, mouseX, mouseY, a);
      Identifier headerSeparator = this.minecraft.level == null ? Screen.HEADER_SEPARATOR : Screen.INWORLD_HEADER_SEPARATOR;
      Identifier footerSeparator = this.minecraft.level == null ? Screen.FOOTER_SEPARATOR : Screen.INWORLD_FOOTER_SEPARATOR;
      graphics.blit(RenderPipelines.GUI_TEXTURED, headerSeparator, 0, this.layout.getHeaderHeight() - 2, 0.0F, 0.0F, this.width, 2, 32, 2);
      graphics.blit(RenderPipelines.GUI_TEXTURED, footerSeparator, 0, this.height - this.layout.getFooterHeight() - 2, 0.0F, 0.0F, this.width, 2, 32, 2);
      graphics.blit(RenderPipelines.GUI_TEXTURED, INWORLD_MENU_LIST_BACKGROUND, 0, this.layout.getHeaderHeight(), (float)this.width, (float)(this.height - this.layout.getFooterHeight()), this.width, this.layout.getContentHeight() - 2, 32, 32);
   }

   static {
      NETWORK_HEADER = Component.translatable("menu.multiplayerOptions.network.header").withStyle(ChatFormatting.UNDERLINE, ChatFormatting.BOLD);
      OTHER_PLAYERS_HEADER = Component.translatable("menu.multiplayerOptions.otherPlayers.header").withStyle(ChatFormatting.UNDERLINE, ChatFormatting.BOLD);
      APPLY_CHANGES = Component.translatable("menu.multiplayerOptions.applyChanges");
      INWORLD_MENU_LIST_BACKGROUND = Identifier.withDefaultNamespace("textures/gui/inworld_menu_list_background.png");
   }
}
