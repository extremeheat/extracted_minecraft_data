package org.apache.logging.log4j.core.config.plugins.convert;

import org.apache.logging.log4j.util.EnglishEnums;

public class EnumConverter<E extends Enum<E>> implements TypeConverter<E> {
   private final Class<E> clazz;

   public EnumConverter(Class<E> var1) {
      super();
      this.clazz = var1;
   }

   public E convert(String var1) {
      return EnglishEnums.valueOf(this.clazz, var1);
   }
}
