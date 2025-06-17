package net.minecraft.server.dialog;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.List;
import java.util.Optional;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.server.dialog.action.Action;

public record NoticeDialog(CommonDialogData common, ActionButton action) implements SimpleDialog {
   public static final ActionButton DEFAULT_ACTION;
   public static final MapCodec<NoticeDialog> MAP_CODEC;

   public NoticeDialog(CommonDialogData var1, ActionButton var2) {
      super();
      this.common = var1;
      this.action = var2;
   }

   public MapCodec<NoticeDialog> codec() {
      return MAP_CODEC;
   }

   public Optional<Action> onCancel() {
      return this.action.action();
   }

   public List<ActionButton> mainActions() {
      return List.of(this.action);
   }

   static {
      DEFAULT_ACTION = new ActionButton(new CommonButtonData(CommonComponents.GUI_OK, 150), Optional.empty());
      MAP_CODEC = RecordCodecBuilder.mapCodec((var0) -> var0.group(CommonDialogData.MAP_CODEC.forGetter(NoticeDialog::common), ActionButton.CODEC.optionalFieldOf("action", DEFAULT_ACTION).forGetter(NoticeDialog::action)).apply(var0, NoticeDialog::new));
   }
}
