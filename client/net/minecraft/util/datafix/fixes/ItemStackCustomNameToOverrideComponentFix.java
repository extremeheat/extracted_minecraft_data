package net.minecraft.util.datafix.fixes;

import com.mojang.datafixers.DSL;
import com.mojang.datafixers.DataFix;
import com.mojang.datafixers.OpticFinder;
import com.mojang.datafixers.TypeRewriteRule;
import com.mojang.datafixers.schemas.Schema;
import com.mojang.datafixers.types.Type;
import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.Dynamic;
import com.mojang.serialization.OptionalDynamic;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.function.Predicate;
import net.minecraft.util.datafix.ComponentDataFixUtils;
import net.minecraft.util.datafix.schemas.NamespacedSchema;

public class ItemStackCustomNameToOverrideComponentFix extends DataFix {
   private static final Set<String> MAP_NAMES = Set.of("filled_map.buried_treasure", "filled_map.explorer_jungle", "filled_map.explorer_swamp", "filled_map.mansion", "filled_map.monument", "filled_map.trial_chambers", "filled_map.village_desert", "filled_map.village_plains", "filled_map.village_savanna", "filled_map.village_snowy", "filled_map.village_taiga");

   public ItemStackCustomNameToOverrideComponentFix(Schema var1) {
      super(var1, false);
   }

   public final TypeRewriteRule makeRule() {
      Type var1 = this.getInputSchema().getType(References.ITEM_STACK);
      OpticFinder var2 = DSL.fieldFinder("id", DSL.named(References.ITEM_NAME.typeName(), NamespacedSchema.namespacedString()));
      OpticFinder var3 = var1.findField("components");
      return this.fixTypeEverywhereTyped("ItemStack custom_name to item_name component fix", var1, (var2x) -> {
         Optional var3x = var2x.getOptional(var2);
         Optional var4 = var3x.map(Pair::getSecond);
         if (var4.filter((var0) -> {
            return var0.equals("minecraft:white_banner");
         }).isPresent()) {
            return var2x.updateTyped(var3, (var0) -> {
               return var0.update(DSL.remainderFinder(), ItemStackCustomNameToOverrideComponentFix::fixBanner);
            });
         } else {
            return var4.filter((var0) -> {
               return var0.equals("minecraft:filled_map");
            }).isPresent() ? var2x.updateTyped(var3, (var0) -> {
               return var0.update(DSL.remainderFinder(), ItemStackCustomNameToOverrideComponentFix::fixMap);
            }) : var2x;
         }
      });
   }

   private static <T> Dynamic<T> fixMap(Dynamic<T> var0) {
      Set var10001 = MAP_NAMES;
      Objects.requireNonNull(var10001);
      return fixCustomName(var0, var10001::contains);
   }

   private static <T> Dynamic<T> fixBanner(Dynamic<T> var0) {
      return fixCustomName(var0, (var0x) -> {
         return var0x.equals("block.minecraft.ominous_banner");
      });
   }

   private static <T> Dynamic<T> fixCustomName(Dynamic<T> var0, Predicate<String> var1) {
      OptionalDynamic var2 = var0.get("minecraft:custom_name");
      Optional var3 = var2.asString().result().flatMap(ComponentDataFixUtils::extractTranslationString).filter(var1);
      return var3.isPresent() ? var0.renameField("minecraft:custom_name", "minecraft:item_name") : var0;
   }
}
