package org.apache.logging.log4j.core.layout;

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.apache.logging.log4j.core.LogEvent;
import org.apache.logging.log4j.core.config.plugins.Plugin;
import org.apache.logging.log4j.core.config.plugins.PluginBuilderAttribute;
import org.apache.logging.log4j.core.config.plugins.PluginBuilderFactory;
import org.apache.logging.log4j.core.net.Facility;
import org.apache.logging.log4j.core.net.Priority;
import org.apache.logging.log4j.core.util.NetUtils;

@Plugin(
   name = "SyslogLayout",
   category = "Core",
   elementType = "layout",
   printObject = true
)
public final class SyslogLayout extends AbstractStringLayout {
   public static final Pattern NEWLINE_PATTERN = Pattern.compile("\\r?\\n");
   private final Facility facility;
   private final boolean includeNewLine;
   private final String escapeNewLine;
   private final SimpleDateFormat dateFormat;
   private final String localHostname;

   @PluginBuilderFactory
   public static <B extends SyslogLayout.Builder<B>> B newBuilder() {
      return (SyslogLayout.Builder)(new SyslogLayout.Builder()).asBuilder();
   }

   protected SyslogLayout(Facility var1, boolean var2, String var3, Charset var4) {
      super(var4);
      this.dateFormat = new SimpleDateFormat("MMM dd HH:mm:ss", Locale.ENGLISH);
      this.localHostname = NetUtils.getLocalHostname();
      this.facility = var1;
      this.includeNewLine = var2;
      this.escapeNewLine = var3 == null ? null : Matcher.quoteReplacement(var3);
   }

   public String toSerializable(LogEvent var1) {
      StringBuilder var2 = getStringBuilder();
      var2.append('<');
      var2.append(Priority.getPriority(this.facility, var1.getLevel()));
      var2.append('>');
      this.addDate(var1.getTimeMillis(), var2);
      var2.append(' ');
      var2.append(this.localHostname);
      var2.append(' ');
      String var3 = var1.getMessage().getFormattedMessage();
      if (null != this.escapeNewLine) {
         var3 = NEWLINE_PATTERN.matcher(var3).replaceAll(this.escapeNewLine);
      }

      var2.append(var3);
      if (this.includeNewLine) {
         var2.append('\n');
      }

      return var2.toString();
   }

   private synchronized void addDate(long var1, StringBuilder var3) {
      int var4 = var3.length() + 4;
      var3.append(this.dateFormat.format(new Date(var1)));
      if (var3.charAt(var4) == '0') {
         var3.setCharAt(var4, ' ');
      }

   }

   public Map<String, String> getContentFormat() {
      HashMap var1 = new HashMap();
      var1.put("structured", "false");
      var1.put("formatType", "logfilepatternreceiver");
      var1.put("dateFormat", this.dateFormat.toPattern());
      var1.put("format", "<LEVEL>TIMESTAMP PROP(HOSTNAME) MESSAGE");
      return var1;
   }

   /** @deprecated */
   @Deprecated
   public static SyslogLayout createLayout(Facility var0, boolean var1, String var2, Charset var3) {
      return new SyslogLayout(var0, var1, var2, var3);
   }

   public Facility getFacility() {
      return this.facility;
   }

   public static class Builder<B extends SyslogLayout.Builder<B>> extends AbstractStringLayout.Builder<B> implements org.apache.logging.log4j.core.util.Builder<SyslogLayout> {
      @PluginBuilderAttribute
      private Facility facility;
      @PluginBuilderAttribute("newLine")
      private boolean includeNewLine;
      @PluginBuilderAttribute("newLineEscape")
      private String escapeNL;

      public Builder() {
         super();
         this.facility = Facility.LOCAL0;
         this.setCharset(StandardCharsets.UTF_8);
      }

      public SyslogLayout build() {
         return new SyslogLayout(this.facility, this.includeNewLine, this.escapeNL, this.getCharset());
      }

      public Facility getFacility() {
         return this.facility;
      }

      public boolean isIncludeNewLine() {
         return this.includeNewLine;
      }

      public String getEscapeNL() {
         return this.escapeNL;
      }

      public B setFacility(Facility var1) {
         this.facility = var1;
         return (SyslogLayout.Builder)this.asBuilder();
      }

      public B setIncludeNewLine(boolean var1) {
         this.includeNewLine = var1;
         return (SyslogLayout.Builder)this.asBuilder();
      }

      public B setEscapeNL(String var1) {
         this.escapeNL = var1;
         return (SyslogLayout.Builder)this.asBuilder();
      }
   }
}
