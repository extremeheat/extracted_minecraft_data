package net.minecraft.world.entity;

import org.jspecify.annotations.Nullable;

@FunctionalInterface
public interface EntityProcessor {
   EntityProcessor NOP = (var0) -> var0;

   @Nullable Entity process(Entity var1);
}
