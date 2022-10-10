package net.minecraft.world.biome.provider;

import net.minecraft.world.gen.OverworldGenSettings;
import net.minecraft.world.storage.WorldInfo;

public class OverworldBiomeProviderSettings implements IBiomeProviderSettings {
   private WorldInfo field_205443_a;
   private OverworldGenSettings field_205444_b;

   public OverworldBiomeProviderSettings() {
      super();
   }

   public OverworldBiomeProviderSettings func_205439_a(WorldInfo var1) {
      this.field_205443_a = var1;
      return this;
   }

   public OverworldBiomeProviderSettings func_205441_a(OverworldGenSettings var1) {
      this.field_205444_b = var1;
      return this;
   }

   public WorldInfo func_205440_a() {
      return this.field_205443_a;
   }

   public OverworldGenSettings func_205442_b() {
      return this.field_205444_b;
   }
}
