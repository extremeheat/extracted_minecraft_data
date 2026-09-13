package net.minecraft.command.server;

import net.minecraft.command.CommandBase;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.NetworkStatistics$PacketStat;
import net.minecraft.util.ChatComponentText;

public class CommandNetstat extends CommandBase {
   public CommandNetstat() {
      super();
   }

   @Override
   public String func_71517_b() {
      return "netstat";
   }

   @Override
   public int func_82362_a() {
      return 0;
   }

   @Override
   public String func_71518_a(ICommandSender var1) {
      return "commands.players.usage";
   }

   @Override
   public void func_71515_b(ICommandSender var1, String[] var2) {
      if (var1 instanceof EntityPlayer) {
         var1.func_145747_a(new ChatComponentText("Command is not available for players"));
      } else {
         if (var2.length <= 0 || var2[0].length() <= 1) {
            String var8 = "reads: " + NetworkManager.field_152462_h.func_152465_a();
            var8 = var8 + ", writes: " + NetworkManager.field_152462_h.func_152471_b();
            var1.func_145747_a(new ChatComponentText(var8));
         } else if ("hottest-read".equals(var2[0])) {
            var1.func_145747_a(new ChatComponentText(NetworkManager.field_152462_h.func_152477_e().toString()));
         } else if ("hottest-write".equals(var2[0])) {
            var1.func_145747_a(new ChatComponentText(NetworkManager.field_152462_h.func_152475_g().toString()));
         } else if ("most-read".equals(var2[0])) {
            var1.func_145747_a(new ChatComponentText(NetworkManager.field_152462_h.func_152467_f().toString()));
         } else if ("most-write".equals(var2[0])) {
            var1.func_145747_a(new ChatComponentText(NetworkManager.field_152462_h.func_152470_h().toString()));
         } else if ("packet-read".equals(var2[0])) {
            if (var2.length > 1 && var2[1].length() > 0) {
               try {
                  int var3 = Integer.parseInt(var2[1].trim());
                  NetworkStatistics$PacketStat var4 = NetworkManager.field_152462_h.func_152466_a(var3);
                  this.func_152375_a(var1, var3, var4);
               } catch (Exception var6) {
                  var1.func_145747_a(new ChatComponentText("Packet " + var2[1] + " not found!"));
               }
            } else {
               var1.func_145747_a(new ChatComponentText("Packet id is missing"));
            }
         } else if ("packet-write".equals(var2[0])) {
            if (var2.length > 1 && var2[1].length() > 0) {
               try {
                  int var7 = Integer.parseInt(var2[1].trim());
                  NetworkStatistics$PacketStat var10 = NetworkManager.field_152462_h.func_152468_b(var7);
                  this.func_152375_a(var1, var7, var10);
               } catch (Exception var5) {
                  var1.func_145747_a(new ChatComponentText("Packet " + var2[1] + " not found!"));
               }
            } else {
               var1.func_145747_a(new ChatComponentText("Packet id is missing"));
            }
         } else if ("read-count".equals(var2[0])) {
            var1.func_145747_a(new ChatComponentText("total-read-count" + String.valueOf(NetworkManager.field_152462_h.func_152472_c())));
         } else if ("write-count".equals(var2[0])) {
            var1.func_145747_a(new ChatComponentText("total-write-count" + String.valueOf(NetworkManager.field_152462_h.func_152473_d())));
         } else {
            var1.func_145747_a(new ChatComponentText("Unrecognized: " + var2[0]));
         }
      }
   }

   private void func_152375_a(ICommandSender var1, int var2, NetworkStatistics$PacketStat var3) {
      if (var3 != null) {
         var1.func_145747_a(new ChatComponentText(var3.toString()));
      } else {
         var1.func_145747_a(new ChatComponentText("Packet " + var2 + " not found!"));
      }
   }
}
