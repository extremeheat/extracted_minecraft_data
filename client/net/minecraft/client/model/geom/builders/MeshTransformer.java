package net.minecraft.client.model.geom.builders;

@FunctionalInterface
public interface MeshTransformer {
   static MeshTransformer scaling(float var0) {
      float var1 = 24.016F * (1.0F - var0);
      return (var2) -> {
         return var2.transformed((var2x) -> {
            return var2x.scaled(var0).translated(0.0F, var1, 0.0F);
         });
      };
   }

   MeshDefinition apply(MeshDefinition var1);
}
