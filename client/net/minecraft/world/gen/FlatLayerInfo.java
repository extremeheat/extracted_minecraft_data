package net.minecraft.world.gen;

import net.minecraft.block.Block;

public class FlatLayerInfo {
   private Block field_151537_a;
   private int field_82664_a = 1;
   private int field_82663_c;
   private int field_82661_d;

   public FlatLayerInfo(int var1, Block var2) {
      super();
      this.field_82664_a = var1;
      this.field_151537_a = var2;
   }

   public FlatLayerInfo(int var1, Block var2, int var3) {
      this(var1, var2);
      this.field_82663_c = var3;
   }

   public int func_82657_a() {
      return this.field_82664_a;
   }

   public Block func_151536_b() {
      return this.field_151537_a;
   }

   public int func_82658_c() {
      return this.field_82663_c;
   }

   public int func_82656_d() {
      return this.field_82661_d;
   }

   public void func_82660_d(int var1) {
      this.field_82661_d = var1;
   }

   @Override
   public String toString() {
      String var1 = Integer.toString(Block.func_149682_b(this.field_151537_a));
      if (this.field_82664_a > 1) {
         var1 = this.field_82664_a + "x" + var1;
      }

      if (this.field_82663_c > 0) {
         var1 = var1 + ":" + this.field_82663_c;
      }

      return var1;
   }
}
