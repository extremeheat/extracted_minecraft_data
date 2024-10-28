package net.minecraft.server.commands;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import com.mojang.logging.LogUtils;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Locale;
import java.util.function.Consumer;
import net.minecraft.FileUtil;
import net.minecraft.SharedConstants;
import net.minecraft.SystemReport;
import net.minecraft.Util;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.Component;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.FileZipper;
import net.minecraft.util.TimeUtil;
import net.minecraft.util.profiling.EmptyProfileResults;
import net.minecraft.util.profiling.ProfileResults;
import net.minecraft.util.profiling.metrics.storage.MetricsPersister;
import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;

public class PerfCommand {
   private static final Logger LOGGER = LogUtils.getLogger();
   private static final SimpleCommandExceptionType ERROR_NOT_RUNNING = new SimpleCommandExceptionType(Component.translatable("commands.perf.notRunning"));
   private static final SimpleCommandExceptionType ERROR_ALREADY_RUNNING = new SimpleCommandExceptionType(Component.translatable("commands.perf.alreadyRunning"));

   public PerfCommand() {
      super();
   }

   public static void register(CommandDispatcher<CommandSourceStack> var0) {
      var0.register((LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)Commands.literal("perf").requires((var0x) -> {
         return var0x.hasPermission(4);
      })).then(Commands.literal("start").executes((var0x) -> {
         return startProfilingDedicatedServer((CommandSourceStack)var0x.getSource());
      }))).then(Commands.literal("stop").executes((var0x) -> {
         return stopProfilingDedicatedServer((CommandSourceStack)var0x.getSource());
      })));
   }

   private static int startProfilingDedicatedServer(CommandSourceStack var0) throws CommandSyntaxException {
      MinecraftServer var1 = var0.getServer();
      if (var1.isRecordingMetrics()) {
         throw ERROR_ALREADY_RUNNING.create();
      } else {
         Consumer var2 = (var1x) -> {
            whenStopped(var0, var1x);
         };
         Consumer var3 = (var2x) -> {
            saveResults(var0, var2x, var1);
         };
         var1.startRecordingMetrics(var2, var3);
         var0.sendSuccess(() -> {
            return Component.translatable("commands.perf.started");
         }, false);
         return 0;
      }
   }

   private static int stopProfilingDedicatedServer(CommandSourceStack var0) throws CommandSyntaxException {
      MinecraftServer var1 = var0.getServer();
      if (!var1.isRecordingMetrics()) {
         throw ERROR_NOT_RUNNING.create();
      } else {
         var1.finishRecordingMetrics();
         return 0;
      }
   }

   private static void saveResults(CommandSourceStack var0, Path var1, MinecraftServer var2) {
      String var3 = String.format(Locale.ROOT, "%s-%s-%s", Util.getFilenameFormattedDateTime(), var2.getWorldData().getLevelName(), SharedConstants.getCurrentVersion().getId());

      String var4;
      try {
         var4 = FileUtil.findAvailableName(MetricsPersister.PROFILING_RESULTS_DIR, var3, ".zip");
      } catch (IOException var11) {
         var0.sendFailure(Component.translatable("commands.perf.reportFailed"));
         LOGGER.error("Failed to create report name", var11);
         return;
      }

      FileZipper var5 = new FileZipper(MetricsPersister.PROFILING_RESULTS_DIR.resolve(var4));

      try {
         var5.add(Paths.get("system.txt"), var2.fillSystemReport(new SystemReport()).toLineSeparatedString());
         var5.add(var1);
      } catch (Throwable var10) {
         try {
            var5.close();
         } catch (Throwable var8) {
            var10.addSuppressed(var8);
         }

         throw var10;
      }

      var5.close();

      try {
         FileUtils.forceDelete(var1.toFile());
      } catch (IOException var9) {
         LOGGER.warn("Failed to delete temporary profiling file {}", var1, var9);
      }

      var0.sendSuccess(() -> {
         return Component.translatable("commands.perf.reportSaved", var4);
      }, false);
   }

   private static void whenStopped(CommandSourceStack var0, ProfileResults var1) {
      if (var1 != EmptyProfileResults.EMPTY) {
         int var2 = var1.getTickDuration();
         double var3 = (double)var1.getNanoDuration() / (double)TimeUtil.NANOSECONDS_PER_SECOND;
         var0.sendSuccess(() -> {
            return Component.translatable("commands.perf.stopped", String.format(Locale.ROOT, "%.2f", var3), var2, String.format(Locale.ROOT, "%.2f", (double)var2 / var3));
         }, false);
      }
   }
}
