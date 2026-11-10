package net.minecraft.world.entity.variant;

import com.mojang.serialization.MapCodec;
import net.minecraft.core.Registry;
import net.minecraft.core.WritableRegistry;

public class SpawnConditions {
   public SpawnConditions() {
      super();
   }

   public static void bootstrap(final WritableRegistry<MapCodec<? extends SpawnCondition>> registry) {
      Registry.register(registry, (String)"structure", StructureCheck.MAP_CODEC);
      Registry.register(registry, (String)"moon_brightness", MoonBrightnessCheck.MAP_CODEC);
      Registry.register(registry, (String)"biome", BiomeCheck.MAP_CODEC);
   }
}
