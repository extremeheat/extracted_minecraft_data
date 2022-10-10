package net.minecraft.client.renderer.texture;

import com.google.common.collect.Lists;
import java.io.IOException;
import java.util.Iterator;
import java.util.List;
import net.minecraft.resources.IResource;
import net.minecraft.resources.IResourceManager;
import net.minecraft.util.ResourceLocation;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class LayeredTexture extends AbstractTexture {
   private static final Logger field_147638_c = LogManager.getLogger();
   public final List<String> field_110567_b;

   public LayeredTexture(String... var1) {
      super();
      this.field_110567_b = Lists.newArrayList(var1);
      if (this.field_110567_b.isEmpty()) {
         throw new IllegalStateException("Layered texture with no layers.");
      }
   }

   public void func_195413_a(IResourceManager var1) throws IOException {
      Iterator var2 = this.field_110567_b.iterator();
      String var3 = (String)var2.next();

      try {
         IResource var4 = var1.func_199002_a(new ResourceLocation(var3));
         Throwable var5 = null;

         try {
            NativeImage var6 = NativeImage.func_195713_a(var4.func_199027_b());
            Throwable var7 = null;

            try {
               while(true) {
                  String var8;
                  do {
                     if (!var2.hasNext()) {
                        TextureUtil.func_110991_a(this.func_110552_b(), var6.func_195702_a(), var6.func_195714_b());
                        var6.func_195697_a(0, 0, 0, false);
                        return;
                     }

                     var8 = (String)var2.next();
                  } while(var8 == null);

                  IResource var9 = var1.func_199002_a(new ResourceLocation(var8));
                  Throwable var10 = null;

                  try {
                     NativeImage var11 = NativeImage.func_195713_a(var9.func_199027_b());
                     Throwable var12 = null;

                     try {
                        for(int var13 = 0; var13 < var11.func_195714_b(); ++var13) {
                           for(int var14 = 0; var14 < var11.func_195702_a(); ++var14) {
                              var6.func_195718_b(var14, var13, var11.func_195709_a(var14, var13));
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
         field_147638_c.error("Couldn't load layered image", var97);
      }
   }
}
