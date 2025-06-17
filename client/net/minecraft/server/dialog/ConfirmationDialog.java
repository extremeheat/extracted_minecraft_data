package net.minecraft.server.dialog;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.List;
import java.util.Optional;
import net.minecraft.server.dialog.action.Action;

public record ConfirmationDialog(CommonDialogData common, ActionButton yesButton, ActionButton noButton) implements SimpleDialog {
   public static final MapCodec<ConfirmationDialog> MAP_CODEC = RecordCodecBuilder.mapCodec((var0) -> var0.group(CommonDialogData.MAP_CODEC.forGetter(ConfirmationDialog::common), ActionButton.CODEC.fieldOf("yes").forGetter(ConfirmationDialog::yesButton), ActionButton.CODEC.fieldOf("no").forGetter(ConfirmationDialog::noButton)).apply(var0, ConfirmationDialog::new));

   public ConfirmationDialog(CommonDialogData var1, ActionButton var2, ActionButton var3) {
      super();
      this.common = var1;
      this.yesButton = var2;
      this.noButton = var3;
   }

   public MapCodec<ConfirmationDialog> codec() {
      return MAP_CODEC;
   }

   public Optional<Action> onCancel() {
      return this.noButton.action();
   }

   public List<ActionButton> mainActions() {
      return List.of(this.yesButton, this.noButton);
   }
}
