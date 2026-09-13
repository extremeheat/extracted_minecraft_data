package net.minecraft.network.login.client;

import com.mojang.authlib.GameProfile;
import net.minecraft.network.Packet;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.login.INetHandlerLoginServer;

public class C00PacketLoginStart extends Packet {
   private GameProfile field_149305_a;

   public C00PacketLoginStart() {
      super();
   }

   public C00PacketLoginStart(GameProfile var1) {
      super();
      this.field_149305_a = var1;
   }

   @Override
   public void func_148837_a(PacketBuffer var1) {
      this.field_149305_a = new GameProfile(null, var1.func_150789_c(16));
   }

   @Override
   public void func_148840_b(PacketBuffer var1) {
      var1.func_150785_a(this.field_149305_a.getName());
   }

   public void func_148833_a(INetHandlerLoginServer var1) {
      var1.func_147316_a(this);
   }

   public GameProfile func_149304_c() {
      return this.field_149305_a;
   }
}
