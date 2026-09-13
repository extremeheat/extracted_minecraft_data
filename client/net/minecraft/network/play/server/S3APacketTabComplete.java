package net.minecraft.network.play.server;

import net.minecraft.network.Packet;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.play.INetHandlerPlayClient;
import org.apache.commons.lang3.ArrayUtils;

public class S3APacketTabComplete extends Packet {
   private String[] field_149632_a;

   public S3APacketTabComplete() {
      super();
   }

   public S3APacketTabComplete(String[] var1) {
      super();
      this.field_149632_a = var1;
   }

   @Override
   public void func_148837_a(PacketBuffer var1) {
      this.field_149632_a = new String[var1.func_150792_a()];

      for(int var2 = 0; var2 < this.field_149632_a.length; ++var2) {
         this.field_149632_a[var2] = var1.func_150789_c(32767);
      }
   }

   @Override
   public void func_148840_b(PacketBuffer var1) {
      var1.func_150787_b(this.field_149632_a.length);

      for(String var5 : this.field_149632_a) {
         var1.func_150785_a(var5);
      }
   }

   public void func_148833_a(INetHandlerPlayClient var1) {
      var1.func_147274_a(this);
   }

   public String[] func_149630_c() {
      return this.field_149632_a;
   }

   @Override
   public String func_148835_b() {
      return String.format("candidates='%s'", ArrayUtils.toString(this.field_149632_a));
   }
}
