package net.minecraft.world.chunk;

import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;

public class ChunkPrimer {
   private final short[] field_177860_a = new short[65536];
   private final IBlockState field_177859_b;

   public ChunkPrimer() {
      super();
      this.field_177859_b = Blocks.field_150350_a.func_176223_P();
   }

   public IBlockState func_177856_a(int var1, int var2, int var3) {
      int var4 = var1 << 12 | var3 << 8 | var2;
      return this.func_177858_a(var4);
   }

   public IBlockState func_177858_a(int var1) {
      if (var1 >= 0 && var1 < this.field_177860_a.length) {
         IBlockState var2 = (IBlockState)Block.field_176229_d.func_148745_a(this.field_177860_a[var1]);
         return var2 != null ? var2 : this.field_177859_b;
      } else {
         throw new IndexOutOfBoundsException("The coordinate is out of range");
      }
   }

   public void func_177855_a(int var1, int var2, int var3, IBlockState var4) {
      int var5 = var1 << 12 | var3 << 8 | var2;
      this.func_177857_a(var5, var4);
   }

   public void func_177857_a(int var1, IBlockState var2) {
      if (var1 >= 0 && var1 < this.field_177860_a.length) {
         this.field_177860_a[var1] = (short)Block.field_176229_d.func_148747_b(var2);
      } else {
         throw new IndexOutOfBoundsException("The coordinate is out of range");
      }
   }
}
