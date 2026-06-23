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
import java.util.Objects;
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
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.permissions.LevelBasedPermissionSet;
import net.minecraft.util.TimeUtil;
import net.minecraft.util.Util;
import net.minecraft.util.profiling.ProfileResults;
import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;

public class DebugCommand {
   private static final Logger LOGGER = LogUtils.getLogger();
   private static final SimpleCommandExceptionType ERROR_NOT_RUNNING = new SimpleCommandExceptionType(Component.translatable("commands.debug.notRunning"));
   private static final SimpleCommandExceptionType ERROR_ALREADY_RUNNING = new SimpleCommandExceptionType(Component.translatable("commands.debug.alreadyRunning"));
   private static final SimpleCommandExceptionType NO_RECURSIVE_TRACES = new SimpleCommandExceptionType(Component.translatable("commands.debug.function.noRecursion"));
   private static final SimpleCommandExceptionType NO_RETURN_RUN = new SimpleCommandExceptionType(Component.translatable("commands.debug.function.noReturnRun"));

   public DebugCommand() {
      super();
   }

   public static void register(final CommandDispatcher<CommandSourceStack> dispatcher) {
      dispatcher.register((LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)Commands.literal("debug").requires(Commands.hasPermission(Commands.LEVEL_ADMINS))).then(Commands.literal("start").executes((c) -> start((CommandSourceStack)c.getSource())))).then(Commands.literal("stop").executes((c) -> stop((CommandSourceStack)c.getSource())))).then(((LiteralArgumentBuilder)Commands.literal("function").requires(Commands.hasPermission(Commands.LEVEL_ADMINS))).then(Commands.argument("name", FunctionArgument.functions()).suggests(FunctionCommand.SUGGEST_FUNCTION).executes(new TraceCustomExecutor()))));
   }

   private static int start(final CommandSourceStack source) throws CommandSyntaxException {
      MinecraftServer server = source.getServer();
      if (server.isTimeProfilerRunning()) {
         throw ERROR_ALREADY_RUNNING.create();
      } else {
         server.startTimeProfiler();
         source.sendSuccess(() -> Component.translatable("commands.debug.started"), true);
         return 0;
      }
   }

   private static int stop(final CommandSourceStack source) throws CommandSyntaxException {
      MinecraftServer server = source.getServer();
      if (!server.isTimeProfilerRunning()) {
         throw ERROR_NOT_RUNNING.create();
      } else {
         ProfileResults results = server.stopTimeProfiler();
         double seconds = (double)results.getNanoDuration() / (double)TimeUtil.NANOSECONDS_PER_SECOND;
         double tps = (double)results.getTickDuration() / seconds;
         source.sendSuccess(() -> Component.translatable("commands.debug.stopped", String.format(Locale.ROOT, "%.2f", seconds), results.getTickDuration(), String.format(Locale.ROOT, "%.2f", tps)), true);
         return (int)tps;
      }
   }

   private static class TraceCustomExecutor extends CustomCommandExecutor.WithErrorHandling<CommandSourceStack> implements CustomCommandExecutor.CommandAdapter<CommandSourceStack> {
      private static final CommandResponseTracker.DispatchWithArg<Component, CommandFunction<CommandSourceStack>, String> RESPONSE_TRACE = new CommandResponseTracker.DispatchWithArg<Component, CommandFunction<CommandSourceStack>, String>((function, totalValue, outputName) -> Component.translatable("commands.debug.function.success.single", totalValue, Component.translationArg(function.id()), outputName), (functionCount, totalValue, outputName) -> Component.translatable("commands.debug.function.success.multiple", totalValue, functionCount, outputName));

      private TraceCustomExecutor() {
         super();
      }

      public void runGuarded(final CommandSourceStack source, final ContextChain<CommandSourceStack> currentStep, final ChainModifiers modifiers, final ExecutionControl<CommandSourceStack> context) throws CommandSyntaxException {
         if (modifiers.isReturn()) {
            throw DebugCommand.NO_RETURN_RUN.create();
         } else if (context.tracer() != null) {
            throw DebugCommand.NO_RECURSIVE_TRACES.create();
         } else {
            CommandContext<CommandSourceStack> currentContext = currentStep.getTopContext();
            Collection<CommandFunction<CommandSourceStack>> functions = FunctionArgument.getFunctions(currentContext, "name");
            MinecraftServer server = source.getServer();
            String outputName = "debug-trace-" + Util.getFilenameFormattedDateTime() + ".txt";
            CommandDispatcher<CommandSourceStack> dispatcher = source.getServer().getFunctions().getDispatcher();
            CommandResponseTracker<CommandFunction<CommandSourceStack>> tracker = CommandResponseTracker.<CommandFunction<CommandSourceStack>>create();

            try {
               Path dirPath = server.getFile("debug");
               Files.createDirectories(dirPath);
               final PrintWriter output = new PrintWriter(Files.newBufferedWriter(dirPath.resolve(outputName), StandardCharsets.UTF_8));
               Tracer tracer = new Tracer(output);
               context.tracer(tracer);

               for(final CommandFunction<CommandSourceStack> function : functions) {
                  try {
                     CommandSourceStack functionSource = source.withSource(tracer).withMaximumPermission(LevelBasedPermissionSet.GAMEMASTER);
                     InstantiatedFunction<CommandSourceStack> instantiatedFunction = function.instantiate((CompoundTag)null, dispatcher);
                     context.queueNext((new CallFunction<CommandSourceStack>(instantiatedFunction, CommandResultCallback.EMPTY, false) {
                        {
                           Objects.requireNonNull(TraceCustomExecutor.this);
                        }

                        public void execute(final CommandSourceStack sender, final ExecutionContext<CommandSourceStack> context, final Frame frame) {
                           output.println(function.id());
                           super.execute(sender, context, frame);
                        }
                     }).bind(functionSource));
                     tracker.track(function, instantiatedFunction.entries().size());
                  } catch (FunctionInstantiationException exception) {
                     source.sendFailure(exception.messageComponent());
                  }
               }
            } catch (IOException | UncheckedIOException e) {
               DebugCommand.LOGGER.warn("Tracing failed", e);
               source.sendFailure(Component.translatable("commands.debug.function.traceFailed"));
            }

            context.queueNext((var3, var4) -> source.sendSuccess(() -> (Component)tracker.dispatch(CommandResponseTracker.ElementType.ANY, RESPONSE_TRACE, outputName), true));
         }
      }
   }

   private static class Tracer implements CommandSource, TraceCallbacks {
      public static final int INDENT_OFFSET = 1;
      private final PrintWriter output;
      private int lastIndent;
      private boolean waitingForResult;

      private Tracer(final PrintWriter output) {
         super();
         this.output = output;
      }

      private void indentAndSave(final int value) {
         this.printIndent(value);
         this.lastIndent = value;
      }

      private void printIndent(final int value) {
         for(int i = 0; i < value + 1; ++i) {
            this.output.write("    ");
         }

      }

      private void newLine() {
         if (this.waitingForResult) {
            this.output.println();
            this.waitingForResult = false;
         }

      }

      public void onCommand(final int depth, final String command) {
         this.newLine();
         this.indentAndSave(depth);
         this.output.print("[C] ");
         this.output.print(command);
         this.waitingForResult = true;
      }

      public void onReturn(final int depth, final String command, final int result) {
         if (this.waitingForResult) {
            this.output.print(" -> ");
            this.output.println(result);
            this.waitingForResult = false;
         } else {
            this.indentAndSave(depth);
            this.output.print("[R = ");
            this.output.print(result);
            this.output.print("] ");
            this.output.println(command);
         }

      }

      public void onCall(final int depth, final Identifier function, final int size) {
         this.newLine();
         this.indentAndSave(depth);
         this.output.print("[F] ");
         this.output.print(function);
         this.output.print(" size=");
         this.output.println(size);
      }

      public void onError(final String message) {
         this.newLine();
         this.indentAndSave(this.lastIndent + 1);
         this.output.print("[E] ");
         this.output.print(message);
      }

      public void sendSystemMessage(final Component message) {
         this.newLine();
         this.printIndent(this.lastIndent + 1);
         this.output.print("[M] ");
         this.output.println(message.getString());
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

      public void close() {
         IOUtils.closeQuietly(this.output);
      }
   }
}
