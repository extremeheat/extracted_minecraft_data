package net.minecraft.world.gen.structure;

import java.util.List;
import java.util.Random;
import net.minecraft.init.Blocks;
import net.minecraft.world.World;

public class StructureNetherBridgePieces$Entrance extends StructureNetherBridgePieces$Piece {
   public StructureNetherBridgePieces$Entrance() {
      super();
   }

   public StructureNetherBridgePieces$Entrance(int var1, Random var2, StructureBoundingBox var3, int var4) {
      super(var1);
      this.field_74885_f = var4;
      this.field_74887_e = var3;
   }

   @Override
   public void func_74861_a(StructureComponent var1, List var2, Random var3) {
      this.func_74963_a((StructureNetherBridgePieces$Start)var1, var2, var3, 5, 3, true);
   }

   public static StructureNetherBridgePieces$Entrance func_74984_a(List var0, Random var1, int var2, int var3, int var4, int var5, int var6) {
      StructureBoundingBox var7 = StructureBoundingBox.func_78889_a(var2, var3, var4, -5, -3, 0, 13, 14, 13, var5);
      return func_74964_a(var7) && StructureComponent.func_74883_a(var0, var7) == null
         ? new StructureNetherBridgePieces$Entrance(var6, var1, var7, var5)
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
      this.func_151549_a(var1, var3, 5, 8, 0, 7, 8, 0, Blocks.field_150386_bk, Blocks.field_150386_bk, false);

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

      for(int var7 = 3; var7 <= 9; var7 += 2) {
         this.func_151549_a(var1, var3, 1, 7, var7, 1, 8, var7, Blocks.field_150386_bk, Blocks.field_150386_bk, false);
         this.func_151549_a(var1, var3, 11, 7, var7, 11, 8, var7, Blocks.field_150386_bk, Blocks.field_150386_bk, false);
      }

      this.func_151549_a(var1, var3, 4, 2, 0, 8, 2, 12, Blocks.field_150385_bj, Blocks.field_150385_bj, false);
      this.func_151549_a(var1, var3, 0, 2, 4, 12, 2, 8, Blocks.field_150385_bj, Blocks.field_150385_bj, false);
      this.func_151549_a(var1, var3, 4, 0, 0, 8, 1, 3, Blocks.field_150385_bj, Blocks.field_150385_bj, false);
      this.func_151549_a(var1, var3, 4, 0, 9, 8, 1, 12, Blocks.field_150385_bj, Blocks.field_150385_bj, false);
      this.func_151549_a(var1, var3, 0, 0, 4, 3, 1, 8, Blocks.field_150385_bj, Blocks.field_150385_bj, false);
      this.func_151549_a(var1, var3, 9, 0, 4, 12, 1, 8, Blocks.field_150385_bj, Blocks.field_150385_bj, false);

      for(int var8 = 4; var8 <= 8; ++var8) {
         for(int var5 = 0; var5 <= 2; ++var5) {
            this.func_151554_b(var1, Blocks.field_150385_bj, 0, var8, -1, var5, var3);
            this.func_151554_b(var1, Blocks.field_150385_bj, 0, var8, -1, 12 - var5, var3);
         }
      }

      for(int var9 = 0; var9 <= 2; ++var9) {
         for(int var11 = 4; var11 <= 8; ++var11) {
            this.func_151554_b(var1, Blocks.field_150385_bj, 0, var9, -1, var11, var3);
            this.func_151554_b(var1, Blocks.field_150385_bj, 0, 12 - var9, -1, var11, var3);
         }
      }

      this.func_151549_a(var1, var3, 5, 5, 5, 7, 5, 7, Blocks.field_150385_bj, Blocks.field_150385_bj, false);
      this.func_151549_a(var1, var3, 6, 1, 6, 6, 4, 6, Blocks.field_150350_a, Blocks.field_150350_a, false);
      this.func_151550_a(var1, Blocks.field_150385_bj, 0, 6, 0, 6, var3);
      this.func_151550_a(var1, Blocks.field_150356_k, 0, 6, 5, 6, var3);
      int var10 = this.func_74865_a(6, 6);
      int var12 = this.func_74862_a(5);
      int var6 = this.func_74873_b(6, 6);
      if (var3.func_78890_b(var10, var12, var6)) {
         var1.field_72999_e = true;
         Blocks.field_150356_k.func_149674_a(var1, var10, var12, var6, var2);
         var1.field_72999_e = false;
      }

      return true;
   }
}
