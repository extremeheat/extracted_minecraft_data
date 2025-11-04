package com.mojang.blaze3d.shaders;

import net.minecraft.resources.Identifier;
import org.jspecify.annotations.Nullable;

@FunctionalInterface
public interface ShaderSource {
   @Nullable String get(Identifier var1, ShaderType var2);
}
