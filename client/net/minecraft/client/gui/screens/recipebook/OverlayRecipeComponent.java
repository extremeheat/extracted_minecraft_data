package net.minecraft.client.gui.screens.recipebook;

import com.google.common.collect.Lists;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import javax.annotation.Nullable;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.Renderable;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.recipebook.PlaceRecipeHelper;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.util.context.ContextMap;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.display.FurnaceRecipeDisplay;
import net.minecraft.world.item.crafting.display.RecipeDisplay;
import net.minecraft.world.item.crafting.display.RecipeDisplayEntry;
import net.minecraft.world.item.crafting.display.RecipeDisplayId;
import net.minecraft.world.item.crafting.display.ShapedCraftingRecipeDisplay;
import net.minecraft.world.item.crafting.display.ShapelessCraftingRecipeDisplay;
import net.minecraft.world.item.crafting.display.SlotDisplay;

public class OverlayRecipeComponent implements Renderable, GuiEventListener {
   private static final ResourceLocation OVERLAY_RECIPE_SPRITE = ResourceLocation.withDefaultNamespace("recipe_book/overlay_recipe");
   private static final int MAX_ROW = 4;
   private static final int MAX_ROW_LARGE = 5;
   private static final float ITEM_RENDER_SCALE = 0.375F;
   public static final int BUTTON_SIZE = 25;
   private final List<OverlayRecipeButton> recipeButtons = Lists.newArrayList();
   private boolean isVisible;
   private int x;
   private int y;
   private RecipeCollection collection;
   @Nullable
   private RecipeDisplayId lastRecipeClicked;
   final SlotSelectTime slotSelectTime;
   private final boolean isFurnaceMenu;

   public OverlayRecipeComponent(SlotSelectTime var1, boolean var2) {
      super();
      this.collection = RecipeCollection.EMPTY;
      this.slotSelectTime = var1;
      this.isFurnaceMenu = var2;
   }

   public void init(RecipeCollection var1, ContextMap var2, boolean var3, int var4, int var5, int var6, int var7, float var8) {
      this.collection = var1;
      List var9 = var1.getSelectedRecipes(RecipeCollection.CraftableStatus.CRAFTABLE);
      List var10 = var3 ? Collections.emptyList() : var1.getSelectedRecipes(RecipeCollection.CraftableStatus.NOT_CRAFTABLE);
      int var11 = var9.size();
      int var12 = var11 + var10.size();
      int var13 = var12 <= 16 ? 4 : 5;
      int var14 = (int)Math.ceil((double)((float)var12 / (float)var13));
      this.x = var4;
      this.y = var5;
      float var15 = (float)(this.x + Math.min(var12, var13) * 25);
      float var16 = (float)(var6 + 50);
      if (var15 > var16) {
         this.x = (int)((float)this.x - var8 * (float)((int)((var15 - var16) / var8)));
      }

      float var17 = (float)(this.y + var14 * 25);
      float var18 = (float)(var7 + 50);
      if (var17 > var18) {
         this.y = (int)((float)this.y - var8 * (float)Mth.ceil((var17 - var18) / var8));
      }

      float var19 = (float)this.y;
      float var20 = (float)(var7 - 100);
      if (var19 < var20) {
         this.y = (int)((float)this.y - var8 * (float)Mth.ceil((var19 - var20) / var8));
      }

      this.isVisible = true;
      this.recipeButtons.clear();

      for(int var21 = 0; var21 < var12; ++var21) {
         boolean var22 = var21 < var11;
         RecipeDisplayEntry var23 = var22 ? (RecipeDisplayEntry)var9.get(var21) : (RecipeDisplayEntry)var10.get(var21 - var11);
         int var24 = this.x + 4 + 25 * (var21 % var13);
         int var25 = this.y + 5 + 25 * (var21 / var13);
         if (this.isFurnaceMenu) {
            this.recipeButtons.add(new OverlaySmeltingRecipeButton(var24, var25, var23.id(), var23.display(), var2, var22));
         } else {
            this.recipeButtons.add(new OverlayCraftingRecipeButton(var24, var25, var23.id(), var23.display(), var2, var22));
         }
      }

      this.lastRecipeClicked = null;
   }

   public RecipeCollection getRecipeCollection() {
      return this.collection;
   }

   @Nullable
   public RecipeDisplayId getLastRecipeClicked() {
      return this.lastRecipeClicked;
   }

   public boolean mouseClicked(double var1, double var3, int var5) {
      if (var5 != 0) {
         return false;
      } else {
         for(OverlayRecipeButton var7 : this.recipeButtons) {
            if (var7.mouseClicked(var1, var3, var5)) {
               this.lastRecipeClicked = var7.recipe;
               return true;
            }
         }

         return false;
      }
   }

   public boolean isMouseOver(double var1, double var3) {
      return false;
   }

   public void render(GuiGraphics var1, int var2, int var3, float var4) {
      if (this.isVisible) {
         var1.pose().pushPose();
         var1.pose().translate(0.0F, 0.0F, 1000.0F);
         int var5 = this.recipeButtons.size() <= 16 ? 4 : 5;
         int var6 = Math.min(this.recipeButtons.size(), var5);
         int var7 = Mth.ceil((float)this.recipeButtons.size() / (float)var5);
         boolean var8 = true;
         var1.blitSprite(RenderType::guiTextured, OVERLAY_RECIPE_SPRITE, this.x, this.y, var6 * 25 + 8, var7 * 25 + 8);

         for(OverlayRecipeButton var10 : this.recipeButtons) {
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

   public void setFocused(boolean var1) {
   }

   public boolean isFocused() {
      return false;
   }

   class OverlaySmeltingRecipeButton extends OverlayRecipeButton {
      private static final ResourceLocation ENABLED_SPRITE = ResourceLocation.withDefaultNamespace("recipe_book/furnace_overlay");
      private static final ResourceLocation HIGHLIGHTED_ENABLED_SPRITE = ResourceLocation.withDefaultNamespace("recipe_book/furnace_overlay_highlighted");
      private static final ResourceLocation DISABLED_SPRITE = ResourceLocation.withDefaultNamespace("recipe_book/furnace_overlay_disabled");
      private static final ResourceLocation HIGHLIGHTED_DISABLED_SPRITE = ResourceLocation.withDefaultNamespace("recipe_book/furnace_overlay_disabled_highlighted");

      public OverlaySmeltingRecipeButton(final int var2, final int var3, final RecipeDisplayId var4, final RecipeDisplay var5, final ContextMap var6, final boolean var7) {
         super(var2, var3, var4, var7, calculateIngredientsPositions(var5, var6));
      }

      private static List<OverlayRecipeButton.Pos> calculateIngredientsPositions(RecipeDisplay var0, ContextMap var1) {
         if (var0 instanceof FurnaceRecipeDisplay var2) {
            List var3 = var2.ingredient().resolveForStacks(var1);
            if (!var3.isEmpty()) {
               return List.of(createGridPos(1, 1, var3));
            }
         }

         return List.of();
      }

      protected ResourceLocation getSprite(boolean var1) {
         if (var1) {
            return this.isHoveredOrFocused() ? HIGHLIGHTED_ENABLED_SPRITE : ENABLED_SPRITE;
         } else {
            return this.isHoveredOrFocused() ? HIGHLIGHTED_DISABLED_SPRITE : DISABLED_SPRITE;
         }
      }
   }

   class OverlayCraftingRecipeButton extends OverlayRecipeButton {
      private static final ResourceLocation ENABLED_SPRITE = ResourceLocation.withDefaultNamespace("recipe_book/crafting_overlay");
      private static final ResourceLocation HIGHLIGHTED_ENABLED_SPRITE = ResourceLocation.withDefaultNamespace("recipe_book/crafting_overlay_highlighted");
      private static final ResourceLocation DISABLED_SPRITE = ResourceLocation.withDefaultNamespace("recipe_book/crafting_overlay_disabled");
      private static final ResourceLocation HIGHLIGHTED_DISABLED_SPRITE = ResourceLocation.withDefaultNamespace("recipe_book/crafting_overlay_disabled_highlighted");
      private static final int GRID_WIDTH = 3;
      private static final int GRID_HEIGHT = 3;

      public OverlayCraftingRecipeButton(final int var2, final int var3, final RecipeDisplayId var4, final RecipeDisplay var5, final ContextMap var6, final boolean var7) {
         super(var2, var3, var4, var7, calculateIngredientsPositions(var5, var6));
      }

      private static List<OverlayRecipeButton.Pos> calculateIngredientsPositions(RecipeDisplay var0, ContextMap var1) {
         ArrayList var2 = new ArrayList();
         Objects.requireNonNull(var0);
         byte var4 = 0;
         //$FF: var4->value
         //0->net/minecraft/world/item/crafting/display/ShapedCraftingRecipeDisplay
         //1->net/minecraft/world/item/crafting/display/ShapelessCraftingRecipeDisplay
         switch (var0.typeSwitch<invokedynamic>(var0, var4)) {
            case 0:
               ShapedCraftingRecipeDisplay var5 = (ShapedCraftingRecipeDisplay)var0;
               PlaceRecipeHelper.placeRecipe(3, 3, var5.width(), var5.height(), var5.ingredients(), (var2x, var3, var4x, var5x) -> {
                  List var6 = var2x.resolveForStacks(var1);
                  if (!var6.isEmpty()) {
                     var2.add(createGridPos(var4x, var5x, var6));
                  }

               });
               break;
            case 1:
               ShapelessCraftingRecipeDisplay var6 = (ShapelessCraftingRecipeDisplay)var0;
               List var7 = var6.ingredients();

               for(int var8 = 0; var8 < var7.size(); ++var8) {
                  List var9 = ((SlotDisplay)var7.get(var8)).resolveForStacks(var1);
                  if (!var9.isEmpty()) {
                     var2.add(createGridPos(var8 % 3, var8 / 3, var9));
                  }
               }
         }

         return var2;
      }

      protected ResourceLocation getSprite(boolean var1) {
         if (var1) {
            return this.isHoveredOrFocused() ? HIGHLIGHTED_ENABLED_SPRITE : ENABLED_SPRITE;
         } else {
            return this.isHoveredOrFocused() ? HIGHLIGHTED_DISABLED_SPRITE : DISABLED_SPRITE;
         }
      }
   }

   abstract class OverlayRecipeButton extends AbstractWidget {
      final RecipeDisplayId recipe;
      private final boolean isCraftable;
      private final List<Pos> slots;

      public OverlayRecipeButton(final int var2, final int var3, final RecipeDisplayId var4, final boolean var5, final List<Pos> var6) {
         super(var2, var3, 24, 24, CommonComponents.EMPTY);
         this.slots = var6;
         this.recipe = var4;
         this.isCraftable = var5;
      }

      protected static Pos createGridPos(int var0, int var1, List<ItemStack> var2) {
         return new Pos(3 + var0 * 7, 3 + var1 * 7, var2);
      }

      protected abstract ResourceLocation getSprite(boolean var1);

      public void updateWidgetNarration(NarrationElementOutput var1) {
         this.defaultButtonNarrationText(var1);
      }

      public void renderWidget(GuiGraphics var1, int var2, int var3, float var4) {
         var1.blitSprite(RenderType::guiTextured, this.getSprite(this.isCraftable), this.getX(), this.getY(), this.width, this.height);
         float var5 = (float)(this.getX() + 2);
         float var6 = (float)(this.getY() + 2);
         float var7 = 150.0F;

         for(Pos var9 : this.slots) {
            var1.pose().pushPose();
            var1.pose().translate(var5 + (float)var9.x, var6 + (float)var9.y, 150.0F);
            var1.pose().scale(0.375F, 0.375F, 1.0F);
            var1.pose().translate(-8.0F, -8.0F, 0.0F);
            var1.renderItem(var9.selectIngredient(OverlayRecipeComponent.this.slotSelectTime.currentIndex()), 0, 0);
            var1.pose().popPose();
         }

      }

      protected static record Pos(int x, int y, List<ItemStack> ingredients) {
         final int x;
         final int y;

         public Pos(int var1, int var2, List<ItemStack> var3) {
            super();
            if (var3.isEmpty()) {
               throw new IllegalArgumentException("Ingredient list must be non-empty");
            } else {
               this.x = var1;
               this.y = var2;
               this.ingredients = var3;
            }
         }

         public ItemStack selectIngredient(int var1) {
            return (ItemStack)this.ingredients.get(var1 % this.ingredients.size());
         }
      }
   }
}
