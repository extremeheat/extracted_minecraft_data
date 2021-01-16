package net.minecraft.world.level.newbiome.layer.traits;

public interface DimensionOffset1Transformer extends DimensionTransformer {
   default int getParentX(int var1) {
      return var1 - 1;
   }

   default int getParentY(int var1) {
      return var1 - 1;
   }
}
