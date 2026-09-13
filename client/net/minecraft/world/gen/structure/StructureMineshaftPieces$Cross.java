package net.minecraft.world.gen.structure;

import java.util.List;
import java.util.Random;
import net.minecraft.block.material.Material;
import net.minecraft.init.Blocks;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;

public class StructureMineshaftPieces$Cross extends StructureComponent {
   private int field_74953_a;
   private boolean field_74952_b;

   public StructureMineshaftPieces$Cross() {
      super();
   }

   @Override
   protected void func_143012_a(NBTTagCompound var1) {
      var1.func_74757_a("tf", this.field_74952_b);
      var1.func_74768_a("D", this.field_74953_a);
   }

   @Override
   protected void func_143011_b(NBTTagCompound var1) {
      this.field_74952_b = var1.func_74767_n("tf");
      this.field_74953_a = var1.func_74762_e("D");
   }

   public StructureMineshaftPieces$Cross(int var1, Random var2, StructureBoundingBox var3, int var4) {
      super(var1);
      this.field_74953_a = var4;
      this.field_74887_e = var3;
      this.field_74952_b = var3.func_78882_c() > 3;
   }

   public static StructureBoundingBox func_74951_a(List var0, Random var1, int var2, int var3, int var4, int var5) {
      StructureBoundingBox var6 = new StructureBoundingBox(var2, var3, var4, var2, var3 + 2, var4);
      if (var1.nextInt(4) == 0) {
         var6.field_78894_e += 4;
      }

      switch(var5) {
         case 0:
            var6.field_78897_a = var2 - 1;
            var6.field_78893_d = var2 + 3;
            var6.field_78892_f = var4 + 4;
            break;
         case 1:
            var6.field_78897_a = var2 - 4;
            var6.field_78896_c = var4 - 1;
            var6.field_78892_f = var4 + 3;
            break;
         case 2:
            var6.field_78897_a = var2 - 1;
            var6.field_78893_d = var2 + 3;
            var6.field_78896_c = var4 - 4;
            break;
         case 3:
            var6.field_78893_d = var2 + 4;
            var6.field_78896_c = var4 - 1;
            var6.field_78892_f = var4 + 3;
      }

      return StructureComponent.func_74883_a(var0, var6) != null ? null : var6;
   }

   @Override
   public void func_74861_a(StructureComponent var1, List var2, Random var3) {
      int var4 = this.func_74877_c();
      switch(this.field_74953_a) {
         case 0:
            StructureMineshaftPieces.access$000(
               var1, var2, var3, this.field_74887_e.field_78897_a + 1, this.field_74887_e.field_78895_b, this.field_74887_e.field_78892_f + 1, 0, var4
            );
            StructureMineshaftPieces.access$000(
               var1, var2, var3, this.field_74887_e.field_78897_a - 1, this.field_74887_e.field_78895_b, this.field_74887_e.field_78896_c + 1, 1, var4
            );
            StructureMineshaftPieces.access$000(
               var1, var2, var3, this.field_74887_e.field_78893_d + 1, this.field_74887_e.field_78895_b, this.field_74887_e.field_78896_c + 1, 3, var4
            );
            break;
         case 1:
            StructureMineshaftPieces.access$000(
               var1, var2, var3, this.field_74887_e.field_78897_a + 1, this.field_74887_e.field_78895_b, this.field_74887_e.field_78896_c - 1, 2, var4
            );
            StructureMineshaftPieces.access$000(
               var1, var2, var3, this.field_74887_e.field_78897_a + 1, this.field_74887_e.field_78895_b, this.field_74887_e.field_78892_f + 1, 0, var4
            );
            StructureMineshaftPieces.access$000(
               var1, var2, var3, this.field_74887_e.field_78897_a - 1, this.field_74887_e.field_78895_b, this.field_74887_e.field_78896_c + 1, 1, var4
            );
            break;
         case 2:
            StructureMineshaftPieces.access$000(
               var1, var2, var3, this.field_74887_e.field_78897_a + 1, this.field_74887_e.field_78895_b, this.field_74887_e.field_78896_c - 1, 2, var4
            );
            StructureMineshaftPieces.access$000(
               var1, var2, var3, this.field_74887_e.field_78897_a - 1, this.field_74887_e.field_78895_b, this.field_74887_e.field_78896_c + 1, 1, var4
            );
            StructureMineshaftPieces.access$000(
               var1, var2, var3, this.field_74887_e.field_78893_d + 1, this.field_74887_e.field_78895_b, this.field_74887_e.field_78896_c + 1, 3, var4
            );
            break;
         case 3:
            StructureMineshaftPieces.access$000(
               var1, var2, var3, this.field_74887_e.field_78897_a + 1, this.field_74887_e.field_78895_b, this.field_74887_e.field_78896_c - 1, 2, var4
            );
            StructureMineshaftPieces.access$000(
               var1, var2, var3, this.field_74887_e.field_78897_a + 1, this.field_74887_e.field_78895_b, this.field_74887_e.field_78892_f + 1, 0, var4
            );
            StructureMineshaftPieces.access$000(
               var1, var2, var3, this.field_74887_e.field_78893_d + 1, this.field_74887_e.field_78895_b, this.field_74887_e.field_78896_c + 1, 3, var4
            );
      }

      if (this.field_74952_b) {
         if (var3.nextBoolean()) {
            StructureMineshaftPieces.access$000(
               var1, var2, var3, this.field_74887_e.field_78897_a + 1, this.field_74887_e.field_78895_b + 3 + 1, this.field_74887_e.field_78896_c - 1, 2, var4
            );
         }

         if (var3.nextBoolean()) {
            StructureMineshaftPieces.access$000(
               var1, var2, var3, this.field_74887_e.field_78897_a - 1, this.field_74887_e.field_78895_b + 3 + 1, this.field_74887_e.field_78896_c + 1, 1, var4
            );
         }

         if (var3.nextBoolean()) {
            StructureMineshaftPieces.access$000(
               var1, var2, var3, this.field_74887_e.field_78893_d + 1, this.field_74887_e.field_78895_b + 3 + 1, this.field_74887_e.field_78896_c + 1, 3, var4
            );
         }

         if (var3.nextBoolean()) {
            StructureMineshaftPieces.access$000(
               var1, var2, var3, this.field_74887_e.field_78897_a + 1, this.field_74887_e.field_78895_b + 3 + 1, this.field_74887_e.field_78892_f + 1, 0, var4
            );
         }
      }
   }

   @Override
   public boolean func_74875_a(World var1, Random var2, StructureBoundingBox var3) {
      if (this.func_74860_a(var1, var3)) {
         return false;
      } else {
         if (this.field_74952_b) {
            this.func_151549_a(
               var1,
               var3,
               this.field_74887_e.field_78897_a + 1,
               this.field_74887_e.field_78895_b,
               this.field_74887_e.field_78896_c,
               this.field_74887_e.field_78893_d - 1,
               this.field_74887_e.field_78895_b + 3 - 1,
               this.field_74887_e.field_78892_f,
               Blocks.field_150350_a,
               Blocks.field_150350_a,
               false
            );
            this.func_151549_a(
               var1,
               var3,
               this.field_74887_e.field_78897_a,
               this.field_74887_e.field_78895_b,
               this.field_74887_e.field_78896_c + 1,
               this.field_74887_e.field_78893_d,
               this.field_74887_e.field_78895_b + 3 - 1,
               this.field_74887_e.field_78892_f - 1,
               Blocks.field_150350_a,
               Blocks.field_150350_a,
               false
            );
            this.func_151549_a(
               var1,
               var3,
               this.field_74887_e.field_78897_a + 1,
               this.field_74887_e.field_78894_e - 2,
               this.field_74887_e.field_78896_c,
               this.field_74887_e.field_78893_d - 1,
               this.field_74887_e.field_78894_e,
               this.field_74887_e.field_78892_f,
               Blocks.field_150350_a,
               Blocks.field_150350_a,
               false
            );
            this.func_151549_a(
               var1,
               var3,
               this.field_74887_e.field_78897_a,
               this.field_74887_e.field_78894_e - 2,
               this.field_74887_e.field_78896_c + 1,
               this.field_74887_e.field_78893_d,
               this.field_74887_e.field_78894_e,
               this.field_74887_e.field_78892_f - 1,
               Blocks.field_150350_a,
               Blocks.field_150350_a,
               false
            );
            this.func_151549_a(
               var1,
               var3,
               this.field_74887_e.field_78897_a + 1,
               this.field_74887_e.field_78895_b + 3,
               this.field_74887_e.field_78896_c + 1,
               this.field_74887_e.field_78893_d - 1,
               this.field_74887_e.field_78895_b + 3,
               this.field_74887_e.field_78892_f - 1,
               Blocks.field_150350_a,
               Blocks.field_150350_a,
               false
            );
         } else {
            this.func_151549_a(
               var1,
               var3,
               this.field_74887_e.field_78897_a + 1,
               this.field_74887_e.field_78895_b,
               this.field_74887_e.field_78896_c,
               this.field_74887_e.field_78893_d - 1,
               this.field_74887_e.field_78894_e,
               this.field_74887_e.field_78892_f,
               Blocks.field_150350_a,
               Blocks.field_150350_a,
               false
            );
            this.func_151549_a(
               var1,
               var3,
               this.field_74887_e.field_78897_a,
               this.field_74887_e.field_78895_b,
               this.field_74887_e.field_78896_c + 1,
               this.field_74887_e.field_78893_d,
               this.field_74887_e.field_78894_e,
               this.field_74887_e.field_78892_f - 1,
               Blocks.field_150350_a,
               Blocks.field_150350_a,
               false
            );
         }

         this.func_151549_a(
            var1,
            var3,
            this.field_74887_e.field_78897_a + 1,
            this.field_74887_e.field_78895_b,
            this.field_74887_e.field_78896_c + 1,
            this.field_74887_e.field_78897_a + 1,
            this.field_74887_e.field_78894_e,
            this.field_74887_e.field_78896_c + 1,
            Blocks.field_150344_f,
            Blocks.field_150350_a,
            false
         );
         this.func_151549_a(
            var1,
            var3,
            this.field_74887_e.field_78897_a + 1,
            this.field_74887_e.field_78895_b,
            this.field_74887_e.field_78892_f - 1,
            this.field_74887_e.field_78897_a + 1,
            this.field_74887_e.field_78894_e,
            this.field_74887_e.field_78892_f - 1,
            Blocks.field_150344_f,
            Blocks.field_150350_a,
            false
         );
         this.func_151549_a(
            var1,
            var3,
            this.field_74887_e.field_78893_d - 1,
            this.field_74887_e.field_78895_b,
            this.field_74887_e.field_78896_c + 1,
            this.field_74887_e.field_78893_d - 1,
            this.field_74887_e.field_78894_e,
            this.field_74887_e.field_78896_c + 1,
            Blocks.field_150344_f,
            Blocks.field_150350_a,
            false
         );
         this.func_151549_a(
            var1,
            var3,
            this.field_74887_e.field_78893_d - 1,
            this.field_74887_e.field_78895_b,
            this.field_74887_e.field_78892_f - 1,
            this.field_74887_e.field_78893_d - 1,
            this.field_74887_e.field_78894_e,
            this.field_74887_e.field_78892_f - 1,
            Blocks.field_150344_f,
            Blocks.field_150350_a,
            false
         );

         for(int var4 = this.field_74887_e.field_78897_a; var4 <= this.field_74887_e.field_78893_d; ++var4) {
            for(int var5 = this.field_74887_e.field_78896_c; var5 <= this.field_74887_e.field_78892_f; ++var5) {
               if (this.func_151548_a(var1, var4, this.field_74887_e.field_78895_b - 1, var5, var3).func_149688_o() == Material.field_151579_a) {
                  this.func_151550_a(var1, Blocks.field_150344_f, 0, var4, this.field_74887_e.field_78895_b - 1, var5, var3);
               }
            }
         }

         return true;
      }
   }
}
