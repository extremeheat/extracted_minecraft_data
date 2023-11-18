package net.minecraft.server.commands;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import com.mojang.logging.LogUtils;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.UncheckedIOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Collection;
import java.util.Locale;
import net.minecraft.Util;
import net.minecraft.commands.CommandFunction;
import net.minecraft.commands.CommandSource;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.FunctionInstantiationException;
import net.minecraft.commands.arguments.item.FunctionArgument;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.ServerFunctionManager;
import net.minecraft.util.TimeUtil;
import net.minecraft.util.profiling.ProfileResults;
import org.slf4j.Logger;

public class DebugCommand {
   private static final Logger LOGGER = LogUtils.getLogger();
   private static final SimpleCommandExceptionType ERROR_NOT_RUNNING = new SimpleCommandExceptionType(Component.translatable("commands.debug.notRunning"));
   private static final SimpleCommandExceptionType ERROR_ALREADY_RUNNING = new SimpleCommandExceptionType(
      Component.translatable("commands.debug.alreadyRunning")
   );

   public DebugCommand() {
      super();
   }

   public static void register(CommandDispatcher<CommandSourceStack> var0) {
      var0.register(
         (LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)Commands.literal("debug")
                     .requires(var0x -> var0x.hasPermission(3)))
                  .then(Commands.literal("start").executes(var0x -> start((CommandSourceStack)var0x.getSource()))))
               .then(Commands.literal("stop").executes(var0x -> stop((CommandSourceStack)var0x.getSource()))))
            .then(
               ((LiteralArgumentBuilder)Commands.literal("function").requires(var0x -> var0x.hasPermission(3)))
                  .then(
                     Commands.argument("name", FunctionArgument.functions())
                        .suggests(FunctionCommand.SUGGEST_FUNCTION)
                        .executes(var0x -> traceFunction((CommandSourceStack)var0x.getSource(), FunctionArgument.getFunctions(var0x, "name")))
                  )
            )
      );
   }

   private static int start(CommandSourceStack var0) throws CommandSyntaxException {
      MinecraftServer var1 = var0.getServer();
      if (var1.isTimeProfilerRunning()) {
         throw ERROR_ALREADY_RUNNING.create();
      } else {
         var1.startTimeProfiler();
         var0.sendSuccess(() -> Component.translatable("commands.debug.started"), true);
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
         var0.sendSuccess(
            () -> Component.translatable(
                  "commands.debug.stopped", String.format(Locale.ROOT, "%.2f", var3), var2.getTickDuration(), String.format(Locale.ROOT, "%.2f", var5)
               ),
            true
         );
         return (int)var5;
      }
   }

   private static int traceFunction(CommandSourceStack var0, Collection<CommandFunction> var1) {
      int var2 = 0;
      MinecraftServer var3 = var0.getServer();
      String var4 = "debug-trace-" + Util.getFilenameFormattedDateTime() + ".txt";

      try {
         Path var5 = var3.getFile("debug").toPath();
         Files.createDirectories(var5);

         try (BufferedWriter var6 = Files.newBufferedWriter(var5.resolve(var4), StandardCharsets.UTF_8)) {
            PrintWriter var7 = new PrintWriter(var6);

            for(CommandFunction var9 : var1) {
               var7.println(var9.getId());
               DebugCommand.Tracer var10 = new DebugCommand.Tracer(var7);

               try {
                  var2 += var0.getServer().getFunctions().execute(var9, var0.withSource(var10).withMaximumPermission(2), var10, null);
               } catch (FunctionInstantiationException var13) {
                  var0.sendFailure(var13.messageComponent());
               }
            }
         }
      } catch (IOException | UncheckedIOException var15) {
         LOGGER.warn("Tracing failed", var15);
         var0.sendFailure(Component.translatable("commands.debug.function.traceFailed"));
      }

      int var16 = var2;
      if (var1.size() == 1) {
         var0.sendSuccess(
            () -> Component.translatable("commands.debug.function.success.single", var16, ((CommandFunction)var1.iterator().next()).getId(), var4), true
         );
      } else {
         var0.sendSuccess(() -> Component.translatable("commands.debug.function.success.multiple", var16, var1.size(), var4), true);
      }

      return var2;
   }

   static class Tracer implements ServerFunctionManager.TraceCallbacks, CommandSource {
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

      @Override
      public void onCommand(int var1, String var2) {
         this.newLine();
         this.indentAndSave(var1);
         this.output.print("[C] ");
         this.output.print(var2);
         this.waitingForResult = true;
      }

      @Override
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

      @Override
      public void onCall(int var1, ResourceLocation var2, int var3) {
         this.newLine();
         this.indentAndSave(var1);
         this.output.print("[F] ");
         this.output.print(var2);
         this.output.print(" size=");
         this.output.println(var3);
      }

      @Override
      public void onError(int var1, String var2) {
         this.newLine();
         this.indentAndSave(var1 + 1);
         this.output.print("[E] ");
         this.output.print(var2);
      }

      @Override
      public void sendSystemMessage(Component var1) {
         this.newLine();
         this.printIndent(this.lastIndent + 1);
         this.output.print("[M] ");
         this.output.println(var1.getString());
      }

      @Override
      public boolean acceptsSuccess() {
         return true;
      }

      @Override
      public boolean acceptsFailure() {
         return true;
      }

      @Override
      public boolean shouldInformAdmins() {
         return false;
      }

      @Override
      public boolean alwaysAccepts() {
         return true;
      }
   }
}
