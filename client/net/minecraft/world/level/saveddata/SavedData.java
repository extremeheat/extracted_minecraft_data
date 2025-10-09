package net.minecraft.world.level.saveddata;

public abstract class SavedData {
   private boolean dirty;

   public SavedData() {
      super();
   }

   public void setDirty() {
      this.setDirty(true);
   }

   public void setDirty(boolean var1) {
      this.dirty = var1;
   }

   public boolean isDirty() {
      return this.dirty;
   }
}
