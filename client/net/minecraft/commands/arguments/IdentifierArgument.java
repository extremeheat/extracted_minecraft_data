package net.minecraft.commands.arguments;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import java.util.Arrays;
import java.util.Collection;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.resources.Identifier;

public class IdentifierArgument implements ArgumentType<Identifier> {
   private static final Collection<String> EXAMPLES = Arrays.asList("foo", "foo:bar", "012");

   public IdentifierArgument() {
      super();
   }

   public static IdentifierArgument id() {
      return new IdentifierArgument();
   }

   public static Identifier getId(CommandContext<CommandSourceStack> var0, String var1) {
      return (Identifier)var0.getArgument(var1, Identifier.class);
   }

   public Identifier parse(StringReader var1) throws CommandSyntaxException {
      return Identifier.read(var1);
   }

   public Collection<String> getExamples() {
      return EXAMPLES;
   }

   // $FF: synthetic method
   public Object parse(final StringReader var1) throws CommandSyntaxException {
      return this.parse(var1);
   }
}
