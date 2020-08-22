package net.minecraft.world.level.levelgen.structure;

import net.minecraft.world.level.levelgen.feature.StructureFeature;

public abstract class BeardedStructureStart extends StructureStart {
   public BeardedStructureStart(StructureFeature var1, int var2, int var3, BoundingBox var4, int var5, long var6) {
      super(var1, var2, var3, var4, var5, var6);
   }

   protected void calculateBoundingBox() {
      super.calculateBoundingBox();
      boolean var1 = true;
      BoundingBox var10000 = this.boundingBox;
      var10000.x0 -= 12;
      var10000 = this.boundingBox;
      var10000.y0 -= 12;
      var10000 = this.boundingBox;
      var10000.z0 -= 12;
      var10000 = this.boundingBox;
      var10000.x1 += 12;
      var10000 = this.boundingBox;
      var10000.y1 += 12;
      var10000 = this.boundingBox;
      var10000.z1 += 12;
   }
}
