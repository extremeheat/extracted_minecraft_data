package net.minecraft.client.renderer.texture;

import java.io.IOException;
import java.util.List;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.item.EnumDyeColor;
import net.minecraft.resources.IResource;
import net.minecraft.resources.IResourceManager;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class LayeredColorMaskTexture extends AbstractTexture {
   private static final Logger field_174947_f = LogManager.getLogger();
   private final ResourceLocation field_174948_g;
   private final List<String> field_174949_h;
   private final List<EnumDyeColor> field_174950_i;

   public LayeredColorMaskTexture(ResourceLocation var1, List<String> var2, List<EnumDyeColor> var3) {
      super();
      this.field_174948_g = var1;
      this.field_174949_h = var2;
      this.field_174950_i = var3;
   }

   public void func_195413_a(IResourceManager var1) throws IOException {
      try {
         IResource var2 = var1.func_199002_a(this.field_174948_g);
         Throwable var3 = null;

         try {
            NativeImage var4 = NativeImage.func_195713_a(var2.func_199027_b());
            Throwable var5 = null;

            try {
               NativeImage var6 = new NativeImage(var4.func_195702_a(), var4.func_195714_b(), false);
               Throwable var7 = null;

               try {
                  var6.func_195703_a(var4);

                  for(int var8 = 0; var8 < 17 && var8 < this.field_174949_h.size() && var8 < this.field_174950_i.size(); ++var8) {
                     String var9 = (String)this.field_174949_h.get(var8);
                     if (var9 != null) {
                        IResource var10 = var1.func_199002_a(new ResourceLocation(var9));
                        Throwable var11 = null;

                        try {
                           NativeImage var12 = NativeImage.func_195713_a(var10.func_199027_b());
                           Throwable var13 = null;

                           try {
                              int var14 = ((EnumDyeColor)this.field_174950_i.get(var8)).func_196057_c();
                              if (var12.func_195702_a() == var6.func_195702_a() && var12.func_195714_b() == var6.func_195714_b()) {
                                 for(int var15 = 0; var15 < var12.func_195714_b(); ++var15) {
                                    for(int var16 = 0; var16 < var12.func_195702_a(); ++var16) {
                                       int var17 = var12.func_195709_a(var16, var15);
                                       if ((var17 & -16777216) != 0) {
                                          int var18 = (var17 & 255) << 24 & -16777216;
                                          int var19 = var4.func_195709_a(var16, var15);
                                          int var20 = MathHelper.func_180188_d(var19, var14) & 16777215;
                                          var6.func_195718_b(var16, var15, var18 | var20);
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

                  TextureUtil.func_110991_a(this.func_110552_b(), var6.func_195702_a(), var6.func_195714_b());
                  GlStateManager.func_199297_b(3357, 3.4028235E38F);
                  var6.func_195697_a(0, 0, 0, false);
                  GlStateManager.func_199297_b(3357, 0.0F);
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
         field_174947_f.error("Couldn't load layered color mask image", var150);
      }

   }
}
