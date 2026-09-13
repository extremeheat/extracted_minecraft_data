package net.minecraft.world.gen.structure;

import java.util.List;
import java.util.Random;
import net.minecraft.init.Blocks;
import net.minecraft.world.World;

public class StructureStrongholdPieces$RightTurn extends StructureStrongholdPieces$LeftTurn {
   public StructureStrongholdPieces$RightTurn() {
      super();
   }

   @Override
   public void func_74861_a(StructureComponent var1, List var2, Random var3) {
      if (this.field_74885_f != 2 && this.field_74885_f != 3) {
         this.func_74989_b((StructureStrongholdPieces$Stairs2)var1, var2, var3, 1, 1);
      } else {
         this.func_74987_c((StructureStrongholdPieces$Stairs2)var1, var2, var3, 1, 1);
      }
   }

   @Override
   public boolean func_74875_a(World var1, Random var2, StructureBoundingBox var3) {
      if (this.func_74860_a(var1, var3)) {
         return false;
      } else {
         this.func_74882_a(var1, var3, 0, 0, 0, 4, 4, 4, true, var2, StructureStrongholdPieces.access$200());
         this.func_74990_a(var1, var2, var3, this.field_143013_d, 1, 1, 0);
         if (this.field_74885_f != 2 && this.field_74885_f != 3) {
            this.func_151549_a(var1, var3, 0, 1, 1, 0, 3, 3, Blocks.field_150350_a, Blocks.field_150350_a, false);
         } else {
            this.func_151549_a(var1, var3, 4, 1, 1, 4, 3, 3, Blocks.field_150350_a, Blocks.field_150350_a, false);
         }

         return true;
      }
   }
}
