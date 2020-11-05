package net.minecraft.world.inventory;

public abstract class DataSlot {
   private int prevValue;

   public DataSlot() {
      super();
   }

   public static DataSlot forContainer(final ContainerData var0, final int var1) {
      return new DataSlot() {
         public int get() {
            return var0.get(var1);
         }

         public void set(int var1x) {
            var0.set(var1, var1x);
         }
      };
   }

   public static DataSlot shared(final int[] var0, final int var1) {
      return new DataSlot() {
         public int get() {
            return var0[var1];
         }

         public void set(int var1x) {
            var0[var1] = var1x;
         }
      };
   }

   public static DataSlot standalone() {
      return new DataSlot() {
         private int value;

         public int get() {
            return this.value;
         }

         public void set(int var1) {
            this.value = var1;
         }
      };
   }

   public abstract int get();

   public abstract void set(int var1);

   public boolean checkAndClearUpdateFlag() {
      int var1 = this.get();
      boolean var2 = var1 != this.prevValue;
      this.prevValue = var1;
      return var2;
   }
}
