package net.minecraft.util;

import com.google.common.collect.Lists;
import java.io.IOException;
import java.io.Writer;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import javax.annotation.Nullable;
import org.apache.commons.lang3.StringEscapeUtils;

public class CsvOutput {
   private static final String LINE_SEPARATOR = "\r\n";
   private static final String FIELD_SEPARATOR = ",";
   private final Writer output;
   private final int columnCount;

   CsvOutput(Writer var1, List<String> var2) throws IOException {
      super();
      this.output = var1;
      this.columnCount = var2.size();
      this.writeLine(var2.stream());
   }

   public static CsvOutput.Builder builder() {
      return new CsvOutput.Builder();
   }

   public void writeRow(Object... var1) throws IOException {
      if (var1.length != this.columnCount) {
         throw new IllegalArgumentException("Invalid number of columns, expected " + this.columnCount + ", but got " + var1.length);
      } else {
         this.writeLine(Stream.of(var1));
      }
   }

   private void writeLine(Stream<?> var1) throws IOException {
      Writer var10000 = this.output;
      Stream var10001 = var1.map(CsvOutput::getStringValue);
      var10000.write((String)var10001.collect(Collectors.joining(",")) + "\r\n");
   }

   private static String getStringValue(@Nullable Object var0) {
      return StringEscapeUtils.escapeCsv(var0 != null ? var0.toString() : "[null]");
   }

   public static class Builder {
      private final List<String> headers = Lists.newArrayList();

      public Builder() {
         super();
      }

      public CsvOutput.Builder addColumn(String var1) {
         this.headers.add(var1);
         return this;
      }

      public CsvOutput build(Writer var1) throws IOException {
         return new CsvOutput(var1, this.headers);
      }
   }
}
