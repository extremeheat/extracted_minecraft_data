package net.minecraft.commands.arguments.item;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.DynamicCommandExceptionType;
import com.mojang.datafixers.util.Either;
import com.mojang.datafixers.util.Pair;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.functions.CommandFunction;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;

public class FunctionArgument implements ArgumentType<Result> {
   private static final Collection<String> EXAMPLES = Arrays.asList("foo", "foo:bar", "#foo");
   private static final DynamicCommandExceptionType ERROR_UNKNOWN_TAG = new DynamicCommandExceptionType((var0) -> {
      return Component.translatableEscape("arguments.function.tag.unknown", var0);
   });
   private static final DynamicCommandExceptionType ERROR_UNKNOWN_FUNCTION = new DynamicCommandExceptionType((var0) -> {
      return Component.translatableEscape("arguments.function.unknown", var0);
   });

   public FunctionArgument() {
      super();
   }

   public static FunctionArgument functions() {
      return new FunctionArgument();
   }

   public Result parse(StringReader var1) throws CommandSyntaxException {
      final ResourceLocation var2;
      if (var1.canRead() && var1.peek() == '#') {
         var1.skip();
         var2 = ResourceLocation.read(var1);
         return new Result(this) {
            public Collection<CommandFunction<CommandSourceStack>> create(CommandContext<CommandSourceStack> var1) throws CommandSyntaxException {
               return FunctionArgument.getFunctionTag(var1, var2);
            }

            public Pair<ResourceLocation, Either<CommandFunction<CommandSourceStack>, Collection<CommandFunction<CommandSourceStack>>>> unwrap(CommandContext<CommandSourceStack> var1) throws CommandSyntaxException {
               return Pair.of(var2, Either.right(FunctionArgument.getFunctionTag(var1, var2)));
            }

            public Pair<ResourceLocation, Collection<CommandFunction<CommandSourceStack>>> unwrapToCollection(CommandContext<CommandSourceStack> var1) throws CommandSyntaxException {
               return Pair.of(var2, FunctionArgument.getFunctionTag(var1, var2));
            }
         };
      } else {
         var2 = ResourceLocation.read(var1);
         return new Result(this) {
            public Collection<CommandFunction<CommandSourceStack>> create(CommandContext<CommandSourceStack> var1) throws CommandSyntaxException {
               return Collections.singleton(FunctionArgument.getFunction(var1, var2));
            }

            public Pair<ResourceLocation, Either<CommandFunction<CommandSourceStack>, Collection<CommandFunction<CommandSourceStack>>>> unwrap(CommandContext<CommandSourceStack> var1) throws CommandSyntaxException {
               return Pair.of(var2, Either.left(FunctionArgument.getFunction(var1, var2)));
            }

            public Pair<ResourceLocation, Collection<CommandFunction<CommandSourceStack>>> unwrapToCollection(CommandContext<CommandSourceStack> var1) throws CommandSyntaxException {
               return Pair.of(var2, Collections.singleton(FunctionArgument.getFunction(var1, var2)));
            }
         };
      }
   }

   static CommandFunction<CommandSourceStack> getFunction(CommandContext<CommandSourceStack> var0, ResourceLocation var1) throws CommandSyntaxException {
      return (CommandFunction)((CommandSourceStack)var0.getSource()).getServer().getFunctions().get(var1).orElseThrow(() -> {
         return ERROR_UNKNOWN_FUNCTION.create(var1.toString());
      });
   }

   static Collection<CommandFunction<CommandSourceStack>> getFunctionTag(CommandContext<CommandSourceStack> var0, ResourceLocation var1) throws CommandSyntaxException {
      Collection var2 = ((CommandSourceStack)var0.getSource()).getServer().getFunctions().getTag(var1);
      if (var2 == null) {
         throw ERROR_UNKNOWN_TAG.create(var1.toString());
      } else {
         return var2;
      }
   }

   public static Collection<CommandFunction<CommandSourceStack>> getFunctions(CommandContext<CommandSourceStack> var0, String var1) throws CommandSyntaxException {
      return ((Result)var0.getArgument(var1, Result.class)).create(var0);
   }

   public static Pair<ResourceLocation, Either<CommandFunction<CommandSourceStack>, Collection<CommandFunction<CommandSourceStack>>>> getFunctionOrTag(CommandContext<CommandSourceStack> var0, String var1) throws CommandSyntaxException {
      return ((Result)var0.getArgument(var1, Result.class)).unwrap(var0);
   }

   public static Pair<ResourceLocation, Collection<CommandFunction<CommandSourceStack>>> getFunctionCollection(CommandContext<CommandSourceStack> var0, String var1) throws CommandSyntaxException {
      return ((Result)var0.getArgument(var1, Result.class)).unwrapToCollection(var0);
   }

   public Collection<String> getExamples() {
      return EXAMPLES;
   }

   // $FF: synthetic method
   public Object parse(final StringReader var1) throws CommandSyntaxException {
      return this.parse(var1);
   }

   public interface Result {
      Collection<CommandFunction<CommandSourceStack>> create(CommandContext<CommandSourceStack> var1) throws CommandSyntaxException;

      Pair<ResourceLocation, Either<CommandFunction<CommandSourceStack>, Collection<CommandFunction<CommandSourceStack>>>> unwrap(CommandContext<CommandSourceStack> var1) throws CommandSyntaxException;

      Pair<ResourceLocation, Collection<CommandFunction<CommandSourceStack>>> unwrapToCollection(CommandContext<CommandSourceStack> var1) throws CommandSyntaxException;
   }
}
