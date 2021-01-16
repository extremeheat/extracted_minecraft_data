package net.minecraft.util.datafix.fixes;

import com.google.common.collect.ImmutableMap;
import com.mojang.datafixers.DSL;
import com.mojang.datafixers.Typed;
import com.mojang.datafixers.schemas.Schema;
import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.Dynamic;

public class MemoryExpiryDataFix extends NamedEntityFix {
   public MemoryExpiryDataFix(Schema var1, String var2) {
      super(var1, false, "Memory expiry data fix (" + var2 + ")", References.ENTITY, var2);
   }

   protected Typed<?> fix(Typed<?> var1) {
      return var1.update(DSL.remainderFinder(), this::fixTag);
   }

   public Dynamic<?> fixTag(Dynamic<?> var1) {
      return var1.update("Brain", this::updateBrain);
   }

   private Dynamic<?> updateBrain(Dynamic<?> var1) {
      return var1.update("memories", this::updateMemories);
   }

   private Dynamic<?> updateMemories(Dynamic<?> var1) {
      return var1.updateMapValues(this::updateMemoryEntry);
   }

   private Pair<Dynamic<?>, Dynamic<?>> updateMemoryEntry(Pair<Dynamic<?>, Dynamic<?>> var1) {
      return var1.mapSecond(this::wrapMemoryValue);
   }

   private Dynamic<?> wrapMemoryValue(Dynamic<?> var1) {
      return var1.createMap(ImmutableMap.of(var1.createString("value"), var1));
   }
}
