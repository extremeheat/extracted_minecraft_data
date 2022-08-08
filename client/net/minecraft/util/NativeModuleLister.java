package net.minecraft.util;

import com.google.common.collect.ImmutableList;
import com.mojang.logging.LogUtils;
import com.sun.jna.Memory;
import com.sun.jna.Native;
import com.sun.jna.Platform;
import com.sun.jna.Pointer;
import com.sun.jna.platform.win32.Kernel32;
import com.sun.jna.platform.win32.Kernel32Util;
import com.sun.jna.platform.win32.Tlhelp32;
import com.sun.jna.platform.win32.Version;
import com.sun.jna.platform.win32.Win32Exception;
import com.sun.jna.ptr.IntByReference;
import com.sun.jna.ptr.PointerByReference;
import java.nio.charset.StandardCharsets;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;
import java.util.OptionalInt;
import java.util.stream.Collectors;
import net.minecraft.CrashReportCategory;
import org.slf4j.Logger;

public class NativeModuleLister {
   private static final Logger LOGGER = LogUtils.getLogger();
   private static final int LANG_MASK = 65535;
   private static final int DEFAULT_LANG = 1033;
   private static final int CODEPAGE_MASK = -65536;
   private static final int DEFAULT_CODEPAGE = 78643200;

   public NativeModuleLister() {
      super();
   }

   public static List<NativeModuleInfo> listModules() {
      if (!Platform.isWindows()) {
         return ImmutableList.of();
      } else {
         int var0 = Kernel32.INSTANCE.GetCurrentProcessId();
         ImmutableList.Builder var1 = ImmutableList.builder();
         List var2 = Kernel32Util.getModules(var0);
         Iterator var3 = var2.iterator();

         while(var3.hasNext()) {
            Tlhelp32.MODULEENTRY32W var4 = (Tlhelp32.MODULEENTRY32W)var3.next();
            String var5 = var4.szModule();
            Optional var6 = tryGetVersion(var4.szExePath());
            var1.add(new NativeModuleInfo(var5, var6));
         }

         return var1.build();
      }
   }

   private static Optional<NativeModuleVersion> tryGetVersion(String var0) {
      try {
         IntByReference var1 = new IntByReference();
         int var2 = Version.INSTANCE.GetFileVersionInfoSize(var0, var1);
         if (var2 == 0) {
            int var15 = Native.getLastError();
            if (var15 != 1813 && var15 != 1812) {
               throw new Win32Exception(var15);
            } else {
               return Optional.empty();
            }
         } else {
            Memory var3 = new Memory((long)var2);
            if (!Version.INSTANCE.GetFileVersionInfo(var0, 0, var2, var3)) {
               throw new Win32Exception(Native.getLastError());
            } else {
               IntByReference var4 = new IntByReference();
               Pointer var5 = queryVersionValue(var3, "\\VarFileInfo\\Translation", var4);
               int[] var6 = var5.getIntArray(0L, var4.getValue() / 4);
               OptionalInt var7 = findLangAndCodepage(var6);
               if (!var7.isPresent()) {
                  return Optional.empty();
               } else {
                  int var8 = var7.getAsInt();
                  int var9 = var8 & '\uffff';
                  int var10 = (var8 & -65536) >> 16;
                  String var11 = queryVersionString(var3, langTableKey("FileDescription", var9, var10), var4);
                  String var12 = queryVersionString(var3, langTableKey("CompanyName", var9, var10), var4);
                  String var13 = queryVersionString(var3, langTableKey("FileVersion", var9, var10), var4);
                  return Optional.of(new NativeModuleVersion(var11, var13, var12));
               }
            }
         }
      } catch (Exception var14) {
         LOGGER.info("Failed to find module info for {}", var0, var14);
         return Optional.empty();
      }
   }

   private static String langTableKey(String var0, int var1, int var2) {
      return String.format("\\StringFileInfo\\%04x%04x\\%s", var1, var2, var0);
   }

   private static OptionalInt findLangAndCodepage(int[] var0) {
      OptionalInt var1 = OptionalInt.empty();
      int[] var2 = var0;
      int var3 = var0.length;

      for(int var4 = 0; var4 < var3; ++var4) {
         int var5 = var2[var4];
         if ((var5 & -65536) == 78643200 && (var5 & '\uffff') == 1033) {
            return OptionalInt.of(var5);
         }

         var1 = OptionalInt.of(var5);
      }

      return var1;
   }

   private static Pointer queryVersionValue(Pointer var0, String var1, IntByReference var2) {
      PointerByReference var3 = new PointerByReference();
      if (!Version.INSTANCE.VerQueryValue(var0, var1, var3, var2)) {
         throw new UnsupportedOperationException("Can't get version value " + var1);
      } else {
         return var3.getValue();
      }
   }

   private static String queryVersionString(Pointer var0, String var1, IntByReference var2) {
      try {
         Pointer var3 = queryVersionValue(var0, var1, var2);
         byte[] var4 = var3.getByteArray(0L, (var2.getValue() - 1) * 2);
         return new String(var4, StandardCharsets.UTF_16LE);
      } catch (Exception var5) {
         return "";
      }
   }

   public static void addCrashSection(CrashReportCategory var0) {
      var0.setDetail("Modules", () -> {
         return (String)listModules().stream().sorted(Comparator.comparing((var0) -> {
            return var0.name;
         })).map((var0) -> {
            return "\n\t\t" + var0;
         }).collect(Collectors.joining());
      });
   }

   public static class NativeModuleInfo {
      public final String name;
      public final Optional<NativeModuleVersion> version;

      public NativeModuleInfo(String var1, Optional<NativeModuleVersion> var2) {
         super();
         this.name = var1;
         this.version = var2;
      }

      public String toString() {
         return (String)this.version.map((var1) -> {
            return this.name + ":" + var1;
         }).orElse(this.name);
      }
   }

   public static class NativeModuleVersion {
      public final String description;
      public final String version;
      public final String company;

      public NativeModuleVersion(String var1, String var2, String var3) {
         super();
         this.description = var1;
         this.version = var2;
         this.company = var3;
      }

      public String toString() {
         return this.description + ":" + this.version + ":" + this.company;
      }
   }
}
