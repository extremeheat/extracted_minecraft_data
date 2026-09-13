package net.minecraft.world.gen.structure;

import java.util.List;
import java.util.Random;
import net.minecraft.init.Blocks;
import net.minecraft.world.World;

public class StructureNetherBridgePieces$Crossing extends StructureNetherBridgePieces$Piece {
   public StructureNetherBridgePieces$Crossing() {
      super();
   }

   public StructureNetherBridgePieces$Crossing(int var1, Random var2, StructureBoundingBox var3, int var4) {
      super(var1);
      this.field_74885_f = var4;
      this.field_74887_e = var3;
   }

   @Override
   public void func_74861_a(StructureComponent var1, List var2, Random var3) {
      this.func_74963_a((StructureNetherBridgePieces$Start)var1, var2, var3, 2, 0, false);
      this.func_74961_b((StructureNetherBridgePieces$Start)var1, var2, var3, 0, 2, false);
      this.func_74965_c((StructureNetherBridgePieces$Start)var1, var2, var3, 0, 2, false);
   }

   public static StructureNetherBridgePieces$Crossing func_74974_a(List var0, Random var1, int var2, int var3, int var4, int var5, int var6) {
      StructureBoundingBox var7 = StructureBoundingBox.func_78889_a(var2, var3, var4, -2, 0, 0, 7, 9, 7, var5);
      return func_74964_a(var7) && StructureComponent.func_74883_a(var0, var7) == null
         ? new StructureNetherBridgePieces$Crossing(var6, var1, var7, var5)
         : null;
   }

   @Override
   public boolean func_74875_a(World var1, Random var2, StructureBoundingBox var3) {
      this.func_151549_a(var1, var3, 0, 0, 0, 6, 1, 6, Blocks.field_150385_bj, Blocks.field_150385_bj, false);
      this.func_151549_a(var1, var3, 0, 2, 0, 6, 7, 6, Blocks.field_150350_a, Blocks.field_150350_a, false);
      this.func_151549_a(var1, var3, 0, 2, 0, 1, 6, 0, Blocks.field_150385_bj, Blocks.field_150385_bj, false);
      this.func_151549_a(var1, var3, 0, 2, 6, 1, 6, 6, Blocks.field_150385_bj, Blocks.field_150385_bj, false);
      this.func_151549_a(var1, var3, 5, 2, 0, 6, 6, 0, Blocks.field_150385_bj, Blocks.field_150385_bj, false);
      this.func_151549_a(var1, var3, 5, 2, 6, 6, 6, 6, Blocks.field_150385_bj, Blocks.field_150385_bj, false);
      this.func_151549_a(var1, var3, 0, 2, 0, 0, 6, 1, Blocks.field_150385_bj, Blocks.field_150385_bj, false);
      this.func_151549_a(var1, var3, 0, 2, 5, 0, 6, 6, Blocks.field_150385_bj, Blocks.field_150385_bj, false);
      this.func_151549_a(var1, var3, 6, 2, 0, 6, 6, 1, Blocks.field_150385_bj, Blocks.field_150385_bj, false);
      this.func_151549_a(var1, var3, 6, 2, 5, 6, 6, 6, Blocks.field_150385_bj, Blocks.field_150385_bj, false);
      this.func_151549_a(var1, var3, 2, 6, 0, 4, 6, 0, Blocks.field_150385_bj, Blocks.field_150385_bj, false);
      this.func_151549_a(var1, var3, 2, 5, 0, 4, 5, 0, Blocks.field_150386_bk, Blocks.field_150386_bk, false);
      this.func_151549_a(var1, var3, 2, 6, 6, 4, 6, 6, Blocks.field_150385_bj, Blocks.field_150385_bj, false);
      this.func_151549_a(var1, var3, 2, 5, 6, 4, 5, 6, Blocks.field_150386_bk, Blocks.field_150386_bk, false);
      this.func_151549_a(var1, var3, 0, 6, 2, 0, 6, 4, Blocks.field_150385_bj, Blocks.field_150385_bj, false);
      this.func_151549_a(var1, var3, 0, 5, 2, 0, 5, 4, Blocks.field_150386_bk, Blocks.field_150386_bk, false);
      this.func_151549_a(var1, var3, 6, 6, 2, 6, 6, 4, Blocks.field_150385_bj, Blocks.field_150385_bj, false);
      this.func_151549_a(var1, var3, 6, 5, 2, 6, 5, 4, Blocks.field_150386_bk, Blocks.field_150386_bk, false);

      for(int var4 = 0; var4 <= 6; ++var4) {
         for(int var5 = 0; var5 <= 6; ++var5) {
            this.func_151554_b(var1, Blocks.field_150385_bj, 0, var4, -1, var5, var3);
         }
      }

      return true;
   }
}
