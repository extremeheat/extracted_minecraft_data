package net.minecraft.util.datafix.fixes;

import com.mojang.datafixers.DSL;
import com.mojang.datafixers.DataFix;
import com.mojang.datafixers.DataFixUtils;
import com.mojang.datafixers.OpticFinder;
import com.mojang.datafixers.TypeRewriteRule;
import com.mojang.datafixers.Typed;
import com.mojang.datafixers.schemas.Schema;
import com.mojang.datafixers.types.Type;
import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.Dynamic;
import it.unimi.dsi.fastutil.shorts.ShortArrayList;
import it.unimi.dsi.fastutil.shorts.ShortList;
import java.nio.ByteBuffer;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

public class ChunkToProtochunkFix extends DataFix {
   public ChunkToProtochunkFix(Schema var1, boolean var2) {
      super(var1, var2);
   }

   public TypeRewriteRule makeRule() {
      Type var1 = this.getInputSchema().getType(References.CHUNK);
      Type var2 = this.getOutputSchema().getType(References.CHUNK);
      Type var3 = var1.findFieldType("Level");
      Type var4 = var2.findFieldType("Level");
      Type var5 = var3.findFieldType("TileTicks");
      OpticFinder var6 = DSL.fieldFinder("Level", var3);
      OpticFinder var7 = DSL.fieldFinder("TileTicks", var5);
      return TypeRewriteRule.seq(this.fixTypeEverywhereTyped("ChunkToProtoChunkFix", var1, this.getOutputSchema().getType(References.CHUNK), (var3x) -> {
         return var3x.updateTyped(var6, var4, (var2) -> {
            Optional var3 = var2.getOptionalTyped(var7).flatMap((var0) -> {
               return var0.write().result();
            }).flatMap((var0) -> {
               return var0.asStreamOpt().result();
            });
            Dynamic var4x = (Dynamic)var2.get(DSL.remainderFinder());
            boolean var5 = var4x.get("TerrainPopulated").asBoolean(false) && (!var4x.get("LightPopulated").asNumber().result().isPresent() || var4x.get("LightPopulated").asBoolean(false));
            var4x = var4x.set("Status", var4x.createString(var5 ? "mobs_spawned" : "empty"));
            var4x = var4x.set("hasLegacyStructureData", var4x.createBoolean(true));
            Dynamic var6;
            if (var5) {
               Optional var7x = var4x.get("Biomes").asByteBufferOpt().result();
               if (var7x.isPresent()) {
                  ByteBuffer var8 = (ByteBuffer)var7x.get();
                  int[] var9 = new int[256];

                  for(int var10 = 0; var10 < var9.length; ++var10) {
                     if (var10 < var8.capacity()) {
                        var9[var10] = var8.get(var10) & 255;
                     }
                  }

                  var4x = var4x.set("Biomes", var4x.createIntList(Arrays.stream(var9)));
               }

               List var11 = (List)IntStream.range(0, 16).mapToObj((var0) -> {
                  return new ShortArrayList();
               }).collect(Collectors.toList());
               if (var3.isPresent()) {
                  ((Stream)var3.get()).forEach((var1) -> {
                     int var2 = var1.get("x").asInt(0);
                     int var3 = var1.get("y").asInt(0);
                     int var4 = var1.get("z").asInt(0);
                     short var5 = packOffsetCoordinates(var2, var3, var4);
                     ((ShortList)var11.get(var3 >> 4)).add(var5);
                  });
                  var4x = var4x.set("ToBeTicked", var4x.createList(var11.stream().map((var1) -> {
                     Stream var10001 = var1.stream();
                     var4x.getClass();
                     return var4x.createList(var10001.map(var4x::createShort));
                  })));
               }

               var6 = (Dynamic)DataFixUtils.orElse(var2.set(DSL.remainderFinder(), var4x).write().result(), var4x);
            } else {
               var6 = var4x;
            }

            return (Typed)((Pair)var4.readTyped(var6).result().orElseThrow(() -> {
               return new IllegalStateException("Could not read the new chunk");
            })).getFirst();
         });
      }), this.writeAndRead("Structure biome inject", this.getInputSchema().getType(References.STRUCTURE_FEATURE), this.getOutputSchema().getType(References.STRUCTURE_FEATURE)));
   }

   private static short packOffsetCoordinates(int var0, int var1, int var2) {
      return (short)(var0 & 15 | (var1 & 15) << 4 | (var2 & 15) << 8);
   }
}
