package net.minecraft.command.impl;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import net.minecraft.command.CommandSource;
import net.minecraft.command.Commands;
import net.minecraft.profiler.Profiler;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.Util;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.text.TextComponentTranslation;
import org.apache.commons.io.IOUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class DebugCommand {
   private static final Logger field_198337_a = LogManager.getLogger();
   private static final SimpleCommandExceptionType field_198338_b = new SimpleCommandExceptionType(new TextComponentTranslation("commands.debug.notRunning", new Object[0]));
   private static final SimpleCommandExceptionType field_198339_c = new SimpleCommandExceptionType(new TextComponentTranslation("commands.debug.alreadyRunning", new Object[0]));

   public static void func_198330_a(CommandDispatcher<CommandSource> var0) {
      var0.register((LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)Commands.func_197057_a("debug").requires((var0x) -> {
         return var0x.func_197034_c(3);
      })).then(Commands.func_197057_a("start").executes((var0x) -> {
         return func_198335_a((CommandSource)var0x.getSource());
      }))).then(Commands.func_197057_a("stop").executes((var0x) -> {
         return func_198336_b((CommandSource)var0x.getSource());
      })));
   }

   private static int func_198335_a(CommandSource var0) throws CommandSyntaxException {
      MinecraftServer var1 = var0.func_197028_i();
      Profiler var2 = var1.field_71304_b;
      if (var2.func_199094_a()) {
         throw field_198339_c.create();
      } else {
         var1.func_71223_ag();
         var0.func_197030_a(new TextComponentTranslation("commands.debug.started", new Object[]{"Started the debug profiler. Type '/debug stop' to stop it."}), true);
         return 0;
      }
   }

   private static int func_198336_b(CommandSource var0) throws CommandSyntaxException {
      MinecraftServer var1 = var0.func_197028_i();
      Profiler var2 = var1.field_71304_b;
      if (!var2.func_199094_a()) {
         throw field_198338_b.create();
      } else {
         long var3 = Util.func_211178_c();
         int var5 = var1.func_71259_af();
         long var6 = var3 - var2.func_199097_c();
         int var8 = var5 - var2.func_199096_d();
         File var9 = new File(var1.func_71209_f("debug"), "profile-results-" + (new SimpleDateFormat("yyyy-MM-dd_HH.mm.ss")).format(new Date()) + ".txt");
         var9.getParentFile().mkdirs();
         OutputStreamWriter var10 = null;

         try {
            var10 = new OutputStreamWriter(new FileOutputStream(var9), StandardCharsets.UTF_8);
            var10.write(func_198328_a(var6, var8, var2));
         } catch (Throwable var15) {
            field_198337_a.error("Could not save profiler results to {}", var9, var15);
         } finally {
            IOUtils.closeQuietly(var10);
         }

         var2.func_199098_b();
         float var11 = (float)var6 / 1.0E9F;
         float var12 = (float)var8 / var11;
         var0.func_197030_a(new TextComponentTranslation("commands.debug.stopped", new Object[]{String.format(Locale.ROOT, "%.2f", var11), var8, String.format("%.2f", var12)}), true);
         return MathHelper.func_76141_d(var12);
      }
   }

   private static String func_198328_a(long var0, int var2, Profiler var3) {
      StringBuilder var4 = new StringBuilder();
      var4.append("---- Minecraft Profiler Results ----\n");
      var4.append("// ");
      var4.append(func_198331_a());
      var4.append("\n\n");
      var4.append("Time span: ").append(var0).append(" ms\n");
      var4.append("Tick span: ").append(var2).append(" ticks\n");
      var4.append("// This is approximately ").append(String.format(Locale.ROOT, "%.2f", (float)var2 / ((float)var0 / 1.0E9F))).append(" ticks per second. It should be ").append(20).append(" ticks per second\n\n");
      var4.append("--- BEGIN PROFILE DUMP ---\n\n");
      func_198334_a(0, "root", var4, var3);
      var4.append("--- END PROFILE DUMP ---\n\n");
      return var4.toString();
   }

   private static void func_198334_a(int var0, String var1, StringBuilder var2, Profiler var3) {
      List var4 = var3.func_76321_b(var1);
      if (var4 != null && var4.size() >= 3) {
         for(int var5 = 1; var5 < var4.size(); ++var5) {
            Profiler.Result var6 = (Profiler.Result)var4.get(var5);
            var2.append(String.format("[%02d] ", var0));

            for(int var7 = 0; var7 < var0; ++var7) {
               var2.append("|   ");
            }

            var2.append(var6.field_76331_c).append(" - ").append(String.format(Locale.ROOT, "%.2f", var6.field_76332_a)).append("%/").append(String.format(Locale.ROOT, "%.2f", var6.field_76330_b)).append("%\n");
            if (!"unspecified".equals(var6.field_76331_c)) {
               try {
                  func_198334_a(var0 + 1, var1 + "." + var6.field_76331_c, var2, var3);
               } catch (Exception var8) {
                  var2.append("[[ EXCEPTION ").append(var8).append(" ]]");
               }
            }
         }

      }
   }

   private static String func_198331_a() {
      String[] var0 = new String[]{"Shiny numbers!", "Am I not running fast enough? :(", "I'm working as hard as I can!", "Will I ever be good enough for you? :(", "Speedy. Zoooooom!", "Hello world", "40% better than a crash report.", "Now with extra numbers", "Now with less numbers", "Now with the same numbers", "You should add flames to things, it makes them go faster!", "Do you feel the need for... optimization?", "*cracks redstone whip*", "Maybe if you treated it better then it'll have more motivation to work faster! Poor server."};

      try {
         return var0[(int)(Util.func_211178_c() % (long)var0.length)];
      } catch (Throwable var2) {
         return "Witty comment unavailable :(";
      }
   }
}
