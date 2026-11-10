package net.minecraft.commands.suggestions;

import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.suggestion.SuggestionProvider;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.netty.buffer.ByteBuf;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;

public record SuggestLiteralString(List<String> values) implements SerializableCustomSuggestion {
   private static final MapCodec<SuggestLiteralString> MAP_CODEC = RecordCodecBuilder.mapCodec((i) -> i.group(Codec.STRING.listOf().fieldOf("values").forGetter(SuggestLiteralString::values)).apply(i, SuggestLiteralString::new));
   private static final StreamCodec<ByteBuf, SuggestLiteralString> STREAM_CODEC;
   public static final SerializableCustomSuggestion.Type<SuggestLiteralString> TYPE;

   public SuggestLiteralString {
      super();
   }

   public static SuggestionProvider<CommandSourceStack> suggestLiteral(final String... literals) {
      return (new SuggestLiteralString(List.of(literals))).toBrigadier();
   }

   public SerializableCustomSuggestion.Type<SuggestLiteralString> type() {
      return TYPE;
   }

   public CompletableFuture<Suggestions> generate(final CommandContext<? extends SuggestionContext> context, final SuggestionsBuilder builder) throws CommandSyntaxException {
      return SuggestionContext.suggest(this.values, builder);
   }

   static {
      STREAM_CODEC = StreamCodec.composite(ByteBufCodecs.STRING_UTF8.apply(ByteBufCodecs.list()), SuggestLiteralString::values, SuggestLiteralString::new);
      TYPE = new SerializableCustomSuggestion.Type<SuggestLiteralString>(MAP_CODEC, STREAM_CODEC);
   }
}
