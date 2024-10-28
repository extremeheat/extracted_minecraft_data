package net.minecraft.util.datafix.fixes;

import com.mojang.datafixers.schemas.Schema;
import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.Dynamic;
import java.util.Objects;

public class EntityCatSplitFix extends SimpleEntityRenameFix {
   public EntityCatSplitFix(Schema var1, boolean var2) {
      super("EntityCatSplitFix", var1, var2);
   }

   protected Pair<String, Dynamic<?>> getNewNameAndTag(String var1, Dynamic<?> var2) {
      if (Objects.equals("minecraft:ocelot", var1)) {
         int var3 = var2.get("CatType").asInt(0);
         if (var3 == 0) {
            String var4 = var2.get("Owner").asString("");
            String var5 = var2.get("OwnerUUID").asString("");
            if (var4.length() > 0 || var5.length() > 0) {
               var2.set("Trusting", var2.createBoolean(true));
            }
         } else if (var3 > 0 && var3 < 4) {
            var2 = var2.set("CatType", var2.createInt(var3));
            var2 = var2.set("OwnerUUID", var2.createString(var2.get("OwnerUUID").asString("")));
            return Pair.of("minecraft:cat", var2);
         }
      }

      return Pair.of(var1, var2);
   }
}
