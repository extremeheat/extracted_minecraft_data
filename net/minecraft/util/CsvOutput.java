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
   private final Writer output;
   private final int columnCount;

   private CsvOutput(Writer var1, List var2) throws IOException {
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

   private void writeLine(Stream var1) throws IOException {
      this.output.write((String)var1.map(CsvOutput::getStringValue).collect(Collectors.joining(",")) + "\r\n");
   }

   private static String getStringValue(@Nullable Object var0) {
      return StringEscapeUtils.escapeCsv(var0 != null ? var0.toString() : "[null]");
   }

   // $FF: synthetic method
   CsvOutput(Writer var1, List var2, Object var3) throws IOException {
      this(var1, var2);
   }

   public static class Builder {
      private final List headers = Lists.newArrayList();

      public CsvOutput.Builder addColumn(String var1) {
         this.headers.add(var1);
         return this;
      }

      public CsvOutput build(Writer var1) throws IOException {
         return new CsvOutput(var1, this.headers);
      }
   }
}
