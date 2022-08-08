package net.minecraft.client.gui.screens.recipebook;

import com.google.common.collect.Lists;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
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
   @Nullable
   private Recipe<?> recipe;
   private final List<GhostIngredient> ingredients = Lists.newArrayList();
   float time;

   public GhostRecipe() {
      super();
   }

   public void clear() {
      this.recipe = null;
      this.ingredients.clear();
      this.time = 0.0F;
   }

   public void addIngredient(Ingredient var1, int var2, int var3) {
      this.ingredients.add(new GhostIngredient(var1, var2, var3));
   }

   public GhostIngredient get(int var1) {
      return (GhostIngredient)this.ingredients.get(var1);
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

   public void render(PoseStack var1, Minecraft var2, int var3, int var4, boolean var5, float var6) {
      if (!Screen.hasControlDown()) {
         this.time += var6;
      }

      for(int var7 = 0; var7 < this.ingredients.size(); ++var7) {
         GhostIngredient var8 = (GhostIngredient)this.ingredients.get(var7);
         int var9 = var8.getX() + var3;
         int var10 = var8.getY() + var4;
         if (var7 == 0 && var5) {
            GuiComponent.fill(var1, var9 - 4, var10 - 4, var9 + 20, var10 + 20, 822018048);
         } else {
            GuiComponent.fill(var1, var9, var10, var9 + 16, var10 + 16, 822018048);
         }

         ItemStack var11 = var8.getItem();
         ItemRenderer var12 = var2.getItemRenderer();
         var12.renderAndDecorateFakeItem(var11, var9, var10);
         RenderSystem.depthFunc(516);
         GuiComponent.fill(var1, var9, var10, var9 + 16, var10 + 16, 822083583);
         RenderSystem.depthFunc(515);
         if (var7 == 0) {
            var12.renderGuiItemDecorations(var2.font, var11, var9, var10);
         }
      }

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
         return var1.length == 0 ? ItemStack.EMPTY : var1[Mth.floor(GhostRecipe.this.time / 30.0F) % var1.length];
      }
   }
}
