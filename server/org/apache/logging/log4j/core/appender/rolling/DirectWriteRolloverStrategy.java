package org.apache.logging.log4j.core.appender.rolling;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.SortedMap;
import java.util.concurrent.TimeUnit;
import org.apache.logging.log4j.core.appender.rolling.action.Action;
import org.apache.logging.log4j.core.config.Configuration;
import org.apache.logging.log4j.core.config.plugins.Plugin;
import org.apache.logging.log4j.core.config.plugins.PluginAttribute;
import org.apache.logging.log4j.core.config.plugins.PluginConfiguration;
import org.apache.logging.log4j.core.config.plugins.PluginElement;
import org.apache.logging.log4j.core.config.plugins.PluginFactory;
import org.apache.logging.log4j.core.lookup.StrSubstitutor;
import org.apache.logging.log4j.core.util.Integers;

@Plugin(
   name = "DirectWriteRolloverStrategy",
   category = "Core",
   printObject = true
)
public class DirectWriteRolloverStrategy extends AbstractRolloverStrategy implements DirectFileRolloverStrategy {
   private static final int DEFAULT_MAX_FILES = 7;
   private final int maxFiles;
   private final int compressionLevel;
   private final List<Action> customActions;
   private final boolean stopCustomActionsOnError;
   private volatile String currentFileName;
   private int nextIndex = -1;

   @PluginFactory
   public static DirectWriteRolloverStrategy createStrategy(@PluginAttribute("maxFiles") String var0, @PluginAttribute("compressionLevel") String var1, @PluginElement("Actions") Action[] var2, @PluginAttribute(value = "stopCustomActionsOnError",defaultBoolean = true) boolean var3, @PluginConfiguration Configuration var4) {
      int var5 = 2147483647;
      if (var0 != null) {
         var5 = Integer.parseInt(var0);
         if (var5 < 0) {
            var5 = 2147483647;
         } else if (var5 < 2) {
            LOGGER.error("Maximum files too small. Limited to 7");
            var5 = 7;
         }
      }

      int var6 = Integers.parseInt(var1, -1);
      return new DirectWriteRolloverStrategy(var5, var6, var4.getStrSubstitutor(), var2, var3);
   }

   protected DirectWriteRolloverStrategy(int var1, int var2, StrSubstitutor var3, Action[] var4, boolean var5) {
      super(var3);
      this.maxFiles = var1;
      this.compressionLevel = var2;
      this.stopCustomActionsOnError = var5;
      this.customActions = var4 == null ? Collections.emptyList() : Arrays.asList(var4);
   }

   public int getCompressionLevel() {
      return this.compressionLevel;
   }

   public List<Action> getCustomActions() {
      return this.customActions;
   }

   public int getMaxFiles() {
      return this.maxFiles;
   }

   public boolean isStopCustomActionsOnError() {
      return this.stopCustomActionsOnError;
   }

   private int purge(RollingFileManager var1) {
      SortedMap var2 = this.getEligibleFiles(var1);
      LOGGER.debug((String)"Found {} eligible files, max is  {}", (Object)var2.size(), (Object)this.maxFiles);

      while(var2.size() >= this.maxFiles) {
         try {
            Integer var3 = (Integer)var2.firstKey();
            Files.delete((Path)var2.get(var3));
            var2.remove(var3);
         } catch (IOException var4) {
            LOGGER.error((String)"Unable to delete {}", (Object)var2.firstKey(), (Object)var4);
            break;
         }
      }

      return var2.size() > 0 ? (Integer)var2.lastKey() : 1;
   }

   public String getCurrentFileName(RollingFileManager var1) {
      if (this.currentFileName == null) {
         SortedMap var2 = this.getEligibleFiles(var1);
         int var3 = var2.size() > 0 ? (this.nextIndex > 0 ? this.nextIndex : var2.size()) : 1;
         StringBuilder var4 = new StringBuilder(255);
         var1.getPatternProcessor().formatFileName(this.strSubstitutor, var4, true, var3);
         int var5 = this.suffixLength(var4.toString());
         String var6 = var5 > 0 ? var4.substring(0, var4.length() - var5) : var4.toString();
         this.currentFileName = var6;
      }

      return this.currentFileName;
   }

   public RolloverDescription rollover(RollingFileManager var1) throws SecurityException {
      LOGGER.debug("Rolling " + this.currentFileName);
      if (this.maxFiles < 0) {
         return null;
      } else {
         long var2 = System.nanoTime();
         int var4 = this.purge(var1);
         if (LOGGER.isTraceEnabled()) {
            double var5 = (double)TimeUnit.NANOSECONDS.toMillis(System.nanoTime() - var2);
            LOGGER.trace((String)"DirectWriteRolloverStrategy.purge() took {} milliseconds", (Object)var5);
         }

         Action var9 = null;
         String var6 = this.currentFileName;
         this.currentFileName = null;
         this.nextIndex = var4 + 1;
         FileExtension var7 = var1.getFileExtension();
         if (var7 != null) {
            var9 = var7.createCompressAction(var6, var6 + var7.getExtension(), true, this.compressionLevel);
         }

         Action var8 = this.merge(var9, this.customActions, this.stopCustomActionsOnError);
         return new RolloverDescriptionImpl(var6, false, (Action)null, var8);
      }
   }

   public String toString() {
      return "DirectWriteRolloverStrategy(maxFiles=" + this.maxFiles + ')';
   }
}
