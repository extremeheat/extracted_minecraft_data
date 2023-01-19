package net.minecraft.commands.arguments;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.DynamicCommandExceptionType;
import java.util.Arrays;
import java.util.Collection;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;

public class ComponentArgument implements ArgumentType<Component> {
   private static final Collection<String> EXAMPLES = Arrays.asList("\"hello world\"", "\"\"", "\"{\"text\":\"hello world\"}", "[\"\"]");
   public static final DynamicCommandExceptionType ERROR_INVALID_JSON = new DynamicCommandExceptionType(
      var0 -> Component.translatable("argument.component.invalid", var0)
   );

   private ComponentArgument() {
      super();
   }

   public static Component getComponent(CommandContext<CommandSourceStack> var0, String var1) {
      return (Component)var0.getArgument(var1, Component.class);
   }

   public static ComponentArgument textComponent() {
      return new ComponentArgument();
   }

   public Component parse(StringReader var1) throws CommandSyntaxException {
      try {
         MutableComponent var2 = Component.Serializer.fromJson(var1);
         if (var2 == null) {
            throw ERROR_INVALID_JSON.createWithContext(var1, "empty");
         } else {
            return var2;
         }
      } catch (Exception var4) {
         String var3 = var4.getCause() != null ? var4.getCause().getMessage() : var4.getMessage();
         throw ERROR_INVALID_JSON.createWithContext(var1, var3);
      }
   }

   public Collection<String> getExamples() {
      return EXAMPLES;
   }
}
