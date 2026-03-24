package net.minecraft.util.datafix.fixes;

import com.mojang.datafixers.DSL;
import com.mojang.datafixers.Typed;
import com.mojang.datafixers.schemas.Schema;
import com.mojang.datafixers.types.Type;
import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.Dynamic;
import java.util.Objects;
import net.minecraft.util.Util;

public class EntityHorseSplitFix extends EntityRenameFix {
   public EntityHorseSplitFix(final Schema outputSchema, final boolean changesType) {
      super("EntityHorseSplitFix", outputSchema, changesType);
   }

   protected Pair<String, Typed<?>> fix(final String name, final Typed<?> entity) {
      if (Objects.equals("EntityHorse", name)) {
         Dynamic<?> tag = (Dynamic)entity.get(DSL.remainderFinder());
         int type = tag.get("Type").asInt(0);
         String var10000;
         switch (type) {
            case 1 -> var10000 = "Donkey";
            case 2 -> var10000 = "Mule";
            case 3 -> var10000 = "ZombieHorse";
            case 4 -> var10000 = "SkeletonHorse";
            default -> var10000 = "Horse";
         }

         String newName = var10000;
         Type<?> newType = (Type)this.getOutputSchema().findChoiceType(References.ENTITY).types().get(newName);
         return Pair.of(newName, Util.writeAndReadTypedOrThrow(entity, newType, (dynamic) -> dynamic.remove("Type")));
      } else {
         return Pair.of(name, entity);
      }
   }
}
