package net.minecraft.util.datafix.fixes;

import com.google.common.base.Suppliers;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import com.mojang.datafixers.DSL;
import com.mojang.datafixers.DataFix;
import com.mojang.datafixers.DataFixUtils;
import com.mojang.datafixers.OpticFinder;
import com.mojang.datafixers.TypeRewriteRule;
import com.mojang.datafixers.Typed;
import com.mojang.datafixers.schemas.Schema;
import com.mojang.datafixers.types.Type;
import com.mojang.datafixers.types.templates.List.ListType;
import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.Dynamic;
import it.unimi.dsi.fastutil.ints.Int2ObjectArrayMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Stream;
import javax.annotation.Nullable;
import org.apache.commons.lang3.mutable.MutableInt;

public class ChunkProtoTickListFix extends DataFix {
   private static final int SECTION_WIDTH = 16;
   private static final ImmutableSet<String> ALWAYS_WATERLOGGED = ImmutableSet.of(
      "minecraft:bubble_column", "minecraft:kelp", "minecraft:kelp_plant", "minecraft:seagrass", "minecraft:tall_seagrass"
   );

   public ChunkProtoTickListFix(Schema var1) {
      super(var1, false);
   }

   protected TypeRewriteRule makeRule() {
      Type var1 = this.getInputSchema().getType(References.CHUNK);
      OpticFinder var2 = var1.findField("Level");
      OpticFinder var3 = var2.type().findField("Sections");
      OpticFinder var4 = ((ListType)var3.type()).getElement().finder();
      OpticFinder var5 = var4.type().findField("block_states");
      OpticFinder var6 = var4.type().findField("biomes");
      OpticFinder var7 = var5.type().findField("palette");
      OpticFinder var8 = var2.type().findField("TileTicks");
      return this.fixTypeEverywhereTyped(
         "ChunkProtoTickListFix",
         var1,
         var8x -> var8x.updateTyped(
               var2,
               var7xx -> {
                  var7xx = var7xx.update(
                     DSL.remainderFinder(),
                     var0 -> (Dynamic)DataFixUtils.orElse(
                           var0.get("LiquidTicks").result().map(var1xxx -> var0.set("fluid_ticks", var1xxx).remove("LiquidTicks")), var0
                        )
                  );
                  Dynamic var8xx = (Dynamic)var7xx.get(DSL.remainderFinder());
                  MutableInt var9 = new MutableInt();
                  Int2ObjectArrayMap var10 = new Int2ObjectArrayMap();
                  var7xx.getOptionalTyped(var3)
                     .ifPresent(
                        var6xxx -> var6xxx.getAllTyped(var4)
                              .forEach(
                                 var5xxxx -> {
                                    Dynamic var6xxxx = (Dynamic)var5xxxx.get(DSL.remainderFinder());
                                    int var7xxx = var6xxxx.get("Y").asInt(2147483647);
                                    if (var7xxx != 2147483647) {
                                       if (var5xxxx.getOptionalTyped(var6).isPresent()) {
                                          var9.setValue(Math.min(var7xxx, var9.getValue()));
                                       }

                                       var5xxxx.getOptionalTyped(var5)
                                          .ifPresent(
                                             var3xxxxx -> var10.put(
                                                   var7xxx,
                                                   Suppliers.memoize(
                                                      () -> {
                                                         List var2xxxxxx = var3xxxxx.getOptionalTyped(var7)
                                                            .map(
                                                               var0xxxx -> var0xxxx.write()
                                                                     .result()
                                                                     .map(var0xxxxx -> var0xxxxx.asList(Function.identity()))
                                                                     .orElse(Collections.emptyList())
                                                            )
                                                            .orElse(Collections.emptyList());
                                                         long[] var3xxxxxx = ((Dynamic)var3xxxxx.get(DSL.remainderFinder()))
                                                            .get("data")
                                                            .asLongStream()
                                                            .toArray();
                                                         return new ChunkProtoTickListFix.PoorMansPalettedContainer(var2xxxxxx, var3xxxxxx);
                                                      }
                                                   )
                                                )
                                          );
                                    }
                                 }
                              )
                     );
                  byte var11 = var9.getValue().byteValue();
                  var7xx = var7xx.update(DSL.remainderFinder(), var1xxx -> var1xxx.update("yPos", var1xxxx -> var1xxxx.createByte(var11)));
                  if (!var7xx.getOptionalTyped(var8).isPresent() && !var8xx.get("fluid_ticks").result().isPresent()) {
                     int var12 = var8xx.get("xPos").asInt(0);
                     int var13 = var8xx.get("zPos").asInt(0);
                     Dynamic var14 = this.makeTickList(var8xx, var10, var11, var12, var13, "LiquidsToBeTicked", ChunkProtoTickListFix::getLiquid);
                     Dynamic var15 = this.makeTickList(var8xx, var10, var11, var12, var13, "ToBeTicked", ChunkProtoTickListFix::getBlock);
                     Optional var16 = var8.type().readTyped(var15).result();
                     if (var16.isPresent()) {
                        var7xx = var7xx.set(var8, (Typed)((Pair)var16.get()).getFirst());
                     }

                     return var7xx.update(DSL.remainderFinder(), var1xxx -> var1xxx.remove("ToBeTicked").remove("LiquidsToBeTicked").set("fluid_ticks", var14));
                  } else {
                     return var7xx;
                  }
               }
            )
      );
   }

   private Dynamic<?> makeTickList(
      Dynamic<?> var1,
      Int2ObjectMap<Supplier<ChunkProtoTickListFix.PoorMansPalettedContainer>> var2,
      byte var3,
      int var4,
      int var5,
      String var6,
      Function<Dynamic<?>, String> var7
   ) {
      Stream var8 = Stream.empty();
      List var9 = var1.get(var6).asList(Function.identity());

      for (int var10 = 0; var10 < var9.size(); var10++) {
         int var11 = var10 + var3;
         Supplier var12 = (Supplier)var2.get(var11);
         Stream var13 = ((Dynamic)var9.get(var10))
            .asStream()
            .mapToInt(var0 -> var0.asShort((short)-1))
            .filter(var0 -> var0 > 0)
            .mapToObj(var7x -> this.createTick(var1, var12, var4, var11, var5, var7x, var7));
         var8 = Stream.concat(var8, var13);
      }

      return var1.createList(var8);
   }

   private static String getBlock(@Nullable Dynamic<?> var0) {
      return var0 != null ? var0.get("Name").asString("minecraft:air") : "minecraft:air";
   }

   private static String getLiquid(@Nullable Dynamic<?> var0) {
      if (var0 == null) {
         return "minecraft:empty";
      } else {
         String var1 = var0.get("Name").asString("");
         if ("minecraft:water".equals(var1)) {
            return var0.get("Properties").get("level").asInt(0) == 0 ? "minecraft:water" : "minecraft:flowing_water";
         } else if ("minecraft:lava".equals(var1)) {
            return var0.get("Properties").get("level").asInt(0) == 0 ? "minecraft:lava" : "minecraft:flowing_lava";
         } else {
            return !ALWAYS_WATERLOGGED.contains(var1) && !var0.get("Properties").get("waterlogged").asBoolean(false) ? "minecraft:empty" : "minecraft:water";
         }
      }
   }

   private Dynamic<?> createTick(
      Dynamic<?> var1,
      @Nullable Supplier<ChunkProtoTickListFix.PoorMansPalettedContainer> var2,
      int var3,
      int var4,
      int var5,
      int var6,
      Function<Dynamic<?>, String> var7
   ) {
      int var8 = var6 & 15;
      int var9 = var6 >>> 4 & 15;
      int var10 = var6 >>> 8 & 15;
      String var11 = (String)var7.apply(var2 != null ? ((ChunkProtoTickListFix.PoorMansPalettedContainer)var2.get()).get(var8, var9, var10) : null);
      return var1.createMap(
         ImmutableMap.builder()
            .put(var1.createString("i"), var1.createString(var11))
            .put(var1.createString("x"), var1.createInt(var3 * 16 + var8))
            .put(var1.createString("y"), var1.createInt(var4 * 16 + var9))
            .put(var1.createString("z"), var1.createInt(var5 * 16 + var10))
            .put(var1.createString("t"), var1.createInt(0))
            .put(var1.createString("p"), var1.createInt(0))
            .build()
      );
   }

   public static final class PoorMansPalettedContainer {
      private static final long SIZE_BITS = 4L;
      private final List<? extends Dynamic<?>> palette;
      private final long[] data;
      private final int bits;
      private final long mask;
      private final int valuesPerLong;

      public PoorMansPalettedContainer(List<? extends Dynamic<?>> var1, long[] var2) {
         super();
         this.palette = var1;
         this.data = var2;
         this.bits = Math.max(4, ChunkHeightAndBiomeFix.ceillog2(var1.size()));
         this.mask = (1L << this.bits) - 1L;
         this.valuesPerLong = (char)(64 / this.bits);
      }

      @Nullable
      public Dynamic<?> get(int var1, int var2, int var3) {
         int var4 = this.palette.size();
         if (var4 < 1) {
            return null;
         } else if (var4 == 1) {
            return (Dynamic<?>)this.palette.get(0);
         } else {
            int var5 = this.getIndex(var1, var2, var3);
            int var6 = var5 / this.valuesPerLong;
            if (var6 >= 0 && var6 < this.data.length) {
               long var7 = this.data[var6];
               int var9 = (var5 - var6 * this.valuesPerLong) * this.bits;
               int var10 = (int)(var7 >> var9 & this.mask);
               return (Dynamic<?>)(var10 >= 0 && var10 < var4 ? this.palette.get(var10) : null);
            } else {
               return null;
            }
         }
      }

      private int getIndex(int var1, int var2, int var3) {
         return (var2 << 4 | var3) << 4 | var1;
      }

      public List<? extends Dynamic<?>> palette() {
         return this.palette;
      }

      public long[] data() {
         return this.data;
      }
   }
}
