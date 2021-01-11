package net.minecraft.network.play.server;

import java.io.IOException;
import net.minecraft.network.Packet;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.play.INetHandlerPlayClient;

public class S48PacketResourcePackSend implements Packet<INetHandlerPlayClient> {
   private String field_179786_a;
   private String field_179785_b;

   public S48PacketResourcePackSend() {
      super();
   }

   public S48PacketResourcePackSend(String var1, String var2) {
      super();
      this.field_179786_a = var1;
      this.field_179785_b = var2;
      if (var2.length() > 40) {
         throw new IllegalArgumentException("Hash is too long (max 40, was " + var2.length() + ")");
      }
   }

   public void func_148837_a(PacketBuffer var1) throws IOException {
      this.field_179786_a = var1.func_150789_c(32767);
      this.field_179785_b = var1.func_150789_c(40);
   }

   public void func_148840_b(PacketBuffer var1) throws IOException {
      var1.func_180714_a(this.field_179786_a);
      var1.func_180714_a(this.field_179785_b);
   }

   public void func_148833_a(INetHandlerPlayClient var1) {
      var1.func_175095_a(this);
   }

   public String func_179783_a() {
      return this.field_179786_a;
   }

   public String func_179784_b() {
      return this.field_179785_b;
   }
}
