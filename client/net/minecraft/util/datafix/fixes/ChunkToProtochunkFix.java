package net.minecraft.util.datafix.fixes;

import com.mojang.datafixers.DataFix;
import com.mojang.datafixers.DataFixUtils;
import com.mojang.datafixers.TypeRewriteRule;
import com.mojang.datafixers.schemas.Schema;
import com.mojang.serialization.Dynamic;
import it.unimi.dsi.fastutil.shorts.ShortArrayList;
import it.unimi.dsi.fastutil.shorts.ShortList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class ChunkToProtochunkFix extends DataFix {
   private static final int NUM_SECTIONS = 16;

   public ChunkToProtochunkFix(Schema var1, boolean var2) {
      super(var1, var2);
   }

   public TypeRewriteRule makeRule() {
      return this.writeFixAndRead("ChunkToProtoChunkFix", this.getInputSchema().getType(References.CHUNK), this.getOutputSchema().getType(References.CHUNK), (var0) -> {
         return var0.update("Level", ChunkToProtochunkFix::fixChunkData);
      });
   }

   private static <T> Dynamic<T> fixChunkData(Dynamic<T> var0) {
      boolean var1 = var0.get("TerrainPopulated").asBoolean(false);
      boolean var2 = var0.get("LightPopulated").asNumber().result().isEmpty() || var0.get("LightPopulated").asBoolean(false);
      String var3;
      if (var1) {
         if (var2) {
            var3 = "mobs_spawned";
         } else {
            var3 = "decorated";
         }
      } else {
         var3 = "carved";
      }

      return repackTicks(repackBiomes(var0)).set("Status", var0.createString(var3)).set("hasLegacyStructureData", var0.createBoolean(true));
   }

   private static <T> Dynamic<T> repackBiomes(Dynamic<T> var0) {
      return var0.update("Biomes", (var1) -> {
         return (Dynamic)DataFixUtils.orElse(var1.asByteBufferOpt().result().map((var1x) -> {
            int[] var2 = new int[256];

            for(int var3 = 0; var3 < var2.length; ++var3) {
               if (var3 < var1x.capacity()) {
                  var2[var3] = var1x.get(var3) & 255;
               }
            }

            return var0.createIntList(Arrays.stream(var2));
         }), var1);
      });
   }

   private static <T> Dynamic<T> repackTicks(Dynamic<T> var0) {
      return (Dynamic)DataFixUtils.orElse(var0.get("TileTicks").asStreamOpt().result().map((var1) -> {
         List var2 = (List)IntStream.range(0, 16).mapToObj((var0x) -> {
            return new ShortArrayList();
         }).collect(Collectors.toList());
         var1.forEach((var1x) -> {
            int var2x = var1x.get("x").asInt(0);
            int var3 = var1x.get("y").asInt(0);
            int var4 = var1x.get("z").asInt(0);
            short var5 = packOffsetCoordinates(var2x, var3, var4);
            ((ShortList)var2.get(var3 >> 4)).add(var5);
         });
         return var0.remove("TileTicks").set("ToBeTicked", var0.createList(var2.stream().map((var1x) -> {
            return var0.createList(var1x.intStream().mapToObj((var1) -> {
               return var0.createShort((short)var1);
            }));
         })));
      }), var0);
   }

   private static short packOffsetCoordinates(int var0, int var1, int var2) {
      return (short)(var0 & 15 | (var1 & 15) << 4 | (var2 & 15) << 8);
   }
}
