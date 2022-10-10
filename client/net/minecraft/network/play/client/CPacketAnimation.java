package net.minecraft.network.play.client;

import java.io.IOException;
import net.minecraft.network.Packet;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.play.INetHandlerPlayServer;
import net.minecraft.util.EnumHand;

public class CPacketAnimation implements Packet<INetHandlerPlayServer> {
   private EnumHand field_187019_a;

   public CPacketAnimation() {
      super();
   }

   public CPacketAnimation(EnumHand var1) {
      super();
      this.field_187019_a = var1;
   }

   public void func_148837_a(PacketBuffer var1) throws IOException {
      this.field_187019_a = (EnumHand)var1.func_179257_a(EnumHand.class);
   }

   public void func_148840_b(PacketBuffer var1) throws IOException {
      var1.func_179249_a(this.field_187019_a);
   }

   public void func_148833_a(INetHandlerPlayServer var1) {
      var1.func_175087_a(this);
   }

   public EnumHand func_187018_a() {
      return this.field_187019_a;
   }
}
