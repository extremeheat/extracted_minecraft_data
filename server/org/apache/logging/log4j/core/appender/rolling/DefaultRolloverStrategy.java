package org.apache.logging.log4j.core.appender.rolling;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.SortedMap;
import java.util.Map.Entry;
import java.util.concurrent.TimeUnit;
import org.apache.logging.log4j.core.appender.rolling.action.Action;
import org.apache.logging.log4j.core.appender.rolling.action.FileRenameAction;
import org.apache.logging.log4j.core.config.Configuration;
import org.apache.logging.log4j.core.config.plugins.Plugin;
import org.apache.logging.log4j.core.config.plugins.PluginAttribute;
import org.apache.logging.log4j.core.config.plugins.PluginConfiguration;
import org.apache.logging.log4j.core.config.plugins.PluginElement;
import org.apache.logging.log4j.core.config.plugins.PluginFactory;
import org.apache.logging.log4j.core.lookup.StrSubstitutor;
import org.apache.logging.log4j.core.util.Integers;

@Plugin(
   name = "DefaultRolloverStrategy",
   category = "Core",
   printObject = true
)
public class DefaultRolloverStrategy extends AbstractRolloverStrategy {
   private static final int MIN_WINDOW_SIZE = 1;
   private static final int DEFAULT_WINDOW_SIZE = 7;
   private final int maxIndex;
   private final int minIndex;
   private final boolean useMax;
   private final int compressionLevel;
   private final List<Action> customActions;
   private final boolean stopCustomActionsOnError;

   @PluginFactory
   public static DefaultRolloverStrategy createStrategy(@PluginAttribute("max") String var0, @PluginAttribute("min") String var1, @PluginAttribute("fileIndex") String var2, @PluginAttribute("compressionLevel") String var3, @PluginElement("Actions") Action[] var4, @PluginAttribute(value = "stopCustomActionsOnError",defaultBoolean = true) boolean var5, @PluginConfiguration Configuration var6) {
      int var7;
      int var8;
      boolean var9;
      if (var2 != null && var2.equalsIgnoreCase("nomax")) {
         var7 = -2147483648;
         var8 = 2147483647;
         var9 = false;
      } else {
         var9 = var2 == null ? true : var2.equalsIgnoreCase("max");
         var7 = 1;
         if (var1 != null) {
            var7 = Integer.parseInt(var1);
            if (var7 < 1) {
               LOGGER.error("Minimum window size too small. Limited to 1");
               var7 = 1;
            }
         }

         var8 = 7;
         if (var0 != null) {
            var8 = Integer.parseInt(var0);
            if (var8 < var7) {
               var8 = var7 < 7 ? 7 : var7;
               LOGGER.error("Maximum window size must be greater than the minimum windows size. Set to " + var8);
            }
         }
      }

      int var10 = Integers.parseInt(var3, -1);
      return new DefaultRolloverStrategy(var7, var8, var9, var10, var6.getStrSubstitutor(), var4, var5);
   }

   protected DefaultRolloverStrategy(int var1, int var2, boolean var3, int var4, StrSubstitutor var5, Action[] var6, boolean var7) {
      super(var5);
      this.minIndex = var1;
      this.maxIndex = var2;
      this.useMax = var3;
      this.compressionLevel = var4;
      this.stopCustomActionsOnError = var7;
      this.customActions = var6 == null ? Collections.emptyList() : Arrays.asList(var6);
   }

   public int getCompressionLevel() {
      return this.compressionLevel;
   }

   public List<Action> getCustomActions() {
      return this.customActions;
   }

   public int getMaxIndex() {
      return this.maxIndex;
   }

   public int getMinIndex() {
      return this.minIndex;
   }

   public boolean isStopCustomActionsOnError() {
      return this.stopCustomActionsOnError;
   }

   public boolean isUseMax() {
      return this.useMax;
   }

   private int purge(int var1, int var2, RollingFileManager var3) {
      return this.useMax ? this.purgeAscending(var1, var2, var3) : this.purgeDescending(var1, var2, var3);
   }

   private int purgeAscending(int var1, int var2, RollingFileManager var3) {
      SortedMap var4 = this.getEligibleFiles(var3);
      int var5 = var2 - var1 + 1;
      boolean var6 = false;

      while(var4.size() >= var5) {
         try {
            LOGGER.debug((String)"Eligible files: {}", (Object)var4);
            Integer var7 = (Integer)var4.firstKey();
            LOGGER.debug((String)"Deleting {}", (Object)((Path)var4.get(var7)).toFile().getAbsolutePath());
            Files.delete((Path)var4.get(var7));
            var4.remove(var7);
            var6 = true;
         } catch (IOException var16) {
            LOGGER.error((String)"Unable to delete {}, {}", (Object)var4.firstKey(), var16.getMessage(), var16);
            break;
         }
      }

      StringBuilder var17 = new StringBuilder();
      if (var6) {
         Iterator var8 = var4.entrySet().iterator();

         while(var8.hasNext()) {
            Entry var9 = (Entry)var8.next();
            var17.setLength(0);
            var3.getPatternProcessor().formatFileName(this.strSubstitutor, var17, (Integer)var9.getKey() - 1);
            String var10 = ((Path)var9.getValue()).toFile().getName();
            String var11 = var17.toString();
            int var12 = this.suffixLength(var11);
            if (var12 > 0 && this.suffixLength(var10) == 0) {
               var11 = var11.substring(0, var11.length() - var12);
            }

            FileRenameAction var13 = new FileRenameAction(((Path)var9.getValue()).toFile(), new File(var11), true);

            try {
               LOGGER.debug((String)"DefaultRolloverStrategy.purgeAscending executing {}", (Object)var13);
               if (!var13.execute()) {
                  return -1;
               }
            } catch (Exception var15) {
               LOGGER.warn((String)"Exception during purge in RollingFileAppender", (Throwable)var15);
               return -1;
            }
         }
      }

      return var4.size() > 0 ? ((Integer)var4.lastKey() < var2 ? (Integer)var4.lastKey() + 1 : var2) : var1;
   }

   private int purgeDescending(int var1, int var2, RollingFileManager var3) {
      SortedMap var4 = this.getEligibleFiles(var3, false);
      int var5 = var2 - var1 + 1;

      while(var4.size() >= var5) {
         try {
            Integer var6 = (Integer)var4.firstKey();
            Files.delete((Path)var4.get(var6));
            var4.remove(var6);
         } catch (IOException var15) {
            LOGGER.error((String)"Unable to delete {}, {}", (Object)var4.firstKey(), var15.getMessage(), var15);
            break;
         }
      }

      StringBuilder var16 = new StringBuilder();
      Iterator var7 = var4.entrySet().iterator();

      while(var7.hasNext()) {
         Entry var8 = (Entry)var7.next();
         var16.setLength(0);
         var3.getPatternProcessor().formatFileName(this.strSubstitutor, var16, (Integer)var8.getKey() + 1);
         String var9 = ((Path)var8.getValue()).toFile().getName();
         String var10 = var16.toString();
         int var11 = this.suffixLength(var10);
         if (var11 > 0 && this.suffixLength(var9) == 0) {
            var10 = var10.substring(0, var10.length() - var11);
         }

         FileRenameAction var12 = new FileRenameAction(((Path)var8.getValue()).toFile(), new File(var10), true);

         try {
            LOGGER.debug((String)"DefaultRolloverStrategy.purgeDescending executing {}", (Object)var12);
            if (!var12.execute()) {
               return -1;
            }
         } catch (Exception var14) {
            LOGGER.warn((String)"Exception during purge in RollingFileAppender", (Throwable)var14);
            return -1;
         }
      }

      return var1;
   }

   public RolloverDescription rollover(RollingFileManager var1) throws SecurityException {
      int var2;
      if (this.minIndex == -2147483648) {
         SortedMap var3 = this.getEligibleFiles(var1);
         var2 = var3.size() > 0 ? (Integer)var3.lastKey() + 1 : 1;
      } else {
         if (this.maxIndex < 0) {
            return null;
         }

         long var11 = System.nanoTime();
         var2 = this.purge(this.minIndex, this.maxIndex, var1);
         if (var2 < 0) {
            return null;
         }

         if (LOGGER.isTraceEnabled()) {
            double var5 = (double)TimeUnit.NANOSECONDS.toMillis(System.nanoTime() - var11);
            LOGGER.trace((String)"DefaultRolloverStrategy.purge() took {} milliseconds", (Object)var5);
         }
      }

      StringBuilder var12 = new StringBuilder(255);
      var1.getPatternProcessor().formatFileName(this.strSubstitutor, var12, var2);
      String var4 = var1.getFileName();
      String var13 = var12.toString();
      String var6 = var13;
      Action var7 = null;
      FileExtension var8 = var1.getFileExtension();
      if (var8 != null) {
         var13 = var13.substring(0, var13.length() - var8.length());
         var7 = var8.createCompressAction(var13, var6, true, this.compressionLevel);
      }

      if (var4.equals(var13)) {
         LOGGER.warn((String)"Attempt to rename file {} to itself will be ignored", (Object)var4);
         return new RolloverDescriptionImpl(var4, false, (Action)null, (Action)null);
      } else {
         FileRenameAction var9 = new FileRenameAction(new File(var4), new File(var13), var1.isRenameEmptyFiles());
         Action var10 = this.merge(var7, this.customActions, this.stopCustomActionsOnError);
         return new RolloverDescriptionImpl(var4, false, var9, var10);
      }
   }

   public String toString() {
      return "DefaultRolloverStrategy(min=" + this.minIndex + ", max=" + this.maxIndex + ", useMax=" + this.useMax + ")";
   }
}
