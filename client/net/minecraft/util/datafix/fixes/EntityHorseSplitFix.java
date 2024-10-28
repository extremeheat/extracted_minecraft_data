package net.minecraft.util.datafix.fixes;

import com.mojang.datafixers.DSL;
import com.mojang.datafixers.Typed;
import com.mojang.datafixers.schemas.Schema;
import com.mojang.datafixers.types.Type;
import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.Dynamic;
import java.util.Objects;
import net.minecraft.Util;

public class EntityHorseSplitFix extends EntityRenameFix {
   public EntityHorseSplitFix(Schema var1, boolean var2) {
      super("EntityHorseSplitFix", var1, var2);
   }

   protected Pair<String, Typed<?>> fix(String var1, Typed<?> var2) {
      if (Objects.equals("EntityHorse", var1)) {
         Dynamic var3 = (Dynamic)var2.get(DSL.remainderFinder());
         int var4 = var3.get("Type").asInt(0);
         String var10000;
         switch (var4) {
            case 1 -> var10000 = "Donkey";
            case 2 -> var10000 = "Mule";
            case 3 -> var10000 = "ZombieHorse";
            case 4 -> var10000 = "SkeletonHorse";
            default -> var10000 = "Horse";
         }

         String var5 = var10000;
         Type var6 = (Type)this.getOutputSchema().findChoiceType(References.ENTITY).types().get(var5);
         return Pair.of(var5, Util.writeAndReadTypedOrThrow(var2, var6, (var0) -> {
            return var0.remove("Type");
         }));
      } else {
         return Pair.of(var1, var2);
      }
   }
}
