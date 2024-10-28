package net.minecraft.world.item.crafting;

import com.mojang.datafixers.Products;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.Objects;
import java.util.function.Function;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.NonNullList;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.ItemStack;

public abstract class SingleItemRecipe implements Recipe<SingleRecipeInput> {
   protected final Ingredient ingredient;
   protected final ItemStack result;
   private final RecipeType<?> type;
   private final RecipeSerializer<?> serializer;
   protected final String group;

   public SingleItemRecipe(RecipeType<?> var1, RecipeSerializer<?> var2, String var3, Ingredient var4, ItemStack var5) {
      super();
      this.type = var1;
      this.serializer = var2;
      this.group = var3;
      this.ingredient = var4;
      this.result = var5;
   }

   public RecipeType<?> getType() {
      return this.type;
   }

   public RecipeSerializer<?> getSerializer() {
      return this.serializer;
   }

   public String getGroup() {
      return this.group;
   }

   public ItemStack getResultItem(HolderLookup.Provider var1) {
      return this.result;
   }

   public NonNullList<Ingredient> getIngredients() {
      NonNullList var1 = NonNullList.create();
      var1.add(this.ingredient);
      return var1;
   }

   public boolean canCraftInDimensions(int var1, int var2) {
      return true;
   }

   public ItemStack assemble(SingleRecipeInput var1, HolderLookup.Provider var2) {
      return this.result.copy();
   }

   public interface Factory<T extends SingleItemRecipe> {
      T create(String var1, Ingredient var2, ItemStack var3);
   }

   public static class Serializer<T extends SingleItemRecipe> implements RecipeSerializer<T> {
      final Factory<T> factory;
      private final MapCodec<T> codec;
      private final StreamCodec<RegistryFriendlyByteBuf, T> streamCodec;

      protected Serializer(Factory<T> var1) {
         super();
         this.factory = var1;
         this.codec = RecordCodecBuilder.mapCodec((var1x) -> {
            Products.P3 var10000 = var1x.group(Codec.STRING.optionalFieldOf("group", "").forGetter((var0) -> {
               return var0.group;
            }), Ingredient.CODEC_NONEMPTY.fieldOf("ingredient").forGetter((var0) -> {
               return var0.ingredient;
            }), ItemStack.STRICT_CODEC.fieldOf("result").forGetter((var0) -> {
               return var0.result;
            }));
            Objects.requireNonNull(var1);
            return var10000.apply(var1x, var1::create);
         });
         StreamCodec var10001 = ByteBufCodecs.STRING_UTF8;
         Function var10002 = (var0) -> {
            return var0.group;
         };
         StreamCodec var10003 = Ingredient.CONTENTS_STREAM_CODEC;
         Function var10004 = (var0) -> {
            return var0.ingredient;
         };
         StreamCodec var10005 = ItemStack.STREAM_CODEC;
         Function var10006 = (var0) -> {
            return var0.result;
         };
         Objects.requireNonNull(var1);
         this.streamCodec = StreamCodec.composite(var10001, var10002, var10003, var10004, var10005, var10006, var1::create);
      }

      public MapCodec<T> codec() {
         return this.codec;
      }

      public StreamCodec<RegistryFriendlyByteBuf, T> streamCodec() {
         return this.streamCodec;
      }
   }
}
