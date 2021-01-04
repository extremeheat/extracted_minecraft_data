package net.minecraft.util.datafix.fixes;

import com.mojang.datafixers.DSL;
import com.mojang.datafixers.DataFix;
import com.mojang.datafixers.Dynamic;
import com.mojang.datafixers.TypeRewriteRule;
import com.mojang.datafixers.schemas.Schema;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Stream;

public class EntityRedundantChanceTagsFix extends DataFix {
   public EntityRedundantChanceTagsFix(Schema var1, boolean var2) {
      super(var1, var2);
   }

   public TypeRewriteRule makeRule() {
      return this.fixTypeEverywhereTyped("EntityRedundantChanceTagsFix", this.getInputSchema().getType(References.ENTITY), (var0) -> {
         return var0.update(DSL.remainderFinder(), (var0x) -> {
            Dynamic var1 = var0x;
            if (Objects.equals(var0x.get("HandDropChances"), Optional.of(var0x.createList(Stream.generate(() -> {
               return var0x.createFloat(0.0F);
            }).limit(2L))))) {
               var0x = var0x.remove("HandDropChances");
            }

            if (Objects.equals(var0x.get("ArmorDropChances"), Optional.of(var0x.createList(Stream.generate(() -> {
               return var1.createFloat(0.0F);
            }).limit(4L))))) {
               var0x = var0x.remove("ArmorDropChances");
            }

            return var0x;
         });
      });
   }
}
