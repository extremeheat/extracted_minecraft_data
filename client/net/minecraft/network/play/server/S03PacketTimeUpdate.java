package net.minecraft.network.play.server;

import java.io.IOException;
import net.minecraft.network.Packet;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.play.INetHandlerPlayClient;

public class S03PacketTimeUpdate implements Packet<INetHandlerPlayClient> {
   private long field_149369_a;
   private long field_149368_b;

   public S03PacketTimeUpdate() {
      super();
   }

   public S03PacketTimeUpdate(long var1, long var3, boolean var5) {
      super();
      this.field_149369_a = var1;
      this.field_149368_b = var3;
      if (!var5) {
         this.field_149368_b = -this.field_149368_b;
         if (this.field_149368_b == 0L) {
            this.field_149368_b = -1L;
         }
      }

   }

   public void func_148837_a(PacketBuffer var1) throws IOException {
      this.field_149369_a = var1.readLong();
      this.field_149368_b = var1.readLong();
   }

   public void func_148840_b(PacketBuffer var1) throws IOException {
      var1.writeLong(this.field_149369_a);
      var1.writeLong(this.field_149368_b);
   }

   public void func_148833_a(INetHandlerPlayClient var1) {
      var1.func_147285_a(this);
   }

   public long func_149366_c() {
      return this.field_149369_a;
   }

   public long func_149365_d() {
      return this.field_149368_b;
   }
}
