package net.minecraft.util.datafix.fixes;

import com.mojang.datafixers.DSL;
import com.mojang.datafixers.DataFix;
import com.mojang.datafixers.Dynamic;
import com.mojang.datafixers.TypeRewriteRule;
import com.mojang.datafixers.schemas.Schema;
import java.util.UUID;
import net.minecraft.util.datafix.TypeReferences;

public class StringToUUID extends DataFix {
   public StringToUUID(Schema var1, boolean var2) {
      super(var1, var2);
   }

   public TypeRewriteRule makeRule() {
      return this.fixTypeEverywhereTyped("EntityStringUuidFix", this.getInputSchema().getType(TypeReferences.field_211299_o), (var0) -> {
         return var0.update(DSL.remainderFinder(), (var0x) -> {
            if (var0x.get("UUID").flatMap(Dynamic::getStringValue).isPresent()) {
               UUID var1 = UUID.fromString(var0x.getString("UUID"));
               return var0x.remove("UUID").set("UUIDMost", var0x.createLong(var1.getMostSignificantBits())).set("UUIDLeast", var0x.createLong(var1.getLeastSignificantBits()));
            } else {
               return var0x;
            }
         });
      });
   }
}
