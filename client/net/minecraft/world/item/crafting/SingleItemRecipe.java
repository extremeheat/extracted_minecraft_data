package net.minecraft.world.item.crafting;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
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

   @Override
   public abstract RecipeSerializer<? extends SingleItemRecipe> getSerializer();

   @Override
   public abstract RecipeType<? extends SingleItemRecipe> getType();

   public boolean matches(SingleRecipeInput var1, Level var2) {
      return this.input.test(var1.item());
   }

   @Override
   public String group() {
      return this.group;
   }

   public Ingredient input() {
      return this.input;
   }

   protected ItemStack result() {
      return this.result;
   }

   @Override
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

      protected Serializer(SingleItemRecipe.Factory<T> var1) {
         super();
         this.codec = RecordCodecBuilder.mapCodec(
            var1x -> var1x.group(
                     Codec.STRING.optionalFieldOf("group", "").forGetter(SingleItemRecipe::group),
                     Ingredient.CODEC.fieldOf("ingredient").forGetter(SingleItemRecipe::input),
                     ItemStack.STRICT_CODEC.fieldOf("result").forGetter(SingleItemRecipe::result)
                  )
                  .apply(var1x, var1::create)
         );
         this.streamCodec = StreamCodec.composite(
            ByteBufCodecs.STRING_UTF8,
            SingleItemRecipe::group,
            Ingredient.CONTENTS_STREAM_CODEC,
            SingleItemRecipe::input,
            ItemStack.STREAM_CODEC,
            SingleItemRecipe::result,
            var1::create
         );
      }

      @Override
      public MapCodec<T> codec() {
         return this.codec;
      }

      @Override
      public StreamCodec<RegistryFriendlyByteBuf, T> streamCodec() {
         return this.streamCodec;
      }
   }
}
