package net.minecraft.world.item.crafting;

import com.google.gson.JsonObject;
import java.util.Optional;
import java.util.stream.Stream;
import net.minecraft.core.Holder;
import net.minecraft.core.RegistryAccess;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.Container;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.armortrim.ArmorTrim;
import net.minecraft.world.item.armortrim.TrimMaterial;
import net.minecraft.world.item.armortrim.TrimMaterials;
import net.minecraft.world.item.armortrim.TrimPattern;
import net.minecraft.world.item.armortrim.TrimPatterns;
import net.minecraft.world.level.Level;

public class SmithingTrimRecipe implements SmithingRecipe {
   private final ResourceLocation id;
   final Ingredient template;
   final Ingredient base;
   final Ingredient addition;

   public SmithingTrimRecipe(ResourceLocation var1, Ingredient var2, Ingredient var3, Ingredient var4) {
      super();
      this.id = var1;
      this.template = var2;
      this.base = var3;
      this.addition = var4;
   }

   @Override
   public boolean matches(Container var1, Level var2) {
      return this.template.test(var1.getItem(0)) && this.base.test(var1.getItem(1)) && this.addition.test(var1.getItem(2));
   }

   @Override
   public ItemStack assemble(Container var1, RegistryAccess var2) {
      ItemStack var3 = var1.getItem(1);
      if (this.base.test(var3)) {
         Optional var4 = TrimMaterials.getFromIngredient(var2, var1.getItem(2));
         Optional var5 = TrimPatterns.getFromTemplate(var2, var1.getItem(0));
         if (var4.isPresent() && var5.isPresent()) {
            Optional var6 = ArmorTrim.getTrim(var2, var3);
            if (var6.isPresent() && ((ArmorTrim)var6.get()).hasPatternAndMaterial((Holder<TrimPattern>)var5.get(), (Holder<TrimMaterial>)var4.get())) {
               return ItemStack.EMPTY;
            }

            ItemStack var7 = var3.copy();
            var7.setCount(1);
            ArmorTrim var8 = new ArmorTrim((Holder<TrimMaterial>)var4.get(), (Holder<TrimPattern>)var5.get());
            if (ArmorTrim.setTrim(var2, var7, var8)) {
               return var7;
            }
         }
      }

      return ItemStack.EMPTY;
   }

   @Override
   public ItemStack getResultItem(RegistryAccess var1) {
      ItemStack var2 = new ItemStack(Items.IRON_CHESTPLATE);
      Optional var3 = var1.registryOrThrow(Registries.TRIM_PATTERN).holders().findFirst();
      if (var3.isPresent()) {
         Optional var4 = var1.registryOrThrow(Registries.TRIM_MATERIAL).getHolder(TrimMaterials.REDSTONE);
         if (var4.isPresent()) {
            ArmorTrim var5 = new ArmorTrim((Holder<TrimMaterial>)var4.get(), (Holder<TrimPattern>)var3.get());
            ArmorTrim.setTrim(var1, var2, var5);
         }
      }

      return var2;
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
      return RecipeSerializer.SMITHING_TRIM;
   }

   @Override
   public boolean isIncomplete() {
      return Stream.of(this.template, this.base, this.addition).anyMatch(Ingredient::isEmpty);
   }

   public static class Serializer implements RecipeSerializer<SmithingTrimRecipe> {
      public Serializer() {
         super();
      }

      public SmithingTrimRecipe fromJson(ResourceLocation var1, JsonObject var2) {
         Ingredient var3 = Ingredient.fromJson(GsonHelper.getNonNull(var2, "template"));
         Ingredient var4 = Ingredient.fromJson(GsonHelper.getNonNull(var2, "base"));
         Ingredient var5 = Ingredient.fromJson(GsonHelper.getNonNull(var2, "addition"));
         return new SmithingTrimRecipe(var1, var3, var4, var5);
      }

      public SmithingTrimRecipe fromNetwork(ResourceLocation var1, FriendlyByteBuf var2) {
         Ingredient var3 = Ingredient.fromNetwork(var2);
         Ingredient var4 = Ingredient.fromNetwork(var2);
         Ingredient var5 = Ingredient.fromNetwork(var2);
         return new SmithingTrimRecipe(var1, var3, var4, var5);
      }

      public void toNetwork(FriendlyByteBuf var1, SmithingTrimRecipe var2) {
         var2.template.toNetwork(var1);
         var2.base.toNetwork(var1);
         var2.addition.toNetwork(var1);
      }
   }
}
