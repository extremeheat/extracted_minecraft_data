package net.minecraft.command;

import java.util.List;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.BlockPos;

public class CommandXP extends CommandBase {
   public CommandXP() {
      super();
   }

   public String func_71517_b() {
      return "xp";
   }

   public int func_82362_a() {
      return 2;
   }

   public String func_71518_a(ICommandSender var1) {
      return "commands.xp.usage";
   }

   public void func_71515_b(ICommandSender var1, String[] var2) throws CommandException {
      if (var2.length <= 0) {
         throw new WrongUsageException("commands.xp.usage", new Object[0]);
      } else {
         String var3 = var2[0];
         boolean var4 = var3.endsWith("l") || var3.endsWith("L");
         if (var4 && var3.length() > 1) {
            var3 = var3.substring(0, var3.length() - 1);
         }

         int var5 = func_175755_a(var3);
         boolean var6 = var5 < 0;
         if (var6) {
            var5 *= -1;
         }

         EntityPlayerMP var7 = var2.length > 1 ? func_82359_c(var1, var2[1]) : func_71521_c(var1);
         if (var4) {
            var1.func_174794_a(CommandResultStats.Type.QUERY_RESULT, var7.field_71068_ca);
            if (var6) {
               var7.func_82242_a(-var5);
               func_152373_a(var1, this, "commands.xp.success.negative.levels", new Object[]{var5, var7.func_70005_c_()});
            } else {
               var7.func_82242_a(var5);
               func_152373_a(var1, this, "commands.xp.success.levels", new Object[]{var5, var7.func_70005_c_()});
            }
         } else {
            var1.func_174794_a(CommandResultStats.Type.QUERY_RESULT, var7.field_71067_cb);
            if (var6) {
               throw new CommandException("commands.xp.failure.widthdrawXp", new Object[0]);
            }

            var7.func_71023_q(var5);
            func_152373_a(var1, this, "commands.xp.success", new Object[]{var5, var7.func_70005_c_()});
         }

      }
   }

   public List<String> func_180525_a(ICommandSender var1, String[] var2, BlockPos var3) {
      return var2.length == 2 ? func_71530_a(var2, this.func_71542_c()) : null;
   }

   protected String[] func_71542_c() {
      return MinecraftServer.func_71276_C().func_71213_z();
   }

   public boolean func_82358_a(String[] var1, int var2) {
      return var2 == 1;
   }
}
