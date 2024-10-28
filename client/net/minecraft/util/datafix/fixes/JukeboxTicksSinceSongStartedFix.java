package net.minecraft.util.datafix.fixes;

import com.mojang.datafixers.DSL;
import com.mojang.datafixers.Typed;
import com.mojang.datafixers.schemas.Schema;
import com.mojang.serialization.Dynamic;

public class JukeboxTicksSinceSongStartedFix extends NamedEntityFix {
   public JukeboxTicksSinceSongStartedFix(Schema var1) {
      super(var1, false, "JukeboxTicksSinceSongStartedFix", References.BLOCK_ENTITY, "minecraft:jukebox");
   }

   public Dynamic<?> fixTag(Dynamic<?> var1) {
      long var2 = var1.get("TickCount").asLong(0L) - var1.get("RecordStartTick").asLong(0L);
      Dynamic var4 = var1.remove("IsPlaying").remove("TickCount").remove("RecordStartTick");
      return var2 > 0L ? var4.set("ticks_since_song_started", var1.createLong(var2)) : var4;
   }

   protected Typed<?> fix(Typed<?> var1) {
      return var1.update(DSL.remainderFinder(), this::fixTag);
   }
}
