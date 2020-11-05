package net.minecraft.world.level.newbiome.layer.traits;

public interface DimensionOffset0Transformer extends DimensionTransformer {
   default int getParentX(int var1) {
      return var1;
   }

   default int getParentY(int var1) {
      return var1;
   }
}
