package net.minecraft.network.login.server;

import java.io.IOException;
import net.minecraft.network.Packet;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.login.INetHandlerLoginClient;
import net.minecraft.util.text.ITextComponent;

public class SPacketDisconnectLogin implements Packet<INetHandlerLoginClient> {
   private ITextComponent field_149605_a;

   public SPacketDisconnectLogin() {
      super();
   }

   public SPacketDisconnectLogin(ITextComponent var1) {
      super();
      this.field_149605_a = var1;
   }

   public void func_148837_a(PacketBuffer var1) throws IOException {
      this.field_149605_a = ITextComponent.Serializer.func_186877_b(var1.func_150789_c(32767));
   }

   public void func_148840_b(PacketBuffer var1) throws IOException {
      var1.func_179256_a(this.field_149605_a);
   }

   public void func_148833_a(INetHandlerLoginClient var1) {
      var1.func_147388_a(this);
   }

   public ITextComponent func_149603_c() {
      return this.field_149605_a;
   }
}
