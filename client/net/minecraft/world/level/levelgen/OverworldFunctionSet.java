package net.minecraft.world.level.levelgen;

import java.util.function.Function;

public record OverworldFunctionSet<T>(T temperature, T vegetation, T continents, T erosion, T offset, T factor, T jaggedness, T depth, T slopedCheese, T preliminarySurfaceLevel, T chunkSurfaceLevel, T finalDensity) {
   public OverworldFunctionSet {
      super();
   }

   public <U> OverworldFunctionSet<U> map(final Function<T, U> function) {
      return new OverworldFunctionSet<U>(function.apply(this.temperature), function.apply(this.vegetation), function.apply(this.continents), function.apply(this.erosion), function.apply(this.offset), function.apply(this.factor), function.apply(this.jaggedness), function.apply(this.depth), function.apply(this.slopedCheese), function.apply(this.preliminarySurfaceLevel), function.apply(this.chunkSurfaceLevel), function.apply(this.finalDensity));
   }
}
