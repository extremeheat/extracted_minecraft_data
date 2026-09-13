package net.minecraft.util;

import java.io.DataOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.Proxy;
import java.net.URL;
import java.net.URLConnection;
import java.util.Map;
import java.util.Map.Entry;

final class HttpUtil$1 implements Runnable {
   HttpUtil$1(IProgressUpdate var1, String var2, Proxy var3, Map var4, File var5, HttpUtil$DownloadListener var6, int var7) {
      super();
      this.field_151199_a = var1;
      this.field_151197_b = var2;
      this.field_151198_c = var3;
      this.field_151195_d = var4;
      this.field_151196_e = var5;
      this.field_151193_f = var6;
      this.field_151194_g = var7;
   }

   @Override
   public void run() {
      URLConnection var1 = null;
      InputStream var2 = null;
      DataOutputStream var3 = null;
      if (this.field_151199_a != null) {
         this.field_151199_a.func_73721_b("Downloading Texture Pack");
         this.field_151199_a.func_73719_c("Making Request...");
      }

      try {
         try {
            byte[] var4 = new byte[4096];
            URL var5 = new URL(this.field_151197_b);
            var1 = var5.openConnection(this.field_151198_c);
            float var6 = 0.0F;
            float var7 = (float)this.field_151195_d.entrySet().size();

            for(Entry var9 : this.field_151195_d.entrySet()) {
               var1.setRequestProperty((String)var9.getKey(), (String)var9.getValue());
               if (this.field_151199_a != null) {
                  this.field_151199_a.func_73718_a((int)(++var6 / var7 * 100.0F));
               }
            }

            var2 = var1.getInputStream();
            var7 = (float)var1.getContentLength();
            int var30 = var1.getContentLength();
            if (this.field_151199_a != null) {
               this.field_151199_a.func_73719_c(String.format("Downloading file (%.2f MB)...", var7 / 1000.0F / 1000.0F));
            }

            if (this.field_151196_e.exists()) {
               long var31 = this.field_151196_e.length();
               if (var31 == (long)var30) {
                  this.field_151193_f.func_148522_a(this.field_151196_e);
                  if (this.field_151199_a != null) {
                     this.field_151199_a.func_146586_a();
                  }

                  return;
               }

               HttpUtil.access$000()
                  .warn("Deleting " + this.field_151196_e + " as it does not match what we currently have (" + var30 + " vs our " + var31 + ").");
               this.field_151196_e.delete();
            } else if (this.field_151196_e.getParentFile() != null) {
               this.field_151196_e.getParentFile().mkdirs();
            }

            var3 = new DataOutputStream(new FileOutputStream(this.field_151196_e));
            if (this.field_151194_g > 0 && var7 > (float)this.field_151194_g) {
               if (this.field_151199_a != null) {
                  this.field_151199_a.func_146586_a();
               }

               throw new IOException("Filesize is bigger than maximum allowed (file is " + var6 + ", limit is " + this.field_151194_g + ")");
            }

            int var32 = 0;

            while((var32 = var2.read(var4)) >= 0) {
               var6 += (float)var32;
               if (this.field_151199_a != null) {
                  this.field_151199_a.func_73718_a((int)(var6 / var7 * 100.0F));
               }

               if (this.field_151194_g > 0 && var6 > (float)this.field_151194_g) {
                  if (this.field_151199_a != null) {
                     this.field_151199_a.func_146586_a();
                  }

                  throw new IOException("Filesize was bigger than maximum allowed (got >= " + var6 + ", limit was " + this.field_151194_g + ")");
               }

               var3.write(var4, 0, var32);
            }

            this.field_151193_f.func_148522_a(this.field_151196_e);
            if (this.field_151199_a != null) {
               this.field_151199_a.func_146586_a();
               return;
            }
         } catch (Throwable var26) {
            var26.printStackTrace();
         }
      } finally {
         try {
            if (var2 != null) {
               var2.close();
            }
         } catch (IOException var25) {
         }

         try {
            if (var3 != null) {
               var3.close();
            }
         } catch (IOException var24) {
         }
      }
   }
}
