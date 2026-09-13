package net.minecraft.nbt;

import java.util.concurrent.Callable;

class NBTTagCompound$2 implements Callable {
   NBTTagCompound$2(NBTTagCompound var1, int var2) {
      super();
      this.field_82587_b = var1;
      this.field_82588_a = var2;
   }

   public String call() {
      return NBTBase.field_82578_b[this.field_82588_a];
   }
}
