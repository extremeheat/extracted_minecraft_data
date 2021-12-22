package net.minecraft.client.gui.screens.recipebook;

import com.google.common.collect.Lists;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.narration.NarratedElementType;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.stats.RecipeBook;
import net.minecraft.util.Mth;
import net.minecraft.world.inventory.RecipeBookMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Recipe;

public class RecipeButton extends AbstractWidget {
   private static final ResourceLocation RECIPE_BOOK_LOCATION = new ResourceLocation("textures/gui/recipe_book.png");
   private static final float ANIMATION_TIME = 15.0F;
   private static final int BACKGROUND_SIZE = 25;
   public static final int TICKS_TO_SWAP = 30;
   private static final Component MORE_RECIPES_TOOLTIP = new TranslatableComponent("gui.recipebook.moreRecipes");
   private RecipeBookMenu<?> menu;
   private RecipeBook book;
   private RecipeCollection collection;
   private float time;
   private float animationTime;
   private int currentIndex;

   public RecipeButton() {
      super(0, 0, 25, 25, TextComponent.EMPTY);
   }

   public void init(RecipeCollection var1, RecipeBookPage var2) {
      this.collection = var1;
      this.menu = (RecipeBookMenu)var2.getMinecraft().player.containerMenu;
      this.book = var2.getRecipeBook();
      List var3 = var1.getRecipes(this.book.isFiltering(this.menu));
      Iterator var4 = var3.iterator();

      while(var4.hasNext()) {
         Recipe var5 = (Recipe)var4.next();
         if (this.book.willHighlight(var5)) {
            var2.recipesShown(var3);
            this.animationTime = 15.0F;
            break;
         }
      }

   }

   public RecipeCollection getCollection() {
      return this.collection;
   }

   public void setPosition(int var1, int var2) {
      this.x = var1;
      this.y = var2;
   }

   public void renderButton(PoseStack var1, int var2, int var3, float var4) {
      if (!Screen.hasControlDown()) {
         this.time += var4;
      }

      Minecraft var5 = Minecraft.getInstance();
      RenderSystem.setShader(GameRenderer::getPositionTexShader);
      RenderSystem.setShaderTexture(0, RECIPE_BOOK_LOCATION);
      int var6 = 29;
      if (!this.collection.hasCraftable()) {
         var6 += 25;
      }

      int var7 = 206;
      if (this.collection.getRecipes(this.book.isFiltering(this.menu)).size() > 1) {
         var7 += 25;
      }

      boolean var8 = this.animationTime > 0.0F;
      PoseStack var9 = RenderSystem.getModelViewStack();
      if (var8) {
         float var10 = 1.0F + 0.1F * (float)Math.sin((double)(this.animationTime / 15.0F * 3.1415927F));
         var9.pushPose();
         var9.translate((double)(this.x + 8), (double)(this.y + 12), 0.0D);
         var9.scale(var10, var10, 1.0F);
         var9.translate((double)(-(this.x + 8)), (double)(-(this.y + 12)), 0.0D);
         RenderSystem.applyModelViewMatrix();
         this.animationTime -= var4;
      }

      this.blit(var1, this.x, this.y, var6, var7, this.width, this.height);
      List var13 = this.getOrderedRecipes();
      this.currentIndex = Mth.floor(this.time / 30.0F) % var13.size();
      ItemStack var11 = ((Recipe)var13.get(this.currentIndex)).getResultItem();
      int var12 = 4;
      if (this.collection.hasSingleResultItem() && this.getOrderedRecipes().size() > 1) {
         var5.getItemRenderer().renderAndDecorateItem(var11, this.x + var12 + 1, this.y + var12 + 1, 0, 10);
         --var12;
      }

      var5.getItemRenderer().renderAndDecorateFakeItem(var11, this.x + var12, this.y + var12);
      if (var8) {
         var9.popPose();
         RenderSystem.applyModelViewMatrix();
      }

   }

   private List<Recipe<?>> getOrderedRecipes() {
      List var1 = this.collection.getDisplayRecipes(true);
      if (!this.book.isFiltering(this.menu)) {
         var1.addAll(this.collection.getDisplayRecipes(false));
      }

      return var1;
   }

   public boolean isOnlyOption() {
      return this.getOrderedRecipes().size() == 1;
   }

   public Recipe<?> getRecipe() {
      List var1 = this.getOrderedRecipes();
      return (Recipe)var1.get(this.currentIndex);
   }

   public List<Component> getTooltipText(Screen var1) {
      ItemStack var2 = ((Recipe)this.getOrderedRecipes().get(this.currentIndex)).getResultItem();
      ArrayList var3 = Lists.newArrayList(var1.getTooltipFromItem(var2));
      if (this.collection.getRecipes(this.book.isFiltering(this.menu)).size() > 1) {
         var3.add(MORE_RECIPES_TOOLTIP);
      }

      return var3;
   }

   public void updateNarration(NarrationElementOutput var1) {
      ItemStack var2 = ((Recipe)this.getOrderedRecipes().get(this.currentIndex)).getResultItem();
      var1.add(NarratedElementType.TITLE, (Component)(new TranslatableComponent("narration.recipe", new Object[]{var2.getHoverName()})));
      if (this.collection.getRecipes(this.book.isFiltering(this.menu)).size() > 1) {
         var1.add(NarratedElementType.USAGE, new TranslatableComponent("narration.button.usage.hovered"), new TranslatableComponent("narration.recipe.usage.more"));
      } else {
         var1.add(NarratedElementType.USAGE, (Component)(new TranslatableComponent("narration.button.usage.hovered")));
      }

   }

   public int getWidth() {
      return 25;
   }

   protected boolean isValidClickButton(int var1) {
      return var1 == 0 || var1 == 1;
   }
}
