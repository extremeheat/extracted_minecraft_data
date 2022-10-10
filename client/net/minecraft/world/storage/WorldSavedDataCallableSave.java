package net.minecraft.world.storage;

public class WorldSavedDataCallableSave implements Runnable {
   private final WorldSavedData field_186338_a;

   public WorldSavedDataCallableSave(WorldSavedData var1) {
      super();
      this.field_186338_a = var1;
   }

   public void run() {
      this.field_186338_a.func_76185_a();
   }
}
