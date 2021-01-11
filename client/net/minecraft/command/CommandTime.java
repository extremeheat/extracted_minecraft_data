package net.minecraft.command;

import java.util.List;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.BlockPos;
import net.minecraft.world.WorldServer;

public class CommandTime extends CommandBase {
   public CommandTime() {
      super();
   }

   public String func_71517_b() {
      return "time";
   }

   public int func_82362_a() {
      return 2;
   }

   public String func_71518_a(ICommandSender var1) {
      return "commands.time.usage";
   }

   public void func_71515_b(ICommandSender var1, String[] var2) throws CommandException {
      if (var2.length > 1) {
         int var3;
         if (var2[0].equals("set")) {
            if (var2[1].equals("day")) {
               var3 = 1000;
            } else if (var2[1].equals("night")) {
               var3 = 13000;
            } else {
               var3 = func_180528_a(var2[1], 0);
            }

            this.func_71552_a(var1, var3);
            func_152373_a(var1, this, "commands.time.set", new Object[]{var3});
            return;
         }

         if (var2[0].equals("add")) {
            var3 = func_180528_a(var2[1], 0);
            this.func_71553_b(var1, var3);
            func_152373_a(var1, this, "commands.time.added", new Object[]{var3});
            return;
         }

         if (var2[0].equals("query")) {
            if (var2[1].equals("daytime")) {
               var3 = (int)(var1.func_130014_f_().func_72820_D() % 2147483647L);
               var1.func_174794_a(CommandResultStats.Type.QUERY_RESULT, var3);
               func_152373_a(var1, this, "commands.time.query", new Object[]{var3});
               return;
            }

            if (var2[1].equals("gametime")) {
               var3 = (int)(var1.func_130014_f_().func_82737_E() % 2147483647L);
               var1.func_174794_a(CommandResultStats.Type.QUERY_RESULT, var3);
               func_152373_a(var1, this, "commands.time.query", new Object[]{var3});
               return;
            }
         }
      }

      throw new WrongUsageException("commands.time.usage", new Object[0]);
   }

   public List<String> func_180525_a(ICommandSender var1, String[] var2, BlockPos var3) {
      if (var2.length == 1) {
         return func_71530_a(var2, new String[]{"set", "add", "query"});
      } else if (var2.length == 2 && var2[0].equals("set")) {
         return func_71530_a(var2, new String[]{"day", "night"});
      } else {
         return var2.length == 2 && var2[0].equals("query") ? func_71530_a(var2, new String[]{"daytime", "gametime"}) : null;
      }
   }

   protected void func_71552_a(ICommandSender var1, int var2) {
      for(int var3 = 0; var3 < MinecraftServer.func_71276_C().field_71305_c.length; ++var3) {
         MinecraftServer.func_71276_C().field_71305_c[var3].func_72877_b((long)var2);
      }

   }

   protected void func_71553_b(ICommandSender var1, int var2) {
      for(int var3 = 0; var3 < MinecraftServer.func_71276_C().field_71305_c.length; ++var3) {
         WorldServer var4 = MinecraftServer.func_71276_C().field_71305_c[var3];
         var4.func_72877_b(var4.func_72820_D() + (long)var2);
      }

   }
}
