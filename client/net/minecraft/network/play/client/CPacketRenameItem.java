package net.minecraft.network.play.client;

import java.io.IOException;
import net.minecraft.network.Packet;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.play.INetHandlerPlayServer;

public class CPacketRenameItem implements Packet<INetHandlerPlayServer> {
   private String field_210352_a;

   public CPacketRenameItem() {
      super();
   }

   public CPacketRenameItem(String var1) {
      super();
      this.field_210352_a = var1;
   }

   public void func_148837_a(PacketBuffer var1) throws IOException {
      this.field_210352_a = var1.func_150789_c(32767);
   }

   public void func_148840_b(PacketBuffer var1) throws IOException {
      var1.func_180714_a(this.field_210352_a);
   }

   public void func_148833_a(INetHandlerPlayServer var1) {
      var1.func_210155_a(this);
   }

   public String func_210351_a() {
      return this.field_210352_a;
   }
}
