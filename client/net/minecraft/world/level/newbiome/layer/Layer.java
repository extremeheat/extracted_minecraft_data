package net.minecraft.world.level.newbiome.layer;

import net.minecraft.SharedConstants;
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

   public Layer(AreaFactory<LazyArea> var1) {
      super();
      this.area = (LazyArea)var1.make();
   }

   public Biome[] getArea(int var1, int var2, int var3, int var4) {
      Biome[] var5 = new Biome[var3 * var4];

      for(int var6 = 0; var6 < var4; ++var6) {
         for(int var7 = 0; var7 < var3; ++var7) {
            int var8 = this.area.get(var1 + var7, var2 + var6);
            Biome var9 = this.getBiome(var8);
            var5[var7 + var6 * var3] = var9;
         }
      }

      return var5;
   }

   private Biome getBiome(int var1) {
      Biome var2 = (Biome)Registry.BIOME.byId(var1);
      if (var2 == null) {
         if (SharedConstants.IS_RUNNING_IN_IDE) {
            throw new IllegalStateException("Unknown biome id: " + var1);
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
