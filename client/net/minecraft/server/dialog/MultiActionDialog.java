package net.minecraft.server.dialog;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.List;
import java.util.Optional;
import net.minecraft.network.chat.ClickEvent;
import net.minecraft.util.ExtraCodecs;

public record MultiActionDialog(CommonDialogData common, List<ClickAction> actions, Optional<ClickEvent> onCancel, int columns) implements ButtonListDialog {
   public static final MapCodec<MultiActionDialog> MAP_CODEC = RecordCodecBuilder.mapCodec((var0) -> var0.group(CommonDialogData.MAP_CODEC.forGetter(MultiActionDialog::common), ExtraCodecs.nonEmptyList(ClickAction.CODEC.listOf()).fieldOf("actions").forGetter(MultiActionDialog::actions), ClickEvent.CODEC.optionalFieldOf("on_cancel").forGetter(MultiActionDialog::onCancel), ExtraCodecs.POSITIVE_INT.optionalFieldOf("columns", 2).forGetter(MultiActionDialog::columns)).apply(var0, MultiActionDialog::new));

   public MultiActionDialog(CommonDialogData var1, List<ClickAction> var2, Optional<ClickEvent> var3, int var4) {
      super();
      this.common = var1;
      this.actions = var2;
      this.onCancel = var3;
      this.columns = var4;
   }

   public MapCodec<MultiActionDialog> codec() {
      return MAP_CODEC;
   }
}
