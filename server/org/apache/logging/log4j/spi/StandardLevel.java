package org.apache.logging.log4j.spi;

import java.util.EnumSet;
import java.util.Iterator;

public enum StandardLevel {
   OFF(0),
   FATAL(100),
   ERROR(200),
   WARN(300),
   INFO(400),
   DEBUG(500),
   TRACE(600),
   ALL(2147483647);

   private static final EnumSet<StandardLevel> LEVELSET = EnumSet.allOf(StandardLevel.class);
   private final int intLevel;

   private StandardLevel(int var3) {
      this.intLevel = var3;
   }

   public int intLevel() {
      return this.intLevel;
   }

   public static StandardLevel getStandardLevel(int var0) {
      StandardLevel var1 = OFF;

      StandardLevel var3;
      for(Iterator var2 = LEVELSET.iterator(); var2.hasNext(); var1 = var3) {
         var3 = (StandardLevel)var2.next();
         if (var3.intLevel() > var0) {
            break;
         }
      }

      return var1;
   }
}
