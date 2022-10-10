package net.minecraft.world.biome.provider;

import net.minecraft.init.Biomes;
import net.minecraft.world.biome.Biome;

public class CheckerboardBiomeProviderSettings implements IBiomeProviderSettings {
   private Biome[] field_205434_a;
   private int field_205435_b;

   public CheckerboardBiomeProviderSettings() {
      super();
      this.field_205434_a = new Biome[]{Biomes.field_76772_c};
      this.field_205435_b = 1;
   }

   public CheckerboardBiomeProviderSettings func_206860_a(Biome[] var1) {
      this.field_205434_a = var1;
      return this;
   }

   public CheckerboardBiomeProviderSettings func_206861_a(int var1) {
      this.field_205435_b = var1;
      return this;
   }

   public Biome[] func_205432_a() {
      return this.field_205434_a;
   }

   public int func_205433_b() {
      return this.field_205435_b;
   }
}
