package net.minecraft.world.gen.structure;

import java.util.List;
import java.util.Random;
import net.minecraft.block.Block;
import net.minecraft.entity.passive.EntityVillager;
import net.minecraft.init.Blocks;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;

abstract class StructureVillagePieces$Village extends StructureComponent {
   protected int field_143015_k = -1;
   private int field_74896_a;
   private boolean field_143014_b;

   public StructureVillagePieces$Village() {
      super();
   }

   protected StructureVillagePieces$Village(StructureVillagePieces$Start var1, int var2) {
      super(var2);
      if (var1 != null) {
         this.field_143014_b = var1.field_74927_b;
      }
   }

   @Override
   protected void func_143012_a(NBTTagCompound var1) {
      var1.func_74768_a("HPos", this.field_143015_k);
      var1.func_74768_a("VCount", this.field_74896_a);
      var1.func_74757_a("Desert", this.field_143014_b);
   }

   @Override
   protected void func_143011_b(NBTTagCompound var1) {
      this.field_143015_k = var1.func_74762_e("HPos");
      this.field_74896_a = var1.func_74762_e("VCount");
      this.field_143014_b = var1.func_74767_n("Desert");
   }

   protected StructureComponent func_74891_a(StructureVillagePieces$Start var1, List var2, Random var3, int var4, int var5) {
      switch(this.field_74885_f) {
         case 0:
            return StructureVillagePieces.access$000(
               var1,
               var2,
               var3,
               this.field_74887_e.field_78897_a - 1,
               this.field_74887_e.field_78895_b + var4,
               this.field_74887_e.field_78896_c + var5,
               1,
               this.func_74877_c()
            );
         case 1:
            return StructureVillagePieces.access$000(
               var1,
               var2,
               var3,
               this.field_74887_e.field_78897_a + var5,
               this.field_74887_e.field_78895_b + var4,
               this.field_74887_e.field_78896_c - 1,
               2,
               this.func_74877_c()
            );
         case 2:
            return StructureVillagePieces.access$000(
               var1,
               var2,
               var3,
               this.field_74887_e.field_78897_a - 1,
               this.field_74887_e.field_78895_b + var4,
               this.field_74887_e.field_78896_c + var5,
               1,
               this.func_74877_c()
            );
         case 3:
            return StructureVillagePieces.access$000(
               var1,
               var2,
               var3,
               this.field_74887_e.field_78897_a + var5,
               this.field_74887_e.field_78895_b + var4,
               this.field_74887_e.field_78896_c - 1,
               2,
               this.func_74877_c()
            );
         default:
            return null;
      }
   }

   protected StructureComponent func_74894_b(StructureVillagePieces$Start var1, List var2, Random var3, int var4, int var5) {
      switch(this.field_74885_f) {
         case 0:
            return StructureVillagePieces.access$000(
               var1,
               var2,
               var3,
               this.field_74887_e.field_78893_d + 1,
               this.field_74887_e.field_78895_b + var4,
               this.field_74887_e.field_78896_c + var5,
               3,
               this.func_74877_c()
            );
         case 1:
            return StructureVillagePieces.access$000(
               var1,
               var2,
               var3,
               this.field_74887_e.field_78897_a + var5,
               this.field_74887_e.field_78895_b + var4,
               this.field_74887_e.field_78892_f + 1,
               0,
               this.func_74877_c()
            );
         case 2:
            return StructureVillagePieces.access$000(
               var1,
               var2,
               var3,
               this.field_74887_e.field_78893_d + 1,
               this.field_74887_e.field_78895_b + var4,
               this.field_74887_e.field_78896_c + var5,
               3,
               this.func_74877_c()
            );
         case 3:
            return StructureVillagePieces.access$000(
               var1,
               var2,
               var3,
               this.field_74887_e.field_78897_a + var5,
               this.field_74887_e.field_78895_b + var4,
               this.field_74887_e.field_78892_f + 1,
               0,
               this.func_74877_c()
            );
         default:
            return null;
      }
   }

   protected int func_74889_b(World var1, StructureBoundingBox var2) {
      int var3 = 0;
      int var4 = 0;

      for(int var5 = this.field_74887_e.field_78896_c; var5 <= this.field_74887_e.field_78892_f; ++var5) {
         for(int var6 = this.field_74887_e.field_78897_a; var6 <= this.field_74887_e.field_78893_d; ++var6) {
            if (var2.func_78890_b(var6, 64, var5)) {
               var3 += Math.max(var1.func_72825_h(var6, var5), var1.field_73011_w.func_76557_i());
               ++var4;
            }
         }
      }

      return var4 == 0 ? -1 : var3 / var4;
   }

   protected static boolean func_74895_a(StructureBoundingBox var0) {
      return var0 != null && var0.field_78895_b > 10;
   }

   protected void func_74893_a(World var1, StructureBoundingBox var2, int var3, int var4, int var5, int var6) {
      if (this.field_74896_a < var6) {
         for(int var7 = this.field_74896_a; var7 < var6; ++var7) {
            int var8 = this.func_74865_a(var3 + var7, var5);
            int var9 = this.func_74862_a(var4);
            int var10 = this.func_74873_b(var3 + var7, var5);
            if (!var2.func_78890_b(var8, var9, var10)) {
               break;
            }

            ++this.field_74896_a;
            EntityVillager var11 = new EntityVillager(var1, this.func_74888_b(var7));
            var11.func_70012_b((double)var8 + 0.5, (double)var9, (double)var10 + 0.5, 0.0F, 0.0F);
            var1.func_72838_d(var11);
         }
      }
   }

   protected int func_74888_b(int var1) {
      return 0;
   }

   protected Block func_151558_b(Block var1, int var2) {
      if (this.field_143014_b) {
         if (var1 == Blocks.field_150364_r || var1 == Blocks.field_150363_s) {
            return Blocks.field_150322_A;
         }

         if (var1 == Blocks.field_150347_e) {
            return Blocks.field_150322_A;
         }

         if (var1 == Blocks.field_150344_f) {
            return Blocks.field_150322_A;
         }

         if (var1 == Blocks.field_150476_ad) {
            return Blocks.field_150372_bz;
         }

         if (var1 == Blocks.field_150446_ar) {
            return Blocks.field_150372_bz;
         }

         if (var1 == Blocks.field_150351_n) {
            return Blocks.field_150322_A;
         }
      }

      return var1;
   }

   protected int func_151557_c(Block var1, int var2) {
      if (this.field_143014_b) {
         if (var1 == Blocks.field_150364_r || var1 == Blocks.field_150363_s) {
            return 0;
         }

         if (var1 == Blocks.field_150347_e) {
            return 0;
         }

         if (var1 == Blocks.field_150344_f) {
            return 2;
         }
      }

      return var2;
   }

   @Override
   protected void func_151550_a(World var1, Block var2, int var3, int var4, int var5, int var6, StructureBoundingBox var7) {
      Block var8 = this.func_151558_b(var2, var3);
      int var9 = this.func_151557_c(var2, var3);
      super.func_151550_a(var1, var8, var9, var4, var5, var6, var7);
   }

   @Override
   protected void func_151549_a(
      World var1, StructureBoundingBox var2, int var3, int var4, int var5, int var6, int var7, int var8, Block var9, Block var10, boolean var11
   ) {
      Block var12 = this.func_151558_b(var9, 0);
      int var13 = this.func_151557_c(var9, 0);
      Block var14 = this.func_151558_b(var10, 0);
      int var15 = this.func_151557_c(var10, 0);
      super.func_151556_a(var1, var2, var3, var4, var5, var6, var7, var8, var12, var13, var14, var15, var11);
   }

   @Override
   protected void func_151554_b(World var1, Block var2, int var3, int var4, int var5, int var6, StructureBoundingBox var7) {
      Block var8 = this.func_151558_b(var2, var3);
      int var9 = this.func_151557_c(var2, var3);
      super.func_151554_b(var1, var8, var9, var4, var5, var6, var7);
   }
}
