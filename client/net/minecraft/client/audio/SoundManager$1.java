package net.minecraft.client.audio;

class SoundManager$1 implements Runnable {
   SoundManager$1(SoundManager var1) {
      super();
      this.field_148631_a = var1;
   }

   @Override
   public void run() {
      SoundManager.access$002(this.field_148631_a, new SoundManager$SoundSystemStarterThread(this.field_148631_a, null));
      SoundManager.access$202(this.field_148631_a, true);
      SoundManager.access$000(this.field_148631_a).setMasterVolume(SoundManager.access$300(this.field_148631_a).func_151438_a(SoundCategory.MASTER));
      SoundManager.access$500().info(SoundManager.access$400(), "Sound engine started");
   }
}
