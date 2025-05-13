package net.minecraft.server.dialog;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.List;
import net.minecraft.util.ExtraCodecs;

public record MultiActionInputFormDialog(CommonDialogData common, List<InputFormDialog.Input> inputs, List<InputFormDialog.SubmitAction> actions) implements InputFormDialog {
   public static final MapCodec<MultiActionInputFormDialog> MAP_CODEC = RecordCodecBuilder.mapCodec((var0) -> var0.group(CommonDialogData.MAP_CODEC.forGetter(MultiActionInputFormDialog::common), ExtraCodecs.nonEmptyList(InputFormDialog.Input.CODEC.listOf()).fieldOf("inputs").forGetter(MultiActionInputFormDialog::inputs), ExtraCodecs.nonEmptyList(InputFormDialog.SubmitAction.CODEC.listOf()).fieldOf("actions").forGetter(MultiActionInputFormDialog::actions)).apply(var0, MultiActionInputFormDialog::new));

   public MultiActionInputFormDialog(CommonDialogData var1, List<InputFormDialog.Input> var2, List<InputFormDialog.SubmitAction> var3) {
      super();
      this.common = var1;
      this.inputs = var2;
      this.actions = var3;
   }

   public MapCodec<MultiActionInputFormDialog> codec() {
      return MAP_CODEC;
   }
}
