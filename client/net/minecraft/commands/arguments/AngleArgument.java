package net.minecraft.commands.arguments;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import java.util.Collection;
import java.util.List;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.arguments.coordinates.WorldCoordinate;
import net.minecraft.network.chat.Component;
import net.minecraft.util.Mth;

public class AngleArgument implements SerializableArgument.ContextFree<SingleAngle> {
   private static final AngleArgument INSTANCE = new AngleArgument();
   public static final SerializableArgument.Type<AngleArgument> TYPE;
   private static final Collection<String> EXAMPLES;
   public static final SimpleCommandExceptionType ERROR_NOT_COMPLETE;
   public static final SimpleCommandExceptionType ERROR_INVALID_ANGLE;

   private AngleArgument() {
      super();
   }

   public SerializableArgument.Type<AngleArgument> type() {
      return TYPE;
   }

   public static ArgumentType<SingleAngle> angle() {
      return INSTANCE.toBrigadier();
   }

   public static float getAngle(final CommandContext<CommandSourceStack> context, final String name) {
      return ((SingleAngle)context.getArgument(name, SingleAngle.class)).getAngle((CommandSourceStack)context.getSource());
   }

   public SingleAngle parse(final StringReader reader) throws CommandSyntaxException {
      if (!reader.canRead()) {
         throw ERROR_NOT_COMPLETE.createWithContext(reader);
      } else {
         boolean isRelative = WorldCoordinate.isRelative(reader);
         float value = reader.canRead() && reader.peek() != ' ' ? reader.readFloat() : 0.0F;
         if (!Float.isNaN(value) && !Float.isInfinite(value)) {
            return new SingleAngle(value, isRelative);
         } else {
            throw ERROR_INVALID_ANGLE.createWithContext(reader);
         }
      }
   }

   public Collection<String> getExamples() {
      return EXAMPLES;
   }

   static {
      TYPE = SerializableArgument.<AngleArgument>singletonType(INSTANCE);
      EXAMPLES = List.of("0", "~", "~-5");
      ERROR_NOT_COMPLETE = new SimpleCommandExceptionType(Component.translatable("argument.angle.incomplete"));
      ERROR_INVALID_ANGLE = new SimpleCommandExceptionType(Component.translatable("argument.angle.invalid"));
   }

   public static final class SingleAngle {
      private final float angle;
      private final boolean isRelative;

      private SingleAngle(final float angle, final boolean isRelative) {
         super();
         this.angle = angle;
         this.isRelative = isRelative;
      }

      public float getAngle(final CommandSourceStack sender) {
         return Mth.wrapDegrees(this.isRelative ? this.angle + sender.getRotation().y : this.angle);
      }
   }
}
