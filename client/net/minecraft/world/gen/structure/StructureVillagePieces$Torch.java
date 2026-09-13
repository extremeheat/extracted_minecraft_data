package net.minecraft.world.gen.structure;

import java.util.List;
import java.util.Random;
import net.minecraft.init.Blocks;
import net.minecraft.world.World;

public class StructureVillagePieces$Torch extends StructureVillagePieces$Village {
   public StructureVillagePieces$Torch() {
      super();
   }

   public StructureVillagePieces$Torch(StructureVillagePieces$Start var1, int var2, Random var3, StructureBoundingBox var4, int var5) {
      super(var1, var2);
      this.field_74885_f = var5;
      this.field_74887_e = var4;
   }

   public static StructureBoundingBox func_74904_a(StructureVillagePieces$Start var0, List var1, Random var2, int var3, int var4, int var5, int var6) {
      StructureBoundingBox var7 = StructureBoundingBox.func_78889_a(var3, var4, var5, 0, 0, 0, 3, 4, 2, var6);
      return StructureComponent.func_74883_a(var1, var7) != null ? null : var7;
   }

   @Override
   public boolean func_74875_a(World var1, Random var2, StructureBoundingBox var3) {
      if (this.field_143015_k < 0) {
         this.field_143015_k = this.func_74889_b(var1, var3);
         if (this.field_143015_k < 0) {
            return true;
         }

         this.field_74887_e.func_78886_a(0, this.field_143015_k - this.field_74887_e.field_78894_e + 4 - 1, 0);
      }

      this.func_151549_a(var1, var3, 0, 0, 0, 2, 3, 1, Blocks.field_150350_a, Blocks.field_150350_a, false);
      this.func_151550_a(var1, Blocks.field_150422_aJ, 0, 1, 0, 0, var3);
      this.func_151550_a(var1, Blocks.field_150422_aJ, 0, 1, 1, 0, var3);
      this.func_151550_a(var1, Blocks.field_150422_aJ, 0, 1, 2, 0, var3);
      this.func_151550_a(var1, Blocks.field_150325_L, 15, 1, 3, 0, var3);
      this.func_151550_a(var1, Blocks.field_150478_aa, 0, 0, 3, 0, var3);
      this.func_151550_a(var1, Blocks.field_150478_aa, 0, 1, 3, 1, var3);
      this.func_151550_a(var1, Blocks.field_150478_aa, 0, 2, 3, 0, var3);
      this.func_151550_a(var1, Blocks.field_150478_aa, 0, 1, 3, -1, var3);
      return true;
   }
}
