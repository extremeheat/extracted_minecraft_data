package net.minecraft.commands.arguments;

import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.arguments.BoolArgumentType;
import com.mojang.brigadier.context.CommandContext;

public class BooleanArgument extends BrigadierWrapperArgument<Boolean, BoolArgumentType> {
   public static final BooleanArgument INSTANCE = new BooleanArgument();
   public static final SerializableArgument.Type<BooleanArgument> TYPE;

   private BooleanArgument() {
      super(BoolArgumentType.bool());
   }

   public SerializableArgument.Type<BooleanArgument> type() {
      return TYPE;
   }

   public static ArgumentType<Boolean> bool() {
      return INSTANCE.toBrigadier();
   }

   public static boolean getBool(final CommandContext<?> context, final String name) {
      return BoolArgumentType.getBool(context, name);
   }

   static {
      TYPE = SerializableArgument.<BooleanArgument>singletonType(INSTANCE);
   }
}
