package net.minecraft.util.datafix.fixes;

import com.google.common.collect.ImmutableMap;
import com.mojang.datafixers.DSL;
import com.mojang.datafixers.DataFix;
import com.mojang.datafixers.OpticFinder;
import com.mojang.datafixers.TypeRewriteRule;
import com.mojang.datafixers.schemas.Schema;
import com.mojang.datafixers.types.Type;
import com.mojang.serialization.Dynamic;
import com.mojang.serialization.OptionalDynamic;
import java.util.stream.Stream;
import net.minecraft.Util;
import org.apache.commons.lang3.mutable.MutableBoolean;

public class WorldGenSettingsHeightAndBiomeFix extends DataFix {
   private static final String NAME = "WorldGenSettingsHeightAndBiomeFix";
   public static final String WAS_PREVIOUSLY_INCREASED_KEY = "has_increased_height_already";

   public WorldGenSettingsHeightAndBiomeFix(Schema var1) {
      super(var1, true);
   }

   protected TypeRewriteRule makeRule() {
      Type var1 = this.getInputSchema().getType(References.WORLD_GEN_SETTINGS);
      OpticFinder var2 = var1.findField("dimensions");
      Type var3 = this.getOutputSchema().getType(References.WORLD_GEN_SETTINGS);
      Type var4 = var3.findFieldType("dimensions");
      return this.fixTypeEverywhereTyped("WorldGenSettingsHeightAndBiomeFix", var1, var3, (var2x) -> {
         OptionalDynamic var3 = ((Dynamic)var2x.get(DSL.remainderFinder())).get("has_increased_height_already");
         boolean var4x = var3.result().isEmpty();
         boolean var5 = var3.asBoolean(true);
         return var2x.update(DSL.remainderFinder(), (var0) -> {
            return var0.remove("has_increased_height_already");
         }).updateTyped(var2, var4, (var3x) -> {
            return Util.writeAndReadTypedOrThrow(var3x, var4, (var2) -> {
               return var2.update("minecraft:overworld", (var2x) -> {
                  return var2x.update("generator", (var2) -> {
                     String var3 = var2.get("type").asString("");
                     if ("minecraft:noise".equals(var3)) {
                        MutableBoolean var4 = new MutableBoolean();
                        var2 = var2.update("biome_source", (var2x) -> {
                           String var3 = var2x.get("type").asString("");
                           if ("minecraft:vanilla_layered".equals(var3) || var4x && "minecraft:multi_noise".equals(var3)) {
                              if (var2x.get("large_biomes").asBoolean(false)) {
                                 var4.setTrue();
                              }

                              return var2x.createMap(ImmutableMap.of(var2x.createString("preset"), var2x.createString("minecraft:overworld"), var2x.createString("type"), var2x.createString("minecraft:multi_noise")));
                           } else {
                              return var2x;
                           }
                        });
                        return var4.booleanValue() ? var2.update("settings", (var0) -> {
                           return "minecraft:overworld".equals(var0.asString("")) ? var0.createString("minecraft:large_biomes") : var0;
                        }) : var2;
                     } else if ("minecraft:flat".equals(var3)) {
                        return var5 ? var2 : var2.update("settings", (var0) -> {
                           return var0.update("layers", WorldGenSettingsHeightAndBiomeFix::updateLayers);
                        });
                     } else {
                        return var2;
                     }
                  });
               });
            });
         });
      });
   }

   private static Dynamic<?> updateLayers(Dynamic<?> var0) {
      Dynamic var1 = var0.createMap(ImmutableMap.of(var0.createString("height"), var0.createInt(64), var0.createString("block"), var0.createString("minecraft:air")));
      return var0.createList(Stream.concat(Stream.of(var1), var0.asStream()));
   }
}
