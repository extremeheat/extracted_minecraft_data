package net.minecraft.command;

import java.util.List;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.WorldServer;

public class CommandTime extends CommandBase {
   public CommandTime() {
      super();
   }

   @Override
   public String func_71517_b() {
      return "time";
   }

   @Override
   public int func_82362_a() {
      return 2;
   }

   @Override
   public String func_71518_a(ICommandSender var1) {
      return "commands.time.usage";
   }

   @Override
   public void func_71515_b(ICommandSender var1, String[] var2) {
      if (var2.length > 1) {
         if (var2[0].equals("set")) {
            int var4;
            if (var2[1].equals("day")) {
               var4 = 1000;
            } else if (var2[1].equals("night")) {
               var4 = 13000;
            } else {
               var4 = func_71528_a(var1, var2[1], 0);
            }

            this.func_71552_a(var1, var4);
            func_152373_a(var1, this, "commands.time.set", new Object[]{var4});
            return;
         }

         if (var2[0].equals("add")) {
            int var3 = func_71528_a(var1, var2[1], 0);
            this.func_71553_b(var1, var3);
            func_152373_a(var1, this, "commands.time.added", new Object[]{var3});
            return;
         }
      }

      throw new WrongUsageException("commands.time.usage");
   }

   @Override
   public List func_71516_a(ICommandSender var1, String[] var2) {
      if (var2.length == 1) {
         return func_71530_a(var2, new String[]{"set", "add"});
      } else {
         return var2.length == 2 && var2[0].equals("set") ? func_71530_a(var2, new String[]{"day", "night"}) : null;
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
