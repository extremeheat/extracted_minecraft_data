package net.minecraft.server.dialog;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.Optional;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.ComponentSerialization;

public record CommonButtonData(Component label, Optional<Component> tooltip, int width) {
   public static final int DEFAULT_WIDTH = 150;
   public static final MapCodec<CommonButtonData> MAP_CODEC = RecordCodecBuilder.mapCodec((var0) -> var0.group(ComponentSerialization.CODEC.fieldOf("label").forGetter(CommonButtonData::label), ComponentSerialization.CODEC.optionalFieldOf("tooltip").forGetter(CommonButtonData::tooltip), Dialog.WIDTH_CODEC.optionalFieldOf("width", 150).forGetter(CommonButtonData::width)).apply(var0, CommonButtonData::new));

   public CommonButtonData(Component var1, int var2) {
      this(var1, Optional.empty(), var2);
   }

   public CommonButtonData(Component var1, Optional<Component> var2, int var3) {
      super();
      this.label = var1;
      this.tooltip = var2;
      this.width = var3;
   }
}
