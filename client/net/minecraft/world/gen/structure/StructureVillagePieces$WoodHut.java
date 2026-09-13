package net.minecraft.world.gen.structure;

import java.util.List;
import java.util.Random;
import net.minecraft.block.material.Material;
import net.minecraft.init.Blocks;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;

public class StructureVillagePieces$WoodHut extends StructureVillagePieces$Village {
   private boolean field_74909_b;
   private int field_74910_c;

   public StructureVillagePieces$WoodHut() {
      super();
   }

   public StructureVillagePieces$WoodHut(StructureVillagePieces$Start var1, int var2, Random var3, StructureBoundingBox var4, int var5) {
      super(var1, var2);
      this.field_74885_f = var5;
      this.field_74887_e = var4;
      this.field_74909_b = var3.nextBoolean();
      this.field_74910_c = var3.nextInt(3);
   }

   @Override
   protected void func_143012_a(NBTTagCompound var1) {
      super.func_143012_a(var1);
      var1.func_74768_a("T", this.field_74910_c);
      var1.func_74757_a("C", this.field_74909_b);
   }

   @Override
   protected void func_143011_b(NBTTagCompound var1) {
      super.func_143011_b(var1);
      this.field_74910_c = var1.func_74762_e("T");
      this.field_74909_b = var1.func_74767_n("C");
   }

   public static StructureVillagePieces$WoodHut func_74908_a(
      StructureVillagePieces$Start var0, List var1, Random var2, int var3, int var4, int var5, int var6, int var7
   ) {
      StructureBoundingBox var8 = StructureBoundingBox.func_78889_a(var3, var4, var5, 0, 0, 0, 4, 6, 5, var6);
      return func_74895_a(var8) && StructureComponent.func_74883_a(var1, var8) == null
         ? new StructureVillagePieces$WoodHut(var0, var7, var2, var8, var6)
         : null;
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

      this.func_151549_a(var1, var3, 1, 1, 1, 3, 5, 4, Blocks.field_150350_a, Blocks.field_150350_a, false);
      this.func_151549_a(var1, var3, 0, 0, 0, 3, 0, 4, Blocks.field_150347_e, Blocks.field_150347_e, false);
      this.func_151549_a(var1, var3, 1, 0, 1, 2, 0, 3, Blocks.field_150346_d, Blocks.field_150346_d, false);
      if (this.field_74909_b) {
         this.func_151549_a(var1, var3, 1, 4, 1, 2, 4, 3, Blocks.field_150364_r, Blocks.field_150364_r, false);
      } else {
         this.func_151549_a(var1, var3, 1, 5, 1, 2, 5, 3, Blocks.field_150364_r, Blocks.field_150364_r, false);
      }

      this.func_151550_a(var1, Blocks.field_150364_r, 0, 1, 4, 0, var3);
      this.func_151550_a(var1, Blocks.field_150364_r, 0, 2, 4, 0, var3);
      this.func_151550_a(var1, Blocks.field_150364_r, 0, 1, 4, 4, var3);
      this.func_151550_a(var1, Blocks.field_150364_r, 0, 2, 4, 4, var3);
      this.func_151550_a(var1, Blocks.field_150364_r, 0, 0, 4, 1, var3);
      this.func_151550_a(var1, Blocks.field_150364_r, 0, 0, 4, 2, var3);
      this.func_151550_a(var1, Blocks.field_150364_r, 0, 0, 4, 3, var3);
      this.func_151550_a(var1, Blocks.field_150364_r, 0, 3, 4, 1, var3);
      this.func_151550_a(var1, Blocks.field_150364_r, 0, 3, 4, 2, var3);
      this.func_151550_a(var1, Blocks.field_150364_r, 0, 3, 4, 3, var3);
      this.func_151549_a(var1, var3, 0, 1, 0, 0, 3, 0, Blocks.field_150364_r, Blocks.field_150364_r, false);
      this.func_151549_a(var1, var3, 3, 1, 0, 3, 3, 0, Blocks.field_150364_r, Blocks.field_150364_r, false);
      this.func_151549_a(var1, var3, 0, 1, 4, 0, 3, 4, Blocks.field_150364_r, Blocks.field_150364_r, false);
      this.func_151549_a(var1, var3, 3, 1, 4, 3, 3, 4, Blocks.field_150364_r, Blocks.field_150364_r, false);
      this.func_151549_a(var1, var3, 0, 1, 1, 0, 3, 3, Blocks.field_150344_f, Blocks.field_150344_f, false);
      this.func_151549_a(var1, var3, 3, 1, 1, 3, 3, 3, Blocks.field_150344_f, Blocks.field_150344_f, false);
      this.func_151549_a(var1, var3, 1, 1, 0, 2, 3, 0, Blocks.field_150344_f, Blocks.field_150344_f, false);
      this.func_151549_a(var1, var3, 1, 1, 4, 2, 3, 4, Blocks.field_150344_f, Blocks.field_150344_f, false);
      this.func_151550_a(var1, Blocks.field_150410_aZ, 0, 0, 2, 2, var3);
      this.func_151550_a(var1, Blocks.field_150410_aZ, 0, 3, 2, 2, var3);
      if (this.field_74910_c > 0) {
         this.func_151550_a(var1, Blocks.field_150422_aJ, 0, this.field_74910_c, 1, 3, var3);
         this.func_151550_a(var1, Blocks.field_150452_aw, 0, this.field_74910_c, 2, 3, var3);
      }

      this.func_151550_a(var1, Blocks.field_150350_a, 0, 1, 1, 0, var3);
      this.func_151550_a(var1, Blocks.field_150350_a, 0, 1, 2, 0, var3);
      this.func_74881_a(var1, var3, var2, 1, 1, 0, this.func_151555_a(Blocks.field_150466_ao, 1));
      if (this.func_151548_a(var1, 1, 0, -1, var3).func_149688_o() == Material.field_151579_a
         && this.func_151548_a(var1, 1, -1, -1, var3).func_149688_o() != Material.field_151579_a) {
         this.func_151550_a(var1, Blocks.field_150446_ar, this.func_151555_a(Blocks.field_150446_ar, 3), 1, 0, -1, var3);
      }

      for(int var4 = 0; var4 < 5; ++var4) {
         for(int var5 = 0; var5 < 4; ++var5) {
            this.func_74871_b(var1, var5, 6, var4, var3);
            this.func_151554_b(var1, Blocks.field_150347_e, 0, var5, -1, var4, var3);
         }
      }

      this.func_74893_a(var1, var3, 1, 1, 2, 1);
      return true;
   }
}
