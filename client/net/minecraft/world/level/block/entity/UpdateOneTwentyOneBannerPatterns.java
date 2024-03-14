package net.minecraft.world.level.block.entity;

import net.minecraft.data.worldgen.BootstrapContext;

public interface UpdateOneTwentyOneBannerPatterns {
   static void bootstrap(BootstrapContext<BannerPattern> var0) {
      BannerPatterns.register(var0, BannerPatterns.FLOW);
      BannerPatterns.register(var0, BannerPatterns.GUSTER);
   }
}
