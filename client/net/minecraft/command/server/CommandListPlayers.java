package net.minecraft.command.server;

import net.minecraft.command.CommandBase;
import net.minecraft.command.ICommandSender;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.ChatComponentTranslation;

public class CommandListPlayers extends CommandBase {
   public CommandListPlayers() {
      super();
   }

   @Override
   public String func_71517_b() {
      return "list";
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
      var1.func_145747_a(
         new ChatComponentTranslation("commands.players.list", MinecraftServer.func_71276_C().func_71233_x(), MinecraftServer.func_71276_C().func_71275_y())
      );
      var1.func_145747_a(
         new ChatComponentText(MinecraftServer.func_71276_C().func_71203_ab().func_152609_b(var2.length > 0 && "uuids".equalsIgnoreCase(var2[0])))
      );
   }
}
