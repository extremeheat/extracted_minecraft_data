package net.minecraft.server.commands;

import com.google.common.collect.ImmutableMap;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import java.io.File;
import java.io.IOException;
import java.nio.file.FileSystem;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.spi.FileSystemProvider;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import javax.annotation.Nullable;
import net.minecraft.SharedConstants;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.Mth;
import net.minecraft.util.profiling.ProfileResults;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class DebugCommand {
   private static final Logger LOGGER = LogManager.getLogger();
   private static final SimpleCommandExceptionType ERROR_NOT_RUNNING = new SimpleCommandExceptionType(new TranslatableComponent("commands.debug.notRunning"));
   private static final SimpleCommandExceptionType ERROR_ALREADY_RUNNING = new SimpleCommandExceptionType(new TranslatableComponent("commands.debug.alreadyRunning"));
   @Nullable
   private static final FileSystemProvider ZIP_FS_PROVIDER = (FileSystemProvider)FileSystemProvider.installedProviders().stream().filter((var0) -> {
      return var0.getScheme().equalsIgnoreCase("jar");
   }).findFirst().orElse((Object)null);

   public static void register(CommandDispatcher<CommandSourceStack> var0) {
      var0.register((LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)Commands.literal("debug").requires((var0x) -> {
         return var0x.hasPermission(3);
      })).then(Commands.literal("start").executes((var0x) -> {
         return start((CommandSourceStack)var0x.getSource());
      }))).then(Commands.literal("stop").executes((var0x) -> {
         return stop((CommandSourceStack)var0x.getSource());
      }))).then(Commands.literal("report").executes((var0x) -> {
         return report((CommandSourceStack)var0x.getSource());
      })));
   }

   private static int start(CommandSourceStack var0) throws CommandSyntaxException {
      MinecraftServer var1 = var0.getServer();
      if (var1.isProfiling()) {
         throw ERROR_ALREADY_RUNNING.create();
      } else {
         var1.startProfiling();
         var0.sendSuccess(new TranslatableComponent("commands.debug.started", new Object[]{"Started the debug profiler. Type '/debug stop' to stop it."}), true);
         return 0;
      }
   }

   private static int stop(CommandSourceStack var0) throws CommandSyntaxException {
      MinecraftServer var1 = var0.getServer();
      if (!var1.isProfiling()) {
         throw ERROR_NOT_RUNNING.create();
      } else {
         ProfileResults var2 = var1.finishProfiling();
         File var3 = new File(var1.getFile("debug"), "profile-results-" + (new SimpleDateFormat("yyyy-MM-dd_HH.mm.ss")).format(new Date()) + ".txt");
         var2.saveResults(var3);
         float var4 = (float)var2.getNanoDuration() / 1.0E9F;
         float var5 = (float)var2.getTickDuration() / var4;
         var0.sendSuccess(new TranslatableComponent("commands.debug.stopped", new Object[]{String.format(Locale.ROOT, "%.2f", var4), var2.getTickDuration(), String.format("%.2f", var5)}), true);
         return Mth.floor(var5);
      }
   }

   private static int report(CommandSourceStack var0) {
      MinecraftServer var1 = var0.getServer();
      String var2 = "debug-report-" + (new SimpleDateFormat("yyyy-MM-dd_HH.mm.ss")).format(new Date());

      try {
         Path var4 = var1.getFile("debug").toPath();
         Files.createDirectories(var4);
         Path var3;
         if (!SharedConstants.IS_RUNNING_IN_IDE && ZIP_FS_PROVIDER != null) {
            var3 = var4.resolve(var2 + ".zip");
            FileSystem var5 = ZIP_FS_PROVIDER.newFileSystem(var3, ImmutableMap.of("create", "true"));
            Throwable var6 = null;

            try {
               var1.saveDebugReport(var5.getPath("/"));
            } catch (Throwable var16) {
               var6 = var16;
               throw var16;
            } finally {
               if (var5 != null) {
                  if (var6 != null) {
                     try {
                        var5.close();
                     } catch (Throwable var15) {
                        var6.addSuppressed(var15);
                     }
                  } else {
                     var5.close();
                  }
               }

            }
         } else {
            var3 = var4.resolve(var2);
            var1.saveDebugReport(var3);
         }

         var0.sendSuccess(new TranslatableComponent("commands.debug.reportSaved", new Object[]{var2}), false);
         return 1;
      } catch (IOException var18) {
         LOGGER.error("Failed to save debug dump", var18);
         var0.sendFailure(new TranslatableComponent("commands.debug.reportFailed"));
         return 0;
      }
   }
}
