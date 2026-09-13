package net.minecraft.world.gen.structure;

import java.util.List;
import java.util.Random;
import net.minecraft.init.Blocks;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;

public class StructureNetherBridgePieces$End extends StructureNetherBridgePieces$Piece {
   private int field_74972_a;

   public StructureNetherBridgePieces$End() {
      super();
   }

   public StructureNetherBridgePieces$End(int var1, Random var2, StructureBoundingBox var3, int var4) {
      super(var1);
      this.field_74885_f = var4;
      this.field_74887_e = var3;
      this.field_74972_a = var2.nextInt();
   }

   public static StructureNetherBridgePieces$End func_74971_a(List var0, Random var1, int var2, int var3, int var4, int var5, int var6) {
      StructureBoundingBox var7 = StructureBoundingBox.func_78889_a(var2, var3, var4, -1, -3, 0, 5, 10, 8, var5);
      return func_74964_a(var7) && StructureComponent.func_74883_a(var0, var7) == null ? new StructureNetherBridgePieces$End(var6, var1, var7, var5) : null;
   }

   @Override
   protected void func_143011_b(NBTTagCompound var1) {
      super.func_143011_b(var1);
      this.field_74972_a = var1.func_74762_e("Seed");
   }

   @Override
   protected void func_143012_a(NBTTagCompound var1) {
      super.func_143012_a(var1);
      var1.func_74768_a("Seed", this.field_74972_a);
   }

   @Override
   public boolean func_74875_a(World var1, Random var2, StructureBoundingBox var3) {
      Random var4 = new Random((long)this.field_74972_a);

      for(int var5 = 0; var5 <= 4; ++var5) {
         for(int var6 = 3; var6 <= 4; ++var6) {
            int var7 = var4.nextInt(8);
            this.func_151549_a(var1, var3, var5, var6, 0, var5, var6, var7, Blocks.field_150385_bj, Blocks.field_150385_bj, false);
         }
      }

      int var8 = var4.nextInt(8);
      this.func_151549_a(var1, var3, 0, 5, 0, 0, 5, var8, Blocks.field_150385_bj, Blocks.field_150385_bj, false);
      var8 = var4.nextInt(8);
      this.func_151549_a(var1, var3, 4, 5, 0, 4, 5, var8, Blocks.field_150385_bj, Blocks.field_150385_bj, false);

      for(int var10 = 0; var10 <= 4; ++var10) {
         int var12 = var4.nextInt(5);
         this.func_151549_a(var1, var3, var10, 2, 0, var10, 2, var12, Blocks.field_150385_bj, Blocks.field_150385_bj, false);
      }

      for(int var11 = 0; var11 <= 4; ++var11) {
         for(int var13 = 0; var13 <= 1; ++var13) {
            int var14 = var4.nextInt(3);
            this.func_151549_a(var1, var3, var11, var13, 0, var11, var13, var14, Blocks.field_150385_bj, Blocks.field_150385_bj, false);
         }
      }

      return true;
   }
}
