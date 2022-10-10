package net.minecraft.realms;

import net.minecraft.util.SharedConstants;

public class RealmsSharedConstants {
   public static final int NETWORK_PROTOCOL_VERSION = 404;
   public static final int TICKS_PER_SECOND = 20;
   public static final String VERSION_STRING = "1.13.2";
   public static final char[] ILLEGAL_FILE_CHARACTERS;

   public RealmsSharedConstants() {
      super();
   }

   static {
      ILLEGAL_FILE_CHARACTERS = SharedConstants.field_71567_b;
   }
}
