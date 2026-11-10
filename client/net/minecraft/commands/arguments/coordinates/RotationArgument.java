package net.minecraft.commands.arguments.coordinates;

import com.google.common.annotations.VisibleForTesting;
import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import java.util.Collection;
import java.util.List;
import net.minecraft.commands.arguments.SerializableArgument;
import net.minecraft.network.chat.Component;

public class RotationArgument implements SerializableArgument.ContextFree<Coordinates> {
   @VisibleForTesting
   public static final RotationArgument INSTANCE = new RotationArgument();
   public static final SerializableArgument.Type<RotationArgument> TYPE;
   private static final Collection<String> EXAMPLES;
   public static final SimpleCommandExceptionType ERROR_NOT_COMPLETE;

   private RotationArgument() {
      super();
   }

   public SerializableArgument.Type<RotationArgument> type() {
      return TYPE;
   }

   public static ArgumentType<Coordinates> rotation() {
      return INSTANCE.toBrigadier();
   }

   public static Coordinates getRotation(final CommandContext<?> context, final String name) {
      return (Coordinates)context.getArgument(name, Coordinates.class);
   }

   public Coordinates parse(final StringReader reader) throws CommandSyntaxException {
      int start = reader.getCursor();
      if (!reader.canRead()) {
         throw ERROR_NOT_COMPLETE.createWithContext(reader);
      } else {
         WorldCoordinate y = WorldCoordinate.parseDouble(reader, false);
         if (reader.canRead() && reader.peek() == ' ') {
            reader.skip();
            WorldCoordinate x = WorldCoordinate.parseDouble(reader, false);
            return new WorldCoordinates(x, y, new WorldCoordinate(true, 0.0));
         } else {
            reader.setCursor(start);
            throw ERROR_NOT_COMPLETE.createWithContext(reader);
         }
      }
   }

   public Collection<String> getExamples() {
      return EXAMPLES;
   }

   static {
      TYPE = SerializableArgument.<RotationArgument>singletonType(INSTANCE);
      EXAMPLES = List.of("0 0", "~ ~", "~-5 ~5");
      ERROR_NOT_COMPLETE = new SimpleCommandExceptionType(Component.translatable("argument.rotation.incomplete"));
   }
}
