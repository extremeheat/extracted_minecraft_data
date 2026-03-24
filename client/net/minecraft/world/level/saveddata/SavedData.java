package net.minecraft.world.level.saveddata;

public abstract class SavedData {
   private boolean dirty;

   public SavedData() {
      super();
   }

   public void setDirty() {
      this.setDirty(true);
   }

   public void setDirty(final boolean dirty) {
      this.dirty = dirty;
   }

   public boolean isDirty() {
      return this.dirty;
   }
}
