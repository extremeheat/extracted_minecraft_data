package net.minecraft.server.dialog.submit;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

public record CommandTemplate(ParsedTemplate template) implements SubmitMethod {
   public static final MapCodec<CommandTemplate> MAP_CODEC = RecordCodecBuilder.mapCodec((var0) -> var0.group(ParsedTemplate.CODEC.fieldOf("template").forGetter(CommandTemplate::template)).apply(var0, CommandTemplate::new));

   public CommandTemplate(ParsedTemplate var1) {
      super();
      this.template = var1;
   }

   public MapCodec<CommandTemplate> mapCodec() {
      return MAP_CODEC;
   }
}
