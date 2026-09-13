package net.minecraft.world.gen.structure;

import java.util.List;
import java.util.Random;
import net.minecraft.init.Blocks;
import net.minecraft.world.World;

public class StructureVillagePieces$Well extends StructureVillagePieces$Village {
   public StructureVillagePieces$Well() {
      super();
   }

   public StructureVillagePieces$Well(StructureVillagePieces$Start var1, int var2, Random var3, int var4, int var5) {
      super(var1, var2);
      this.field_74885_f = var3.nextInt(4);
      switch(this.field_74885_f) {
         case 0:
         case 2:
            this.field_74887_e = new StructureBoundingBox(var4, 64, var5, var4 + 6 - 1, 78, var5 + 6 - 1);
            break;
         default:
            this.field_74887_e = new StructureBoundingBox(var4, 64, var5, var4 + 6 - 1, 78, var5 + 6 - 1);
      }
   }

   @Override
   public void func_74861_a(StructureComponent var1, List var2, Random var3) {
      StructureVillagePieces.access$100(
         (StructureVillagePieces$Start)var1,
         var2,
         var3,
         this.field_74887_e.field_78897_a - 1,
         this.field_74887_e.field_78894_e - 4,
         this.field_74887_e.field_78896_c + 1,
         1,
         this.func_74877_c()
      );
      StructureVillagePieces.access$100(
         (StructureVillagePieces$Start)var1,
         var2,
         var3,
         this.field_74887_e.field_78893_d + 1,
         this.field_74887_e.field_78894_e - 4,
         this.field_74887_e.field_78896_c + 1,
         3,
         this.func_74877_c()
      );
      StructureVillagePieces.access$100(
         (StructureVillagePieces$Start)var1,
         var2,
         var3,
         this.field_74887_e.field_78897_a + 1,
         this.field_74887_e.field_78894_e - 4,
         this.field_74887_e.field_78896_c - 1,
         2,
         this.func_74877_c()
      );
      StructureVillagePieces.access$100(
         (StructureVillagePieces$Start)var1,
         var2,
         var3,
         this.field_74887_e.field_78897_a + 1,
         this.field_74887_e.field_78894_e - 4,
         this.field_74887_e.field_78892_f + 1,
         0,
         this.func_74877_c()
      );
   }

   @Override
   public boolean func_74875_a(World var1, Random var2, StructureBoundingBox var3) {
      if (this.field_143015_k < 0) {
         this.field_143015_k = this.func_74889_b(var1, var3);
         if (this.field_143015_k < 0) {
            return true;
         }

         this.field_74887_e.func_78886_a(0, this.field_143015_k - this.field_74887_e.field_78894_e + 3, 0);
      }

      this.func_151549_a(var1, var3, 1, 0, 1, 4, 12, 4, Blocks.field_150347_e, Blocks.field_150358_i, false);
      this.func_151550_a(var1, Blocks.field_150350_a, 0, 2, 12, 2, var3);
      this.func_151550_a(var1, Blocks.field_150350_a, 0, 3, 12, 2, var3);
      this.func_151550_a(var1, Blocks.field_150350_a, 0, 2, 12, 3, var3);
      this.func_151550_a(var1, Blocks.field_150350_a, 0, 3, 12, 3, var3);
      this.func_151550_a(var1, Blocks.field_150422_aJ, 0, 1, 13, 1, var3);
      this.func_151550_a(var1, Blocks.field_150422_aJ, 0, 1, 14, 1, var3);
      this.func_151550_a(var1, Blocks.field_150422_aJ, 0, 4, 13, 1, var3);
      this.func_151550_a(var1, Blocks.field_150422_aJ, 0, 4, 14, 1, var3);
      this.func_151550_a(var1, Blocks.field_150422_aJ, 0, 1, 13, 4, var3);
      this.func_151550_a(var1, Blocks.field_150422_aJ, 0, 1, 14, 4, var3);
      this.func_151550_a(var1, Blocks.field_150422_aJ, 0, 4, 13, 4, var3);
      this.func_151550_a(var1, Blocks.field_150422_aJ, 0, 4, 14, 4, var3);
      this.func_151549_a(var1, var3, 1, 15, 1, 4, 15, 4, Blocks.field_150347_e, Blocks.field_150347_e, false);

      for(int var4 = 0; var4 <= 5; ++var4) {
         for(int var5 = 0; var5 <= 5; ++var5) {
            if (var5 == 0 || var5 == 5 || var4 == 0 || var4 == 5) {
               this.func_151550_a(var1, Blocks.field_150351_n, 0, var5, 11, var4, var3);
               this.func_74871_b(var1, var5, 12, var4, var3);
            }
         }
      }

      return true;
   }
}
