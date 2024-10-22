package net.minecraft.client.gui.screens.inventory;

import java.util.List;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.navigation.ScreenPosition;
import net.minecraft.client.gui.screens.recipebook.FurnaceRecipeBookComponent;
import net.minecraft.client.gui.screens.recipebook.RecipeBookComponent;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.AbstractFurnaceMenu;

public abstract class AbstractFurnaceScreen<T extends AbstractFurnaceMenu> extends AbstractRecipeBookScreen<T> {
   private final ResourceLocation texture;
   private final ResourceLocation litProgressSprite;
   private final ResourceLocation burnProgressSprite;

   public AbstractFurnaceScreen(
      T var1,
      Inventory var2,
      Component var3,
      Component var4,
      ResourceLocation var5,
      ResourceLocation var6,
      ResourceLocation var7,
      List<RecipeBookComponent.TabInfo> var8
   ) {
      super((T)var1, new FurnaceRecipeBookComponent(var1, var4, var8), var2, var3);
      this.texture = var5;
      this.litProgressSprite = var6;
      this.burnProgressSprite = var7;
   }

   @Override
   public void init() {
      super.init();
      this.titleLabelX = (this.imageWidth - this.font.width(this.title)) / 2;
   }

   @Override
   protected ScreenPosition getRecipeBookButtonPosition() {
      return new ScreenPosition(this.leftPos + 20, this.height / 2 - 49);
   }

   @Override
   protected void renderBg(GuiGraphics var1, float var2, int var3, int var4) {
      int var5 = this.leftPos;
      int var6 = this.topPos;
      var1.blit(RenderType::guiTextured, this.texture, var5, var6, 0.0F, 0.0F, this.imageWidth, this.imageHeight, 256, 256);
      if (this.menu.isLit()) {
         byte var7 = 14;
         int var8 = Mth.ceil(this.menu.getLitProgress() * 13.0F) + 1;
         var1.blitSprite(RenderType::guiTextured, this.litProgressSprite, 14, 14, 0, 14 - var8, var5 + 56, var6 + 36 + 14 - var8, 14, var8);
      }

      byte var9 = 24;
      int var10 = Mth.ceil(this.menu.getBurnProgress() * 24.0F);
      var1.blitSprite(RenderType::guiTextured, this.burnProgressSprite, 24, 16, 0, 0, var5 + 79, var6 + 34, var10, 16);
   }
}
