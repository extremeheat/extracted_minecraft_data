package net.minecraft.client.renderer.texture;

import com.google.common.collect.Lists;
import com.mojang.blaze3d.platform.NativeImage;
import com.mojang.blaze3d.platform.TextureUtil;
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
   public final List<String> layerPaths;

   public LayeredTexture(String... var1) {
      super();
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
            Throwable var7 = null;

            try {
               while(true) {
                  String var8;
                  do {
                     if (!var2.hasNext()) {
                        TextureUtil.prepareImage(this.getId(), var6.getWidth(), var6.getHeight());
                        var6.upload(0, 0, 0, false);
                        return;
                     }

                     var8 = (String)var2.next();
                  } while(var8 == null);

                  Resource var9 = var1.getResource(new ResourceLocation(var8));
                  Throwable var10 = null;

                  try {
                     NativeImage var11 = NativeImage.read(var9.getInputStream());
                     Throwable var12 = null;

                     try {
                        for(int var13 = 0; var13 < var11.getHeight(); ++var13) {
                           for(int var14 = 0; var14 < var11.getWidth(); ++var14) {
                              var6.blendPixel(var14, var13, var11.getPixelRGBA(var14, var13));
                           }
                        }
                     } catch (Throwable var89) {
                        var12 = var89;
                        throw var89;
                     } finally {
                        if (var11 != null) {
                           if (var12 != null) {
                              try {
                                 var11.close();
                              } catch (Throwable var88) {
                                 var12.addSuppressed(var88);
                              }
                           } else {
                              var11.close();
                           }
                        }

                     }
                  } catch (Throwable var91) {
                     var10 = var91;
                     throw var91;
                  } finally {
                     if (var9 != null) {
                        if (var10 != null) {
                           try {
                              var9.close();
                           } catch (Throwable var87) {
                              var10.addSuppressed(var87);
                           }
                        } else {
                           var9.close();
                        }
                     }

                  }
               }
            } catch (Throwable var93) {
               var7 = var93;
               throw var93;
            } finally {
               if (var6 != null) {
                  if (var7 != null) {
                     try {
                        var6.close();
                     } catch (Throwable var86) {
                        var7.addSuppressed(var86);
                     }
                  } else {
                     var6.close();
                  }
               }

            }
         } catch (Throwable var95) {
            var5 = var95;
            throw var95;
         } finally {
            if (var4 != null) {
               if (var5 != null) {
                  try {
                     var4.close();
                  } catch (Throwable var85) {
                     var5.addSuppressed(var85);
                  }
               } else {
                  var4.close();
               }
            }

         }
      } catch (IOException var97) {
         LOGGER.error("Couldn't load layered image", var97);
      }
   }
}
