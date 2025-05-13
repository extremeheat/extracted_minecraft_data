package net.minecraft.server.dialog;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.List;
import java.util.Optional;
import net.minecraft.network.chat.ClickEvent;
import net.minecraft.network.chat.CommonComponents;

public record NoticeDialog(CommonDialogData common, ClickAction button) implements SimpleDialog {
   public static final ClickAction DEFAULT_ACTION;
   public static final MapCodec<NoticeDialog> MAP_CODEC;

   public NoticeDialog(CommonDialogData var1, ClickAction var2) {
      super();
      this.common = var1;
      this.button = var2;
   }

   public MapCodec<NoticeDialog> codec() {
      return MAP_CODEC;
   }

   public Optional<ClickEvent> onCancel() {
      return this.button.onClick();
   }

   public List<ClickAction> mainActions() {
      return List.of(this.button);
   }

   static {
      DEFAULT_ACTION = new ClickAction(new CommonButtonData(CommonComponents.GUI_OK, 150), Optional.empty());
      MAP_CODEC = RecordCodecBuilder.mapCodec((var0) -> var0.group(CommonDialogData.MAP_CODEC.forGetter(NoticeDialog::common), ClickAction.CODEC.optionalFieldOf("action", DEFAULT_ACTION).forGetter(NoticeDialog::button)).apply(var0, NoticeDialog::new));
   }
}
