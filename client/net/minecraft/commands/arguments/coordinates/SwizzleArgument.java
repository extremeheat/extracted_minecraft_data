package net.minecraft.commands.arguments.coordinates;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import java.util.Arrays;
import java.util.Collection;
import java.util.EnumSet;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.TranslatableComponent;

public class SwizzleArgument implements ArgumentType<EnumSet<Direction.Axis>> {
   private static final Collection<String> EXAMPLES = Arrays.asList("xyz", "x");
   private static final SimpleCommandExceptionType ERROR_INVALID = new SimpleCommandExceptionType(new TranslatableComponent("arguments.swizzle.invalid"));

   public SwizzleArgument() {
      super();
   }

   public static SwizzleArgument swizzle() {
      return new SwizzleArgument();
   }

   public static EnumSet<Direction.Axis> getSwizzle(CommandContext<CommandSourceStack> var0, String var1) {
      return (EnumSet)var0.getArgument(var1, EnumSet.class);
   }

   public EnumSet<Direction.Axis> parse(StringReader var1) throws CommandSyntaxException {
      EnumSet var2 = EnumSet.noneOf(Direction.Axis.class);

      while(var1.canRead() && var1.peek() != ' ') {
         char var3 = var1.read();
         Direction.Axis var4;
         switch(var3) {
         case 'x':
            var4 = Direction.Axis.X;
            break;
         case 'y':
            var4 = Direction.Axis.Y;
            break;
         case 'z':
            var4 = Direction.Axis.Z;
            break;
         default:
            throw ERROR_INVALID.create();
         }

         if (var2.contains(var4)) {
            throw ERROR_INVALID.create();
         }

         var2.add(var4);
      }

      return var2;
   }

   public Collection<String> getExamples() {
      return EXAMPLES;
   }

   // $FF: synthetic method
   public Object parse(StringReader var1) throws CommandSyntaxException {
      return this.parse(var1);
   }
}
