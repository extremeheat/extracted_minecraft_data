package net.minecraft.network.play.server;

import java.io.IOException;
import net.minecraft.network.Packet;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.play.INetHandlerPlayClient;
import net.minecraft.util.IChatComponent;

public class S47PacketPlayerListHeaderFooter implements Packet<INetHandlerPlayClient> {
   private IChatComponent field_179703_a;
   private IChatComponent field_179702_b;

   public S47PacketPlayerListHeaderFooter() {
      super();
   }

   public S47PacketPlayerListHeaderFooter(IChatComponent var1) {
      super();
      this.field_179703_a = var1;
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

   public IChatComponent func_179700_a() {
      return this.field_179703_a;
   }

   public IChatComponent func_179701_b() {
      return this.field_179702_b;
   }
}
