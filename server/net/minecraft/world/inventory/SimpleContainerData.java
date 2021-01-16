package net.minecraft.world.inventory;

public class SimpleContainerData implements ContainerData {
   private final int[] ints;

   public SimpleContainerData(int var1) {
      super();
      this.ints = new int[var1];
   }

   public int get(int var1) {
      return this.ints[var1];
   }

   public void set(int var1, int var2) {
      this.ints[var1] = var2;
   }

   public int getCount() {
      return this.ints.length;
   }
}
