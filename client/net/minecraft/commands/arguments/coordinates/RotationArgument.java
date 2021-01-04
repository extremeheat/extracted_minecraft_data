package net.minecraft.commands.arguments.coordinates;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import java.util.Arrays;
import java.util.Collection;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.network.chat.TranslatableComponent;

public class RotationArgument implements ArgumentType<Coordinates> {
   private static final Collection<String> EXAMPLES = Arrays.asList("0 0", "~ ~", "~-5 ~5");
   public static final SimpleCommandExceptionType ERROR_NOT_COMPLETE = new SimpleCommandExceptionType(new TranslatableComponent("argument.rotation.incomplete", new Object[0]));

   public RotationArgument() {
      super();
   }

   public static RotationArgument rotation() {
      return new RotationArgument();
   }

   public static Coordinates getRotation(CommandContext<CommandSourceStack> var0, String var1) {
      return (Coordinates)var0.getArgument(var1, Coordinates.class);
   }

   public Coordinates parse(StringReader var1) throws CommandSyntaxException {
      int var2 = var1.getCursor();
      if (!var1.canRead()) {
         throw ERROR_NOT_COMPLETE.createWithContext(var1);
      } else {
         WorldCoordinate var3 = WorldCoordinate.parseDouble(var1, false);
         if (var1.canRead() && var1.peek() == ' ') {
            var1.skip();
            WorldCoordinate var4 = WorldCoordinate.parseDouble(var1, false);
            return new WorldCoordinates(var4, var3, new WorldCoordinate(true, 0.0D));
         } else {
            var1.setCursor(var2);
            throw ERROR_NOT_COMPLETE.createWithContext(var1);
         }
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
