package net.minecraft.network.play.server;

import java.io.IOException;
import net.minecraft.network.Packet;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.play.INetHandlerPlayClient;

public class SPacketUnloadChunk implements Packet<INetHandlerPlayClient> {
   private int field_186942_a;
   private int field_186943_b;

   public SPacketUnloadChunk() {
      super();
   }

   public SPacketUnloadChunk(int var1, int var2) {
      super();
      this.field_186942_a = var1;
      this.field_186943_b = var2;
   }

   public void func_148837_a(PacketBuffer var1) throws IOException {
      this.field_186942_a = var1.readInt();
      this.field_186943_b = var1.readInt();
   }

   public void func_148840_b(PacketBuffer var1) throws IOException {
      var1.writeInt(this.field_186942_a);
      var1.writeInt(this.field_186943_b);
   }

   public void func_148833_a(INetHandlerPlayClient var1) {
      var1.func_184326_a(this);
   }

   public int func_186940_a() {
      return this.field_186942_a;
   }

   public int func_186941_b() {
      return this.field_186943_b;
   }
}
