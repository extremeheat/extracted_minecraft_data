package net.minecraft.util.datafix.schemas;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;
import com.mojang.datafixers.DSL;
import com.mojang.datafixers.DataFixUtils;
import com.mojang.datafixers.schemas.Schema;
import com.mojang.datafixers.types.templates.Hook;
import com.mojang.datafixers.types.templates.TypeTemplate;
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
   protected static final Hook.HookFunction UNPACK_OBJECTIVE_ID = new Hook.HookFunction() {
      public <T> T apply(DynamicOps<T> var1, T var2) {
         Dynamic var3 = new Dynamic(var1, var2);
         return ((Dynamic)DataFixUtils.orElse(var3.get("CriteriaName").asString().result().map((var0) -> {
            int var1 = var0.indexOf(58);
            if (var1 < 0) {
               return Pair.of("_special", var0);
            } else {
               try {
                  ResourceLocation var2 = ResourceLocation.of(var0.substring(0, var1), '.');
                  ResourceLocation var3 = ResourceLocation.of(var0.substring(var1 + 1), '.');
                  return Pair.of(var2.toString(), var3.toString());
               } catch (Exception var4) {
                  return Pair.of("_special", var0);
               }
            }
         }).map((var1x) -> {
            return var3.set("CriteriaType", var3.createMap(ImmutableMap.of(var3.createString("type"), var3.createString((String)var1x.getFirst()), var3.createString("id"), var3.createString((String)var1x.getSecond()))));
         }), var3)).getValue();
      }
   };
   protected static final Hook.HookFunction REPACK_OBJECTIVE_ID = new Hook.HookFunction() {
      public <T> T apply(DynamicOps<T> var1, T var2) {
         Dynamic var3 = new Dynamic(var1, var2);
         Optional var4 = var3.get("CriteriaType").get().result().flatMap((var1x) -> {
            Optional var2 = var1x.get("type").asString().result();
            Optional var3x = var1x.get("id").asString().result();
            if (var2.isPresent() && var3x.isPresent()) {
               String var4 = (String)var2.get();
               if (var4.equals("_special")) {
                  return Optional.of(var3.createString((String)var3x.get()));
               } else {
                  String var10001 = V1451_6.packNamespacedWithDot(var4);
                  return Optional.of(var1x.createString(var10001 + ":" + V1451_6.packNamespacedWithDot((String)var3x.get())));
               }
            } else {
               return Optional.empty();
            }
         });
         return ((Dynamic)DataFixUtils.orElse(var4.map((var1x) -> {
            return var3.set("CriteriaName", var1x).remove("CriteriaType");
         }), var3)).getValue();
      }
   };

   public V1451_6(int var1, Schema var2) {
      super(var1, var2);
   }

   public void registerTypes(Schema var1, Map<String, Supplier<TypeTemplate>> var2, Map<String, Supplier<TypeTemplate>> var3) {
      super.registerTypes(var1, var2, var3);
      Supplier var4 = () -> {
         return DSL.compoundList(References.ITEM_NAME.in(var1), DSL.constType(DSL.intType()));
      };
      var1.registerType(false, References.STATS, () -> {
         return DSL.optionalFields("stats", DSL.optionalFields(new Pair[]{Pair.of("minecraft:mined", DSL.compoundList(References.BLOCK_NAME.in(var1), DSL.constType(DSL.intType()))), Pair.of("minecraft:crafted", (TypeTemplate)var4.get()), Pair.of("minecraft:used", (TypeTemplate)var4.get()), Pair.of("minecraft:broken", (TypeTemplate)var4.get()), Pair.of("minecraft:picked_up", (TypeTemplate)var4.get()), Pair.of("minecraft:dropped", (TypeTemplate)var4.get()), Pair.of("minecraft:killed", DSL.compoundList(References.ENTITY_NAME.in(var1), DSL.constType(DSL.intType()))), Pair.of("minecraft:killed_by", DSL.compoundList(References.ENTITY_NAME.in(var1), DSL.constType(DSL.intType()))), Pair.of("minecraft:custom", DSL.compoundList(DSL.constType(namespacedString()), DSL.constType(DSL.intType())))}));
      });
      Map var5 = createCriterionTypes(var1);
      var1.registerType(false, References.OBJECTIVE, () -> {
         return DSL.hook(DSL.optionalFields("CriteriaType", DSL.taggedChoiceLazy("type", DSL.string(), var5)), UNPACK_OBJECTIVE_ID, REPACK_OBJECTIVE_ID);
      });
   }

   protected static Map<String, Supplier<TypeTemplate>> createCriterionTypes(Schema var0) {
      Supplier var1 = () -> {
         return DSL.optionalFields("id", References.ITEM_NAME.in(var0));
      };
      Supplier var2 = () -> {
         return DSL.optionalFields("id", References.BLOCK_NAME.in(var0));
      };
      Supplier var3 = () -> {
         return DSL.optionalFields("id", References.ENTITY_NAME.in(var0));
      };
      HashMap var4 = Maps.newHashMap();
      var4.put("minecraft:mined", var2);
      var4.put("minecraft:crafted", var1);
      var4.put("minecraft:used", var1);
      var4.put("minecraft:broken", var1);
      var4.put("minecraft:picked_up", var1);
      var4.put("minecraft:dropped", var1);
      var4.put("minecraft:killed", var3);
      var4.put("minecraft:killed_by", var3);
      var4.put("minecraft:custom", () -> {
         return DSL.optionalFields("id", DSL.constType(namespacedString()));
      });
      var4.put("_special", () -> {
         return DSL.optionalFields("id", DSL.constType(DSL.string()));
      });
      return var4;
   }

   public static String packNamespacedWithDot(String var0) {
      ResourceLocation var1 = ResourceLocation.tryParse(var0);
      return var1 != null ? var1.getNamespace() + "." + var1.getPath() : var0;
   }
}
