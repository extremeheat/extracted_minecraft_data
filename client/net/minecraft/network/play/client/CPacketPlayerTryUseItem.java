package net.minecraft.network.play.client;

import java.io.IOException;
import net.minecraft.network.Packet;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.play.INetHandlerPlayServer;
import net.minecraft.util.EnumHand;

public class CPacketPlayerTryUseItem implements Packet<INetHandlerPlayServer> {
   private EnumHand field_187029_a;

   public CPacketPlayerTryUseItem() {
      super();
   }

   public CPacketPlayerTryUseItem(EnumHand var1) {
      super();
      this.field_187029_a = var1;
   }

   public void func_148837_a(PacketBuffer var1) throws IOException {
      this.field_187029_a = (EnumHand)var1.func_179257_a(EnumHand.class);
   }

   public void func_148840_b(PacketBuffer var1) throws IOException {
      var1.func_179249_a(this.field_187029_a);
   }

   public void func_148833_a(INetHandlerPlayServer var1) {
      var1.func_147346_a(this);
   }

   public EnumHand func_187028_a() {
      return this.field_187029_a;
   }
}
