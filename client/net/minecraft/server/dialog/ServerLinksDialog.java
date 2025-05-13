package net.minecraft.server.dialog;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.Optional;
import net.minecraft.network.chat.ClickEvent;
import net.minecraft.util.ExtraCodecs;

public record ServerLinksDialog(CommonDialogData common, Optional<ClickEvent> onCancel, int columns, int buttonWidth) implements ButtonListDialog {
   public static final MapCodec<ServerLinksDialog> MAP_CODEC = RecordCodecBuilder.mapCodec((var0) -> var0.group(CommonDialogData.MAP_CODEC.forGetter(ServerLinksDialog::common), ClickEvent.CODEC.optionalFieldOf("on_cancel").forGetter(ServerLinksDialog::onCancel), ExtraCodecs.POSITIVE_INT.optionalFieldOf("columns", 2).forGetter(ServerLinksDialog::columns), ExtraCodecs.POSITIVE_INT.optionalFieldOf("button_width", 150).forGetter(ServerLinksDialog::buttonWidth)).apply(var0, ServerLinksDialog::new));

   public ServerLinksDialog(CommonDialogData var1, Optional<ClickEvent> var2, int var3, int var4) {
      super();
      this.common = var1;
      this.onCancel = var2;
      this.columns = var3;
      this.buttonWidth = var4;
   }

   public MapCodec<ServerLinksDialog> codec() {
      return MAP_CODEC;
   }
}
