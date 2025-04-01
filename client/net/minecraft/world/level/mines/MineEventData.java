package net.minecraft.world.level.mines;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.List;

public record MineEventData(boolean started, List<MineEvent> events) {
   public static final Codec<MineEventData> CODEC = RecordCodecBuilder.create((var0) -> var0.group(Codec.BOOL.fieldOf("started").forGetter(MineEventData::started), MineEvent.CODEC.listOf().fieldOf("events").forGetter(MineEventData::events)).apply(var0, MineEventData::new));

   public MineEventData(boolean var1, List<MineEvent> var2) {
      super();
      this.started = var1;
      this.events = var2;
   }
}
