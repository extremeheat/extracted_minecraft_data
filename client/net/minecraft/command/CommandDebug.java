package net.minecraft.command;

import java.io.File;
import java.io.FileWriter;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import net.minecraft.profiler.Profiler;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.BlockPos;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class CommandDebug extends CommandBase {
   private static final Logger field_147208_a = LogManager.getLogger();
   private long field_147206_b;
   private int field_147207_c;

   public CommandDebug() {
      super();
   }

   public String func_71517_b() {
      return "debug";
   }

   public int func_82362_a() {
      return 3;
   }

   public String func_71518_a(ICommandSender var1) {
      return "commands.debug.usage";
   }

   public void func_71515_b(ICommandSender var1, String[] var2) throws CommandException {
      if (var2.length < 1) {
         throw new WrongUsageException("commands.debug.usage", new Object[0]);
      } else {
         if (var2[0].equals("start")) {
            if (var2.length != 1) {
               throw new WrongUsageException("commands.debug.usage", new Object[0]);
            }

            func_152373_a(var1, this, "commands.debug.start", new Object[0]);
            MinecraftServer.func_71276_C().func_71223_ag();
            this.field_147206_b = MinecraftServer.func_130071_aq();
            this.field_147207_c = MinecraftServer.func_71276_C().func_71259_af();
         } else {
            if (!var2[0].equals("stop")) {
               throw new WrongUsageException("commands.debug.usage", new Object[0]);
            }

            if (var2.length != 1) {
               throw new WrongUsageException("commands.debug.usage", new Object[0]);
            }

            if (!MinecraftServer.func_71276_C().field_71304_b.field_76327_a) {
               throw new CommandException("commands.debug.notStarted", new Object[0]);
            }

            long var3 = MinecraftServer.func_130071_aq();
            int var5 = MinecraftServer.func_71276_C().func_71259_af();
            long var6 = var3 - this.field_147206_b;
            int var8 = var5 - this.field_147207_c;
            this.func_147205_a(var6, var8);
            MinecraftServer.func_71276_C().field_71304_b.field_76327_a = false;
            func_152373_a(var1, this, "commands.debug.stop", new Object[]{(float)var6 / 1000.0F, var8});
         }

      }
   }

   private void func_147205_a(long var1, int var3) {
      File var4 = new File(MinecraftServer.func_71276_C().func_71209_f("debug"), "profile-results-" + (new SimpleDateFormat("yyyy-MM-dd_HH.mm.ss")).format(new Date()) + ".txt");
      var4.getParentFile().mkdirs();

      try {
         FileWriter var5 = new FileWriter(var4);
         var5.write(this.func_147204_b(var1, var3));
         var5.close();
      } catch (Throwable var6) {
         field_147208_a.error("Could not save profiler results to " + var4, var6);
      }

   }

   private String func_147204_b(long var1, int var3) {
      StringBuilder var4 = new StringBuilder();
      var4.append("---- Minecraft Profiler Results ----\n");
      var4.append("// ");
      var4.append(func_147203_d());
      var4.append("\n\n");
      var4.append("Time span: ").append(var1).append(" ms\n");
      var4.append("Tick span: ").append(var3).append(" ticks\n");
      var4.append("// This is approximately ").append(String.format("%.2f", (float)var3 / ((float)var1 / 1000.0F))).append(" ticks per second. It should be ").append(20).append(" ticks per second\n\n");
      var4.append("--- BEGIN PROFILE DUMP ---\n\n");
      this.func_147202_a(0, "root", var4);
      var4.append("--- END PROFILE DUMP ---\n\n");
      return var4.toString();
   }

   private void func_147202_a(int var1, String var2, StringBuilder var3) {
      List var4 = MinecraftServer.func_71276_C().field_71304_b.func_76321_b(var2);
      if (var4 != null && var4.size() >= 3) {
         for(int var5 = 1; var5 < var4.size(); ++var5) {
            Profiler.Result var6 = (Profiler.Result)var4.get(var5);
            var3.append(String.format("[%02d] ", var1));

            for(int var7 = 0; var7 < var1; ++var7) {
               var3.append(" ");
            }

            var3.append(var6.field_76331_c).append(" - ").append(String.format("%.2f", var6.field_76332_a)).append("%/").append(String.format("%.2f", var6.field_76330_b)).append("%\n");
            if (!var6.field_76331_c.equals("unspecified")) {
               try {
                  this.func_147202_a(var1 + 1, var2 + "." + var6.field_76331_c, var3);
               } catch (Exception var8) {
                  var3.append("[[ EXCEPTION ").append(var8).append(" ]]");
               }
            }
         }

      }
   }

   private static String func_147203_d() {
      String[] var0 = new String[]{"Shiny numbers!", "Am I not running fast enough? :(", "I'm working as hard as I can!", "Will I ever be good enough for you? :(", "Speedy. Zoooooom!", "Hello world", "40% better than a crash report.", "Now with extra numbers", "Now with less numbers", "Now with the same numbers", "You should add flames to things, it makes them go faster!", "Do you feel the need for... optimization?", "*cracks redstone whip*", "Maybe if you treated it better then it'll have more motivation to work faster! Poor server."};

      try {
         return var0[(int)(System.nanoTime() % (long)var0.length)];
      } catch (Throwable var2) {
         return "Witty comment unavailable :(";
      }
   }

   public List<String> func_180525_a(ICommandSender var1, String[] var2, BlockPos var3) {
      return var2.length == 1 ? func_71530_a(var2, new String[]{"start", "stop"}) : null;
   }
}
