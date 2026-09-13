package net.minecraft.command.server;

import com.mojang.authlib.GameProfile;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.command.WrongUsageException;
import net.minecraft.server.MinecraftServer;

public class CommandOp extends CommandBase {
   public CommandOp() {
      super();
   }

   @Override
   public String func_71517_b() {
      return "op";
   }

   @Override
   public int func_82362_a() {
      return 3;
   }

   @Override
   public String func_71518_a(ICommandSender var1) {
      return "commands.op.usage";
   }

   @Override
   public void func_71515_b(ICommandSender var1, String[] var2) {
      if (var2.length == 1 && var2[0].length() > 0) {
         MinecraftServer var3 = MinecraftServer.func_71276_C();
         GameProfile var4 = var3.func_152358_ax().func_152655_a(var2[0]);
         if (var4 == null) {
            throw new CommandException("commands.op.failed", var2[0]);
         } else {
            var3.func_71203_ab().func_152605_a(var4);
            func_152373_a(var1, this, "commands.op.success", new Object[]{var2[0]});
         }
      } else {
         throw new WrongUsageException("commands.op.usage");
      }
   }

   @Override
   public List func_71516_a(ICommandSender var1, String[] var2) {
      if (var2.length == 1) {
         String var3 = var2[var2.length - 1];
         ArrayList var4 = new ArrayList();

         for(GameProfile var8 : MinecraftServer.func_71276_C().func_152357_F()) {
            if (!MinecraftServer.func_71276_C().func_71203_ab().func_152596_g(var8) && func_71523_a(var3, var8.getName())) {
               var4.add(var8.getName());
            }
         }

         return var4;
      } else {
         return null;
      }
   }
}
