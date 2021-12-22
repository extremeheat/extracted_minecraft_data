package net.minecraft.server.commands;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.UncheckedIOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.text.SimpleDateFormat;
import java.util.Collection;
import java.util.Date;
import java.util.Iterator;
import java.util.Locale;
import java.util.UUID;
import net.minecraft.Util;
import net.minecraft.commands.CommandFunction;
import net.minecraft.commands.CommandSource;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.item.FunctionArgument;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.ServerFunctionManager;
import net.minecraft.util.TimeUtil;
import net.minecraft.util.profiling.ProfileResults;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class DebugCommand {
   private static final Logger LOGGER = LogManager.getLogger();
   private static final SimpleCommandExceptionType ERROR_NOT_RUNNING = new SimpleCommandExceptionType(new TranslatableComponent("commands.debug.notRunning"));
   private static final SimpleCommandExceptionType ERROR_ALREADY_RUNNING = new SimpleCommandExceptionType(new TranslatableComponent("commands.debug.alreadyRunning"));

   public DebugCommand() {
      super();
   }

   public static void register(CommandDispatcher<CommandSourceStack> var0) {
      var0.register((LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)Commands.literal("debug").requires((var0x) -> {
         return var0x.hasPermission(3);
      })).then(Commands.literal("start").executes((var0x) -> {
         return start((CommandSourceStack)var0x.getSource());
      }))).then(Commands.literal("stop").executes((var0x) -> {
         return stop((CommandSourceStack)var0x.getSource());
      }))).then(((LiteralArgumentBuilder)Commands.literal("function").requires((var0x) -> {
         return var0x.hasPermission(3);
      })).then(Commands.argument("name", FunctionArgument.functions()).suggests(FunctionCommand.SUGGEST_FUNCTION).executes((var0x) -> {
         return traceFunction((CommandSourceStack)var0x.getSource(), FunctionArgument.getFunctions(var0x, "name"));
      }))));
   }

   private static int start(CommandSourceStack var0) throws CommandSyntaxException {
      MinecraftServer var1 = var0.getServer();
      if (var1.isTimeProfilerRunning()) {
         throw ERROR_ALREADY_RUNNING.create();
      } else {
         var1.startTimeProfiler();
         var0.sendSuccess(new TranslatableComponent("commands.debug.started"), true);
         return 0;
      }
   }

   private static int stop(CommandSourceStack var0) throws CommandSyntaxException {
      MinecraftServer var1 = var0.getServer();
      if (!var1.isTimeProfilerRunning()) {
         throw ERROR_NOT_RUNNING.create();
      } else {
         ProfileResults var2 = var1.stopTimeProfiler();
         double var3 = (double)var2.getNanoDuration() / (double)TimeUtil.NANOSECONDS_PER_SECOND;
         double var5 = (double)var2.getTickDuration() / var3;
         var0.sendSuccess(new TranslatableComponent("commands.debug.stopped", new Object[]{String.format(Locale.ROOT, "%.2f", var3), var2.getTickDuration(), String.format("%.2f", var5)}), true);
         return (int)var5;
      }
   }

   private static int traceFunction(CommandSourceStack var0, Collection<CommandFunction> var1) {
      int var2 = 0;
      MinecraftServer var3 = var0.getServer();
      SimpleDateFormat var10000 = new SimpleDateFormat("yyyy-MM-dd_HH.mm.ss");
      Date var10001 = new Date();
      String var4 = "debug-trace-" + var10000.format(var10001) + ".txt";

      try {
         Path var5 = var3.getFile("debug").toPath();
         Files.createDirectories(var5);
         BufferedWriter var6 = Files.newBufferedWriter(var5.resolve(var4), StandardCharsets.UTF_8);

         try {
            PrintWriter var7 = new PrintWriter(var6);

            CommandFunction var9;
            DebugCommand.Tracer var10;
            for(Iterator var8 = var1.iterator(); var8.hasNext(); var2 += var0.getServer().getFunctions().execute(var9, var0.withSource(var10).withMaximumPermission(2), var10)) {
               var9 = (CommandFunction)var8.next();
               var7.println(var9.getId());
               var10 = new DebugCommand.Tracer(var7);
            }
         } catch (Throwable var12) {
            if (var6 != null) {
               try {
                  var6.close();
               } catch (Throwable var11) {
                  var12.addSuppressed(var11);
               }
            }

            throw var12;
         }

         if (var6 != null) {
            var6.close();
         }
      } catch (IOException | UncheckedIOException var13) {
         LOGGER.warn("Tracing failed", var13);
         var0.sendFailure(new TranslatableComponent("commands.debug.function.traceFailed"));
      }

      if (var1.size() == 1) {
         var0.sendSuccess(new TranslatableComponent("commands.debug.function.success.single", new Object[]{var2, ((CommandFunction)var1.iterator().next()).getId(), var4}), true);
      } else {
         var0.sendSuccess(new TranslatableComponent("commands.debug.function.success.multiple", new Object[]{var2, var1.size(), var4}), true);
      }

      return var2;
   }

   private static class Tracer implements CommandSource, ServerFunctionManager.TraceCallbacks {
      public static final int INDENT_OFFSET = 1;
      private final PrintWriter output;
      private int lastIndent;
      private boolean waitingForResult;

      Tracer(PrintWriter var1) {
         super();
         this.output = var1;
      }

      private void indentAndSave(int var1) {
         this.printIndent(var1);
         this.lastIndent = var1;
      }

      private void printIndent(int var1) {
         for(int var2 = 0; var2 < var1 + 1; ++var2) {
            this.output.write("    ");
         }

      }

      private void newLine() {
         if (this.waitingForResult) {
            this.output.println();
            this.waitingForResult = false;
         }

      }

      public void onCommand(int var1, String var2) {
         this.newLine();
         this.indentAndSave(var1);
         this.output.print("[C] ");
         this.output.print(var2);
         this.waitingForResult = true;
      }

      public void onReturn(int var1, String var2, int var3) {
         if (this.waitingForResult) {
            this.output.print(" -> ");
            this.output.println(var3);
            this.waitingForResult = false;
         } else {
            this.indentAndSave(var1);
            this.output.print("[R = ");
            this.output.print(var3);
            this.output.print("] ");
            this.output.println(var2);
         }

      }

      public void onCall(int var1, ResourceLocation var2, int var3) {
         this.newLine();
         this.indentAndSave(var1);
         this.output.print("[F] ");
         this.output.print(var2);
         this.output.print(" size=");
         this.output.println(var3);
      }

      public void onError(int var1, String var2) {
         this.newLine();
         this.indentAndSave(var1 + 1);
         this.output.print("[E] ");
         this.output.print(var2);
      }

      public void sendMessage(Component var1, UUID var2) {
         this.newLine();
         this.printIndent(this.lastIndent + 1);
         this.output.print("[M] ");
         if (var2 != Util.NIL_UUID) {
            this.output.print(var2);
            this.output.print(": ");
         }

         this.output.println(var1.getString());
      }

      public boolean acceptsSuccess() {
         return true;
      }

      public boolean acceptsFailure() {
         return true;
      }

      public boolean shouldInformAdmins() {
         return false;
      }

      public boolean alwaysAccepts() {
         return true;
      }
   }
}
