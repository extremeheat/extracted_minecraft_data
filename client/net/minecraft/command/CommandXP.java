package net.minecraft.command;

import java.util.List;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.server.MinecraftServer;

public class CommandXP extends CommandBase {
   public CommandXP() {
      super();
   }

   @Override
   public String func_71517_b() {
      return "xp";
   }

   @Override
   public int func_82362_a() {
      return 2;
   }

   @Override
   public String func_71518_a(ICommandSender var1) {
      return "commands.xp.usage";
   }

   @Override
   public void func_71515_b(ICommandSender var1, String[] var2) {
      if (var2.length <= 0) {
         throw new WrongUsageException("commands.xp.usage");
      } else {
         String var4 = var2[0];
         boolean var5 = var4.endsWith("l") || var4.endsWith("L");
         if (var5 && var4.length() > 1) {
            var4 = var4.substring(0, var4.length() - 1);
         }

         int var6 = func_71526_a(var1, var4);
         boolean var7 = var6 < 0;
         if (var7) {
            var6 *= -1;
         }

         EntityPlayerMP var3;
         if (var2.length > 1) {
            var3 = func_82359_c(var1, var2[1]);
         } else {
            var3 = func_71521_c(var1);
         }

         if (var5) {
            if (var7) {
               var3.func_82242_a(-var6);
               func_152373_a(var1, this, "commands.xp.success.negative.levels", new Object[]{var6, var3.func_70005_c_()});
            } else {
               var3.func_82242_a(var6);
               func_152373_a(var1, this, "commands.xp.success.levels", new Object[]{var6, var3.func_70005_c_()});
            }
         } else {
            if (var7) {
               throw new WrongUsageException("commands.xp.failure.widthdrawXp");
            }

            var3.func_71023_q(var6);
            func_152373_a(var1, this, "commands.xp.success", new Object[]{var6, var3.func_70005_c_()});
         }
      }
   }

   @Override
   public List func_71516_a(ICommandSender var1, String[] var2) {
      return var2.length == 2 ? func_71530_a(var2, this.func_71542_c()) : null;
   }

   protected String[] func_71542_c() {
      return MinecraftServer.func_71276_C().func_71213_z();
   }

   @Override
   public boolean func_82358_a(String[] var1, int var2) {
      return var2 == 1;
   }
}
