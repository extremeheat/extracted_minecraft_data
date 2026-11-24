package net.minecraft.util.datafix.fixes;

import com.mojang.datafixers.DSL;
import com.mojang.datafixers.DataFix;
import com.mojang.datafixers.TypeRewriteRule;
import com.mojang.datafixers.schemas.Schema;
import com.mojang.serialization.Dynamic;
import java.util.Locale;

public class LocatorBarIconStyleFix extends DataFix {
   private final DSL.TypeReference type;

   public LocatorBarIconStyleFix(final Schema outputSchema, final DSL.TypeReference type) {
      super(outputSchema, false);
      this.type = type;
   }

   protected TypeRewriteRule makeRule() {
      return this.fixTypeEverywhereTyped("Adding field `style` to type `" + this.type.typeName().toLowerCase(Locale.ROOT) + "`", this.getInputSchema().getType(this.type), (input) -> input.update(DSL.remainderFinder(), this::addField));
   }

   private Dynamic<?> addField(final Dynamic<?> dynamic) {
      return dynamic.update("locator_bar_icon", (field) -> field.set("style", dynamic.createString("minecraft:default")));
   }
}
