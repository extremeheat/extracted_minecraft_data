package net.minecraft.commands.arguments.item;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.DynamicCommandExceptionType;
import com.mojang.datafixers.util.Either;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import net.minecraft.commands.CommandFunction;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.Tag;

public class FunctionArgument implements ArgumentType {
   private static final Collection EXAMPLES = Arrays.asList("foo", "foo:bar", "#foo");
   private static final DynamicCommandExceptionType ERROR_UNKNOWN_TAG = new DynamicCommandExceptionType((var0) -> {
      return new TranslatableComponent("arguments.function.tag.unknown", new Object[]{var0});
   });
   private static final DynamicCommandExceptionType ERROR_UNKNOWN_FUNCTION = new DynamicCommandExceptionType((var0) -> {
      return new TranslatableComponent("arguments.function.unknown", new Object[]{var0});
   });

   public static FunctionArgument functions() {
      return new FunctionArgument();
   }

   public FunctionArgument.Result parse(StringReader var1) throws CommandSyntaxException {
      final ResourceLocation var2;
      if (var1.canRead() && var1.peek() == '#') {
         var1.skip();
         var2 = ResourceLocation.read(var1);
         return new FunctionArgument.Result() {
            public Collection create(CommandContext var1) throws CommandSyntaxException {
               Tag var2x = FunctionArgument.getFunctionTag(var1, var2);
               return var2x.getValues();
            }

            public Either unwrap(CommandContext var1) throws CommandSyntaxException {
               return Either.right(FunctionArgument.getFunctionTag(var1, var2));
            }
         };
      } else {
         var2 = ResourceLocation.read(var1);
         return new FunctionArgument.Result() {
            public Collection create(CommandContext var1) throws CommandSyntaxException {
               return Collections.singleton(FunctionArgument.getFunction(var1, var2));
            }

            public Either unwrap(CommandContext var1) throws CommandSyntaxException {
               return Either.left(FunctionArgument.getFunction(var1, var2));
            }
         };
      }
   }

   private static CommandFunction getFunction(CommandContext var0, ResourceLocation var1) throws CommandSyntaxException {
      return (CommandFunction)((CommandSourceStack)var0.getSource()).getServer().getFunctions().get(var1).orElseThrow(() -> {
         return ERROR_UNKNOWN_FUNCTION.create(var1.toString());
      });
   }

   private static Tag getFunctionTag(CommandContext var0, ResourceLocation var1) throws CommandSyntaxException {
      Tag var2 = ((CommandSourceStack)var0.getSource()).getServer().getFunctions().getTags().getTag(var1);
      if (var2 == null) {
         throw ERROR_UNKNOWN_TAG.create(var1.toString());
      } else {
         return var2;
      }
   }

   public static Collection getFunctions(CommandContext var0, String var1) throws CommandSyntaxException {
      return ((FunctionArgument.Result)var0.getArgument(var1, FunctionArgument.Result.class)).create(var0);
   }

   public static Either getFunctionOrTag(CommandContext var0, String var1) throws CommandSyntaxException {
      return ((FunctionArgument.Result)var0.getArgument(var1, FunctionArgument.Result.class)).unwrap(var0);
   }

   public Collection getExamples() {
      return EXAMPLES;
   }

   // $FF: synthetic method
   public Object parse(StringReader var1) throws CommandSyntaxException {
      return this.parse(var1);
   }

   public interface Result {
      Collection create(CommandContext var1) throws CommandSyntaxException;

      Either unwrap(CommandContext var1) throws CommandSyntaxException;
   }
}
