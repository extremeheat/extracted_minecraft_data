package net.minecraft.client.renderer.texture;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import net.minecraft.client.resources.IResource;
import net.minecraft.client.resources.IResourceManager;
import net.minecraft.client.resources.data.TextureMetadataSection;
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

   public void func_110551_a(IResourceManager var1) throws IOException {
      this.func_147631_c();
      InputStream var2 = null;

      try {
         IResource var3 = var1.func_110536_a(this.field_110568_b);
         var2 = var3.func_110527_b();
         BufferedImage var4 = TextureUtil.func_177053_a(var2);
         boolean var5 = false;
         boolean var6 = false;
         if (var3.func_110528_c()) {
            try {
               TextureMetadataSection var7 = (TextureMetadataSection)var3.func_110526_a("texture");
               if (var7 != null) {
                  var5 = var7.func_110479_a();
                  var6 = var7.func_110480_b();
               }
            } catch (RuntimeException var11) {
               field_147639_c.warn("Failed reading metadata of: " + this.field_110568_b, var11);
            }
         }

         TextureUtil.func_110989_a(this.func_110552_b(), var4, var5, var6);
      } finally {
         if (var2 != null) {
            var2.close();
         }

      }

   }
}
