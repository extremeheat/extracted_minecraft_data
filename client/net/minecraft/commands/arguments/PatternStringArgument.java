package net.minecraft.commands.arguments;

import com.google.common.annotations.VisibleForTesting;
import com.mojang.brigadier.Message;
import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.ComponentSerialization;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.util.ExtraCodecs;

public record PatternStringArgument(Pattern pattern, Optional<Component> errorMessage) implements SerializableArgument.ContextFree<String> {
   @VisibleForTesting
   public static final SimpleCommandExceptionType ERROR_NOT_MATCHED = new SimpleCommandExceptionType(Component.translatable("argument.pattern.not_matched"));
   private static final MapCodec<PatternStringArgument> MAP_CODEC = RecordCodecBuilder.mapCodec((i) -> i.group(ExtraCodecs.PATTERN.fieldOf("pattern").forGetter(PatternStringArgument::pattern), ComponentSerialization.CODEC.optionalFieldOf("error_message").forGetter(PatternStringArgument::errorMessage)).apply(i, PatternStringArgument::new));
   private static final StreamCodec<RegistryFriendlyByteBuf, PatternStringArgument> STREAM_CODEC;
   public static final SerializableArgument.Type<PatternStringArgument> TYPE;

   public PatternStringArgument {
      super();
   }

   public String parse(final StringReader reader) throws CommandSyntaxException {
      String fullString = reader.getString();
      Matcher matcher = this.pattern.matcher(fullString).region(reader.getCursor(), fullString.length());
      if (!matcher.lookingAt()) {
         CommandSyntaxException exception = ERROR_NOT_MATCHED.createWithContext(reader);
         if (this.errorMessage.isPresent()) {
            exception = new CommandSyntaxException(exception.getType(), (Message)this.errorMessage.get(), exception.getInput(), exception.getCursor());
         }

         throw exception;
      } else {
         reader.setCursor(matcher.end());
         return matcher.group(0);
      }
   }

   public SerializableArgument.Type<PatternStringArgument> type() {
      return TYPE;
   }

   static {
      STREAM_CODEC = StreamCodec.composite(ByteBufCodecs.PATTERN, PatternStringArgument::pattern, ComponentSerialization.TRUSTED_OPTIONAL_STREAM_CODEC, PatternStringArgument::errorMessage, PatternStringArgument::new);
      TYPE = new SerializableArgument.Type<PatternStringArgument>(MAP_CODEC, STREAM_CODEC);
   }
}
