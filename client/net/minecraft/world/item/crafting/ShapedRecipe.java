package net.minecraft.world.item.crafting;

import com.google.common.annotations.VisibleForTesting;
import com.google.common.collect.Sets;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import net.minecraft.core.NonNullList;
import net.minecraft.core.RegistryAccess;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.util.ExtraCodecs;
import net.minecraft.world.inventory.CraftingContainer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.apache.commons.lang3.NotImplementedException;

public class ShapedRecipe implements CraftingRecipe {
   final int width;
   final int height;
   final NonNullList<Ingredient> recipeItems;
   final ItemStack result;
   final String group;
   final CraftingBookCategory category;
   final boolean showNotification;

   public ShapedRecipe(String var1, CraftingBookCategory var2, int var3, int var4, NonNullList<Ingredient> var5, ItemStack var6, boolean var7) {
      super();
      this.group = var1;
      this.category = var2;
      this.width = var3;
      this.height = var4;
      this.recipeItems = var5;
      this.result = var6;
      this.showNotification = var7;
   }

   public ShapedRecipe(String var1, CraftingBookCategory var2, int var3, int var4, NonNullList<Ingredient> var5, ItemStack var6) {
      this(var1, var2, var3, var4, var5, var6, true);
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
      return this.recipeItems;
   }

   @Override
   public boolean showNotification() {
      return this.showNotification;
   }

   @Override
   public boolean canCraftInDimensions(int var1, int var2) {
      return var1 >= this.width && var2 >= this.height;
   }

   public boolean matches(CraftingContainer var1, Level var2) {
      for(int var3 = 0; var3 <= var1.getWidth() - this.width; ++var3) {
         for(int var4 = 0; var4 <= var1.getHeight() - this.height; ++var4) {
            if (this.matches(var1, var3, var4, true)) {
               return true;
            }

            if (this.matches(var1, var3, var4, false)) {
               return true;
            }
         }
      }

      return false;
   }

   private boolean matches(CraftingContainer var1, int var2, int var3, boolean var4) {
      for(int var5 = 0; var5 < var1.getWidth(); ++var5) {
         for(int var6 = 0; var6 < var1.getHeight(); ++var6) {
            int var7 = var5 - var2;
            int var8 = var6 - var3;
            Ingredient var9 = Ingredient.EMPTY;
            if (var7 >= 0 && var8 >= 0 && var7 < this.width && var8 < this.height) {
               if (var4) {
                  var9 = this.recipeItems.get(this.width - var7 - 1 + var8 * this.width);
               } else {
                  var9 = this.recipeItems.get(var7 + var8 * this.width);
               }
            }

            if (!var9.test(var1.getItem(var5 + var6 * var1.getWidth()))) {
               return false;
            }
         }
      }

      return true;
   }

   public ItemStack assemble(CraftingContainer var1, RegistryAccess var2) {
      return this.getResultItem(var2).copy();
   }

   public int getWidth() {
      return this.width;
   }

   public int getHeight() {
      return this.height;
   }

   @VisibleForTesting
   static String[] shrink(List<String> var0) {
      int var1 = 2147483647;
      int var2 = 0;
      int var3 = 0;
      int var4 = 0;

      for(int var5 = 0; var5 < var0.size(); ++var5) {
         String var6 = (String)var0.get(var5);
         var1 = Math.min(var1, firstNonSpace(var6));
         int var7 = lastNonSpace(var6);
         var2 = Math.max(var2, var7);
         if (var7 < 0) {
            if (var3 == var5) {
               ++var3;
            }

            ++var4;
         } else {
            var4 = 0;
         }
      }

      if (var0.size() == var4) {
         return new String[0];
      } else {
         String[] var8 = new String[var0.size() - var4 - var3];

         for(int var9 = 0; var9 < var8.length; ++var9) {
            var8[var9] = ((String)var0.get(var9 + var3)).substring(var1, var2 + 1);
         }

         return var8;
      }
   }

   @Override
   public boolean isIncomplete() {
      NonNullList var1 = this.getIngredients();
      return var1.isEmpty() || var1.stream().filter(var0 -> !var0.isEmpty()).anyMatch(var0 -> var0.getItems().length == 0);
   }

   private static int firstNonSpace(String var0) {
      int var1 = 0;

      while(var1 < var0.length() && var0.charAt(var1) == ' ') {
         ++var1;
      }

      return var1;
   }

   private static int lastNonSpace(String var0) {
      int var1 = var0.length() - 1;

      while(var1 >= 0 && var0.charAt(var1) == ' ') {
         --var1;
      }

      return var1;
   }

   public static class Serializer implements RecipeSerializer<ShapedRecipe> {
      static final Codec<List<String>> PATTERN_CODEC = Codec.STRING.listOf().flatXmap(var0 -> {
         if (var0.size() > 3) {
            return DataResult.error(() -> "Invalid pattern: too many rows, 3 is maximum");
         } else if (var0.isEmpty()) {
            return DataResult.error(() -> "Invalid pattern: empty pattern not allowed");
         } else {
            int var1 = ((String)var0.get(0)).length();

            for(String var3 : var0) {
               if (var3.length() > 3) {
                  return DataResult.error(() -> "Invalid pattern: too many columns, 3 is maximum");
               }

               if (var1 != var3.length()) {
                  return DataResult.error(() -> "Invalid pattern: each row must be the same width");
               }
            }

            return DataResult.success(var0);
         }
      }, DataResult::success);
      static final Codec<String> SINGLE_CHARACTER_STRING_CODEC = Codec.STRING.flatXmap(var0 -> {
         if (var0.length() != 1) {
            return DataResult.error(() -> "Invalid key entry: '" + var0 + "' is an invalid symbol (must be 1 character only).");
         } else {
            return " ".equals(var0) ? DataResult.error(() -> "Invalid key entry: ' ' is a reserved symbol.") : DataResult.success(var0);
         }
      }, DataResult::success);
      private static final Codec<ShapedRecipe> CODEC = ShapedRecipe.Serializer.RawShapedRecipe.CODEC.flatXmap(var0 -> {
         String[] var1 = ShapedRecipe.shrink(var0.pattern);
         int var2 = var1[0].length();
         int var3 = var1.length;
         NonNullList var4 = NonNullList.withSize(var2 * var3, Ingredient.EMPTY);
         HashSet var5 = Sets.newHashSet(var0.key.keySet());

         for(int var6 = 0; var6 < var1.length; ++var6) {
            String var7 = var1[var6];

            for(int var8 = 0; var8 < var7.length(); ++var8) {
               String var9 = var7.substring(var8, var8 + 1);
               Ingredient var10 = var9.equals(" ") ? Ingredient.EMPTY : var0.key.get(var9);
               if (var10 == null) {
                  return DataResult.error(() -> "Pattern references symbol '" + var9 + "' but it's not defined in the key");
               }

               var5.remove(var9);
               var4.set(var8 + var2 * var6, var10);
            }
         }

         if (!var5.isEmpty()) {
            return DataResult.error(() -> "Key defines symbols that aren't used in pattern: " + var5);
         } else {
            ShapedRecipe var11 = new ShapedRecipe(var0.group, var0.category, var2, var3, var4, var0.result, var0.showNotification);
            return DataResult.success(var11);
         }
      }, var0 -> {
         throw new NotImplementedException("Serializing ShapedRecipe is not implemented yet.");
      });

      public Serializer() {
         super();
      }

      @Override
      public Codec<ShapedRecipe> codec() {
         return CODEC;
      }

      public ShapedRecipe fromNetwork(FriendlyByteBuf var1) {
         int var2 = var1.readVarInt();
         int var3 = var1.readVarInt();
         String var4 = var1.readUtf();
         CraftingBookCategory var5 = var1.readEnum(CraftingBookCategory.class);
         NonNullList var6 = NonNullList.withSize(var2 * var3, Ingredient.EMPTY);

         for(int var7 = 0; var7 < var6.size(); ++var7) {
            var6.set(var7, Ingredient.fromNetwork(var1));
         }

         ItemStack var9 = var1.readItem();
         boolean var8 = var1.readBoolean();
         return new ShapedRecipe(var4, var5, var2, var3, var6, var9, var8);
      }

      public void toNetwork(FriendlyByteBuf var1, ShapedRecipe var2) {
         var1.writeVarInt(var2.width);
         var1.writeVarInt(var2.height);
         var1.writeUtf(var2.group);
         var1.writeEnum(var2.category);

         for(Ingredient var4 : var2.recipeItems) {
            var4.toNetwork(var1);
         }

         var1.writeItem(var2.result);
         var1.writeBoolean(var2.showNotification);
      }

      static record RawShapedRecipe(String b, CraftingBookCategory c, Map<String, Ingredient> d, List<String> e, ItemStack f, boolean g) {
         final String group;
         final CraftingBookCategory category;
         final Map<String, Ingredient> key;
         final List<String> pattern;
         final ItemStack result;
         final boolean showNotification;
         public static final Codec<ShapedRecipe.Serializer.RawShapedRecipe> CODEC = RecordCodecBuilder.create(
            var0 -> var0.group(
                     ExtraCodecs.strictOptionalField(Codec.STRING, "group", "").forGetter(var0x -> var0x.group),
                     CraftingBookCategory.CODEC.fieldOf("category").orElse(CraftingBookCategory.MISC).forGetter(var0x -> var0x.category),
                     ExtraCodecs.strictUnboundedMap(ShapedRecipe.Serializer.SINGLE_CHARACTER_STRING_CODEC, Ingredient.CODEC_NONEMPTY)
                        .fieldOf("key")
                        .forGetter(var0x -> var0x.key),
                     ShapedRecipe.Serializer.PATTERN_CODEC.fieldOf("pattern").forGetter(var0x -> var0x.pattern),
                     CraftingRecipeCodecs.ITEMSTACK_OBJECT_CODEC.fieldOf("result").forGetter(var0x -> var0x.result),
                     ExtraCodecs.strictOptionalField(Codec.BOOL, "show_notification", true).forGetter(var0x -> var0x.showNotification)
                  )
                  .apply(var0, ShapedRecipe.Serializer.RawShapedRecipe::new)
         );

         private RawShapedRecipe(String var1, CraftingBookCategory var2, Map<String, Ingredient> var3, List<String> var4, ItemStack var5, boolean var6) {
            super();
            this.group = var1;
            this.category = var2;
            this.key = var3;
            this.pattern = var4;
            this.result = var5;
            this.showNotification = var6;
         }
      }
   }
}
