package net.minecraft.world.item.crafting;

import net.minecraft.core.NonNullList;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.Container;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

public abstract class AbstractCookingRecipe implements Recipe<Container> {
   protected final RecipeType<?> type;
   protected final ResourceLocation id;
   private final CookingBookCategory category;
   protected final String group;
   protected final Ingredient ingredient;
   protected final ItemStack result;
   protected final float experience;
   protected final int cookingTime;

   public AbstractCookingRecipe(
      RecipeType<?> var1, ResourceLocation var2, String var3, CookingBookCategory var4, Ingredient var5, ItemStack var6, float var7, int var8
   ) {
      super();
      this.type = var1;
      this.category = var4;
      this.id = var2;
      this.group = var3;
      this.ingredient = var5;
      this.result = var6;
      this.experience = var7;
      this.cookingTime = var8;
   }

   @Override
   public boolean matches(Container var1, Level var2) {
      return this.ingredient.test(var1.getItem(0));
   }

   @Override
   public ItemStack assemble(Container var1) {
      return this.result.copy();
   }

   @Override
   public boolean canCraftInDimensions(int var1, int var2) {
      return true;
   }

   @Override
   public NonNullList<Ingredient> getIngredients() {
      NonNullList var1 = NonNullList.create();
      var1.add(this.ingredient);
      return var1;
   }

   public float getExperience() {
      return this.experience;
   }

   @Override
   public ItemStack getResultItem() {
      return this.result;
   }

   @Override
   public String getGroup() {
      return this.group;
   }

   public int getCookingTime() {
      return this.cookingTime;
   }

   @Override
   public ResourceLocation getId() {
      return this.id;
   }

   @Override
   public RecipeType<?> getType() {
      return this.type;
   }

   public CookingBookCategory category() {
      return this.category;
   }
}
