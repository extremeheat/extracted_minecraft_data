package net.minecraft.client.renderer.texture;

import java.io.IOException;
import net.minecraft.client.resources.data.TextureMetadataSection;
import net.minecraft.resources.IResource;
import net.minecraft.resources.IResourceManager;
import net.minecraft.util.ResourceLocation;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class SimpleTexture extends AbstractTexture {
   private static final Logger field_147639_c = LogManager.getLogger();
   protected final ResourceLocation field_110568_b;

   public SimpleTexture(ResourceLocation var1) {
      super();
      this.field_110568_b = var1;
   }

   public void func_195413_a(IResourceManager var1) throws IOException {
      IResource var2 = var1.func_199002_a(this.field_110568_b);
      Throwable var3 = null;

      try {
         NativeImage var4 = NativeImage.func_195713_a(var2.func_199027_b());
         Throwable var5 = null;

         try {
            boolean var6 = false;
            boolean var7 = false;
            if (var2.func_199030_c()) {
               try {
                  TextureMetadataSection var8 = (TextureMetadataSection)var2.func_199028_a(TextureMetadataSection.field_195819_a);
                  if (var8 != null) {
                     var6 = var8.func_110479_a();
                     var7 = var8.func_110480_b();
                  }
               } catch (RuntimeException var32) {
                  field_147639_c.warn("Failed reading metadata of: {}", this.field_110568_b, var32);
               }
            }

            this.func_195412_h();
            TextureUtil.func_180600_a(this.func_110552_b(), 0, var4.func_195702_a(), var4.func_195714_b());
            var4.func_195712_a(0, 0, 0, 0, 0, var4.func_195702_a(), var4.func_195714_b(), var6, var7, false);
         } catch (Throwable var33) {
            var5 = var33;
            throw var33;
         } finally {
            if (var4 != null) {
               if (var5 != null) {
                  try {
                     var4.close();
                  } catch (Throwable var31) {
                     var5.addSuppressed(var31);
                  }
               } else {
                  var4.close();
               }
            }

         }
      } catch (Throwable var35) {
         var3 = var35;
         throw var35;
      } finally {
         if (var2 != null) {
            if (var3 != null) {
               try {
                  var2.close();
               } catch (Throwable var30) {
                  var3.addSuppressed(var30);
               }
            } else {
               var2.close();
            }
         }

      }

   }
}
