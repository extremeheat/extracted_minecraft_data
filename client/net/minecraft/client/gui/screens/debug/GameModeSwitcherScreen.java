package net.minecraft.client.gui.screens.debug;

import com.google.common.collect.Lists;
import com.mojang.blaze3d.platform.InputConstants;
import com.mojang.blaze3d.systems.RenderSystem;
import java.util.List;
import net.minecraft.ChatFormatting;
import net.minecraft.client.GameNarrator;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.multiplayer.MultiPlayerGameMode;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.GameType;
import net.minecraft.world.level.block.Blocks;

public class GameModeSwitcherScreen extends Screen {
   static final ResourceLocation SLOT_SPRITE = new ResourceLocation("gamemode_switcher/slot");
   static final ResourceLocation SELECTION_SPRITE = new ResourceLocation("gamemode_switcher/selection");
   private static final ResourceLocation GAMEMODE_SWITCHER_LOCATION = new ResourceLocation("textures/gui/container/gamemode_switcher.png");
   private static final int SPRITE_SHEET_WIDTH = 128;
   private static final int SPRITE_SHEET_HEIGHT = 128;
   private static final int SLOT_AREA = 26;
   private static final int SLOT_PADDING = 5;
   private static final int SLOT_AREA_PADDED = 31;
   private static final int HELP_TIPS_OFFSET_Y = 5;
   private static final int ALL_SLOTS_WIDTH = GameModeSwitcherScreen.GameModeIcon.values().length * 31 - 5;
   private static final Component SELECT_KEY = Component.translatable(
      "debug.gamemodes.select_next", Component.translatable("debug.gamemodes.press_f4").withStyle(ChatFormatting.AQUA)
   );
   private final GameModeSwitcherScreen.GameModeIcon previousHovered;
   private GameModeSwitcherScreen.GameModeIcon currentlyHovered;
   private int firstMouseX;
   private int firstMouseY;
   private boolean setFirstMousePos;
   private final List<GameModeSwitcherScreen.GameModeSlot> slots = Lists.newArrayList();

   public GameModeSwitcherScreen() {
      super(GameNarrator.NO_TITLE);
      this.previousHovered = GameModeSwitcherScreen.GameModeIcon.getFromGameType(this.getDefaultSelected());
      this.currentlyHovered = this.previousHovered;
   }

   private GameType getDefaultSelected() {
      MultiPlayerGameMode var1 = Minecraft.getInstance().gameMode;
      GameType var2 = var1.getPreviousPlayerMode();
      if (var2 != null) {
         return var2;
      } else {
         return var1.getPlayerMode() == GameType.CREATIVE ? GameType.SURVIVAL : GameType.CREATIVE;
      }
   }

   @Override
   protected void init() {
      super.init();
      this.currentlyHovered = this.previousHovered;

      for (int var1 = 0; var1 < GameModeSwitcherScreen.GameModeIcon.VALUES.length; var1++) {
         GameModeSwitcherScreen.GameModeIcon var2 = GameModeSwitcherScreen.GameModeIcon.VALUES[var1];
         this.slots.add(new GameModeSwitcherScreen.GameModeSlot(var2, this.width / 2 - ALL_SLOTS_WIDTH / 2 + var1 * 31, this.height / 2 - 31));
      }
   }

   @Override
   public void render(GuiGraphics var1, int var2, int var3, float var4) {
      if (!this.checkToClose()) {
         var1.pose().pushPose();
         RenderSystem.enableBlend();
         int var5 = this.width / 2 - 62;
         int var6 = this.height / 2 - 31 - 27;
         var1.blit(GAMEMODE_SWITCHER_LOCATION, var5, var6, 0.0F, 0.0F, 125, 75, 128, 128);
         var1.pose().popPose();
         super.render(var1, var2, var3, var4);
         var1.drawCenteredString(this.font, this.currentlyHovered.getName(), this.width / 2, this.height / 2 - 31 - 20, -1);
         var1.drawCenteredString(this.font, SELECT_KEY, this.width / 2, this.height / 2 + 5, 16777215);
         if (!this.setFirstMousePos) {
            this.firstMouseX = var2;
            this.firstMouseY = var3;
            this.setFirstMousePos = true;
         }

         boolean var7 = this.firstMouseX == var2 && this.firstMouseY == var3;

         for (GameModeSwitcherScreen.GameModeSlot var9 : this.slots) {
            var9.render(var1, var2, var3, var4);
            var9.setSelected(this.currentlyHovered == var9.icon);
            if (!var7 && var9.isHoveredOrFocused()) {
               this.currentlyHovered = var9.icon;
            }
         }
      }
   }

   @Override
   public void renderBackground(GuiGraphics var1, int var2, int var3, float var4) {
   }

   private void switchToHoveredGameMode() {
      switchToHoveredGameMode(this.minecraft, this.currentlyHovered);
   }

   private static void switchToHoveredGameMode(Minecraft var0, GameModeSwitcherScreen.GameModeIcon var1) {
      if (var0.gameMode != null && var0.player != null) {
         GameModeSwitcherScreen.GameModeIcon var2 = GameModeSwitcherScreen.GameModeIcon.getFromGameType(var0.gameMode.getPlayerMode());
         if (var0.player.hasPermissions(2) && var1 != var2) {
            var0.player.connection.sendUnsignedCommand(var1.getCommand());
         }
      }
   }

   private boolean checkToClose() {
      if (!InputConstants.isKeyDown(this.minecraft.getWindow().getWindow(), 292)) {
         this.switchToHoveredGameMode();
         this.minecraft.setScreen(null);
         return true;
      } else {
         return false;
      }
   }

   @Override
   public boolean keyPressed(int var1, int var2, int var3) {
      if (var1 == 293) {
         this.setFirstMousePos = false;
         this.currentlyHovered = this.currentlyHovered.getNext();
         return true;
      } else {
         return super.keyPressed(var1, var2, var3);
      }
   }

   @Override
   public boolean isPauseScreen() {
      return false;
   }

   static enum GameModeIcon {
      CREATIVE(Component.translatable("gameMode.creative"), "gamemode creative", new ItemStack(Blocks.GRASS_BLOCK)),
      SURVIVAL(Component.translatable("gameMode.survival"), "gamemode survival", new ItemStack(Items.IRON_SWORD)),
      ADVENTURE(Component.translatable("gameMode.adventure"), "gamemode adventure", new ItemStack(Items.MAP)),
      SPECTATOR(Component.translatable("gameMode.spectator"), "gamemode spectator", new ItemStack(Items.ENDER_EYE));

      protected static final GameModeSwitcherScreen.GameModeIcon[] VALUES = values();
      private static final int ICON_AREA = 16;
      protected static final int ICON_TOP_LEFT = 5;
      final Component name;
      final String command;
      final ItemStack renderStack;

      private GameModeIcon(Component var3, String var4, ItemStack var5) {
         this.name = var3;
         this.command = var4;
         this.renderStack = var5;
      }

      void drawIcon(GuiGraphics var1, int var2, int var3) {
         var1.renderItem(this.renderStack, var2, var3);
      }

      Component getName() {
         return this.name;
      }

      String getCommand() {
         return this.command;
      }

      GameModeSwitcherScreen.GameModeIcon getNext() {
         return switch (this) {
            case CREATIVE -> SURVIVAL;
            case SURVIVAL -> ADVENTURE;
            case ADVENTURE -> SPECTATOR;
            case SPECTATOR -> CREATIVE;
         };
      }

      static GameModeSwitcherScreen.GameModeIcon getFromGameType(GameType var0) {
         return switch (var0) {
            case SPECTATOR -> SPECTATOR;
            case SURVIVAL -> SURVIVAL;
            case CREATIVE -> CREATIVE;
            case ADVENTURE -> ADVENTURE;
         };
      }
   }

   public class GameModeSlot extends AbstractWidget {
      final GameModeSwitcherScreen.GameModeIcon icon;
      private boolean isSelected;

      public GameModeSlot(GameModeSwitcherScreen.GameModeIcon var2, int var3, int var4) {
         super(var3, var4, 26, 26, var2.getName());
         this.icon = var2;
      }

      @Override
      public void renderWidget(GuiGraphics var1, int var2, int var3, float var4) {
         this.drawSlot(var1);
         this.icon.drawIcon(var1, this.getX() + 5, this.getY() + 5);
         if (this.isSelected) {
            this.drawSelection(var1);
         }
      }

      @Override
      public void updateWidgetNarration(NarrationElementOutput var1) {
         this.defaultButtonNarrationText(var1);
      }

      @Override
      public boolean isHoveredOrFocused() {
         return super.isHoveredOrFocused() || this.isSelected;
      }

      public void setSelected(boolean var1) {
         this.isSelected = var1;
      }

      private void drawSlot(GuiGraphics var1) {
         var1.blitSprite(GameModeSwitcherScreen.SLOT_SPRITE, this.getX(), this.getY(), 26, 26);
      }

      private void drawSelection(GuiGraphics var1) {
         var1.blitSprite(GameModeSwitcherScreen.SELECTION_SPRITE, this.getX(), this.getY(), 26, 26);
      }
   }
}
