package net.minecraft.world.gen.structure;

import java.util.List;
import java.util.Random;
import net.minecraft.block.material.Material;
import net.minecraft.init.Blocks;
import net.minecraft.world.World;

public class StructureVillagePieces$Hall extends StructureVillagePieces$Village {
   public StructureVillagePieces$Hall() {
      super();
   }

   public StructureVillagePieces$Hall(StructureVillagePieces$Start var1, int var2, Random var3, StructureBoundingBox var4, int var5) {
      super(var1, var2);
      this.field_74885_f = var5;
      this.field_74887_e = var4;
   }

   public static StructureVillagePieces$Hall func_74906_a(
      StructureVillagePieces$Start var0, List var1, Random var2, int var3, int var4, int var5, int var6, int var7
   ) {
      StructureBoundingBox var8 = StructureBoundingBox.func_78889_a(var3, var4, var5, 0, 0, 0, 9, 7, 11, var6);
      return func_74895_a(var8) && StructureComponent.func_74883_a(var1, var8) == null ? new StructureVillagePieces$Hall(var0, var7, var2, var8, var6) : null;
   }

   @Override
   public boolean func_74875_a(World var1, Random var2, StructureBoundingBox var3) {
      if (this.field_143015_k < 0) {
         this.field_143015_k = this.func_74889_b(var1, var3);
         if (this.field_143015_k < 0) {
            return true;
         }

         this.field_74887_e.func_78886_a(0, this.field_143015_k - this.field_74887_e.field_78894_e + 7 - 1, 0);
      }

      this.func_151549_a(var1, var3, 1, 1, 1, 7, 4, 4, Blocks.field_150350_a, Blocks.field_150350_a, false);
      this.func_151549_a(var1, var3, 2, 1, 6, 8, 4, 10, Blocks.field_150350_a, Blocks.field_150350_a, false);
      this.func_151549_a(var1, var3, 2, 0, 6, 8, 0, 10, Blocks.field_150346_d, Blocks.field_150346_d, false);
      this.func_151550_a(var1, Blocks.field_150347_e, 0, 6, 0, 6, var3);
      this.func_151549_a(var1, var3, 2, 1, 6, 2, 1, 10, Blocks.field_150422_aJ, Blocks.field_150422_aJ, false);
      this.func_151549_a(var1, var3, 8, 1, 6, 8, 1, 10, Blocks.field_150422_aJ, Blocks.field_150422_aJ, false);
      this.func_151549_a(var1, var3, 3, 1, 10, 7, 1, 10, Blocks.field_150422_aJ, Blocks.field_150422_aJ, false);
      this.func_151549_a(var1, var3, 1, 0, 1, 7, 0, 4, Blocks.field_150344_f, Blocks.field_150344_f, false);
      this.func_151549_a(var1, var3, 0, 0, 0, 0, 3, 5, Blocks.field_150347_e, Blocks.field_150347_e, false);
      this.func_151549_a(var1, var3, 8, 0, 0, 8, 3, 5, Blocks.field_150347_e, Blocks.field_150347_e, false);
      this.func_151549_a(var1, var3, 1, 0, 0, 7, 1, 0, Blocks.field_150347_e, Blocks.field_150347_e, false);
      this.func_151549_a(var1, var3, 1, 0, 5, 7, 1, 5, Blocks.field_150347_e, Blocks.field_150347_e, false);
      this.func_151549_a(var1, var3, 1, 2, 0, 7, 3, 0, Blocks.field_150344_f, Blocks.field_150344_f, false);
      this.func_151549_a(var1, var3, 1, 2, 5, 7, 3, 5, Blocks.field_150344_f, Blocks.field_150344_f, false);
      this.func_151549_a(var1, var3, 0, 4, 1, 8, 4, 1, Blocks.field_150344_f, Blocks.field_150344_f, false);
      this.func_151549_a(var1, var3, 0, 4, 4, 8, 4, 4, Blocks.field_150344_f, Blocks.field_150344_f, false);
      this.func_151549_a(var1, var3, 0, 5, 2, 8, 5, 3, Blocks.field_150344_f, Blocks.field_150344_f, false);
      this.func_151550_a(var1, Blocks.field_150344_f, 0, 0, 4, 2, var3);
      this.func_151550_a(var1, Blocks.field_150344_f, 0, 0, 4, 3, var3);
      this.func_151550_a(var1, Blocks.field_150344_f, 0, 8, 4, 2, var3);
      this.func_151550_a(var1, Blocks.field_150344_f, 0, 8, 4, 3, var3);
      int var4 = this.func_151555_a(Blocks.field_150476_ad, 3);
      int var5 = this.func_151555_a(Blocks.field_150476_ad, 2);

      for(int var6 = -1; var6 <= 2; ++var6) {
         for(int var7 = 0; var7 <= 8; ++var7) {
            this.func_151550_a(var1, Blocks.field_150476_ad, var4, var7, 4 + var6, var6, var3);
            this.func_151550_a(var1, Blocks.field_150476_ad, var5, var7, 4 + var6, 5 - var6, var3);
         }
      }

      this.func_151550_a(var1, Blocks.field_150364_r, 0, 0, 2, 1, var3);
      this.func_151550_a(var1, Blocks.field_150364_r, 0, 0, 2, 4, var3);
      this.func_151550_a(var1, Blocks.field_150364_r, 0, 8, 2, 1, var3);
      this.func_151550_a(var1, Blocks.field_150364_r, 0, 8, 2, 4, var3);
      this.func_151550_a(var1, Blocks.field_150410_aZ, 0, 0, 2, 2, var3);
      this.func_151550_a(var1, Blocks.field_150410_aZ, 0, 0, 2, 3, var3);
      this.func_151550_a(var1, Blocks.field_150410_aZ, 0, 8, 2, 2, var3);
      this.func_151550_a(var1, Blocks.field_150410_aZ, 0, 8, 2, 3, var3);
      this.func_151550_a(var1, Blocks.field_150410_aZ, 0, 2, 2, 5, var3);
      this.func_151550_a(var1, Blocks.field_150410_aZ, 0, 3, 2, 5, var3);
      this.func_151550_a(var1, Blocks.field_150410_aZ, 0, 5, 2, 0, var3);
      this.func_151550_a(var1, Blocks.field_150410_aZ, 0, 6, 2, 5, var3);
      this.func_151550_a(var1, Blocks.field_150422_aJ, 0, 2, 1, 3, var3);
      this.func_151550_a(var1, Blocks.field_150452_aw, 0, 2, 2, 3, var3);
      this.func_151550_a(var1, Blocks.field_150344_f, 0, 1, 1, 4, var3);
      this.func_151550_a(var1, Blocks.field_150476_ad, this.func_151555_a(Blocks.field_150476_ad, 3), 2, 1, 4, var3);
      this.func_151550_a(var1, Blocks.field_150476_ad, this.func_151555_a(Blocks.field_150476_ad, 1), 1, 1, 3, var3);
      this.func_151549_a(var1, var3, 5, 0, 1, 7, 0, 3, Blocks.field_150334_T, Blocks.field_150334_T, false);
      this.func_151550_a(var1, Blocks.field_150334_T, 0, 6, 1, 1, var3);
      this.func_151550_a(var1, Blocks.field_150334_T, 0, 6, 1, 2, var3);
      this.func_151550_a(var1, Blocks.field_150350_a, 0, 2, 1, 0, var3);
      this.func_151550_a(var1, Blocks.field_150350_a, 0, 2, 2, 0, var3);
      this.func_151550_a(var1, Blocks.field_150478_aa, 0, 2, 3, 1, var3);
      this.func_74881_a(var1, var3, var2, 2, 1, 0, this.func_151555_a(Blocks.field_150466_ao, 1));
      if (this.func_151548_a(var1, 2, 0, -1, var3).func_149688_o() == Material.field_151579_a
         && this.func_151548_a(var1, 2, -1, -1, var3).func_149688_o() != Material.field_151579_a) {
         this.func_151550_a(var1, Blocks.field_150446_ar, this.func_151555_a(Blocks.field_150446_ar, 3), 2, 0, -1, var3);
      }

      this.func_151550_a(var1, Blocks.field_150350_a, 0, 6, 1, 5, var3);
      this.func_151550_a(var1, Blocks.field_150350_a, 0, 6, 2, 5, var3);
      this.func_151550_a(var1, Blocks.field_150478_aa, 0, 6, 3, 4, var3);
      this.func_74881_a(var1, var3, var2, 6, 1, 5, this.func_151555_a(Blocks.field_150466_ao, 1));

      for(int var8 = 0; var8 < 5; ++var8) {
         for(int var9 = 0; var9 < 9; ++var9) {
            this.func_74871_b(var1, var9, 7, var8, var3);
            this.func_151554_b(var1, Blocks.field_150347_e, 0, var9, -1, var8, var3);
         }
      }

      this.func_74893_a(var1, var3, 4, 1, 2, 2);
      return true;
   }

   @Override
   protected int func_74888_b(int var1) {
      return var1 == 0 ? 4 : 0;
   }
}
