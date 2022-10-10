package net.minecraft.network.play.server;

import java.io.IOException;
import net.minecraft.network.Packet;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.play.INetHandlerPlayClient;
import net.minecraft.util.text.ITextComponent;

public class SPacketPlayerListHeaderFooter implements Packet<INetHandlerPlayClient> {
   private ITextComponent field_179703_a;
   private ITextComponent field_179702_b;

   public SPacketPlayerListHeaderFooter() {
      super();
   }

   public void func_148837_a(PacketBuffer var1) throws IOException {
      this.field_179703_a = var1.func_179258_d();
      this.field_179702_b = var1.func_179258_d();
   }

   public void func_148840_b(PacketBuffer var1) throws IOException {
      var1.func_179256_a(this.field_179703_a);
      var1.func_179256_a(this.field_179702_b);
   }

   public void func_148833_a(INetHandlerPlayClient var1) {
      var1.func_175096_a(this);
   }

   public ITextComponent func_179700_a() {
      return this.field_179703_a;
   }

   public ITextComponent func_179701_b() {
      return this.field_179702_b;
   }
}
