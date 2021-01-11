package net.minecraft.network.play.server;

import java.io.IOException;
import net.minecraft.network.Packet;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.play.INetHandlerPlayClient;
import net.minecraft.util.IChatComponent;

public class S02PacketChat implements Packet<INetHandlerPlayClient> {
   private IChatComponent field_148919_a;
   private byte field_179842_b;

   public S02PacketChat() {
      super();
   }

   public S02PacketChat(IChatComponent var1) {
      this(var1, (byte)1);
   }

   public S02PacketChat(IChatComponent var1, byte var2) {
      super();
      this.field_148919_a = var1;
      this.field_179842_b = var2;
   }

   public void func_148837_a(PacketBuffer var1) throws IOException {
      this.field_148919_a = var1.func_179258_d();
      this.field_179842_b = var1.readByte();
   }

   public void func_148840_b(PacketBuffer var1) throws IOException {
      var1.func_179256_a(this.field_148919_a);
      var1.writeByte(this.field_179842_b);
   }

   public void func_148833_a(INetHandlerPlayClient var1) {
      var1.func_147251_a(this);
   }

   public IChatComponent func_148915_c() {
      return this.field_148919_a;
   }

   public boolean func_148916_d() {
      return this.field_179842_b == 1 || this.field_179842_b == 2;
   }

   public byte func_179841_c() {
      return this.field_179842_b;
   }
}
