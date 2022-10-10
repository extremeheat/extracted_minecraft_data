package net.minecraft.network.login.server;

import com.mojang.authlib.GameProfile;
import java.io.IOException;
import java.util.UUID;
import net.minecraft.network.Packet;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.login.INetHandlerLoginClient;

public class SPacketLoginSuccess implements Packet<INetHandlerLoginClient> {
   private GameProfile field_149602_a;

   public SPacketLoginSuccess() {
      super();
   }

   public SPacketLoginSuccess(GameProfile var1) {
      super();
      this.field_149602_a = var1;
   }

   public void func_148837_a(PacketBuffer var1) throws IOException {
      String var2 = var1.func_150789_c(36);
      String var3 = var1.func_150789_c(16);
      UUID var4 = UUID.fromString(var2);
      this.field_149602_a = new GameProfile(var4, var3);
   }

   public void func_148840_b(PacketBuffer var1) throws IOException {
      UUID var2 = this.field_149602_a.getId();
      var1.func_180714_a(var2 == null ? "" : var2.toString());
      var1.func_180714_a(this.field_149602_a.getName());
   }

   public void func_148833_a(INetHandlerLoginClient var1) {
      var1.func_147390_a(this);
   }

   public GameProfile func_179730_a() {
      return this.field_149602_a;
   }
}
