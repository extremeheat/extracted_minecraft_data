package net.minecraft.util.datafix.fixes;

import com.mojang.datafixers.schemas.Schema;
import com.mojang.serialization.Dynamic;

public class GoatHornIdFix extends ItemStackTagFix {
   private static final String[] INSTRUMENTS = new String[]{"minecraft:ponder_goat_horn", "minecraft:sing_goat_horn", "minecraft:seek_goat_horn", "minecraft:feel_goat_horn", "minecraft:admire_goat_horn", "minecraft:call_goat_horn", "minecraft:yearn_goat_horn", "minecraft:dream_goat_horn"};

   public GoatHornIdFix(Schema var1) {
      super(var1, "GoatHornIdFix", (var0) -> {
         return var0.equals("minecraft:goat_horn");
      });
   }

   protected <T> Dynamic<T> fixItemStackTag(Dynamic<T> var1) {
      int var2 = var1.get("SoundVariant").asInt(0);
      String var3 = INSTRUMENTS[var2 >= 0 && var2 < INSTRUMENTS.length ? var2 : 0];
      return var1.remove("SoundVariant").set("instrument", var1.createString(var3));
   }
}
