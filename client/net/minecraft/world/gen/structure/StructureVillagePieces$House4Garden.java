package net.minecraft.world.gen.structure;

import java.util.List;
import java.util.Random;
import net.minecraft.block.material.Material;
import net.minecraft.init.Blocks;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;

public class StructureVillagePieces$House4Garden extends StructureVillagePieces$Village {
   private boolean field_74913_b;

   public StructureVillagePieces$House4Garden() {
      super();
   }

   public StructureVillagePieces$House4Garden(StructureVillagePieces$Start var1, int var2, Random var3, StructureBoundingBox var4, int var5) {
      super(var1, var2);
      this.field_74885_f = var5;
      this.field_74887_e = var4;
      this.field_74913_b = var3.nextBoolean();
   }

   @Override
   protected void func_143012_a(NBTTagCompound var1) {
      super.func_143012_a(var1);
      var1.func_74757_a("Terrace", this.field_74913_b);
   }

   @Override
   protected void func_143011_b(NBTTagCompound var1) {
      super.func_143011_b(var1);
      this.field_74913_b = var1.func_74767_n("Terrace");
   }

   public static StructureVillagePieces$House4Garden func_74912_a(
      StructureVillagePieces$Start var0, List var1, Random var2, int var3, int var4, int var5, int var6, int var7
   ) {
      StructureBoundingBox var8 = StructureBoundingBox.func_78889_a(var3, var4, var5, 0, 0, 0, 5, 6, 5, var6);
      return StructureComponent.func_74883_a(var1, var8) != null ? null : new StructureVillagePieces$House4Garden(var0, var7, var2, var8, var6);
   }

   @Override
   public boolean func_74875_a(World var1, Random var2, StructureBoundingBox var3) {
      if (this.field_143015_k < 0) {
         this.field_143015_k = this.func_74889_b(var1, var3);
         if (this.field_143015_k < 0) {
            return true;
         }

         this.field_74887_e.func_78886_a(0, this.field_143015_k - this.field_74887_e.field_78894_e + 6 - 1, 0);
      }

      this.func_151549_a(var1, var3, 0, 0, 0, 4, 0, 4, Blocks.field_150347_e, Blocks.field_150347_e, false);
      this.func_151549_a(var1, var3, 0, 4, 0, 4, 4, 4, Blocks.field_150364_r, Blocks.field_150364_r, false);
      this.func_151549_a(var1, var3, 1, 4, 1, 3, 4, 3, Blocks.field_150344_f, Blocks.field_150344_f, false);
      this.func_151550_a(var1, Blocks.field_150347_e, 0, 0, 1, 0, var3);
      this.func_151550_a(var1, Blocks.field_150347_e, 0, 0, 2, 0, var3);
      this.func_151550_a(var1, Blocks.field_150347_e, 0, 0, 3, 0, var3);
      this.func_151550_a(var1, Blocks.field_150347_e, 0, 4, 1, 0, var3);
      this.func_151550_a(var1, Blocks.field_150347_e, 0, 4, 2, 0, var3);
      this.func_151550_a(var1, Blocks.field_150347_e, 0, 4, 3, 0, var3);
      this.func_151550_a(var1, Blocks.field_150347_e, 0, 0, 1, 4, var3);
      this.func_151550_a(var1, Blocks.field_150347_e, 0, 0, 2, 4, var3);
      this.func_151550_a(var1, Blocks.field_150347_e, 0, 0, 3, 4, var3);
      this.func_151550_a(var1, Blocks.field_150347_e, 0, 4, 1, 4, var3);
      this.func_151550_a(var1, Blocks.field_150347_e, 0, 4, 2, 4, var3);
      this.func_151550_a(var1, Blocks.field_150347_e, 0, 4, 3, 4, var3);
      this.func_151549_a(var1, var3, 0, 1, 1, 0, 3, 3, Blocks.field_150344_f, Blocks.field_150344_f, false);
      this.func_151549_a(var1, var3, 4, 1, 1, 4, 3, 3, Blocks.field_150344_f, Blocks.field_150344_f, false);
      this.func_151549_a(var1, var3, 1, 1, 4, 3, 3, 4, Blocks.field_150344_f, Blocks.field_150344_f, false);
      this.func_151550_a(var1, Blocks.field_150410_aZ, 0, 0, 2, 2, var3);
      this.func_151550_a(var1, Blocks.field_150410_aZ, 0, 2, 2, 4, var3);
      this.func_151550_a(var1, Blocks.field_150410_aZ, 0, 4, 2, 2, var3);
      this.func_151550_a(var1, Blocks.field_150344_f, 0, 1, 1, 0, var3);
      this.func_151550_a(var1, Blocks.field_150344_f, 0, 1, 2, 0, var3);
      this.func_151550_a(var1, Blocks.field_150344_f, 0, 1, 3, 0, var3);
      this.func_151550_a(var1, Blocks.field_150344_f, 0, 2, 3, 0, var3);
      this.func_151550_a(var1, Blocks.field_150344_f, 0, 3, 3, 0, var3);
      this.func_151550_a(var1, Blocks.field_150344_f, 0, 3, 2, 0, var3);
      this.func_151550_a(var1, Blocks.field_150344_f, 0, 3, 1, 0, var3);
      if (this.func_151548_a(var1, 2, 0, -1, var3).func_149688_o() == Material.field_151579_a
         && this.func_151548_a(var1, 2, -1, -1, var3).func_149688_o() != Material.field_151579_a) {
         this.func_151550_a(var1, Blocks.field_150446_ar, this.func_151555_a(Blocks.field_150446_ar, 3), 2, 0, -1, var3);
      }

      this.func_151549_a(var1, var3, 1, 1, 1, 3, 3, 3, Blocks.field_150350_a, Blocks.field_150350_a, false);
      if (this.field_74913_b) {
         this.func_151550_a(var1, Blocks.field_150422_aJ, 0, 0, 5, 0, var3);
         this.func_151550_a(var1, Blocks.field_150422_aJ, 0, 1, 5, 0, var3);
         this.func_151550_a(var1, Blocks.field_150422_aJ, 0, 2, 5, 0, var3);
         this.func_151550_a(var1, Blocks.field_150422_aJ, 0, 3, 5, 0, var3);
         this.func_151550_a(var1, Blocks.field_150422_aJ, 0, 4, 5, 0, var3);
         this.func_151550_a(var1, Blocks.field_150422_aJ, 0, 0, 5, 4, var3);
         this.func_151550_a(var1, Blocks.field_150422_aJ, 0, 1, 5, 4, var3);
         this.func_151550_a(var1, Blocks.field_150422_aJ, 0, 2, 5, 4, var3);
         this.func_151550_a(var1, Blocks.field_150422_aJ, 0, 3, 5, 4, var3);
         this.func_151550_a(var1, Blocks.field_150422_aJ, 0, 4, 5, 4, var3);
         this.func_151550_a(var1, Blocks.field_150422_aJ, 0, 4, 5, 1, var3);
         this.func_151550_a(var1, Blocks.field_150422_aJ, 0, 4, 5, 2, var3);
         this.func_151550_a(var1, Blocks.field_150422_aJ, 0, 4, 5, 3, var3);
         this.func_151550_a(var1, Blocks.field_150422_aJ, 0, 0, 5, 1, var3);
         this.func_151550_a(var1, Blocks.field_150422_aJ, 0, 0, 5, 2, var3);
         this.func_151550_a(var1, Blocks.field_150422_aJ, 0, 0, 5, 3, var3);
      }

      if (this.field_74913_b) {
         int var4 = this.func_151555_a(Blocks.field_150468_ap, 3);
         this.func_151550_a(var1, Blocks.field_150468_ap, var4, 3, 1, 3, var3);
         this.func_151550_a(var1, Blocks.field_150468_ap, var4, 3, 2, 3, var3);
         this.func_151550_a(var1, Blocks.field_150468_ap, var4, 3, 3, 3, var3);
         this.func_151550_a(var1, Blocks.field_150468_ap, var4, 3, 4, 3, var3);
      }

      this.func_151550_a(var1, Blocks.field_150478_aa, 0, 2, 3, 1, var3);

      for(int var6 = 0; var6 < 5; ++var6) {
         for(int var5 = 0; var5 < 5; ++var5) {
            this.func_74871_b(var1, var5, 6, var6, var3);
            this.func_151554_b(var1, Blocks.field_150347_e, 0, var5, -1, var6, var3);
         }
      }

      this.func_74893_a(var1, var3, 1, 1, 2, 1);
      return true;
   }
}
