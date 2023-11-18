package net.minecraft.server.commands;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.builder.RequiredArgumentBuilder;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.DynamicCommandExceptionType;
import com.mojang.brigadier.suggestion.SuggestionProvider;
import java.util.Collection;
import javax.annotation.Nullable;
import net.minecraft.commands.CommandFunction;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.FunctionInstantiationException;
import net.minecraft.commands.SharedSuggestionProvider;
import net.minecraft.commands.arguments.CompoundTagArgument;
import net.minecraft.commands.arguments.NbtPathArgument;
import net.minecraft.commands.arguments.item.FunctionArgument;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.Component;
import net.minecraft.server.ServerFunctionManager;
import net.minecraft.server.commands.data.DataAccessor;
import net.minecraft.server.commands.data.DataCommands;
import org.apache.commons.lang3.mutable.MutableObject;

public class FunctionCommand {
   private static final DynamicCommandExceptionType ERROR_ARGUMENT_NOT_COMPOUND = new DynamicCommandExceptionType(
      var0 -> Component.translatable("commands.function.error.argument_not_compound", var0)
   );
   public static final SuggestionProvider<CommandSourceStack> SUGGEST_FUNCTION = (var0, var1) -> {
      ServerFunctionManager var2 = ((CommandSourceStack)var0.getSource()).getServer().getFunctions();
      SharedSuggestionProvider.suggestResource(var2.getTagNames(), var1, "#");
      return SharedSuggestionProvider.suggestResource(var2.getFunctionNames(), var1);
   };

   public FunctionCommand() {
      super();
   }

   public static void register(CommandDispatcher<CommandSourceStack> var0) {
      LiteralArgumentBuilder var1 = Commands.literal("with");

      for(DataCommands.DataProvider var3 : DataCommands.SOURCE_PROVIDERS) {
         var3.wrap(
            var1,
            var1x -> var1x.executes(
                     var1xx -> runFunction(
                           (CommandSourceStack)var1xx.getSource(), FunctionArgument.getFunctions(var1xx, "name"), var3.access(var1xx).getData()
                        )
                  )
                  .then(
                     Commands.argument("path", NbtPathArgument.nbtPath())
                        .executes(
                           var1xx -> runFunction(
                                 (CommandSourceStack)var1xx.getSource(),
                                 FunctionArgument.getFunctions(var1xx, "name"),
                                 getArgumentTag(NbtPathArgument.getPath(var1xx, "path"), var3.access(var1xx))
                              )
                        )
                  )
         );
      }

      var0.register(
         (LiteralArgumentBuilder)((LiteralArgumentBuilder)Commands.literal("function").requires(var0x -> var0x.hasPermission(2)))
            .then(
               ((RequiredArgumentBuilder)((RequiredArgumentBuilder)Commands.argument("name", FunctionArgument.functions())
                        .suggests(SUGGEST_FUNCTION)
                        .executes(var0x -> runFunction((CommandSourceStack)var0x.getSource(), FunctionArgument.getFunctions(var0x, "name"), null)))
                     .then(
                        Commands.argument("arguments", CompoundTagArgument.compoundTag())
                           .executes(
                              var0x -> runFunction(
                                    (CommandSourceStack)var0x.getSource(),
                                    FunctionArgument.getFunctions(var0x, "name"),
                                    CompoundTagArgument.getCompoundTag(var0x, "arguments")
                                 )
                           )
                     ))
                  .then(var1)
            )
      );
   }

   private static CompoundTag getArgumentTag(NbtPathArgument.NbtPath var0, DataAccessor var1) throws CommandSyntaxException {
      Tag var2 = DataCommands.getSingleTag(var0, var1);
      if (var2 instanceof CompoundTag) {
         return (CompoundTag)var2;
      } else {
         throw ERROR_ARGUMENT_NOT_COMPOUND.create(var2.getType().getName());
      }
   }

   private static int runFunction(CommandSourceStack var0, Collection<CommandFunction> var1, @Nullable CompoundTag var2) {
      int var3 = 0;
      boolean var4 = false;
      boolean var5 = false;

      for(CommandFunction var7 : var1) {
         try {
            FunctionCommand.FunctionResult var8 = runFunction(var0, var7, var2);
            var3 += var8.value();
            var4 |= var8.isReturn();
            var5 = true;
         } catch (FunctionInstantiationException var9) {
            var0.sendFailure(var9.messageComponent());
         }
      }

      if (var5) {
         int var10 = var3;
         if (var1.size() == 1) {
            if (var4) {
               var0.sendSuccess(
                  () -> Component.translatable("commands.function.success.single.result", var10, ((CommandFunction)var1.iterator().next()).getId()), true
               );
            } else {
               var0.sendSuccess(
                  () -> Component.translatable("commands.function.success.single", var10, ((CommandFunction)var1.iterator().next()).getId()), true
               );
            }
         } else if (var4) {
            var0.sendSuccess(() -> Component.translatable("commands.function.success.multiple.result", var1.size()), true);
         } else {
            var0.sendSuccess(() -> Component.translatable("commands.function.success.multiple", var10, var1.size()), true);
         }
      }

      return var3;
   }

   public static FunctionCommand.FunctionResult runFunction(CommandSourceStack var0, CommandFunction var1, @Nullable CompoundTag var2) throws FunctionInstantiationException {
      MutableObject var3 = new MutableObject();
      int var4 = var0.getServer()
         .getFunctions()
         .execute(
            var1,
            var0.withSuppressedOutput()
               .withMaximumPermission(2)
               .withReturnValueConsumer(var1x -> var3.setValue(new FunctionCommand.FunctionResult(var1x, true))),
            null,
            var2
         );
      FunctionCommand.FunctionResult var5 = (FunctionCommand.FunctionResult)var3.getValue();
      return var5 != null ? var5 : new FunctionCommand.FunctionResult(var4, false);
   }

   public static record FunctionResult(int a, boolean b) {
      private final int value;
      private final boolean isReturn;

      public FunctionResult(int var1, boolean var2) {
         super();
         this.value = var1;
         this.isReturn = var2;
      }
   }
}
