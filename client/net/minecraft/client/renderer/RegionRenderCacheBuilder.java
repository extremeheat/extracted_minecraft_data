package net.minecraft.client.renderer;

import net.minecraft.util.EnumWorldBlockLayer;

public class RegionRenderCacheBuilder {
   private final WorldRenderer[] field_179040_a = new WorldRenderer[EnumWorldBlockLayer.values().length];

   public RegionRenderCacheBuilder() {
      super();
      this.field_179040_a[EnumWorldBlockLayer.SOLID.ordinal()] = new WorldRenderer(2097152);
      this.field_179040_a[EnumWorldBlockLayer.CUTOUT.ordinal()] = new WorldRenderer(131072);
      this.field_179040_a[EnumWorldBlockLayer.CUTOUT_MIPPED.ordinal()] = new WorldRenderer(131072);
      this.field_179040_a[EnumWorldBlockLayer.TRANSLUCENT.ordinal()] = new WorldRenderer(262144);
   }

   public WorldRenderer func_179038_a(EnumWorldBlockLayer var1) {
      return this.field_179040_a[var1.ordinal()];
   }

   public WorldRenderer func_179039_a(int var1) {
      return this.field_179040_a[var1];
   }
}
