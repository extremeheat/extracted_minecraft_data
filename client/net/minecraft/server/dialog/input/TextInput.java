package net.minecraft.server.dialog.input;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.ComponentSerialization;
import net.minecraft.util.ExtraCodecs;

public record TextInput(int width, Component label, boolean labelVisible, String initial) implements InputControl {
   public static final MapCodec<TextInput> MAP_CODEC = RecordCodecBuilder.mapCodec((var0) -> var0.group(ExtraCodecs.POSITIVE_INT.optionalFieldOf("width", 200).forGetter(TextInput::width), ComponentSerialization.CODEC.fieldOf("label").forGetter(TextInput::label), Codec.BOOL.optionalFieldOf("label_visible", true).forGetter(TextInput::labelVisible), Codec.STRING.optionalFieldOf("initial", "").forGetter(TextInput::initial)).apply(var0, TextInput::new));

   public TextInput(int var1, Component var2, boolean var3, String var4) {
      super();
      this.width = var1;
      this.label = var2;
      this.labelVisible = var3;
      this.initial = var4;
   }

   public MapCodec<TextInput> mapCodec() {
      return MAP_CODEC;
   }
}
