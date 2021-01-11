package net.minecraft.network.play.client;

import java.io.IOException;
import net.minecraft.network.Packet;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.play.INetHandlerPlayServer;

public class C19PacketResourcePackStatus implements Packet<INetHandlerPlayServer> {
   private String field_179720_a;
   private C19PacketResourcePackStatus.Action field_179719_b;

   public C19PacketResourcePackStatus() {
      super();
   }

   public C19PacketResourcePackStatus(String var1, C19PacketResourcePackStatus.Action var2) {
      super();
      if (var1.length() > 40) {
         var1 = var1.substring(0, 40);
      }

      this.field_179720_a = var1;
      this.field_179719_b = var2;
   }

   public void func_148837_a(PacketBuffer var1) throws IOException {
      this.field_179720_a = var1.func_150789_c(40);
      this.field_179719_b = (C19PacketResourcePackStatus.Action)var1.func_179257_a(C19PacketResourcePackStatus.Action.class);
   }

   public void func_148840_b(PacketBuffer var1) throws IOException {
      var1.func_180714_a(this.field_179720_a);
      var1.func_179249_a(this.field_179719_b);
   }

   public void func_148833_a(INetHandlerPlayServer var1) {
      var1.func_175086_a(this);
   }

   public static enum Action {
      SUCCESSFULLY_LOADED,
      DECLINED,
      FAILED_DOWNLOAD,
      ACCEPTED;

      private Action() {
      }
   }
}
