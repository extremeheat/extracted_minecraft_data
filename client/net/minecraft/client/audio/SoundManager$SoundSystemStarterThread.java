package net.minecraft.client.audio;

import paulscode.sound.SoundSystem;
import paulscode.sound.SoundSystemConfig;
import paulscode.sound.Source;

class SoundManager$SoundSystemStarterThread extends SoundSystem {
   private SoundManager$SoundSystemStarterThread(SoundManager var1) {
      super();
      this.field_148591_a = var1;
   }

   public boolean playing(String var1) {
      synchronized(SoundSystemConfig.THREAD_SYNC) {
         if (this.soundLibrary == null) {
            return false;
         } else {
            Source var3 = (Source)this.soundLibrary.getSources().get(var1);
            if (var3 == null) {
               return false;
            } else {
               return var3.playing() || var3.paused() || var3.preLoad;
            }
         }
      }
   }
}
