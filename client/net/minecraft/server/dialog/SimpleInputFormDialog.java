package net.minecraft.server.dialog;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.List;
import net.minecraft.util.ExtraCodecs;

public record SimpleInputFormDialog(CommonDialogData common, List<InputFormDialog.Input> inputs, InputFormDialog.SubmitAction action) implements InputFormDialog {
   public static final MapCodec<SimpleInputFormDialog> MAP_CODEC = RecordCodecBuilder.mapCodec((var0) -> var0.group(CommonDialogData.MAP_CODEC.forGetter(SimpleInputFormDialog::common), ExtraCodecs.nonEmptyList(InputFormDialog.Input.CODEC.listOf()).fieldOf("inputs").forGetter(SimpleInputFormDialog::inputs), InputFormDialog.SubmitAction.CODEC.fieldOf("action").forGetter(SimpleInputFormDialog::action)).apply(var0, SimpleInputFormDialog::new));

   public SimpleInputFormDialog(CommonDialogData var1, List<InputFormDialog.Input> var2, InputFormDialog.SubmitAction var3) {
      super();
      this.common = var1;
      this.inputs = var2;
      this.action = var3;
   }

   public MapCodec<SimpleInputFormDialog> codec() {
      return MAP_CODEC;
   }
}
