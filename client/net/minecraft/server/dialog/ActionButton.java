package net.minecraft.server.dialog;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.Optional;
import net.minecraft.server.dialog.action.Action;

public record ActionButton(CommonButtonData button, Optional<Action> action) {
   public static final Codec<ActionButton> CODEC = RecordCodecBuilder.create((var0) -> var0.group(CommonButtonData.MAP_CODEC.forGetter(ActionButton::button), Action.CODEC.optionalFieldOf("action").forGetter(ActionButton::action)).apply(var0, ActionButton::new));

   public ActionButton(CommonButtonData var1, Optional<Action> var2) {
      super();
      this.button = var1;
      this.action = var2;
   }
}
