package net.minecraft.world.item.crafting;

import com.mojang.datafixers.Products;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.Objects;
import java.util.function.Function;
import javax.annotation.Nullable;
import net.minecraft.core.HolderLookup;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

public abstract class SingleItemRecipe implements Recipe<SingleRecipeInput> {
   private final Ingredient input;
   private final ItemStack result;
   private final String group;
   @Nullable
   private PlacementInfo placementInfo;

   public SingleItemRecipe(String var1, Ingredient var2, ItemStack var3) {
      super();
      this.group = var1;
      this.input = var2;
      this.result = var3;
   }

   public abstract RecipeSerializer<? extends SingleItemRecipe> getSerializer();

   public abstract RecipeType<? extends SingleItemRecipe> getType();

   public boolean matches(SingleRecipeInput var1, Level var2) {
      return this.input.test(var1.item());
   }

   public String group() {
      return this.group;
   }

   public Ingredient input() {
      return this.input;
   }

   protected ItemStack result() {
      return this.result;
   }

   public PlacementInfo placementInfo() {
      if (this.placementInfo == null) {
         this.placementInfo = PlacementInfo.create(this.input);
      }

      return this.placementInfo;
   }

   public ItemStack assemble(SingleRecipeInput var1, HolderLookup.Provider var2) {
      return this.result.copy();
   }

   @FunctionalInterface
   public interface Factory<T extends SingleItemRecipe> {
      T create(String var1, Ingredient var2, ItemStack var3);
   }

   public static class Serializer<T extends SingleItemRecipe> implements RecipeSerializer<T> {
      private final MapCodec<T> codec;
      private final StreamCodec<RegistryFriendlyByteBuf, T> streamCodec;

      protected Serializer(Factory<T> var1) {
         super();
         this.codec = RecordCodecBuilder.mapCodec((var1x) -> {
            Products.P3 var10000 = var1x.group(Codec.STRING.optionalFieldOf("group", "").forGetter(SingleItemRecipe::group), Ingredient.CODEC.fieldOf("ingredient").forGetter(SingleItemRecipe::input), ItemStack.STRICT_CODEC.fieldOf("result").forGetter(SingleItemRecipe::result));
            Objects.requireNonNull(var1);
            return var10000.apply(var1x, var1::create);
         });
         StreamCodec var10001 = ByteBufCodecs.STRING_UTF8;
         Function var10002 = SingleItemRecipe::group;
         StreamCodec var10003 = Ingredient.CONTENTS_STREAM_CODEC;
         Function var10004 = SingleItemRecipe::input;
         StreamCodec var10005 = ItemStack.STREAM_CODEC;
         Function var10006 = SingleItemRecipe::result;
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
