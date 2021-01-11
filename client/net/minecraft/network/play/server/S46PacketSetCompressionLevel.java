package net.minecraft.network.play.server;

import java.io.IOException;
import net.minecraft.network.Packet;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.play.INetHandlerPlayClient;

public class S46PacketSetCompressionLevel implements Packet<INetHandlerPlayClient> {
   private int field_179761_a;

   public S46PacketSetCompressionLevel() {
      super();
   }

   public void func_148837_a(PacketBuffer var1) throws IOException {
      this.field_179761_a = var1.func_150792_a();
   }

   public void func_148840_b(PacketBuffer var1) throws IOException {
      var1.func_150787_b(this.field_179761_a);
   }

   public void func_148833_a(INetHandlerPlayClient var1) {
      var1.func_175100_a(this);
   }

   public int func_179760_a() {
      return this.field_179761_a;
   }
}
