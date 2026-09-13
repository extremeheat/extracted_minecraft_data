package net.minecraft.world.gen.structure;

import java.util.Random;
import net.minecraft.init.Blocks;

class ComponentScatteredFeaturePieces$JunglePyramid$Stones extends StructureComponent$BlockSelector {
   private ComponentScatteredFeaturePieces$JunglePyramid$Stones() {
      super();
   }

   @Override
   public void func_75062_a(Random var1, int var2, int var3, int var4, boolean var5) {
      if (var1.nextFloat() < 0.4F) {
         this.field_151562_a = Blocks.field_150347_e;
      } else {
         this.field_151562_a = Blocks.field_150341_Y;
      }
   }
}
