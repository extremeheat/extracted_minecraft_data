package net.minecraft.util.datafix.fixes;

import com.mojang.datafixers.DSL;
import com.mojang.datafixers.Typed;
import com.mojang.datafixers.schemas.Schema;
import com.mojang.datafixers.types.Type;
import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.Dynamic;
import java.util.Objects;

public class EntityHorseSplitFix extends EntityRenameFix {
   public EntityHorseSplitFix(Schema var1, boolean var2) {
      super("EntityHorseSplitFix", var1, var2);
   }

   protected Pair<String, Typed<?>> fix(String var1, Typed<?> var2) {
      Dynamic var3 = (Dynamic)var2.get(DSL.remainderFinder());
      if (Objects.equals("EntityHorse", var1)) {
         int var5 = var3.get("Type").asInt(0);
         String var4;
         switch(var5) {
         case 0:
         default:
            var4 = "Horse";
            break;
         case 1:
            var4 = "Donkey";
            break;
         case 2:
            var4 = "Mule";
            break;
         case 3:
            var4 = "ZombieHorse";
            break;
         case 4:
            var4 = "SkeletonHorse";
         }

         var3.remove("Type");
         Type var6 = (Type)this.getOutputSchema().findChoiceType(References.ENTITY).types().get(var4);
         DataResult var10001 = var2.write();
         Objects.requireNonNull(var6);
         return Pair.of(var4, (Typed)((Pair)var10001.flatMap(var6::readTyped).result().orElseThrow(() -> {
            return new IllegalStateException("Could not parse the new horse");
         })).getFirst());
      } else {
         return Pair.of(var1, var2);
      }
   }
}
