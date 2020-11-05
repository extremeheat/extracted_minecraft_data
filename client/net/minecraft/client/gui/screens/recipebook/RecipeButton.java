package net.minecraft.client.gui.screens.recipebook;

import com.google.common.collect.Lists;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.screens.Screen;
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
      var5.getTextureManager().bind(RECIPE_BOOK_LOCATION);
      int var6 = 29;
      if (!this.collection.hasCraftable()) {
         var6 += 25;
      }

      int var7 = 206;
      if (this.collection.getRecipes(this.book.isFiltering(this.menu)).size() > 1) {
         var7 += 25;
      }

      boolean var8 = this.animationTime > 0.0F;
      if (var8) {
         float var9 = 1.0F + 0.1F * (float)Math.sin((double)(this.animationTime / 15.0F * 3.1415927F));
         RenderSystem.pushMatrix();
         RenderSystem.translatef((float)(this.x + 8), (float)(this.y + 12), 0.0F);
         RenderSystem.scalef(var9, var9, 1.0F);
         RenderSystem.translatef((float)(-(this.x + 8)), (float)(-(this.y + 12)), 0.0F);
         this.animationTime -= var4;
      }

      this.blit(var1, this.x, this.y, var6, var7, this.width, this.height);
      List var12 = this.getOrderedRecipes();
      this.currentIndex = Mth.floor(this.time / 30.0F) % var12.size();
      ItemStack var10 = ((Recipe)var12.get(this.currentIndex)).getResultItem();
      int var11 = 4;
      if (this.collection.hasSingleResultItem() && this.getOrderedRecipes().size() > 1) {
         var5.getItemRenderer().renderAndDecorateItem(var10, this.x + var11 + 1, this.y + var11 + 1);
         --var11;
      }

      var5.getItemRenderer().renderAndDecorateFakeItem(var10, this.x + var11, this.y + var11);
      if (var8) {
         RenderSystem.popMatrix();
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

   public int getWidth() {
      return 25;
   }

   protected boolean isValidClickButton(int var1) {
      return var1 == 0 || var1 == 1;
   }
}
