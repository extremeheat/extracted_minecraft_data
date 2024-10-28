package net.minecraft.world.item.crafting;

import net.minecraft.core.HolderLookup;
import net.minecraft.core.NonNullList;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

public abstract class AbstractCookingRecipe implements Recipe<SingleRecipeInput> {
   protected final RecipeType<?> type;
   protected final CookingBookCategory category;
   protected final String group;
   protected final Ingredient ingredient;
   protected final ItemStack result;
   protected final float experience;
   protected final int cookingTime;

   public AbstractCookingRecipe(RecipeType<?> var1, String var2, CookingBookCategory var3, Ingredient var4, ItemStack var5, float var6, int var7) {
      super();
      this.type = var1;
      this.category = var3;
      this.group = var2;
      this.ingredient = var4;
      this.result = var5;
      this.experience = var6;
      this.cookingTime = var7;
   }

   public boolean matches(SingleRecipeInput var1, Level var2) {
      return this.ingredient.test(var1.item());
   }

   public ItemStack assemble(SingleRecipeInput var1, HolderLookup.Provider var2) {
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

   public ItemStack getResultItem(HolderLookup.Provider var1) {
      return this.result;
   }

   public String getGroup() {
      return this.group;
   }

   public int getCookingTime() {
      return this.cookingTime;
   }

   public RecipeType<?> getType() {
      return this.type;
   }

   public CookingBookCategory category() {
      return this.category;
   }

   public interface Factory<T extends AbstractCookingRecipe> {
      T create(String var1, CookingBookCategory var2, Ingredient var3, ItemStack var4, float var5, int var6);
   }
}
