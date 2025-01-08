package net.minecraft.util.datafix.fixes;

import com.mojang.datafixers.DSL;
import com.mojang.datafixers.Typed;
import com.mojang.datafixers.schemas.Schema;
import com.mojang.serialization.Dynamic;
import java.util.function.Predicate;

public abstract class ItemStackTagRemainderFix extends ItemStackTagFix {
   public ItemStackTagRemainderFix(Schema var1, String var2, Predicate<String> var3) {
      super(var1, var2, var3);
   }

   protected abstract <T> Dynamic<T> fixItemStackTag(Dynamic<T> var1);

   protected final Typed<?> fixItemStackTag(Typed<?> var1) {
      return var1.update(DSL.remainderFinder(), this::fixItemStackTag);
   }
}
