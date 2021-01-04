package net.minecraft.world.phys.shapes;

import it.unimi.dsi.fastutil.doubles.AbstractDoubleList;

public class CubePointRange extends AbstractDoubleList {
   private final int parts;

   CubePointRange(int var1) {
      super();
      this.parts = var1;
   }

   public double getDouble(int var1) {
      return (double)var1 / (double)this.parts;
   }

   public int size() {
      return this.parts + 1;
   }
}
