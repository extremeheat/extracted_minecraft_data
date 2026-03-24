package net.minecraft.world.inventory;

public class SimpleContainerData implements ContainerData {
   private final int[] ints;

   public SimpleContainerData(final int count) {
      super();
      this.ints = new int[count];
   }

   public int get(final int dataId) {
      return this.ints[dataId];
   }

   public void set(final int dataId, final int value) {
      this.ints[dataId] = value;
   }

   public int getCount() {
      return this.ints.length;
   }
}
