package net.minecraft.network.rcon;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.net.SocketTimeoutException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class RConThreadClient extends RConThreadBase {
   private static final Logger field_164005_h = LogManager.getLogger();
   private boolean field_72657_g;
   private Socket field_72659_h;
   private final byte[] field_72660_i = new byte[1460];
   private final String field_72658_j;

   RConThreadClient(IServer var1, Socket var2) {
      super(var1, "RCON Client");
      this.field_72659_h = var2;

      try {
         this.field_72659_h.setSoTimeout(0);
      } catch (Exception var4) {
         this.field_72619_a = false;
      }

      this.field_72658_j = var1.func_71330_a("rcon.password", "");
      this.func_72609_b("Rcon connection from: " + var2.getInetAddress());
   }

   public void run() {
      while(true) {
         try {
            if (!this.field_72619_a) {
               return;
            }

            BufferedInputStream var1 = new BufferedInputStream(this.field_72659_h.getInputStream());
            int var2 = var1.read(this.field_72660_i, 0, 1460);
            if (10 <= var2) {
               byte var3 = 0;
               int var4 = RConUtils.func_72665_b(this.field_72660_i, 0, var2);
               if (var4 != var2 - 4) {
                  return;
               }

               int var21 = var3 + 4;
               int var5 = RConUtils.func_72665_b(this.field_72660_i, var21, var2);
               var21 += 4;
               int var6 = RConUtils.func_72662_b(this.field_72660_i, var21);
               var21 += 4;
               switch(var6) {
               case 2:
                  if (this.field_72657_g) {
                     String var8 = RConUtils.func_72661_a(this.field_72660_i, var21, var2);

                     try {
                        this.func_72655_a(var5, this.field_72617_b.func_71252_i(var8));
                     } catch (Exception var16) {
                        this.func_72655_a(var5, "Error executing: " + var8 + " (" + var16.getMessage() + ")");
                     }
                     continue;
                  }

                  this.func_72656_f();
                  continue;
               case 3:
                  String var7 = RConUtils.func_72661_a(this.field_72660_i, var21, var2);
                  int var10000 = var21 + var7.length();
                  if (!var7.isEmpty() && var7.equals(this.field_72658_j)) {
                     this.field_72657_g = true;
                     this.func_72654_a(var5, 2, "");
                     continue;
                  }

                  this.field_72657_g = false;
                  this.func_72656_f();
                  continue;
               default:
                  this.func_72655_a(var5, String.format("Unknown request %s", Integer.toHexString(var6)));
                  continue;
               }
            }
         } catch (SocketTimeoutException var17) {
            return;
         } catch (IOException var18) {
            return;
         } catch (Exception var19) {
            field_164005_h.error("Exception whilst parsing RCON input", var19);
            return;
         } finally {
            this.func_72653_g();
         }

         return;
      }
   }

   private void func_72654_a(int var1, int var2, String var3) throws IOException {
      ByteArrayOutputStream var4 = new ByteArrayOutputStream(1248);
      DataOutputStream var5 = new DataOutputStream(var4);
      byte[] var6 = var3.getBytes("UTF-8");
      var5.writeInt(Integer.reverseBytes(var6.length + 10));
      var5.writeInt(Integer.reverseBytes(var1));
      var5.writeInt(Integer.reverseBytes(var2));
      var5.write(var6);
      var5.write(0);
      var5.write(0);
      this.field_72659_h.getOutputStream().write(var4.toByteArray());
   }

   private void func_72656_f() throws IOException {
      this.func_72654_a(-1, 2, "");
   }

   private void func_72655_a(int var1, String var2) throws IOException {
      int var3 = var2.length();

      do {
         int var4 = 4096 <= var3 ? 4096 : var3;
         this.func_72654_a(var1, 0, var2.substring(0, var4));
         var2 = var2.substring(var4);
         var3 = var2.length();
      } while(0 != var3);

   }

   private void func_72653_g() {
      if (null != this.field_72659_h) {
         try {
            this.field_72659_h.close();
         } catch (IOException var2) {
            this.func_72606_c("IO: " + var2.getMessage());
         }

         this.field_72659_h = null;
      }
   }
}
