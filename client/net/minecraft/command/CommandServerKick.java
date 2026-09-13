package net.minecraft.command;

import java.util.List;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.server.MinecraftServer;

public class CommandServerKick extends CommandBase {
   public CommandServerKick() {
      super();
   }

   @Override
   public String func_71517_b() {
      return "kick";
   }

   @Override
   public int func_82362_a() {
      return 2;
   }

   @Override
   public String func_71518_a(ICommandSender var1) {
      return "commands.kick.usage";
   }

   @Override
   public void func_71515_b(ICommandSender var1, String[] var2) {
      if (var2.length > 0 && var2[0].length() > 1) {
         EntityPlayerMP var3 = MinecraftServer.func_71276_C().func_71203_ab().func_152612_a(var2[0]);
         String var4 = "Kicked by an operator.";
         boolean var5 = false;
         if (var3 == null) {
            throw new PlayerNotFoundException();
         } else {
            if (var2.length >= 2) {
               var4 = func_147178_a(var1, var2, 1).func_150260_c();
               var5 = true;
            }

            var3.field_71135_a.func_147360_c(var4);
            if (var5) {
               func_152373_a(var1, this, "commands.kick.success.reason", new Object[]{var3.func_70005_c_(), var4});
            } else {
               func_152373_a(var1, this, "commands.kick.success", new Object[]{var3.func_70005_c_()});
            }
         }
      } else {
         throw new WrongUsageException("commands.kick.usage");
      }
   }

   @Override
   public List func_71516_a(ICommandSender var1, String[] var2) {
      return var2.length >= 1 ? func_71530_a(var2, MinecraftServer.func_71276_C().func_71213_z()) : null;
   }
}
