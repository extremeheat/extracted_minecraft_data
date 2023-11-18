package net.minecraft.world.item.crafting;

import net.minecraft.resources.ResourceLocation;

public record RecipeHolder<T extends Recipe<?>>(ResourceLocation a, T b) {
   private final ResourceLocation id;
   private final T value;

   public RecipeHolder(ResourceLocation var1, T var2) {
      super();
      this.id = var1;
      this.value = var2;
   }

   @Override
   public boolean equals(Object var1) {
      if (this == var1) {
         return true;
      } else {
         if (var1 instanceof RecipeHolder var2 && this.id.equals(var2.id)) {
            return true;
         }

         return false;
      }
   }

   @Override
   public int hashCode() {
      return this.id.hashCode();
   }

   @Override
   public String toString() {
      return this.id.toString();
   }
}
