package net.minecraft.world.entity.variant;

import com.mojang.serialization.MapCodec;
import net.minecraft.core.Registry;

public class SpawnConditions {
   public SpawnConditions() {
      super();
   }

   public static MapCodec<? extends SpawnCondition> bootstrap(final Registry<MapCodec<? extends SpawnCondition>> registry) {
      Registry.register(registry, (String)"structure", StructureCheck.MAP_CODEC);
      Registry.register(registry, (String)"moon_brightness", MoonBrightnessCheck.MAP_CODEC);
      return (MapCodec)Registry.register(registry, (String)"biome", BiomeCheck.MAP_CODEC);
   }
}
