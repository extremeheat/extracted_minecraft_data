package net.minecraft.network.play.client;

import java.io.IOException;
import net.minecraft.network.Packet;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.play.INetHandlerPlayServer;

public class C16PacketClientStatus implements Packet<INetHandlerPlayServer> {
   private C16PacketClientStatus.EnumState field_149437_a;

   public C16PacketClientStatus() {
      super();
   }

   public C16PacketClientStatus(C16PacketClientStatus.EnumState var1) {
      super();
      this.field_149437_a = var1;
   }

   public void func_148837_a(PacketBuffer var1) throws IOException {
      this.field_149437_a = (C16PacketClientStatus.EnumState)var1.func_179257_a(C16PacketClientStatus.EnumState.class);
   }

   public void func_148840_b(PacketBuffer var1) throws IOException {
      var1.func_179249_a(this.field_149437_a);
   }

   public void func_148833_a(INetHandlerPlayServer var1) {
      var1.func_147342_a(this);
   }

   public C16PacketClientStatus.EnumState func_149435_c() {
      return this.field_149437_a;
   }

   public static enum EnumState {
      PERFORM_RESPAWN,
      REQUEST_STATS,
      OPEN_INVENTORY_ACHIEVEMENT;

      private EnumState() {
      }
   }
}
