package net.minecraft.command.arguments;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.DynamicCommandExceptionType;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import net.minecraft.command.CommandSource;
import net.minecraft.command.FunctionObject;
import net.minecraft.tags.Tag;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.TextComponentTranslation;

public class FunctionArgument implements ArgumentType<FunctionArgument.IResult> {
   private static final Collection<String> field_201338_a = Arrays.asList("foo", "foo:bar", "#foo");
   private static final DynamicCommandExceptionType field_200023_a = new DynamicCommandExceptionType((var0) -> {
      return new TextComponentTranslation("arguments.function.tag.unknown", new Object[]{var0});
   });
   private static final DynamicCommandExceptionType field_200024_b = new DynamicCommandExceptionType((var0) -> {
      return new TextComponentTranslation("arguments.function.unknown", new Object[]{var0});
   });

   public FunctionArgument() {
      super();
   }

   public static FunctionArgument func_200021_a() {
      return new FunctionArgument();
   }

   public FunctionArgument.IResult parse(StringReader var1) throws CommandSyntaxException {
      ResourceLocation var2;
      if (var1.canRead() && var1.peek() == '#') {
         var1.skip();
         var2 = ResourceLocation.func_195826_a(var1);
         return (var1x) -> {
            Tag var2x = ((CommandSource)var1x.getSource()).func_197028_i().func_193030_aL().func_200000_g().func_199910_a(var2);
            if (var2x == null) {
               throw field_200023_a.create(var2.toString());
            } else {
               return var2x.func_199885_a();
            }
         };
      } else {
         var2 = ResourceLocation.func_195826_a(var1);
         return (var1x) -> {
            FunctionObject var2x = ((CommandSource)var1x.getSource()).func_197028_i().func_193030_aL().func_193058_a(var2);
            if (var2x == null) {
               throw field_200024_b.create(var2.toString());
            } else {
               return Collections.singleton(var2x);
            }
         };
      }
   }

   public static Collection<FunctionObject> func_200022_a(CommandContext<CommandSource> var0, String var1) throws CommandSyntaxException {
      return ((FunctionArgument.IResult)var0.getArgument(var1, FunctionArgument.IResult.class)).create(var0);
   }

   public Collection<String> getExamples() {
      return field_201338_a;
   }

   // $FF: synthetic method
   public Object parse(StringReader var1) throws CommandSyntaxException {
      return this.parse(var1);
   }

   public interface IResult {
      Collection<FunctionObject> create(CommandContext<CommandSource> var1) throws CommandSyntaxException;
   }
}
