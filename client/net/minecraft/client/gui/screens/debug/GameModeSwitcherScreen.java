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
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.GameType;
import net.minecraft.world.level.block.Blocks;

public class GameModeSwitcherScreen extends Screen {
   private static final ResourceLocation GAMEMODE_SWITCHER_LOCATION = new ResourceLocation("textures/gui/container/gamemode_switcher.png");
   private static final int ALL_SLOTS_WIDTH = GameModeSwitcherScreen.GameModeIcon.values().length * 30 - 5;
   private static final Component SELECT_KEY;
   private final Optional<GameModeSwitcherScreen.GameModeIcon> previousHovered = GameModeSwitcherScreen.GameModeIcon.getFromGameType(this.getDefaultSelected());
   private Optional<GameModeSwitcherScreen.GameModeIcon> currentlyHovered = Optional.empty();
   private int firstMouseX;
   private int firstMouseY;
   private boolean setFirstMousePos;
   private final List<GameModeSwitcherScreen.GameModeSlot> slots = Lists.newArrayList();

   public GameModeSwitcherScreen() {
      super(NarratorChatListener.NO_TITLE);
   }

   private GameType getDefaultSelected() {
      GameType var1 = Minecraft.getInstance().gameMode.getPlayerMode();
      GameType var2 = Minecraft.getInstance().gameMode.getPreviousPlayerMode();
      if (var2 == GameType.NOT_SET) {
         if (var1 == GameType.CREATIVE) {
            var2 = GameType.SURVIVAL;
         } else {
            var2 = GameType.CREATIVE;
         }
      }

      return var2;
   }

   protected void init() {
      super.init();
      this.currentlyHovered = this.previousHovered.isPresent() ? this.previousHovered : GameModeSwitcherScreen.GameModeIcon.getFromGameType(this.minecraft.gameMode.getPlayerMode());

      for(int var1 = 0; var1 < GameModeSwitcherScreen.GameModeIcon.VALUES.length; ++var1) {
         GameModeSwitcherScreen.GameModeIcon var2 = GameModeSwitcherScreen.GameModeIcon.VALUES[var1];
         this.slots.add(new GameModeSwitcherScreen.GameModeSlot(var2, this.width / 2 - ALL_SLOTS_WIDTH / 2 + var1 * 30, this.height / 2 - 30));
      }

   }

   public void render(PoseStack var1, int var2, int var3, float var4) {
      if (!this.checkToClose()) {
         var1.pushPose();
         RenderSystem.enableBlend();
         this.minecraft.getTextureManager().bind(GAMEMODE_SWITCHER_LOCATION);
         int var5 = this.width / 2 - 62;
         int var6 = this.height / 2 - 30 - 27;
         blit(var1, var5, var6, 0.0F, 0.0F, 125, 75, 128, 128);
         var1.popPose();
         super.render(var1, var2, var3, var4);
         this.currentlyHovered.ifPresent((var2x) -> {
            drawCenteredString(var1, this.font, var2x.getName(), this.width / 2, this.height / 2 - 30 - 20, -1);
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
            GameModeSwitcherScreen.GameModeSlot var9 = (GameModeSwitcherScreen.GameModeSlot)var8.next();
            var9.render(var1, var2, var3, var4);
            this.currentlyHovered.ifPresent((var1x) -> {
               var9.setSelected(var1x == var9.icon);
            });
            if (!var7 && var9.isHovered()) {
               this.currentlyHovered = Optional.of(var9.icon);
            }
         }

      }
   }

   private void switchToHoveredGameMode() {
      switchToHoveredGameMode(this.minecraft, this.currentlyHovered);
   }

   private static void switchToHoveredGameMode(Minecraft var0, Optional<GameModeSwitcherScreen.GameModeIcon> var1) {
      if (var0.gameMode != null && var0.player != null && var1.isPresent()) {
         Optional var2 = GameModeSwitcherScreen.GameModeIcon.getFromGameType(var0.gameMode.getPlayerMode());
         GameModeSwitcherScreen.GameModeIcon var3 = (GameModeSwitcherScreen.GameModeIcon)var1.get();
         if (var2.isPresent() && var0.player.hasPermissions(2) && var3 != var2.get()) {
            var0.player.chat(var3.getCommand());
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
         this.currentlyHovered = ((GameModeSwitcherScreen.GameModeIcon)this.currentlyHovered.get()).getNext();
         return true;
      } else {
         return super.keyPressed(var1, var2, var3);
      }
   }

   public boolean isPauseScreen() {
      return false;
   }

   static {
      SELECT_KEY = new TranslatableComponent("debug.gamemodes.select_next", new Object[]{(new TranslatableComponent("debug.gamemodes.press_f4")).withStyle(ChatFormatting.AQUA)});
   }

   public class GameModeSlot extends AbstractWidget {
      private final GameModeSwitcherScreen.GameModeIcon icon;
      private boolean isSelected;

      public GameModeSlot(GameModeSwitcherScreen.GameModeIcon var2, int var3, int var4) {
         super(var3, var4, 25, 25, var2.getName());
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

      public boolean isHovered() {
         return super.isHovered() || this.isSelected;
      }

      public void setSelected(boolean var1) {
         this.isSelected = var1;
         this.narrate();
      }

      private void drawSlot(PoseStack var1, TextureManager var2) {
         var2.bind(GameModeSwitcherScreen.GAMEMODE_SWITCHER_LOCATION);
         var1.pushPose();
         var1.translate((double)this.x, (double)this.y, 0.0D);
         blit(var1, 0, 0, 0.0F, 75.0F, 25, 25, 128, 128);
         var1.popPose();
      }

      private void drawSelection(PoseStack var1, TextureManager var2) {
         var2.bind(GameModeSwitcherScreen.GAMEMODE_SWITCHER_LOCATION);
         var1.pushPose();
         var1.translate((double)this.x, (double)this.y, 0.0D);
         blit(var1, 0, 0, 25.0F, 75.0F, 25, 25, 128, 128);
         var1.popPose();
      }
   }

   static enum GameModeIcon {
      CREATIVE(new TranslatableComponent("gameMode.creative"), "/gamemode creative", new ItemStack(Blocks.GRASS_BLOCK)),
      SURVIVAL(new TranslatableComponent("gameMode.survival"), "/gamemode survival", new ItemStack(Items.IRON_SWORD)),
      ADVENTURE(new TranslatableComponent("gameMode.adventure"), "/gamemode adventure", new ItemStack(Items.MAP)),
      SPECTATOR(new TranslatableComponent("gameMode.spectator"), "/gamemode spectator", new ItemStack(Items.ENDER_EYE));

      protected static final GameModeSwitcherScreen.GameModeIcon[] VALUES = values();
      final Component name;
      final String command;
      final ItemStack renderStack;

      private GameModeIcon(Component var3, String var4, ItemStack var5) {
         this.name = var3;
         this.command = var4;
         this.renderStack = var5;
      }

      private void drawIcon(ItemRenderer var1, int var2, int var3) {
         var1.renderAndDecorateItem(this.renderStack, var2, var3);
      }

      private Component getName() {
         return this.name;
      }

      private String getCommand() {
         return this.command;
      }

      private Optional<GameModeSwitcherScreen.GameModeIcon> getNext() {
         switch(this) {
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

      private static Optional<GameModeSwitcherScreen.GameModeIcon> getFromGameType(GameType var0) {
         switch(var0) {
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
   }
}
