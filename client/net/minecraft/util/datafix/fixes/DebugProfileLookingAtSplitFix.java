package net.minecraft.util.datafix.fixes;

import com.mojang.datafixers.DSL;
import com.mojang.datafixers.DataFix;
import com.mojang.datafixers.DataFixUtils;
import com.mojang.datafixers.TypeRewriteRule;
import com.mojang.datafixers.schemas.Schema;
import com.mojang.serialization.Dynamic;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

public class DebugProfileLookingAtSplitFix extends DataFix {
   public DebugProfileLookingAtSplitFix(final Schema outputSchema) {
      super(outputSchema, false);
   }

   public TypeRewriteRule makeRule() {
      return this.fixTypeEverywhereTyped("DebugProfileLookingAtSplitFix", this.getInputSchema().getType(References.DEBUG_PROFILE), (input) -> input.update(DSL.remainderFinder(), (tag) -> tag.update("custom", DebugProfileLookingAtSplitFix::updateOptions)));
   }

   private static <T> Dynamic<T> updateOptions(final Dynamic<T> custom) {
      return (Dynamic)DataFixUtils.orElse(custom.getMapValues().map((map) -> {
         Map<Dynamic<T>, Dynamic<T>> newOptions = new HashMap();
         map.forEach((key, value) -> renamedKey(key).ifPresentOrElse((newKey) -> newOptions.putIfAbsent(newKey, value), () -> newOptions.put(key, value)));
         return custom.createMap(newOptions);
      }).result(), custom);
   }

   private static <T> Optional<Dynamic<T>> renamedKey(final Dynamic<T> keyDynamic) {
      Optional var10000 = keyDynamic.asString().result().flatMap((key) -> {
         Optional var10000;
         switch (key) {
            case "minecraft:looking_at_block" -> var10000 = Optional.of("minecraft:looking_at_block_state");
            case "minecraft:looking_at_fluid" -> var10000 = Optional.of("minecraft:looking_at_fluid_state");
            default -> var10000 = Optional.empty();
         }

         return var10000;
      });
      Objects.requireNonNull(keyDynamic);
      return var10000.map(keyDynamic::createString);
   }
}
