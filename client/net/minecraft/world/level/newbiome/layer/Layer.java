package net.minecraft.world.level.newbiome.layer;

import net.minecraft.SharedConstants;
import net.minecraft.Util;
import net.minecraft.core.Registry;
import net.minecraft.data.worldgen.biome.Biomes;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.newbiome.area.AreaFactory;
import net.minecraft.world.level.newbiome.area.LazyArea;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class Layer {
   private static final Logger LOGGER = LogManager.getLogger();
   private final LazyArea area;

   public Layer(AreaFactory<LazyArea> var1) {
      super();
      this.area = (LazyArea)var1.make();
   }

   public Biome get(Registry<Biome> var1, int var2, int var3) {
      int var4 = this.area.get(var2, var3);
      ResourceKey var5 = Biomes.byId(var4);
      if (var5 == null) {
         throw new IllegalStateException("Unknown biome id emitted by layers: " + var4);
      } else {
         Biome var6 = (Biome)var1.get(var5);
         if (var6 == null) {
            if (SharedConstants.IS_RUNNING_IN_IDE) {
               throw (IllegalStateException)Util.pauseInIde(new IllegalStateException("Unknown biome id: " + var4));
            } else {
               LOGGER.warn("Unknown biome id: {}", var4);
               return (Biome)var1.get(Biomes.byId(0));
            }
         } else {
            return var6;
         }
      }
   }
}
