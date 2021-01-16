package org.apache.logging.log4j.core.layout;

import java.io.IOException;
import java.io.InterruptedIOException;
import java.io.LineNumberReader;
import java.io.PrintWriter;
import java.io.StringReader;
import java.io.StringWriter;
import java.lang.management.ManagementFactory;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.core.LogEvent;
import org.apache.logging.log4j.core.config.plugins.Plugin;
import org.apache.logging.log4j.core.config.plugins.PluginAttribute;
import org.apache.logging.log4j.core.config.plugins.PluginBuilderAttribute;
import org.apache.logging.log4j.core.config.plugins.PluginBuilderFactory;
import org.apache.logging.log4j.core.config.plugins.PluginFactory;
import org.apache.logging.log4j.core.util.Transform;
import org.apache.logging.log4j.util.Strings;

@Plugin(
   name = "HtmlLayout",
   category = "Core",
   elementType = "layout",
   printObject = true
)
public final class HtmlLayout extends AbstractStringLayout {
   public static final String DEFAULT_FONT_FAMILY = "arial,sans-serif";
   private static final String TRACE_PREFIX = "<br />&nbsp;&nbsp;&nbsp;&nbsp;";
   private static final String REGEXP;
   private static final String DEFAULT_TITLE = "Log4j Log Messages";
   private static final String DEFAULT_CONTENT_TYPE = "text/html";
   private final long jvmStartTime;
   private final boolean locationInfo;
   private final String title;
   private final String contentType;
   private final String font;
   private final String fontSize;
   private final String headerSize;

   private HtmlLayout(boolean var1, String var2, String var3, Charset var4, String var5, String var6, String var7) {
      super(var4);
      this.jvmStartTime = ManagementFactory.getRuntimeMXBean().getStartTime();
      this.locationInfo = var1;
      this.title = var2;
      this.contentType = this.addCharsetToContentType(var3);
      this.font = var5;
      this.fontSize = var6;
      this.headerSize = var7;
   }

   public String getTitle() {
      return this.title;
   }

   public boolean isLocationInfo() {
      return this.locationInfo;
   }

   private String addCharsetToContentType(String var1) {
      if (var1 == null) {
         return "text/html; charset=" + this.getCharset();
      } else {
         return var1.contains("charset") ? var1 : var1 + "; charset=" + this.getCharset();
      }
   }

   public String toSerializable(LogEvent var1) {
      StringBuilder var2 = getStringBuilder();
      var2.append(Strings.LINE_SEPARATOR).append("<tr>").append(Strings.LINE_SEPARATOR);
      var2.append("<td>");
      var2.append(var1.getTimeMillis() - this.jvmStartTime);
      var2.append("</td>").append(Strings.LINE_SEPARATOR);
      String var3 = Transform.escapeHtmlTags(var1.getThreadName());
      var2.append("<td title=\"").append(var3).append(" thread\">");
      var2.append(var3);
      var2.append("</td>").append(Strings.LINE_SEPARATOR);
      var2.append("<td title=\"Level\">");
      if (var1.getLevel().equals(Level.DEBUG)) {
         var2.append("<font color=\"#339933\">");
         var2.append(Transform.escapeHtmlTags(String.valueOf(var1.getLevel())));
         var2.append("</font>");
      } else if (var1.getLevel().isMoreSpecificThan(Level.WARN)) {
         var2.append("<font color=\"#993300\"><strong>");
         var2.append(Transform.escapeHtmlTags(String.valueOf(var1.getLevel())));
         var2.append("</strong></font>");
      } else {
         var2.append(Transform.escapeHtmlTags(String.valueOf(var1.getLevel())));
      }

      var2.append("</td>").append(Strings.LINE_SEPARATOR);
      String var4 = Transform.escapeHtmlTags(var1.getLoggerName());
      if (var4.isEmpty()) {
         var4 = "root";
      }

      var2.append("<td title=\"").append(var4).append(" logger\">");
      var2.append(var4);
      var2.append("</td>").append(Strings.LINE_SEPARATOR);
      if (this.locationInfo) {
         StackTraceElement var5 = var1.getSource();
         var2.append("<td>");
         var2.append(Transform.escapeHtmlTags(var5.getFileName()));
         var2.append(':');
         var2.append(var5.getLineNumber());
         var2.append("</td>").append(Strings.LINE_SEPARATOR);
      }

      var2.append("<td title=\"Message\">");
      var2.append(Transform.escapeHtmlTags(var1.getMessage().getFormattedMessage()).replaceAll(REGEXP, "<br />"));
      var2.append("</td>").append(Strings.LINE_SEPARATOR);
      var2.append("</tr>").append(Strings.LINE_SEPARATOR);
      if (var1.getContextStack() != null && !var1.getContextStack().isEmpty()) {
         var2.append("<tr><td bgcolor=\"#EEEEEE\" style=\"font-size : ").append(this.fontSize);
         var2.append(";\" colspan=\"6\" ");
         var2.append("title=\"Nested Diagnostic Context\">");
         var2.append("NDC: ").append(Transform.escapeHtmlTags(var1.getContextStack().toString()));
         var2.append("</td></tr>").append(Strings.LINE_SEPARATOR);
      }

      if (var1.getContextData() != null && !var1.getContextData().isEmpty()) {
         var2.append("<tr><td bgcolor=\"#EEEEEE\" style=\"font-size : ").append(this.fontSize);
         var2.append(";\" colspan=\"6\" ");
         var2.append("title=\"Mapped Diagnostic Context\">");
         var2.append("MDC: ").append(Transform.escapeHtmlTags(var1.getContextData().toMap().toString()));
         var2.append("</td></tr>").append(Strings.LINE_SEPARATOR);
      }

      Throwable var6 = var1.getThrown();
      if (var6 != null) {
         var2.append("<tr><td bgcolor=\"#993300\" style=\"color:White; font-size : ").append(this.fontSize);
         var2.append(";\" colspan=\"6\">");
         this.appendThrowableAsHtml(var6, var2);
         var2.append("</td></tr>").append(Strings.LINE_SEPARATOR);
      }

      return var2.toString();
   }

   public String getContentType() {
      return this.contentType;
   }

   private void appendThrowableAsHtml(Throwable var1, StringBuilder var2) {
      StringWriter var3 = new StringWriter();
      PrintWriter var4 = new PrintWriter(var3);

      try {
         var1.printStackTrace(var4);
      } catch (RuntimeException var10) {
      }

      var4.flush();
      LineNumberReader var5 = new LineNumberReader(new StringReader(var3.toString()));
      ArrayList var6 = new ArrayList();

      try {
         for(String var7 = var5.readLine(); var7 != null; var7 = var5.readLine()) {
            var6.add(var7);
         }
      } catch (IOException var11) {
         if (var11 instanceof InterruptedIOException) {
            Thread.currentThread().interrupt();
         }

         var6.add(var11.toString());
      }

      boolean var12 = true;
      Iterator var8 = var6.iterator();

      while(var8.hasNext()) {
         String var9 = (String)var8.next();
         if (!var12) {
            var2.append("<br />&nbsp;&nbsp;&nbsp;&nbsp;");
         } else {
            var12 = false;
         }

         var2.append(Transform.escapeHtmlTags(var9));
         var2.append(Strings.LINE_SEPARATOR);
      }

   }

   private StringBuilder appendLs(StringBuilder var1, String var2) {
      var1.append(var2).append(Strings.LINE_SEPARATOR);
      return var1;
   }

   private StringBuilder append(StringBuilder var1, String var2) {
      var1.append(var2);
      return var1;
   }

   public byte[] getHeader() {
      StringBuilder var1 = new StringBuilder();
      this.append(var1, "<!DOCTYPE HTML PUBLIC \"-//W3C//DTD HTML 4.01 Transitional//EN\" ");
      this.appendLs(var1, "\"http://www.w3.org/TR/html4/loose.dtd\">");
      this.appendLs(var1, "<html>");
      this.appendLs(var1, "<head>");
      this.append(var1, "<meta charset=\"");
      this.append(var1, this.getCharset().toString());
      this.appendLs(var1, "\"/>");
      this.append(var1, "<title>").append(this.title);
      this.appendLs(var1, "</title>");
      this.appendLs(var1, "<style type=\"text/css\">");
      this.appendLs(var1, "<!--");
      this.append(var1, "body, table {font-family:").append(this.font).append("; font-size: ");
      this.appendLs(var1, this.headerSize).append(";}");
      this.appendLs(var1, "th {background: #336699; color: #FFFFFF; text-align: left;}");
      this.appendLs(var1, "-->");
      this.appendLs(var1, "</style>");
      this.appendLs(var1, "</head>");
      this.appendLs(var1, "<body bgcolor=\"#FFFFFF\" topmargin=\"6\" leftmargin=\"6\">");
      this.appendLs(var1, "<hr size=\"1\" noshade=\"noshade\">");
      this.appendLs(var1, "Log session start time " + new Date() + "<br>");
      this.appendLs(var1, "<br>");
      this.appendLs(var1, "<table cellspacing=\"0\" cellpadding=\"4\" border=\"1\" bordercolor=\"#224466\" width=\"100%\">");
      this.appendLs(var1, "<tr>");
      this.appendLs(var1, "<th>Time</th>");
      this.appendLs(var1, "<th>Thread</th>");
      this.appendLs(var1, "<th>Level</th>");
      this.appendLs(var1, "<th>Logger</th>");
      if (this.locationInfo) {
         this.appendLs(var1, "<th>File:Line</th>");
      }

      this.appendLs(var1, "<th>Message</th>");
      this.appendLs(var1, "</tr>");
      return var1.toString().getBytes(this.getCharset());
   }

   public byte[] getFooter() {
      StringBuilder var1 = new StringBuilder();
      this.appendLs(var1, "</table>");
      this.appendLs(var1, "<br>");
      this.appendLs(var1, "</body></html>");
      return this.getBytes(var1.toString());
   }

   @PluginFactory
   public static HtmlLayout createLayout(@PluginAttribute("locationInfo") boolean var0, @PluginAttribute(value = "title",defaultString = "Log4j Log Messages") String var1, @PluginAttribute("contentType") String var2, @PluginAttribute(value = "charset",defaultString = "UTF-8") Charset var3, @PluginAttribute("fontSize") String var4, @PluginAttribute(value = "fontName",defaultString = "arial,sans-serif") String var5) {
      HtmlLayout.FontSize var6 = HtmlLayout.FontSize.getFontSize(var4);
      var4 = var6.getFontSize();
      String var7 = var6.larger().getFontSize();
      if (var2 == null) {
         var2 = "text/html; charset=" + var3;
      }

      return new HtmlLayout(var0, var1, var2, var3, var5, var4, var7);
   }

   public static HtmlLayout createDefaultLayout() {
      return newBuilder().build();
   }

   @PluginBuilderFactory
   public static HtmlLayout.Builder newBuilder() {
      return new HtmlLayout.Builder();
   }

   // $FF: synthetic method
   HtmlLayout(boolean var1, String var2, String var3, Charset var4, String var5, String var6, String var7, Object var8) {
      this(var1, var2, var3, var4, var5, var6, var7);
   }

   static {
      REGEXP = Strings.LINE_SEPARATOR.equals("\n") ? "\n" : Strings.LINE_SEPARATOR + "|\n";
   }

   public static class Builder implements org.apache.logging.log4j.core.util.Builder<HtmlLayout> {
      @PluginBuilderAttribute
      private boolean locationInfo;
      @PluginBuilderAttribute
      private String title;
      @PluginBuilderAttribute
      private String contentType;
      @PluginBuilderAttribute
      private Charset charset;
      @PluginBuilderAttribute
      private HtmlLayout.FontSize fontSize;
      @PluginBuilderAttribute
      private String fontName;

      private Builder() {
         super();
         this.locationInfo = false;
         this.title = "Log4j Log Messages";
         this.contentType = null;
         this.charset = StandardCharsets.UTF_8;
         this.fontSize = HtmlLayout.FontSize.SMALL;
         this.fontName = "arial,sans-serif";
      }

      public HtmlLayout.Builder withLocationInfo(boolean var1) {
         this.locationInfo = var1;
         return this;
      }

      public HtmlLayout.Builder withTitle(String var1) {
         this.title = var1;
         return this;
      }

      public HtmlLayout.Builder withContentType(String var1) {
         this.contentType = var1;
         return this;
      }

      public HtmlLayout.Builder withCharset(Charset var1) {
         this.charset = var1;
         return this;
      }

      public HtmlLayout.Builder withFontSize(HtmlLayout.FontSize var1) {
         this.fontSize = var1;
         return this;
      }

      public HtmlLayout.Builder withFontName(String var1) {
         this.fontName = var1;
         return this;
      }

      public HtmlLayout build() {
         if (this.contentType == null) {
            this.contentType = "text/html; charset=" + this.charset;
         }

         return new HtmlLayout(this.locationInfo, this.title, this.contentType, this.charset, this.fontName, this.fontSize.getFontSize(), this.fontSize.larger().getFontSize());
      }

      // $FF: synthetic method
      Builder(Object var1) {
         this();
      }
   }

   public static enum FontSize {
      SMALLER("smaller"),
      XXSMALL("xx-small"),
      XSMALL("x-small"),
      SMALL("small"),
      MEDIUM("medium"),
      LARGE("large"),
      XLARGE("x-large"),
      XXLARGE("xx-large"),
      LARGER("larger");

      private final String size;

      private FontSize(String var3) {
         this.size = var3;
      }

      public String getFontSize() {
         return this.size;
      }

      public static HtmlLayout.FontSize getFontSize(String var0) {
         HtmlLayout.FontSize[] var1 = values();
         int var2 = var1.length;

         for(int var3 = 0; var3 < var2; ++var3) {
            HtmlLayout.FontSize var4 = var1[var3];
            if (var4.size.equals(var0)) {
               return var4;
            }
         }

         return SMALL;
      }

      public HtmlLayout.FontSize larger() {
         return this.ordinal() < XXLARGE.ordinal() ? values()[this.ordinal() + 1] : this;
      }
   }
}
