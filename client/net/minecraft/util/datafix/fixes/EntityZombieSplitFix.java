package net.minecraft.util.datafix.fixes;

import com.google.common.base.Suppliers;
import com.mojang.datafixers.DSL;
import com.mojang.datafixers.Typed;
import com.mojang.datafixers.schemas.Schema;
import com.mojang.datafixers.types.Type;
import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.Dynamic;
import java.util.function.Supplier;
import net.minecraft.Util;

public class EntityZombieSplitFix extends EntityRenameFix {
   private final Supplier<Type<?>> zombieVillagerType = Suppliers.memoize(() -> this.getOutputSchema().getChoiceType(References.ENTITY, "ZombieVillager"));

   public EntityZombieSplitFix(Schema var1) {
      super("EntityZombieSplitFix", var1, true);
   }

   protected Pair<String, Typed<?>> fix(String var1, Typed<?> var2) {
      if (!var1.equals("Zombie")) {
         return Pair.of(var1, var2);
      } else {
         Dynamic var3 = (Dynamic)var2.getOptional(DSL.remainderFinder()).orElseThrow();
         int var4 = var3.get("ZombieType").asInt(0);
         String var5;
         Typed var6;
         switch (var4) {
            case 1:
            case 2:
            case 3:
            case 4:
            case 5:
               var5 = "ZombieVillager";
               var6 = this.changeSchemaToZombieVillager(var2, var4 - 1);
               break;
            case 6:
               var5 = "Husk";
               var6 = var2;
               break;
            default:
               var5 = "Zombie";
               var6 = var2;
         }

         return Pair.of(var5, var6.update(DSL.remainderFinder(), (var0) -> var0.remove("ZombieType")));
      }
   }

   private Typed<?> changeSchemaToZombieVillager(Typed<?> var1, int var2) {
      return Util.writeAndReadTypedOrThrow(var1, (Type)this.zombieVillagerType.get(), (var1x) -> var1x.set("Profession", var1x.createInt(var2)));
   }
}
