package net.minecraft.server.commands;

import com.google.common.annotations.VisibleForTesting;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.builder.RequiredArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.context.ContextChain;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.Dynamic2CommandExceptionType;
import com.mojang.brigadier.exceptions.DynamicCommandExceptionType;
import com.mojang.brigadier.suggestion.SuggestionProvider;
import com.mojang.datafixers.util.Pair;
import java.util.Collection;
import java.util.Iterator;
import java.util.function.Function;
import javax.annotation.Nullable;
import net.minecraft.commands.CommandResultCallback;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.ExecutionCommandSource;
import net.minecraft.commands.FunctionInstantiationException;
import net.minecraft.commands.SharedSuggestionProvider;
import net.minecraft.commands.arguments.CompoundTagArgument;
import net.minecraft.commands.arguments.NbtPathArgument;
import net.minecraft.commands.arguments.item.FunctionArgument;
import net.minecraft.commands.execution.ChainModifiers;
import net.minecraft.commands.execution.CustomCommandExecutor;
import net.minecraft.commands.execution.ExecutionControl;
import net.minecraft.commands.execution.tasks.CallFunction;
import net.minecraft.commands.execution.tasks.FallthroughTask;
import net.minecraft.commands.functions.CommandFunction;
import net.minecraft.commands.functions.InstantiatedFunction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.ComponentUtils;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.ServerFunctionManager;
import net.minecraft.server.commands.data.DataAccessor;
import net.minecraft.server.commands.data.DataCommands;

public class FunctionCommand {
   private static final DynamicCommandExceptionType ERROR_ARGUMENT_NOT_COMPOUND = new DynamicCommandExceptionType((var0) -> {
      return Component.translatableEscape("commands.function.error.argument_not_compound", var0);
   });
   static final DynamicCommandExceptionType ERROR_NO_FUNCTIONS = new DynamicCommandExceptionType((var0) -> {
      return Component.translatableEscape("commands.function.scheduled.no_functions", var0);
   });
   @VisibleForTesting
   public static final Dynamic2CommandExceptionType ERROR_FUNCTION_INSTANTATION_FAILURE = new Dynamic2CommandExceptionType((var0, var1) -> {
      return Component.translatableEscape("commands.function.instantiationFailure", var0, var1);
   });
   public static final SuggestionProvider<CommandSourceStack> SUGGEST_FUNCTION = (var0, var1) -> {
      ServerFunctionManager var2 = ((CommandSourceStack)var0.getSource()).getServer().getFunctions();
      SharedSuggestionProvider.suggestResource(var2.getTagNames(), var1, "#");
      return SharedSuggestionProvider.suggestResource(var2.getFunctionNames(), var1);
   };
   static final Callbacks<CommandSourceStack> FULL_CONTEXT_CALLBACKS = new Callbacks<CommandSourceStack>() {
      public void signalResult(CommandSourceStack var1, ResourceLocation var2, int var3) {
         var1.sendSuccess(() -> {
            return Component.translatable("commands.function.result", Component.translationArg(var2), var3);
         }, true);
      }
   };

   public FunctionCommand() {
      super();
   }

   public static void register(CommandDispatcher<CommandSourceStack> var0) {
      LiteralArgumentBuilder var1 = Commands.literal("with");
      Iterator var2 = DataCommands.SOURCE_PROVIDERS.iterator();

      while(var2.hasNext()) {
         DataCommands.DataProvider var3 = (DataCommands.DataProvider)var2.next();
         var3.wrap(var1, (var1x) -> {
            return var1x.executes(new FunctionCustomExecutor() {
               protected CompoundTag arguments(CommandContext<CommandSourceStack> var1) throws CommandSyntaxException {
                  return var0.access(var1).getData();
               }
            }).then(Commands.argument("path", NbtPathArgument.nbtPath()).executes(new FunctionCustomExecutor() {
               protected CompoundTag arguments(CommandContext<CommandSourceStack> var1) throws CommandSyntaxException {
                  return FunctionCommand.getArgumentTag(NbtPathArgument.getPath(var1, "path"), var0.access(var1));
               }
            }));
         });
      }

      var0.register((LiteralArgumentBuilder)((LiteralArgumentBuilder)Commands.literal("function").requires((var0x) -> {
         return var0x.hasPermission(2);
      })).then(((RequiredArgumentBuilder)((RequiredArgumentBuilder)Commands.argument("name", FunctionArgument.functions()).suggests(SUGGEST_FUNCTION).executes(new FunctionCustomExecutor() {
         @Nullable
         protected CompoundTag arguments(CommandContext<CommandSourceStack> var1) {
            return null;
         }
      })).then(Commands.argument("arguments", CompoundTagArgument.compoundTag()).executes(new FunctionCustomExecutor() {
         protected CompoundTag arguments(CommandContext<CommandSourceStack> var1) {
            return CompoundTagArgument.getCompoundTag(var1, "arguments");
         }
      }))).then(var1)));
   }

   static CompoundTag getArgumentTag(NbtPathArgument.NbtPath var0, DataAccessor var1) throws CommandSyntaxException {
      Tag var2 = DataCommands.getSingleTag(var0, var1);
      if (var2 instanceof CompoundTag var3) {
         return var3;
      } else {
         throw ERROR_ARGUMENT_NOT_COMPOUND.create(var2.getType().getName());
      }
   }

   public static CommandSourceStack modifySenderForExecution(CommandSourceStack var0) {
      return var0.withSuppressedOutput().withMaximumPermission(2);
   }

   public static <T extends ExecutionCommandSource<T>> void queueFunctions(Collection<CommandFunction<T>> var0, @Nullable CompoundTag var1, T var2, T var3, ExecutionControl<T> var4, Callbacks<T> var5, ChainModifiers var6) throws CommandSyntaxException {
      if (var6.isReturn()) {
         queueFunctionsAsReturn(var0, var1, var2, var3, var4, var5);
      } else {
         queueFunctionsNoReturn(var0, var1, var2, var3, var4, var5);
      }

   }

   private static <T extends ExecutionCommandSource<T>> void instantiateAndQueueFunctions(@Nullable CompoundTag var0, ExecutionControl<T> var1, CommandDispatcher<T> var2, T var3, CommandFunction<T> var4, ResourceLocation var5, CommandResultCallback var6, boolean var7) throws CommandSyntaxException {
      try {
         InstantiatedFunction var8 = var4.instantiate(var0, var2);
         var1.queueNext((new CallFunction(var8, var6, var7)).bind(var3));
      } catch (FunctionInstantiationException var9) {
         throw ERROR_FUNCTION_INSTANTATION_FAILURE.create(var5, var9.messageComponent());
      }
   }

   private static <T extends ExecutionCommandSource<T>> CommandResultCallback decorateOutputIfNeeded(T var0, Callbacks<T> var1, ResourceLocation var2, CommandResultCallback var3) {
      return var0.isSilent() ? var3 : (var4, var5) -> {
         var1.signalResult(var0, var2, var5);
         var3.onResult(var4, var5);
      };
   }

   private static <T extends ExecutionCommandSource<T>> void queueFunctionsAsReturn(Collection<CommandFunction<T>> var0, @Nullable CompoundTag var1, T var2, T var3, ExecutionControl<T> var4, Callbacks<T> var5) throws CommandSyntaxException {
      CommandDispatcher var6 = var2.dispatcher();
      ExecutionCommandSource var7 = var3.clearCallbacks();
      CommandResultCallback var8 = CommandResultCallback.chain(var2.callback(), var4.currentFrame().returnValueConsumer());
      Iterator var9 = var0.iterator();

      while(var9.hasNext()) {
         CommandFunction var10 = (CommandFunction)var9.next();
         ResourceLocation var11 = var10.id();
         CommandResultCallback var12 = decorateOutputIfNeeded(var2, var5, var11, var8);
         instantiateAndQueueFunctions(var1, var4, var6, var7, var10, var11, var12, true);
      }

      var4.queueNext(FallthroughTask.instance());
   }

   private static <T extends ExecutionCommandSource<T>> void queueFunctionsNoReturn(Collection<CommandFunction<T>> var0, @Nullable CompoundTag var1, T var2, T var3, ExecutionControl<T> var4, Callbacks<T> var5) throws CommandSyntaxException {
      CommandDispatcher var6 = var2.dispatcher();
      ExecutionCommandSource var7 = var3.clearCallbacks();
      CommandResultCallback var8 = var2.callback();
      if (!var0.isEmpty()) {
         if (var0.size() == 1) {
            CommandFunction var9 = (CommandFunction)var0.iterator().next();
            ResourceLocation var10 = var9.id();
            CommandResultCallback var11 = decorateOutputIfNeeded(var2, var5, var10, var8);
            instantiateAndQueueFunctions(var1, var4, var6, var7, var9, var10, var11, false);
         } else if (var8 == CommandResultCallback.EMPTY) {
            Iterator var15 = var0.iterator();

            while(var15.hasNext()) {
               CommandFunction var17 = (CommandFunction)var15.next();
               ResourceLocation var19 = var17.id();
               CommandResultCallback var12 = decorateOutputIfNeeded(var2, var5, var19, var8);
               instantiateAndQueueFunctions(var1, var4, var6, var7, var17, var19, var12, false);
            }
         } else {
            class 1Accumulator {
               boolean anyResult;
               int sum;

               _Accumulator/* $FF was: 1Accumulator*/() {
                  super();
               }

               public void add(int var1) {
                  this.anyResult = true;
                  this.sum += var1;
               }
            }

            1Accumulator var16 = new 1Accumulator();
            CommandResultCallback var18 = (var1x, var2x) -> {
               var16.add(var2x);
            };
            Iterator var20 = var0.iterator();

            while(var20.hasNext()) {
               CommandFunction var21 = (CommandFunction)var20.next();
               ResourceLocation var13 = var21.id();
               CommandResultCallback var14 = decorateOutputIfNeeded(var2, var5, var13, var18);
               instantiateAndQueueFunctions(var1, var4, var6, var7, var21, var13, var14, false);
            }

            var4.queueNext((var2x, var3x) -> {
               if (var16.anyResult) {
                  var8.onSuccess(var16.sum);
               }

            });
         }

      }
   }

   public interface Callbacks<T> {
      void signalResult(T var1, ResourceLocation var2, int var3);
   }

   abstract static class FunctionCustomExecutor extends CustomCommandExecutor.WithErrorHandling<CommandSourceStack> implements CustomCommandExecutor.CommandAdapter<CommandSourceStack> {
      FunctionCustomExecutor() {
         super();
      }

      @Nullable
      protected abstract CompoundTag arguments(CommandContext<CommandSourceStack> var1) throws CommandSyntaxException;

      public void runGuarded(CommandSourceStack var1, ContextChain<CommandSourceStack> var2, ChainModifiers var3, ExecutionControl<CommandSourceStack> var4) throws CommandSyntaxException {
         CommandContext var5 = var2.getTopContext().copyFor(var1);
         Pair var6 = FunctionArgument.getFunctionCollection(var5, "name");
         Collection var7 = (Collection)var6.getSecond();
         if (var7.isEmpty()) {
            throw FunctionCommand.ERROR_NO_FUNCTIONS.create(Component.translationArg((ResourceLocation)var6.getFirst()));
         } else {
            CompoundTag var8 = this.arguments(var5);
            CommandSourceStack var9 = FunctionCommand.modifySenderForExecution(var1);
            if (var7.size() == 1) {
               var1.sendSuccess(() -> {
                  return Component.translatable("commands.function.scheduled.single", Component.translationArg(((CommandFunction)var7.iterator().next()).id()));
               }, true);
            } else {
               var1.sendSuccess(() -> {
                  return Component.translatable("commands.function.scheduled.multiple", ComponentUtils.formatList(var7.stream().map(CommandFunction::id).toList(), (Function)(Component::translationArg)));
               }, true);
            }

            FunctionCommand.queueFunctions(var7, var8, var1, var9, var4, FunctionCommand.FULL_CONTEXT_CALLBACKS, var3);
         }
      }

      // $FF: synthetic method
      public void runGuarded(ExecutionCommandSource var1, ContextChain var2, ChainModifiers var3, ExecutionControl var4) throws CommandSyntaxException {
         this.runGuarded((CommandSourceStack)var1, var2, var3, var4);
      }
   }
}
