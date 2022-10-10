package net.minecraft.world.biome.provider;

import net.minecraft.init.Biomes;
import net.minecraft.world.biome.Biome;

public class SingleBiomeProviderSettings implements IBiomeProviderSettings {
   private Biome field_205438_a;

   public SingleBiomeProviderSettings() {
      super();
      this.field_205438_a = Biomes.field_76772_c;
   }

   public SingleBiomeProviderSettings func_205436_a(Biome var1) {
      this.field_205438_a = var1;
      return this;
   }

   public Biome func_205437_a() {
      return this.field_205438_a;
   }
}
