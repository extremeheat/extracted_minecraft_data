package net.minecraft.util.datafix.fixes;

import com.mojang.datafixers.DSL;
import com.mojang.datafixers.DataFix;
import com.mojang.datafixers.TypeRewriteRule;
import com.mojang.datafixers.schemas.Schema;
import com.mojang.serialization.Dynamic;
import java.util.stream.IntStream;

public class WorldSpawnDataFix extends DataFix {
   public WorldSpawnDataFix(Schema var1) {
      super(var1, false);
   }

   protected TypeRewriteRule makeRule() {
      return this.fixTypeEverywhereTyped("WorldSpawnDataFix", this.getInputSchema().getType(References.LEVEL), (var0) -> var0.update(DSL.remainderFinder(), (var0x) -> {
            int var1 = var0x.get("SpawnX").asInt(0);
            int var2 = var0x.get("SpawnY").asInt(0);
            int var3 = var0x.get("SpawnZ").asInt(0);
            float var4 = var0x.get("SpawnAngle").asFloat(0.0F);
            Dynamic var5 = var0x.emptyMap().set("dimension", var0x.createString("minecraft:overworld")).set("pos", var0x.createIntList(IntStream.of(new int[]{var1, var2, var3}))).set("yaw", var0x.createFloat(var4)).set("pitch", var0x.createFloat(0.0F));
            var0x = var0x.remove("SpawnX").remove("SpawnY").remove("SpawnZ").remove("SpawnAngle");
            return var0x.set("spawn", var5);
         }));
   }
}
