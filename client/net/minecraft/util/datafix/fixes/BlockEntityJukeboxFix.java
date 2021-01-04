package net.minecraft.util.datafix.fixes;

import com.mojang.datafixers.DSL;
import com.mojang.datafixers.Dynamic;
import com.mojang.datafixers.OpticFinder;
import com.mojang.datafixers.Typed;
import com.mojang.datafixers.schemas.Schema;
import com.mojang.datafixers.types.Type;
import java.util.Optional;

public class BlockEntityJukeboxFix extends NamedEntityFix {
   public BlockEntityJukeboxFix(Schema var1, boolean var2) {
      super(var1, var2, "BlockEntityJukeboxFix", References.BLOCK_ENTITY, "minecraft:jukebox");
   }

   protected Typed<?> fix(Typed<?> var1) {
      Type var2 = this.getInputSchema().getChoiceType(References.BLOCK_ENTITY, "minecraft:jukebox");
      Type var3 = var2.findFieldType("RecordItem");
      OpticFinder var4 = DSL.fieldFinder("RecordItem", var3);
      Dynamic var5 = (Dynamic)var1.get(DSL.remainderFinder());
      int var6 = var5.get("Record").asInt(0);
      if (var6 > 0) {
         var5.remove("Record");
         String var7 = ItemStackTheFlatteningFix.updateItem(ItemIdFix.getItem(var6), 0);
         if (var7 != null) {
            Dynamic var8 = var5.emptyMap();
            var8 = var8.set("id", var8.createString(var7));
            var8 = var8.set("Count", var8.createByte((byte)1));
            return var1.set(var4, (Typed)((Optional)var3.readTyped(var8).getSecond()).orElseThrow(() -> {
               return new IllegalStateException("Could not create record item stack.");
            })).set(DSL.remainderFinder(), var5);
         }
      }

      return var1;
   }
}
