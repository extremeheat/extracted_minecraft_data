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
   private static final ImmutableSet<String> ALWAYS_WATERLOGGED = ImmutableSet.of("minecraft:bubble_column", "minecraft:kelp", "minecraft:kelp_plant", "minecraft:seagrass", "minecraft:tall_seagrass");

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
      return this.fixTypeEverywhereTyped("ChunkProtoTickListFix", var1, (var8x) -> {
         return var8x.updateTyped(var2, (var7x) -> {
            var7x = var7x.update(DSL.remainderFinder(), (var0) -> {
               return (Dynamic)DataFixUtils.orElse(var0.get("LiquidTicks").result().map((var1) -> {
                  return var0.set("fluid_ticks", var1).remove("LiquidTicks");
               }), var0);
            });
            Dynamic var8x = (Dynamic)var7x.get(DSL.remainderFinder());
            MutableInt var9 = new MutableInt();
            Int2ObjectArrayMap var10 = new Int2ObjectArrayMap();
            var7x.getOptionalTyped(var3).ifPresent((var6x) -> {
               var6x.getAllTyped(var4).forEach((var5x) -> {
                  Dynamic var6x = (Dynamic)var5x.get(DSL.remainderFinder());
                  int var7x = var6x.get("Y").asInt(2147483647);
                  if (var7x != 2147483647) {
                     if (var5x.getOptionalTyped(var6).isPresent()) {
                        var9.setValue(Math.min(var7x, var9.getValue()));
                     }

                     var5x.getOptionalTyped(var5).ifPresent((var3) -> {
                        var10.put(var7x, Suppliers.memoize(() -> {
                           List var2 = (List)var3.getOptionalTyped(var7).map((var0) -> {
                              return (List)var0.write().result().map((var0x) -> {
                                 return var0x.asList(Function.identity());
                              }).orElse(Collections.emptyList());
                           }).orElse(Collections.emptyList());
                           long[] var3x = ((Dynamic)var3.get(DSL.remainderFinder())).get("data").asLongStream().toArray();
                           return new ChunkProtoTickListFix.PoorMansPalettedContainer(var2, var3x);
                        }));
                     });
                  }
               });
            });
            byte var11 = var9.getValue().byteValue();
            var7x = var7x.update(DSL.remainderFinder(), (var1) -> {
               return var1.update("yPos", (var1x) -> {
                  return var1x.createByte(var11);
               });
            });
            if (!var7x.getOptionalTyped(var8).isPresent() && !var8x.get("fluid_ticks").result().isPresent()) {
               int var12 = var8x.get("xPos").asInt(0);
               int var13 = var8x.get("zPos").asInt(0);
               Dynamic var14 = this.makeTickList(var8x, var10, var11, var12, var13, "LiquidsToBeTicked", ChunkProtoTickListFix::getLiquid);
               Dynamic var15 = this.makeTickList(var8x, var10, var11, var12, var13, "ToBeTicked", ChunkProtoTickListFix::getBlock);
               Optional var16 = var8.type().readTyped(var15).result();
               if (var16.isPresent()) {
                  var7x = var7x.set(var8, (Typed)((Pair)var16.get()).getFirst());
               }

               return var7x.update(DSL.remainderFinder(), (var1) -> {
                  return var1.remove("ToBeTicked").remove("LiquidsToBeTicked").set("fluid_ticks", var14);
               });
            } else {
               return var7x;
            }
         });
      });
   }

   private Dynamic<?> makeTickList(Dynamic<?> var1, Int2ObjectMap<Supplier<ChunkProtoTickListFix.PoorMansPalettedContainer>> var2, byte var3, int var4, int var5, String var6, Function<Dynamic<?>, String> var7) {
      Stream var8 = Stream.empty();
      List var9 = var1.get(var6).asList(Function.identity());

      for(int var10 = 0; var10 < var9.size(); ++var10) {
         int var11 = var10 + var3;
         Supplier var12 = (Supplier)var2.get(var11);
         Stream var13 = ((Dynamic)var9.get(var10)).asStream().mapToInt((var0) -> {
            return var0.asShort((short)-1);
         }).filter((var0) -> {
            return var0 > 0;
         }).mapToObj((var7x) -> {
            return this.createTick(var1, var12, var4, var11, var5, var7x, var7);
         });
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

   private Dynamic<?> createTick(Dynamic<?> var1, @Nullable Supplier<ChunkProtoTickListFix.PoorMansPalettedContainer> var2, int var3, int var4, int var5, int var6, Function<Dynamic<?>, String> var7) {
      int var8 = var6 & 15;
      int var9 = var6 >>> 4 & 15;
      int var10 = var6 >>> 8 & 15;
      String var11 = (String)var7.apply(var2 != null ? ((ChunkProtoTickListFix.PoorMansPalettedContainer)var2.get()).get(var8, var9, var10) : null);
      return var1.createMap(ImmutableMap.builder().put(var1.createString("i"), var1.createString(var11)).put(var1.createString("x"), var1.createInt(var3 * 16 + var8)).put(var1.createString("y"), var1.createInt(var4 * 16 + var9)).put(var1.createString("z"), var1.createInt(var5 * 16 + var10)).put(var1.createString("t"), var1.createInt(0)).put(var1.createString("p"), var1.createInt(0)).build());
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
            return (Dynamic)this.palette.get(0);
         } else {
            int var5 = this.getIndex(var1, var2, var3);
            int var6 = var5 / this.valuesPerLong;
            if (var6 >= 0 && var6 < this.data.length) {
               long var7 = this.data[var6];
               int var9 = (var5 - var6 * this.valuesPerLong) * this.bits;
               int var10 = (int)(var7 >> var9 & this.mask);
               return var10 >= 0 && var10 < var4 ? (Dynamic)this.palette.get(var10) : null;
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
