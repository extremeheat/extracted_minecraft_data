package org.apache.logging.log4j.core.util;

import com.beust.jcommander.IStringConverter;
import java.net.InetAddress;
import java.net.UnknownHostException;

public class InetAddressConverter implements IStringConverter<InetAddress> {
   public InetAddressConverter() {
      super();
   }

   public InetAddress convert(String var1) {
      try {
         return InetAddress.getByName(var1);
      } catch (UnknownHostException var3) {
         throw new IllegalArgumentException(var1, var3);
      }
   }
}
