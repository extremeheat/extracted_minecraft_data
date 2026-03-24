package net.minecraft.server.dialog.input;

import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.Optional;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.ComponentSerialization;
import net.minecraft.server.dialog.Dialog;
import net.minecraft.util.ExtraCodecs;

public record TextInput(int width, Component label, boolean labelVisible, String initial, int maxLength, Optional<MultilineOptions> multiline) implements InputControl {
   public static final MapCodec<TextInput> MAP_CODEC = RecordCodecBuilder.mapCodec((i) -> i.group(Dialog.WIDTH_CODEC.optionalFieldOf("width", 200).forGetter(TextInput::width), ComponentSerialization.CODEC.fieldOf("label").forGetter(TextInput::label), Codec.BOOL.optionalFieldOf("label_visible", true).forGetter(TextInput::labelVisible), Codec.STRING.optionalFieldOf("initial", "").forGetter(TextInput::initial), ExtraCodecs.POSITIVE_INT.optionalFieldOf("max_length", 32).forGetter(TextInput::maxLength), TextInput.MultilineOptions.CODEC.optionalFieldOf("multiline").forGetter(TextInput::multiline)).apply(i, TextInput::new)).validate((o) -> o.initial.length() > o.maxLength() ? DataResult.error(() -> "Default text length exceeds allowed size") : DataResult.success(o));

   public TextInput {
      super();
   }

   public MapCodec<TextInput> mapCodec() {
      return MAP_CODEC;
   }

   public static record MultilineOptions(Optional<Integer> maxLines, Optional<Integer> height) {
      public static final int MAX_HEIGHT = 512;
      public static final Codec<MultilineOptions> CODEC = RecordCodecBuilder.create((i) -> i.group(ExtraCodecs.POSITIVE_INT.optionalFieldOf("max_lines").forGetter(MultilineOptions::maxLines), ExtraCodecs.intRange(1, 512).optionalFieldOf("height").forGetter(MultilineOptions::height)).apply(i, MultilineOptions::new));

      public MultilineOptions {
         super();
      }
   }
}
