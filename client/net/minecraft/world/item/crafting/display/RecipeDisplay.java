package net.minecraft.world.item.crafting.display;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.flag.FeatureFlagSet;

public interface RecipeDisplay {
   Codec<RecipeDisplay> CODEC = BuiltInRegistries.RECIPE_DISPLAY.byNameCodec().dispatch(RecipeDisplay::type, Type::codec);
   StreamCodec<RegistryFriendlyByteBuf, RecipeDisplay> STREAM_CODEC = ByteBufCodecs.registry(Registries.RECIPE_DISPLAY).dispatch(RecipeDisplay::type, Type::streamCodec);

   SlotDisplay result();

   SlotDisplay craftingStation();

   Type<? extends RecipeDisplay> type();

   default boolean isEnabled(FeatureFlagSet var1) {
      return this.result().isEnabled(var1) && this.craftingStation().isEnabled(var1);
   }

   public static record Type<T extends RecipeDisplay>(MapCodec<T> codec, StreamCodec<RegistryFriendlyByteBuf, T> streamCodec) {
      public Type(MapCodec<T> var1, StreamCodec<RegistryFriendlyByteBuf, T> var2) {
         super();
         this.codec = var1;
         this.streamCodec = var2;
      }

      public MapCodec<T> codec() {
         return this.codec;
      }

      public StreamCodec<RegistryFriendlyByteBuf, T> streamCodec() {
         return this.streamCodec;
      }
   }
}
