package net.minecraft.client.gui.screens;

import java.util.function.Consumer;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.AbstractSelectionList;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.CycleButton;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.components.FocusableTextWidget;
import net.minecraft.client.gui.components.LockIconButton;
import net.minecraft.client.gui.components.PopupScreen;
import net.minecraft.client.gui.components.ScrollableLayout;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.client.gui.layouts.EqualSpacingLayout;
import net.minecraft.client.gui.layouts.GridLayout;
import net.minecraft.client.gui.layouts.HeaderAndFooterLayout;
import net.minecraft.client.gui.layouts.LayoutElement;
import net.minecraft.client.gui.layouts.LinearLayout;
import net.minecraft.client.gui.screens.multiplayer.RestrictionsScreen;
import net.minecraft.client.gui.screens.options.HasDifficultyReaction;
import net.minecraft.client.gui.screens.options.HasGamemasterPermissionReaction;
import net.minecraft.client.gui.screens.options.InWorldGameRulesScreen;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.client.server.IntegratedServer;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.ComponentUtils;
import net.minecraft.network.protocol.game.ServerboundChangeDifficultyPacket;
import net.minecraft.network.protocol.game.ServerboundLockDifficultyPacket;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.permissions.Permissions;
import net.minecraft.util.HttpUtil;
import net.minecraft.world.Difficulty;
import net.minecraft.world.level.GameType;
import net.minecraft.world.level.Level;
import org.jspecify.annotations.Nullable;

public class WorldOptionsScreen extends Screen implements HasGamemasterPermissionReaction, HasDifficultyReaction {
   private static final Component TITLE = Component.translatable("options.worldOptions.title");
   private static final Component GENERAL_TITLE;
   private static final Component GAME_RULES;
   private static final Tooltip GAMERULES_DISABLED_TOOLTIP;
   private static final Tooltip GAMERULES_DISABLED_HARDCORE_TOOLTIP;
   private static final Component DEFAULT_GAME_MODE;
   private static final Tooltip DEFAULT_GAME_MODE_TOOLTIP;
   private static final Component PERSONAL_GAME_MODE;
   private static final Tooltip PERSONAL_GAME_MODE_TOOLTIP;
   private static final Tooltip GAME_MODE_DISABLED_OPERATOR_TOOLTIP;
   private static final Tooltip GAME_MODE_DISABLED_HARDCORE_TOOLTIP;
   private static final Component ALLOW_COMMANDS;
   private static final Tooltip ALLOW_COMMANDS_TOOLTIP;
   private static final Tooltip ALLOW_COMMANDS_DISABLED_HARDCORE_TOOLTIP;
   private static final Tooltip ALLOW_COMMANDS_DISABLED_DEMO_TOOLTIP;
   private static final Component RESTRICTIONS;
   private static final Component MULTIPLAYER_TITLE;
   private static final Component GUEST_COMMAND_ACCESS;
   private static final Tooltip GUEST_COMMAND_ACCESS_TOOLTIP;
   private static final Tooltip GUEST_COMMAND_ACCESS_DISABLED_MULTIPLAYER_SCOPE_OFF;
   private static final Tooltip GUEST_COMMAND_ACCESS_DISABLED_COMMANDS_OFF;
   private static final Component FORCE_GAME_MODE;
   private static final Tooltip FORCE_GAME_MODE_ON_TOOLTIP;
   private static final Tooltip FORCE_GAME_MODE_OFF_TOOLTIP;
   private static final Tooltip FORCE_GAME_MODE_OFF_COMMANDS_TOOLTIP;
   private static final int PORT_LOWER_BOUND = 1024;
   private static final int PORT_HIGHER_BOUND = 65535;
   private static final Component PORT_INFO_TEXT;
   private static final Component PORT_UNAVAILABLE;
   private static final Component INVALID_PORT;
   private static final Component APPLY_CHANGES;
   private final HeaderAndFooterLayout layout = new HeaderAndFooterLayout(this);
   private @Nullable ScrollableLayout scrollArea;
   private final DifficultyButtons difficultyButtons;
   private @Nullable Button gameRulesButton;
   private @Nullable CycleButton<GameType> defaultGameModeButton;
   private @Nullable CycleButton<GameType> personalGameModeButton;
   private @Nullable Difficulty initialDifficulty;
   private @Nullable Difficulty wantedDifficulty;
   private @Nullable Boolean initialDifficultyLocked;
   private @Nullable Boolean wantedDifficultyLocked;
   private @Nullable GameType initialDefaultGameMode;
   private @Nullable GameType wantedDefaultGameMode;
   private @Nullable GameType initialPersonalGameMode;
   private @Nullable GameType wantedPersonalGameMode;
   private @Nullable Boolean initialAllowCommands;
   private @Nullable Boolean wantedAllowCommands;
   private MinecraftServer.@Nullable MultiplayerScope initialMultiplayerScope;
   private MinecraftServer.@Nullable MultiplayerScope wantedMultiplayerScope;
   private int port = HttpUtil.getAvailablePort();
   private boolean portValid = true;
   private @Nullable EditBox portEdit;
   private int initialPort;
   private @Nullable Boolean initialGuestCommandAccess;
   private @Nullable Boolean wantedGuestCommandAccess;
   private @Nullable CycleButton<Boolean> guestCommandAccessButton;
   private @Nullable Boolean initialForceGameMode;
   private @Nullable Boolean wantedForceGameMode;
   private @Nullable CycleButton<Boolean> forceGameModeButton;
   private final Screen lastScreen;
   private final Level level;
   private @Nullable Button applyChanges;

   public WorldOptionsScreen(final Screen lastScreen, final Level level) {
      super(TITLE);
      this.lastScreen = lastScreen;
      this.level = level;
      this.difficultyButtons = WorldOptionsScreen.DifficultyButtons.create(this.minecraft, level, this);
   }

   protected void init() {
      this.layout.addTitleHeader(TITLE, this.font);
      IntegratedServer singleplayerServer = this.minecraft.getSingleplayerServer();
      LinearLayout content = LinearLayout.vertical().spacing(8);
      content.defaultCellSetting().padding(8).alignHorizontallyCenter().alignVerticallyTop();
      this.scrollArea = (ScrollableLayout)this.layout.addToContents(new ScrollableLayout(this.minecraft, content, this.layout.getContentHeight()));
      this.generalOptions(content, singleplayerServer);
      if (singleplayerServer != null) {
         this.multiplayerOptions(content, singleplayerServer);
      }

      this.applyChanges = Button.builder(APPLY_CHANGES, (var2) -> {
         this.applyChanges(singleplayerServer);
         this.minecraft.gui.setScreen(this.lastScreen);
      }).build();
      this.applyChanges.active = false;
      LinearLayout footer = (LinearLayout)this.layout.addToFooter(LinearLayout.horizontal().spacing(8));
      footer.addChild(this.applyChanges);
      footer.addChild(Button.builder(CommonComponents.GUI_CANCEL, (var1) -> this.onClose()).build());
      this.layout.visitWidgets(this::addRenderableWidget);
      this.repositionElements();
   }

   private FocusableTextWidget createCategoryHeader(final Component title) {
      return FocusableTextWidget.builder(title, this.font).alwaysShowBorder(false).backgroundFill(FocusableTextWidget.BackgroundFill.ON_FOCUS).build();
   }

   private void generalOptions(final LinearLayout content, final @Nullable IntegratedServer singleplayerServer) {
      GridLayout grid = (GridLayout)content.addChild(new GridLayout());
      GridLayout.RowHelper rowHelper = grid.columnSpacing(8).rowSpacing(4).createRowHelper(2);
      rowHelper.defaultCellSetting().alignHorizontallyCenter();
      rowHelper.addChild(this.createCategoryHeader(GENERAL_TITLE), 2);
      boolean host = singleplayerServer != null && this.minecraft.player != null && singleplayerServer.isSingleplayerOwner(this.minecraft.player.nameAndId());
      if (host) {
         this.wantedDefaultGameMode = singleplayerServer.getWorldData().getGameType();
         this.initialDefaultGameMode = this.wantedDefaultGameMode;
         this.defaultGameModeButton = (CycleButton)rowHelper.addChild(this.createGameModeButton(singleplayerServer, DEFAULT_GAME_MODE, DEFAULT_GAME_MODE_TOOLTIP, this.wantedDefaultGameMode, (value) -> this.wantedDefaultGameMode = value), 2);
         this.wantedPersonalGameMode = singleplayerServer.getPersonalGameMode();
         this.initialPersonalGameMode = this.wantedPersonalGameMode;
         if (this.wantedPersonalGameMode != null) {
            this.personalGameModeButton = (CycleButton)rowHelper.addChild(this.createGameModeButton(singleplayerServer, PERSONAL_GAME_MODE, PERSONAL_GAME_MODE_TOOLTIP, this.wantedPersonalGameMode, (value) -> this.wantedPersonalGameMode = value), 2);
         }

         this.wantedAllowCommands = singleplayerServer.getWorldData().isAllowCommands();
         this.initialAllowCommands = this.wantedAllowCommands;
         rowHelper.addChild(this.createAllowCommandsButton(singleplayerServer));
      }

      rowHelper.addChild(this.difficultyButtons.layout());
      this.gameRulesButton = (Button)rowHelper.addChild(this.createGameRulesButton(singleplayerServer));
      rowHelper.addChild(this.createRestrictionsButton());
   }

   private Button createGameRulesButton(final @Nullable IntegratedServer singleplayerServer) {
      Button gameRulesButton = Button.builder(GAME_RULES, (var1) -> {
         if (this.minecraft.player != null) {
            this.minecraft.gui.setScreen(new InWorldGameRulesScreen(this.minecraft.player.connection, (var1x) -> this.minecraft.gui.setScreen(this), this));
         }

      }).build();
      this.updateButton(gameRulesButton, singleplayerServer, (Tooltip)null, GAMERULES_DISABLED_TOOLTIP, GAMERULES_DISABLED_HARDCORE_TOOLTIP);
      return gameRulesButton;
   }

   private CycleButton<GameType> createGameModeButton(final IntegratedServer singleplayerServer, final Component buttonName, final Tooltip tooltip, final GameType defaultValue, final Consumer<GameType> gameTypeToChange) {
      CycleButton<GameType> gameModeButton = CycleButton.builder(GameType::getShortDisplayName, defaultValue).withValues(GameType.values()).withTooltip((var1) -> tooltip).create(0, 0, 308, 20, buttonName, (var2, value) -> {
         gameTypeToChange.accept(value);
         this.updateApplyChangesActiveState();
      });
      this.updateButton(gameModeButton, singleplayerServer, tooltip, GAME_MODE_DISABLED_OPERATOR_TOOLTIP, GAME_MODE_DISABLED_HARDCORE_TOOLTIP);
      return gameModeButton;
   }

   private CycleButton<Boolean> createAllowCommandsButton(final IntegratedServer singleplayerServer) {
      CycleButton<Boolean> allowCommandsButton = CycleButton.onOffBuilder(singleplayerServer.getWorldData().isAllowCommands()).create(ALLOW_COMMANDS, (var2, allowCommands) -> {
         this.wantedAllowCommands = allowCommands;
         this.updateGuestCommandAccessButton(singleplayerServer);
         this.updateApplyChangesActiveState();
      });
      if (singleplayerServer.isDemo()) {
         allowCommandsButton.active = false;
         allowCommandsButton.setTooltip(ALLOW_COMMANDS_DISABLED_DEMO_TOOLTIP);
      } else if (!singleplayerServer.isHardcore() || this.minecraft.player != null && this.minecraft.player.permissions().hasPermission(Permissions.COMMANDS_OWNER)) {
         allowCommandsButton.setTooltip(ALLOW_COMMANDS_TOOLTIP);
      } else {
         allowCommandsButton.active = false;
         allowCommandsButton.setTooltip(ALLOW_COMMANDS_DISABLED_HARDCORE_TOOLTIP);
      }

      return allowCommandsButton;
   }

   private void updateButton(final @Nullable AbstractWidget widget, final @Nullable IntegratedServer singleplayerServer, final @Nullable Tooltip tooltip, final Tooltip disabledTooltip, final Tooltip hardcoreTooltip) {
      if (widget != null) {
         boolean hardcore = singleplayerServer != null && singleplayerServer.isHardcore();
         boolean hasGameMasterPermission = this.minecraft.player != null && this.minecraft.player.permissions().hasPermission(Permissions.COMMANDS_GAMEMASTER);
         widget.active = !hardcore && hasGameMasterPermission;
         widget.setTooltip(hardcore ? hardcoreTooltip : (hasGameMasterPermission ? tooltip : disabledTooltip));
      }

   }

   private Button createRestrictionsButton() {
      return Button.builder(RESTRICTIONS, (var1) -> {
         if (this.minecraft.player != null) {
            this.minecraft.gui.setScreen(new RestrictionsScreen(this, this.minecraft.player.chatAbilities()));
         }

      }).build();
   }

   private void multiplayerOptions(final LinearLayout content, final IntegratedServer singleplayerServer) {
      GridLayout grid = (GridLayout)content.addChild(new GridLayout());
      grid.defaultCellSetting().alignHorizontallyCenter();
      GridLayout.RowHelper helper = grid.columnSpacing(8).rowSpacing(4).createRowHelper(2);
      helper.addChild(this.createCategoryHeader(MULTIPLAYER_TITLE), 2);
      this.wantedMultiplayerScope = singleplayerServer.getMultiplayerScope();
      this.initialMultiplayerScope = this.wantedMultiplayerScope;
      helper.addChild(CycleButton.onOffBuilder(singleplayerServer.getMultiplayerScope() == MinecraftServer.MultiplayerScope.LAN).withTooltip((value) -> Tooltip.create(value ? MinecraftServer.MultiplayerScope.LAN.getTooltip() : MinecraftServer.MultiplayerScope.OFF.getTooltip())).create(Component.translatable("menu.multiplayerOptions.lan"), (var2, value) -> {
         this.wantedMultiplayerScope = value ? MinecraftServer.MultiplayerScope.LAN : MinecraftServer.MultiplayerScope.OFF;
         this.updateGuestCommandAccessButton(singleplayerServer);
         this.updatePortControlsState();
         this.updateApplyChangesActiveState();
      }));
      if (this.initialMultiplayerScope == MinecraftServer.MultiplayerScope.LAN) {
         this.port = singleplayerServer.getPort();
         this.initialPort = this.port;
      }

      this.portEdit = new EditBox(this.font, PORT_INFO_TEXT);
      this.portEdit.setResponder((value) -> {
         this.setPortError(this.portEdit, this.tryParsePort(value));
         this.portEdit.setHint(Component.literal(String.valueOf(this.port)));
         this.updateApplyChangesActiveState();
      });
      this.portEdit.setTooltip(Tooltip.create(PORT_INFO_TEXT));
      if (this.initialMultiplayerScope == MinecraftServer.MultiplayerScope.LAN) {
         this.portEdit.setValue(String.valueOf(this.port));
      }

      helper.addChild(this.portEdit);
      this.updatePortControlsState();
      this.wantedGuestCommandAccess = singleplayerServer.getGuestCommandAccess();
      this.initialGuestCommandAccess = this.wantedGuestCommandAccess;
      this.guestCommandAccessButton = (CycleButton)helper.addChild(CycleButton.onOffBuilder(this.initialGuestCommandAccess).withTooltip((var0) -> GUEST_COMMAND_ACCESS_TOOLTIP).create(GUEST_COMMAND_ACCESS, (var2, value) -> {
         this.wantedGuestCommandAccess = value;
         this.updateForceGameModeButton(singleplayerServer);
         this.updateApplyChangesActiveState();
      }));
      this.wantedForceGameMode = singleplayerServer.forceGameMode();
      this.initialForceGameMode = this.wantedForceGameMode;
      this.forceGameModeButton = (CycleButton)helper.addChild(CycleButton.onOffBuilder(this.initialForceGameMode).withTooltip((value) -> value ? FORCE_GAME_MODE_ON_TOOLTIP : FORCE_GAME_MODE_OFF_TOOLTIP).create(FORCE_GAME_MODE, (var1, value) -> {
         this.wantedForceGameMode = value;
         this.updateApplyChangesActiveState();
      }));
      this.updateGuestCommandAccessButton(singleplayerServer);
   }

   private void updateGuestCommandAccessButton(final IntegratedServer singleplayerServer) {
      if (this.guestCommandAccessButton != null) {
         boolean lanScope = this.wantedMultiplayerScope == MinecraftServer.MultiplayerScope.LAN;
         boolean allowCommands = Boolean.TRUE.equals(this.wantedAllowCommands);
         Tooltip tooltip;
         if (!lanScope) {
            this.wantedGuestCommandAccess = false;
            tooltip = GUEST_COMMAND_ACCESS_DISABLED_MULTIPLAYER_SCOPE_OFF;
         } else if (!allowCommands) {
            this.wantedGuestCommandAccess = false;
            tooltip = GUEST_COMMAND_ACCESS_DISABLED_COMMANDS_OFF;
         } else {
            this.wantedGuestCommandAccess = singleplayerServer.getGuestCommandAccess();
            tooltip = GUEST_COMMAND_ACCESS_TOOLTIP;
         }

         this.guestCommandAccessButton.setValue(this.wantedGuestCommandAccess);
         this.guestCommandAccessButton.setTooltip(tooltip);
         this.guestCommandAccessButton.active = lanScope && allowCommands;
         this.updateForceGameModeButton(singleplayerServer);
      }

   }

   private void updateForceGameModeButton(final IntegratedServer singleplayerServer) {
      if (this.forceGameModeButton != null) {
         boolean lanScope = this.wantedMultiplayerScope == MinecraftServer.MultiplayerScope.LAN;
         boolean guestCommandAccess = Boolean.TRUE.equals(this.wantedGuestCommandAccess);
         Tooltip tooltip;
         if (!lanScope) {
            this.wantedForceGameMode = true;
            tooltip = null;
         } else if (guestCommandAccess) {
            this.wantedForceGameMode = false;
            tooltip = FORCE_GAME_MODE_OFF_COMMANDS_TOOLTIP;
         } else {
            this.wantedForceGameMode = singleplayerServer.forceGameMode();
            tooltip = this.wantedForceGameMode ? FORCE_GAME_MODE_ON_TOOLTIP : FORCE_GAME_MODE_OFF_TOOLTIP;
         }

         this.forceGameModeButton.setValue(this.wantedForceGameMode);
         this.forceGameModeButton.setTooltip(tooltip);
         this.forceGameModeButton.active = lanScope && !guestCommandAccess;
      }

   }

   protected boolean hasChanges() {
      return this.hasSettingsChanges() && (!this.portIsRequired() || this.portValid);
   }

   protected void applyChanges(final @Nullable IntegratedServer singleplayerServer) {
      if (this.wantedDifficulty != null && this.wantedDifficulty != this.initialDifficulty && this.minecraft.getConnection() != null) {
         this.minecraft.getConnection().send(new ServerboundChangeDifficultyPacket(this.wantedDifficulty));
      }

      if (this.wantedDifficultyLocked != null && this.wantedDifficultyLocked != this.initialDifficultyLocked && this.minecraft.getConnection() != null) {
         this.minecraft.getConnection().send(new ServerboundLockDifficultyPacket(true));
         this.difficultyButtons.lockButton.setLocked(true);
         WorldOptionsScreen.DifficultyButtons.updateDifficultyButtonsState(this.minecraft, this.level, this.difficultyButtons.difficultyButton, this.difficultyButtons.lockButton);
      }

      if (singleplayerServer != null) {
         if (this.wantedForceGameMode != null && this.wantedForceGameMode != this.initialForceGameMode) {
            singleplayerServer.setForceGameMode(this.wantedForceGameMode);
         }

         if (this.wantedDefaultGameMode != null && this.wantedDefaultGameMode != this.initialDefaultGameMode) {
            singleplayerServer.setWorldGameType(this.wantedDefaultGameMode);
         }

         if (this.wantedPersonalGameMode != null && this.wantedPersonalGameMode != this.initialPersonalGameMode) {
            singleplayerServer.setPersonalGameType(this.wantedPersonalGameMode);
         }

         if (this.wantedAllowCommands != null && this.wantedAllowCommands != this.initialAllowCommands) {
            singleplayerServer.setWorldAllowCommands(this.wantedAllowCommands);
         }

         if (this.wantedGuestCommandAccess != null && this.wantedGuestCommandAccess != this.initialGuestCommandAccess) {
            singleplayerServer.setGuestCommandAccess(this.wantedGuestCommandAccess);
         }

         if (this.wantedMultiplayerScope != this.initialMultiplayerScope || this.lanPortChanged()) {
            this.changeMultiplayerScope(singleplayerServer);
         }

      }
   }

   private void updateApplyChangesActiveState() {
      if (this.applyChanges != null) {
         this.applyChanges.active = this.hasChanges();
      }

   }

   protected void repositionElements() {
      this.scrollArea.arrangeElements();
      this.scrollArea.setMaxHeight(this.layout.getContentHeight());
      this.scrollArea.setMinHeight(this.layout.getContentHeight());
      this.layout.arrangeElements();
   }

   protected void extractMenuBackground(final GuiGraphicsExtractor graphics) {
      super.extractMenuBackground(graphics);
      graphics.blit(RenderPipelines.GUI_TEXTURED, AbstractSelectionList.INWORLD_MENU_LIST_BACKGROUND, this.layout.getX(), this.layout.getHeaderHeight(), (float)this.width, (float)(this.height - this.layout.getFooterHeight() + (int)this.scrollArea.getScrollAmount()), this.width, this.layout.getContentHeight(), 32, 32);
   }

   public void extractRenderState(final GuiGraphicsExtractor graphics, final int xm, final int ym, final float a) {
      super.extractRenderState(graphics, xm, ym, a);
      graphics.blit(RenderPipelines.GUI_TEXTURED, Screen.INWORLD_HEADER_SEPARATOR, this.layout.getX(), this.layout.getHeaderHeight() - 2, 0.0F, 0.0F, this.width, 2, 32, 2);
      graphics.blit(RenderPipelines.GUI_TEXTURED, Screen.INWORLD_FOOTER_SEPARATOR, this.layout.getX(), this.height - this.layout.getFooterHeight(), 0.0F, 0.0F, this.width, 2, 32, 2);
   }

   public void onClose() {
      this.minecraft.gui.setScreen(this.lastScreen);
   }

   public void onGamemasterPermissionChanged(final boolean hasGamemasterPermission) {
      IntegratedServer singleplayerServer = this.minecraft.getSingleplayerServer();
      this.updateButton(this.gameRulesButton, singleplayerServer, (Tooltip)null, GAMERULES_DISABLED_TOOLTIP, GAMERULES_DISABLED_HARDCORE_TOOLTIP);
      this.updateButton(this.defaultGameModeButton, singleplayerServer, DEFAULT_GAME_MODE_TOOLTIP, GAME_MODE_DISABLED_OPERATOR_TOOLTIP, GAME_MODE_DISABLED_HARDCORE_TOOLTIP);
      this.updateButton(this.personalGameModeButton, singleplayerServer, PERSONAL_GAME_MODE_TOOLTIP, GAME_MODE_DISABLED_OPERATOR_TOOLTIP, GAME_MODE_DISABLED_HARDCORE_TOOLTIP);
      this.difficultyButtons.refresh(this.minecraft, this);
      if (!hasGamemasterPermission && !this.minecraft.hasSingleplayerServer()) {
         this.minecraft.gui.setScreen(this.lastScreen);
         Screen var4 = this.minecraft.gui.screen();
         if (var4 instanceof HasGamemasterPermissionReaction) {
            HasGamemasterPermissionReaction screen = (HasGamemasterPermissionReaction)var4;
            screen.onGamemasterPermissionChanged(hasGamemasterPermission);
         }
      }

   }

   public void updatePersonalGameModeButton(final GameType mode) {
      if (this.personalGameModeButton != null) {
         this.initialPersonalGameMode = mode;
         this.personalGameModeButton.setValue(mode);
      }

   }

   public void added() {
      this.difficultyButtons.refresh(this.minecraft, this);
   }

   public void onDifficultyChanged() {
      this.difficultyButtons.refresh(this.minecraft, this);
   }

   private boolean portIsRequired() {
      return this.wantedMultiplayerScope == MinecraftServer.MultiplayerScope.LAN;
   }

   private boolean hasSettingsChanges() {
      return this.wantedDifficulty != this.initialDifficulty || this.wantedDifficultyLocked != this.initialDifficultyLocked || this.wantedDefaultGameMode != this.initialDefaultGameMode || this.wantedPersonalGameMode != this.initialPersonalGameMode || this.wantedAllowCommands != this.initialAllowCommands || this.wantedMultiplayerScope != this.initialMultiplayerScope || this.wantedGuestCommandAccess != this.initialGuestCommandAccess || this.wantedForceGameMode != this.initialForceGameMode || this.lanPortChanged();
   }

   private boolean lanPortChanged() {
      return this.wantedMultiplayerScope == MinecraftServer.MultiplayerScope.LAN && this.initialMultiplayerScope == MinecraftServer.MultiplayerScope.LAN && this.port != this.initialPort;
   }

   private void changeMultiplayerScope(final IntegratedServer singleplayerServer) {
      if (this.wantedMultiplayerScope != null) {
         if (singleplayerServer.unpublishServer()) {
            this.sendPublishMessage(Component.translatable("menu.multiplayerOptions.publish.stopped"));
         }

         if (this.wantedMultiplayerScope != MinecraftServer.MultiplayerScope.OFF) {
            this.publish(singleplayerServer, this.wantedMultiplayerScope);
         }

         this.minecraft.getPlayerSocialManager().getPresenceHandler().tryUpdatePresence();
      }
   }

   private void publish(final IntegratedServer singleplayerServer, final MinecraftServer.MultiplayerScope scope) {
      if (!singleplayerServer.publishServer(scope, this.port)) {
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

   private void updatePortControlsState() {
      boolean lanWanted = this.wantedMultiplayerScope == MinecraftServer.MultiplayerScope.LAN;
      if (this.portEdit != null) {
         String desired = lanWanted ? (this.initialMultiplayerScope == MinecraftServer.MultiplayerScope.LAN ? String.valueOf(this.initialPort) : "") : "";
         if (!this.portEdit.getValue().equals(desired)) {
            this.portEdit.setValue(desired);
         }

         this.portEdit.setEditable(lanWanted);
         this.portEdit.active = lanWanted;
         this.portEdit.setHint((Component)(lanWanted ? Component.literal(String.valueOf(this.port)) : PORT_INFO_TEXT));
         if (!lanWanted) {
            this.portEdit.setFocused(false);
            this.setPortError(this.portEdit, (Component)null);
         }
      }

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

   private void setPortError(final EditBox portEdit, final @Nullable Component errorMessage) {
      this.portValid = errorMessage == null;
      if (errorMessage == null) {
         portEdit.setTextColor(-2039584);
         portEdit.setTooltip(Tooltip.create(PORT_INFO_TEXT));
      } else {
         portEdit.setTextColor(-2142128);
         portEdit.setTooltip(Tooltip.create(errorMessage));
      }

   }

   static {
      GENERAL_TITLE = Component.translatable("options.worldOptions.general.title").withStyle(ChatFormatting.UNDERLINE, ChatFormatting.BOLD);
      GAME_RULES = Component.translatable("editGamerule.inGame.button");
      GAMERULES_DISABLED_TOOLTIP = Tooltip.create(Component.translatable("editGamerule.inGame.disabled.tooltip"));
      GAMERULES_DISABLED_HARDCORE_TOOLTIP = Tooltip.create(Component.translatable("editGamerule.inGame.disabled.hardcore.tooltip"));
      DEFAULT_GAME_MODE = Component.translatable("options.worldOptions.game_mode");
      DEFAULT_GAME_MODE_TOOLTIP = Tooltip.create(Component.translatable("options.worldOptions.game_mode.tooltip"));
      PERSONAL_GAME_MODE = Component.translatable("options.worldOptions.personal_game_mode");
      PERSONAL_GAME_MODE_TOOLTIP = Tooltip.create(Component.translatable("options.worldOptions.personal_game_mode.tooltip"));
      GAME_MODE_DISABLED_OPERATOR_TOOLTIP = Tooltip.create(Component.translatable("options.worldOptions.game_mode.disabled.operator.tooltip"));
      GAME_MODE_DISABLED_HARDCORE_TOOLTIP = Tooltip.create(Component.translatable("options.worldOptions.game_mode.disabled.tooltip"));
      ALLOW_COMMANDS = Component.translatable("selectWorld.allowCommands");
      ALLOW_COMMANDS_TOOLTIP = Tooltip.create(Component.translatable("options.worldOptions.allow_commands.tooltip"));
      ALLOW_COMMANDS_DISABLED_HARDCORE_TOOLTIP = Tooltip.create(Component.translatable("options.worldOptions.allow_commands.disabled.tooltip"));
      ALLOW_COMMANDS_DISABLED_DEMO_TOOLTIP = Tooltip.create(Component.translatable("options.worldOptions.allow_commands.disabled.demo.tooltip"));
      RESTRICTIONS = Component.translatable("restrictions_screen.button");
      MULTIPLAYER_TITLE = Component.translatable("options.worldOptions.multiplayer.title").withStyle(ChatFormatting.UNDERLINE, ChatFormatting.BOLD);
      GUEST_COMMAND_ACCESS = Component.translatable("options.worldOptions.guest.command_access");
      GUEST_COMMAND_ACCESS_TOOLTIP = Tooltip.create(Component.translatable("options.worldOptions.guest.command_access.tooltip"));
      GUEST_COMMAND_ACCESS_DISABLED_MULTIPLAYER_SCOPE_OFF = Tooltip.create(Component.translatable("options.worldOptions.guest.command_access.disabled.scope.tooltip"));
      GUEST_COMMAND_ACCESS_DISABLED_COMMANDS_OFF = Tooltip.create(Component.translatable("options.worldOptions.guest.command_access.disabled.commands.tooltip"));
      FORCE_GAME_MODE = Component.translatable("options.worldOptions.guest.force_game_mode");
      FORCE_GAME_MODE_ON_TOOLTIP = Tooltip.create(Component.translatable("options.worldOptions.guest.force_game_mode.on.tooltip"));
      FORCE_GAME_MODE_OFF_TOOLTIP = Tooltip.create(Component.translatable("options.worldOptions.guest.force_game_mode.off.tooltip"));
      FORCE_GAME_MODE_OFF_COMMANDS_TOOLTIP = Tooltip.create(Component.translatable("options.worldOptions.guest.force_game_mode.off.commands.tooltip"));
      PORT_INFO_TEXT = Component.translatable("lanServer.port");
      PORT_UNAVAILABLE = Component.translatable("lanServer.port.unavailable", 1024, 65535);
      INVALID_PORT = Component.translatable("lanServer.port.invalid", 1024, 65535);
      APPLY_CHANGES = Component.translatable("menu.multiplayerOptions.applyChanges");
   }

   private static record DifficultyButtons(LayoutElement layout, CycleButton<Difficulty> difficultyButton, LockIconButton lockButton, Level level) {
      private static final Component DIFFICULTY_TITLE = Component.translatable("options.difficulty");
      private static final Tooltip DIFFICULTY_DISABLED_HARDCORE_TOOLTIP = Tooltip.create(Component.translatable("options.worldOptions.difficulty.disabled.hardcore.tooltip"));
      private static final Tooltip DIFFICULTY_DISABLED_LOCKED_TOOLTIP = Tooltip.create(Component.translatable("options.worldOptions.difficulty.disabled.locked.tooltip"));
      private static final Tooltip DIFFICULTY_DISABLED_OPERATOR_TOOLTIP = Tooltip.create(Component.translatable("options.worldOptions.difficulty.disabled.operator.tooltip"));
      private static final Component DIFFICULTY_LOCK_TITLE = Component.translatable("difficulty.lock.title");

      private DifficultyButtons {
         super();
      }

      public static DifficultyButtons create(final Minecraft minecraft, final Level level, final WorldOptionsScreen screen) {
         screen.wantedDifficulty = level.getDifficulty();
         screen.initialDifficulty = screen.wantedDifficulty;
         CycleButton<Difficulty> difficultyButton = CycleButton.builder(Difficulty::getDisplayName, level.getDifficulty()).withValues(Difficulty.values()).create(0, 0, 150, 20, DIFFICULTY_TITLE, (var1, value) -> {
            screen.wantedDifficulty = value;
            screen.updateApplyChangesActiveState();
         });
         screen.wantedDifficultyLocked = isDifficultyLocked(level);
         screen.initialDifficultyLocked = screen.wantedDifficultyLocked;
         LockIconButton lockButton = new LockIconButton(0, 0, (button) -> {
            Component difficultyDisplayName = screen.wantedDifficulty != null ? screen.wantedDifficulty.getDisplayName() : level.getDifficulty().getDisplayName();
            minecraft.gui.setScreen((new PopupScreen.Builder(screen, DIFFICULTY_LOCK_TITLE)).addMessage(Component.translatable("difficulty.lock.question", difficultyDisplayName)).addButton(CommonComponents.GUI_YES, (var3) -> {
               if (button instanceof LockIconButton lockIconButton) {
                  lockIconButton.setLocked(true);
               }

               screen.wantedDifficultyLocked = true;
               screen.updateApplyChangesActiveState();
               minecraft.gui.setScreen(screen);
            }).addButton(CommonComponents.GUI_NO, (var3) -> {
               if (button instanceof LockIconButton lockIconButton) {
                  lockIconButton.setLocked(false);
               }

               screen.wantedDifficultyLocked = false;
               screen.updateApplyChangesActiveState();
               minecraft.gui.setScreen(screen);
            }).build());
         });
         difficultyButton.setWidth(difficultyButton.getWidth() - lockButton.getWidth());
         lockButton.setLocked(isDifficultyLocked(level));
         updateDifficultyButtonsState(minecraft, level, difficultyButton, lockButton);
         EqualSpacingLayout linearLayout = new EqualSpacingLayout(150, 0, EqualSpacingLayout.Orientation.HORIZONTAL);
         linearLayout.addChild(difficultyButton);
         linearLayout.addChild(lockButton);
         return new DifficultyButtons(linearLayout, difficultyButton, lockButton, level);
      }

      private void refresh(final Minecraft minecraft, final WorldOptionsScreen worldOptionsScreen) {
         this.difficultyButton.setValue(worldOptionsScreen.wantedDifficulty != null ? worldOptionsScreen.wantedDifficulty : this.level.getDifficulty());
         this.lockButton.setLocked(worldOptionsScreen.wantedDifficultyLocked != null ? worldOptionsScreen.wantedDifficultyLocked : isDifficultyLocked(this.level));
         updateDifficultyButtonsState(minecraft, this.level, this.difficultyButton, this.lockButton);
      }

      private static void updateDifficultyButtonsState(final Minecraft minecraft, final Level level, final CycleButton<Difficulty> difficultyButton, final LockIconButton lockButton) {
         if (minecraft.player != null && minecraft.player.permissions().hasPermission(Permissions.COMMANDS_GAMEMASTER)) {
            if (level.getLevelData().isDifficultyLocked()) {
               lockButton.active = false;
               difficultyButton.active = false;
               difficultyButton.setTooltip(DIFFICULTY_DISABLED_LOCKED_TOOLTIP);
            } else if (level.getLevelData().isHardcore()) {
               lockButton.active = false;
               difficultyButton.active = false;
               difficultyButton.setTooltip(DIFFICULTY_DISABLED_HARDCORE_TOOLTIP);
            } else {
               lockButton.active = true;
               difficultyButton.active = true;
               difficultyButton.setTooltip((Tooltip)null);
            }
         } else {
            lockButton.active = false;
            difficultyButton.active = false;
            difficultyButton.setTooltip(DIFFICULTY_DISABLED_OPERATOR_TOOLTIP);
         }

      }

      private static boolean isDifficultyLocked(final Level level) {
         return level.getLevelData().isDifficultyLocked() || level.getLevelData().isHardcore();
      }
   }
}
