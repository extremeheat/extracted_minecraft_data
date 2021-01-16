package com.mojang.datafixers.optics;

class IdAdapter<S, T> implements Adapter<S, T, S, T> {
   IdAdapter() {
      super();
   }

   public S from(S var1) {
      return var1;
   }

   public T to(T var1) {
      return var1;
   }

   public boolean equals(Object var1) {
      return var1 instanceof IdAdapter;
   }

   public String toString() {
      return "id";
   }
}
