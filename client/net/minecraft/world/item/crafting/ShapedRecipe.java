package net.minecraft.world.item.crafting;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.NonNullList;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
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
   public ItemStack getResultItem(HolderLookup.Provider var1) {
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

   public ItemStack assemble(CraftingContainer var1, HolderLookup.Provider var2) {
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
      public static final MapCodec<ShapedRecipe> CODEC = RecordCodecBuilder.mapCodec(
         var0 -> var0.group(
                  Codec.STRING.optionalFieldOf("group", "").forGetter(var0x -> var0x.group),
                  CraftingBookCategory.CODEC.fieldOf("category").orElse(CraftingBookCategory.MISC).forGetter(var0x -> var0x.category),
                  ShapedRecipePattern.MAP_CODEC.forGetter(var0x -> var0x.pattern),
                  ItemStack.STRICT_CODEC.fieldOf("result").forGetter(var0x -> var0x.result),
                  Codec.BOOL.optionalFieldOf("show_notification", true).forGetter(var0x -> var0x.showNotification)
               )
               .apply(var0, ShapedRecipe::new)
      );
      public static final StreamCodec<RegistryFriendlyByteBuf, ShapedRecipe> STREAM_CODEC = StreamCodec.of(
         ShapedRecipe.Serializer::toNetwork, ShapedRecipe.Serializer::fromNetwork
      );

      public Serializer() {
         super();
      }

      @Override
      public MapCodec<ShapedRecipe> codec() {
         return CODEC;
      }

      @Override
      public StreamCodec<RegistryFriendlyByteBuf, ShapedRecipe> streamCodec() {
         return STREAM_CODEC;
      }

      private static ShapedRecipe fromNetwork(RegistryFriendlyByteBuf var0) {
         String var1 = var0.readUtf();
         CraftingBookCategory var2 = var0.readEnum(CraftingBookCategory.class);
         ShapedRecipePattern var3 = ShapedRecipePattern.STREAM_CODEC.decode(var0);
         ItemStack var4 = ItemStack.STREAM_CODEC.decode(var0);
         boolean var5 = var0.readBoolean();
         return new ShapedRecipe(var1, var2, var3, var4, var5);
      }

      private static void toNetwork(RegistryFriendlyByteBuf var0, ShapedRecipe var1) {
         var0.writeUtf(var1.group);
         var0.writeEnum(var1.category);
         ShapedRecipePattern.STREAM_CODEC.encode(var0, var1.pattern);
         ItemStack.STREAM_CODEC.encode(var0, var1.result);
         var0.writeBoolean(var1.showNotification);
      }
   }
}
