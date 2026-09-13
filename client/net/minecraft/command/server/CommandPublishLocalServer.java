package net.minecraft.command.server;

import net.minecraft.command.CommandBase;
import net.minecraft.command.ICommandSender;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.WorldSettings$GameType;

public class CommandPublishLocalServer extends CommandBase {
   public CommandPublishLocalServer() {
      super();
   }

   @Override
   public String func_71517_b() {
      return "publish";
   }

   @Override
   public String func_71518_a(ICommandSender var1) {
      return "commands.publish.usage";
   }

   @Override
   public void func_71515_b(ICommandSender var1, String[] var2) {
      String var3 = MinecraftServer.func_71276_C().func_71206_a(WorldSettings$GameType.SURVIVAL, false);
      if (var3 != null) {
         func_152373_a(var1, this, "commands.publish.started", new Object[]{var3});
      } else {
         func_152373_a(var1, this, "commands.publish.failed", new Object[0]);
      }
   }
}
