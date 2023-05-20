package net.minecraft.world.item.crafting;

import com.google.gson.JsonObject;
import java.util.stream.Stream;
import net.minecraft.core.RegistryAccess;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.Container;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

@Deprecated(
   forRemoval = true
)
public class LegacyUpgradeRecipe implements SmithingRecipe {
   final Ingredient base;
   final Ingredient addition;
   final ItemStack result;
   private final ResourceLocation id;

   public LegacyUpgradeRecipe(ResourceLocation var1, Ingredient var2, Ingredient var3, ItemStack var4) {
      super();
      this.id = var1;
      this.base = var2;
      this.addition = var3;
      this.result = var4;
   }

   @Override
   public boolean matches(Container var1, Level var2) {
      return this.base.test(var1.getItem(0)) && this.addition.test(var1.getItem(1));
   }

   @Override
   public ItemStack assemble(Container var1, RegistryAccess var2) {
      ItemStack var3 = this.result.copy();
      CompoundTag var4 = var1.getItem(0).getTag();
      if (var4 != null) {
         var3.setTag(var4.copy());
      }

      return var3;
   }

   @Override
   public boolean canCraftInDimensions(int var1, int var2) {
      return var1 * var2 >= 2;
   }

   @Override
   public ItemStack getResultItem(RegistryAccess var1) {
      return this.result;
   }

   @Override
   public boolean isTemplateIngredient(ItemStack var1) {
      return false;
   }

   @Override
   public boolean isBaseIngredient(ItemStack var1) {
      return this.base.test(var1);
   }

   @Override
   public boolean isAdditionIngredient(ItemStack var1) {
      return this.addition.test(var1);
   }

   @Override
   public ResourceLocation getId() {
      return this.id;
   }

   @Override
   public RecipeSerializer<?> getSerializer() {
      return RecipeSerializer.SMITHING;
   }

   @Override
   public boolean isIncomplete() {
      return Stream.of(this.base, this.addition).anyMatch(var0 -> var0.getItems().length == 0);
   }

   public static class Serializer implements RecipeSerializer<LegacyUpgradeRecipe> {
      public Serializer() {
         super();
      }

      public LegacyUpgradeRecipe fromJson(ResourceLocation var1, JsonObject var2) {
         Ingredient var3 = Ingredient.fromJson(GsonHelper.getAsJsonObject(var2, "base"));
         Ingredient var4 = Ingredient.fromJson(GsonHelper.getAsJsonObject(var2, "addition"));
         ItemStack var5 = ShapedRecipe.itemStackFromJson(GsonHelper.getAsJsonObject(var2, "result"));
         return new LegacyUpgradeRecipe(var1, var3, var4, var5);
      }

      public LegacyUpgradeRecipe fromNetwork(ResourceLocation var1, FriendlyByteBuf var2) {
         Ingredient var3 = Ingredient.fromNetwork(var2);
         Ingredient var4 = Ingredient.fromNetwork(var2);
         ItemStack var5 = var2.readItem();
         return new LegacyUpgradeRecipe(var1, var3, var4, var5);
      }

      public void toNetwork(FriendlyByteBuf var1, LegacyUpgradeRecipe var2) {
         var2.base.toNetwork(var1);
         var2.addition.toNetwork(var1);
         var1.writeItem(var2.result);
      }
   }
}
