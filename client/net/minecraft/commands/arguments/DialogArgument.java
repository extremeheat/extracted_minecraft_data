package net.minecraft.commands.arguments;

import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.context.CommandContext;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.Registries;
import net.minecraft.server.dialog.Dialog;

public class DialogArgument extends ResourceOrIdArgument.Simple<Dialog> {
   private static final DialogArgument INSTANCE = new DialogArgument();
   public static final SerializableArgument.Type<DialogArgument> TYPE;

   private DialogArgument() {
      super(Registries.DIALOG, Dialog.DIRECT_CODEC);
   }

   public SerializableArgument.Type<DialogArgument> type() {
      return TYPE;
   }

   public static ArgumentType<Holder<Dialog>> dialog() {
      return INSTANCE.toBrigadier();
   }

   public static Holder<Dialog> getDialog(final CommandContext<?> context, final String name) {
      return getResource(context, name);
   }

   static {
      TYPE = SerializableArgument.<DialogArgument>singletonType(INSTANCE);
   }
}
