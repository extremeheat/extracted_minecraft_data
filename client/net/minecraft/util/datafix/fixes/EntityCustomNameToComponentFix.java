package net.minecraft.util.datafix.fixes;

import com.mojang.datafixers.DSL;
import com.mojang.datafixers.DataFix;
import com.mojang.datafixers.OpticFinder;
import com.mojang.datafixers.TypeRewriteRule;
import com.mojang.datafixers.schemas.Schema;
import com.mojang.serialization.Dynamic;
import java.util.Objects;
import java.util.Optional;
import net.minecraft.util.datafix.ComponentDataFixUtils;
import net.minecraft.util.datafix.schemas.NamespacedSchema;

public class EntityCustomNameToComponentFix extends DataFix {
   public EntityCustomNameToComponentFix(Schema var1, boolean var2) {
      super(var1, var2);
   }

   public TypeRewriteRule makeRule() {
      OpticFinder var1 = DSL.fieldFinder("id", NamespacedSchema.namespacedString());
      return this.fixTypeEverywhereTyped("EntityCustomNameToComponentFix", this.getInputSchema().getType(References.ENTITY), (var1x) -> {
         return var1x.update(DSL.remainderFinder(), (var2) -> {
            Optional var3 = var1x.getOptional(var1);
            return var3.isPresent() && Objects.equals(var3.get(), "minecraft:commandblock_minecart") ? var2 : fixTagCustomName(var2);
         });
      });
   }

   public static Dynamic<?> fixTagCustomName(Dynamic<?> var0) {
      String var1 = var0.get("CustomName").asString("");
      return var1.isEmpty() ? var0.remove("CustomName") : var0.set("CustomName", ComponentDataFixUtils.createPlainTextComponent(var0.getOps(), var1));
   }
}
