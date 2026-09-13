package net.minecraft.world.biome;

public class BiomeCache$Block {
   public float[] field_76890_b;
   public BiomeGenBase[] field_76891_c;
   public int field_76888_d;
   public int field_76889_e;
   public long field_76886_f;

   public BiomeCache$Block(BiomeCache var1, int var2, int var3) {
      super();
      this.field_76887_g = var1;
      this.field_76890_b = new float[256];
      this.field_76891_c = new BiomeGenBase[256];
      this.field_76888_d = var2;
      this.field_76889_e = var3;
      BiomeCache.access$000(var1).func_76936_a(this.field_76890_b, var2 << 4, var3 << 4, 16, 16);
      BiomeCache.access$000(var1).func_76931_a(this.field_76891_c, var2 << 4, var3 << 4, 16, 16, false);
   }

   public BiomeGenBase func_76885_a(int var1, int var2) {
      return this.field_76891_c[var1 & 15 | (var2 & 15) << 4];
   }
}
