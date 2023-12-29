package net.minecraft.world.item.crafting;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.mojang.serialization.codecs.RecordCodecBuilder.Instance;
import net.minecraft.core.NonNullList;
import net.minecraft.core.RegistryAccess;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.util.ExtraCodecs;
import net.minecraft.world.inventory.CraftingContainer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

public class ShapedRecipe implements CraftingRecipe {
   final ShapedRecipePattern pattern;
   final ItemStack result;
   final String group;
   final CraftingBookCategory category;
   final boolean showNotification;

   public ShapedRecipe(String var1, CraftingBookCategory var2, ShapedRecipePattern var3, ItemStack var4, boolean var5) {
      super();
      this.group = var1;
      this.category = var2;
      this.pattern = var3;
      this.result = var4;
      this.showNotification = var5;
   }

   public ShapedRecipe(String var1, CraftingBookCategory var2, ShapedRecipePattern var3, ItemStack var4) {
      this(var1, var2, var3, var4, true);
   }

   @Override
   public RecipeSerializer<?> getSerializer() {
      return RecipeSerializer.SHAPED_RECIPE;
   }

   @Override
   public String getGroup() {
      return this.group;
   }

   @Override
   public CraftingBookCategory category() {
      return this.category;
   }

   @Override
   public ItemStack getResultItem(RegistryAccess var1) {
      return this.result;
   }

   @Override
   public NonNullList<Ingredient> getIngredients() {
      return this.pattern.ingredients();
   }

   @Override
   public boolean showNotification() {
      return this.showNotification;
   }

   @Override
   public boolean canCraftInDimensions(int var1, int var2) {
      return var1 >= this.pattern.width() && var2 >= this.pattern.height();
   }

   public boolean matches(CraftingContainer var1, Level var2) {
      return this.pattern.matches(var1);
   }

   public ItemStack assemble(CraftingContainer var1, RegistryAccess var2) {
      return this.getResultItem(var2).copy();
   }

   public int getWidth() {
      return this.pattern.width();
   }

   public int getHeight() {
      return this.pattern.height();
   }

   @Override
   public boolean isIncomplete() {
      NonNullList var1 = this.getIngredients();
      return var1.isEmpty() || var1.stream().filter(var0 -> !var0.isEmpty()).anyMatch(var0 -> var0.getItems().length == 0);
   }

   public static class Serializer implements RecipeSerializer<ShapedRecipe> {
      public static final Codec<ShapedRecipe> CODEC = RecordCodecBuilder.create(
         var0 -> var0.group(
                  ExtraCodecs.strictOptionalField(Codec.STRING, "group", "").forGetter(var0x -> var0x.group),
                  CraftingBookCategory.CODEC.fieldOf("category").orElse(CraftingBookCategory.MISC).forGetter(var0x -> var0x.category),
                  ShapedRecipePattern.MAP_CODEC.forGetter(var0x -> var0x.pattern),
                  ItemStack.ITEM_WITH_COUNT_CODEC.fieldOf("result").forGetter(var0x -> var0x.result),
                  ExtraCodecs.strictOptionalField(Codec.BOOL, "show_notification", true).forGetter(var0x -> var0x.showNotification)
               )
               .apply(var0, ShapedRecipe::new)
      );

      public Serializer() {
         super();
      }

      @Override
      public Codec<ShapedRecipe> codec() {
         return CODEC;
      }

      public ShapedRecipe fromNetwork(FriendlyByteBuf var1) {
         String var2 = var1.readUtf();
         CraftingBookCategory var3 = var1.readEnum(CraftingBookCategory.class);
         ShapedRecipePattern var4 = ShapedRecipePattern.fromNetwork(var1);
         ItemStack var5 = var1.readItem();
         boolean var6 = var1.readBoolean();
         return new ShapedRecipe(var2, var3, var4, var5, var6);
      }

      public void toNetwork(FriendlyByteBuf var1, ShapedRecipe var2) {
         var1.writeUtf(var2.group);
         var1.writeEnum(var2.category);
         var2.pattern.toNetwork(var1);
         var1.writeItem(var2.result);
         var1.writeBoolean(var2.showNotification);
      }
   }
}
