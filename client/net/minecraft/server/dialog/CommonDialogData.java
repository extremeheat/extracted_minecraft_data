package net.minecraft.server.dialog;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.List;
import java.util.Optional;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.ComponentSerialization;
import net.minecraft.server.dialog.body.DialogBody;

public record CommonDialogData(Component title, Optional<Component> externalTitle, boolean canCloseWithEscape, List<DialogBody> body) {
   public static final MapCodec<CommonDialogData> MAP_CODEC = RecordCodecBuilder.mapCodec((var0) -> var0.group(ComponentSerialization.CODEC.fieldOf("title").forGetter(CommonDialogData::title), ComponentSerialization.CODEC.optionalFieldOf("external_title").forGetter(CommonDialogData::externalTitle), Codec.BOOL.optionalFieldOf("can_close_with_escape", true).forGetter(CommonDialogData::canCloseWithEscape), DialogBody.COMPACT_LIST_CODEC.optionalFieldOf("body", List.of()).forGetter(CommonDialogData::body)).apply(var0, CommonDialogData::new));

   public CommonDialogData(Component var1, Optional<Component> var2, boolean var3, List<DialogBody> var4) {
      super();
      this.title = var1;
      this.externalTitle = var2;
      this.canCloseWithEscape = var3;
      this.body = var4;
   }

   public Component computeExternalTitle() {
      return (Component)this.externalTitle.orElse(this.title);
   }
}
