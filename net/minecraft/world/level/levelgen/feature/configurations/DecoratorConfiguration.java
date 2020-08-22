package net.minecraft.world.level.levelgen.feature.configurations;

import com.mojang.datafixers.Dynamic;
import com.mojang.datafixers.types.DynamicOps;

public interface DecoratorConfiguration {
   NoneDecoratorConfiguration NONE = new NoneDecoratorConfiguration();

   Dynamic serialize(DynamicOps var1);
}
