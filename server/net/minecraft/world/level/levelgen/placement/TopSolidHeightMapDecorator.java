package net.minecraft.world.level.levelgen.placement;

import com.mojang.serialization.Codec;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.levelgen.feature.configurations.NoneDecoratorConfiguration;

public class TopSolidHeightMapDecorator extends BaseHeightmapDecorator<NoneDecoratorConfiguration> {
   public TopSolidHeightMapDecorator(Codec<NoneDecoratorConfiguration> var1) {
      super(var1);
   }

   protected Heightmap.Types type(NoneDecoratorConfiguration var1) {
      return Heightmap.Types.OCEAN_FLOOR_WG;
   }
}
