package net.minecraft.client.renderer;

import java.awt.image.BufferedImage;
import java.net.HttpURLConnection;
import java.net.URL;
import javax.imageio.ImageIO;
import net.minecraft.client.Minecraft;
import org.apache.commons.io.FileUtils;

class ThreadDownloadImageData$1 extends Thread {
   ThreadDownloadImageData$1(ThreadDownloadImageData var1, String var2) {
      super(var2);
      this.field_110932_a = var1;
   }

   @Override
   public void run() {
      HttpURLConnection var1 = null;
      ThreadDownloadImageData.access$200()
         .debug(
            "Downloading http texture from {} to {}",
            new Object[]{ThreadDownloadImageData.access$000(this.field_110932_a), ThreadDownloadImageData.access$100(this.field_110932_a)}
         );

      try {
         var1 = (HttpURLConnection)new URL(ThreadDownloadImageData.access$000(this.field_110932_a)).openConnection(Minecraft.func_71410_x().func_110437_J());
         var1.setDoInput(true);
         var1.setDoOutput(false);
         var1.connect();
         if (var1.getResponseCode() / 100 == 2) {
            BufferedImage var2;
            if (ThreadDownloadImageData.access$100(this.field_110932_a) != null) {
               FileUtils.copyInputStreamToFile(var1.getInputStream(), ThreadDownloadImageData.access$100(this.field_110932_a));
               var2 = ImageIO.read(ThreadDownloadImageData.access$100(this.field_110932_a));
            } else {
               var2 = ImageIO.read(var1.getInputStream());
            }

            if (ThreadDownloadImageData.access$300(this.field_110932_a) != null) {
               var2 = ThreadDownloadImageData.access$300(this.field_110932_a).func_78432_a(var2);
            }

            this.field_110932_a.func_147641_a(var2);
            return;
         }
      } catch (Exception var6) {
         ThreadDownloadImageData.access$200().error("Couldn't download http texture", var6);
         return;
      } finally {
         if (var1 != null) {
            var1.disconnect();
         }
      }
   }
}
