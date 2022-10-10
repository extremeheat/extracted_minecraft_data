package net.minecraft.command.arguments;

import com.google.gson.JsonParseException;
import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.DynamicCommandExceptionType;
import java.util.Arrays;
import java.util.Collection;
import net.minecraft.command.CommandSource;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentTranslation;

public class ComponentArgument implements ArgumentType<ITextComponent> {
   private static final Collection<String> field_201307_b = Arrays.asList("\"hello world\"", "\"\"", "\"{\"text\":\"hello world\"}", "[\"\"]");
   public static final DynamicCommandExceptionType field_197070_a = new DynamicCommandExceptionType((var0) -> {
      return new TextComponentTranslation("argument.component.invalid", new Object[]{var0});
   });

   private ComponentArgument() {
      super();
   }

   public static ITextComponent func_197068_a(CommandContext<CommandSource> var0, String var1) {
      return (ITextComponent)var0.getArgument(var1, ITextComponent.class);
   }

   public static ComponentArgument func_197067_a() {
      return new ComponentArgument();
   }

   public ITextComponent parse(StringReader var1) throws CommandSyntaxException {
      try {
         ITextComponent var2 = ITextComponent.Serializer.func_197671_a(var1);
         if (var2 == null) {
            throw field_197070_a.createWithContext(var1, "empty");
         } else {
            return var2;
         }
      } catch (JsonParseException var4) {
         String var3 = var4.getCause() != null ? var4.getCause().getMessage() : var4.getMessage();
         throw field_197070_a.createWithContext(var1, var3);
      }
   }

   public Collection<String> getExamples() {
      return field_201307_b;
   }

   // $FF: synthetic method
   public Object parse(StringReader var1) throws CommandSyntaxException {
      return this.parse(var1);
   }
}
