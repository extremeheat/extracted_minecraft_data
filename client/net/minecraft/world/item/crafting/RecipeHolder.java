package net.minecraft.world.item.crafting;

import net.minecraft.core.registries.Registries;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceKey;

public record RecipeHolder<T extends Recipe<?>>(ResourceKey<Recipe<?>> id, T value) {
   public static final StreamCodec<RegistryFriendlyByteBuf, RecipeHolder<?>> STREAM_CODEC;

   public RecipeHolder(ResourceKey<Recipe<?>> var1, T var2) {
      super();
      this.id = var1;
      this.value = var2;
   }

   public boolean equals(Object var1) {
      if (this == var1) {
         return true;
      } else {
         boolean var10000;
         if (var1 instanceof RecipeHolder) {
            RecipeHolder var2 = (RecipeHolder)var1;
            if (this.id == var2.id) {
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

   public ResourceKey<Recipe<?>> id() {
      return this.id;
   }

   public T value() {
      return this.value;
   }

   static {
      STREAM_CODEC = StreamCodec.composite(ResourceKey.streamCodec(Registries.RECIPE), RecipeHolder::id, Recipe.STREAM_CODEC, RecipeHolder::value, RecipeHolder::new);
   }
}
