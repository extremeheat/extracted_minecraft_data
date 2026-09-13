package net.minecraft.network.play.server;

import net.minecraft.network.Packet;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.play.INetHandlerPlayClient;
import net.minecraft.util.IChatComponent;
import net.minecraft.util.IChatComponent$Serializer;

public class S02PacketChat extends Packet {
   private IChatComponent field_148919_a;
   private boolean field_148918_b = true;

   public S02PacketChat() {
      super();
   }

   public S02PacketChat(IChatComponent var1) {
      this(var1, true);
   }

   public S02PacketChat(IChatComponent var1, boolean var2) {
      super();
      this.field_148919_a = var1;
      this.field_148918_b = var2;
   }

   @Override
   public void func_148837_a(PacketBuffer var1) {
      this.field_148919_a = IChatComponent$Serializer.func_150699_a(var1.func_150789_c(32767));
   }

   @Override
   public void func_148840_b(PacketBuffer var1) {
      var1.func_150785_a(IChatComponent$Serializer.func_150696_a(this.field_148919_a));
   }

   public void func_148833_a(INetHandlerPlayClient var1) {
      var1.func_147251_a(this);
   }

   @Override
   public String func_148835_b() {
      return String.format("message='%s'", this.field_148919_a);
   }

   public IChatComponent func_148915_c() {
      return this.field_148919_a;
   }

   public boolean func_148916_d() {
      return this.field_148918_b;
   }
}
