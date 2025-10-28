package com.mojang.blaze3d.shaders;

import net.minecraft.resources.ResourceLocation;
import org.jspecify.annotations.Nullable;

@FunctionalInterface
public interface ShaderSource {
   @Nullable String get(ResourceLocation var1, ShaderType var2);
}
