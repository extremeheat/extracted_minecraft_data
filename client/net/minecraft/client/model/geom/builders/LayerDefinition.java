package net.minecraft.client.model.geom.builders;

import net.minecraft.client.model.geom.ModelPart;

public class LayerDefinition {
   private final MeshDefinition mesh;
   private final MaterialDefinition material;

   private LayerDefinition(MeshDefinition var1, MaterialDefinition var2) {
      super();
      this.mesh = var1;
      this.material = var2;
   }

   public ModelPart bakeRoot() {
      return this.mesh.getRoot().bake(this.material.xTexSize, this.material.yTexSize);
   }

   public static LayerDefinition create(MeshDefinition var0, int var1, int var2) {
      return new LayerDefinition(var0, new MaterialDefinition(var1, var2));
   }
}
