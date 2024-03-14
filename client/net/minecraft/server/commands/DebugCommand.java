package net.minecraft.server.commands;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.context.ContextChain;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import com.mojang.logging.LogUtils;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.UncheckedIOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Collection;
import java.util.Locale;
import net.minecraft.Util;
import net.minecraft.commands.CommandResultCallback;
import net.minecraft.commands.CommandSource;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.FunctionInstantiationException;
import net.minecraft.commands.arguments.item.FunctionArgument;
import net.minecraft.commands.execution.ChainModifiers;
import net.minecraft.commands.execution.CustomCommandExecutor;
import net.minecraft.commands.execution.ExecutionContext;
import net.minecraft.commands.execution.ExecutionControl;
import net.minecraft.commands.execution.Frame;
import net.minecraft.commands.execution.TraceCallbacks;
import net.minecraft.commands.execution.tasks.CallFunction;
import net.minecraft.commands.functions.CommandFunction;
import net.minecraft.commands.functions.InstantiatedFunction;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.TimeUtil;
import net.minecraft.util.profiling.ProfileResults;
import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;

public class DebugCommand {
   static final Logger LOGGER = LogUtils.getLogger();
   private static final SimpleCommandExceptionType ERROR_NOT_RUNNING = new SimpleCommandExceptionType(Component.translatable("commands.debug.notRunning"));
   private static final SimpleCommandExceptionType ERROR_ALREADY_RUNNING = new SimpleCommandExceptionType(
      Component.translatable("commands.debug.alreadyRunning")
   );
   static final SimpleCommandExceptionType NO_RECURSIVE_TRACES = new SimpleCommandExceptionType(Component.translatable("commands.debug.function.noRecursion"));
   static final SimpleCommandExceptionType NO_RETURN_RUN = new SimpleCommandExceptionType(Component.translatable("commands.debug.function.noReturnRun"));

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
                        .executes(new DebugCommand.TraceCustomExecutor())
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

   static class TraceCustomExecutor
      extends CustomCommandExecutor.WithErrorHandling<CommandSourceStack>
      implements CustomCommandExecutor.CommandAdapter<CommandSourceStack> {
      TraceCustomExecutor() {
         super();
      }

      public void runGuarded(CommandSourceStack var1, ContextChain<CommandSourceStack> var2, ChainModifiers var3, ExecutionControl<CommandSourceStack> var4) throws CommandSyntaxException {
         if (var3.isReturn()) {
            throw DebugCommand.NO_RETURN_RUN.create();
         } else if (var4.tracer() != null) {
            throw DebugCommand.NO_RECURSIVE_TRACES.create();
         } else {
            CommandContext var5 = var2.getTopContext();
            Collection var6 = FunctionArgument.getFunctions(var5, "name");
            MinecraftServer var7 = var1.getServer();
            String var8 = "debug-trace-" + Util.getFilenameFormattedDateTime() + ".txt";
            CommandDispatcher var9 = var1.getServer().getFunctions().getDispatcher();
            int var10 = 0;

            try {
               Path var11 = var7.getFile("debug").toPath();
               Files.createDirectories(var11);
               final PrintWriter var12 = new PrintWriter(Files.newBufferedWriter(var11.resolve(var8), StandardCharsets.UTF_8));
               DebugCommand.Tracer var13 = new DebugCommand.Tracer(var12);
               var4.tracer(var13);

               for(final CommandFunction var15 : var6) {
                  try {
                     CommandSourceStack var16 = var1.withSource(var13).withMaximumPermission(2);
                     InstantiatedFunction var17 = var15.instantiate(null, var9);
                     var4.queueNext((new CallFunction<CommandSourceStack>(var17, CommandResultCallback.EMPTY, false) {
                        public void execute(CommandSourceStack var1, ExecutionContext<CommandSourceStack> var2, Frame var3) {
                           var12.println(var15.id());
                           super.execute(var1, var2, var3);
                        }
                     }).bind(var16));
                     var10 += var17.entries().size();
                  } catch (FunctionInstantiationException var18) {
                     var1.sendFailure(var18.messageComponent());
                  }
               }
            } catch (IOException | UncheckedIOException var19) {
               DebugCommand.LOGGER.warn("Tracing failed", var19);
               var1.sendFailure(Component.translatable("commands.debug.function.traceFailed"));
            }

            int var20 = var10;
            var4.queueNext(
               (var4x, var5x) -> {
                  if (var6.size() == 1) {
                     var1.sendSuccess(
                        () -> Component.translatable(
                              "commands.debug.function.success.single", var20, Component.translationArg(((CommandFunction)var6.iterator().next()).id()), var8
                           ),
                        true
                     );
                  } else {
                     var1.sendSuccess(() -> Component.translatable("commands.debug.function.success.multiple", var20, var6.size(), var8), true);
                  }
               }
            );
         }
      }
   }

   static class Tracer implements CommandSource, TraceCallbacks {
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
      public void onError(String var1) {
         this.newLine();
         this.indentAndSave(this.lastIndent + 1);
         this.output.print("[E] ");
         this.output.print(var1);
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

      @Override
      public void close() {
         IOUtils.closeQuietly(this.output);
      }
   }
}
