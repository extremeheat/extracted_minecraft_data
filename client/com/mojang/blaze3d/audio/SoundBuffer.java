package com.mojang.blaze3d.audio;

import java.nio.ByteBuffer;
import java.util.OptionalInt;
import javax.annotation.Nullable;
import javax.sound.sampled.AudioFormat;
import org.lwjgl.openal.AL10;

public class SoundBuffer {
   @Nullable
   private ByteBuffer data;
   private final AudioFormat format;
   private boolean hasAlBuffer;
   private int alBuffer;

   public SoundBuffer(ByteBuffer var1, AudioFormat var2) {
      super();
      this.data = var1;
      this.format = var2;
   }

   OptionalInt getAlBuffer() {
      if (!this.hasAlBuffer) {
         if (this.data == null) {
            return OptionalInt.empty();
         }

         int var1 = OpenAlUtil.audioFormatToOpenAl(this.format);
         int[] var2 = new int[1];
         AL10.alGenBuffers(var2);
         if (OpenAlUtil.checkALError("Creating buffer")) {
            return OptionalInt.empty();
         }

         AL10.alBufferData(var2[0], var1, this.data, (int)this.format.getSampleRate());
         if (OpenAlUtil.checkALError("Assigning buffer data")) {
            return OptionalInt.empty();
         }

         this.alBuffer = var2[0];
         this.hasAlBuffer = true;
         this.data = null;
      }

      return OptionalInt.of(this.alBuffer);
   }

   public void discardAlBuffer() {
      if (this.hasAlBuffer) {
         AL10.alDeleteBuffers(new int[]{this.alBuffer});
         if (OpenAlUtil.checkALError("Deleting stream buffers")) {
            return;
         }
      }

      this.hasAlBuffer = false;
   }

   public OptionalInt releaseAlBuffer() {
      OptionalInt var1 = this.getAlBuffer();
      this.hasAlBuffer = false;
      return var1;
   }
}
