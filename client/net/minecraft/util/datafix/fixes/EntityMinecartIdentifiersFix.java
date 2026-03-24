package net.minecraft.util.datafix.fixes;

import com.mojang.datafixers.DSL;
import com.mojang.datafixers.Typed;
import com.mojang.datafixers.schemas.Schema;
import com.mojang.datafixers.types.Type;
import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.Dynamic;
import net.minecraft.util.Util;

public class EntityMinecartIdentifiersFix extends EntityRenameFix {
   public EntityMinecartIdentifiersFix(final Schema outputSchema) {
      super("EntityMinecartIdentifiersFix", outputSchema, true);
   }

   protected Pair<String, Typed<?>> fix(final String name, final Typed<?> entity) {
      if (!name.equals("Minecart")) {
         return Pair.of(name, entity);
      } else {
         int id = ((Dynamic)entity.getOrCreate(DSL.remainderFinder())).get("Type").asInt(0);
         String var10000;
         switch (id) {
            case 1 -> var10000 = "MinecartChest";
            case 2 -> var10000 = "MinecartFurnace";
            default -> var10000 = "MinecartRideable";
         }

         String newName = var10000;
         Type<?> newType = (Type)this.getOutputSchema().findChoiceType(References.ENTITY).types().get(newName);
         return Pair.of(newName, Util.writeAndReadTypedOrThrow(entity, newType, (dynamic) -> dynamic.remove("Type")));
      }
   }
}
