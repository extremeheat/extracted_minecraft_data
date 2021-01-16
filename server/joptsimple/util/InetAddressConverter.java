package joptsimple.util;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Locale;
import joptsimple.ValueConversionException;
import joptsimple.ValueConverter;
import joptsimple.internal.Messages;

public class InetAddressConverter implements ValueConverter<InetAddress> {
   public InetAddressConverter() {
      super();
   }

   public InetAddress convert(String var1) {
      try {
         return InetAddress.getByName(var1);
      } catch (UnknownHostException var3) {
         throw new ValueConversionException(this.message(var1));
      }
   }

   public Class<InetAddress> valueType() {
      return InetAddress.class;
   }

   public String valuePattern() {
      return null;
   }

   private String message(String var1) {
      return Messages.message(Locale.getDefault(), "joptsimple.ExceptionMessages", InetAddressConverter.class, "message", var1);
   }
}
