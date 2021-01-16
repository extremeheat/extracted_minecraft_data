package net.minecraft.world.level.saveddata;

public class SaveDataDirtyRunnable implements Runnable {
   private final SavedData savedData;

   public SaveDataDirtyRunnable(SavedData var1) {
      super();
      this.savedData = var1;
   }

   public void run() {
      this.savedData.setDirty();
   }
}
