package com.mojang.blaze3d.shaders;

import net.minecraft.resources.Identifier;
import org.jspecify.annotations.Nullable;

@FunctionalInterface
public interface ShaderSource {
   @Nullable String get(Identifier id, ShaderType type);
}
