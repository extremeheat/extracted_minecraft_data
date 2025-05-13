package net.minecraft.server.dialog;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.Optional;
import net.minecraft.network.chat.ClickEvent;
import net.minecraft.server.dialog.input.InputControl;
import net.minecraft.server.dialog.submit.ParsedTemplate;
import net.minecraft.server.dialog.submit.SubmitMethod;

public interface InputFormDialog extends Dialog {
   MapCodec<? extends InputFormDialog> codec();

   default Optional<ClickEvent> onCancel() {
      return Optional.empty();
   }

   public static record Input(String key, InputControl control) {
      public static final Codec<Input> CODEC = RecordCodecBuilder.create((var0) -> var0.group(ParsedTemplate.VARIABLE_CODEC.fieldOf("key").forGetter(Input::key), InputControl.MAP_CODEC.forGetter(Input::control)).apply(var0, Input::new));

      public Input(String var1, InputControl var2) {
         super();
         this.key = var1;
         this.control = var2;
      }
   }

   public static record SubmitAction(String id, CommonButtonData buttonData, SubmitMethod method) {
      public static final Codec<SubmitAction> CODEC = RecordCodecBuilder.create((var0) -> var0.group(Codec.STRING.fieldOf("id").forGetter(SubmitAction::id), CommonButtonData.MAP_CODEC.forGetter(SubmitAction::buttonData), SubmitMethod.CODEC.fieldOf("on_submit").forGetter(SubmitAction::method)).apply(var0, SubmitAction::new));

      public SubmitAction(String var1, CommonButtonData var2, SubmitMethod var3) {
         super();
         this.id = var1;
         this.buttonData = var2;
         this.method = var3;
      }
   }
}
