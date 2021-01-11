package net.minecraft.command;

import net.minecraft.server.MinecraftServer;

public class CommandSetPlayerTimeout extends CommandBase {
   public CommandSetPlayerTimeout() {
      super();
   }

   public String func_71517_b() {
      return "setidletimeout";
   }

   public int func_82362_a() {
      return 3;
   }

   public String func_71518_a(ICommandSender var1) {
      return "commands.setidletimeout.usage";
   }

   public void func_71515_b(ICommandSender var1, String[] var2) throws CommandException {
      if (var2.length != 1) {
         throw new WrongUsageException("commands.setidletimeout.usage", new Object[0]);
      } else {
         int var3 = func_180528_a(var2[0], 0);
         MinecraftServer.func_71276_C().func_143006_e(var3);
         func_152373_a(var1, this, "commands.setidletimeout.success", new Object[]{var3});
      }
   }
}
