package net.minecraft.world.item.crafting;

import com.mojang.serialization.MapCodec;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;

public record RecipeSerializer<T extends Recipe<?>>(MapCodec<T> codec, StreamCodec<RegistryFriendlyByteBuf, T> streamCodec) {
   public RecipeSerializer(MapCodec<T> codec, @Deprecated StreamCodec<RegistryFriendlyByteBuf, T> streamCodec) {
      super();
      this.codec = codec;
      this.streamCodec = streamCodec;
   }

   /** @deprecated */
   @Deprecated
   public StreamCodec<RegistryFriendlyByteBuf, T> streamCodec() {
      return this.streamCodec;
   }
}
