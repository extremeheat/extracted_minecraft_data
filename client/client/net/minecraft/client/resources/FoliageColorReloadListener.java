package net.minecraft.client.resources;

import java.io.IOException;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.SimplePreparableReloadListener;
import net.minecraft.util.profiling.ProfilerFiller;
import net.minecraft.world.level.FoliageColor;

public class FoliageColorReloadListener extends SimplePreparableReloadListener<int[]> {
   private static final ResourceLocation LOCATION = new ResourceLocation("textures/colormap/foliage.png");

   public FoliageColorReloadListener() {
      super();
   }

   protected int[] prepare(ResourceManager var1, ProfilerFiller var2) {
      try {
         return LegacyStuffWrapper.getPixels(var1, LOCATION);
      } catch (IOException var4) {
         throw new IllegalStateException("Failed to load foliage color texture", var4);
      }
   }

   protected void apply(int[] var1, ResourceManager var2, ProfilerFiller var3) {
      FoliageColor.init(var1);
   }
}
