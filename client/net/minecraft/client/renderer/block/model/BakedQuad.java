package net.minecraft.client.renderer.block.model;

import net.minecraft.util.EnumFacing;

public class BakedQuad {
   protected final int[] field_178215_a;
   protected final int field_178213_b;
   protected final EnumFacing field_178214_c;

   public BakedQuad(int[] var1, int var2, EnumFacing var3) {
      super();
      this.field_178215_a = var1;
      this.field_178213_b = var2;
      this.field_178214_c = var3;
   }

   public int[] func_178209_a() {
      return this.field_178215_a;
   }

   public boolean func_178212_b() {
      return this.field_178213_b != -1;
   }

   public int func_178211_c() {
      return this.field_178213_b;
   }

   public EnumFacing func_178210_d() {
      return this.field_178214_c;
   }
}
