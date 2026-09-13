package net.minecraft.client.network;

import com.mojang.authlib.GameProfile;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ServerData;
import net.minecraft.network.EnumConnectionState;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.ServerStatusResponse;
import net.minecraft.network.status.INetHandlerStatusClient;
import net.minecraft.network.status.client.C01PacketPing;
import net.minecraft.network.status.server.S00PacketServerInfo;
import net.minecraft.network.status.server.S01PacketPong;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.IChatComponent;
import org.apache.commons.lang3.ArrayUtils;

class OldServerPinger$1 implements INetHandlerStatusClient {
   private boolean field_147403_d;

   OldServerPinger$1(OldServerPinger var1, ServerData var2, NetworkManager var3) {
      super();
      this.field_147405_c = var1;
      this.field_147406_a = var2;
      this.field_147404_b = var3;
      this.field_147403_d = false;
   }

   @Override
   public void func_147397_a(S00PacketServerInfo var1) {
      ServerStatusResponse var2 = var1.func_149294_c();
      if (var2.func_151317_a() != null) {
         this.field_147406_a.field_78843_d = var2.func_151317_a().func_150254_d();
      } else {
         this.field_147406_a.field_78843_d = "";
      }

      if (var2.func_151322_c() != null) {
         this.field_147406_a.field_82822_g = var2.func_151322_c().func_151303_a();
         this.field_147406_a.field_82821_f = var2.func_151322_c().func_151304_b();
      } else {
         this.field_147406_a.field_82822_g = "Old";
         this.field_147406_a.field_82821_f = 0;
      }

      if (var2.func_151318_b() != null) {
         this.field_147406_a.field_78846_c = EnumChatFormatting.GRAY
            + ""
            + var2.func_151318_b().func_151333_b()
            + ""
            + EnumChatFormatting.DARK_GRAY
            + "/"
            + EnumChatFormatting.GRAY
            + var2.func_151318_b().func_151332_a();
         if (ArrayUtils.isNotEmpty(var2.func_151318_b().func_151331_c())) {
            StringBuilder var3 = new StringBuilder();

            for(GameProfile var7 : var2.func_151318_b().func_151331_c()) {
               if (var3.length() > 0) {
                  var3.append("\n");
               }

               var3.append(var7.getName());
            }

            if (var2.func_151318_b().func_151331_c().length < var2.func_151318_b().func_151333_b()) {
               if (var3.length() > 0) {
                  var3.append("\n");
               }

               var3.append("... and ").append(var2.func_151318_b().func_151333_b() - var2.func_151318_b().func_151331_c().length).append(" more ...");
            }

            this.field_147406_a.field_147412_i = var3.toString();
         }
      } else {
         this.field_147406_a.field_78846_c = EnumChatFormatting.DARK_GRAY + "???";
      }

      if (var2.func_151316_d() != null) {
         String var8 = var2.func_151316_d();
         if (var8.startsWith("data:image/png;base64,")) {
            this.field_147406_a.func_147407_a(var8.substring("data:image/png;base64,".length()));
         } else {
            OldServerPinger.access$000().error("Invalid server icon (unknown format)");
         }
      } else {
         this.field_147406_a.func_147407_a(null);
      }

      this.field_147404_b.func_150725_a(new C01PacketPing(Minecraft.func_71386_F()));
      this.field_147403_d = true;
   }

   @Override
   public void func_147398_a(S01PacketPong var1) {
      long var2 = var1.func_149292_c();
      long var4 = Minecraft.func_71386_F();
      this.field_147406_a.field_78844_e = var4 - var2;
      this.field_147404_b.func_150718_a(new ChatComponentText("Finished"));
   }

   @Override
   public void func_147231_a(IChatComponent var1) {
      if (!this.field_147403_d) {
         OldServerPinger.access$000().error("Can't ping " + this.field_147406_a.field_78845_b + ": " + var1.func_150260_c());
         this.field_147406_a.field_78843_d = EnumChatFormatting.DARK_RED + "Can't connect to server.";
         this.field_147406_a.field_78846_c = "";
         OldServerPinger.access$100(this.field_147405_c, this.field_147406_a);
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
