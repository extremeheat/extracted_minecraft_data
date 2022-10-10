package net.minecraft.command.arguments;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import java.util.Arrays;
import java.util.Collection;
import net.minecraft.command.CommandSource;
import net.minecraft.util.text.TextComponentTranslation;

public class RotationArgument implements ArgumentType<ILocationArgument> {
   private static final Collection<String> field_201334_b = Arrays.asList("0 0", "~ ~", "~-5 ~5");
   public static final SimpleCommandExceptionType field_197290_a = new SimpleCommandExceptionType(new TextComponentTranslation("argument.rotation.incomplete", new Object[0]));

   public RotationArgument() {
      super();
   }

   public static RotationArgument func_197288_a() {
      return new RotationArgument();
   }

   public static ILocationArgument func_200384_a(CommandContext<CommandSource> var0, String var1) {
      return (ILocationArgument)var0.getArgument(var1, ILocationArgument.class);
   }

   public ILocationArgument parse(StringReader var1) throws CommandSyntaxException {
      int var2 = var1.getCursor();
      if (!var1.canRead()) {
         throw field_197290_a.createWithContext(var1);
      } else {
         LocationPart var3 = LocationPart.func_197308_a(var1, false);
         if (var1.canRead() && var1.peek() == ' ') {
            var1.skip();
            LocationPart var4 = LocationPart.func_197308_a(var1, false);
            return new LocationInput(var4, var3, new LocationPart(true, 0.0D));
         } else {
            var1.setCursor(var2);
            throw field_197290_a.createWithContext(var1);
         }
      }
   }

   public Collection<String> getExamples() {
      return field_201334_b;
   }

   // $FF: synthetic method
   public Object parse(StringReader var1) throws CommandSyntaxException {
      return this.parse(var1);
   }
}
