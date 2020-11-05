package net.minecraft.client.gui.screens.recipebook;

import com.google.common.collect.Lists;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiComponent;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.Widget;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.recipebook.PlaceRecipe;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.inventory.AbstractFurnaceMenu;
import net.minecraft.world.inventory.RecipeBookMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.Recipe;

public class OverlayRecipeComponent extends GuiComponent implements Widget, GuiEventListener {
   private static final ResourceLocation RECIPE_BOOK_LOCATION = new ResourceLocation("textures/gui/recipe_book.png");
   private final List<OverlayRecipeComponent.OverlayRecipeButton> recipeButtons = Lists.newArrayList();
   private boolean isVisible;
   private int x;
   private int y;
   private Minecraft minecraft;
   private RecipeCollection collection;
   private Recipe<?> lastRecipeClicked;
   private float time;
   private boolean isFurnaceMenu;

   public OverlayRecipeComponent() {
      super();
   }

   public void init(Minecraft var1, RecipeCollection var2, int var3, int var4, int var5, int var6, float var7) {
      this.minecraft = var1;
      this.collection = var2;
      if (var1.player.containerMenu instanceof AbstractFurnaceMenu) {
         this.isFurnaceMenu = true;
      }

      boolean var8 = var1.player.getRecipeBook().isFiltering((RecipeBookMenu)var1.player.containerMenu);
      List var9 = var2.getDisplayRecipes(true);
      List var10 = var8 ? Collections.emptyList() : var2.getDisplayRecipes(false);
      int var11 = var9.size();
      int var12 = var11 + var10.size();
      int var13 = var12 <= 16 ? 4 : 5;
      int var14 = (int)Math.ceil((double)((float)var12 / (float)var13));
      this.x = var3;
      this.y = var4;
      boolean var15 = true;
      float var16 = (float)(this.x + Math.min(var12, var13) * 25);
      float var17 = (float)(var5 + 50);
      if (var16 > var17) {
         this.x = (int)((float)this.x - var7 * (float)((int)((var16 - var17) / var7)));
      }

      float var18 = (float)(this.y + var14 * 25);
      float var19 = (float)(var6 + 50);
      if (var18 > var19) {
         this.y = (int)((float)this.y - var7 * (float)Mth.ceil((var18 - var19) / var7));
      }

      float var20 = (float)this.y;
      float var21 = (float)(var6 - 100);
      if (var20 < var21) {
         this.y = (int)((float)this.y - var7 * (float)Mth.ceil((var20 - var21) / var7));
      }

      this.isVisible = true;
      this.recipeButtons.clear();

      for(int var22 = 0; var22 < var12; ++var22) {
         boolean var23 = var22 < var11;
         Recipe var24 = var23 ? (Recipe)var9.get(var22) : (Recipe)var10.get(var22 - var11);
         int var25 = this.x + 4 + 25 * (var22 % var13);
         int var26 = this.y + 5 + 25 * (var22 / var13);
         if (this.isFurnaceMenu) {
            this.recipeButtons.add(new OverlayRecipeComponent.OverlaySmeltingRecipeButton(var25, var26, var24, var23));
         } else {
            this.recipeButtons.add(new OverlayRecipeComponent.OverlayRecipeButton(var25, var26, var24, var23));
         }
      }

      this.lastRecipeClicked = null;
   }

   public boolean changeFocus(boolean var1) {
      return false;
   }

   public RecipeCollection getRecipeCollection() {
      return this.collection;
   }

   public Recipe<?> getLastRecipeClicked() {
      return this.lastRecipeClicked;
   }

   public boolean mouseClicked(double var1, double var3, int var5) {
      if (var5 != 0) {
         return false;
      } else {
         Iterator var6 = this.recipeButtons.iterator();

         OverlayRecipeComponent.OverlayRecipeButton var7;
         do {
            if (!var6.hasNext()) {
               return false;
            }

            var7 = (OverlayRecipeComponent.OverlayRecipeButton)var6.next();
         } while(!var7.mouseClicked(var1, var3, var5));

         this.lastRecipeClicked = var7.recipe;
         return true;
      }
   }

   public boolean isMouseOver(double var1, double var3) {
      return false;
   }

   public void render(PoseStack var1, int var2, int var3, float var4) {
      if (this.isVisible) {
         this.time += var4;
         RenderSystem.enableBlend();
         RenderSystem.color4f(1.0F, 1.0F, 1.0F, 1.0F);
         this.minecraft.getTextureManager().bind(RECIPE_BOOK_LOCATION);
         RenderSystem.pushMatrix();
         RenderSystem.translatef(0.0F, 0.0F, 170.0F);
         int var5 = this.recipeButtons.size() <= 16 ? 4 : 5;
         int var6 = Math.min(this.recipeButtons.size(), var5);
         int var7 = Mth.ceil((float)this.recipeButtons.size() / (float)var5);
         boolean var8 = true;
         boolean var9 = true;
         boolean var10 = true;
         boolean var11 = true;
         this.nineInchSprite(var1, var6, var7, 24, 4, 82, 208);
         RenderSystem.disableBlend();
         Iterator var12 = this.recipeButtons.iterator();

         while(var12.hasNext()) {
            OverlayRecipeComponent.OverlayRecipeButton var13 = (OverlayRecipeComponent.OverlayRecipeButton)var12.next();
            var13.render(var1, var2, var3, var4);
         }

         RenderSystem.popMatrix();
      }
   }

   private void nineInchSprite(PoseStack var1, int var2, int var3, int var4, int var5, int var6, int var7) {
      this.blit(var1, this.x, this.y, var6, var7, var5, var5);
      this.blit(var1, this.x + var5 * 2 + var2 * var4, this.y, var6 + var4 + var5, var7, var5, var5);
      this.blit(var1, this.x, this.y + var5 * 2 + var3 * var4, var6, var7 + var4 + var5, var5, var5);
      this.blit(var1, this.x + var5 * 2 + var2 * var4, this.y + var5 * 2 + var3 * var4, var6 + var4 + var5, var7 + var4 + var5, var5, var5);

      for(int var8 = 0; var8 < var2; ++var8) {
         this.blit(var1, this.x + var5 + var8 * var4, this.y, var6 + var5, var7, var4, var5);
         this.blit(var1, this.x + var5 + (var8 + 1) * var4, this.y, var6 + var5, var7, var5, var5);

         for(int var9 = 0; var9 < var3; ++var9) {
            if (var8 == 0) {
               this.blit(var1, this.x, this.y + var5 + var9 * var4, var6, var7 + var5, var5, var4);
               this.blit(var1, this.x, this.y + var5 + (var9 + 1) * var4, var6, var7 + var5, var5, var5);
            }

            this.blit(var1, this.x + var5 + var8 * var4, this.y + var5 + var9 * var4, var6 + var5, var7 + var5, var4, var4);
            this.blit(var1, this.x + var5 + (var8 + 1) * var4, this.y + var5 + var9 * var4, var6 + var5, var7 + var5, var5, var4);
            this.blit(var1, this.x + var5 + var8 * var4, this.y + var5 + (var9 + 1) * var4, var6 + var5, var7 + var5, var4, var5);
            this.blit(var1, this.x + var5 + (var8 + 1) * var4 - 1, this.y + var5 + (var9 + 1) * var4 - 1, var6 + var5, var7 + var5, var5 + 1, var5 + 1);
            if (var8 == var2 - 1) {
               this.blit(var1, this.x + var5 * 2 + var2 * var4, this.y + var5 + var9 * var4, var6 + var4 + var5, var7 + var5, var5, var4);
               this.blit(var1, this.x + var5 * 2 + var2 * var4, this.y + var5 + (var9 + 1) * var4, var6 + var4 + var5, var7 + var5, var5, var5);
            }
         }

         this.blit(var1, this.x + var5 + var8 * var4, this.y + var5 * 2 + var3 * var4, var6 + var5, var7 + var4 + var5, var4, var5);
         this.blit(var1, this.x + var5 + (var8 + 1) * var4, this.y + var5 * 2 + var3 * var4, var6 + var5, var7 + var4 + var5, var5, var5);
      }

   }

   public void setVisible(boolean var1) {
      this.isVisible = var1;
   }

   public boolean isVisible() {
      return this.isVisible;
   }

   class OverlayRecipeButton extends AbstractWidget implements PlaceRecipe<Ingredient> {
      private final Recipe<?> recipe;
      private final boolean isCraftable;
      protected final List<OverlayRecipeComponent.OverlayRecipeButton.Pos> ingredientPos = Lists.newArrayList();

      public OverlayRecipeButton(int var2, int var3, Recipe<?> var4, boolean var5) {
         super(var2, var3, 200, 20, TextComponent.EMPTY);
         this.width = 24;
         this.height = 24;
         this.recipe = var4;
         this.isCraftable = var5;
         this.calculateIngredientsPositions(var4);
      }

      protected void calculateIngredientsPositions(Recipe<?> var1) {
         this.placeRecipe(3, 3, -1, var1, var1.getIngredients().iterator(), 0);
      }

      public void addItemToSlot(Iterator<Ingredient> var1, int var2, int var3, int var4, int var5) {
         ItemStack[] var6 = ((Ingredient)var1.next()).getItems();
         if (var6.length != 0) {
            this.ingredientPos.add(new OverlayRecipeComponent.OverlayRecipeButton.Pos(3 + var5 * 7, 3 + var4 * 7, var6));
         }

      }

      public void renderButton(PoseStack var1, int var2, int var3, float var4) {
         RenderSystem.enableAlphaTest();
         OverlayRecipeComponent.this.minecraft.getTextureManager().bind(OverlayRecipeComponent.RECIPE_BOOK_LOCATION);
         int var5 = 152;
         if (!this.isCraftable) {
            var5 += 26;
         }

         int var6 = OverlayRecipeComponent.this.isFurnaceMenu ? 130 : 78;
         if (this.isHovered()) {
            var6 += 26;
         }

         this.blit(var1, this.x, this.y, var5, var6, this.width, this.height);
         Iterator var7 = this.ingredientPos.iterator();

         while(var7.hasNext()) {
            OverlayRecipeComponent.OverlayRecipeButton.Pos var8 = (OverlayRecipeComponent.OverlayRecipeButton.Pos)var7.next();
            RenderSystem.pushMatrix();
            float var9 = 0.42F;
            int var10 = (int)((float)(this.x + var8.x) / 0.42F - 3.0F);
            int var11 = (int)((float)(this.y + var8.y) / 0.42F - 3.0F);
            RenderSystem.scalef(0.42F, 0.42F, 1.0F);
            OverlayRecipeComponent.this.minecraft.getItemRenderer().renderAndDecorateItem(var8.ingredients[Mth.floor(OverlayRecipeComponent.this.time / 30.0F) % var8.ingredients.length], var10, var11);
            RenderSystem.popMatrix();
         }

         RenderSystem.disableAlphaTest();
      }

      public class Pos {
         public final ItemStack[] ingredients;
         public final int x;
         public final int y;

         public Pos(int var2, int var3, ItemStack[] var4) {
            super();
            this.x = var2;
            this.y = var3;
            this.ingredients = var4;
         }
      }
   }

   class OverlaySmeltingRecipeButton extends OverlayRecipeComponent.OverlayRecipeButton {
      public OverlaySmeltingRecipeButton(int var2, int var3, Recipe<?> var4, boolean var5) {
         super(var2, var3, var4, var5);
      }

      protected void calculateIngredientsPositions(Recipe<?> var1) {
         ItemStack[] var2 = ((Ingredient)var1.getIngredients().get(0)).getItems();
         this.ingredientPos.add(new OverlayRecipeComponent.OverlayRecipeButton.Pos(10, 10, var2));
      }
   }
}
