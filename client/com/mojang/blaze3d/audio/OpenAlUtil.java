package com.mojang.blaze3d.audio;

import com.mojang.logging.LogUtils;
import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioFormat.Encoding;
import org.lwjgl.openal.AL10;
import org.lwjgl.openal.ALC10;
import org.slf4j.Logger;

public class OpenAlUtil {
   private static final Logger LOGGER = LogUtils.getLogger();

   public OpenAlUtil() {
      super();
   }

   private static String alErrorToString(final int error) {
      String var10000;
      switch (error) {
         case 40961 -> var10000 = "Invalid name parameter.";
         case 40962 -> var10000 = "Invalid enumerated parameter value.";
         case 40963 -> var10000 = "Invalid parameter parameter value.";
         case 40964 -> var10000 = "Invalid operation.";
         case 40965 -> var10000 = "Unable to allocate memory.";
         default -> var10000 = "An unrecognized error occurred.";
      }

      return var10000;
   }

   static boolean checkALError(final String location) {
      int error = AL10.alGetError();
      if (error != 0) {
         LOGGER.error("{}: {}", location, alErrorToString(error));
         return true;
      } else {
         return false;
      }
   }

   private static String alcErrorToString(final int error) {
      String var10000;
      switch (error) {
         case 40961 -> var10000 = "Invalid device.";
         case 40962 -> var10000 = "Invalid context.";
         case 40963 -> var10000 = "Illegal enum.";
         case 40964 -> var10000 = "Invalid value.";
         case 40965 -> var10000 = "Unable to allocate memory.";
         default -> var10000 = "An unrecognized error occurred.";
      }

      return var10000;
   }

   static boolean checkALCError(final long device, final String location) {
      int error = ALC10.alcGetError(device);
      if (error != 0) {
         LOGGER.error("{} ({}): {}", new Object[]{location, device, alcErrorToString(error)});
         return true;
      } else {
         return false;
      }
   }

   static int audioFormatToOpenAl(final AudioFormat audioFormat) {
      AudioFormat.Encoding encoding = audioFormat.getEncoding();
      int channels = audioFormat.getChannels();
      int sampleSizeInBits = audioFormat.getSampleSizeInBits();
      if (encoding.equals(Encoding.PCM_UNSIGNED) || encoding.equals(Encoding.PCM_SIGNED)) {
         if (channels == 1) {
            if (sampleSizeInBits == 8) {
               return 4352;
            }

            if (sampleSizeInBits == 16) {
               return 4353;
            }
         } else if (channels == 2) {
            if (sampleSizeInBits == 8) {
               return 4354;
            }

            if (sampleSizeInBits == 16) {
               return 4355;
            }
         }
      }

      throw new IllegalArgumentException("Invalid audio format: " + String.valueOf(audioFormat));
   }
}
