package net.minecraft.nbt;

import java.util.AbstractList;

public abstract class NBTTagCollection<T extends INBTBase> extends AbstractList<T> implements INBTBase {
   public NBTTagCollection() {
      super();
   }

   public abstract int size();

   public T get(int var1) {
      return this.func_197647_c(var1);
   }

   public T set(int var1, T var2) {
      INBTBase var3 = this.get(var1);
      this.func_197648_a(var1, var2);
      return var3;
   }

   public abstract T func_197647_c(int var1);

   public abstract void func_197648_a(int var1, INBTBase var2);

   public abstract void func_197649_b(int var1);

   // $FF: synthetic method
   public Object set(int var1, Object var2) {
      return this.set(var1, (INBTBase)var2);
   }

   // $FF: synthetic method
   public Object get(int var1) {
      return this.get(var1);
   }
}
