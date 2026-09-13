package net.minecraft.client.network;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.net.SocketTimeoutException;

public class LanServerDetector$ThreadLanServerFind extends Thread {
   private final LanServerDetector$LanServerList field_77500_a;
   private final InetAddress field_77498_b;
   private final MulticastSocket field_77499_c;

   public LanServerDetector$ThreadLanServerFind(LanServerDetector$LanServerList var1) {
      super("LanServerDetector #" + LanServerDetector.access$000().incrementAndGet());
      this.field_77500_a = var1;
      this.setDaemon(true);
      this.field_77499_c = new MulticastSocket(4445);
      this.field_77498_b = InetAddress.getByName("224.0.2.60");
      this.field_77499_c.setSoTimeout(5000);
      this.field_77499_c.joinGroup(this.field_77498_b);
   }

   @Override
   public void run() {
      byte[] var2 = new byte[1024];

      while(!this.isInterrupted()) {
         DatagramPacket var1 = new DatagramPacket(var2, var2.length);

         try {
            this.field_77499_c.receive(var1);
         } catch (SocketTimeoutException var5) {
            continue;
         } catch (IOException var6) {
            LanServerDetector.access$100().error("Couldn't ping server", var6);
            break;
         }

         String var3 = new String(var1.getData(), var1.getOffset(), var1.getLength());
         LanServerDetector.access$100().debug(var1.getAddress() + ": " + var3);
         this.field_77500_a.func_77551_a(var3, var1.getAddress());
      }

      try {
         this.field_77499_c.leaveGroup(this.field_77498_b);
      } catch (IOException var4) {
      }

      this.field_77499_c.close();
   }
}
