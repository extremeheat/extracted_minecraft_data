package com.google.gson.internal.bind;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.google.gson.TypeAdapter;
import com.google.gson.TypeAdapterFactory;
import com.google.gson.internal.bind.util.ISO8601Utils;
import com.google.gson.reflect.TypeToken;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonToken;
import com.google.gson.stream.JsonWriter;
import java.io.IOException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.ParsePosition;
import java.util.Date;
import java.util.Locale;

public final class DateTypeAdapter extends TypeAdapter<Date> {
   public static final TypeAdapterFactory FACTORY = new TypeAdapterFactory() {
      public <T> TypeAdapter<T> create(Gson var1, TypeToken<T> var2) {
         return var2.getRawType() == Date.class ? new DateTypeAdapter() : null;
      }
   };
   private final DateFormat enUsFormat;
   private final DateFormat localFormat;

   public DateTypeAdapter() {
      super();
      this.enUsFormat = DateFormat.getDateTimeInstance(2, 2, Locale.US);
      this.localFormat = DateFormat.getDateTimeInstance(2, 2);
   }

   public Date read(JsonReader var1) throws IOException {
      if (var1.peek() == JsonToken.NULL) {
         var1.nextNull();
         return null;
      } else {
         return this.deserializeToDate(var1.nextString());
      }
   }

   private synchronized Date deserializeToDate(String var1) {
      try {
         return this.localFormat.parse(var1);
      } catch (ParseException var5) {
         try {
            return this.enUsFormat.parse(var1);
         } catch (ParseException var4) {
            try {
               return ISO8601Utils.parse(var1, new ParsePosition(0));
            } catch (ParseException var3) {
               throw new JsonSyntaxException(var1, var3);
            }
         }
      }
   }

   public synchronized void write(JsonWriter var1, Date var2) throws IOException {
      if (var2 == null) {
         var1.nullValue();
      } else {
         String var3 = this.enUsFormat.format(var2);
         var1.value(var3);
      }
   }
}
