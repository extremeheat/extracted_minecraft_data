package org.apache.logging.log4j;

import java.io.Serializable;
import java.util.Collection;
import java.util.Locale;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import org.apache.logging.log4j.spi.StandardLevel;
import org.apache.logging.log4j.util.Strings;

public final class Level implements Comparable<Level>, Serializable {
   public static final Level OFF;
   public static final Level FATAL;
   public static final Level ERROR;
   public static final Level WARN;
   public static final Level INFO;
   public static final Level DEBUG;
   public static final Level TRACE;
   public static final Level ALL;
   public static final String CATEGORY = "Level";
   private static final ConcurrentMap<String, Level> LEVELS = new ConcurrentHashMap();
   private static final long serialVersionUID = 1581082L;
   private final String name;
   private final int intLevel;
   private final StandardLevel standardLevel;

   private Level(String var1, int var2) {
      super();
      if (Strings.isEmpty(var1)) {
         throw new IllegalArgumentException("Illegal null or empty Level name.");
      } else if (var2 < 0) {
         throw new IllegalArgumentException("Illegal Level int less than zero.");
      } else {
         this.name = var1;
         this.intLevel = var2;
         this.standardLevel = StandardLevel.getStandardLevel(var2);
         if (LEVELS.putIfAbsent(var1, this) != null) {
            throw new IllegalStateException("Level " + var1 + " has already been defined.");
         }
      }
   }

   public int intLevel() {
      return this.intLevel;
   }

   public StandardLevel getStandardLevel() {
      return this.standardLevel;
   }

   public boolean isInRange(Level var1, Level var2) {
      return this.intLevel >= var1.intLevel && this.intLevel <= var2.intLevel;
   }

   public boolean isLessSpecificThan(Level var1) {
      return this.intLevel >= var1.intLevel;
   }

   public boolean isMoreSpecificThan(Level var1) {
      return this.intLevel <= var1.intLevel;
   }

   public Level clone() throws CloneNotSupportedException {
      throw new CloneNotSupportedException();
   }

   public int compareTo(Level var1) {
      return this.intLevel < var1.intLevel ? -1 : (this.intLevel > var1.intLevel ? 1 : 0);
   }

   public boolean equals(Object var1) {
      return var1 instanceof Level && var1 == this;
   }

   public Class<Level> getDeclaringClass() {
      return Level.class;
   }

   public int hashCode() {
      return this.name.hashCode();
   }

   public String name() {
      return this.name;
   }

   public String toString() {
      return this.name;
   }

   public static Level forName(String var0, int var1) {
      Level var2 = (Level)LEVELS.get(var0);
      if (var2 != null) {
         return var2;
      } else {
         try {
            return new Level(var0, var1);
         } catch (IllegalStateException var4) {
            return (Level)LEVELS.get(var0);
         }
      }
   }

   public static Level getLevel(String var0) {
      return (Level)LEVELS.get(var0);
   }

   public static Level toLevel(String var0) {
      return toLevel(var0, DEBUG);
   }

   public static Level toLevel(String var0, Level var1) {
      if (var0 == null) {
         return var1;
      } else {
         Level var2 = (Level)LEVELS.get(var0.toUpperCase(Locale.ENGLISH));
         return var2 == null ? var1 : var2;
      }
   }

   public static Level[] values() {
      Collection var0 = LEVELS.values();
      return (Level[])var0.toArray(new Level[var0.size()]);
   }

   public static Level valueOf(String var0) {
      Objects.requireNonNull(var0, "No level name given.");
      String var1 = var0.toUpperCase(Locale.ENGLISH);
      Level var2 = (Level)LEVELS.get(var1);
      if (var2 != null) {
         return var2;
      } else {
         throw new IllegalArgumentException("Unknown level constant [" + var1 + "].");
      }
   }

   public static <T extends Enum<T>> T valueOf(Class<T> var0, String var1) {
      return Enum.valueOf(var0, var1);
   }

   protected Object readResolve() {
      return valueOf(this.name);
   }

   static {
      OFF = new Level("OFF", StandardLevel.OFF.intLevel());
      FATAL = new Level("FATAL", StandardLevel.FATAL.intLevel());
      ERROR = new Level("ERROR", StandardLevel.ERROR.intLevel());
      WARN = new Level("WARN", StandardLevel.WARN.intLevel());
      INFO = new Level("INFO", StandardLevel.INFO.intLevel());
      DEBUG = new Level("DEBUG", StandardLevel.DEBUG.intLevel());
      TRACE = new Level("TRACE", StandardLevel.TRACE.intLevel());
      ALL = new Level("ALL", StandardLevel.ALL.intLevel());
   }
}
