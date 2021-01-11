package net.minecraft.client.renderer;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.concurrent.atomic.AtomicInteger;
import javax.imageio.ImageIO;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.texture.SimpleTexture;
import net.minecraft.client.renderer.texture.TextureUtil;
import net.minecraft.client.resources.IResourceManager;
import net.minecraft.util.ResourceLocation;
import org.apache.commons.io.FileUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class ThreadDownloadImageData extends SimpleTexture {
   private static final Logger field_147644_c = LogManager.getLogger();
   private static final AtomicInteger field_147643_d = new AtomicInteger(0);
   private final File field_152434_e;
   private final String field_110562_b;
   private final IImageBuffer field_110563_c;
   private BufferedImage field_110560_d;
   private Thread field_110561_e;
   private boolean field_110559_g;

   public ThreadDownloadImageData(File var1, String var2, ResourceLocation var3, IImageBuffer var4) {
      super(var3);
      this.field_152434_e = var1;
      this.field_110562_b = var2;
      this.field_110563_c = var4;
   }

   private void func_147640_e() {
      if (!this.field_110559_g) {
         if (this.field_110560_d != null) {
            if (this.field_110568_b != null) {
               this.func_147631_c();
            }

            TextureUtil.func_110987_a(super.func_110552_b(), this.field_110560_d);
            this.field_110559_g = true;
         }

      }
   }

   public int func_110552_b() {
      this.func_147640_e();
      return super.func_110552_b();
   }

   public void func_147641_a(BufferedImage var1) {
      this.field_110560_d = var1;
      if (this.field_110563_c != null) {
         this.field_110563_c.func_152634_a();
      }

   }

   public void func_110551_a(IResourceManager var1) throws IOException {
      if (this.field_110560_d == null && this.field_110568_b != null) {
         super.func_110551_a(var1);
      }

      if (this.field_110561_e == null) {
         if (this.field_152434_e != null && this.field_152434_e.isFile()) {
            field_147644_c.debug("Loading http texture from local cache ({})", new Object[]{this.field_152434_e});

            try {
               this.field_110560_d = ImageIO.read(this.field_152434_e);
               if (this.field_110563_c != null) {
                  this.func_147641_a(this.field_110563_c.func_78432_a(this.field_110560_d));
               }
            } catch (IOException var3) {
               field_147644_c.error("Couldn't load skin " + this.field_152434_e, var3);
               this.func_152433_a();
            }
         } else {
            this.func_152433_a();
         }
      }

   }

   protected void func_152433_a() {
      this.field_110561_e = new Thread("Texture Downloader #" + field_147643_d.incrementAndGet()) {
         public void run() {
            HttpURLConnection var1 = null;
            ThreadDownloadImageData.field_147644_c.debug("Downloading http texture from {} to {}", new Object[]{ThreadDownloadImageData.this.field_110562_b, ThreadDownloadImageData.this.field_152434_e});

            try {
               var1 = (HttpURLConnection)(new URL(ThreadDownloadImageData.this.field_110562_b)).openConnection(Minecraft.func_71410_x().func_110437_J());
               var1.setDoInput(true);
               var1.setDoOutput(false);
               var1.connect();
               if (var1.getResponseCode() / 100 != 2) {
                  return;
               }

               BufferedImage var2;
               if (ThreadDownloadImageData.this.field_152434_e != null) {
                  FileUtils.copyInputStreamToFile(var1.getInputStream(), ThreadDownloadImageData.this.field_152434_e);
                  var2 = ImageIO.read(ThreadDownloadImageData.this.field_152434_e);
               } else {
                  var2 = TextureUtil.func_177053_a(var1.getInputStream());
               }

               if (ThreadDownloadImageData.this.field_110563_c != null) {
                  var2 = ThreadDownloadImageData.this.field_110563_c.func_78432_a(var2);
               }

               ThreadDownloadImageData.this.func_147641_a(var2);
            } catch (Exception var6) {
               ThreadDownloadImageData.field_147644_c.error("Couldn't download http texture", var6);
            } finally {
               if (var1 != null) {
                  var1.disconnect();
               }

            }

         }
      };
      this.field_110561_e.setDaemon(true);
      this.field_110561_e.start();
   }
}
