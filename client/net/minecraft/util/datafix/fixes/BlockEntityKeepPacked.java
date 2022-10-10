package net.minecraft.util.datafix.fixes;

import com.mojang.datafixers.DSL;
import com.mojang.datafixers.Dynamic;
import com.mojang.datafixers.Typed;
import com.mojang.datafixers.schemas.Schema;
import net.minecraft.util.datafix.TypeReferences;

public class BlockEntityKeepPacked extends NamedEntityFix {
   public BlockEntityKeepPacked(Schema var1, boolean var2) {
      super(var1, var2, "BlockEntityKeepPacked", TypeReferences.field_211294_j, "DUMMY");
   }

   private static Dynamic<?> func_209645_a(Dynamic<?> var0) {
      return var0.set("keepPacked", var0.createBoolean(true));
   }

   protected Typed<?> func_207419_a(Typed<?> var1) {
      return var1.update(DSL.remainderFinder(), BlockEntityKeepPacked::func_209645_a);
   }
}
