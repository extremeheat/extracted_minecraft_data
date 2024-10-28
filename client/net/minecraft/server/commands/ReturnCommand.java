package net.minecraft.server.commands;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.builder.RequiredArgumentBuilder;
import com.mojang.brigadier.context.ContextChain;
import java.util.List;
import net.minecraft.commands.ExecutionCommandSource;
import net.minecraft.commands.execution.ChainModifiers;
import net.minecraft.commands.execution.CustomCommandExecutor;
import net.minecraft.commands.execution.CustomModifierExecutor;
import net.minecraft.commands.execution.ExecutionControl;
import net.minecraft.commands.execution.Frame;
import net.minecraft.commands.execution.tasks.BuildContexts;
import net.minecraft.commands.execution.tasks.FallthroughTask;

public class ReturnCommand {
   public ReturnCommand() {
      super();
   }

   public static <T extends ExecutionCommandSource<T>> void register(CommandDispatcher<T> var0) {
      var0.register((LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)LiteralArgumentBuilder.literal("return").requires((var0x) -> {
         return var0x.hasPermission(2);
      })).then(RequiredArgumentBuilder.argument("value", IntegerArgumentType.integer()).executes(new ReturnValueCustomExecutor()))).then(LiteralArgumentBuilder.literal("fail").executes(new ReturnFailCustomExecutor()))).then(LiteralArgumentBuilder.literal("run").forward(var0.getRoot(), new ReturnFromCommandCustomModifier(), false)));
   }

   static class ReturnValueCustomExecutor<T extends ExecutionCommandSource<T>> implements CustomCommandExecutor.CommandAdapter<T> {
      ReturnValueCustomExecutor() {
         super();
      }

      public void run(T var1, ContextChain<T> var2, ChainModifiers var3, ExecutionControl<T> var4) {
         int var5 = IntegerArgumentType.getInteger(var2.getTopContext(), "value");
         var1.callback().onSuccess(var5);
         Frame var6 = var4.currentFrame();
         var6.returnSuccess(var5);
         var6.discard();
      }
   }

   static class ReturnFailCustomExecutor<T extends ExecutionCommandSource<T>> implements CustomCommandExecutor.CommandAdapter<T> {
      ReturnFailCustomExecutor() {
         super();
      }

      public void run(T var1, ContextChain<T> var2, ChainModifiers var3, ExecutionControl<T> var4) {
         var1.callback().onFailure();
         Frame var5 = var4.currentFrame();
         var5.returnFailure();
         var5.discard();
      }
   }

   static class ReturnFromCommandCustomModifier<T extends ExecutionCommandSource<T>> implements CustomModifierExecutor.ModifierAdapter<T> {
      ReturnFromCommandCustomModifier() {
         super();
      }

      public void apply(T var1, List<T> var2, ContextChain<T> var3, ChainModifiers var4, ExecutionControl<T> var5) {
         if (var2.isEmpty()) {
            if (var4.isReturn()) {
               var5.queueNext(FallthroughTask.instance());
            }

         } else {
            var5.currentFrame().discard();
            ContextChain var6 = var3.nextStage();
            String var7 = var6.getTopContext().getInput();
            var5.queueNext(new BuildContexts.Continuation(var7, var6, var4.setReturn(), var1, var2));
         }
      }
   }
}
