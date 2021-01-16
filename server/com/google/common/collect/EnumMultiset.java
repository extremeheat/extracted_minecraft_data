package com.google.common.collect;

import com.google.common.annotations.GwtCompatible;
import com.google.common.annotations.GwtIncompatible;
import com.google.common.base.Preconditions;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.EnumMap;
import java.util.Iterator;

@GwtCompatible(
   emulated = true
)
public final class EnumMultiset<E extends Enum<E>> extends AbstractMapBasedMultiset<E> {
   private transient Class<E> type;
   @GwtIncompatible
   private static final long serialVersionUID = 0L;

   public static <E extends Enum<E>> EnumMultiset<E> create(Class<E> var0) {
      return new EnumMultiset(var0);
   }

   public static <E extends Enum<E>> EnumMultiset<E> create(Iterable<E> var0) {
      Iterator var1 = var0.iterator();
      Preconditions.checkArgument(var1.hasNext(), "EnumMultiset constructor passed empty Iterable");
      EnumMultiset var2 = new EnumMultiset(((Enum)var1.next()).getDeclaringClass());
      Iterables.addAll(var2, var0);
      return var2;
   }

   public static <E extends Enum<E>> EnumMultiset<E> create(Iterable<E> var0, Class<E> var1) {
      EnumMultiset var2 = create(var1);
      Iterables.addAll(var2, var0);
      return var2;
   }

   private EnumMultiset(Class<E> var1) {
      super(WellBehavedMap.wrap(new EnumMap(var1)));
      this.type = var1;
   }

   @GwtIncompatible
   private void writeObject(ObjectOutputStream var1) throws IOException {
      var1.defaultWriteObject();
      var1.writeObject(this.type);
      Serialization.writeMultiset(this, var1);
   }

   @GwtIncompatible
   private void readObject(ObjectInputStream var1) throws IOException, ClassNotFoundException {
      var1.defaultReadObject();
      Class var2 = (Class)var1.readObject();
      this.type = var2;
      this.setBackingMap(WellBehavedMap.wrap(new EnumMap(this.type)));
      Serialization.populateMultiset(this, var1);
   }
}
