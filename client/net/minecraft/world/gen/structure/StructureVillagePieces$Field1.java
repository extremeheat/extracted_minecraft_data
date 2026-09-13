package net.minecraft.world.gen.structure;

import java.util.List;
import java.util.Random;
import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;

public class StructureVillagePieces$Field1 extends StructureVillagePieces$Village {
   private Block field_82679_b;
   private Block field_82680_c;
   private Block field_82678_d;
   private Block field_82681_h;

   public StructureVillagePieces$Field1() {
      super();
   }

   public StructureVillagePieces$Field1(StructureVillagePieces$Start var1, int var2, Random var3, StructureBoundingBox var4, int var5) {
      super(var1, var2);
      this.field_74885_f = var5;
      this.field_74887_e = var4;
      this.field_82679_b = this.func_151559_a(var3);
      this.field_82680_c = this.func_151559_a(var3);
      this.field_82678_d = this.func_151559_a(var3);
      this.field_82681_h = this.func_151559_a(var3);
   }

   @Override
   protected void func_143012_a(NBTTagCompound var1) {
      super.func_143012_a(var1);
      var1.func_74768_a("CA", Block.field_149771_c.func_148757_b(this.field_82679_b));
      var1.func_74768_a("CB", Block.field_149771_c.func_148757_b(this.field_82680_c));
      var1.func_74768_a("CC", Block.field_149771_c.func_148757_b(this.field_82678_d));
      var1.func_74768_a("CD", Block.field_149771_c.func_148757_b(this.field_82681_h));
   }

   @Override
   protected void func_143011_b(NBTTagCompound var1) {
      super.func_143011_b(var1);
      this.field_82679_b = Block.func_149729_e(var1.func_74762_e("CA"));
      this.field_82680_c = Block.func_149729_e(var1.func_74762_e("CB"));
      this.field_82678_d = Block.func_149729_e(var1.func_74762_e("CC"));
      this.field_82681_h = Block.func_149729_e(var1.func_74762_e("CD"));
   }

   private Block func_151559_a(Random var1) {
      switch(var1.nextInt(5)) {
         case 0:
            return Blocks.field_150459_bM;
         case 1:
            return Blocks.field_150469_bN;
         default:
            return Blocks.field_150464_aj;
      }
   }

   public static StructureVillagePieces$Field1 func_74900_a(
      StructureVillagePieces$Start var0, List var1, Random var2, int var3, int var4, int var5, int var6, int var7
   ) {
      StructureBoundingBox var8 = StructureBoundingBox.func_78889_a(var3, var4, var5, 0, 0, 0, 13, 4, 9, var6);
      return func_74895_a(var8) && StructureComponent.func_74883_a(var1, var8) == null
         ? new StructureVillagePieces$Field1(var0, var7, var2, var8, var6)
         : null;
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

      this.func_151549_a(var1, var3, 0, 1, 0, 12, 4, 8, Blocks.field_150350_a, Blocks.field_150350_a, false);
      this.func_151549_a(var1, var3, 1, 0, 1, 2, 0, 7, Blocks.field_150458_ak, Blocks.field_150458_ak, false);
      this.func_151549_a(var1, var3, 4, 0, 1, 5, 0, 7, Blocks.field_150458_ak, Blocks.field_150458_ak, false);
      this.func_151549_a(var1, var3, 7, 0, 1, 8, 0, 7, Blocks.field_150458_ak, Blocks.field_150458_ak, false);
      this.func_151549_a(var1, var3, 10, 0, 1, 11, 0, 7, Blocks.field_150458_ak, Blocks.field_150458_ak, false);
      this.func_151549_a(var1, var3, 0, 0, 0, 0, 0, 8, Blocks.field_150364_r, Blocks.field_150364_r, false);
      this.func_151549_a(var1, var3, 6, 0, 0, 6, 0, 8, Blocks.field_150364_r, Blocks.field_150364_r, false);
      this.func_151549_a(var1, var3, 12, 0, 0, 12, 0, 8, Blocks.field_150364_r, Blocks.field_150364_r, false);
      this.func_151549_a(var1, var3, 1, 0, 0, 11, 0, 0, Blocks.field_150364_r, Blocks.field_150364_r, false);
      this.func_151549_a(var1, var3, 1, 0, 8, 11, 0, 8, Blocks.field_150364_r, Blocks.field_150364_r, false);
      this.func_151549_a(var1, var3, 3, 0, 1, 3, 0, 7, Blocks.field_150355_j, Blocks.field_150355_j, false);
      this.func_151549_a(var1, var3, 9, 0, 1, 9, 0, 7, Blocks.field_150355_j, Blocks.field_150355_j, false);

      for(int var4 = 1; var4 <= 7; ++var4) {
         this.func_151550_a(var1, this.field_82679_b, MathHelper.func_76136_a(var2, 2, 7), 1, 1, var4, var3);
         this.func_151550_a(var1, this.field_82679_b, MathHelper.func_76136_a(var2, 2, 7), 2, 1, var4, var3);
         this.func_151550_a(var1, this.field_82680_c, MathHelper.func_76136_a(var2, 2, 7), 4, 1, var4, var3);
         this.func_151550_a(var1, this.field_82680_c, MathHelper.func_76136_a(var2, 2, 7), 5, 1, var4, var3);
         this.func_151550_a(var1, this.field_82678_d, MathHelper.func_76136_a(var2, 2, 7), 7, 1, var4, var3);
         this.func_151550_a(var1, this.field_82678_d, MathHelper.func_76136_a(var2, 2, 7), 8, 1, var4, var3);
         this.func_151550_a(var1, this.field_82681_h, MathHelper.func_76136_a(var2, 2, 7), 10, 1, var4, var3);
         this.func_151550_a(var1, this.field_82681_h, MathHelper.func_76136_a(var2, 2, 7), 11, 1, var4, var3);
      }

      for(int var6 = 0; var6 < 9; ++var6) {
         for(int var5 = 0; var5 < 13; ++var5) {
            this.func_74871_b(var1, var5, 4, var6, var3);
            this.func_151554_b(var1, Blocks.field_150346_d, 0, var5, -1, var6, var3);
         }
      }

      return true;
   }
}
