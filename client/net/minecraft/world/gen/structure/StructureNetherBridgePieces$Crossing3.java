package net.minecraft.world.gen.structure;

import java.util.List;
import java.util.Random;
import net.minecraft.init.Blocks;
import net.minecraft.world.World;

public class StructureNetherBridgePieces$Crossing3 extends StructureNetherBridgePieces$Piece {
   public StructureNetherBridgePieces$Crossing3() {
      super();
   }

   public StructureNetherBridgePieces$Crossing3(int var1, Random var2, StructureBoundingBox var3, int var4) {
      super(var1);
      this.field_74885_f = var4;
      this.field_74887_e = var3;
   }

   protected StructureNetherBridgePieces$Crossing3(Random var1, int var2, int var3) {
      super(0);
      this.field_74885_f = var1.nextInt(4);
      switch(this.field_74885_f) {
         case 0:
         case 2:
            this.field_74887_e = new StructureBoundingBox(var2, 64, var3, var2 + 19 - 1, 73, var3 + 19 - 1);
            break;
         default:
            this.field_74887_e = new StructureBoundingBox(var2, 64, var3, var2 + 19 - 1, 73, var3 + 19 - 1);
      }
   }

   @Override
   public void func_74861_a(StructureComponent var1, List var2, Random var3) {
      this.func_74963_a((StructureNetherBridgePieces$Start)var1, var2, var3, 8, 3, false);
      this.func_74961_b((StructureNetherBridgePieces$Start)var1, var2, var3, 3, 8, false);
      this.func_74965_c((StructureNetherBridgePieces$Start)var1, var2, var3, 3, 8, false);
   }

   public static StructureNetherBridgePieces$Crossing3 func_74966_a(List var0, Random var1, int var2, int var3, int var4, int var5, int var6) {
      StructureBoundingBox var7 = StructureBoundingBox.func_78889_a(var2, var3, var4, -8, -3, 0, 19, 10, 19, var5);
      return func_74964_a(var7) && StructureComponent.func_74883_a(var0, var7) == null
         ? new StructureNetherBridgePieces$Crossing3(var6, var1, var7, var5)
         : null;
   }

   @Override
   public boolean func_74875_a(World var1, Random var2, StructureBoundingBox var3) {
      this.func_151549_a(var1, var3, 7, 3, 0, 11, 4, 18, Blocks.field_150385_bj, Blocks.field_150385_bj, false);
      this.func_151549_a(var1, var3, 0, 3, 7, 18, 4, 11, Blocks.field_150385_bj, Blocks.field_150385_bj, false);
      this.func_151549_a(var1, var3, 8, 5, 0, 10, 7, 18, Blocks.field_150350_a, Blocks.field_150350_a, false);
      this.func_151549_a(var1, var3, 0, 5, 8, 18, 7, 10, Blocks.field_150350_a, Blocks.field_150350_a, false);
      this.func_151549_a(var1, var3, 7, 5, 0, 7, 5, 7, Blocks.field_150385_bj, Blocks.field_150385_bj, false);
      this.func_151549_a(var1, var3, 7, 5, 11, 7, 5, 18, Blocks.field_150385_bj, Blocks.field_150385_bj, false);
      this.func_151549_a(var1, var3, 11, 5, 0, 11, 5, 7, Blocks.field_150385_bj, Blocks.field_150385_bj, false);
      this.func_151549_a(var1, var3, 11, 5, 11, 11, 5, 18, Blocks.field_150385_bj, Blocks.field_150385_bj, false);
      this.func_151549_a(var1, var3, 0, 5, 7, 7, 5, 7, Blocks.field_150385_bj, Blocks.field_150385_bj, false);
      this.func_151549_a(var1, var3, 11, 5, 7, 18, 5, 7, Blocks.field_150385_bj, Blocks.field_150385_bj, false);
      this.func_151549_a(var1, var3, 0, 5, 11, 7, 5, 11, Blocks.field_150385_bj, Blocks.field_150385_bj, false);
      this.func_151549_a(var1, var3, 11, 5, 11, 18, 5, 11, Blocks.field_150385_bj, Blocks.field_150385_bj, false);
      this.func_151549_a(var1, var3, 7, 2, 0, 11, 2, 5, Blocks.field_150385_bj, Blocks.field_150385_bj, false);
      this.func_151549_a(var1, var3, 7, 2, 13, 11, 2, 18, Blocks.field_150385_bj, Blocks.field_150385_bj, false);
      this.func_151549_a(var1, var3, 7, 0, 0, 11, 1, 3, Blocks.field_150385_bj, Blocks.field_150385_bj, false);
      this.func_151549_a(var1, var3, 7, 0, 15, 11, 1, 18, Blocks.field_150385_bj, Blocks.field_150385_bj, false);

      for(int var4 = 7; var4 <= 11; ++var4) {
         for(int var5 = 0; var5 <= 2; ++var5) {
            this.func_151554_b(var1, Blocks.field_150385_bj, 0, var4, -1, var5, var3);
            this.func_151554_b(var1, Blocks.field_150385_bj, 0, var4, -1, 18 - var5, var3);
         }
      }

      this.func_151549_a(var1, var3, 0, 2, 7, 5, 2, 11, Blocks.field_150385_bj, Blocks.field_150385_bj, false);
      this.func_151549_a(var1, var3, 13, 2, 7, 18, 2, 11, Blocks.field_150385_bj, Blocks.field_150385_bj, false);
      this.func_151549_a(var1, var3, 0, 0, 7, 3, 1, 11, Blocks.field_150385_bj, Blocks.field_150385_bj, false);
      this.func_151549_a(var1, var3, 15, 0, 7, 18, 1, 11, Blocks.field_150385_bj, Blocks.field_150385_bj, false);

      for(int var6 = 0; var6 <= 2; ++var6) {
         for(int var7 = 7; var7 <= 11; ++var7) {
            this.func_151554_b(var1, Blocks.field_150385_bj, 0, var6, -1, var7, var3);
            this.func_151554_b(var1, Blocks.field_150385_bj, 0, 18 - var6, -1, var7, var3);
         }
      }

      return true;
   }
}
