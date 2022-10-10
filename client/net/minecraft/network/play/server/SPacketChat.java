package net.minecraft.network.play.server;

import java.io.IOException;
import net.minecraft.network.Packet;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.play.INetHandlerPlayClient;
import net.minecraft.util.text.ChatType;
import net.minecraft.util.text.ITextComponent;

public class SPacketChat implements Packet<INetHandlerPlayClient> {
   private ITextComponent field_148919_a;
   private ChatType field_179842_b;

   public SPacketChat() {
      super();
   }

   public SPacketChat(ITextComponent var1) {
      this(var1, ChatType.SYSTEM);
   }

   public SPacketChat(ITextComponent var1, ChatType var2) {
      super();
      this.field_148919_a = var1;
      this.field_179842_b = var2;
   }

   public void func_148837_a(PacketBuffer var1) throws IOException {
      this.field_148919_a = var1.func_179258_d();
      this.field_179842_b = ChatType.func_192582_a(var1.readByte());
   }

   public void func_148840_b(PacketBuffer var1) throws IOException {
      var1.func_179256_a(this.field_148919_a);
      var1.writeByte(this.field_179842_b.func_192583_a());
   }

   public void func_148833_a(INetHandlerPlayClient var1) {
      var1.func_147251_a(this);
   }

   public ITextComponent func_148915_c() {
      return this.field_148919_a;
   }

   public boolean func_148916_d() {
      return this.field_179842_b == ChatType.SYSTEM || this.field_179842_b == ChatType.GAME_INFO;
   }

   public ChatType func_192590_c() {
      return this.field_179842_b;
   }

   public boolean func_211402_a() {
      return true;
   }
}
