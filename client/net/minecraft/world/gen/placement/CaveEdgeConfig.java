package net.minecraft.world.gen.placement;

import net.minecraft.world.gen.GenerationStage;

public class CaveEdgeConfig implements IPlacementConfig {
   final GenerationStage.Carving field_206928_a;
   final float field_206929_b;

   public CaveEdgeConfig(GenerationStage.Carving var1, float var2) {
      super();
      this.field_206928_a = var1;
      this.field_206929_b = var2;
   }
}
