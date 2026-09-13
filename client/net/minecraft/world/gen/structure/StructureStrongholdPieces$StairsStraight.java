package net.minecraft.world.gen.structure;

import java.util.List;
import java.util.Random;
import net.minecraft.init.Blocks;
import net.minecraft.world.World;

public class StructureStrongholdPieces$StairsStraight extends StructureStrongholdPieces$Stronghold {
   public StructureStrongholdPieces$StairsStraight() {
      super();
   }

   public StructureStrongholdPieces$StairsStraight(int var1, Random var2, StructureBoundingBox var3, int var4) {
      super(var1);
      this.field_74885_f = var4;
      this.field_143013_d = this.func_74988_a(var2);
      this.field_74887_e = var3;
   }

   @Override
   public void func_74861_a(StructureComponent var1, List var2, Random var3) {
      this.func_74986_a((StructureStrongholdPieces$Stairs2)var1, var2, var3, 1, 1);
   }

   public static StructureStrongholdPieces$StairsStraight func_75028_a(List var0, Random var1, int var2, int var3, int var4, int var5, int var6) {
      StructureBoundingBox var7 = StructureBoundingBox.func_78889_a(var2, var3, var4, -1, -7, 0, 5, 11, 8, var5);
      return func_74991_a(var7) && StructureComponent.func_74883_a(var0, var7) == null
         ? new StructureStrongholdPieces$StairsStraight(var6, var1, var7, var5)
         : null;
   }

   @Override
   public boolean func_74875_a(World var1, Random var2, StructureBoundingBox var3) {
      if (this.func_74860_a(var1, var3)) {
         return false;
      } else {
         this.func_74882_a(var1, var3, 0, 0, 0, 4, 10, 7, true, var2, StructureStrongholdPieces.access$200());
         this.func_74990_a(var1, var2, var3, this.field_143013_d, 1, 7, 0);
         this.func_74990_a(var1, var2, var3, StructureStrongholdPieces$Stronghold$Door.OPENING, 1, 1, 7);
         int var4 = this.func_151555_a(Blocks.field_150446_ar, 2);

         for(int var5 = 0; var5 < 6; ++var5) {
            this.func_151550_a(var1, Blocks.field_150446_ar, var4, 1, 6 - var5, 1 + var5, var3);
            this.func_151550_a(var1, Blocks.field_150446_ar, var4, 2, 6 - var5, 1 + var5, var3);
            this.func_151550_a(var1, Blocks.field_150446_ar, var4, 3, 6 - var5, 1 + var5, var3);
            if (var5 < 5) {
               this.func_151550_a(var1, Blocks.field_150417_aV, 0, 1, 5 - var5, 1 + var5, var3);
               this.func_151550_a(var1, Blocks.field_150417_aV, 0, 2, 5 - var5, 1 + var5, var3);
               this.func_151550_a(var1, Blocks.field_150417_aV, 0, 3, 5 - var5, 1 + var5, var3);
            }
         }

         return true;
      }
   }
}
