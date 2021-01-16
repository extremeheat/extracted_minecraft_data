package com.google.common.collect;

import com.google.common.annotations.GwtCompatible;
import com.google.common.annotations.GwtIncompatible;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.LinkedHashMap;

@GwtCompatible(
   serializable = true,
   emulated = true
)
public final class LinkedHashMultiset<E> extends AbstractMapBasedMultiset<E> {
   @GwtIncompatible
   private static final long serialVersionUID = 0L;

   public static <E> LinkedHashMultiset<E> create() {
      return new LinkedHashMultiset();
   }

   public static <E> LinkedHashMultiset<E> create(int var0) {
      return new LinkedHashMultiset(var0);
   }

   public static <E> LinkedHashMultiset<E> create(Iterable<? extends E> var0) {
      LinkedHashMultiset var1 = create(Multisets.inferDistinctElements(var0));
      Iterables.addAll(var1, var0);
      return var1;
   }

   private LinkedHashMultiset() {
      super(new LinkedHashMap());
   }

   private LinkedHashMultiset(int var1) {
      super(Maps.newLinkedHashMapWithExpectedSize(var1));
   }

   @GwtIncompatible
   private void writeObject(ObjectOutputStream var1) throws IOException {
      var1.defaultWriteObject();
      Serialization.writeMultiset(this, var1);
   }

   @GwtIncompatible
   private void readObject(ObjectInputStream var1) throws IOException, ClassNotFoundException {
      var1.defaultReadObject();
      int var2 = Serialization.readCount(var1);
      this.setBackingMap(new LinkedHashMap());
      Serialization.populateMultiset(this, var1, var2);
   }
}
