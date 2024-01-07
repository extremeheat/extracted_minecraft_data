package net.minecraft.world.item.crafting;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.mojang.serialization.codecs.RecordCodecBuilder.Instance;
import net.minecraft.network.FriendlyByteBuf;

public class SimpleCraftingRecipeSerializer<T extends CraftingRecipe> implements RecipeSerializer<T> {
   private final SimpleCraftingRecipeSerializer.Factory<T> constructor;
   private final Codec<T> codec;

   public SimpleCraftingRecipeSerializer(SimpleCraftingRecipeSerializer.Factory<T> var1) {
      super();
      this.constructor = var1;
      this.codec = RecordCodecBuilder.create(
         var1x -> var1x.group(CraftingBookCategory.CODEC.fieldOf("category").orElse(CraftingBookCategory.MISC).forGetter(CraftingRecipe::category))
               .apply(var1x, var1::create)
      );
   }

   @Override
   public Codec<T> codec() {
      return this.codec;
   }

   public T fromNetwork(FriendlyByteBuf var1) {
      CraftingBookCategory var2 = var1.readEnum(CraftingBookCategory.class);
      return this.constructor.create(var2);
   }

   public void toNetwork(FriendlyByteBuf var1, T var2) {
      var1.writeEnum(var2.category());
   }

   @FunctionalInterface
   public interface Factory<T extends CraftingRecipe> {
      T create(CraftingBookCategory var1);
   }
}
