package net.minecraft.client.network;

import net.minecraft.network.EnumConnectionState;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.handshake.INetHandlerHandshakeServer;
import net.minecraft.network.handshake.client.C00Handshake;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.NetHandlerLoginServer;
import net.minecraft.util.IChatComponent;
import org.apache.commons.lang3.Validate;

public class NetHandlerHandshakeMemory implements INetHandlerHandshakeServer {
   private final MinecraftServer field_147385_a;
   private final NetworkManager field_147384_b;

   public NetHandlerHandshakeMemory(MinecraftServer var1, NetworkManager var2) {
      super();
      this.field_147385_a = var1;
      this.field_147384_b = var2;
   }

   @Override
   public void func_147383_a(C00Handshake var1) {
      this.field_147384_b.func_150723_a(var1.func_149594_c());
   }

   @Override
   public void func_147231_a(IChatComponent var1) {
   }

   @Override
   public void func_147232_a(EnumConnectionState var1, EnumConnectionState var2) {
      Validate.validState(var2 == EnumConnectionState.LOGIN || var2 == EnumConnectionState.STATUS, "Unexpected protocol " + var2, new Object[0]);
      switch(var2) {
         case LOGIN:
            this.field_147384_b.func_150719_a(new NetHandlerLoginServer(this.field_147385_a, this.field_147384_b));
         default:
            return;
         case STATUS:
            throw new UnsupportedOperationException("NYI");
      }
   }

   @Override
   public void func_147233_a() {
   }
}
