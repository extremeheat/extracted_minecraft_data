package net.minecraft.world.level.mines;

import java.util.List;
import java.util.Objects;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.biome.Biome;

@FunctionalInterface
public interface WorldGenEffect extends WorldEffectComponent {
   void modifyWorld(WorldGenBuilder var1);

   public static record AddBiome(List<ResourceKey<Biome>> biomes) implements WorldGenEffect {
      @SafeVarargs
      public AddBiome(ResourceKey<Biome>... var1) {
         this(List.of(var1));
      }

      public AddBiome(List<ResourceKey<Biome>> var1) {
         super();
         this.biomes = var1;
      }

      public void modifyWorld(WorldGenBuilder var1) {
         List var10000 = this.biomes;
         Objects.requireNonNull(var1);
         var10000.forEach(var1::addBiome);
      }
   }
}
