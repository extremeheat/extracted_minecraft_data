package io.netty.resolver;

import io.netty.util.NetUtil;
import io.netty.util.internal.ObjectUtil;
import io.netty.util.internal.PlatformDependent;
import io.netty.util.internal.logging.InternalLogger;
import io.netty.util.internal.logging.InternalLoggerFactory;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.net.Inet4Address;
import java.net.Inet6Address;
import java.net.InetAddress;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Locale;
import java.util.regex.Pattern;

public final class HostsFileParser {
   private static final String WINDOWS_DEFAULT_SYSTEM_ROOT = "C:\\Windows";
   private static final String WINDOWS_HOSTS_FILE_RELATIVE_PATH = "\\system32\\drivers\\etc\\hosts";
   private static final String X_PLATFORMS_HOSTS_FILE_PATH = "/etc/hosts";
   private static final Pattern WHITESPACES = Pattern.compile("[ \t]+");
   private static final InternalLogger logger = InternalLoggerFactory.getInstance(HostsFileParser.class);

   private static File locateHostsFile() {
      File var0;
      if (PlatformDependent.isWindows()) {
         var0 = new File(System.getenv("SystemRoot") + "\\system32\\drivers\\etc\\hosts");
         if (!var0.exists()) {
            var0 = new File("C:\\Windows\\system32\\drivers\\etc\\hosts");
         }
      } else {
         var0 = new File("/etc/hosts");
      }

      return var0;
   }

   public static HostsFileEntries parseSilently() {
      File var0 = locateHostsFile();

      try {
         return parse(var0);
      } catch (IOException var2) {
         logger.warn("Failed to load and parse hosts file at " + var0.getPath(), (Throwable)var2);
         return HostsFileEntries.EMPTY;
      }
   }

   public static HostsFileEntries parse() throws IOException {
      return parse(locateHostsFile());
   }

   public static HostsFileEntries parse(File var0) throws IOException {
      ObjectUtil.checkNotNull(var0, "file");
      return var0.exists() && var0.isFile() ? parse((Reader)(new BufferedReader(new FileReader(var0)))) : HostsFileEntries.EMPTY;
   }

   public static HostsFileEntries parse(Reader var0) throws IOException {
      ObjectUtil.checkNotNull(var0, "reader");
      BufferedReader var1 = new BufferedReader(var0);

      try {
         HashMap var2 = new HashMap();
         HashMap var3 = new HashMap();

         String var4;
         while((var4 = var1.readLine()) != null) {
            int var5 = var4.indexOf(35);
            if (var5 != -1) {
               var4 = var4.substring(0, var5);
            }

            var4 = var4.trim();
            if (!var4.isEmpty()) {
               ArrayList var6 = new ArrayList();
               String[] var7 = WHITESPACES.split(var4);
               int var8 = var7.length;

               String var10;
               for(int var9 = 0; var9 < var8; ++var9) {
                  var10 = var7[var9];
                  if (!var10.isEmpty()) {
                     var6.add(var10);
                  }
               }

               if (var6.size() >= 2) {
                  byte[] var21 = NetUtil.createByteArrayFromIpAddressString((String)var6.get(0));
                  if (var21 != null) {
                     for(var8 = 1; var8 < var6.size(); ++var8) {
                        String var22 = (String)var6.get(var8);
                        var10 = var22.toLowerCase(Locale.ENGLISH);
                        InetAddress var11 = InetAddress.getByAddress(var22, var21);
                        if (var11 instanceof Inet4Address) {
                           Inet4Address var12 = (Inet4Address)var2.put(var10, (Inet4Address)var11);
                           if (var12 != null) {
                              var2.put(var10, var12);
                           }
                        } else {
                           Inet6Address var23 = (Inet6Address)var3.put(var10, (Inet6Address)var11);
                           if (var23 != null) {
                              var3.put(var10, var23);
                           }
                        }
                     }
                  }
               }
            }
         }

         HostsFileEntries var20 = var2.isEmpty() && var3.isEmpty() ? HostsFileEntries.EMPTY : new HostsFileEntries(var2, var3);
         return var20;
      } finally {
         try {
            var1.close();
         } catch (IOException var18) {
            logger.warn("Failed to close a reader", (Throwable)var18);
         }

      }
   }

   private HostsFileParser() {
      super();
   }
}
