package net.minecraft.client.gui.screens.recipebook;

import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.platform.Lighting;
import java.util.Iterator;
import java.util.List;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.stats.RecipeBook;
import net.minecraft.util.Mth;
import net.minecraft.world.inventory.RecipeBookMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Recipe;

public class RecipeButton extends AbstractWidget {
   private static final ResourceLocation RECIPE_BOOK_LOCATION = new ResourceLocation("textures/gui/recipe_book.png");
   private RecipeBookMenu<?> menu;
   private RecipeBook book;
   private RecipeCollection collection;
   private float time;
   private float animationTime;
   private int currentIndex;

   public RecipeButton() {
      super(0, 0, 25, 25, "");
   }

   public void init(RecipeCollection var1, RecipeBookPage var2) {
      this.collection = var1;
      this.menu = (RecipeBookMenu)var2.getMinecraft().player.containerMenu;
      this.book = var2.getRecipeBook();
      List var3 = var1.getRecipes(this.book.isFilteringCraftable(this.menu));
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

   public void renderButton(int var1, int var2, float var3) {
      if (!Screen.hasControlDown()) {
         this.time += var3;
      }

      Lighting.turnOnGui();
      Minecraft var4 = Minecraft.getInstance();
      var4.getTextureManager().bind(RECIPE_BOOK_LOCATION);
      GlStateManager.disableLighting();
      int var5 = 29;
      if (!this.collection.hasCraftable()) {
         var5 += 25;
      }

      int var6 = 206;
      if (this.collection.getRecipes(this.book.isFilteringCraftable(this.menu)).size() > 1) {
         var6 += 25;
      }

      boolean var7 = this.animationTime > 0.0F;
      if (var7) {
         float var8 = 1.0F + 0.1F * (float)Math.sin((double)(this.animationTime / 15.0F * 3.1415927F));
         GlStateManager.pushMatrix();
         GlStateManager.translatef((float)(this.x + 8), (float)(this.y + 12), 0.0F);
         GlStateManager.scalef(var8, var8, 1.0F);
         GlStateManager.translatef((float)(-(this.x + 8)), (float)(-(this.y + 12)), 0.0F);
         this.animationTime -= var3;
      }

      this.blit(this.x, this.y, var5, var6, this.width, this.height);
      List var11 = this.getOrderedRecipes();
      this.currentIndex = Mth.floor(this.time / 30.0F) % var11.size();
      ItemStack var9 = ((Recipe)var11.get(this.currentIndex)).getResultItem();
      int var10 = 4;
      if (this.collection.hasSingleResultItem() && this.getOrderedRecipes().size() > 1) {
         var4.getItemRenderer().renderAndDecorateItem(var9, this.x + var10 + 1, this.y + var10 + 1);
         --var10;
      }

      var4.getItemRenderer().renderAndDecorateItem(var9, this.x + var10, this.y + var10);
      if (var7) {
         GlStateManager.popMatrix();
      }

      GlStateManager.enableLighting();
      Lighting.turnOff();
   }

   private List<Recipe<?>> getOrderedRecipes() {
      List var1 = this.collection.getDisplayRecipes(true);
      if (!this.book.isFilteringCraftable(this.menu)) {
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

   public List<String> getTooltipText(Screen var1) {
      ItemStack var2 = ((Recipe)this.getOrderedRecipes().get(this.currentIndex)).getResultItem();
      List var3 = var1.getTooltipFromItem(var2);
      if (this.collection.getRecipes(this.book.isFilteringCraftable(this.menu)).size() > 1) {
         var3.add(I18n.get("gui.recipebook.moreRecipes"));
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
