package com.mojang.datafixers.types.constant;

import com.mojang.datafixers.DSL;
import com.mojang.datafixers.types.Type;
import com.mojang.datafixers.types.templates.TypeTemplate;
import com.mojang.serialization.Codec;
import com.mojang.serialization.Dynamic;
import com.mojang.serialization.DynamicOps;
import java.util.Optional;

public final class EmptyPartPassthrough extends Type<Dynamic<?>> {
   public EmptyPartPassthrough() {
      super();
   }

   public String toString() {
      return "EmptyPartPassthrough";
   }

   public Optional<Dynamic<?>> point(DynamicOps<?> var1) {
      return Optional.of(new Dynamic(var1));
   }

   public boolean equals(Object var1, boolean var2, boolean var3) {
      return this == var1;
   }

   public TypeTemplate buildTemplate() {
      return DSL.constType(this);
   }

   public Codec<Dynamic<?>> buildCodec() {
      return Codec.PASSTHROUGH;
   }
}
