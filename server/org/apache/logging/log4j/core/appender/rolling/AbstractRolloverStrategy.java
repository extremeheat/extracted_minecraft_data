package org.apache.logging.log4j.core.appender.rolling;

import java.io.File;
import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.SortedMap;
import java.util.TreeMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LoggingException;
import org.apache.logging.log4j.core.appender.rolling.action.Action;
import org.apache.logging.log4j.core.appender.rolling.action.CompositeAction;
import org.apache.logging.log4j.core.lookup.StrSubstitutor;
import org.apache.logging.log4j.core.pattern.NotANumber;
import org.apache.logging.log4j.status.StatusLogger;

public abstract class AbstractRolloverStrategy implements RolloverStrategy {
   protected static final Logger LOGGER = StatusLogger.getLogger();
   protected final StrSubstitutor strSubstitutor;

   protected AbstractRolloverStrategy(StrSubstitutor var1) {
      super();
      this.strSubstitutor = var1;
   }

   public StrSubstitutor getStrSubstitutor() {
      return this.strSubstitutor;
   }

   protected Action merge(Action var1, List<Action> var2, boolean var3) {
      if (var2.isEmpty()) {
         return var1;
      } else if (var1 == null) {
         return new CompositeAction(var2, var3);
      } else {
         ArrayList var4 = new ArrayList();
         var4.add(var1);
         var4.addAll(var2);
         return new CompositeAction(var4, var3);
      }
   }

   protected int suffixLength(String var1) {
      FileExtension[] var2 = FileExtension.values();
      int var3 = var2.length;

      for(int var4 = 0; var4 < var3; ++var4) {
         FileExtension var5 = var2[var4];
         if (var5.isExtensionFor(var1)) {
            return var5.length();
         }
      }

      return 0;
   }

   protected SortedMap<Integer, Path> getEligibleFiles(RollingFileManager var1) {
      return this.getEligibleFiles(var1, true);
   }

   protected SortedMap<Integer, Path> getEligibleFiles(RollingFileManager var1, boolean var2) {
      StringBuilder var3 = new StringBuilder();
      String var4 = var1.getPatternProcessor().getPattern();
      var1.getPatternProcessor().formatFileName(this.strSubstitutor, var3, NotANumber.NAN);
      return this.getEligibleFiles(var3.toString(), var4, var2);
   }

   protected SortedMap<Integer, Path> getEligibleFiles(String var1, String var2) {
      return this.getEligibleFiles(var1, var2, true);
   }

   protected SortedMap<Integer, Path> getEligibleFiles(String var1, String var2, boolean var3) {
      TreeMap var4 = new TreeMap();
      File var5 = new File(var1);
      File var6 = var5.getParentFile();
      if (var6 == null) {
         var6 = new File(".");
      } else {
         var6.mkdirs();
      }

      if (!var2.contains("%i")) {
         return var4;
      } else {
         Path var7 = var6.toPath();
         String var8 = var5.getName();
         int var9 = this.suffixLength(var8);
         if (var9 > 0) {
            var8 = var8.substring(0, var8.length() - var9) + ".*";
         }

         String var10 = var8.replace("\u0000", "(\\d+)");
         Pattern var11 = Pattern.compile(var10);

         try {
            DirectoryStream var12 = Files.newDirectoryStream(var7);
            Throwable var13 = null;

            try {
               Iterator var14 = var12.iterator();

               while(var14.hasNext()) {
                  Path var15 = (Path)var14.next();
                  Matcher var16 = var11.matcher(var15.toFile().getName());
                  if (var16.matches()) {
                     Integer var17 = Integer.parseInt(var16.group(1));
                     var4.put(var17, var15);
                  }
               }
            } catch (Throwable var26) {
               var13 = var26;
               throw var26;
            } finally {
               if (var12 != null) {
                  if (var13 != null) {
                     try {
                        var12.close();
                     } catch (Throwable var25) {
                        var13.addSuppressed(var25);
                     }
                  } else {
                     var12.close();
                  }
               }

            }
         } catch (IOException var28) {
            throw new LoggingException("Error reading folder " + var7 + " " + var28.getMessage(), var28);
         }

         return (SortedMap)(var3 ? var4 : var4.descendingMap());
      }
   }
}
