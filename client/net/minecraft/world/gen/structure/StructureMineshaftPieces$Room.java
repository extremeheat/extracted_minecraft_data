package net.minecraft.world.gen.structure;

import java.util.LinkedList;
import java.util.List;
import java.util.Random;
import net.minecraft.init.Blocks;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.world.World;

public class StructureMineshaftPieces$Room extends StructureComponent {
   private List field_74949_a = new LinkedList();

   public StructureMineshaftPieces$Room() {
      super();
   }

   public StructureMineshaftPieces$Room(int var1, Random var2, int var3, int var4) {
      super(var1);
      this.field_74887_e = new StructureBoundingBox(var3, 50, var4, var3 + 7 + var2.nextInt(6), 54 + var2.nextInt(6), var4 + 7 + var2.nextInt(6));
   }

   @Override
   public void func_74861_a(StructureComponent var1, List var2, Random var3) {
      int var4 = this.func_74877_c();
      int var6 = this.field_74887_e.func_78882_c() - 3 - 1;
      if (var6 <= 0) {
         var6 = 1;
      }

      int var9;
      for(var9 = 0; var9 < this.field_74887_e.func_78883_b(); var9 += 4) {
         var9 += var3.nextInt(this.field_74887_e.func_78883_b());
         if (var9 + 3 > this.field_74887_e.func_78883_b()) {
            break;
         }

         StructureComponent var7 = StructureMineshaftPieces.access$000(
            var1,
            var2,
            var3,
            this.field_74887_e.field_78897_a + var9,
            this.field_74887_e.field_78895_b + var3.nextInt(var6) + 1,
            this.field_74887_e.field_78896_c - 1,
            2,
            var4
         );
         if (var7 != null) {
            StructureBoundingBox var8 = var7.func_74874_b();
            this.field_74949_a
               .add(
                  new StructureBoundingBox(
                     var8.field_78897_a,
                     var8.field_78895_b,
                     this.field_74887_e.field_78896_c,
                     var8.field_78893_d,
                     var8.field_78894_e,
                     this.field_74887_e.field_78896_c + 1
                  )
               );
         }
      }

      for(var9 = 0; var9 < this.field_74887_e.func_78883_b(); var9 += 4) {
         var9 += var3.nextInt(this.field_74887_e.func_78883_b());
         if (var9 + 3 > this.field_74887_e.func_78883_b()) {
            break;
         }

         StructureComponent var16 = StructureMineshaftPieces.access$000(
            var1,
            var2,
            var3,
            this.field_74887_e.field_78897_a + var9,
            this.field_74887_e.field_78895_b + var3.nextInt(var6) + 1,
            this.field_74887_e.field_78892_f + 1,
            0,
            var4
         );
         if (var16 != null) {
            StructureBoundingBox var19 = var16.func_74874_b();
            this.field_74949_a
               .add(
                  new StructureBoundingBox(
                     var19.field_78897_a,
                     var19.field_78895_b,
                     this.field_74887_e.field_78892_f - 1,
                     var19.field_78893_d,
                     var19.field_78894_e,
                     this.field_74887_e.field_78892_f
                  )
               );
         }
      }

      for(var9 = 0; var9 < this.field_74887_e.func_78880_d(); var9 += 4) {
         var9 += var3.nextInt(this.field_74887_e.func_78880_d());
         if (var9 + 3 > this.field_74887_e.func_78880_d()) {
            break;
         }

         StructureComponent var17 = StructureMineshaftPieces.access$000(
            var1,
            var2,
            var3,
            this.field_74887_e.field_78897_a - 1,
            this.field_74887_e.field_78895_b + var3.nextInt(var6) + 1,
            this.field_74887_e.field_78896_c + var9,
            1,
            var4
         );
         if (var17 != null) {
            StructureBoundingBox var20 = var17.func_74874_b();
            this.field_74949_a
               .add(
                  new StructureBoundingBox(
                     this.field_74887_e.field_78897_a,
                     var20.field_78895_b,
                     var20.field_78896_c,
                     this.field_74887_e.field_78897_a + 1,
                     var20.field_78894_e,
                     var20.field_78892_f
                  )
               );
         }
      }

      for(var9 = 0; var9 < this.field_74887_e.func_78880_d(); var9 += 4) {
         var9 += var3.nextInt(this.field_74887_e.func_78880_d());
         if (var9 + 3 > this.field_74887_e.func_78880_d()) {
            break;
         }

         StructureComponent var18 = StructureMineshaftPieces.access$000(
            var1,
            var2,
            var3,
            this.field_74887_e.field_78893_d + 1,
            this.field_74887_e.field_78895_b + var3.nextInt(var6) + 1,
            this.field_74887_e.field_78896_c + var9,
            3,
            var4
         );
         if (var18 != null) {
            StructureBoundingBox var21 = var18.func_74874_b();
            this.field_74949_a
               .add(
                  new StructureBoundingBox(
                     this.field_74887_e.field_78893_d - 1,
                     var21.field_78895_b,
                     var21.field_78896_c,
                     this.field_74887_e.field_78893_d,
                     var21.field_78894_e,
                     var21.field_78892_f
                  )
               );
         }
      }
   }

   @Override
   public boolean func_74875_a(World var1, Random var2, StructureBoundingBox var3) {
      if (this.func_74860_a(var1, var3)) {
         return false;
      } else {
         this.func_151549_a(
            var1,
            var3,
            this.field_74887_e.field_78897_a,
            this.field_74887_e.field_78895_b,
            this.field_74887_e.field_78896_c,
            this.field_74887_e.field_78893_d,
            this.field_74887_e.field_78895_b,
            this.field_74887_e.field_78892_f,
            Blocks.field_150346_d,
            Blocks.field_150350_a,
            true
         );
         this.func_151549_a(
            var1,
            var3,
            this.field_74887_e.field_78897_a,
            this.field_74887_e.field_78895_b + 1,
            this.field_74887_e.field_78896_c,
            this.field_74887_e.field_78893_d,
            Math.min(this.field_74887_e.field_78895_b + 3, this.field_74887_e.field_78894_e),
            this.field_74887_e.field_78892_f,
            Blocks.field_150350_a,
            Blocks.field_150350_a,
            false
         );

         for(StructureBoundingBox var5 : this.field_74949_a) {
            this.func_151549_a(
               var1,
               var3,
               var5.field_78897_a,
               var5.field_78894_e - 2,
               var5.field_78896_c,
               var5.field_78893_d,
               var5.field_78894_e,
               var5.field_78892_f,
               Blocks.field_150350_a,
               Blocks.field_150350_a,
               false
            );
         }

         this.func_151547_a(
            var1,
            var3,
            this.field_74887_e.field_78897_a,
            this.field_74887_e.field_78895_b + 4,
            this.field_74887_e.field_78896_c,
            this.field_74887_e.field_78893_d,
            this.field_74887_e.field_78894_e,
            this.field_74887_e.field_78892_f,
            Blocks.field_150350_a,
            false
         );
         return true;
      }
   }

   @Override
   protected void func_143012_a(NBTTagCompound var1) {
      NBTTagList var2 = new NBTTagList();

      for(StructureBoundingBox var4 : this.field_74949_a) {
         var2.func_74742_a(var4.func_151535_h());
      }

      var1.func_74782_a("Entrances", var2);
   }

   @Override
   protected void func_143011_b(NBTTagCompound var1) {
      NBTTagList var2 = var1.func_150295_c("Entrances", 11);

      for(int var3 = 0; var3 < var2.func_74745_c(); ++var3) {
         this.field_74949_a.add(new StructureBoundingBox(var2.func_150306_c(var3)));
      }
   }
}
