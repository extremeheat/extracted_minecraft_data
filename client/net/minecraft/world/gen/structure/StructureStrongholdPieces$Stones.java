package net.minecraft.world.gen.structure;

import java.util.Random;
import net.minecraft.init.Blocks;

class StructureStrongholdPieces$Stones extends StructureComponent$BlockSelector {
   private StructureStrongholdPieces$Stones() {
      super();
   }

   @Override
   public void func_75062_a(Random var1, int var2, int var3, int var4, boolean var5) {
      if (var5) {
         this.field_151562_a = Blocks.field_150417_aV;
         float var6 = var1.nextFloat();
         if (var6 < 0.2F) {
            this.field_75065_b = 2;
         } else if (var6 < 0.5F) {
            this.field_75065_b = 1;
         } else if (var6 < 0.55F) {
            this.field_151562_a = Blocks.field_150418_aU;
            this.field_75065_b = 2;
         } else {
            this.field_75065_b = 0;
         }
      } else {
         this.field_151562_a = Blocks.field_150350_a;
         this.field_75065_b = 0;
      }
   }
}
