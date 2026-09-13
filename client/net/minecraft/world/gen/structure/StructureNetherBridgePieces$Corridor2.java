package net.minecraft.world.gen.structure;

import java.util.List;
import java.util.Random;
import net.minecraft.init.Blocks;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;

public class StructureNetherBridgePieces$Corridor2 extends StructureNetherBridgePieces$Piece {
   private boolean field_111020_b;

   public StructureNetherBridgePieces$Corridor2() {
      super();
   }

   public StructureNetherBridgePieces$Corridor2(int var1, Random var2, StructureBoundingBox var3, int var4) {
      super(var1);
      this.field_74885_f = var4;
      this.field_74887_e = var3;
      this.field_111020_b = var2.nextInt(3) == 0;
   }

   @Override
   protected void func_143011_b(NBTTagCompound var1) {
      super.func_143011_b(var1);
      this.field_111020_b = var1.func_74767_n("Chest");
   }

   @Override
   protected void func_143012_a(NBTTagCompound var1) {
      super.func_143012_a(var1);
      var1.func_74757_a("Chest", this.field_111020_b);
   }

   @Override
   public void func_74861_a(StructureComponent var1, List var2, Random var3) {
      this.func_74965_c((StructureNetherBridgePieces$Start)var1, var2, var3, 0, 1, true);
   }

   public static StructureNetherBridgePieces$Corridor2 func_74980_a(List var0, Random var1, int var2, int var3, int var4, int var5, int var6) {
      StructureBoundingBox var7 = StructureBoundingBox.func_78889_a(var2, var3, var4, -1, 0, 0, 5, 7, 5, var5);
      return func_74964_a(var7) && StructureComponent.func_74883_a(var0, var7) == null
         ? new StructureNetherBridgePieces$Corridor2(var6, var1, var7, var5)
         : null;
   }

   @Override
   public boolean func_74875_a(World var1, Random var2, StructureBoundingBox var3) {
      this.func_151549_a(var1, var3, 0, 0, 0, 4, 1, 4, Blocks.field_150385_bj, Blocks.field_150385_bj, false);
      this.func_151549_a(var1, var3, 0, 2, 0, 4, 5, 4, Blocks.field_150350_a, Blocks.field_150350_a, false);
      this.func_151549_a(var1, var3, 0, 2, 0, 0, 5, 4, Blocks.field_150385_bj, Blocks.field_150385_bj, false);
      this.func_151549_a(var1, var3, 0, 3, 1, 0, 4, 1, Blocks.field_150386_bk, Blocks.field_150386_bk, false);
      this.func_151549_a(var1, var3, 0, 3, 3, 0, 4, 3, Blocks.field_150386_bk, Blocks.field_150386_bk, false);
      this.func_151549_a(var1, var3, 4, 2, 0, 4, 5, 0, Blocks.field_150385_bj, Blocks.field_150385_bj, false);
      this.func_151549_a(var1, var3, 1, 2, 4, 4, 5, 4, Blocks.field_150385_bj, Blocks.field_150385_bj, false);
      this.func_151549_a(var1, var3, 1, 3, 4, 1, 4, 4, Blocks.field_150386_bk, Blocks.field_150385_bj, false);
      this.func_151549_a(var1, var3, 3, 3, 4, 3, 4, 4, Blocks.field_150386_bk, Blocks.field_150385_bj, false);
      if (this.field_111020_b) {
         int var4 = this.func_74862_a(2);
         int var5 = this.func_74865_a(1, 3);
         int var6 = this.func_74873_b(1, 3);
         if (var3.func_78890_b(var5, var4, var6)) {
            this.field_111020_b = false;
            this.func_74879_a(var1, var3, var2, 1, 2, 3, field_111019_a, 2 + var2.nextInt(4));
         }
      }

      this.func_151549_a(var1, var3, 0, 6, 0, 4, 6, 4, Blocks.field_150385_bj, Blocks.field_150385_bj, false);

      for(int var7 = 0; var7 <= 4; ++var7) {
         for(int var8 = 0; var8 <= 4; ++var8) {
            this.func_151554_b(var1, Blocks.field_150385_bj, 0, var7, -1, var8, var3);
         }
      }

      return true;
   }
}
