package net.minecraft.server.dialog;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.Optional;
import net.minecraft.network.chat.ClickEvent;

public record ClickAction(CommonButtonData buttonData, Optional<ClickEvent> onClick) {
   public static final MapCodec<ClickAction> MAP_CODEC = RecordCodecBuilder.mapCodec((var0) -> var0.group(CommonButtonData.MAP_CODEC.forGetter(ClickAction::buttonData), ClickEvent.CODEC.optionalFieldOf("on_click").forGetter(ClickAction::onClick)).apply(var0, ClickAction::new));
   public static final Codec<ClickAction> CODEC;

   public ClickAction(CommonButtonData var1, Optional<ClickEvent> var2) {
      super();
      this.buttonData = var1;
      this.onClick = var2;
   }

   static {
      CODEC = MAP_CODEC.codec();
   }
}
