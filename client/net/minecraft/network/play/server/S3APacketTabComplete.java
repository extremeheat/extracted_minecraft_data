package net.minecraft.network.play.server;

import java.io.IOException;
import net.minecraft.network.Packet;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.play.INetHandlerPlayClient;

public class S3APacketTabComplete implements Packet<INetHandlerPlayClient> {
   private String[] field_149632_a;

   public S3APacketTabComplete() {
      super();
   }

   public S3APacketTabComplete(String[] var1) {
      super();
      this.field_149632_a = var1;
   }

   public void func_148837_a(PacketBuffer var1) throws IOException {
      this.field_149632_a = new String[var1.func_150792_a()];

      for(int var2 = 0; var2 < this.field_149632_a.length; ++var2) {
         this.field_149632_a[var2] = var1.func_150789_c(32767);
      }

   }

   public void func_148840_b(PacketBuffer var1) throws IOException {
      var1.func_150787_b(this.field_149632_a.length);
      String[] var2 = this.field_149632_a;
      int var3 = var2.length;

      for(int var4 = 0; var4 < var3; ++var4) {
         String var5 = var2[var4];
         var1.func_180714_a(var5);
      }

   }

   public void func_148833_a(INetHandlerPlayClient var1) {
      var1.func_147274_a(this);
   }

   public String[] func_149630_c() {
      return this.field_149632_a;
   }
}
