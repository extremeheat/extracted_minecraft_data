package net.minecraft.server.jsonrpc.methods;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.List;
import java.util.Optional;

public record Message(Optional<String> literal, Optional<String> translatable, Optional<List<String>> translatableParams) {
   public static final Codec<Message> CODEC = RecordCodecBuilder.create((var0) -> var0.group(Codec.STRING.optionalFieldOf("literal").forGetter(Message::literal), Codec.STRING.optionalFieldOf("translatable").forGetter(Message::translatable), Codec.STRING.listOf().lenientOptionalFieldOf("translatableParams").forGetter(Message::translatableParams)).apply(var0, Message::new));

   public Message(Optional<String> var1, Optional<String> var2, Optional<List<String>> var3) {
      super();
      this.literal = var1;
      this.translatable = var2;
      this.translatableParams = var3;
   }
}
