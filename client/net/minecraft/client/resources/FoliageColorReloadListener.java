package net.minecraft.client.resources;

import java.io.IOException;
import net.minecraft.client.renderer.texture.TextureUtil;
import net.minecraft.resources.IResourceManager;
import net.minecraft.resources.IResourceManagerReloadListener;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.FoliageColors;

public class FoliageColorReloadListener implements IResourceManagerReloadListener {
   private static final ResourceLocation field_130079_a = new ResourceLocation("textures/colormap/foliage.png");

   public FoliageColorReloadListener() {
      super();
   }

   public void func_195410_a(IResourceManager var1) {
      try {
         FoliageColors.func_77467_a(TextureUtil.func_195725_a(var1, field_130079_a));
      } catch (IOException var3) {
      }

   }
}
