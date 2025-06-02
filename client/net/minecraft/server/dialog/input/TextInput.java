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
   public static final MapCodec<TextInput> MAP_CODEC = RecordCodecBuilder.mapCodec((var0) -> var0.group(Dialog.WIDTH_CODEC.optionalFieldOf("width", 200).forGetter(TextInput::width), ComponentSerialization.CODEC.fieldOf("label").forGetter(TextInput::label), Codec.BOOL.optionalFieldOf("label_visible", true).forGetter(TextInput::labelVisible), Codec.STRING.optionalFieldOf("initial", "").forGetter(TextInput::initial), ExtraCodecs.POSITIVE_INT.optionalFieldOf("max_length", 32).forGetter(TextInput::maxLength), TextInput.MultilineOptions.CODEC.optionalFieldOf("multiline").forGetter(TextInput::multiline)).apply(var0, TextInput::new)).validate((var0) -> var0.initial.length() > var0.maxLength() ? DataResult.error(() -> "Default text length exceeds allowed size") : DataResult.success(var0));

   public TextInput(int var1, Component var2, boolean var3, String var4, int var5, Optional<MultilineOptions> var6) {
      super();
      this.width = var1;
      this.label = var2;
      this.labelVisible = var3;
      this.initial = var4;
      this.maxLength = var5;
      this.multiline = var6;
   }

   public MapCodec<TextInput> mapCodec() {
      return MAP_CODEC;
   }

   public static record MultilineOptions(Optional<Integer> maxLines, Optional<Integer> height) {
      public static final int MAX_HEIGHT = 512;
      public static final Codec<MultilineOptions> CODEC = RecordCodecBuilder.create((var0) -> var0.group(ExtraCodecs.POSITIVE_INT.optionalFieldOf("max_lines").forGetter(MultilineOptions::maxLines), ExtraCodecs.intRange(1, 512).optionalFieldOf("height").forGetter(MultilineOptions::height)).apply(var0, MultilineOptions::new));

      public MultilineOptions(Optional<Integer> var1, Optional<Integer> var2) {
         super();
         this.maxLines = var1;
         this.height = var2;
      }
   }
}
