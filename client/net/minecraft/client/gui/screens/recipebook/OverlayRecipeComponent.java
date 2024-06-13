package net.minecraft.client.gui.screens.recipebook;

import com.google.common.collect.Lists;
import com.mojang.blaze3d.systems.RenderSystem;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import javax.annotation.Nullable;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.Renderable;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.recipebook.PlaceRecipe;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.inventory.AbstractFurnaceMenu;
import net.minecraft.world.inventory.RecipeBookMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeHolder;

public class OverlayRecipeComponent implements Renderable, GuiEventListener {
   private static final ResourceLocation OVERLAY_RECIPE_SPRITE = new ResourceLocation("recipe_book/overlay_recipe");
   static final ResourceLocation FURNACE_OVERLAY_HIGHLIGHTED_SPRITE = new ResourceLocation("recipe_book/furnace_overlay_highlighted");
   static final ResourceLocation FURNACE_OVERLAY_SPRITE = new ResourceLocation("recipe_book/furnace_overlay");
   static final ResourceLocation CRAFTING_OVERLAY_HIGHLIGHTED_SPRITE = new ResourceLocation("recipe_book/crafting_overlay_highlighted");
   static final ResourceLocation CRAFTING_OVERLAY_SPRITE = new ResourceLocation("recipe_book/crafting_overlay");
   static final ResourceLocation FURNACE_OVERLAY_DISABLED_HIGHLIGHTED_SPRITE = new ResourceLocation("recipe_book/furnace_overlay_disabled_highlighted");
   static final ResourceLocation FURNACE_OVERLAY_DISABLED_SPRITE = new ResourceLocation("recipe_book/furnace_overlay_disabled");
   static final ResourceLocation CRAFTING_OVERLAY_DISABLED_HIGHLIGHTED_SPRITE = new ResourceLocation("recipe_book/crafting_overlay_disabled_highlighted");
   static final ResourceLocation CRAFTING_OVERLAY_DISABLED_SPRITE = new ResourceLocation("recipe_book/crafting_overlay_disabled");
   private static final int MAX_ROW = 4;
   private static final int MAX_ROW_LARGE = 5;
   private static final float ITEM_RENDER_SCALE = 0.375F;
   public static final int BUTTON_SIZE = 25;
   private final List<OverlayRecipeComponent.OverlayRecipeButton> recipeButtons = Lists.newArrayList();
   private boolean isVisible;
   private int x;
   private int y;
   private Minecraft minecraft;
   private RecipeCollection collection;
   @Nullable
   private RecipeHolder<?> lastRecipeClicked;
   float time;
   boolean isFurnaceMenu;

   public OverlayRecipeComponent() {
      super();
   }

   public void init(Minecraft var1, RecipeCollection var2, int var3, int var4, int var5, int var6, float var7) {
      this.minecraft = var1;
      this.collection = var2;
      if (var1.player.containerMenu instanceof AbstractFurnaceMenu) {
         this.isFurnaceMenu = true;
      }

      boolean var8 = var1.player.getRecipeBook().isFiltering((RecipeBookMenu<?>)var1.player.containerMenu);
      List var9 = var2.getDisplayRecipes(true);
      List var10 = var8 ? Collections.emptyList() : var2.getDisplayRecipes(false);
      int var11 = var9.size();
      int var12 = var11 + var10.size();
      int var13 = var12 <= 16 ? 4 : 5;
      int var14 = (int)Math.ceil((double)((float)var12 / (float)var13));
      this.x = var3;
      this.y = var4;
      float var15 = (float)(this.x + Math.min(var12, var13) * 25);
      float var16 = (float)(var5 + 50);
      if (var15 > var16) {
         this.x = (int)((float)this.x - var7 * (float)((int)((var15 - var16) / var7)));
      }

      float var17 = (float)(this.y + var14 * 25);
      float var18 = (float)(var6 + 50);
      if (var17 > var18) {
         this.y = (int)((float)this.y - var7 * (float)Mth.ceil((var17 - var18) / var7));
      }

      float var19 = (float)this.y;
      float var20 = (float)(var6 - 100);
      if (var19 < var20) {
         this.y = (int)((float)this.y - var7 * (float)Mth.ceil((var19 - var20) / var7));
      }

      this.isVisible = true;
      this.recipeButtons.clear();

      for (int var21 = 0; var21 < var12; var21++) {
         boolean var22 = var21 < var11;
         RecipeHolder var23 = var22 ? (RecipeHolder)var9.get(var21) : (RecipeHolder)var10.get(var21 - var11);
         int var24 = this.x + 4 + 25 * (var21 % var13);
         int var25 = this.y + 5 + 25 * (var21 / var13);
         if (this.isFurnaceMenu) {
            this.recipeButtons.add(new OverlayRecipeComponent.OverlaySmeltingRecipeButton(var24, var25, var23, var22));
         } else {
            this.recipeButtons.add(new OverlayRecipeComponent.OverlayRecipeButton(var24, var25, var23, var22));
         }
      }

      this.lastRecipeClicked = null;
   }

   public RecipeCollection getRecipeCollection() {
      return this.collection;
   }

   @Nullable
   public RecipeHolder<?> getLastRecipeClicked() {
      return this.lastRecipeClicked;
   }

   @Override
   public boolean mouseClicked(double var1, double var3, int var5) {
      if (var5 != 0) {
         return false;
      } else {
         for (OverlayRecipeComponent.OverlayRecipeButton var7 : this.recipeButtons) {
            if (var7.mouseClicked(var1, var3, var5)) {
               this.lastRecipeClicked = var7.recipe;
               return true;
            }
         }

         return false;
      }
   }

   @Override
   public boolean isMouseOver(double var1, double var3) {
      return false;
   }

   @Override
   public void render(GuiGraphics var1, int var2, int var3, float var4) {
      if (this.isVisible) {
         this.time += var4;
         RenderSystem.enableBlend();
         var1.pose().pushPose();
         var1.pose().translate(0.0F, 0.0F, 1000.0F);
         int var5 = this.recipeButtons.size() <= 16 ? 4 : 5;
         int var6 = Math.min(this.recipeButtons.size(), var5);
         int var7 = Mth.ceil((float)this.recipeButtons.size() / (float)var5);
         byte var8 = 4;
         var1.blitSprite(OVERLAY_RECIPE_SPRITE, this.x, this.y, var6 * 25 + 8, var7 * 25 + 8);
         RenderSystem.disableBlend();

         for (OverlayRecipeComponent.OverlayRecipeButton var10 : this.recipeButtons) {
            var10.render(var1, var2, var3, var4);
         }

         var1.pose().popPose();
      }
   }

   public void setVisible(boolean var1) {
      this.isVisible = var1;
   }

   public boolean isVisible() {
      return this.isVisible;
   }

   @Override
   public void setFocused(boolean var1) {
   }

   @Override
   public boolean isFocused() {
      return false;
   }

   class OverlayRecipeButton extends AbstractWidget implements PlaceRecipe<Ingredient> {
      final RecipeHolder<?> recipe;
      private final boolean isCraftable;
      protected final List<OverlayRecipeComponent.OverlayRecipeButton.Pos> ingredientPos = Lists.newArrayList();

      public OverlayRecipeButton(final int nullx, final int nullxx, final RecipeHolder<?> nullxxx, final boolean nullxxxx) {
         super(nullx, nullxx, 200, 20, CommonComponents.EMPTY);
         this.width = 24;
         this.height = 24;
         this.recipe = nullxxx;
         this.isCraftable = nullxxxx;
         this.calculateIngredientsPositions(nullxxx);
      }

      protected void calculateIngredientsPositions(RecipeHolder<?> var1) {
         this.placeRecipe(3, 3, -1, var1, var1.value().getIngredients().iterator(), 0);
      }

      @Override
      public void updateWidgetNarration(NarrationElementOutput var1) {
         this.defaultButtonNarrationText(var1);
      }

      @Override
      public void addItemToSlot(Iterator<Ingredient> var1, int var2, int var3, int var4, int var5) {
         ItemStack[] var6 = ((Ingredient)var1.next()).getItems();
         if (var6.length != 0) {
            this.ingredientPos.add(new OverlayRecipeComponent.OverlayRecipeButton.Pos(3 + var5 * 7, 3 + var4 * 7, var6));
         }
      }

      @Override
      public void renderWidget(GuiGraphics var1, int var2, int var3, float var4) {
         ResourceLocation var5;
         if (this.isCraftable) {
            if (OverlayRecipeComponent.this.isFurnaceMenu) {
               var5 = this.isHoveredOrFocused() ? OverlayRecipeComponent.FURNACE_OVERLAY_HIGHLIGHTED_SPRITE : OverlayRecipeComponent.FURNACE_OVERLAY_SPRITE;
            } else {
               var5 = this.isHoveredOrFocused() ? OverlayRecipeComponent.CRAFTING_OVERLAY_HIGHLIGHTED_SPRITE : OverlayRecipeComponent.CRAFTING_OVERLAY_SPRITE;
            }
         } else if (OverlayRecipeComponent.this.isFurnaceMenu) {
            var5 = this.isHoveredOrFocused()
               ? OverlayRecipeComponent.FURNACE_OVERLAY_DISABLED_HIGHLIGHTED_SPRITE
               : OverlayRecipeComponent.FURNACE_OVERLAY_DISABLED_SPRITE;
         } else {
            var5 = this.isHoveredOrFocused()
               ? OverlayRecipeComponent.CRAFTING_OVERLAY_DISABLED_HIGHLIGHTED_SPRITE
               : OverlayRecipeComponent.CRAFTING_OVERLAY_DISABLED_SPRITE;
         }

         var1.blitSprite(var5, this.getX(), this.getY(), this.width, this.height);
         var1.pose().pushPose();
         var1.pose().translate((double)(this.getX() + 2), (double)(this.getY() + 2), 150.0);

         for (OverlayRecipeComponent.OverlayRecipeButton.Pos var7 : this.ingredientPos) {
            var1.pose().pushPose();
            var1.pose().translate((double)var7.x, (double)var7.y, 0.0);
            var1.pose().scale(0.375F, 0.375F, 1.0F);
            var1.pose().translate(-8.0, -8.0, 0.0);
            if (var7.ingredients.length > 0) {
               var1.renderItem(var7.ingredients[Mth.floor(OverlayRecipeComponent.this.time / 30.0F) % var7.ingredients.length], 0, 0);
            }

            var1.pose().popPose();
         }

         var1.pose().popPose();
      }

      protected class Pos {
         public final ItemStack[] ingredients;
         public final int x;
         public final int y;

         public Pos(final int nullx, final int nullxx, final ItemStack[] nullxxx) {
            super();
            this.x = nullx;
            this.y = nullxx;
            this.ingredients = nullxxx;
         }
      }
   }

   class OverlaySmeltingRecipeButton extends OverlayRecipeComponent.OverlayRecipeButton {
      public OverlaySmeltingRecipeButton(final int nullx, final int nullxx, final RecipeHolder<?> nullxxx, final boolean nullxxxx) {
         super(nullx, nullxx, nullxxx, nullxxxx);
      }

      @Override
      protected void calculateIngredientsPositions(RecipeHolder<?> var1) {
         Ingredient var2 = var1.value().getIngredients().get(0);
         ItemStack[] var3 = var2.getItems();
         this.ingredientPos.add(new OverlayRecipeComponent.OverlayRecipeButton.Pos(10, 10, var3));
      }
   }
}