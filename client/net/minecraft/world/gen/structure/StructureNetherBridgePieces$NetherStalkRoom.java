package net.minecraft.world.gen.structure;

import java.util.List;
import java.util.Random;
import net.minecraft.init.Blocks;
import net.minecraft.world.World;

public class StructureNetherBridgePieces$NetherStalkRoom extends StructureNetherBridgePieces$Piece {
   public StructureNetherBridgePieces$NetherStalkRoom() {
      super();
   }

   public StructureNetherBridgePieces$NetherStalkRoom(int var1, Random var2, StructureBoundingBox var3, int var4) {
      super(var1);
      this.field_74885_f = var4;
      this.field_74887_e = var3;
   }

   @Override
   public void func_74861_a(StructureComponent var1, List var2, Random var3) {
      this.func_74963_a((StructureNetherBridgePieces$Start)var1, var2, var3, 5, 3, true);
      this.func_74963_a((StructureNetherBridgePieces$Start)var1, var2, var3, 5, 11, true);
   }

   public static StructureNetherBridgePieces$NetherStalkRoom func_74977_a(List var0, Random var1, int var2, int var3, int var4, int var5, int var6) {
      StructureBoundingBox var7 = StructureBoundingBox.func_78889_a(var2, var3, var4, -5, -3, 0, 13, 14, 13, var5);
      return func_74964_a(var7) && StructureComponent.func_74883_a(var0, var7) == null
         ? new StructureNetherBridgePieces$NetherStalkRoom(var6, var1, var7, var5)
         : null;
   }

   @Override
   public boolean func_74875_a(World var1, Random var2, StructureBoundingBox var3) {
      this.func_151549_a(var1, var3, 0, 3, 0, 12, 4, 12, Blocks.field_150385_bj, Blocks.field_150385_bj, false);
      this.func_151549_a(var1, var3, 0, 5, 0, 12, 13, 12, Blocks.field_150350_a, Blocks.field_150350_a, false);
      this.func_151549_a(var1, var3, 0, 5, 0, 1, 12, 12, Blocks.field_150385_bj, Blocks.field_150385_bj, false);
      this.func_151549_a(var1, var3, 11, 5, 0, 12, 12, 12, Blocks.field_150385_bj, Blocks.field_150385_bj, false);
      this.func_151549_a(var1, var3, 2, 5, 11, 4, 12, 12, Blocks.field_150385_bj, Blocks.field_150385_bj, false);
      this.func_151549_a(var1, var3, 8, 5, 11, 10, 12, 12, Blocks.field_150385_bj, Blocks.field_150385_bj, false);
      this.func_151549_a(var1, var3, 5, 9, 11, 7, 12, 12, Blocks.field_150385_bj, Blocks.field_150385_bj, false);
      this.func_151549_a(var1, var3, 2, 5, 0, 4, 12, 1, Blocks.field_150385_bj, Blocks.field_150385_bj, false);
      this.func_151549_a(var1, var3, 8, 5, 0, 10, 12, 1, Blocks.field_150385_bj, Blocks.field_150385_bj, false);
      this.func_151549_a(var1, var3, 5, 9, 0, 7, 12, 1, Blocks.field_150385_bj, Blocks.field_150385_bj, false);
      this.func_151549_a(var1, var3, 2, 11, 2, 10, 12, 10, Blocks.field_150385_bj, Blocks.field_150385_bj, false);

      for(int var4 = 1; var4 <= 11; var4 += 2) {
         this.func_151549_a(var1, var3, var4, 10, 0, var4, 11, 0, Blocks.field_150386_bk, Blocks.field_150386_bk, false);
         this.func_151549_a(var1, var3, var4, 10, 12, var4, 11, 12, Blocks.field_150386_bk, Blocks.field_150386_bk, false);
         this.func_151549_a(var1, var3, 0, 10, var4, 0, 11, var4, Blocks.field_150386_bk, Blocks.field_150386_bk, false);
         this.func_151549_a(var1, var3, 12, 10, var4, 12, 11, var4, Blocks.field_150386_bk, Blocks.field_150386_bk, false);
         this.func_151550_a(var1, Blocks.field_150385_bj, 0, var4, 13, 0, var3);
         this.func_151550_a(var1, Blocks.field_150385_bj, 0, var4, 13, 12, var3);
         this.func_151550_a(var1, Blocks.field_150385_bj, 0, 0, 13, var4, var3);
         this.func_151550_a(var1, Blocks.field_150385_bj, 0, 12, 13, var4, var3);
         this.func_151550_a(var1, Blocks.field_150386_bk, 0, var4 + 1, 13, 0, var3);
         this.func_151550_a(var1, Blocks.field_150386_bk, 0, var4 + 1, 13, 12, var3);
         this.func_151550_a(var1, Blocks.field_150386_bk, 0, 0, 13, var4 + 1, var3);
         this.func_151550_a(var1, Blocks.field_150386_bk, 0, 12, 13, var4 + 1, var3);
      }

      this.func_151550_a(var1, Blocks.field_150386_bk, 0, 0, 13, 0, var3);
      this.func_151550_a(var1, Blocks.field_150386_bk, 0, 0, 13, 12, var3);
      this.func_151550_a(var1, Blocks.field_150386_bk, 0, 0, 13, 0, var3);
      this.func_151550_a(var1, Blocks.field_150386_bk, 0, 12, 13, 0, var3);

      for(int var9 = 3; var9 <= 9; var9 += 2) {
         this.func_151549_a(var1, var3, 1, 7, var9, 1, 8, var9, Blocks.field_150386_bk, Blocks.field_150386_bk, false);
         this.func_151549_a(var1, var3, 11, 7, var9, 11, 8, var9, Blocks.field_150386_bk, Blocks.field_150386_bk, false);
      }

      int var10 = this.func_151555_a(Blocks.field_150387_bl, 3);

      for(int var5 = 0; var5 <= 6; ++var5) {
         int var6 = var5 + 4;

         for(int var7 = 5; var7 <= 7; ++var7) {
            this.func_151550_a(var1, Blocks.field_150387_bl, var10, var7, 5 + var5, var6, var3);
         }

         if (var6 >= 5 && var6 <= 8) {
            this.func_151549_a(var1, var3, 5, 5, var6, 7, var5 + 4, var6, Blocks.field_150385_bj, Blocks.field_150385_bj, false);
         } else if (var6 >= 9 && var6 <= 10) {
            this.func_151549_a(var1, var3, 5, 8, var6, 7, var5 + 4, var6, Blocks.field_150385_bj, Blocks.field_150385_bj, false);
         }

         if (var5 >= 1) {
            this.func_151549_a(var1, var3, 5, 6 + var5, var6, 7, 9 + var5, var6, Blocks.field_150350_a, Blocks.field_150350_a, false);
         }
      }

      for(int var11 = 5; var11 <= 7; ++var11) {
         this.func_151550_a(var1, Blocks.field_150387_bl, var10, var11, 12, 11, var3);
      }

      this.func_151549_a(var1, var3, 5, 6, 7, 5, 7, 7, Blocks.field_150386_bk, Blocks.field_150386_bk, false);
      this.func_151549_a(var1, var3, 7, 6, 7, 7, 7, 7, Blocks.field_150386_bk, Blocks.field_150386_bk, false);
      this.func_151549_a(var1, var3, 5, 13, 12, 7, 13, 12, Blocks.field_150350_a, Blocks.field_150350_a, false);
      this.func_151549_a(var1, var3, 2, 5, 2, 3, 5, 3, Blocks.field_150385_bj, Blocks.field_150385_bj, false);
      this.func_151549_a(var1, var3, 2, 5, 9, 3, 5, 10, Blocks.field_150385_bj, Blocks.field_150385_bj, false);
      this.func_151549_a(var1, var3, 2, 5, 4, 2, 5, 8, Blocks.field_150385_bj, Blocks.field_150385_bj, false);
      this.func_151549_a(var1, var3, 9, 5, 2, 10, 5, 3, Blocks.field_150385_bj, Blocks.field_150385_bj, false);
      this.func_151549_a(var1, var3, 9, 5, 9, 10, 5, 10, Blocks.field_150385_bj, Blocks.field_150385_bj, false);
      this.func_151549_a(var1, var3, 10, 5, 4, 10, 5, 8, Blocks.field_150385_bj, Blocks.field_150385_bj, false);
      int var12 = this.func_151555_a(Blocks.field_150387_bl, 0);
      int var13 = this.func_151555_a(Blocks.field_150387_bl, 1);
      this.func_151550_a(var1, Blocks.field_150387_bl, var13, 4, 5, 2, var3);
      this.func_151550_a(var1, Blocks.field_150387_bl, var13, 4, 5, 3, var3);
      this.func_151550_a(var1, Blocks.field_150387_bl, var13, 4, 5, 9, var3);
      this.func_151550_a(var1, Blocks.field_150387_bl, var13, 4, 5, 10, var3);
      this.func_151550_a(var1, Blocks.field_150387_bl, var12, 8, 5, 2, var3);
      this.func_151550_a(var1, Blocks.field_150387_bl, var12, 8, 5, 3, var3);
      this.func_151550_a(var1, Blocks.field_150387_bl, var12, 8, 5, 9, var3);
      this.func_151550_a(var1, Blocks.field_150387_bl, var12, 8, 5, 10, var3);
      this.func_151549_a(var1, var3, 3, 4, 4, 4, 4, 8, Blocks.field_150425_aM, Blocks.field_150425_aM, false);
      this.func_151549_a(var1, var3, 8, 4, 4, 9, 4, 8, Blocks.field_150425_aM, Blocks.field_150425_aM, false);
      this.func_151549_a(var1, var3, 3, 5, 4, 4, 5, 8, Blocks.field_150388_bm, Blocks.field_150388_bm, false);
      this.func_151549_a(var1, var3, 8, 5, 4, 9, 5, 8, Blocks.field_150388_bm, Blocks.field_150388_bm, false);
      this.func_151549_a(var1, var3, 4, 2, 0, 8, 2, 12, Blocks.field_150385_bj, Blocks.field_150385_bj, false);
      this.func_151549_a(var1, var3, 0, 2, 4, 12, 2, 8, Blocks.field_150385_bj, Blocks.field_150385_bj, false);
      this.func_151549_a(var1, var3, 4, 0, 0, 8, 1, 3, Blocks.field_150385_bj, Blocks.field_150385_bj, false);
      this.func_151549_a(var1, var3, 4, 0, 9, 8, 1, 12, Blocks.field_150385_bj, Blocks.field_150385_bj, false);
      this.func_151549_a(var1, var3, 0, 0, 4, 3, 1, 8, Blocks.field_150385_bj, Blocks.field_150385_bj, false);
      this.func_151549_a(var1, var3, 9, 0, 4, 12, 1, 8, Blocks.field_150385_bj, Blocks.field_150385_bj, false);

      for(int var14 = 4; var14 <= 8; ++var14) {
         for(int var8 = 0; var8 <= 2; ++var8) {
            this.func_151554_b(var1, Blocks.field_150385_bj, 0, var14, -1, var8, var3);
            this.func_151554_b(var1, Blocks.field_150385_bj, 0, var14, -1, 12 - var8, var3);
         }
      }

      for(int var15 = 0; var15 <= 2; ++var15) {
         for(int var16 = 4; var16 <= 8; ++var16) {
            this.func_151554_b(var1, Blocks.field_150385_bj, 0, var15, -1, var16, var3);
            this.func_151554_b(var1, Blocks.field_150385_bj, 0, 12 - var15, -1, var16, var3);
         }
      }

      return true;
   }
}
