package net.minecraft.command;

import java.util.List;
import java.util.Random;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.BlockPos;
import net.minecraft.world.WorldServer;
import net.minecraft.world.storage.WorldInfo;

public class CommandWeather extends CommandBase {
   public CommandWeather() {
      super();
   }

   public String func_71517_b() {
      return "weather";
   }

   public int func_82362_a() {
      return 2;
   }

   public String func_71518_a(ICommandSender var1) {
      return "commands.weather.usage";
   }

   public void func_71515_b(ICommandSender var1, String[] var2) throws CommandException {
      if (var2.length >= 1 && var2.length <= 2) {
         int var3 = (300 + (new Random()).nextInt(600)) * 20;
         if (var2.length >= 2) {
            var3 = func_175764_a(var2[1], 1, 1000000) * 20;
         }

         WorldServer var4 = MinecraftServer.func_71276_C().field_71305_c[0];
         WorldInfo var5 = var4.func_72912_H();
         if ("clear".equalsIgnoreCase(var2[0])) {
            var5.func_176142_i(var3);
            var5.func_76080_g(0);
            var5.func_76090_f(0);
            var5.func_76084_b(false);
            var5.func_76069_a(false);
            func_152373_a(var1, this, "commands.weather.clear", new Object[0]);
         } else if ("rain".equalsIgnoreCase(var2[0])) {
            var5.func_176142_i(0);
            var5.func_76080_g(var3);
            var5.func_76090_f(var3);
            var5.func_76084_b(true);
            var5.func_76069_a(false);
            func_152373_a(var1, this, "commands.weather.rain", new Object[0]);
         } else {
            if (!"thunder".equalsIgnoreCase(var2[0])) {
               throw new WrongUsageException("commands.weather.usage", new Object[0]);
            }

            var5.func_176142_i(0);
            var5.func_76080_g(var3);
            var5.func_76090_f(var3);
            var5.func_76084_b(true);
            var5.func_76069_a(true);
            func_152373_a(var1, this, "commands.weather.thunder", new Object[0]);
         }

      } else {
         throw new WrongUsageException("commands.weather.usage", new Object[0]);
      }
   }

   public List<String> func_180525_a(ICommandSender var1, String[] var2, BlockPos var3) {
      return var2.length == 1 ? func_71530_a(var2, new String[]{"clear", "rain", "thunder"}) : null;
   }
}
