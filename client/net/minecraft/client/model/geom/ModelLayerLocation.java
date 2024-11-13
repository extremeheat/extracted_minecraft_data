package net.minecraft.client.model.geom;

import net.minecraft.resources.ResourceLocation;

public record ModelLayerLocation(ResourceLocation model, String layer) {
   public ModelLayerLocation(ResourceLocation var1, String var2) {
      super();
      this.model = var1;
      this.layer = var2;
   }

   public String toString() {
      String var10000 = String.valueOf(this.model);
      return var10000 + "#" + this.layer;
   }
}
