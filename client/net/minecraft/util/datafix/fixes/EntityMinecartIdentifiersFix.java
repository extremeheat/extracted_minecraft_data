package net.minecraft.util.datafix.fixes;

import com.mojang.datafixers.DSL;
import com.mojang.datafixers.Typed;
import com.mojang.datafixers.schemas.Schema;
import com.mojang.datafixers.types.Type;
import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.Dynamic;
import net.minecraft.Util;

public class EntityMinecartIdentifiersFix extends EntityRenameFix {
   public EntityMinecartIdentifiersFix(Schema var1) {
      super("EntityMinecartIdentifiersFix", var1, true);
   }

   protected Pair<String, Typed<?>> fix(String var1, Typed<?> var2) {
      if (!var1.equals("Minecart")) {
         return Pair.of(var1, var2);
      } else {
         int var3 = ((Dynamic)var2.getOrCreate(DSL.remainderFinder())).get("Type").asInt(0);
         String var10000;
         switch (var3) {
            case 1 -> var10000 = "MinecartChest";
            case 2 -> var10000 = "MinecartFurnace";
            default -> var10000 = "MinecartRideable";
         }

         String var4 = var10000;
         Type var5 = (Type)this.getOutputSchema().findChoiceType(References.ENTITY).types().get(var4);
         return Pair.of(var4, Util.writeAndReadTypedOrThrow(var2, var5, (var0) -> var0.remove("Type")));
      }
   }
}
