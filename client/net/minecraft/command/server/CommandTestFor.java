package net.minecraft.command.server;

import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.command.WrongUsageException;

public class CommandTestFor extends CommandBase {
   public CommandTestFor() {
      super();
   }

   @Override
   public String func_71517_b() {
      return "testfor";
   }

   @Override
   public int func_82362_a() {
      return 2;
   }

   @Override
   public String func_71518_a(ICommandSender var1) {
      return "commands.testfor.usage";
   }

   @Override
   public void func_71515_b(ICommandSender var1, String[] var2) {
      if (var2.length != 1) {
         throw new WrongUsageException("commands.testfor.usage");
      } else if (!(var1 instanceof CommandBlockLogic)) {
         throw new CommandException("commands.testfor.failed");
      } else {
         func_82359_c(var1, var2[0]);
      }
   }

   @Override
   public boolean func_82358_a(String[] var1, int var2) {
      return var2 == 0;
   }
}
