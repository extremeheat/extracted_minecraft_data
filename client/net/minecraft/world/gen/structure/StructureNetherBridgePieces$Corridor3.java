package net.minecraft.world.gen.structure;

import java.util.List;
import java.util.Random;
import net.minecraft.init.Blocks;
import net.minecraft.world.World;

public class StructureNetherBridgePieces$Corridor3 extends StructureNetherBridgePieces$Piece {
   public StructureNetherBridgePieces$Corridor3() {
      super();
   }

   public StructureNetherBridgePieces$Corridor3(int var1, Random var2, StructureBoundingBox var3, int var4) {
      super(var1);
      this.field_74885_f = var4;
      this.field_74887_e = var3;
   }

   @Override
   public void func_74861_a(StructureComponent var1, List var2, Random var3) {
      this.func_74963_a((StructureNetherBridgePieces$Start)var1, var2, var3, 1, 0, true);
   }

   public static StructureNetherBridgePieces$Corridor3 func_74982_a(List var0, Random var1, int var2, int var3, int var4, int var5, int var6) {
      StructureBoundingBox var7 = StructureBoundingBox.func_78889_a(var2, var3, var4, -1, -7, 0, 5, 14, 10, var5);
      return func_74964_a(var7) && StructureComponent.func_74883_a(var0, var7) == null
         ? new StructureNetherBridgePieces$Corridor3(var6, var1, var7, var5)
         : null;
   }

   @Override
   public boolean func_74875_a(World var1, Random var2, StructureBoundingBox var3) {
      int var4 = this.func_151555_a(Blocks.field_150387_bl, 2);

      for(int var5 = 0; var5 <= 9; ++var5) {
         int var6 = Math.max(1, 7 - var5);
         int var7 = Math.min(Math.max(var6 + 5, 14 - var5), 13);
         int var8 = var5;
         this.func_151549_a(var1, var3, 0, 0, var5, 4, var6, var5, Blocks.field_150385_bj, Blocks.field_150385_bj, false);
         this.func_151549_a(var1, var3, 1, var6 + 1, var5, 3, var7 - 1, var5, Blocks.field_150350_a, Blocks.field_150350_a, false);
         if (var5 <= 6) {
            this.func_151550_a(var1, Blocks.field_150387_bl, var4, 1, var6 + 1, var5, var3);
            this.func_151550_a(var1, Blocks.field_150387_bl, var4, 2, var6 + 1, var5, var3);
            this.func_151550_a(var1, Blocks.field_150387_bl, var4, 3, var6 + 1, var5, var3);
         }

         this.func_151549_a(var1, var3, 0, var7, var5, 4, var7, var5, Blocks.field_150385_bj, Blocks.field_150385_bj, false);
         this.func_151549_a(var1, var3, 0, var6 + 1, var5, 0, var7 - 1, var5, Blocks.field_150385_bj, Blocks.field_150385_bj, false);
         this.func_151549_a(var1, var3, 4, var6 + 1, var5, 4, var7 - 1, var5, Blocks.field_150385_bj, Blocks.field_150385_bj, false);
         if ((var5 & 1) == 0) {
            this.func_151549_a(var1, var3, 0, var6 + 2, var5, 0, var6 + 3, var5, Blocks.field_150386_bk, Blocks.field_150386_bk, false);
            this.func_151549_a(var1, var3, 4, var6 + 2, var5, 4, var6 + 3, var5, Blocks.field_150386_bk, Blocks.field_150386_bk, false);
         }

         for(int var9 = 0; var9 <= 4; ++var9) {
            this.func_151554_b(var1, Blocks.field_150385_bj, 0, var9, -1, var8, var3);
         }
      }

      return true;
   }
}
