package net.minecraft.world.gen.structure;

import java.util.List;
import java.util.Random;
import net.minecraft.init.Blocks;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;

public class StructureMineshaftPieces$Stairs extends StructureComponent {
   public StructureMineshaftPieces$Stairs() {
      super();
   }

   public StructureMineshaftPieces$Stairs(int var1, Random var2, StructureBoundingBox var3, int var4) {
      super(var1);
      this.field_74885_f = var4;
      this.field_74887_e = var3;
   }

   @Override
   protected void func_143012_a(NBTTagCompound var1) {
   }

   @Override
   protected void func_143011_b(NBTTagCompound var1) {
   }

   public static StructureBoundingBox func_74950_a(List var0, Random var1, int var2, int var3, int var4, int var5) {
      StructureBoundingBox var6 = new StructureBoundingBox(var2, var3 - 5, var4, var2, var3 + 2, var4);
      switch(var5) {
         case 0:
            var6.field_78893_d = var2 + 2;
            var6.field_78892_f = var4 + 8;
            break;
         case 1:
            var6.field_78897_a = var2 - 8;
            var6.field_78892_f = var4 + 2;
            break;
         case 2:
            var6.field_78893_d = var2 + 2;
            var6.field_78896_c = var4 - 8;
            break;
         case 3:
            var6.field_78893_d = var2 + 8;
            var6.field_78892_f = var4 + 2;
      }

      return StructureComponent.func_74883_a(var0, var6) != null ? null : var6;
   }

   @Override
   public void func_74861_a(StructureComponent var1, List var2, Random var3) {
      int var4 = this.func_74877_c();
      switch(this.field_74885_f) {
         case 0:
            StructureMineshaftPieces.access$000(
               var1, var2, var3, this.field_74887_e.field_78897_a, this.field_74887_e.field_78895_b, this.field_74887_e.field_78892_f + 1, 0, var4
            );
            break;
         case 1:
            StructureMineshaftPieces.access$000(
               var1, var2, var3, this.field_74887_e.field_78897_a - 1, this.field_74887_e.field_78895_b, this.field_74887_e.field_78896_c, 1, var4
            );
            break;
         case 2:
            StructureMineshaftPieces.access$000(
               var1, var2, var3, this.field_74887_e.field_78897_a, this.field_74887_e.field_78895_b, this.field_74887_e.field_78896_c - 1, 2, var4
            );
            break;
         case 3:
            StructureMineshaftPieces.access$000(
               var1, var2, var3, this.field_74887_e.field_78893_d + 1, this.field_74887_e.field_78895_b, this.field_74887_e.field_78896_c, 3, var4
            );
      }
   }

   @Override
   public boolean func_74875_a(World var1, Random var2, StructureBoundingBox var3) {
      if (this.func_74860_a(var1, var3)) {
         return false;
      } else {
         this.func_151549_a(var1, var3, 0, 5, 0, 2, 7, 1, Blocks.field_150350_a, Blocks.field_150350_a, false);
         this.func_151549_a(var1, var3, 0, 0, 7, 2, 2, 8, Blocks.field_150350_a, Blocks.field_150350_a, false);

         for(int var4 = 0; var4 < 5; ++var4) {
            this.func_151549_a(
               var1, var3, 0, 5 - var4 - (var4 < 4 ? 1 : 0), 2 + var4, 2, 7 - var4, 2 + var4, Blocks.field_150350_a, Blocks.field_150350_a, false
            );
         }

         return true;
      }
   }
}
