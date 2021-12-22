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
import net.minecraft.commands.CommandFunction;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.Tag;

public class FunctionArgument implements ArgumentType<FunctionArgument.Result> {
   private static final Collection<String> EXAMPLES = Arrays.asList("foo", "foo:bar", "#foo");
   private static final DynamicCommandExceptionType ERROR_UNKNOWN_TAG = new DynamicCommandExceptionType((var0) -> {
      return new TranslatableComponent("arguments.function.tag.unknown", new Object[]{var0});
   });
   private static final DynamicCommandExceptionType ERROR_UNKNOWN_FUNCTION = new DynamicCommandExceptionType((var0) -> {
      return new TranslatableComponent("arguments.function.unknown", new Object[]{var0});
   });

   public FunctionArgument() {
      super();
   }

   public static FunctionArgument functions() {
      return new FunctionArgument();
   }

   public FunctionArgument.Result parse(StringReader var1) throws CommandSyntaxException {
      final ResourceLocation var2;
      if (var1.canRead() && var1.peek() == '#') {
         var1.skip();
         var2 = ResourceLocation.read(var1);
         return new FunctionArgument.Result() {
            public Collection<CommandFunction> create(CommandContext<CommandSourceStack> var1) throws CommandSyntaxException {
               Tag var2x = FunctionArgument.getFunctionTag(var1, var2);
               return var2x.getValues();
            }

            public Pair<ResourceLocation, Either<CommandFunction, Tag<CommandFunction>>> unwrap(CommandContext<CommandSourceStack> var1) throws CommandSyntaxException {
               return Pair.of(var2, Either.right(FunctionArgument.getFunctionTag(var1, var2)));
            }
         };
      } else {
         var2 = ResourceLocation.read(var1);
         return new FunctionArgument.Result() {
            public Collection<CommandFunction> create(CommandContext<CommandSourceStack> var1) throws CommandSyntaxException {
               return Collections.singleton(FunctionArgument.getFunction(var1, var2));
            }

            public Pair<ResourceLocation, Either<CommandFunction, Tag<CommandFunction>>> unwrap(CommandContext<CommandSourceStack> var1) throws CommandSyntaxException {
               return Pair.of(var2, Either.left(FunctionArgument.getFunction(var1, var2)));
            }
         };
      }
   }

   static CommandFunction getFunction(CommandContext<CommandSourceStack> var0, ResourceLocation var1) throws CommandSyntaxException {
      return (CommandFunction)((CommandSourceStack)var0.getSource()).getServer().getFunctions().get(var1).orElseThrow(() -> {
         return ERROR_UNKNOWN_FUNCTION.create(var1.toString());
      });
   }

   static Tag<CommandFunction> getFunctionTag(CommandContext<CommandSourceStack> var0, ResourceLocation var1) throws CommandSyntaxException {
      Tag var2 = ((CommandSourceStack)var0.getSource()).getServer().getFunctions().getTag(var1);
      if (var2 == null) {
         throw ERROR_UNKNOWN_TAG.create(var1.toString());
      } else {
         return var2;
      }
   }

   public static Collection<CommandFunction> getFunctions(CommandContext<CommandSourceStack> var0, String var1) throws CommandSyntaxException {
      return ((FunctionArgument.Result)var0.getArgument(var1, FunctionArgument.Result.class)).create(var0);
   }

   public static Pair<ResourceLocation, Either<CommandFunction, Tag<CommandFunction>>> getFunctionOrTag(CommandContext<CommandSourceStack> var0, String var1) throws CommandSyntaxException {
      return ((FunctionArgument.Result)var0.getArgument(var1, FunctionArgument.Result.class)).unwrap(var0);
   }

   public Collection<String> getExamples() {
      return EXAMPLES;
   }

   // $FF: synthetic method
   public Object parse(StringReader var1) throws CommandSyntaxException {
      return this.parse(var1);
   }

   public interface Result {
      Collection<CommandFunction> create(CommandContext<CommandSourceStack> var1) throws CommandSyntaxException;

      Pair<ResourceLocation, Either<CommandFunction, Tag<CommandFunction>>> unwrap(CommandContext<CommandSourceStack> var1) throws CommandSyntaxException;
   }
}
