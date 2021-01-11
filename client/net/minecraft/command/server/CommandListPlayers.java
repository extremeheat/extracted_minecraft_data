package net.minecraft.command.server;

import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.CommandResultStats;
import net.minecraft.command.ICommandSender;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.ChatComponentTranslation;

public class CommandListPlayers extends CommandBase {
   public CommandListPlayers() {
      super();
   }

   public String func_71517_b() {
      return "list";
   }

   public int func_82362_a() {
      return 0;
   }

   public String func_71518_a(ICommandSender var1) {
      return "commands.players.usage";
   }

   public void func_71515_b(ICommandSender var1, String[] var2) throws CommandException {
      int var3 = MinecraftServer.func_71276_C().func_71233_x();
      var1.func_145747_a(new ChatComponentTranslation("commands.players.list", new Object[]{var3, MinecraftServer.func_71276_C().func_71275_y()}));
      var1.func_145747_a(new ChatComponentText(MinecraftServer.func_71276_C().func_71203_ab().func_181058_b(var2.length > 0 && "uuids".equalsIgnoreCase(var2[0]))));
      var1.func_174794_a(CommandResultStats.Type.QUERY_RESULT, var3);
   }
}
