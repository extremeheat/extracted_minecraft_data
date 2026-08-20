package com.mojang.blaze3d.platform;

import com.mojang.logging.LogUtils;
import java.util.HexFormat;
import org.lwjgl.sdl.SDLLog;
import org.lwjgl.sdl.SDL_LogOutputFunction;
import org.lwjgl.system.MemoryUtil;
import org.slf4j.Logger;

public class SdlDebug {
   private static final Logger LOGGER = LogUtils.getLogger();
   private static final HexFormat HEX_FORMAT = HexFormat.of().withUpperCase();
   private static final SDL_LogOutputFunction CALLBACK = SDL_LogOutputFunction.create(SdlDebug::onLogMessage);

   private SdlDebug() {
      super();
   }

   public static void init() {
      SDLLog.SDL_SetLogOutputFunction(CALLBACK, 0L);
      SDLLog.SDL_SetLogPriorities(LOGGER.isDebugEnabled() ? 3 : 4);
   }

   private static String printUnknownToken(final int token) {
      return "Unknown (0x" + HEX_FORMAT.toHexDigits(token) + ")";
   }

   private static String categoryToString(final int category) {
      String var10000;
      switch (category) {
         case 0 -> var10000 = "APPLICATION";
         case 1 -> var10000 = "ERROR";
         case 2 -> var10000 = "ASSERT";
         case 3 -> var10000 = "SYSTEM";
         case 4 -> var10000 = "AUDIO";
         case 5 -> var10000 = "VIDEO";
         case 6 -> var10000 = "RENDER";
         case 7 -> var10000 = "INPUT";
         case 8 -> var10000 = "TEST";
         case 9 -> var10000 = "GPU";
         default -> var10000 = printUnknownToken(category);
      }

      return var10000;
   }

   private static void onLogMessage(final long userData, final int category, final int priority, final long message) {
      String text = message == 0L ? "" : MemoryUtil.memUTF8(message);
      String categoryName = categoryToString(category);
      switch (priority) {
         case 2:
         case 3:
            LOGGER.debug("SDL [{}]: {}", categoryName, text);
            break;
         case 4:
            LOGGER.info("SDL [{}]: {}", categoryName, text);
            break;
         case 5:
            LOGGER.warn("SDL [{}]: {}", categoryName, text);
            break;
         default:
            LOGGER.error("SDL [{}]: {}", categoryName, text);
      }

   }
}
