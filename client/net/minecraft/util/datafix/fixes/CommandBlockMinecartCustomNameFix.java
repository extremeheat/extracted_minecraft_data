package net.minecraft.util.datafix.fixes;

import com.mojang.datafixers.DSL;
import com.mojang.datafixers.DataFix;
import com.mojang.datafixers.OpticFinder;
import com.mojang.datafixers.TypeRewriteRule;
import com.mojang.datafixers.schemas.Schema;
import com.mojang.datafixers.types.Type;
import com.mojang.datafixers.util.Pair;
import net.minecraft.util.datafix.LegacyComponentDataFixUtils;
import net.minecraft.util.datafix.schemas.NamespacedSchema;

public class CommandBlockMinecartCustomNameFix extends DataFix {
   public CommandBlockMinecartCustomNameFix(final Schema outputSchema, final boolean changesType) {
      super(outputSchema, changesType);
   }

   protected TypeRewriteRule makeRule() {
      Type<?> entityType = this.getInputSchema().getType(References.ENTITY);
      OpticFinder<String> idFinder = DSL.fieldFinder("id", NamespacedSchema.namespacedString());
      OpticFinder<?> customNameFinder = entityType.findField("CustomName");
      OpticFinder<Pair<String, String>> componentFinder = DSL.typeFinder(this.getInputSchema().getType(References.TEXT_COMPONENT));
      return this.fixTypeEverywhereTyped("Command block minecart custom name fix", entityType, (input) -> {
         String id = (String)input.getOptional(idFinder).orElse("");
         return !"minecraft:commandblock_minecart".equals(id) ? input : input.updateTyped(customNameFinder, (customName) -> customName.update(componentFinder, (pair) -> pair.mapSecond(LegacyComponentDataFixUtils::createTextComponentJson)));
      });
   }
}
