package net.minecraft.commands.arguments;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import java.util.Collection;
import java.util.List;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.TagParser;

public class CompoundTagArgument implements SerializableArgument.ContextFree<CompoundTag> {
   private static final CompoundTagArgument INSTANCE = new CompoundTagArgument();
   public static final SerializableArgument.Type<CompoundTagArgument> TYPE;
   private static final Collection<String> EXAMPLES;

   private CompoundTagArgument() {
      super();
   }

   public SerializableArgument.Type<CompoundTagArgument> type() {
      return TYPE;
   }

   public static ArgumentType<CompoundTag> compoundTag() {
      return INSTANCE.toBrigadier();
   }

   public static CompoundTag getCompoundTag(final CommandContext<?> context, final String name) {
      return (CompoundTag)context.getArgument(name, CompoundTag.class);
   }

   public CompoundTag parse(final StringReader reader) throws CommandSyntaxException {
      return TagParser.parseCompoundAsArgument(reader);
   }

   public Collection<String> getExamples() {
      return EXAMPLES;
   }

   static {
      TYPE = SerializableArgument.<CompoundTagArgument>singletonType(INSTANCE);
      EXAMPLES = List.of("{}", "{foo=bar}");
   }
}
