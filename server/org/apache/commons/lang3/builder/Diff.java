package org.apache.commons.lang3.builder;

import java.lang.reflect.Type;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.reflect.TypeUtils;
import org.apache.commons.lang3.tuple.Pair;

public abstract class Diff<T> extends Pair<T, T> {
   private static final long serialVersionUID = 1L;
   private final Type type = (Type)ObjectUtils.defaultIfNull(TypeUtils.getTypeArguments(this.getClass(), Diff.class).get(Diff.class.getTypeParameters()[0]), Object.class);
   private final String fieldName;

   protected Diff(String var1) {
      super();
      this.fieldName = var1;
   }

   public final Type getType() {
      return this.type;
   }

   public final String getFieldName() {
      return this.fieldName;
   }

   public final String toString() {
      return String.format("[%s: %s, %s]", this.fieldName, this.getLeft(), this.getRight());
   }

   public final T setValue(T var1) {
      throw new UnsupportedOperationException("Cannot alter Diff object.");
   }
}
