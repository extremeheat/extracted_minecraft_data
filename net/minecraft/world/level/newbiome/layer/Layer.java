package net.minecraft.world.level.newbiome.layer;

import net.minecraft.SharedConstants;
import net.minecraft.Util;
import net.minecraft.core.Registry;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.Biomes;
import net.minecraft.world.level.newbiome.area.AreaFactory;
import net.minecraft.world.level.newbiome.area.LazyArea;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class Layer {
   private static final Logger LOGGER = LogManager.getLogger();
   private final LazyArea area;

   public Layer(AreaFactory var1) {
      this.area = (LazyArea)var1.make();
   }

   private Biome getBiome(int var1) {
      Biome var2 = (Biome)Registry.BIOME.byId(var1);
      if (var2 == null) {
         if (SharedConstants.IS_RUNNING_IN_IDE) {
            throw (IllegalStateException)Util.pauseInIde(new IllegalStateException("Unknown biome id: " + var1));
         } else {
            LOGGER.warn("Unknown biome id: ", var1);
            return Biomes.DEFAULT;
         }
      } else {
         return var2;
      }
   }

   public Biome get(int var1, int var2) {
      return this.getBiome(this.area.get(var1, var2));
   }
}
