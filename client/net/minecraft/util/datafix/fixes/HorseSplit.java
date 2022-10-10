package net.minecraft.util.datafix.fixes;

import com.mojang.datafixers.DSL;
import com.mojang.datafixers.Dynamic;
import com.mojang.datafixers.Typed;
import com.mojang.datafixers.schemas.Schema;
import com.mojang.datafixers.types.Type;
import com.mojang.datafixers.util.Pair;
import java.util.Objects;
import java.util.Optional;
import net.minecraft.util.datafix.TypeReferences;

public class HorseSplit extends EntityRename {
   public HorseSplit(Schema var1, boolean var2) {
      super("EntityHorseSplitFix", var1, var2);
   }

   protected Pair<String, Typed<?>> func_209149_a(String var1, Typed<?> var2) {
      Dynamic var3 = (Dynamic)var2.get(DSL.remainderFinder());
      if (Objects.equals("EntityHorse", var1)) {
         int var5 = var3.getInt("Type");
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
         Type var6 = (Type)this.getOutputSchema().findChoiceType(TypeReferences.field_211299_o).types().get(var4);
         return Pair.of(var4, ((Optional)var6.readTyped(var2.write()).getSecond()).orElseThrow(() -> {
            return new IllegalStateException("Could not parse the new horse");
         }));
      } else {
         return Pair.of(var1, var2);
      }
   }
}
