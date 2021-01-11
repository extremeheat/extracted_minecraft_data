package net.minecraft.command.server;

import com.mojang.authlib.GameProfile;
import java.util.List;
import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.command.WrongUsageException;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.BlockPos;

public class CommandDeOp extends CommandBase {
   public CommandDeOp() {
      super();
   }

   public String func_71517_b() {
      return "deop";
   }

   public int func_82362_a() {
      return 3;
   }

   public String func_71518_a(ICommandSender var1) {
      return "commands.deop.usage";
   }

   public void func_71515_b(ICommandSender var1, String[] var2) throws CommandException {
      if (var2.length == 1 && var2[0].length() > 0) {
         MinecraftServer var3 = MinecraftServer.func_71276_C();
         GameProfile var4 = var3.func_71203_ab().func_152603_m().func_152700_a(var2[0]);
         if (var4 == null) {
            throw new CommandException("commands.deop.failed", new Object[]{var2[0]});
         } else {
            var3.func_71203_ab().func_152610_b(var4);
            func_152373_a(var1, this, "commands.deop.success", new Object[]{var2[0]});
         }
      } else {
         throw new WrongUsageException("commands.deop.usage", new Object[0]);
      }
   }

   public List<String> func_180525_a(ICommandSender var1, String[] var2, BlockPos var3) {
      return var2.length == 1 ? func_71530_a(var2, MinecraftServer.func_71276_C().func_71203_ab().func_152606_n()) : null;
   }
}
