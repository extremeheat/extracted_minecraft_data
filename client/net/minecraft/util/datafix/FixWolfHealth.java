package net.minecraft.util.datafix;

import com.mojang.datafixers.DSL;
import com.mojang.datafixers.Typed;
import com.mojang.datafixers.schemas.Schema;
import net.minecraft.util.datafix.fixes.NamedEntityFix;
import net.minecraft.util.datafix.fixes.References;
import net.minecraft.util.datafix.schemas.NamespacedSchema;
import org.apache.commons.lang3.mutable.MutableBoolean;

public class FixWolfHealth extends NamedEntityFix {
   private static final String WOLF_ID = "minecraft:wolf";
   private static final String WOLF_HEALTH = "minecraft:generic.max_health";

   public FixWolfHealth(Schema var1) {
      super(var1, false, "FixWolfHealth", References.ENTITY, "minecraft:wolf");
   }

   protected Typed<?> fix(Typed<?> var1) {
      return var1.update(DSL.remainderFinder(), (var0) -> {
         MutableBoolean var1 = new MutableBoolean(false);
         var0 = var0.update("Attributes", (var1x) -> var1x.createList(var1x.asStream().map((var1xx) -> "minecraft:generic.max_health".equals(NamespacedSchema.ensureNamespaced(var1xx.get("Name").asString(""))) ? var1xx.update("Base", (var1x) -> {
                  if (var1x.asDouble(0.0) == 20.0) {
                     var1.setTrue();
                     return var1x.createDouble(40.0);
                  } else {
                     return var1x;
                  }
               }) : var1xx)));
         if (var1.isTrue()) {
            var0 = var0.update("Health", (var0x) -> var0x.createFloat(var0x.asFloat(0.0F) * 2.0F));
         }

         return var0;
      });
   }
}
