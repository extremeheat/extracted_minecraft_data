package net.minecraft.world.entity.variant;

import com.mojang.serialization.MapCodec;
import net.minecraft.core.Registry;

public class SpawnConditions {
   public SpawnConditions() {
      super();
   }

   public static MapCodec<? extends SpawnCondition> bootstrap(Registry<MapCodec<? extends SpawnCondition>> var0) {
      Registry.register(var0, (String)"structure", StructureCheck.MAP_CODEC);
      Registry.register(var0, (String)"moon_brightness", MoonBrightnessCheck.MAP_CODEC);
      return (MapCodec)Registry.register(var0, (String)"biome", BiomeCheck.MAP_CODEC);
   }
}
