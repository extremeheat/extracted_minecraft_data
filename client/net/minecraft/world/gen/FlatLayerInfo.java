package net.minecraft.world.gen;

import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.util.registry.IRegistry;

public class FlatLayerInfo {
   private final IBlockState field_175901_b;
   private final int field_82664_a;
   private int field_82661_d;

   public FlatLayerInfo(int var1, Block var2) {
      super();
      this.field_82664_a = var1;
      this.field_175901_b = var2.func_176223_P();
   }

   public int func_82657_a() {
      return this.field_82664_a;
   }

   public IBlockState func_175900_c() {
      return this.field_175901_b;
   }

   public int func_82656_d() {
      return this.field_82661_d;
   }

   public void func_82660_d(int var1) {
      this.field_82661_d = var1;
   }

   public String toString() {
      return (this.field_82664_a > 1 ? this.field_82664_a + "*" : "") + IRegistry.field_212618_g.func_177774_c(this.field_175901_b.func_177230_c());
   }
}
