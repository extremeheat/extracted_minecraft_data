package net.minecraft.world.level.saveddata;

import java.util.Objects;
import javax.annotation.Nullable;
import net.minecraft.server.level.ServerLevel;

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

   public static record Context(@Nullable ServerLevel level, long worldSeed) {
      public Context(ServerLevel var1) {
         this(var1, var1.getSeed());
      }

      public Context(@Nullable ServerLevel var1, long var2) {
         super();
         this.level = var1;
         this.worldSeed = var2;
      }

      public ServerLevel levelOrThrow() {
         return (ServerLevel)Objects.requireNonNull(this.level);
      }
   }
}
