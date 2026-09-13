package net.minecraft.network.login.server;

import com.mojang.authlib.GameProfile;
import java.util.UUID;
import net.minecraft.network.Packet;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.login.INetHandlerLoginClient;

public class S02PacketLoginSuccess extends Packet {
   private GameProfile field_149602_a;

   public S02PacketLoginSuccess() {
      super();
   }

   public S02PacketLoginSuccess(GameProfile var1) {
      super();
      this.field_149602_a = var1;
   }

   @Override
   public void func_148837_a(PacketBuffer var1) {
      String var2 = var1.func_150789_c(36);
      String var3 = var1.func_150789_c(16);
      UUID var4 = UUID.fromString(var2);
      this.field_149602_a = new GameProfile(var4, var3);
   }

   @Override
   public void func_148840_b(PacketBuffer var1) {
      UUID var2 = this.field_149602_a.getId();
      var1.func_150785_a(var2 == null ? "" : var2.toString());
      var1.func_150785_a(this.field_149602_a.getName());
   }

   public void func_148833_a(INetHandlerLoginClient var1) {
      var1.func_147390_a(this);
   }

   @Override
   public boolean func_148836_a() {
      return true;
   }
}
