package net.minecraft.world.item.crafting;

import com.mojang.datafixers.Products;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.Objects;
import java.util.function.Function;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;

public class SimpleCraftingRecipeSerializer<T extends CraftingRecipe> implements RecipeSerializer<T> {
   private final MapCodec<T> codec;
   private final StreamCodec<RegistryFriendlyByteBuf, T> streamCodec;

   public SimpleCraftingRecipeSerializer(Factory<T> var1) {
      super();
      this.codec = RecordCodecBuilder.mapCodec((var1x) -> {
         Products.P1 var10000 = var1x.group(CraftingBookCategory.CODEC.fieldOf("category").orElse(CraftingBookCategory.MISC).forGetter(CraftingRecipe::category));
         Objects.requireNonNull(var1);
         return var10000.apply(var1x, var1::create);
      });
      StreamCodec var10001 = CraftingBookCategory.STREAM_CODEC;
      Function var10002 = CraftingRecipe::category;
      Objects.requireNonNull(var1);
      this.streamCodec = StreamCodec.composite(var10001, var10002, var1::create);
   }

   public MapCodec<T> codec() {
      return this.codec;
   }

   public StreamCodec<RegistryFriendlyByteBuf, T> streamCodec() {
      return this.streamCodec;
   }

   @FunctionalInterface
   public interface Factory<T extends CraftingRecipe> {
      T create(CraftingBookCategory var1);
   }
}
