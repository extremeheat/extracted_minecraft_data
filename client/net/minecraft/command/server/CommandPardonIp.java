package net.minecraft.command.server;

import java.util.List;
import java.util.regex.Matcher;
import net.minecraft.command.CommandBase;
import net.minecraft.command.ICommandSender;
import net.minecraft.command.SyntaxErrorException;
import net.minecraft.command.WrongUsageException;
import net.minecraft.server.MinecraftServer;

public class CommandPardonIp extends CommandBase {
   public CommandPardonIp() {
      super();
   }

   @Override
   public String func_71517_b() {
      return "pardon-ip";
   }

   @Override
   public int func_82362_a() {
      return 3;
   }

   @Override
   public boolean func_71519_b(ICommandSender var1) {
      return MinecraftServer.func_71276_C().func_71203_ab().func_72363_f().func_152689_b() && super.func_71519_b(var1);
   }

   @Override
   public String func_71518_a(ICommandSender var1) {
      return "commands.unbanip.usage";
   }

   @Override
   public void func_71515_b(ICommandSender var1, String[] var2) {
      if (var2.length == 1 && var2[0].length() > 1) {
         Matcher var3 = CommandBanIp.field_147211_a.matcher(var2[0]);
         if (var3.matches()) {
            MinecraftServer.func_71276_C().func_71203_ab().func_72363_f().func_152684_c(var2[0]);
            func_152373_a(var1, this, "commands.unbanip.success", new Object[]{var2[0]});
         } else {
            throw new SyntaxErrorException("commands.unbanip.invalid");
         }
      } else {
         throw new WrongUsageException("commands.unbanip.usage");
      }
   }

   @Override
   public List func_71516_a(ICommandSender var1, String[] var2) {
      return var2.length == 1 ? func_71530_a(var2, MinecraftServer.func_71276_C().func_71203_ab().func_72363_f().func_152685_a()) : null;
   }
}
