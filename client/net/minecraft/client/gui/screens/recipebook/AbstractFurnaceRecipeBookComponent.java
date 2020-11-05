package net.minecraft.client.gui.screens.recipebook;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import javax.annotation.Nullable;
import net.minecraft.client.gui.GuiComponent;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.core.NonNullList;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.Recipe;

public abstract class AbstractFurnaceRecipeBookComponent extends RecipeBookComponent {
   private Iterator<Item> iterator;
   private Set<Item> fuels;
   private Slot fuelSlot;
   private Item fuel;
   private float time;

   public AbstractFurnaceRecipeBookComponent() {
      super();
   }

   protected void initFilterButtonTextures() {
      this.filterButton.initTextureValues(152, 182, 28, 18, RECIPE_BOOK_LOCATION);
   }

   public void slotClicked(@Nullable Slot var1) {
      super.slotClicked(var1);
      if (var1 != null && var1.index < this.menu.getSize()) {
         this.fuelSlot = null;
      }

   }

   public void setupGhostRecipe(Recipe<?> var1, List<Slot> var2) {
      ItemStack var3 = var1.getResultItem();
      this.ghostRecipe.setRecipe(var1);
      this.ghostRecipe.addIngredient(Ingredient.of(var3), ((Slot)var2.get(2)).x, ((Slot)var2.get(2)).y);
      NonNullList var4 = var1.getIngredients();
      this.fuelSlot = (Slot)var2.get(1);
      if (this.fuels == null) {
         this.fuels = this.getFuelItems();
      }

      this.iterator = this.fuels.iterator();
      this.fuel = null;
      Iterator var5 = var4.iterator();

      for(int var6 = 0; var6 < 2; ++var6) {
         if (!var5.hasNext()) {
            return;
         }

         Ingredient var7 = (Ingredient)var5.next();
         if (!var7.isEmpty()) {
            Slot var8 = (Slot)var2.get(var6);
            this.ghostRecipe.addIngredient(var7, var8.x, var8.y);
         }
      }

   }

   protected abstract Set<Item> getFuelItems();

   public void renderGhostRecipe(PoseStack var1, int var2, int var3, boolean var4, float var5) {
      super.renderGhostRecipe(var1, var2, var3, var4, var5);
      if (this.fuelSlot != null) {
         if (!Screen.hasControlDown()) {
            this.time += var5;
         }

         int var6 = this.fuelSlot.x + var2;
         int var7 = this.fuelSlot.y + var3;
         GuiComponent.fill(var1, var6, var7, var6 + 16, var7 + 16, 822018048);
         this.minecraft.getItemRenderer().renderAndDecorateItem(this.minecraft.player, this.getFuel().getDefaultInstance(), var6, var7);
         RenderSystem.depthFunc(516);
         GuiComponent.fill(var1, var6, var7, var6 + 16, var7 + 16, 822083583);
         RenderSystem.depthFunc(515);
      }
   }

   private Item getFuel() {
      if (this.fuel == null || this.time > 30.0F) {
         this.time = 0.0F;
         if (this.iterator == null || !this.iterator.hasNext()) {
            if (this.fuels == null) {
               this.fuels = this.getFuelItems();
            }

            this.iterator = this.fuels.iterator();
         }

         this.fuel = (Item)this.iterator.next();
      }

      return this.fuel;
   }
}
