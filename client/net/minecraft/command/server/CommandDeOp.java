package net.minecraft.command.server;

import com.mojang.authlib.GameProfile;
import java.util.List;
import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.command.WrongUsageException;
import net.minecraft.server.MinecraftServer;

public class CommandDeOp extends CommandBase {
   public CommandDeOp() {
      super();
   }

   @Override
   public String func_71517_b() {
      return "deop";
   }

   @Override
   public int func_82362_a() {
      return 3;
   }

   @Override
   public String func_71518_a(ICommandSender var1) {
      return "commands.deop.usage";
   }

   @Override
   public void func_71515_b(ICommandSender var1, String[] var2) {
      if (var2.length == 1 && var2[0].length() > 0) {
         MinecraftServer var3 = MinecraftServer.func_71276_C();
         GameProfile var4 = var3.func_71203_ab().func_152603_m().func_152700_a(var2[0]);
         if (var4 == null) {
            throw new CommandException("commands.deop.failed", var2[0]);
         } else {
            var3.func_71203_ab().func_152610_b(var4);
            func_152373_a(var1, this, "commands.deop.success", new Object[]{var2[0]});
         }
      } else {
         throw new WrongUsageException("commands.deop.usage");
      }
   }

   @Override
   public List func_71516_a(ICommandSender var1, String[] var2) {
      return var2.length == 1 ? func_71530_a(var2, MinecraftServer.func_71276_C().func_71203_ab().func_152606_n()) : null;
   }
}
