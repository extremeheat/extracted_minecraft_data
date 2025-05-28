package net.minecraft.server.dialog;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.server.dialog.action.ParsedTemplate;
import net.minecraft.server.dialog.input.InputControl;

public record Input(String key, InputControl control) {
   public static final Codec<Input> CODEC = RecordCodecBuilder.create((var0) -> var0.group(ParsedTemplate.VARIABLE_CODEC.fieldOf("key").forGetter(Input::key), InputControl.MAP_CODEC.forGetter(Input::control)).apply(var0, Input::new));

   public Input(String var1, InputControl var2) {
      super();
      this.key = var1;
      this.control = var2;
   }
}
