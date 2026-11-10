package net.minecraft.commands.arguments;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import java.util.Collection;
import java.util.List;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import net.minecraft.network.chat.Component;

public class UuidArgument implements SerializableArgument.ContextFree<UUID> {
   private static final UuidArgument INSTANCE = new UuidArgument();
   public static final SerializableArgument.Type<UuidArgument> TYPE;
   public static final SimpleCommandExceptionType ERROR_INVALID_UUID;
   private static final Collection<String> EXAMPLES;
   private static final Pattern ALLOWED_CHARACTERS;

   private UuidArgument() {
      super();
   }

   public SerializableArgument.Type<UuidArgument> type() {
      return TYPE;
   }

   public static UUID getUuid(final CommandContext<?> source, final String name) {
      return (UUID)source.getArgument(name, UUID.class);
   }

   public static ArgumentType<UUID> uuid() {
      return INSTANCE.toBrigadier();
   }

   public UUID parse(final StringReader reader) throws CommandSyntaxException {
      String remaining = reader.getRemaining();
      Matcher matcher = ALLOWED_CHARACTERS.matcher(remaining);
      if (matcher.find()) {
         String maybeUUID = matcher.group(1);

         try {
            UUID result = UUID.fromString(maybeUUID);
            reader.setCursor(reader.getCursor() + maybeUUID.length());
            return result;
         } catch (IllegalArgumentException var6) {
         }
      }

      throw ERROR_INVALID_UUID.createWithContext(reader);
   }

   public Collection<String> getExamples() {
      return EXAMPLES;
   }

   static {
      TYPE = SerializableArgument.<UuidArgument>singletonType(INSTANCE);
      ERROR_INVALID_UUID = new SimpleCommandExceptionType(Component.translatable("argument.uuid.invalid"));
      EXAMPLES = List.of("dd12be42-52a9-4a91-a8a1-11c01849e498");
      ALLOWED_CHARACTERS = Pattern.compile("^([-A-Fa-f0-9]+)");
   }
}
