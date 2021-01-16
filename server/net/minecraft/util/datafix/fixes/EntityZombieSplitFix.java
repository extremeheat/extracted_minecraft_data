package net.minecraft.util.datafix.fixes;

import com.mojang.datafixers.schemas.Schema;
import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.Dynamic;
import java.util.Objects;

public class EntityZombieSplitFix extends SimpleEntityRenameFix {
   public EntityZombieSplitFix(Schema var1, boolean var2) {
      super("EntityZombieSplitFix", var1, var2);
   }

   protected Pair<String, Dynamic<?>> getNewNameAndTag(String var1, Dynamic<?> var2) {
      if (Objects.equals("Zombie", var1)) {
         String var3 = "Zombie";
         int var4 = var2.get("ZombieType").asInt(0);
         switch(var4) {
         case 0:
         default:
            break;
         case 1:
         case 2:
         case 3:
         case 4:
         case 5:
            var3 = "ZombieVillager";
            var2 = var2.set("Profession", var2.createInt(var4 - 1));
            break;
         case 6:
            var3 = "Husk";
         }

         var2 = var2.remove("ZombieType");
         return Pair.of(var3, var2);
      } else {
         return Pair.of(var1, var2);
      }
   }
}
