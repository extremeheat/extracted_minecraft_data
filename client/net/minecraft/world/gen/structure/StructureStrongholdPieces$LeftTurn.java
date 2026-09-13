package net.minecraft.world.gen.structure;

import java.util.List;
import java.util.Random;
import net.minecraft.init.Blocks;
import net.minecraft.world.World;

public class StructureStrongholdPieces$LeftTurn extends StructureStrongholdPieces$Stronghold {
   public StructureStrongholdPieces$LeftTurn() {
      super();
   }

   public StructureStrongholdPieces$LeftTurn(int var1, Random var2, StructureBoundingBox var3, int var4) {
      super(var1);
      this.field_74885_f = var4;
      this.field_143013_d = this.func_74988_a(var2);
      this.field_74887_e = var3;
   }

   @Override
   public void func_74861_a(StructureComponent var1, List var2, Random var3) {
      if (this.field_74885_f != 2 && this.field_74885_f != 3) {
         this.func_74987_c((StructureStrongholdPieces$Stairs2)var1, var2, var3, 1, 1);
      } else {
         this.func_74989_b((StructureStrongholdPieces$Stairs2)var1, var2, var3, 1, 1);
      }
   }

   public static StructureStrongholdPieces$LeftTurn func_75010_a(List var0, Random var1, int var2, int var3, int var4, int var5, int var6) {
      StructureBoundingBox var7 = StructureBoundingBox.func_78889_a(var2, var3, var4, -1, -1, 0, 5, 5, 5, var5);
      return func_74991_a(var7) && StructureComponent.func_74883_a(var0, var7) == null ? new StructureStrongholdPieces$LeftTurn(var6, var1, var7, var5) : null;
   }

   @Override
   public boolean func_74875_a(World var1, Random var2, StructureBoundingBox var3) {
      if (this.func_74860_a(var1, var3)) {
         return false;
      } else {
         this.func_74882_a(var1, var3, 0, 0, 0, 4, 4, 4, true, var2, StructureStrongholdPieces.access$200());
         this.func_74990_a(var1, var2, var3, this.field_143013_d, 1, 1, 0);
         if (this.field_74885_f != 2 && this.field_74885_f != 3) {
            this.func_151549_a(var1, var3, 4, 1, 1, 4, 3, 3, Blocks.field_150350_a, Blocks.field_150350_a, false);
         } else {
            this.func_151549_a(var1, var3, 0, 1, 1, 0, 3, 3, Blocks.field_150350_a, Blocks.field_150350_a, false);
         }

         return true;
      }
   }
}
