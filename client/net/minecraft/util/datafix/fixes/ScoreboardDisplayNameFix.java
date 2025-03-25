package net.minecraft.util.datafix.fixes;

import com.mojang.datafixers.DSL;
import com.mojang.datafixers.DataFix;
import com.mojang.datafixers.OpticFinder;
import com.mojang.datafixers.TypeRewriteRule;
import com.mojang.datafixers.schemas.Schema;
import com.mojang.datafixers.types.Type;
import net.minecraft.util.datafix.LegacyComponentDataFixUtils;

public class ScoreboardDisplayNameFix extends DataFix {
   private final String name;
   private final DSL.TypeReference type;

   public ScoreboardDisplayNameFix(Schema var1, String var2, DSL.TypeReference var3) {
      super(var1, false);
      this.name = var2;
      this.type = var3;
   }

   protected TypeRewriteRule makeRule() {
      Type var1 = this.getInputSchema().getType(this.type);
      OpticFinder var2 = var1.findField("DisplayName");
      OpticFinder var3 = DSL.typeFinder(this.getInputSchema().getType(References.TEXT_COMPONENT));
      return this.fixTypeEverywhereTyped(this.name, var1, (var2x) -> var2x.updateTyped(var2, (var1) -> var1.update(var3, (var0) -> var0.mapSecond(LegacyComponentDataFixUtils::createTextComponentJson))));
   }
}
