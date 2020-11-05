package net.minecraft.util.datafix.fixes;

import com.google.common.collect.ImmutableMap;
import com.mojang.datafixers.DSL;
import com.mojang.datafixers.DataFix;
import com.mojang.datafixers.DataFixUtils;
import com.mojang.datafixers.FieldFinder;
import com.mojang.datafixers.OpticFinder;
import com.mojang.datafixers.TypeRewriteRule;
import com.mojang.datafixers.Typed;
import com.mojang.datafixers.schemas.Schema;
import com.mojang.datafixers.types.Type;
import com.mojang.datafixers.types.templates.CompoundList.CompoundListType;
import com.mojang.datafixers.types.templates.TaggedChoice.TaggedChoiceType;
import com.mojang.datafixers.util.Either;
import com.mojang.datafixers.util.Pair;
import com.mojang.datafixers.util.Unit;
import com.mojang.serialization.Dynamic;
import java.util.List;
import net.minecraft.util.datafix.schemas.NamespacedSchema;

public class MissingDimensionFix extends DataFix {
   public MissingDimensionFix(Schema var1, boolean var2) {
      super(var1, var2);
   }

   private static <A> Type<Pair<A, Dynamic<?>>> fields(String var0, Type<A> var1) {
      return DSL.and(DSL.field(var0, var1), DSL.remainderType());
   }

   private static <A> Type<Pair<Either<A, Unit>, Dynamic<?>>> optionalFields(String var0, Type<A> var1) {
      return DSL.and(DSL.optional(DSL.field(var0, var1)), DSL.remainderType());
   }

   private static <A1, A2> Type<Pair<Either<A1, Unit>, Pair<Either<A2, Unit>, Dynamic<?>>>> optionalFields(String var0, Type<A1> var1, String var2, Type<A2> var3) {
      return DSL.and(DSL.optional(DSL.field(var0, var1)), DSL.optional(DSL.field(var2, var3)), DSL.remainderType());
   }

   protected TypeRewriteRule makeRule() {
      Schema var1 = this.getInputSchema();
      TaggedChoiceType var2 = new TaggedChoiceType("type", DSL.string(), ImmutableMap.of("minecraft:debug", DSL.remainderType(), "minecraft:flat", optionalFields("settings", optionalFields("biome", var1.getType(References.BIOME), "layers", DSL.list(optionalFields("block", var1.getType(References.BLOCK_NAME))))), "minecraft:noise", optionalFields("biome_source", DSL.taggedChoiceType("type", DSL.string(), ImmutableMap.of("minecraft:fixed", fields("biome", var1.getType(References.BIOME)), "minecraft:multi_noise", DSL.list(fields("biome", var1.getType(References.BIOME))), "minecraft:checkerboard", fields("biomes", DSL.list(var1.getType(References.BIOME))), "minecraft:vanilla_layered", DSL.remainderType(), "minecraft:the_end", DSL.remainderType())), "settings", DSL.or(DSL.string(), optionalFields("default_block", var1.getType(References.BLOCK_NAME), "default_fluid", var1.getType(References.BLOCK_NAME))))));
      CompoundListType var3 = DSL.compoundList(NamespacedSchema.namespacedString(), fields("generator", var2));
      Type var4 = DSL.and(var3, DSL.remainderType());
      Type var5 = var1.getType(References.WORLD_GEN_SETTINGS);
      FieldFinder var6 = new FieldFinder("dimensions", var4);
      if (!var5.findFieldType("dimensions").equals(var4)) {
         throw new IllegalStateException();
      } else {
         OpticFinder var7 = var3.finder();
         return this.fixTypeEverywhereTyped("MissingDimensionFix", var5, (var4x) -> {
            return var4x.updateTyped(var6, (var4) -> {
               return var4.updateTyped(var7, (var3x) -> {
                  if (!(var3x.getValue() instanceof List)) {
                     throw new IllegalStateException("List exptected");
                  } else if (((List)var3x.getValue()).isEmpty()) {
                     Dynamic var4 = (Dynamic)var4x.get(DSL.remainderFinder());
                     Dynamic var5 = this.recreateSettings(var4);
                     return (Typed)DataFixUtils.orElse(var3.readTyped(var5).result().map(Pair::getFirst), var3x);
                  } else {
                     return var3x;
                  }
               });
            });
         });
      }
   }

   private <T> Dynamic<T> recreateSettings(Dynamic<T> var1) {
      long var2 = var1.get("seed").asLong(0L);
      return new Dynamic(var1.getOps(), WorldGenSettingsFix.vanillaLevels(var1, var2, WorldGenSettingsFix.defaultOverworld(var1, var2), false));
   }
}
