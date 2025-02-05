package net.minecraft.util.datafix.fixes;

import com.mojang.datafixers.DataFix;
import com.mojang.datafixers.TypeRewriteRule;
import com.mojang.datafixers.schemas.Schema;
import com.mojang.datafixers.types.Type;
import java.util.HashMap;
import java.util.Map;

public class PlayerEquipmentFix extends DataFix {
   private static final Map<Integer, String> SLOT_TRANSLATIONS = Map.of(100, "feet", 101, "legs", 102, "chest", 103, "head", -106, "offhand");

   public PlayerEquipmentFix(Schema var1) {
      super(var1, true);
   }

   protected TypeRewriteRule makeRule() {
      Type var1 = this.getInputSchema().getTypeRaw(References.PLAYER);
      Type var2 = this.getOutputSchema().getTypeRaw(References.PLAYER);
      return this.writeFixAndRead("Player Equipment Fix", var1, var2, (var0) -> {
         HashMap var1 = new HashMap();
         var0 = var0.update("Inventory", (var1x) -> var1x.createList(var1x.asStream().filter((var2) -> {
               int var3 = var2.get("Slot").asInt(-1);
               String var4 = (String)SLOT_TRANSLATIONS.get(var3);
               if (var4 != null) {
                  var1.put(var1x.createString(var4), var2.remove("Slot"));
               }

               return var4 == null;
            })));
         var0 = var0.set("equipment", var0.createMap(var1));
         return var0;
      });
   }
}
