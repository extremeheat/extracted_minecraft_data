package net.minecraft.realms;

import net.minecraft.network.EnumConnectionState;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.ServerStatusResponse;
import net.minecraft.network.status.INetHandlerStatusClient;
import net.minecraft.network.status.client.C01PacketPing;
import net.minecraft.network.status.server.S00PacketServerInfo;
import net.minecraft.network.status.server.S01PacketPong;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.IChatComponent;

public class RealmsServerStatusPinger$1 implements INetHandlerStatusClient {
   private boolean field_154345_e;

   public RealmsServerStatusPinger$1(RealmsServerStatusPinger var1, ServerPing var2, NetworkManager var3, String var4) {
      super();
      this.field_154344_d = var1;
      this.field_154341_a = var2;
      this.field_154342_b = var3;
      this.field_154343_c = var4;
      this.field_154345_e = false;
   }

   @Override
   public void func_147397_a(S00PacketServerInfo var1) {
      ServerStatusResponse var2 = var1.func_149294_c();
      if (var2.func_151318_b() != null) {
         this.field_154341_a.nrOfPlayers = String.valueOf(var2.func_151318_b().func_151333_b());
      }

      this.field_154342_b.func_150725_a(new C01PacketPing(Realms.currentTimeMillis()));
      this.field_154345_e = true;
   }

   @Override
   public void func_147398_a(S01PacketPong var1) {
      this.field_154342_b.func_150718_a(new ChatComponentText("Finished"));
   }

   @Override
   public void func_147231_a(IChatComponent var1) {
      if (!this.field_154345_e) {
         RealmsServerStatusPinger.access$000().error("Can't ping " + this.field_154343_c + ": " + var1.func_150260_c());
      }
   }

   @Override
   public void func_147232_a(EnumConnectionState var1, EnumConnectionState var2) {
      if (var2 != EnumConnectionState.STATUS) {
         throw new UnsupportedOperationException("Unexpected change in protocol to " + var2);
      }
   }

   @Override
   public void func_147233_a() {
   }
}
