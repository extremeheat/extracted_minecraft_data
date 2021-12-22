package net.minecraft.util.datafix.fixes;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.mojang.datafixers.DSL;
import com.mojang.datafixers.DataFix;
import com.mojang.datafixers.OpticFinder;
import com.mojang.datafixers.TypeRewriteRule;
import com.mojang.datafixers.Typed;
import com.mojang.datafixers.schemas.Schema;
import com.mojang.datafixers.types.Type;
import com.mojang.datafixers.types.templates.List.ListType;
import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.Dynamic;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;
import java.util.function.Function;
import java.util.stream.Stream;

public class ChunkBedBlockEntityInjecterFix extends DataFix {
   public ChunkBedBlockEntityInjecterFix(Schema var1, boolean var2) {
      super(var1, var2);
   }

   public TypeRewriteRule makeRule() {
      Type var1 = this.getOutputSchema().getType(References.CHUNK);
      Type var2 = var1.findFieldType("Level");
      Type var3 = var2.findFieldType("TileEntities");
      if (!(var3 instanceof ListType)) {
         throw new IllegalStateException("Tile entity type is not a list type.");
      } else {
         ListType var4 = (ListType)var3;
         return this.cap(var2, var4);
      }
   }

   private <TE> TypeRewriteRule cap(Type<?> var1, ListType<TE> var2) {
      Type var3 = var2.getElement();
      OpticFinder var4 = DSL.fieldFinder("Level", var1);
      OpticFinder var5 = DSL.fieldFinder("TileEntities", var2);
      boolean var6 = true;
      return TypeRewriteRule.seq(this.fixTypeEverywhere("InjectBedBlockEntityType", this.getInputSchema().findChoiceType(References.BLOCK_ENTITY), this.getOutputSchema().findChoiceType(References.BLOCK_ENTITY), (var0) -> {
         return (var0x) -> {
            return var0x;
         };
      }), this.fixTypeEverywhereTyped("BedBlockEntityInjecter", this.getOutputSchema().getType(References.CHUNK), (var3x) -> {
         Typed var4x = var3x.getTyped(var4);
         Dynamic var5x = (Dynamic)var4x.get(DSL.remainderFinder());
         int var6 = var5x.get("xPos").asInt(0);
         int var7 = var5x.get("zPos").asInt(0);
         ArrayList var8 = Lists.newArrayList((Iterable)var4x.getOrCreate(var5));
         List var9 = var5x.get("Sections").asList(Function.identity());

         for(int var10 = 0; var10 < var9.size(); ++var10) {
            Dynamic var11 = (Dynamic)var9.get(var10);
            int var12 = var11.get("Y").asInt(0);
            Stream var13 = var11.get("Blocks").asStream().map((var0) -> {
               return var0.asInt(0);
            });
            int var14 = 0;
            Objects.requireNonNull(var13);

            for(Iterator var15 = (var13::iterator).iterator(); var15.hasNext(); ++var14) {
               int var16 = (Integer)var15.next();
               if (416 == (var16 & 255) << 4) {
                  int var17 = var14 & 15;
                  int var18 = var14 >> 8 & 15;
                  int var19 = var14 >> 4 & 15;
                  HashMap var20 = Maps.newHashMap();
                  var20.put(var11.createString("id"), var11.createString("minecraft:bed"));
                  var20.put(var11.createString("x"), var11.createInt(var17 + (var6 << 4)));
                  var20.put(var11.createString("y"), var11.createInt(var18 + (var12 << 4)));
                  var20.put(var11.createString("z"), var11.createInt(var19 + (var7 << 4)));
                  var20.put(var11.createString("color"), var11.createShort((short)14));
                  var8.add(((Pair)var3.read(var11.createMap(var20)).result().orElseThrow(() -> {
                     return new IllegalStateException("Could not parse newly created bed block entity.");
                  })).getFirst());
               }
            }
         }

         if (!var8.isEmpty()) {
            return var3x.set(var4, var4x.set(var5, var8));
         } else {
            return var3x;
         }
      }));
   }
}
