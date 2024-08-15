package net.minecraft.client.gui.screens.recipebook;

import com.google.common.collect.Lists;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
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
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeHolder;

public class OverlayRecipeComponent implements Renderable, GuiEventListener {
   private static final ResourceLocation OVERLAY_RECIPE_SPRITE = ResourceLocation.withDefaultNamespace("recipe_book/overlay_recipe");
   private static final int MAX_ROW = 4;
   private static final int MAX_ROW_LARGE = 5;
   private static final float ITEM_RENDER_SCALE = 0.375F;
   public static final int BUTTON_SIZE = 25;
   private final List<OverlayRecipeComponent.OverlayRecipeButton> recipeButtons = Lists.newArrayList();
   private boolean isVisible;
   private int x;
   private int y;
   private RecipeCollection collection;
   @Nullable
   private RecipeHolder<?> lastRecipeClicked;
   final SlotSelectTime slotSelectTime;
   private final boolean isFurnaceMenu;

   public OverlayRecipeComponent(SlotSelectTime var1, boolean var2) {
      super();
      this.slotSelectTime = var1;
      this.isFurnaceMenu = var2;
   }

   public void init(RecipeCollection var1, boolean var2, int var3, int var4, int var5, int var6, float var7) {
      this.collection = var1;
      List var8 = var1.getFittingRecipes(RecipeCollection.CraftableStatus.CRAFTABLE);
      List var9 = var2 ? Collections.emptyList() : var1.getFittingRecipes(RecipeCollection.CraftableStatus.NOT_CRAFTABLE);
      int var10 = var8.size();
      int var11 = var10 + var9.size();
      int var12 = var11 <= 16 ? 4 : 5;
      int var13 = (int)Math.ceil((double)((float)var11 / (float)var12));
      this.x = var3;
      this.y = var4;
      float var14 = (float)(this.x + Math.min(var11, var12) * 25);
      float var15 = (float)(var5 + 50);
      if (var14 > var15) {
         this.x = (int)((float)this.x - var7 * (float)((int)((var14 - var15) / var7)));
      }

      float var16 = (float)(this.y + var13 * 25);
      float var17 = (float)(var6 + 50);
      if (var16 > var17) {
         this.y = (int)((float)this.y - var7 * (float)Mth.ceil((var16 - var17) / var7));
      }

      float var18 = (float)this.y;
      float var19 = (float)(var6 - 100);
      if (var18 < var19) {
         this.y = (int)((float)this.y - var7 * (float)Mth.ceil((var18 - var19) / var7));
      }

      this.isVisible = true;
      this.recipeButtons.clear();

      for (int var20 = 0; var20 < var11; var20++) {
         boolean var21 = var20 < var10;
         RecipeHolder var22 = var21 ? (RecipeHolder)var8.get(var20) : (RecipeHolder)var9.get(var20 - var10);
         int var23 = this.x + 4 + 25 * (var20 % var12);
         int var24 = this.y + 5 + 25 * (var20 / var12);
         if (this.isFurnaceMenu) {
            this.recipeButtons.add(new OverlayRecipeComponent.OverlaySmeltingRecipeButton(var23, var24, var22, var21));
         } else {
            this.recipeButtons.add(new OverlayRecipeComponent.OverlayCraftingRecipeButton(var23, var24, var22, var21));
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
         var1.pose().pushPose();
         var1.pose().translate(0.0F, 0.0F, 1000.0F);
         int var5 = this.recipeButtons.size() <= 16 ? 4 : 5;
         int var6 = Math.min(this.recipeButtons.size(), var5);
         int var7 = Mth.ceil((float)this.recipeButtons.size() / (float)var5);
         byte var8 = 4;
         var1.blitSprite(RenderType::guiTextured, OVERLAY_RECIPE_SPRITE, this.x, this.y, var6 * 25 + 8, var7 * 25 + 8);

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

   class OverlayCraftingRecipeButton extends OverlayRecipeComponent.OverlayRecipeButton {
      private static final ResourceLocation ENABLED_SPRITE = ResourceLocation.withDefaultNamespace("recipe_book/crafting_overlay");
      private static final ResourceLocation HIGHLIGHTED_ENABLED_SPRITE = ResourceLocation.withDefaultNamespace("recipe_book/crafting_overlay_highlighted");
      private static final ResourceLocation DISABLED_SPRITE = ResourceLocation.withDefaultNamespace("recipe_book/crafting_overlay_disabled");
      private static final ResourceLocation HIGHLIGHTED_DISABLED_SPRITE = ResourceLocation.withDefaultNamespace(
         "recipe_book/crafting_overlay_disabled_highlighted"
      );

      public OverlayCraftingRecipeButton(final int nullx, final int nullxx, final RecipeHolder<?> nullxxx, final boolean nullxxxx) {
         super(nullx, nullxx, nullxxx, nullxxxx, calculateIngredientsPositions(nullxxx));
      }

      private static List<OverlayRecipeComponent.OverlayRecipeButton.Pos> calculateIngredientsPositions(RecipeHolder<?> var0) {
         ArrayList var1 = new ArrayList();
         PlaceRecipeHelper.placeRecipe(
            3,
            3,
            var0,
            var0.value().placementInfo().slotInfo(),
            (var1x, var2, var3, var4) -> var1x.ifPresent(var3x -> var1.add(createGridPos(var3, var4, var3x.possibleItems())))
         );
         return var1;
      }

      @Override
      protected ResourceLocation getSprite(boolean var1) {
         if (var1) {
            return this.isHoveredOrFocused() ? HIGHLIGHTED_ENABLED_SPRITE : ENABLED_SPRITE;
         } else {
            return this.isHoveredOrFocused() ? HIGHLIGHTED_DISABLED_SPRITE : DISABLED_SPRITE;
         }
      }
   }

   abstract class OverlayRecipeButton extends AbstractWidget {
      final RecipeHolder<?> recipe;
      private final boolean isCraftable;
      private final List<OverlayRecipeComponent.OverlayRecipeButton.Pos> slots;

      public OverlayRecipeButton(
         final int nullx,
         final int nullxx,
         final RecipeHolder<?> nullxxx,
         final boolean nullxxxx,
         final List<OverlayRecipeComponent.OverlayRecipeButton.Pos> nullxxxxx
      ) {
         super(nullx, nullxx, 24, 24, CommonComponents.EMPTY);
         this.slots = nullxxxxx;
         this.recipe = nullxxx;
         this.isCraftable = nullxxxx;
      }

      protected static OverlayRecipeComponent.OverlayRecipeButton.Pos createGridPos(int var0, int var1, List<ItemStack> var2) {
         return new OverlayRecipeComponent.OverlayRecipeButton.Pos(3 + var0 * 7, 3 + var1 * 7, var2);
      }

      protected abstract ResourceLocation getSprite(boolean var1);

      @Override
      public void updateWidgetNarration(NarrationElementOutput var1) {
         this.defaultButtonNarrationText(var1);
      }

      @Override
      public void renderWidget(GuiGraphics var1, int var2, int var3, float var4) {
         var1.blitSprite(RenderType::guiTextured, this.getSprite(this.isCraftable), this.getX(), this.getY(), this.width, this.height);
         float var5 = (float)(this.getX() + 2);
         float var6 = (float)(this.getY() + 2);
         float var7 = 150.0F;

         for (OverlayRecipeComponent.OverlayRecipeButton.Pos var9 : this.slots) {
            var1.pose().pushPose();
            var1.pose().translate(var5 + (float)var9.x, var6 + (float)var9.y, 150.0F);
            var1.pose().scale(0.375F, 0.375F, 1.0F);
            var1.pose().translate(-8.0F, -8.0F, 0.0F);
            var1.renderItem(var9.selectIngredient(OverlayRecipeComponent.this.slotSelectTime.currentIndex()), 0, 0);
            var1.pose().popPose();
         }
      }

      protected static record Pos(int x, int y, List<ItemStack> ingredients) {

         public Pos(int x, int y, List<ItemStack> ingredients) {
            super();
            if (ingredients.isEmpty()) {
               throw new IllegalArgumentException("Ingredient list must be non-empty");
            } else {
               this.x = x;
               this.y = y;
               this.ingredients = ingredients;
            }
         }

         public ItemStack selectIngredient(int var1) {
            return this.ingredients.get(var1 % this.ingredients.size());
         }
      }
   }

   class OverlaySmeltingRecipeButton extends OverlayRecipeComponent.OverlayRecipeButton {
      private static final ResourceLocation ENABLED_SPRITE = ResourceLocation.withDefaultNamespace("recipe_book/furnace_overlay");
      private static final ResourceLocation HIGHLIGHTED_ENABLED_SPRITE = ResourceLocation.withDefaultNamespace("recipe_book/furnace_overlay_highlighted");
      private static final ResourceLocation DISABLED_SPRITE = ResourceLocation.withDefaultNamespace("recipe_book/furnace_overlay_disabled");
      private static final ResourceLocation HIGHLIGHTED_DISABLED_SPRITE = ResourceLocation.withDefaultNamespace(
         "recipe_book/furnace_overlay_disabled_highlighted"
      );

      public OverlaySmeltingRecipeButton(final int nullx, final int nullxx, final RecipeHolder<?> nullxxx, final boolean nullxxxx) {
         super(nullx, nullxx, nullxxx, nullxxxx, calculateIngredientsPositions(nullxxx));
      }

      private static List<OverlayRecipeComponent.OverlayRecipeButton.Pos> calculateIngredientsPositions(RecipeHolder<?> var0) {
         return ((Optional)var0.value().placementInfo().slotInfo().getFirst())
            .<List<OverlayRecipeComponent.OverlayRecipeButton.Pos>>map(var0x -> List.of(createGridPos(1, 1, var0x.possibleItems())))
            .orElse(List.of());
      }

      @Override
      protected ResourceLocation getSprite(boolean var1) {
         if (var1) {
            return this.isHoveredOrFocused() ? HIGHLIGHTED_ENABLED_SPRITE : ENABLED_SPRITE;
         } else {
            return this.isHoveredOrFocused() ? HIGHLIGHTED_DISABLED_SPRITE : DISABLED_SPRITE;
         }
      }
   }
}
