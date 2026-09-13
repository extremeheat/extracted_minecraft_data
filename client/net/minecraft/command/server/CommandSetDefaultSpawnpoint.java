package net.minecraft.command.server;

import net.minecraft.command.CommandBase;
import net.minecraft.command.ICommandSender;
import net.minecraft.command.WrongUsageException;
import net.minecraft.util.ChunkCoordinates;

public class CommandSetDefaultSpawnpoint extends CommandBase {
   public CommandSetDefaultSpawnpoint() {
      super();
   }

   @Override
   public String func_71517_b() {
      return "setworldspawn";
   }

   @Override
   public int func_82362_a() {
      return 2;
   }

   @Override
   public String func_71518_a(ICommandSender var1) {
      return "commands.setworldspawn.usage";
   }

   @Override
   public void func_71515_b(ICommandSender var1, String[] var2) {
      if (var2.length == 3) {
         if (var1.func_130014_f_() == null) {
            throw new WrongUsageException("commands.setworldspawn.usage");
         }

         int var3 = 0;
         int var4 = func_71532_a(var1, var2[var3++], -30000000, 30000000);
         int var5 = func_71532_a(var1, var2[var3++], 0, 256);
         int var6 = func_71532_a(var1, var2[var3++], -30000000, 30000000);
         var1.func_130014_f_().func_72950_A(var4, var5, var6);
         func_152373_a(var1, this, "commands.setworldspawn.success", new Object[]{var4, var5, var6});
      } else {
         if (var2.length != 0) {
            throw new WrongUsageException("commands.setworldspawn.usage");
         }

         ChunkCoordinates var10 = func_71521_c(var1).func_82114_b();
         var1.func_130014_f_().func_72950_A(var10.field_71574_a, var10.field_71572_b, var10.field_71573_c);
         func_152373_a(var1, this, "commands.setworldspawn.success", new Object[]{var10.field_71574_a, var10.field_71572_b, var10.field_71573_c});
      }
   }
}
