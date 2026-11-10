package net.minecraft.commands.suggestions;

import com.mojang.serialization.MapCodec;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;

public non-sealed interface SerializableCustomSuggestion extends CustomSuggestion {
   default SerializableCustomSuggestion serializable() {
      return this;
   }

   Type<? extends SerializableCustomSuggestion> type();

   static <A extends SerializableCustomSuggestion> Type<A> singletonType(final A instance) {
      return new Type<A>(MapCodec.unit(instance), StreamCodec.unit(instance));
   }

   public static record Type<A extends SerializableCustomSuggestion>(MapCodec<A> codec, StreamCodec<? super RegistryFriendlyByteBuf, A> streamCodec) {
      public Type {
         super();
      }
   }
}
