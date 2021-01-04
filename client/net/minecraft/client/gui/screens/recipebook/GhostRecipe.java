package net.minecraft.client.gui.screens.recipebook;

import com.google.common.collect.Lists;
import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.platform.Lighting;
import java.util.List;
import javax.annotation.Nullable;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiComponent;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.util.Mth;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.Recipe;

public class GhostRecipe {
   private Recipe<?> recipe;
   private final List<GhostRecipe.GhostIngredient> ingredients = Lists.newArrayList();
   private float time;

   public GhostRecipe() {
      super();
   }

   public void clear() {
      this.recipe = null;
      this.ingredients.clear();
      this.time = 0.0F;
   }

   public void addIngredient(Ingredient var1, int var2, int var3) {
      this.ingredients.add(new GhostRecipe.GhostIngredient(var1, var2, var3));
   }

   public GhostRecipe.GhostIngredient get(int var1) {
      return (GhostRecipe.GhostIngredient)this.ingredients.get(var1);
   }

   public int size() {
      return this.ingredients.size();
   }

   @Nullable
   public Recipe<?> getRecipe() {
      return this.recipe;
   }

   public void setRecipe(Recipe<?> var1) {
      this.recipe = var1;
   }

   public void render(Minecraft var1, int var2, int var3, boolean var4, float var5) {
      if (!Screen.hasControlDown()) {
         this.time += var5;
      }

      Lighting.turnOnGui();
      GlStateManager.disableLighting();

      for(int var6 = 0; var6 < this.ingredients.size(); ++var6) {
         GhostRecipe.GhostIngredient var7 = (GhostRecipe.GhostIngredient)this.ingredients.get(var6);
         int var8 = var7.getX() + var2;
         int var9 = var7.getY() + var3;
         if (var6 == 0 && var4) {
            GuiComponent.fill(var8 - 4, var9 - 4, var8 + 20, var9 + 20, 822018048);
         } else {
            GuiComponent.fill(var8, var9, var8 + 16, var9 + 16, 822018048);
         }

         ItemStack var10 = var7.getItem();
         ItemRenderer var11 = var1.getItemRenderer();
         var11.renderAndDecorateItem(var1.player, var10, var8, var9);
         GlStateManager.depthFunc(516);
         GuiComponent.fill(var8, var9, var8 + 16, var9 + 16, 822083583);
         GlStateManager.depthFunc(515);
         if (var6 == 0) {
            var11.renderGuiItemDecorations(var1.font, var10, var8, var9);
         }

         GlStateManager.enableLighting();
      }

      Lighting.turnOff();
   }

   public class GhostIngredient {
      private final Ingredient ingredient;
      private final int x;
      private final int y;

      public GhostIngredient(Ingredient var2, int var3, int var4) {
         super();
         this.ingredient = var2;
         this.x = var3;
         this.y = var4;
      }

      public int getX() {
         return this.x;
      }

      public int getY() {
         return this.y;
      }

      public ItemStack getItem() {
         ItemStack[] var1 = this.ingredient.getItems();
         return var1[Mth.floor(GhostRecipe.this.time / 30.0F) % var1.length];
      }
   }
}
