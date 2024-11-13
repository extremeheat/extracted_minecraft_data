package net.minecraft.util.datafix.fixes;

import com.mojang.datafixers.DSL;
import com.mojang.datafixers.DataFix;
import com.mojang.datafixers.DataFixUtils;
import com.mojang.datafixers.OpticFinder;
import com.mojang.datafixers.TypeRewriteRule;
import com.mojang.datafixers.schemas.Schema;
import com.mojang.datafixers.types.Type;
import com.mojang.datafixers.types.templates.CompoundList;
import com.mojang.serialization.Dynamic;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;
import net.minecraft.util.datafix.schemas.NamespacedSchema;

public class NewVillageFix extends DataFix {
   public NewVillageFix(Schema var1, boolean var2) {
      super(var1, var2);
   }

   protected TypeRewriteRule makeRule() {
      CompoundList.CompoundListType var1 = DSL.compoundList(DSL.string(), this.getInputSchema().getType(References.STRUCTURE_FEATURE));
      OpticFinder var2 = var1.finder();
      return this.cap(var1);
   }

   private <SF> TypeRewriteRule cap(CompoundList.CompoundListType<String, SF> var1) {
      Type var2 = this.getInputSchema().getType(References.CHUNK);
      Type var3 = this.getInputSchema().getType(References.STRUCTURE_FEATURE);
      OpticFinder var4 = var2.findField("Level");
      OpticFinder var5 = var4.type().findField("Structures");
      OpticFinder var6 = var5.type().findField("Starts");
      OpticFinder var7 = var1.finder();
      return TypeRewriteRule.seq(this.fixTypeEverywhereTyped("NewVillageFix", var2, (var4x) -> var4x.updateTyped(var4, (var3) -> var3.updateTyped(var5, (var2) -> var2.updateTyped(var6, (var1) -> var1.update(var7, (var0) -> (List)var0.stream().filter((var0x) -> !Objects.equals(var0x.getFirst(), "Village")).map((var0x) -> var0x.mapFirst((var0) -> var0.equals("New_Village") ? "Village" : var0)).collect(Collectors.toList()))).update(DSL.remainderFinder(), (var0) -> var0.update("References", (var0x) -> {
                     Optional var1 = var0x.get("New_Village").result();
                     return ((Dynamic)DataFixUtils.orElse(var1.map((var1x) -> var0x.remove("New_Village").set("Village", var1x)), var0x)).remove("Village");
                  }))))), this.fixTypeEverywhereTyped("NewVillageStartFix", var3, (var0) -> var0.update(DSL.remainderFinder(), (var0x) -> var0x.update("id", (var0) -> Objects.equals(NamespacedSchema.ensureNamespaced(var0.asString("")), "minecraft:new_village") ? var0.createString("minecraft:village") : var0))));
   }
}
