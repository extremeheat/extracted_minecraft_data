package net.minecraft.world.biome;

public class BiomeGenBase$Height {
   public float field_150777_a;
   public float field_150776_b;

   public BiomeGenBase$Height(float var1, float var2) {
      super();
      this.field_150777_a = var1;
      this.field_150776_b = var2;
   }

   public BiomeGenBase$Height func_150775_a() {
      return new BiomeGenBase$Height(this.field_150777_a * 0.8F, this.field_150776_b * 0.6F);
   }
}
