package net.minecraft.util.datafix.fixes;

import com.mojang.datafixers.DSL;
import com.mojang.datafixers.DataFix;
import com.mojang.datafixers.OpticFinder;
import com.mojang.datafixers.TypeRewriteRule;
import com.mojang.datafixers.schemas.Schema;
import com.mojang.datafixers.types.Type;
import com.mojang.datafixers.types.templates.List;
import com.mojang.serialization.Dynamic;
import net.minecraft.util.datafix.ExtraDataFixUtils;

public class BeehiveFieldRenameFix extends DataFix {
   public BeehiveFieldRenameFix(Schema var1) {
      super(var1, true);
   }

   private Dynamic<?> fixBeehive(Dynamic<?> var1) {
      return var1.remove("Bees");
   }

   private Dynamic<?> fixBee(Dynamic<?> var1) {
      var1 = var1.remove("EntityData");
      var1 = var1.renameField("TicksInHive", "ticks_in_hive");
      var1 = var1.renameField("MinOccupationTicks", "min_ticks_in_hive");
      return var1;
   }

   public TypeRewriteRule makeRule() {
      Type var1 = this.getInputSchema().getChoiceType(References.BLOCK_ENTITY, "minecraft:beehive");
      OpticFinder var2 = DSL.namedChoice("minecraft:beehive", var1);
      List.ListType var3 = (List.ListType)var1.findFieldType("Bees");
      Type var4 = var3.getElement();
      OpticFinder var5 = DSL.fieldFinder("Bees", var3);
      OpticFinder var6 = DSL.typeFinder(var4);
      Type var7 = this.getInputSchema().getType(References.BLOCK_ENTITY);
      Type var8 = this.getOutputSchema().getType(References.BLOCK_ENTITY);
      return this.fixTypeEverywhereTyped("BeehiveFieldRenameFix", var7, var8, (var5x) -> ExtraDataFixUtils.cast(var8, var5x.updateTyped(var2, (var3) -> var3.update(DSL.remainderFinder(), this::fixBeehive).updateTyped(var5, (var2) -> var2.updateTyped(var6, (var1) -> var1.update(DSL.remainderFinder(), this::fixBee))))));
   }
}
