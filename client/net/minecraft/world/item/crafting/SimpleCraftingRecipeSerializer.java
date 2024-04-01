package net.minecraft.world.item.crafting;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.mojang.serialization.codecs.RecordCodecBuilder.Instance;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;

public class SimpleCraftingRecipeSerializer<T extends CraftingRecipe> implements RecipeSerializer<T> {
   private final SimpleCraftingRecipeSerializer.Factory<T> constructor;
   private final Codec<T> codec;
   private final StreamCodec<RegistryFriendlyByteBuf, T> streamCodec;

   public SimpleCraftingRecipeSerializer(SimpleCraftingRecipeSerializer.Factory<T> var1) {
      super();
      this.constructor = var1;
      this.codec = RecordCodecBuilder.create(
         var1x -> var1x.group(CraftingBookCategory.CODEC.fieldOf("category").orElse(CraftingBookCategory.MISC).forGetter(CraftingRecipe::category))
               .apply(var1x, var1::create)
      );
      this.streamCodec = StreamCodec.composite(CraftingBookCategory.STREAM_CODEC, CraftingRecipe::category, var1::create);
   }

   @Override
   public Codec<T> codec() {
      return this.codec;
   }

   @Override
   public StreamCodec<RegistryFriendlyByteBuf, T> streamCodec() {
      return this.streamCodec;
   }

   @FunctionalInterface
   public interface Factory<T extends CraftingRecipe> {
      T create(CraftingBookCategory var1);
   }
}
