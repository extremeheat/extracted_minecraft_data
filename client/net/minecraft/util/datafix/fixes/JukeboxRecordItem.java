package net.minecraft.util.datafix.fixes;

import com.mojang.datafixers.DSL;
import com.mojang.datafixers.Dynamic;
import com.mojang.datafixers.OpticFinder;
import com.mojang.datafixers.Typed;
import com.mojang.datafixers.schemas.Schema;
import com.mojang.datafixers.types.Type;
import java.util.Optional;
import net.minecraft.util.datafix.TypeReferences;

public class JukeboxRecordItem extends NamedEntityFix {
   public JukeboxRecordItem(Schema var1, boolean var2) {
      super(var1, var2, "BlockEntityJukeboxFix", TypeReferences.field_211294_j, "minecraft:jukebox");
   }

   protected Typed<?> func_207419_a(Typed<?> var1) {
      Type var2 = this.getInputSchema().getChoiceType(TypeReferences.field_211294_j, "minecraft:jukebox");
      Type var3 = var2.findFieldType("RecordItem");
      OpticFinder var4 = DSL.fieldFinder("RecordItem", var3);
      Dynamic var5 = (Dynamic)var1.get(DSL.remainderFinder());
      int var6 = var5.getInt("Record");
      if (var6 > 0) {
         var5.remove("Record");
         String var7 = ItemStackDataFlattening.func_199175_a(ItemIntIDToString.func_199173_a(var6), 0);
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
