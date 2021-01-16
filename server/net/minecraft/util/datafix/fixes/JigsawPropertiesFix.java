package net.minecraft.util.datafix.fixes;

import com.mojang.datafixers.DSL;
import com.mojang.datafixers.Typed;
import com.mojang.datafixers.schemas.Schema;
import com.mojang.serialization.Dynamic;

public class JigsawPropertiesFix extends NamedEntityFix {
   public JigsawPropertiesFix(Schema var1, boolean var2) {
      super(var1, var2, "JigsawPropertiesFix", References.BLOCK_ENTITY, "minecraft:jigsaw");
   }

   private static Dynamic<?> fixTag(Dynamic<?> var0) {
      String var1 = var0.get("attachement_type").asString("minecraft:empty");
      String var2 = var0.get("target_pool").asString("minecraft:empty");
      return var0.set("name", var0.createString(var1)).set("target", var0.createString(var1)).remove("attachement_type").set("pool", var0.createString(var2)).remove("target_pool");
   }

   protected Typed<?> fix(Typed<?> var1) {
      return var1.update(DSL.remainderFinder(), JigsawPropertiesFix::fixTag);
   }
}
