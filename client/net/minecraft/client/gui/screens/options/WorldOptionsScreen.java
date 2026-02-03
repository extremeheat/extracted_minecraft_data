package net.minecraft.client.gui.screens.options;

import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.StringWidget;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.client.gui.layouts.HeaderAndFooterLayout;
import net.minecraft.client.gui.layouts.LayoutSettings;
import net.minecraft.client.gui.layouts.LinearLayout;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.server.permissions.Permissions;

public class WorldOptionsScreen extends Screen implements HasGamemasterPermissionReaction {
   private static final Component TITLE = Component.translatable("options.worldOptions.title");
   private static final Component GAME_RULES = Component.translatable("editGamerule.inGame.button");
   private static final Tooltip GAMERULES_DISABLED_TOOLTIP = Tooltip.create(Component.translatable("editGamerule.inGame.disabled.tooltip"));
   private final Screen lastScreen;
   private final HeaderAndFooterLayout layout = new HeaderAndFooterLayout(this);

   public WorldOptionsScreen(final Screen lastScreen) {
      super(TITLE);
      this.lastScreen = lastScreen;
   }

   protected void init() {
      this.layout.addToHeader(new StringWidget(TITLE, this.font), LayoutSettings::alignHorizontallyCenter);
      LinearLayout content = (LinearLayout)this.layout.addToContents(LinearLayout.horizontal().spacing(8));
      content.addChild(DifficultyButtons.create(this.minecraft, this));
      content.addChild(this.createGameRulesButton());
      this.layout.addToFooter(Button.builder(CommonComponents.GUI_DONE, (button) -> this.onClose()).width(200).build());
      this.layout.visitWidgets((x$0) -> this.addRenderableWidget(x$0));
      this.repositionElements();
   }

   private Button createGameRulesButton() {
      Button gameRulesButton = Button.builder(GAME_RULES, (button) -> {
         if (this.minecraft.player != null) {
            this.minecraft.setScreen(new InWorldGameRulesScreen(this.minecraft.player.connection, (result) -> this.minecraft.setScreen(this), this));
         }

      }).width(150).build();
      if (this.minecraft.player == null || !this.minecraft.player.permissions().hasPermission(Permissions.COMMANDS_GAMEMASTER)) {
         gameRulesButton.active = false;
         gameRulesButton.setTooltip(GAMERULES_DISABLED_TOOLTIP);
      }

      return gameRulesButton;
   }

   protected void repositionElements() {
      this.layout.arrangeElements();
   }

   public void onClose() {
      this.minecraft.setScreen(this.lastScreen);
   }

   public void onGamemasterPermissionChanged(final boolean hasGamemasterPermission) {
      if (!hasGamemasterPermission) {
         this.minecraft.setScreen(this.lastScreen);
         Screen var3 = this.minecraft.screen;
         if (var3 instanceof HasGamemasterPermissionReaction) {
            HasGamemasterPermissionReaction screen = (HasGamemasterPermissionReaction)var3;
            screen.onGamemasterPermissionChanged(hasGamemasterPermission);
         }
      }

   }
}
