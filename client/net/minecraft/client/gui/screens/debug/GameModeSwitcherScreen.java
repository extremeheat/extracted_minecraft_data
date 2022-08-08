package net.minecraft.client.gui.screens.debug;

import com.google.common.collect.Lists;
import com.mojang.blaze3d.platform.InputConstants;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.chat.NarratorChatListener;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.multiplayer.MultiPlayerGameMode;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.GameType;
import net.minecraft.world.level.block.Blocks;

public class GameModeSwitcherScreen extends Screen {
   static final ResourceLocation GAMEMODE_SWITCHER_LOCATION = new ResourceLocation("textures/gui/container/gamemode_switcher.png");
   private static final int SPRITE_SHEET_WIDTH = 128;
   private static final int SPRITE_SHEET_HEIGHT = 128;
   private static final int SLOT_AREA = 26;
   private static final int SLOT_PADDING = 5;
   private static final int SLOT_AREA_PADDED = 31;
   private static final int HELP_TIPS_OFFSET_Y = 5;
   private static final int ALL_SLOTS_WIDTH = GameModeSwitcherScreen.GameModeIcon.values().length * 31 - 5;
   private static final Component SELECT_KEY;
   private final Optional<GameModeIcon> previousHovered = GameModeSwitcherScreen.GameModeIcon.getFromGameType(this.getDefaultSelected());
   private Optional<GameModeIcon> currentlyHovered = Optional.empty();
   private int firstMouseX;
   private int firstMouseY;
   private boolean setFirstMousePos;
   private final List<GameModeSlot> slots = Lists.newArrayList();

   public GameModeSwitcherScreen() {
      super(NarratorChatListener.NO_TITLE);
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

   protected void init() {
      super.init();
      this.currentlyHovered = this.previousHovered.isPresent() ? this.previousHovered : GameModeSwitcherScreen.GameModeIcon.getFromGameType(this.minecraft.gameMode.getPlayerMode());

      for(int var1 = 0; var1 < GameModeSwitcherScreen.GameModeIcon.VALUES.length; ++var1) {
         GameModeIcon var2 = GameModeSwitcherScreen.GameModeIcon.VALUES[var1];
         this.slots.add(new GameModeSlot(var2, this.width / 2 - ALL_SLOTS_WIDTH / 2 + var1 * 31, this.height / 2 - 31));
      }

   }

   public void render(PoseStack var1, int var2, int var3, float var4) {
      if (!this.checkToClose()) {
         RenderSystem.setShader(GameRenderer::getPositionTexShader);
         var1.pushPose();
         RenderSystem.enableBlend();
         RenderSystem.setShaderTexture(0, GAMEMODE_SWITCHER_LOCATION);
         int var5 = this.width / 2 - 62;
         int var6 = this.height / 2 - 31 - 27;
         blit(var1, var5, var6, 0.0F, 0.0F, 125, 75, 128, 128);
         var1.popPose();
         super.render(var1, var2, var3, var4);
         this.currentlyHovered.ifPresent((var2x) -> {
            drawCenteredString(var1, this.font, var2x.getName(), this.width / 2, this.height / 2 - 31 - 20, -1);
         });
         drawCenteredString(var1, this.font, SELECT_KEY, this.width / 2, this.height / 2 + 5, 16777215);
         if (!this.setFirstMousePos) {
            this.firstMouseX = var2;
            this.firstMouseY = var3;
            this.setFirstMousePos = true;
         }

         boolean var7 = this.firstMouseX == var2 && this.firstMouseY == var3;
         Iterator var8 = this.slots.iterator();

         while(var8.hasNext()) {
            GameModeSlot var9 = (GameModeSlot)var8.next();
            var9.render(var1, var2, var3, var4);
            this.currentlyHovered.ifPresent((var1x) -> {
               var9.setSelected(var1x == var9.icon);
            });
            if (!var7 && var9.isHoveredOrFocused()) {
               this.currentlyHovered = Optional.of(var9.icon);
            }
         }

      }
   }

   private void switchToHoveredGameMode() {
      switchToHoveredGameMode(this.minecraft, this.currentlyHovered);
   }

   private static void switchToHoveredGameMode(Minecraft var0, Optional<GameModeIcon> var1) {
      if (var0.gameMode != null && var0.player != null && var1.isPresent()) {
         Optional var2 = GameModeSwitcherScreen.GameModeIcon.getFromGameType(var0.gameMode.getPlayerMode());
         GameModeIcon var3 = (GameModeIcon)var1.get();
         if (var2.isPresent() && var0.player.hasPermissions(2) && var3 != var2.get()) {
            var0.player.command(var3.getCommand());
         }

      }
   }

   private boolean checkToClose() {
      if (!InputConstants.isKeyDown(this.minecraft.getWindow().getWindow(), 292)) {
         this.switchToHoveredGameMode();
         this.minecraft.setScreen((Screen)null);
         return true;
      } else {
         return false;
      }
   }

   public boolean keyPressed(int var1, int var2, int var3) {
      if (var1 == 293 && this.currentlyHovered.isPresent()) {
         this.setFirstMousePos = false;
         this.currentlyHovered = ((GameModeIcon)this.currentlyHovered.get()).getNext();
         return true;
      } else {
         return super.keyPressed(var1, var2, var3);
      }
   }

   public boolean isPauseScreen() {
      return false;
   }

   static {
      SELECT_KEY = Component.translatable("debug.gamemodes.select_next", Component.translatable("debug.gamemodes.press_f4").withStyle(ChatFormatting.AQUA));
   }

   static enum GameModeIcon {
      CREATIVE(Component.translatable("gameMode.creative"), "gamemode creative", new ItemStack(Blocks.GRASS_BLOCK)),
      SURVIVAL(Component.translatable("gameMode.survival"), "gamemode survival", new ItemStack(Items.IRON_SWORD)),
      ADVENTURE(Component.translatable("gameMode.adventure"), "gamemode adventure", new ItemStack(Items.MAP)),
      SPECTATOR(Component.translatable("gameMode.spectator"), "gamemode spectator", new ItemStack(Items.ENDER_EYE));

      protected static final GameModeIcon[] VALUES = values();
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

      void drawIcon(ItemRenderer var1, int var2, int var3) {
         var1.renderAndDecorateItem(this.renderStack, var2, var3);
      }

      Component getName() {
         return this.name;
      }

      String getCommand() {
         return this.command;
      }

      Optional<GameModeIcon> getNext() {
         switch (this) {
            case CREATIVE:
               return Optional.of(SURVIVAL);
            case SURVIVAL:
               return Optional.of(ADVENTURE);
            case ADVENTURE:
               return Optional.of(SPECTATOR);
            default:
               return Optional.of(CREATIVE);
         }
      }

      static Optional<GameModeIcon> getFromGameType(GameType var0) {
         switch (var0) {
            case SPECTATOR:
               return Optional.of(SPECTATOR);
            case SURVIVAL:
               return Optional.of(SURVIVAL);
            case CREATIVE:
               return Optional.of(CREATIVE);
            case ADVENTURE:
               return Optional.of(ADVENTURE);
            default:
               return Optional.empty();
         }
      }

      // $FF: synthetic method
      private static GameModeIcon[] $values() {
         return new GameModeIcon[]{CREATIVE, SURVIVAL, ADVENTURE, SPECTATOR};
      }
   }

   public class GameModeSlot extends AbstractWidget {
      final GameModeIcon icon;
      private boolean isSelected;

      public GameModeSlot(GameModeIcon var2, int var3, int var4) {
         super(var3, var4, 26, 26, var2.getName());
         this.icon = var2;
      }

      public void renderButton(PoseStack var1, int var2, int var3, float var4) {
         Minecraft var5 = Minecraft.getInstance();
         this.drawSlot(var1, var5.getTextureManager());
         this.icon.drawIcon(GameModeSwitcherScreen.this.itemRenderer, this.x + 5, this.y + 5);
         if (this.isSelected) {
            this.drawSelection(var1, var5.getTextureManager());
         }

      }

      public void updateNarration(NarrationElementOutput var1) {
         this.defaultButtonNarrationText(var1);
      }

      public boolean isHoveredOrFocused() {
         return super.isHoveredOrFocused() || this.isSelected;
      }

      public void setSelected(boolean var1) {
         this.isSelected = var1;
      }

      private void drawSlot(PoseStack var1, TextureManager var2) {
         RenderSystem.setShader(GameRenderer::getPositionTexShader);
         RenderSystem.setShaderTexture(0, GameModeSwitcherScreen.GAMEMODE_SWITCHER_LOCATION);
         var1.pushPose();
         var1.translate((double)this.x, (double)this.y, 0.0);
         blit(var1, 0, 0, 0.0F, 75.0F, 26, 26, 128, 128);
         var1.popPose();
      }

      private void drawSelection(PoseStack var1, TextureManager var2) {
         RenderSystem.setShader(GameRenderer::getPositionTexShader);
         RenderSystem.setShaderTexture(0, GameModeSwitcherScreen.GAMEMODE_SWITCHER_LOCATION);
         var1.pushPose();
         var1.translate((double)this.x, (double)this.y, 0.0);
         blit(var1, 0, 0, 26.0F, 75.0F, 26, 26, 128, 128);
         var1.popPose();
      }
   }
}
