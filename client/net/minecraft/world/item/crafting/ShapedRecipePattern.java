package net.minecraft.world.item.crafting;

import com.google.common.annotations.VisibleForTesting;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import it.unimi.dsi.fastutil.chars.CharArraySet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import net.minecraft.Util;
import net.minecraft.core.NonNullList;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.util.ExtraCodecs;
import net.minecraft.world.item.ItemStack;

public final class ShapedRecipePattern {
   private static final int MAX_SIZE = 3;
   public static final MapCodec<ShapedRecipePattern> MAP_CODEC = ShapedRecipePattern.Data.MAP_CODEC
      .flatXmap(
         ShapedRecipePattern::unpack,
         var0 -> var0.data.<DataResult>map(DataResult::success).orElseGet(() -> DataResult.error(() -> "Cannot encode unpacked recipe"))
      );
   public static final StreamCodec<RegistryFriendlyByteBuf, ShapedRecipePattern> STREAM_CODEC = StreamCodec.ofMember(
      ShapedRecipePattern::toNetwork, ShapedRecipePattern::fromNetwork
   );
   private final int width;
   private final int height;
   private final NonNullList<Ingredient> ingredients;
   private final Optional<ShapedRecipePattern.Data> data;
   private final int ingredientCount;
   private final boolean symmetrical;

   public ShapedRecipePattern(int var1, int var2, NonNullList<Ingredient> var3, Optional<ShapedRecipePattern.Data> var4) {
      super();
      this.width = var1;
      this.height = var2;
      this.ingredients = var3;
      this.data = var4;
      int var5 = 0;

      for (Ingredient var7 : var3) {
         if (!var7.isEmpty()) {
            var5++;
         }
      }

      this.ingredientCount = var5;
      this.symmetrical = Util.isSymmetrical(var1, var2, var3);
   }

   public static ShapedRecipePattern of(Map<Character, Ingredient> var0, String... var1) {
      return of(var0, List.of(var1));
   }

   public static ShapedRecipePattern of(Map<Character, Ingredient> var0, List<String> var1) {
      ShapedRecipePattern.Data var2 = new ShapedRecipePattern.Data(var0, var1);
      return (ShapedRecipePattern)unpack(var2).getOrThrow();
   }

   private static DataResult<ShapedRecipePattern> unpack(ShapedRecipePattern.Data var0) {
      String[] var1 = shrink(var0.pattern);
      int var2 = var1[0].length();
      int var3 = var1.length;
      NonNullList var4 = NonNullList.withSize(var2 * var3, Ingredient.EMPTY);
      CharArraySet var5 = new CharArraySet(var0.key.keySet());

      for (int var6 = 0; var6 < var1.length; var6++) {
         String var7 = var1[var6];

         for (int var8 = 0; var8 < var7.length(); var8++) {
            char var9 = var7.charAt(var8);
            Ingredient var10 = var9 == ' ' ? Ingredient.EMPTY : var0.key.get(var9);
            if (var10 == null) {
               return DataResult.error(() -> "Pattern references symbol '" + var9 + "' but it's not defined in the key");
            }

            var5.remove(var9);
            var4.set(var8 + var2 * var6, var10);
         }
      }

      return !var5.isEmpty()
         ? DataResult.error(() -> "Key defines symbols that aren't used in pattern: " + var5)
         : DataResult.success(new ShapedRecipePattern(var2, var3, var4, Optional.of(var0)));
   }

   @VisibleForTesting
   static String[] shrink(List<String> var0) {
      int var1 = 2147483647;
      int var2 = 0;
      int var3 = 0;
      int var4 = 0;

      for (int var5 = 0; var5 < var0.size(); var5++) {
         String var6 = (String)var0.get(var5);
         var1 = Math.min(var1, firstNonSpace(var6));
         int var7 = lastNonSpace(var6);
         var2 = Math.max(var2, var7);
         if (var7 < 0) {
            if (var3 == var5) {
               var3++;
            }

            var4++;
         } else {
            var4 = 0;
         }
      }

      if (var0.size() == var4) {
         return new String[0];
      } else {
         String[] var8 = new String[var0.size() - var4 - var3];

         for (int var9 = 0; var9 < var8.length; var9++) {
            var8[var9] = ((String)var0.get(var9 + var3)).substring(var1, var2 + 1);
         }

         return var8;
      }
   }

   private static int firstNonSpace(String var0) {
      int var1 = 0;

      while (var1 < var0.length() && var0.charAt(var1) == ' ') {
         var1++;
      }

      return var1;
   }

   private static int lastNonSpace(String var0) {
      int var1 = var0.length() - 1;

      while (var1 >= 0 && var0.charAt(var1) == ' ') {
         var1--;
      }

      return var1;
   }

   public boolean matches(CraftingInput var1) {
      if (var1.ingredientCount() != this.ingredientCount) {
         return false;
      } else {
         if (var1.width() == this.width && var1.height() == this.height) {
            if (!this.symmetrical && this.matches(var1, true)) {
               return true;
            }

            if (this.matches(var1, false)) {
               return true;
            }
         }

         return false;
      }
   }

   private boolean matches(CraftingInput var1, boolean var2) {
      for (int var3 = 0; var3 < this.height; var3++) {
         for (int var4 = 0; var4 < this.width; var4++) {
            Ingredient var5;
            if (var2) {
               var5 = this.ingredients.get(this.width - var4 - 1 + var3 * this.width);
            } else {
               var5 = this.ingredients.get(var4 + var3 * this.width);
            }

            ItemStack var6 = var1.getItem(var4, var3);
            if (!var5.test(var6)) {
               return false;
            }
         }
      }

      return true;
   }

   private void toNetwork(RegistryFriendlyByteBuf var1) {
      var1.writeVarInt(this.width);
      var1.writeVarInt(this.height);

      for (Ingredient var3 : this.ingredients) {
         Ingredient.CONTENTS_STREAM_CODEC.encode(var1, var3);
      }
   }

   private static ShapedRecipePattern fromNetwork(RegistryFriendlyByteBuf var0) {
      int var1 = var0.readVarInt();
      int var2 = var0.readVarInt();
      NonNullList var3 = NonNullList.withSize(var1 * var2, Ingredient.EMPTY);
      var3.replaceAll(var1x -> Ingredient.CONTENTS_STREAM_CODEC.decode(var0));
      return new ShapedRecipePattern(var1, var2, var3, Optional.empty());
   }

   public int width() {
      return this.width;
   }

   public int height() {
      return this.height;
   }

   public NonNullList<Ingredient> ingredients() {
      return this.ingredients;
   }

   public static record Data(Map<Character, Ingredient> key, List<String> pattern) {
      private static final Codec<List<String>> PATTERN_CODEC = Codec.STRING.listOf().comapFlatMap(var0 -> {
         if (var0.size() > 3) {
            return DataResult.error(() -> "Invalid pattern: too many rows, 3 is maximum");
         } else if (var0.isEmpty()) {
            return DataResult.error(() -> "Invalid pattern: empty pattern not allowed");
         } else {
            int var1 = ((String)var0.getFirst()).length();

            for (String var3 : var0) {
               if (var3.length() > 3) {
                  return DataResult.error(() -> "Invalid pattern: too many columns, 3 is maximum");
               }

               if (var1 != var3.length()) {
                  return DataResult.error(() -> "Invalid pattern: each row must be the same width");
               }
            }

            return DataResult.success(var0);
         }
      }, Function.identity());
      private static final Codec<Character> SYMBOL_CODEC = Codec.STRING.comapFlatMap(var0 -> {
         if (var0.length() != 1) {
            return DataResult.error(() -> "Invalid key entry: '" + var0 + "' is an invalid symbol (must be 1 character only).");
         } else {
            return " ".equals(var0) ? DataResult.error(() -> "Invalid key entry: ' ' is a reserved symbol.") : DataResult.success(var0.charAt(0));
         }
      }, String::valueOf);
      public static final MapCodec<ShapedRecipePattern.Data> MAP_CODEC = RecordCodecBuilder.mapCodec(
         var0 -> var0.group(
                  ExtraCodecs.strictUnboundedMap(SYMBOL_CODEC, Ingredient.CODEC_NONEMPTY).fieldOf("key").forGetter(var0x -> var0x.key),
                  PATTERN_CODEC.fieldOf("pattern").forGetter(var0x -> var0x.pattern)
               )
               .apply(var0, ShapedRecipePattern.Data::new)
      );

      public Data(Map<Character, Ingredient> key, List<String> pattern) {
         super();
         this.key = key;
         this.pattern = pattern;
      }
   }
}
