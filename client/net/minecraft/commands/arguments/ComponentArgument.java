package net.minecraft.commands.arguments;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.DynamicCommandExceptionType;
import java.util.Arrays;
import java.util.Collection;
import net.minecraft.commands.CommandBuildContext;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.ParserUtils;
import net.minecraft.core.HolderLookup;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.ComponentSerialization;

public class ComponentArgument implements ArgumentType<Component> {
   private static final Collection<String> EXAMPLES = Arrays.asList("\"hello world\"", "\"\"", "\"{\"text\":\"hello world\"}", "[\"\"]");
   public static final DynamicCommandExceptionType ERROR_INVALID_JSON = new DynamicCommandExceptionType((var0) -> {
      return Component.translatableEscape("argument.component.invalid", var0);
   });
   private final HolderLookup.Provider registries;

   private ComponentArgument(HolderLookup.Provider var1) {
      super();
      this.registries = var1;
   }

   public static Component getComponent(CommandContext<CommandSourceStack> var0, String var1) {
      return (Component)var0.getArgument(var1, Component.class);
   }

   public static ComponentArgument textComponent(CommandBuildContext var0) {
      return new ComponentArgument(var0);
   }

   public Component parse(StringReader var1) throws CommandSyntaxException {
      try {
         return (Component)ParserUtils.parseJson(this.registries, var1, ComponentSerialization.CODEC);
      } catch (Exception var4) {
         String var3 = var4.getCause() != null ? var4.getCause().getMessage() : var4.getMessage();
         throw ERROR_INVALID_JSON.createWithContext(var1, var3);
      }
   }

   public Collection<String> getExamples() {
      return EXAMPLES;
   }

   // $FF: synthetic method
   public Object parse(StringReader var1) throws CommandSyntaxException {
      return this.parse(var1);
   }
}
