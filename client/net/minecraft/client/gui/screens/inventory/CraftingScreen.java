package net.minecraft.client.gui.screens.inventory;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.navigation.ScreenPosition;
import net.minecraft.client.gui.screens.recipebook.CraftingRecipeBookComponent;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.CraftingMenu;

public class CraftingScreen extends AbstractRecipeBookScreen<CraftingMenu> {
   private static final Identifier CRAFTING_TABLE_LOCATION = Identifier.withDefaultNamespace("textures/gui/container/crafting_table.png");

   public CraftingScreen(CraftingMenu var1, Inventory var2, Component var3) {
      super(var1, new CraftingRecipeBookComponent(var1), var2, var3);
   }

   protected void init() {
      super.init();
      this.titleLabelX = 29;
   }

   protected ScreenPosition getRecipeBookButtonPosition() {
      return new ScreenPosition(this.leftPos + 5, this.height / 2 - 49);
   }

   protected void renderBg(GuiGraphics var1, float var2, int var3, int var4) {
      int var5 = this.leftPos;
      int var6 = (this.height - this.imageHeight) / 2;
      var1.blit(RenderPipelines.GUI_TEXTURED, CRAFTING_TABLE_LOCATION, var5, var6, 0.0F, 0.0F, this.imageWidth, this.imageHeight, 256, 256);
   }
}
