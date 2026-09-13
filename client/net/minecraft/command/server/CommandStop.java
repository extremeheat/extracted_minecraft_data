package net.minecraft.command.server;

import net.minecraft.command.CommandBase;
import net.minecraft.command.ICommandSender;
import net.minecraft.server.MinecraftServer;

public class CommandStop extends CommandBase {
   public CommandStop() {
      super();
   }

   @Override
   public String func_71517_b() {
      return "stop";
   }

   @Override
   public String func_71518_a(ICommandSender var1) {
      return "commands.stop.usage";
   }

   @Override
   public void func_71515_b(ICommandSender var1, String[] var2) {
      if (MinecraftServer.func_71276_C().field_71305_c != null) {
         func_152373_a(var1, this, "commands.stop.start", new Object[0]);
      }

      MinecraftServer.func_71276_C().func_71263_m();
   }
}
