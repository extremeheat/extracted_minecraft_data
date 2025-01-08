package net.minecraft.util.datafix.fixes;

import com.mojang.datafixers.DSL;
import com.mojang.datafixers.OpticFinder;
import com.mojang.datafixers.Typed;
import com.mojang.datafixers.schemas.Schema;

public class OminousBannerBlockEntityRenameFix extends NamedEntityFix {
   public OminousBannerBlockEntityRenameFix(Schema var1, boolean var2) {
      super(var1, var2, "OminousBannerBlockEntityRenameFix", References.BLOCK_ENTITY, "minecraft:banner");
   }

   protected Typed<?> fix(Typed<?> var1) {
      OpticFinder var2 = var1.getType().findField("CustomName");
      OpticFinder var3 = DSL.typeFinder(this.getInputSchema().getType(References.TEXT_COMPONENT));
      return var1.updateTyped(var2, (var1x) -> var1x.update(var3, (var0) -> var0.mapSecond((var0x) -> var0x.replace("\"translate\":\"block.minecraft.illager_banner\"", "\"translate\":\"block.minecraft.ominous_banner\""))));
   }
}
