package net.minecraft.commands.arguments;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import java.util.Arrays;
import java.util.Collection;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.arguments.coordinates.WorldCoordinate;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.util.Mth;

public class AngleArgument implements ArgumentType<AngleArgument.SingleAngle> {
   private static final Collection<String> EXAMPLES = Arrays.asList("0", "~", "~-5");
   public static final SimpleCommandExceptionType ERROR_NOT_COMPLETE = new SimpleCommandExceptionType(new TranslatableComponent("argument.angle.incomplete"));

   public AngleArgument() {
      super();
   }

   public static AngleArgument angle() {
      return new AngleArgument();
   }

   public static float getAngle(CommandContext<CommandSourceStack> var0, String var1) {
      return ((AngleArgument.SingleAngle)var0.getArgument(var1, AngleArgument.SingleAngle.class)).getAngle((CommandSourceStack)var0.getSource());
   }

   public AngleArgument.SingleAngle parse(StringReader var1) throws CommandSyntaxException {
      if (!var1.canRead()) {
         throw ERROR_NOT_COMPLETE.createWithContext(var1);
      } else {
         boolean var2 = WorldCoordinate.isRelative(var1);
         float var3 = var1.canRead() && var1.peek() != ' ' ? var1.readFloat() : 0.0F;
         return new AngleArgument.SingleAngle(var3, var2);
      }
   }

   public Collection<String> getExamples() {
      return EXAMPLES;
   }

   // $FF: synthetic method
   public Object parse(StringReader var1) throws CommandSyntaxException {
      return this.parse(var1);
   }

   public static final class SingleAngle {
      private final float angle;
      private final boolean isRelative;

      private SingleAngle(float var1, boolean var2) {
         super();
         this.angle = var1;
         this.isRelative = var2;
      }

      public float getAngle(CommandSourceStack var1) {
         return Mth.wrapDegrees(this.isRelative ? this.angle + var1.getRotation().y : this.angle);
      }

      // $FF: synthetic method
      SingleAngle(float var1, boolean var2, Object var3) {
         this(var1, var2);
      }
   }
}
