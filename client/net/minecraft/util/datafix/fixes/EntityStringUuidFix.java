package net.minecraft.util.datafix.fixes;

import com.mojang.datafixers.DSL;
import com.mojang.datafixers.DataFix;
import com.mojang.datafixers.TypeRewriteRule;
import com.mojang.datafixers.schemas.Schema;
import java.util.Optional;
import java.util.UUID;

public class EntityStringUuidFix extends DataFix {
   public EntityStringUuidFix(Schema var1, boolean var2) {
      super(var1, var2);
   }

   public TypeRewriteRule makeRule() {
      return this.fixTypeEverywhereTyped("EntityStringUuidFix", this.getInputSchema().getType(References.ENTITY), (var0) -> {
         return var0.update(DSL.remainderFinder(), (var0x) -> {
            Optional var1 = var0x.get("UUID").asString().result();
            if (var1.isPresent()) {
               UUID var2 = UUID.fromString((String)var1.get());
               return var0x.remove("UUID").set("UUIDMost", var0x.createLong(var2.getMostSignificantBits())).set("UUIDLeast", var0x.createLong(var2.getLeastSignificantBits()));
            } else {
               return var0x;
            }
         });
      });
   }
}
