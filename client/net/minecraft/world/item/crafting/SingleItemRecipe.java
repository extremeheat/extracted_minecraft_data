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

public abstract class SingleItemRecipe implements Recipe<SingleRecipeInput> {
   protected final Ingredient ingredient;
   protected final ItemStack result;
   private final RecipeType<?> type;
   private final RecipeSerializer<?> serializer;
   protected final String group;
   @Nullable
   private PlacementInfo placementInfo;

   public SingleItemRecipe(RecipeType<?> var1, RecipeSerializer<?> var2, String var3, Ingredient var4, ItemStack var5) {
      super();
      this.type = var1;
      this.serializer = var2;
      this.group = var3;
      this.ingredient = var4;
      this.result = var5;
   }

   @Override
   public RecipeType<?> getType() {
      return this.type;
   }

   @Override
   public RecipeSerializer<?> getSerializer() {
      return this.serializer;
   }

   @Override
   public String getGroup() {
      return this.group;
   }

   @Override
   public ItemStack getResultItem(HolderLookup.Provider var1) {
      return this.result;
   }

   @Override
   public PlacementInfo placementInfo() {
      if (this.placementInfo == null) {
         this.placementInfo = PlacementInfo.create(this.ingredient);
      }

      return this.placementInfo;
   }

   @Override
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
      final SingleItemRecipe.Factory<T> factory;
      private final MapCodec<T> codec;
      private final StreamCodec<RegistryFriendlyByteBuf, T> streamCodec;

      protected Serializer(SingleItemRecipe.Factory<T> var1) {
         super();
         this.factory = var1;
         this.codec = RecordCodecBuilder.mapCodec(
            var1x -> var1x.group(
                     Codec.STRING.optionalFieldOf("group", "").forGetter(var0x -> var0x.group),
                     Ingredient.CODEC.fieldOf("ingredient").forGetter(var0x -> var0x.ingredient),
                     ItemStack.STRICT_CODEC.fieldOf("result").forGetter(var0x -> var0x.result)
                  )
                  .apply(var1x, var1::create)
         );
         this.streamCodec = StreamCodec.composite(
            ByteBufCodecs.STRING_UTF8,
            var0 -> var0.group,
            Ingredient.CONTENTS_STREAM_CODEC,
            var0 -> var0.ingredient,
            ItemStack.STREAM_CODEC,
            var0 -> var0.result,
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
