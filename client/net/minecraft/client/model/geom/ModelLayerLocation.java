package net.minecraft.client.model.geom;

import net.minecraft.resources.Identifier;

public record ModelLayerLocation(Identifier model, String layer) {
   public ModelLayerLocation(Identifier var1, String var2) {
      super();
      this.model = var1;
      this.layer = var2;
   }

   public String toString() {
      String var10000 = String.valueOf(this.model);
      return var10000 + "#" + this.layer;
   }
}
