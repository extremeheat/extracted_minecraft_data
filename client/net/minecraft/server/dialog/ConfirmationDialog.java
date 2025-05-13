package net.minecraft.server.dialog;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.List;
import java.util.Optional;
import net.minecraft.network.chat.ClickEvent;

public record ConfirmationDialog(CommonDialogData common, ClickAction yesButton, ClickAction noButton) implements SimpleDialog {
   public static final MapCodec<ConfirmationDialog> MAP_CODEC = RecordCodecBuilder.mapCodec((var0) -> var0.group(CommonDialogData.MAP_CODEC.forGetter(ConfirmationDialog::common), ClickAction.CODEC.fieldOf("yes").forGetter(ConfirmationDialog::yesButton), ClickAction.CODEC.fieldOf("no").forGetter(ConfirmationDialog::noButton)).apply(var0, ConfirmationDialog::new));

   public ConfirmationDialog(CommonDialogData var1, ClickAction var2, ClickAction var3) {
      super();
      this.common = var1;
      this.yesButton = var2;
      this.noButton = var3;
   }

   public MapCodec<ConfirmationDialog> codec() {
      return MAP_CODEC;
   }

   public Optional<ClickEvent> onCancel() {
      return this.noButton.onClick();
   }

   public List<ClickAction> mainActions() {
      return List.of(this.yesButton, this.noButton);
   }
}
