package net.minecraft.command.server;

import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.WorldSettings;

public class CommandPublishLocalServer extends CommandBase {
   public CommandPublishLocalServer() {
      super();
   }

   public String func_71517_b() {
      return "publish";
   }

   public String func_71518_a(ICommandSender var1) {
      return "commands.publish.usage";
   }

   public void func_71515_b(ICommandSender var1, String[] var2) throws CommandException {
      String var3 = MinecraftServer.func_71276_C().func_71206_a(WorldSettings.GameType.SURVIVAL, false);
      if (var3 != null) {
         func_152373_a(var1, this, "commands.publish.started", new Object[]{var3});
      } else {
         func_152373_a(var1, this, "commands.publish.failed", new Object[0]);
      }

   }
}
