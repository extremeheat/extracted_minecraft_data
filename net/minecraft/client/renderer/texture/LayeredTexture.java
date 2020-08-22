package net.minecraft.client.renderer.texture;

import com.google.common.collect.Lists;
import com.mojang.blaze3d.platform.NativeImage;
import com.mojang.blaze3d.platform.TextureUtil;
import com.mojang.blaze3d.systems.RenderSystem;
import java.io.IOException;
import java.util.Iterator;
import java.util.List;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.Resource;
import net.minecraft.server.packs.resources.ResourceManager;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class LayeredTexture extends AbstractTexture {
   private static final Logger LOGGER = LogManager.getLogger();
   public final List layerPaths;

   public LayeredTexture(String... var1) {
      this.layerPaths = Lists.newArrayList(var1);
      if (this.layerPaths.isEmpty()) {
         throw new IllegalStateException("Layered texture with no layers.");
      }
   }

   public void load(ResourceManager var1) throws IOException {
      Iterator var2 = this.layerPaths.iterator();
      String var3 = (String)var2.next();

      try {
         Resource var4 = var1.getResource(new ResourceLocation(var3));
         Throwable var5 = null;

         try {
            NativeImage var6 = NativeImage.read(var4.getInputStream());

            while(true) {
               String var7;
               do {
                  if (!var2.hasNext()) {
                     if (!RenderSystem.isOnRenderThreadOrInit()) {
                        RenderSystem.recordRenderCall(() -> {
                           this.doLoad(var6);
                        });
                     } else {
                        this.doLoad(var6);
                     }

                     return;
                  }

                  var7 = (String)var2.next();
               } while(var7 == null);

               Resource var8 = var1.getResource(new ResourceLocation(var7));
               Throwable var9 = null;

               try {
                  NativeImage var10 = NativeImage.read(var8.getInputStream());
                  Throwable var11 = null;

                  try {
                     for(int var12 = 0; var12 < var10.getHeight(); ++var12) {
                        for(int var13 = 0; var13 < var10.getWidth(); ++var13) {
                           var6.blendPixel(var13, var12, var10.getPixelRGBA(var13, var12));
                        }
                     }
                  } catch (Throwable var59) {
                     var11 = var59;
                     throw var59;
                  } finally {
                     if (var10 != null) {
                        if (var11 != null) {
                           try {
                              var10.close();
                           } catch (Throwable var58) {
                              var11.addSuppressed(var58);
                           }
                        } else {
                           var10.close();
                        }
                     }

                  }
               } catch (Throwable var61) {
                  var9 = var61;
                  throw var61;
               } finally {
                  if (var8 != null) {
                     if (var9 != null) {
                        try {
                           var8.close();
                        } catch (Throwable var57) {
                           var9.addSuppressed(var57);
                        }
                     } else {
                        var8.close();
                     }
                  }

               }
            }
         } catch (Throwable var63) {
            var5 = var63;
            throw var63;
         } finally {
            if (var4 != null) {
               if (var5 != null) {
                  try {
                     var4.close();
                  } catch (Throwable var56) {
                     var5.addSuppressed(var56);
                  }
               } else {
                  var4.close();
               }
            }

         }
      } catch (IOException var65) {
         LOGGER.error("Couldn't load layered image", var65);
      }
   }

   private void doLoad(NativeImage var1) {
      TextureUtil.prepareImage(this.getId(), var1.getWidth(), var1.getHeight());
      var1.upload(0, 0, 0, true);
   }
}
