package net.minecraft.world.gen.structure;

import java.util.List;
import java.util.Random;
import net.minecraft.block.material.Material;
import net.minecraft.init.Blocks;
import net.minecraft.world.World;

public class StructureVillagePieces$Church extends StructureVillagePieces$Village {
   public StructureVillagePieces$Church() {
      super();
   }

   public StructureVillagePieces$Church(StructureVillagePieces$Start var1, int var2, Random var3, StructureBoundingBox var4, int var5) {
      super(var1, var2);
      this.field_74885_f = var5;
      this.field_74887_e = var4;
   }

   public static StructureVillagePieces$Church func_74919_a(
      StructureVillagePieces$Start var0, List var1, Random var2, int var3, int var4, int var5, int var6, int var7
   ) {
      StructureBoundingBox var8 = StructureBoundingBox.func_78889_a(var3, var4, var5, 0, 0, 0, 5, 12, 9, var6);
      return func_74895_a(var8) && StructureComponent.func_74883_a(var1, var8) == null
         ? new StructureVillagePieces$Church(var0, var7, var2, var8, var6)
         : null;
   }

   @Override
   public boolean func_74875_a(World var1, Random var2, StructureBoundingBox var3) {
      if (this.field_143015_k < 0) {
         this.field_143015_k = this.func_74889_b(var1, var3);
         if (this.field_143015_k < 0) {
            return true;
         }

         this.field_74887_e.func_78886_a(0, this.field_143015_k - this.field_74887_e.field_78894_e + 12 - 1, 0);
      }

      this.func_151549_a(var1, var3, 1, 1, 1, 3, 3, 7, Blocks.field_150350_a, Blocks.field_150350_a, false);
      this.func_151549_a(var1, var3, 1, 5, 1, 3, 9, 3, Blocks.field_150350_a, Blocks.field_150350_a, false);
      this.func_151549_a(var1, var3, 1, 0, 0, 3, 0, 8, Blocks.field_150347_e, Blocks.field_150347_e, false);
      this.func_151549_a(var1, var3, 1, 1, 0, 3, 10, 0, Blocks.field_150347_e, Blocks.field_150347_e, false);
      this.func_151549_a(var1, var3, 0, 1, 1, 0, 10, 3, Blocks.field_150347_e, Blocks.field_150347_e, false);
      this.func_151549_a(var1, var3, 4, 1, 1, 4, 10, 3, Blocks.field_150347_e, Blocks.field_150347_e, false);
      this.func_151549_a(var1, var3, 0, 0, 4, 0, 4, 7, Blocks.field_150347_e, Blocks.field_150347_e, false);
      this.func_151549_a(var1, var3, 4, 0, 4, 4, 4, 7, Blocks.field_150347_e, Blocks.field_150347_e, false);
      this.func_151549_a(var1, var3, 1, 1, 8, 3, 4, 8, Blocks.field_150347_e, Blocks.field_150347_e, false);
      this.func_151549_a(var1, var3, 1, 5, 4, 3, 10, 4, Blocks.field_150347_e, Blocks.field_150347_e, false);
      this.func_151549_a(var1, var3, 1, 5, 5, 3, 5, 7, Blocks.field_150347_e, Blocks.field_150347_e, false);
      this.func_151549_a(var1, var3, 0, 9, 0, 4, 9, 4, Blocks.field_150347_e, Blocks.field_150347_e, false);
      this.func_151549_a(var1, var3, 0, 4, 0, 4, 4, 4, Blocks.field_150347_e, Blocks.field_150347_e, false);
      this.func_151550_a(var1, Blocks.field_150347_e, 0, 0, 11, 2, var3);
      this.func_151550_a(var1, Blocks.field_150347_e, 0, 4, 11, 2, var3);
      this.func_151550_a(var1, Blocks.field_150347_e, 0, 2, 11, 0, var3);
      this.func_151550_a(var1, Blocks.field_150347_e, 0, 2, 11, 4, var3);
      this.func_151550_a(var1, Blocks.field_150347_e, 0, 1, 1, 6, var3);
      this.func_151550_a(var1, Blocks.field_150347_e, 0, 1, 1, 7, var3);
      this.func_151550_a(var1, Blocks.field_150347_e, 0, 2, 1, 7, var3);
      this.func_151550_a(var1, Blocks.field_150347_e, 0, 3, 1, 6, var3);
      this.func_151550_a(var1, Blocks.field_150347_e, 0, 3, 1, 7, var3);
      this.func_151550_a(var1, Blocks.field_150446_ar, this.func_151555_a(Blocks.field_150446_ar, 3), 1, 1, 5, var3);
      this.func_151550_a(var1, Blocks.field_150446_ar, this.func_151555_a(Blocks.field_150446_ar, 3), 2, 1, 6, var3);
      this.func_151550_a(var1, Blocks.field_150446_ar, this.func_151555_a(Blocks.field_150446_ar, 3), 3, 1, 5, var3);
      this.func_151550_a(var1, Blocks.field_150446_ar, this.func_151555_a(Blocks.field_150446_ar, 1), 1, 2, 7, var3);
      this.func_151550_a(var1, Blocks.field_150446_ar, this.func_151555_a(Blocks.field_150446_ar, 0), 3, 2, 7, var3);
      this.func_151550_a(var1, Blocks.field_150410_aZ, 0, 0, 2, 2, var3);
      this.func_151550_a(var1, Blocks.field_150410_aZ, 0, 0, 3, 2, var3);
      this.func_151550_a(var1, Blocks.field_150410_aZ, 0, 4, 2, 2, var3);
      this.func_151550_a(var1, Blocks.field_150410_aZ, 0, 4, 3, 2, var3);
      this.func_151550_a(var1, Blocks.field_150410_aZ, 0, 0, 6, 2, var3);
      this.func_151550_a(var1, Blocks.field_150410_aZ, 0, 0, 7, 2, var3);
      this.func_151550_a(var1, Blocks.field_150410_aZ, 0, 4, 6, 2, var3);
      this.func_151550_a(var1, Blocks.field_150410_aZ, 0, 4, 7, 2, var3);
      this.func_151550_a(var1, Blocks.field_150410_aZ, 0, 2, 6, 0, var3);
      this.func_151550_a(var1, Blocks.field_150410_aZ, 0, 2, 7, 0, var3);
      this.func_151550_a(var1, Blocks.field_150410_aZ, 0, 2, 6, 4, var3);
      this.func_151550_a(var1, Blocks.field_150410_aZ, 0, 2, 7, 4, var3);
      this.func_151550_a(var1, Blocks.field_150410_aZ, 0, 0, 3, 6, var3);
      this.func_151550_a(var1, Blocks.field_150410_aZ, 0, 4, 3, 6, var3);
      this.func_151550_a(var1, Blocks.field_150410_aZ, 0, 2, 3, 8, var3);
      this.func_151550_a(var1, Blocks.field_150478_aa, 0, 2, 4, 7, var3);
      this.func_151550_a(var1, Blocks.field_150478_aa, 0, 1, 4, 6, var3);
      this.func_151550_a(var1, Blocks.field_150478_aa, 0, 3, 4, 6, var3);
      this.func_151550_a(var1, Blocks.field_150478_aa, 0, 2, 4, 5, var3);
      int var4 = this.func_151555_a(Blocks.field_150468_ap, 4);

      for(int var5 = 1; var5 <= 9; ++var5) {
         this.func_151550_a(var1, Blocks.field_150468_ap, var4, 3, var5, 3, var3);
      }

      this.func_151550_a(var1, Blocks.field_150350_a, 0, 2, 1, 0, var3);
      this.func_151550_a(var1, Blocks.field_150350_a, 0, 2, 2, 0, var3);
      this.func_74881_a(var1, var3, var2, 2, 1, 0, this.func_151555_a(Blocks.field_150466_ao, 1));
      if (this.func_151548_a(var1, 2, 0, -1, var3).func_149688_o() == Material.field_151579_a
         && this.func_151548_a(var1, 2, -1, -1, var3).func_149688_o() != Material.field_151579_a) {
         this.func_151550_a(var1, Blocks.field_150446_ar, this.func_151555_a(Blocks.field_150446_ar, 3), 2, 0, -1, var3);
      }

      for(int var7 = 0; var7 < 9; ++var7) {
         for(int var6 = 0; var6 < 5; ++var6) {
            this.func_74871_b(var1, var6, 12, var7, var3);
            this.func_151554_b(var1, Blocks.field_150347_e, 0, var6, -1, var7, var3);
         }
      }

      this.func_74893_a(var1, var3, 2, 1, 2, 1);
      return true;
   }

   @Override
   protected int func_74888_b(int var1) {
      return 2;
   }
}
