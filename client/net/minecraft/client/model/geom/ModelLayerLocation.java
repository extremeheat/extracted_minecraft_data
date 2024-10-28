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

   public int hashCode() {
      int var1 = this.model.hashCode();
      var1 = 31 * var1 + this.layer.hashCode();
      return var1;
   }

   public String toString() {
      String var10000 = String.valueOf(this.model);
      return var10000 + "#" + this.layer;
   }
}
