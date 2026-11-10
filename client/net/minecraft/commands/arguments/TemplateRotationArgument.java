package net.minecraft.commands.arguments;

import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.context.CommandContext;
import net.minecraft.world.level.block.Rotation;

public class TemplateRotationArgument extends StringRepresentableArgument<Rotation> {
   private static final TemplateRotationArgument INSTANCE = new TemplateRotationArgument();
   public static final SerializableArgument.Type<TemplateRotationArgument> TYPE;

   private TemplateRotationArgument() {
      super(Rotation.CODEC, Rotation::values);
   }

   public SerializableArgument.Type<TemplateRotationArgument> type() {
      return TYPE;
   }

   public static ArgumentType<Rotation> templateRotation() {
      return INSTANCE.toBrigadier();
   }

   public static Rotation getRotation(final CommandContext<?> context, final String name) {
      return (Rotation)context.getArgument(name, Rotation.class);
   }

   static {
      TYPE = SerializableArgument.<TemplateRotationArgument>singletonType(INSTANCE);
   }
}
