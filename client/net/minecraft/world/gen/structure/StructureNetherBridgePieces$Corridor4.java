package net.minecraft.world.gen.structure;

import java.util.List;
import java.util.Random;
import net.minecraft.init.Blocks;
import net.minecraft.world.World;

public class StructureNetherBridgePieces$Corridor4 extends StructureNetherBridgePieces$Piece {
   public StructureNetherBridgePieces$Corridor4() {
      super();
   }

   public StructureNetherBridgePieces$Corridor4(int var1, Random var2, StructureBoundingBox var3, int var4) {
      super(var1);
      this.field_74885_f = var4;
      this.field_74887_e = var3;
   }

   @Override
   public void func_74861_a(StructureComponent var1, List var2, Random var3) {
      byte var4 = 1;
      if (this.field_74885_f == 1 || this.field_74885_f == 2) {
         var4 = 5;
      }

      this.func_74961_b((StructureNetherBridgePieces$Start)var1, var2, var3, 0, var4, var3.nextInt(8) > 0);
      this.func_74965_c((StructureNetherBridgePieces$Start)var1, var2, var3, 0, var4, var3.nextInt(8) > 0);
   }

   public static StructureNetherBridgePieces$Corridor4 func_74985_a(List var0, Random var1, int var2, int var3, int var4, int var5, int var6) {
      StructureBoundingBox var7 = StructureBoundingBox.func_78889_a(var2, var3, var4, -3, 0, 0, 9, 7, 9, var5);
      return func_74964_a(var7) && StructureComponent.func_74883_a(var0, var7) == null
         ? new StructureNetherBridgePieces$Corridor4(var6, var1, var7, var5)
         : null;
   }

   @Override
   public boolean func_74875_a(World var1, Random var2, StructureBoundingBox var3) {
      this.func_151549_a(var1, var3, 0, 0, 0, 8, 1, 8, Blocks.field_150385_bj, Blocks.field_150385_bj, false);
      this.func_151549_a(var1, var3, 0, 2, 0, 8, 5, 8, Blocks.field_150350_a, Blocks.field_150350_a, false);
      this.func_151549_a(var1, var3, 0, 6, 0, 8, 6, 5, Blocks.field_150385_bj, Blocks.field_150385_bj, false);
      this.func_151549_a(var1, var3, 0, 2, 0, 2, 5, 0, Blocks.field_150385_bj, Blocks.field_150385_bj, false);
      this.func_151549_a(var1, var3, 6, 2, 0, 8, 5, 0, Blocks.field_150385_bj, Blocks.field_150385_bj, false);
      this.func_151549_a(var1, var3, 1, 3, 0, 1, 4, 0, Blocks.field_150386_bk, Blocks.field_150386_bk, false);
      this.func_151549_a(var1, var3, 7, 3, 0, 7, 4, 0, Blocks.field_150386_bk, Blocks.field_150386_bk, false);
      this.func_151549_a(var1, var3, 0, 2, 4, 8, 2, 8, Blocks.field_150385_bj, Blocks.field_150385_bj, false);
      this.func_151549_a(var1, var3, 1, 1, 4, 2, 2, 4, Blocks.field_150350_a, Blocks.field_150350_a, false);
      this.func_151549_a(var1, var3, 6, 1, 4, 7, 2, 4, Blocks.field_150350_a, Blocks.field_150350_a, false);
      this.func_151549_a(var1, var3, 0, 3, 8, 8, 3, 8, Blocks.field_150386_bk, Blocks.field_150386_bk, false);
      this.func_151549_a(var1, var3, 0, 3, 6, 0, 3, 7, Blocks.field_150386_bk, Blocks.field_150386_bk, false);
      this.func_151549_a(var1, var3, 8, 3, 6, 8, 3, 7, Blocks.field_150386_bk, Blocks.field_150386_bk, false);
      this.func_151549_a(var1, var3, 0, 3, 4, 0, 5, 5, Blocks.field_150385_bj, Blocks.field_150385_bj, false);
      this.func_151549_a(var1, var3, 8, 3, 4, 8, 5, 5, Blocks.field_150385_bj, Blocks.field_150385_bj, false);
      this.func_151549_a(var1, var3, 1, 3, 5, 2, 5, 5, Blocks.field_150385_bj, Blocks.field_150385_bj, false);
      this.func_151549_a(var1, var3, 6, 3, 5, 7, 5, 5, Blocks.field_150385_bj, Blocks.field_150385_bj, false);
      this.func_151549_a(var1, var3, 1, 4, 5, 1, 5, 5, Blocks.field_150386_bk, Blocks.field_150386_bk, false);
      this.func_151549_a(var1, var3, 7, 4, 5, 7, 5, 5, Blocks.field_150386_bk, Blocks.field_150386_bk, false);

      for(int var4 = 0; var4 <= 5; ++var4) {
         for(int var5 = 0; var5 <= 8; ++var5) {
            this.func_151554_b(var1, Blocks.field_150385_bj, 0, var5, -1, var4, var3);
         }
      }

      return true;
   }
}
