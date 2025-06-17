package net.minecraft.server.dialog.action;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.Map;
import java.util.Optional;
import net.minecraft.network.chat.ClickEvent;

public record CommandTemplate(ParsedTemplate template) implements Action {
   public static final MapCodec<CommandTemplate> MAP_CODEC = RecordCodecBuilder.mapCodec((var0) -> var0.group(ParsedTemplate.CODEC.fieldOf("template").forGetter(CommandTemplate::template)).apply(var0, CommandTemplate::new));

   public CommandTemplate(ParsedTemplate var1) {
      super();
      this.template = var1;
   }

   public MapCodec<CommandTemplate> codec() {
      return MAP_CODEC;
   }

   public Optional<ClickEvent> createAction(Map<String, Action.ValueGetter> var1) {
      String var2 = this.template.instantiate(Action.ValueGetter.getAsTemplateSubstitutions(var1));
      return Optional.of(new ClickEvent.RunCommand(var2));
   }
}
