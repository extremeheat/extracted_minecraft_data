package net.minecraft.world.item.crafting;

import com.google.common.annotations.VisibleForTesting;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.MapCodec;
import it.unimi.dsi.fastutil.chars.CharArraySet;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import net.minecraft.Util;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.ItemStack;

public final class ShapedRecipePattern {
   private static final int MAX_SIZE = 3;
   public static final char EMPTY_SLOT = ' ';
   public static final MapCodec<ShapedRecipePattern> MAP_CODEC = ShapedRecipePattern.Data.MAP_CODEC
      .flatXmap(
         ShapedRecipePattern::unpack,
         var0 -> var0.data.<DataResult>map(DataResult::success).orElseGet(() -> DataResult.error(() -> "Cannot encode unpacked recipe"))
      );
   public static final StreamCodec<RegistryFriendlyByteBuf, ShapedRecipePattern> STREAM_CODEC = StreamCodec.composite(
      ByteBufCodecs.VAR_INT,
      var0 -> var0.width,
      ByteBufCodecs.VAR_INT,
      var0 -> var0.height,
      Ingredient.OPTIONAL_CONTENTS_STREAM_CODEC.apply(ByteBufCodecs.list()),
      var0 -> var0.ingredients,
      ShapedRecipePattern::createFromNetwork
   );
   private final int width;
   private final int height;
   private final List<Optional<Ingredient>> ingredients;
   private final Optional<ShapedRecipePattern.Data> data;
   private final int ingredientCount;
   private final boolean symmetrical;

   public ShapedRecipePattern(int var1, int var2, List<Optional<Ingredient>> var3, Optional<ShapedRecipePattern.Data> var4) {
      super();
      this.width = var1;
      this.height = var2;
      this.ingredients = var3;
      this.data = var4;
      this.ingredientCount = (int)var3.stream().flatMap(Optional::stream).count();
      this.symmetrical = Util.isSymmetrical(var1, var2, var3);
   }

   private static ShapedRecipePattern createFromNetwork(Integer var0, Integer var1, List<Optional<Ingredient>> var2) {
      return new ShapedRecipePattern(var0, var1, var2, Optional.empty());
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
      ArrayList var4 = new ArrayList(var2 * var3);
      CharArraySet var5 = new CharArraySet(var0.key.keySet());

      for (String var9 : var1) {
         for (int var10 = 0; var10 < var9.length(); var10++) {
            char var11 = var9.charAt(var10);
            Optional var12;
            if (var11 == ' ') {
               var12 = Optional.empty();
            } else {
               Ingredient var13 = var0.key.get(var11);
               if (var13 == null) {
                  return DataResult.error(() -> "Pattern references symbol '" + var11 + "' but it's not defined in the key");
               }

               var12 = Optional.of(var13);
            }

            var5.remove(var11);
            var4.add(var12);
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
         var1 = Math.min(var1, firstNonEmpty(var6));
         int var7 = lastNonEmpty(var6);
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

   private static int firstNonEmpty(String var0) {
      int var1 = 0;

      while (var1 < var0.length() && var0.charAt(var1) == ' ') {
         var1++;
      }

      return var1;
   }

   private static int lastNonEmpty(String var0) {
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
            Optional var5;
            if (var2) {
               var5 = this.ingredients.get(this.width - var4 - 1 + var3 * this.width);
            } else {
               var5 = this.ingredients.get(var4 + var3 * this.width);
            }

            ItemStack var6 = var1.getItem(var4, var3);
            if (!Ingredient.testOptionalIngredient(var5, var6)) {
               return false;
            }
         }
      }

      return true;
   }

   public int width() {
      return this.width;
   }

   public int height() {
      return this.height;
   }

   public List<Optional<Ingredient>> ingredients() {
      return this.ingredients;
   }

// $VF: Couldn't be decompiled
// Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
// java.lang.NullPointerException
//   at org.jetbrains.java.decompiler.main.InitializerProcessor.isExprentIndependent(InitializerProcessor.java:423)
//   at org.jetbrains.java.decompiler.main.InitializerProcessor.extractDynamicInitializers(InitializerProcessor.java:335)
//   at org.jetbrains.java.decompiler.main.InitializerProcessor.extractInitializers(InitializerProcessor.java:44)
//   at org.jetbrains.java.decompiler.main.ClassWriter.invokeProcessors(ClassWriter.java:97)
//   at org.jetbrains.java.decompiler.main.ClassWriter.writeClass(ClassWriter.java:348)
//   at org.jetbrains.java.decompiler.main.ClassWriter.writeClass(ClassWriter.java:492)
//   at org.jetbrains.java.decompiler.main.ClassesProcessor.writeClass(ClassesProcessor.java:474)
//   at org.jetbrains.java.decompiler.main.Fernflower.getClassContent(Fernflower.java:191)
//   at org.jetbrains.java.decompiler.struct.ContextUnit.lambda$save$3(ContextUnit.java:187)
}