package net.minecraft.world.level.levelgen.carver;

import com.mojang.serialization.MapCodec;
import net.minecraft.core.Registry;

public interface WorldCarverTypes {
   static MapCodec<? extends WorldCarver> bootstrap(final Registry<MapCodec<? extends WorldCarver>> registry) {
      Registry.register(registry, (String)"cave", CaveWorldCarver.MAP_CODEC);
      return (MapCodec)Registry.register(registry, (String)"canyon", CanyonWorldCarver.MAP_CODEC);
   }
}
