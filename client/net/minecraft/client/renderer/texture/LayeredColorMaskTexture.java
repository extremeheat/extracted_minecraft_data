package net.minecraft.client.renderer.texture;

import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.platform.NativeImage;
import com.mojang.blaze3d.platform.TextureUtil;
import java.io.IOException;
import java.util.List;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.Resource;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.util.Mth;
import net.minecraft.world.item.DyeColor;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class LayeredColorMaskTexture extends AbstractTexture {
   private static final Logger LOGGER = LogManager.getLogger();
   private final ResourceLocation baseLayerResource;
   private final List<String> layerMaskPaths;
   private final List<DyeColor> layerColors;

   public LayeredColorMaskTexture(ResourceLocation var1, List<String> var2, List<DyeColor> var3) {
      super();
      this.baseLayerResource = var1;
      this.layerMaskPaths = var2;
      this.layerColors = var3;
   }

   public void load(ResourceManager var1) throws IOException {
      try {
         Resource var2 = var1.getResource(this.baseLayerResource);
         Throwable var3 = null;

         try {
            NativeImage var4 = NativeImage.read(var2.getInputStream());
            Throwable var5 = null;

            try {
               NativeImage var6 = new NativeImage(var4.getWidth(), var4.getHeight(), false);
               Throwable var7 = null;

               try {
                  var6.copyFrom(var4);

                  for(int var8 = 0; var8 < 17 && var8 < this.layerMaskPaths.size() && var8 < this.layerColors.size(); ++var8) {
                     String var9 = (String)this.layerMaskPaths.get(var8);
                     if (var9 != null) {
                        Resource var10 = var1.getResource(new ResourceLocation(var9));
                        Throwable var11 = null;

                        try {
                           NativeImage var12 = NativeImage.read(var10.getInputStream());
                           Throwable var13 = null;

                           try {
                              int var14 = ((DyeColor)this.layerColors.get(var8)).getTextureDiffuseColorBGR();
                              if (var12.getWidth() == var6.getWidth() && var12.getHeight() == var6.getHeight()) {
                                 for(int var15 = 0; var15 < var12.getHeight(); ++var15) {
                                    for(int var16 = 0; var16 < var12.getWidth(); ++var16) {
                                       int var17 = var12.getPixelRGBA(var16, var15);
                                       if ((var17 & -16777216) != 0) {
                                          int var18 = (var17 & 255) << 24 & -16777216;
                                          int var19 = var4.getPixelRGBA(var16, var15);
                                          int var20 = Mth.colorMultiply(var19, var14) & 16777215;
                                          var6.blendPixel(var16, var15, var18 | var20);
                                       }
                                    }
                                 }
                              }
                           } catch (Throwable var140) {
                              var13 = var140;
                              throw var140;
                           } finally {
                              if (var12 != null) {
                                 if (var13 != null) {
                                    try {
                                       var12.close();
                                    } catch (Throwable var139) {
                                       var13.addSuppressed(var139);
                                    }
                                 } else {
                                    var12.close();
                                 }
                              }

                           }
                        } catch (Throwable var142) {
                           var11 = var142;
                           throw var142;
                        } finally {
                           if (var10 != null) {
                              if (var11 != null) {
                                 try {
                                    var10.close();
                                 } catch (Throwable var138) {
                                    var11.addSuppressed(var138);
                                 }
                              } else {
                                 var10.close();
                              }
                           }

                        }
                     }
                  }

                  TextureUtil.prepareImage(this.getId(), var6.getWidth(), var6.getHeight());
                  GlStateManager.pixelTransfer(3357, 3.4028235E38F);
                  var6.upload(0, 0, 0, false);
                  GlStateManager.pixelTransfer(3357, 0.0F);
               } catch (Throwable var144) {
                  var7 = var144;
                  throw var144;
               } finally {
                  if (var6 != null) {
                     if (var7 != null) {
                        try {
                           var6.close();
                        } catch (Throwable var137) {
                           var7.addSuppressed(var137);
                        }
                     } else {
                        var6.close();
                     }
                  }

               }
            } catch (Throwable var146) {
               var5 = var146;
               throw var146;
            } finally {
               if (var4 != null) {
                  if (var5 != null) {
                     try {
                        var4.close();
                     } catch (Throwable var136) {
                        var5.addSuppressed(var136);
                     }
                  } else {
                     var4.close();
                  }
               }

            }
         } catch (Throwable var148) {
            var3 = var148;
            throw var148;
         } finally {
            if (var2 != null) {
               if (var3 != null) {
                  try {
                     var2.close();
                  } catch (Throwable var135) {
                     var3.addSuppressed(var135);
                  }
               } else {
                  var2.close();
               }
            }

         }
      } catch (IOException var150) {
         LOGGER.error("Couldn't load layered color mask image", var150);
      }

   }
}
