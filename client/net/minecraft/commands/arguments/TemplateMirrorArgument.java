package net.minecraft.commands.arguments;

import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.context.CommandContext;
import net.minecraft.world.level.block.Mirror;

public class TemplateMirrorArgument extends StringRepresentableArgument<Mirror> {
   private static final TemplateMirrorArgument INSTANCE = new TemplateMirrorArgument();
   public static final SerializableArgument.Type<TemplateMirrorArgument> TYPE;

   private TemplateMirrorArgument() {
      super(Mirror.CODEC, Mirror::values);
   }

   public SerializableArgument.Type<TemplateMirrorArgument> type() {
      return TYPE;
   }

   public static ArgumentType<Mirror> templateMirror() {
      return INSTANCE.toBrigadier();
   }

   public static Mirror getMirror(final CommandContext<?> context, final String name) {
      return (Mirror)context.getArgument(name, Mirror.class);
   }

   static {
      TYPE = SerializableArgument.<TemplateMirrorArgument>singletonType(INSTANCE);
   }
}
