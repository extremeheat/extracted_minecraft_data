package net.minecraft.util.datafix.fixes;

import com.mojang.datafixers.DSL;
import com.mojang.datafixers.DataFix;
import com.mojang.datafixers.Dynamic;
import com.mojang.datafixers.OpticFinder;
import com.mojang.datafixers.TypeRewriteRule;
import com.mojang.datafixers.Typed;
import com.mojang.datafixers.schemas.Schema;
import com.mojang.datafixers.types.Type;
import java.nio.ByteBuffer;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;
import net.minecraft.util.datafix.TypeReferences;

public class ChunkGenStatus extends DataFix {
   public ChunkGenStatus(Schema var1, boolean var2) {
      super(var1, var2);
   }

   public TypeRewriteRule makeRule() {
      Type var1 = this.getInputSchema().getType(TypeReferences.field_211287_c);
      Type var2 = this.getOutputSchema().getType(TypeReferences.field_211287_c);
      Type var3 = var1.findFieldType("Level");
      Type var4 = var2.findFieldType("Level");
      Type var5 = var3.findFieldType("TileTicks");
      OpticFinder var6 = DSL.fieldFinder("Level", var3);
      OpticFinder var7 = DSL.fieldFinder("TileTicks", var5);
      return TypeRewriteRule.seq(this.fixTypeEverywhereTyped("ChunkToProtoChunkFix", var1, this.getOutputSchema().getType(TypeReferences.field_211287_c), (var3x) -> {
         return var3x.updateTyped(var6, var4, (var2) -> {
            Optional var3 = var2.getOptionalTyped(var7).map(Typed::write).flatMap(Dynamic::getStream);
            Dynamic var4x = (Dynamic)var2.get(DSL.remainderFinder());
            boolean var5 = var4x.getBoolean("TerrainPopulated") && (!var4x.get("LightPopulated").flatMap(Dynamic::getNumberValue).isPresent() || var4x.getBoolean("LightPopulated"));
            var4x = var4x.set("Status", var4x.createString(var5 ? "mobs_spawned" : "empty"));
            var4x = var4x.set("hasLegacyStructureData", var4x.createBoolean(true));
            Dynamic var6;
            if (var5) {
               Optional var7x = var4x.get("Biomes").flatMap(Dynamic::getByteBuffer);
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

               List var11 = (List)IntStream.range(0, 16).mapToObj((var1) -> {
                  return var4x.createList(Stream.empty());
               }).collect(Collectors.toList());
               if (var3.isPresent()) {
                  ((Stream)var3.get()).forEach((var2x) -> {
                     int var3 = var2x.getInt("x");
                     int var4 = var2x.getInt("y");
                     int var5 = var2x.getInt("z");
                     short var6 = func_210975_a(var3, var4, var5);
                     var11.set(var4 >> 4, ((Dynamic)var11.get(var4 >> 4)).merge(var4x.createShort(var6)));
                  });
                  var4x = var4x.set("ToBeTicked", var4x.createList(var11.stream()));
               }

               var6 = var2.set(DSL.remainderFinder(), var4x).write();
            } else {
               var6 = var4x;
            }

            return (Typed)((Optional)var4.readTyped(var6).getSecond()).orElseThrow(() -> {
               return new IllegalStateException("Could not read the new chunk");
            });
         });
      }), this.writeAndRead("Structure biome inject", this.getInputSchema().getType(TypeReferences.field_211303_s), this.getOutputSchema().getType(TypeReferences.field_211303_s)));
   }

   private static short func_210975_a(int var0, int var1, int var2) {
      return (short)(var0 & 15 | (var1 & 15) << 4 | (var2 & 15) << 8);
   }
}
