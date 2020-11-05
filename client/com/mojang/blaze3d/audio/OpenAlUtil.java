package com.mojang.blaze3d.audio;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioFormat.Encoding;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.lwjgl.openal.AL10;
import org.lwjgl.openal.ALC10;

public class OpenAlUtil {
   private static final Logger LOGGER = LogManager.getLogger();

   private static String alErrorToString(int var0) {
      switch(var0) {
      case 40961:
         return "Invalid name parameter.";
      case 40962:
         return "Invalid enumerated parameter value.";
      case 40963:
         return "Invalid parameter parameter value.";
      case 40964:
         return "Invalid operation.";
      case 40965:
         return "Unable to allocate memory.";
      default:
         return "An unrecognized error occurred.";
      }
   }

   static boolean checkALError(String var0) {
      int var1 = AL10.alGetError();
      if (var1 != 0) {
         LOGGER.error("{}: {}", var0, alErrorToString(var1));
         return true;
      } else {
         return false;
      }
   }

   private static String alcErrorToString(int var0) {
      switch(var0) {
      case 40961:
         return "Invalid device.";
      case 40962:
         return "Invalid context.";
      case 40963:
         return "Illegal enum.";
      case 40964:
         return "Invalid value.";
      case 40965:
         return "Unable to allocate memory.";
      default:
         return "An unrecognized error occurred.";
      }
   }

   static boolean checkALCError(long var0, String var2) {
      int var3 = ALC10.alcGetError(var0);
      if (var3 != 0) {
         LOGGER.error("{}{}: {}", var2, var0, alcErrorToString(var3));
         return true;
      } else {
         return false;
      }
   }

   static int audioFormatToOpenAl(AudioFormat var0) {
      Encoding var1 = var0.getEncoding();
      int var2 = var0.getChannels();
      int var3 = var0.getSampleSizeInBits();
      if (var1.equals(Encoding.PCM_UNSIGNED) || var1.equals(Encoding.PCM_SIGNED)) {
         if (var2 == 1) {
            if (var3 == 8) {
               return 4352;
            }

            if (var3 == 16) {
               return 4353;
            }
         } else if (var2 == 2) {
            if (var3 == 8) {
               return 4354;
            }

            if (var3 == 16) {
               return 4355;
            }
         }
      }

      throw new IllegalArgumentException("Invalid audio format: " + var0);
   }
}
