package net.minecraft.server.dialog;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.Optional;
import net.minecraft.core.HolderSet;
import net.minecraft.network.chat.ClickEvent;
import net.minecraft.util.ExtraCodecs;

public record DialogListDialog(CommonDialogData common, HolderSet<Dialog> dialogs, Optional<ClickEvent> onCancel, int columns, int buttonWidth) implements ButtonListDialog {
   public static final MapCodec<DialogListDialog> MAP_CODEC = RecordCodecBuilder.mapCodec((var0) -> var0.group(CommonDialogData.MAP_CODEC.forGetter(DialogListDialog::common), Dialog.LIST_CODEC.fieldOf("dialogs").forGetter(DialogListDialog::dialogs), ClickEvent.CODEC.optionalFieldOf("on_cancel").forGetter(DialogListDialog::onCancel), ExtraCodecs.POSITIVE_INT.optionalFieldOf("columns", 2).forGetter(DialogListDialog::columns), ExtraCodecs.POSITIVE_INT.optionalFieldOf("button_width", 150).forGetter(DialogListDialog::buttonWidth)).apply(var0, DialogListDialog::new));

   public DialogListDialog(CommonDialogData var1, HolderSet<Dialog> var2, Optional<ClickEvent> var3, int var4, int var5) {
      super();
      this.common = var1;
      this.dialogs = var2;
      this.onCancel = var3;
      this.columns = var4;
      this.buttonWidth = var5;
   }

   public MapCodec<DialogListDialog> codec() {
      return MAP_CODEC;
   }
}
