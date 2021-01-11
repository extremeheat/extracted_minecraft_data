package net.minecraft.world.gen;

import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.util.ResourceLocation;

public class FlatLayerInfo {
   private final int field_175902_a;
   private IBlockState field_175901_b;
   private int field_82664_a;
   private int field_82661_d;

   public FlatLayerInfo(int var1, Block var2) {
      this(3, var1, var2);
   }

   public FlatLayerInfo(int var1, int var2, Block var3) {
      super();
      this.field_82664_a = 1;
      this.field_175902_a = var1;
      this.field_82664_a = var2;
      this.field_175901_b = var3.func_176223_P();
   }

   public FlatLayerInfo(int var1, int var2, Block var3, int var4) {
      this(var1, var2, var3);
      this.field_175901_b = var3.func_176203_a(var4);
   }

   public int func_82657_a() {
      return this.field_82664_a;
   }

   public IBlockState func_175900_c() {
      return this.field_175901_b;
   }

   private Block func_151536_b() {
      return this.field_175901_b.func_177230_c();
   }

   private int func_82658_c() {
      return this.field_175901_b.func_177230_c().func_176201_c(this.field_175901_b);
   }

   public int func_82656_d() {
      return this.field_82661_d;
   }

   public void func_82660_d(int var1) {
      this.field_82661_d = var1;
   }

   public String toString() {
      String var1;
      if (this.field_175902_a >= 3) {
         ResourceLocation var2 = (ResourceLocation)Block.field_149771_c.func_177774_c(this.func_151536_b());
         var1 = var2 == null ? "null" : var2.toString();
         if (this.field_82664_a > 1) {
            var1 = this.field_82664_a + "*" + var1;
         }
      } else {
         var1 = Integer.toString(Block.func_149682_b(this.func_151536_b()));
         if (this.field_82664_a > 1) {
            var1 = this.field_82664_a + "x" + var1;
         }
      }

      int var3 = this.func_82658_c();
      if (var3 > 0) {
         var1 = var1 + ":" + var3;
      }

      return var1;
   }
}
