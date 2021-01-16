package com.google.gson;

import com.google.gson.internal.bind.util.ISO8601Utils;
import java.lang.reflect.Type;
import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.ParsePosition;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

final class DefaultDateTypeAdapter implements JsonSerializer<Date>, JsonDeserializer<Date> {
   private final DateFormat enUsFormat;
   private final DateFormat localFormat;

   DefaultDateTypeAdapter() {
      this(DateFormat.getDateTimeInstance(2, 2, Locale.US), DateFormat.getDateTimeInstance(2, 2));
   }

   DefaultDateTypeAdapter(String var1) {
      this(new SimpleDateFormat(var1, Locale.US), new SimpleDateFormat(var1));
   }

   DefaultDateTypeAdapter(int var1) {
      this(DateFormat.getDateInstance(var1, Locale.US), DateFormat.getDateInstance(var1));
   }

   public DefaultDateTypeAdapter(int var1, int var2) {
      this(DateFormat.getDateTimeInstance(var1, var2, Locale.US), DateFormat.getDateTimeInstance(var1, var2));
   }

   DefaultDateTypeAdapter(DateFormat var1, DateFormat var2) {
      super();
      this.enUsFormat = var1;
      this.localFormat = var2;
   }

   public JsonElement serialize(Date var1, Type var2, JsonSerializationContext var3) {
      synchronized(this.localFormat) {
         String var5 = this.enUsFormat.format(var1);
         return new JsonPrimitive(var5);
      }
   }

   public Date deserialize(JsonElement var1, Type var2, JsonDeserializationContext var3) throws JsonParseException {
      if (!(var1 instanceof JsonPrimitive)) {
         throw new JsonParseException("The date should be a string value");
      } else {
         Date var4 = this.deserializeToDate(var1);
         if (var2 == Date.class) {
            return var4;
         } else if (var2 == Timestamp.class) {
            return new Timestamp(var4.getTime());
         } else if (var2 == java.sql.Date.class) {
            return new java.sql.Date(var4.getTime());
         } else {
            throw new IllegalArgumentException(this.getClass() + " cannot deserialize to " + var2);
         }
      }
   }

   private Date deserializeToDate(JsonElement var1) {
      synchronized(this.localFormat) {
         Date var10000;
         try {
            var10000 = this.localFormat.parse(var1.getAsString());
         } catch (ParseException var7) {
            try {
               var10000 = this.enUsFormat.parse(var1.getAsString());
            } catch (ParseException var6) {
               try {
                  var10000 = ISO8601Utils.parse(var1.getAsString(), new ParsePosition(0));
               } catch (ParseException var5) {
                  throw new JsonSyntaxException(var1.getAsString(), var5);
               }

               return var10000;
            }

            return var10000;
         }

         return var10000;
      }
   }

   public String toString() {
      StringBuilder var1 = new StringBuilder();
      var1.append(DefaultDateTypeAdapter.class.getSimpleName());
      var1.append('(').append(this.localFormat.getClass().getSimpleName()).append(')');
      return var1.toString();
   }
}
