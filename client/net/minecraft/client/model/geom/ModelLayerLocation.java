package net.minecraft.client.model.geom;

import net.minecraft.resources.ResourceLocation;

public final class ModelLayerLocation {
   private final ResourceLocation model;
   private final String layer;

   public ModelLayerLocation(ResourceLocation var1, String var2) {
      super();
      this.model = var1;
      this.layer = var2;
   }

   public ResourceLocation getModel() {
      return this.model;
   }

   public String getLayer() {
      return this.layer;
   }

   @Override
   public boolean equals(Object var1) {
      if (this == var1) {
         return true;
      } else if (!(var1 instanceof ModelLayerLocation)) {
         return false;
      } else {
         ModelLayerLocation var2 = (ModelLayerLocation)var1;
         return this.model.equals(var2.model) && this.layer.equals(var2.layer);
      }
   }

   @Override
   public int hashCode() {
      int var1 = this.model.hashCode();
      return 31 * var1 + this.layer.hashCode();
   }

   @Override
   public String toString() {
      return this.model + "#" + this.layer;
   }
}
