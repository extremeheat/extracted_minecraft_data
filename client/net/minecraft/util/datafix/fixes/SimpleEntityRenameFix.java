package net.minecraft.util.datafix.fixes;

import com.mojang.datafixers.DSL;
import com.mojang.datafixers.Typed;
import com.mojang.datafixers.schemas.Schema;
import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.Dynamic;

public abstract class SimpleEntityRenameFix extends EntityRenameFix {
   public SimpleEntityRenameFix(String var1, Schema var2, boolean var3) {
      super(var1, var2, var3);
   }

   protected Pair<String, Typed<?>> fix(String var1, Typed<?> var2) {
      Pair var3 = this.getNewNameAndTag(var1, (Dynamic)var2.getOrCreate(DSL.remainderFinder()));
      return Pair.of((String)var3.getFirst(), var2.set(DSL.remainderFinder(), (Dynamic)var3.getSecond()));
   }

   protected abstract Pair<String, Dynamic<?>> getNewNameAndTag(String var1, Dynamic<?> var2);
}
