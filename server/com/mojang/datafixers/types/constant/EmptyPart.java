package com.mojang.datafixers.types.constant;

import com.mojang.datafixers.DSL;
import com.mojang.datafixers.types.Type;
import com.mojang.datafixers.types.templates.TypeTemplate;
import com.mojang.datafixers.util.Unit;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DynamicOps;
import java.util.Optional;

public final class EmptyPart extends Type<Unit> {
   public EmptyPart() {
      super();
   }

   public String toString() {
      return "EmptyPart";
   }

   public Optional<Unit> point(DynamicOps<?> var1) {
      return Optional.of(Unit.INSTANCE);
   }

   public boolean equals(Object var1, boolean var2, boolean var3) {
      return this == var1;
   }

   public TypeTemplate buildTemplate() {
      return DSL.constType(this);
   }

   protected Codec<Unit> buildCodec() {
      return Codec.EMPTY.codec();
   }
}
