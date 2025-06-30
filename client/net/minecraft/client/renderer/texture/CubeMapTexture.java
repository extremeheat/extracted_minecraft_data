package net.minecraft.client.renderer.texture;

import com.mojang.blaze3d.platform.NativeImage;
import com.mojang.blaze3d.systems.GpuDevice;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.textures.TextureFormat;
import java.io.IOException;
import java.util.Objects;
import net.minecraft.client.resources.metadata.texture.TextureMetadataSection;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;

public class CubeMapTexture extends ReloadableTexture {
   private static final String[] SUFFIXES = new String[]{"_1.png", "_3.png", "_5.png", "_4.png", "_0.png", "_2.png"};

   public CubeMapTexture(ResourceLocation var1) {
      super(var1);
   }

   public TextureContents loadContents(ResourceManager var1) throws IOException {
      ResourceLocation var2 = this.resourceId();
      TextureContents var3 = TextureContents.load(var1, var2.withSuffix(SUFFIXES[0]));

      TextureContents var15;
      try {
         int var4 = var3.image().getWidth();
         int var5 = var3.image().getHeight();
         NativeImage var6 = new NativeImage(var4, var5 * 6, false);
         var3.image().copyRect(var6, 0, 0, 0, 0, var4, var5, false, true);

         for(int var7 = 1; var7 < 6; ++var7) {
            TextureContents var8 = TextureContents.load(var1, var2.withSuffix(SUFFIXES[var7]));

            try {
               if (var8.image().getWidth() != var4 || var8.image().getHeight() != var5) {
                  String var10002 = String.valueOf(var2);
                  throw new IOException("Image dimensions of cubemap '" + var10002 + "' sides do not match: part 0 is " + var4 + "x" + var5 + ", but part " + var7 + " is " + var8.image().getWidth() + "x" + var8.image().getHeight());
               }

               var8.image().copyRect(var6, 0, 0, 0, var7 * var5, var4, var5, false, true);
            } catch (Throwable var13) {
               if (var8 != null) {
                  try {
                     var8.close();
                  } catch (Throwable var12) {
                     var13.addSuppressed(var12);
                  }
               }

               throw var13;
            }

            if (var8 != null) {
               var8.close();
            }
         }

         var15 = new TextureContents(var6, new TextureMetadataSection(true, false));
      } catch (Throwable var14) {
         if (var3 != null) {
            try {
               var3.close();
            } catch (Throwable var11) {
               var14.addSuppressed(var11);
            }
         }

         throw var14;
      }

      if (var3 != null) {
         var3.close();
      }

      return var15;
   }

   protected void doLoad(NativeImage var1, boolean var2, boolean var3) {
      GpuDevice var4 = RenderSystem.getDevice();
      int var5 = var1.getWidth();
      int var6 = var1.getHeight() / 6;
      this.close();
      ResourceLocation var10002 = this.resourceId();
      Objects.requireNonNull(var10002);
      this.texture = var4.createTexture(var10002::toString, 21, TextureFormat.RGBA8, var5, var6, 6, 1);
      this.textureView = var4.createTextureView(this.texture);
      this.setFilter(var2, false);
      this.setClamp(var3);

      for(int var7 = 0; var7 < 6; ++var7) {
         var4.createCommandEncoder().writeToTexture(this.texture, var1, 0, var7, 0, 0, var5, var6, 0, var6 * var7);
      }

   }
}
