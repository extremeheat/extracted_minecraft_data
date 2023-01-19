package net.minecraft.util.datafix.schemas;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;
import com.mojang.datafixers.DSL;
import com.mojang.datafixers.DataFixUtils;
import com.mojang.datafixers.schemas.Schema;
import com.mojang.datafixers.types.templates.TypeTemplate;
import com.mojang.datafixers.types.templates.Hook.HookFunction;
import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.Dynamic;
import com.mojang.serialization.DynamicOps;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.function.Supplier;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.datafix.fixes.References;

public class V1451_6 extends NamespacedSchema {
   public static final String SPECIAL_OBJECTIVE_MARKER = "_special";
   protected static final HookFunction UNPACK_OBJECTIVE_ID = new HookFunction() {
      public <T> T apply(DynamicOps<T> var1, T var2) {
         Dynamic var3 = new Dynamic(var1, var2);
         return (T)((Dynamic)DataFixUtils.orElse(
               var3.get("CriteriaName")
                  .asString()
                  .get()
                  .left()
                  .map(var0 -> {
                     int var1x = var0.indexOf(58);
                     if (var1x < 0) {
                        return Pair.of("_special", var0);
                     } else {
                        try {
                           ResourceLocation var2x = ResourceLocation.of(var0.substring(0, var1x), '.');
                           ResourceLocation var3x = ResourceLocation.of(var0.substring(var1x + 1), '.');
                           return Pair.of(var2x.toString(), var3x.toString());
                        } catch (Exception var4) {
                           return Pair.of("_special", var0);
                        }
                     }
                  })
                  .map(
                     var1x -> var3.set(
                           "CriteriaType",
                           var3.createMap(
                              ImmutableMap.of(
                                 var3.createString("type"),
                                 var3.createString((String)var1x.getFirst()),
                                 var3.createString("id"),
                                 var3.createString((String)var1x.getSecond())
                              )
                           )
                        )
                  ),
               var3
            ))
            .getValue();
      }
   };
   protected static final HookFunction REPACK_OBJECTIVE_ID = new HookFunction() {
      private String packWithDot(String var1) {
         ResourceLocation var2 = ResourceLocation.tryParse(var1);
         return var2 != null ? var2.getNamespace() + "." + var2.getPath() : var1;
      }

      public <T> T apply(DynamicOps<T> var1, T var2) {
         Dynamic var3 = new Dynamic(var1, var2);
         Optional var4 = var3.get("CriteriaType")
            .get()
            .get()
            .left()
            .flatMap(
               var2x -> {
                  Optional var3x = var2x.get("type").asString().get().left();
                  Optional var4x = var2x.get("id").asString().get().left();
                  if (var3x.isPresent() && var4x.isPresent()) {
                     String var5 = (String)var3x.get();
                     return var5.equals("_special")
                        ? Optional.of((T)var3.createString((String)var4x.get()))
                        : Optional.of((T)var2x.createString(this.packWithDot(var5) + ":" + this.packWithDot((String)var4x.get())));
                  } else {
                     return Optional.empty();
                  }
               }
            );
         return (T)((Dynamic)DataFixUtils.orElse(var4.map(var1x -> var3.set("CriteriaName", var1x).remove("CriteriaType")), var3)).getValue();
      }
   };

   public V1451_6(int var1, Schema var2) {
      super(var1, var2);
   }

   public void registerTypes(Schema var1, Map<String, Supplier<TypeTemplate>> var2, Map<String, Supplier<TypeTemplate>> var3) {
      super.registerTypes(var1, var2, var3);
      Supplier var4 = () -> DSL.compoundList(References.ITEM_NAME.in(var1), DSL.constType(DSL.intType()));
      var1.registerType(
         false,
         References.STATS,
         () -> DSL.optionalFields(
               "stats",
               DSL.optionalFields(
                  "minecraft:mined",
                  DSL.compoundList(References.BLOCK_NAME.in(var1), DSL.constType(DSL.intType())),
                  "minecraft:crafted",
                  (TypeTemplate)var4.get(),
                  "minecraft:used",
                  (TypeTemplate)var4.get(),
                  "minecraft:broken",
                  (TypeTemplate)var4.get(),
                  "minecraft:picked_up",
                  (TypeTemplate)var4.get(),
                  DSL.optionalFields(
                     "minecraft:dropped",
                     (TypeTemplate)var4.get(),
                     "minecraft:killed",
                     DSL.compoundList(References.ENTITY_NAME.in(var1), DSL.constType(DSL.intType())),
                     "minecraft:killed_by",
                     DSL.compoundList(References.ENTITY_NAME.in(var1), DSL.constType(DSL.intType())),
                     "minecraft:custom",
                     DSL.compoundList(DSL.constType(namespacedString()), DSL.constType(DSL.intType()))
                  )
               )
            )
      );
      Map var5 = createCriterionTypes(var1);
      var1.registerType(
         false,
         References.OBJECTIVE,
         () -> DSL.hook(DSL.optionalFields("CriteriaType", DSL.taggedChoiceLazy("type", DSL.string(), var5)), UNPACK_OBJECTIVE_ID, REPACK_OBJECTIVE_ID)
      );
   }

   protected static Map<String, Supplier<TypeTemplate>> createCriterionTypes(Schema var0) {
      Supplier var1 = () -> DSL.optionalFields("id", References.ITEM_NAME.in(var0));
      Supplier var2 = () -> DSL.optionalFields("id", References.BLOCK_NAME.in(var0));
      Supplier var3 = () -> DSL.optionalFields("id", References.ENTITY_NAME.in(var0));
      HashMap var4 = Maps.newHashMap();
      var4.put("minecraft:mined", var2);
      var4.put("minecraft:crafted", var1);
      var4.put("minecraft:used", var1);
      var4.put("minecraft:broken", var1);
      var4.put("minecraft:picked_up", var1);
      var4.put("minecraft:dropped", var1);
      var4.put("minecraft:killed", var3);
      var4.put("minecraft:killed_by", var3);
      var4.put("minecraft:custom", () -> DSL.optionalFields("id", DSL.constType(namespacedString())));
      var4.put("_special", () -> DSL.optionalFields("id", DSL.constType(DSL.string())));
      return var4;
   }
}
