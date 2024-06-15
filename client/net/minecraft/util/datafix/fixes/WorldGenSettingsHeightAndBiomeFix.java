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
      return this.fixTypeEverywhereTyped(
         "WorldGenSettingsHeightAndBiomeFix",
         var1,
         var3,
         var2x -> {
            OptionalDynamic var3x = ((Dynamic)var2x.get(DSL.remainderFinder())).get("has_increased_height_already");
            boolean var4x = var3x.result().isEmpty();
            boolean var5 = var3x.asBoolean(true);
            return var2x.update(DSL.remainderFinder(), var0x -> var0x.remove("has_increased_height_already"))
               .updateTyped(
                  var2,
                  var4,
                  var3xx -> Util.writeAndReadTypedOrThrow(
                        var3xx,
                        var4,
                        var2xxx -> var2xxx.update(
                              "minecraft:overworld",
                              var2xxxx -> var2xxxx.update(
                                    "generator",
                                    var2xxxxx -> {
                                       String var3xxx = var2xxxxx.get("type").asString("");
                                       if ("minecraft:noise".equals(var3xxx)) {
                                          MutableBoolean var4xx = new MutableBoolean();
                                          var2xxxxx = var2xxxxx.update(
                                             "biome_source",
                                             var2xxxxxx -> {
                                                String var3xxxx = var2xxxxxx.get("type").asString("");
                                                if ("minecraft:vanilla_layered".equals(var3xxxx) || var4x && "minecraft:multi_noise".equals(var3xxxx)) {
                                                   if (var2xxxxxx.get("large_biomes").asBoolean(false)) {
                                                      var4xx.setTrue();
                                                   }

                                                   return var2xxxxxx.createMap(
                                                      ImmutableMap.of(
                                                         var2xxxxxx.createString("preset"),
                                                         var2xxxxxx.createString("minecraft:overworld"),
                                                         var2xxxxxx.createString("type"),
                                                         var2xxxxxx.createString("minecraft:multi_noise")
                                                      )
                                                   );
                                                } else {
                                                   return var2xxxxxx;
                                                }
                                             }
                                          );
                                          return var4xx.booleanValue()
                                             ? var2xxxxx.update(
                                                "settings",
                                                var0xxxxx -> "minecraft:overworld".equals(var0xxxxx.asString(""))
                                                      ? var0xxxxx.createString("minecraft:large_biomes")
                                                      : var0xxxxx
                                             )
                                             : var2xxxxx;
                                       } else if ("minecraft:flat".equals(var3xxx)) {
                                          return var5
                                             ? var2xxxxx
                                             : var2xxxxx.update(
                                                "settings", var0xxxxx -> var0xxxxx.update("layers", WorldGenSettingsHeightAndBiomeFix::updateLayers)
                                             );
                                       } else {
                                          return var2xxxxx;
                                       }
                                    }
                                 )
                           )
                     )
               );
         }
      );
   }

   private static Dynamic<?> updateLayers(Dynamic<?> var0) {
      Dynamic var1 = var0.createMap(
         ImmutableMap.of(var0.createString("height"), var0.createInt(64), var0.createString("block"), var0.createString("minecraft:air"))
      );
      return var0.createList(Stream.concat(Stream.of(var1), var0.asStream()));
   }
}
