package net.minecraft.world.level.levelgen.feature.configurations;

import com.mojang.datafixers.Dynamic;
import com.mojang.datafixers.types.DynamicOps;

public interface FeatureConfiguration {
   NoneFeatureConfiguration NONE = new NoneFeatureConfiguration();

   Dynamic serialize(DynamicOps var1);
}
