package joptsimple.util;

import java.text.MessageFormat;
import java.util.EnumSet;
import java.util.Iterator;
import java.util.ResourceBundle;
import joptsimple.ValueConversionException;
import joptsimple.ValueConverter;

public abstract class EnumConverter<E extends Enum<E>> implements ValueConverter<E> {
   private final Class<E> clazz;
   private String delimiters = "[,]";

   protected EnumConverter(Class<E> var1) {
      super();
      this.clazz = var1;
   }

   public E convert(String var1) {
      Enum[] var2 = (Enum[])this.valueType().getEnumConstants();
      int var3 = var2.length;

      for(int var4 = 0; var4 < var3; ++var4) {
         Enum var5 = var2[var4];
         if (var5.name().equalsIgnoreCase(var1)) {
            return var5;
         }
      }

      throw new ValueConversionException(this.message(var1));
   }

   public Class<E> valueType() {
      return this.clazz;
   }

   public void setDelimiters(String var1) {
      this.delimiters = var1;
   }

   public String valuePattern() {
      EnumSet var1 = EnumSet.allOf(this.valueType());
      StringBuilder var2 = new StringBuilder();
      var2.append(this.delimiters.charAt(0));
      Iterator var3 = var1.iterator();

      while(var3.hasNext()) {
         var2.append(((Enum)var3.next()).toString());
         if (var3.hasNext()) {
            var2.append(this.delimiters.charAt(1));
         }
      }

      var2.append(this.delimiters.charAt(2));
      return var2.toString();
   }

   private String message(String var1) {
      ResourceBundle var2 = ResourceBundle.getBundle("joptsimple.ExceptionMessages");
      Object[] var3 = new Object[]{var1, this.valuePattern()};
      String var4 = var2.getString(EnumConverter.class.getName() + ".message");
      return (new MessageFormat(var4)).format(var3);
   }
}
