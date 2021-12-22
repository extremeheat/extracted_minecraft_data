package net.minecraft.world.item.crafting;

import net.minecraft.core.NonNullList;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.Container;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

public abstract class AbstractCookingRecipe implements Recipe<Container> {
   protected final RecipeType<?> type;
   // $FF: renamed from: id net.minecraft.resources.ResourceLocation
   protected final ResourceLocation field_126;
   protected final String group;
   protected final Ingredient ingredient;
   protected final ItemStack result;
   protected final float experience;
   protected final int cookingTime;

   public AbstractCookingRecipe(RecipeType<?> var1, ResourceLocation var2, String var3, Ingredient var4, ItemStack var5, float var6, int var7) {
      super();
      this.type = var1;
      this.field_126 = var2;
      this.group = var3;
      this.ingredient = var4;
      this.result = var5;
      this.experience = var6;
      this.cookingTime = var7;
   }

   public boolean matches(Container var1, Level var2) {
      return this.ingredient.test(var1.getItem(0));
   }

   public ItemStack assemble(Container var1) {
      return this.result.copy();
   }

   public boolean canCraftInDimensions(int var1, int var2) {
      return true;
   }

   public NonNullList<Ingredient> getIngredients() {
      NonNullList var1 = NonNullList.create();
      var1.add(this.ingredient);
      return var1;
   }

   public float getExperience() {
      return this.experience;
   }

   public ItemStack getResultItem() {
      return this.result;
   }

   public String getGroup() {
      return this.group;
   }

   public int getCookingTime() {
      return this.cookingTime;
   }

   public ResourceLocation getId() {
      return this.field_126;
   }

   public RecipeType<?> getType() {
      return this.type;
   }
}
