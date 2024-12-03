package net.minecraft.util.datafix.fixes;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Streams;
import com.mojang.datafixers.DSL;
import com.mojang.datafixers.DataFix;
import com.mojang.datafixers.OpticFinder;
import com.mojang.datafixers.TypeRewriteRule;
import com.mojang.datafixers.Typed;
import com.mojang.datafixers.schemas.Schema;
import com.mojang.datafixers.types.Type;
import com.mojang.datafixers.types.templates.List;
import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.Dynamic;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

public class ChunkBedBlockEntityInjecterFix extends DataFix {
   public ChunkBedBlockEntityInjecterFix(Schema var1, boolean var2) {
      super(var1, var2);
   }

   public TypeRewriteRule makeRule() {
      Type var1 = this.getOutputSchema().getType(References.CHUNK);
      Type var2 = var1.findFieldType("Level");
      Type var3 = var2.findFieldType("TileEntities");
      if (!(var3 instanceof List.ListType var4)) {
         throw new IllegalStateException("Tile entity type is not a list type.");
      } else {
         return this.cap(var2, var4);
      }
   }

   private <TE> TypeRewriteRule cap(Type<?> var1, List.ListType<TE> var2) {
      Type var3 = var2.getElement();
      OpticFinder var4 = DSL.fieldFinder("Level", var1);
      OpticFinder var5 = DSL.fieldFinder("TileEntities", var2);
      boolean var6 = true;
      return TypeRewriteRule.seq(this.fixTypeEverywhere("InjectBedBlockEntityType", this.getInputSchema().findChoiceType(References.BLOCK_ENTITY), this.getOutputSchema().findChoiceType(References.BLOCK_ENTITY), (var0) -> (var0x) -> var0x), this.fixTypeEverywhereTyped("BedBlockEntityInjecter", this.getOutputSchema().getType(References.CHUNK), (var3x) -> {
         Typed var4x = var3x.getTyped(var4);
         Dynamic var5x = (Dynamic)var4x.get(DSL.remainderFinder());
         int var6 = var5x.get("xPos").asInt(0);
         int var7 = var5x.get("zPos").asInt(0);
         ArrayList var8 = Lists.newArrayList((Iterable)var4x.getOrCreate(var5));

         for(Dynamic var11 : var5x.get("Sections").asList(Function.identity())) {
            int var12 = var11.get("Y").asInt(0);
            Streams.mapWithIndex(var11.get("Blocks").asIntStream(), (var4xx, var5xx) -> {
               if (416 == (var4xx & 255) << 4) {
                  int var7x = (int)var5xx;
                  int var8 = var7x & 15;
                  int var9 = var7x >> 8 & 15;
                  int var10 = var7x >> 4 & 15;
                  HashMap var11x = Maps.newHashMap();
                  var11x.put(var11.createString("id"), var11.createString("minecraft:bed"));
                  var11x.put(var11.createString("x"), var11.createInt(var8 + (var6 << 4)));
                  var11x.put(var11.createString("y"), var11.createInt(var9 + (var12 << 4)));
                  var11x.put(var11.createString("z"), var11.createInt(var10 + (var7 << 4)));
                  var11x.put(var11.createString("color"), var11.createShort((short)14));
                  return var11x;
               } else {
                  return null;
               }
            }).forEachOrdered((var3xx) -> {
               if (var3xx != null) {
                  var8.add(((Pair)var3.read(var11.createMap(var3xx)).result().orElseThrow(() -> new IllegalStateException("Could not parse newly created bed block entity."))).getFirst());
               }

            });
         }

         return !var8.isEmpty() ? var3x.set(var4, var4x.set(var5, var8)) : var3x;
      }));
   }
}
