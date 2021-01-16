package org.apache.logging.log4j.core.layout;

import java.nio.charset.Charset;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.QuoteMode;
import org.apache.logging.log4j.core.config.Configuration;

public abstract class AbstractCsvLayout extends AbstractStringLayout {
   protected static final String DEFAULT_CHARSET = "UTF-8";
   protected static final String DEFAULT_FORMAT = "Default";
   private static final String CONTENT_TYPE = "text/csv";
   private final CSVFormat format;

   protected static CSVFormat createFormat(String var0, Character var1, Character var2, Character var3, QuoteMode var4, String var5, String var6) {
      CSVFormat var7 = CSVFormat.valueOf(var0);
      if (isNotNul(var1)) {
         var7 = var7.withDelimiter(var1);
      }

      if (isNotNul(var2)) {
         var7 = var7.withEscape(var2);
      }

      if (isNotNul(var3)) {
         var7 = var7.withQuote(var3);
      }

      if (var4 != null) {
         var7 = var7.withQuoteMode(var4);
      }

      if (var5 != null) {
         var7 = var7.withNullString(var5);
      }

      if (var6 != null) {
         var7 = var7.withRecordSeparator(var6);
      }

      return var7;
   }

   private static boolean isNotNul(Character var0) {
      return var0 != null && var0 != 0;
   }

   protected AbstractCsvLayout(Configuration var1, Charset var2, CSVFormat var3, String var4, String var5) {
      super(var1, var2, PatternLayout.newSerializerBuilder().setConfiguration(var1).setPattern(var4).build(), PatternLayout.newSerializerBuilder().setConfiguration(var1).setPattern(var5).build());
      this.format = var3;
   }

   public String getContentType() {
      return "text/csv; charset=" + this.getCharset();
   }

   public CSVFormat getFormat() {
      return this.format;
   }
}
