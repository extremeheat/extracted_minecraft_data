package net.minecraft.util.datafix.fixes;

import com.mojang.datafixers.schemas.Schema;

public class BeehivePoiRenameFix extends PoiTypeRename {
   public BeehivePoiRenameFix(Schema var1) {
      super(var1, false);
   }

   protected String rename(String var1) {
      return var1.equals("minecraft:bee_hive") ? "minecraft:beehive" : var1;
   }
}
