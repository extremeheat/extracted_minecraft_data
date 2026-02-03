package net.minecraft.world.item.crafting;

import net.minecraft.core.registries.Registries;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceKey;

public record RecipeHolder<T extends Recipe<?>>(ResourceKey<Recipe<?>> id, T value) {
   public static final StreamCodec<RegistryFriendlyByteBuf, RecipeHolder<?>> STREAM_CODEC;

   public RecipeHolder {
      super();
   }

   public boolean equals(final Object obj) {
      if (this == obj) {
         return true;
      } else {
         boolean var10000;
         if (obj instanceof RecipeHolder) {
            RecipeHolder<?> holder = (RecipeHolder)obj;
            if (this.id == holder.id) {
               var10000 = true;
               return var10000;
            }
         }

         var10000 = false;
         return var10000;
      }
   }

   public int hashCode() {
      return this.id.hashCode();
   }

   public String toString() {
      return this.id.toString();
   }

   static {
      STREAM_CODEC = StreamCodec.composite(ResourceKey.streamCodec(Registries.RECIPE), RecipeHolder::id, Recipe.STREAM_CODEC, RecipeHolder::value, RecipeHolder::new);
   }
}
