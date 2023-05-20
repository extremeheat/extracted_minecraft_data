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

public class SmithingTransformRecipe implements SmithingRecipe {
   private final ResourceLocation id;
   final Ingredient template;
   final Ingredient base;
   final Ingredient addition;
   final ItemStack result;

   public SmithingTransformRecipe(ResourceLocation var1, Ingredient var2, Ingredient var3, Ingredient var4, ItemStack var5) {
      super();
      this.id = var1;
      this.template = var2;
      this.base = var3;
      this.addition = var4;
      this.result = var5;
   }

   @Override
   public boolean matches(Container var1, Level var2) {
      return this.template.test(var1.getItem(0)) && this.base.test(var1.getItem(1)) && this.addition.test(var1.getItem(2));
   }

   @Override
   public ItemStack assemble(Container var1, RegistryAccess var2) {
      ItemStack var3 = this.result.copy();
      CompoundTag var4 = var1.getItem(1).getTag();
      if (var4 != null) {
         var3.setTag(var4.copy());
      }

      return var3;
   }

   @Override
   public ItemStack getResultItem(RegistryAccess var1) {
      return this.result;
   }

   @Override
   public boolean isTemplateIngredient(ItemStack var1) {
      return this.template.test(var1);
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
      return RecipeSerializer.SMITHING_TRANSFORM;
   }

   @Override
   public boolean isIncomplete() {
      return Stream.of(this.template, this.base, this.addition).anyMatch(Ingredient::isEmpty);
   }

   public static class Serializer implements RecipeSerializer<SmithingTransformRecipe> {
      public Serializer() {
         super();
      }

      public SmithingTransformRecipe fromJson(ResourceLocation var1, JsonObject var2) {
         Ingredient var3 = Ingredient.fromJson(GsonHelper.getAsJsonObject(var2, "template"));
         Ingredient var4 = Ingredient.fromJson(GsonHelper.getAsJsonObject(var2, "base"));
         Ingredient var5 = Ingredient.fromJson(GsonHelper.getAsJsonObject(var2, "addition"));
         ItemStack var6 = ShapedRecipe.itemStackFromJson(GsonHelper.getAsJsonObject(var2, "result"));
         return new SmithingTransformRecipe(var1, var3, var4, var5, var6);
      }

      public SmithingTransformRecipe fromNetwork(ResourceLocation var1, FriendlyByteBuf var2) {
         Ingredient var3 = Ingredient.fromNetwork(var2);
         Ingredient var4 = Ingredient.fromNetwork(var2);
         Ingredient var5 = Ingredient.fromNetwork(var2);
         ItemStack var6 = var2.readItem();
         return new SmithingTransformRecipe(var1, var3, var4, var5, var6);
      }

      public void toNetwork(FriendlyByteBuf var1, SmithingTransformRecipe var2) {
         var2.template.toNetwork(var1);
         var2.base.toNetwork(var1);
         var2.addition.toNetwork(var1);
         var1.writeItem(var2.result);
      }
   }
}
