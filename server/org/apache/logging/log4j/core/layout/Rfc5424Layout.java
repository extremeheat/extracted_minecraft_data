package org.apache.logging.log4j.core.layout;

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.Map.Entry;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LoggingException;
import org.apache.logging.log4j.core.LogEvent;
import org.apache.logging.log4j.core.appender.TlsSyslogFrame;
import org.apache.logging.log4j.core.config.Configuration;
import org.apache.logging.log4j.core.config.plugins.Plugin;
import org.apache.logging.log4j.core.config.plugins.PluginAttribute;
import org.apache.logging.log4j.core.config.plugins.PluginConfiguration;
import org.apache.logging.log4j.core.config.plugins.PluginElement;
import org.apache.logging.log4j.core.config.plugins.PluginFactory;
import org.apache.logging.log4j.core.net.Facility;
import org.apache.logging.log4j.core.net.Priority;
import org.apache.logging.log4j.core.pattern.LogEventPatternConverter;
import org.apache.logging.log4j.core.pattern.PatternConverter;
import org.apache.logging.log4j.core.pattern.PatternFormatter;
import org.apache.logging.log4j.core.pattern.PatternParser;
import org.apache.logging.log4j.core.pattern.ThrowablePatternConverter;
import org.apache.logging.log4j.core.util.NetUtils;
import org.apache.logging.log4j.core.util.Patterns;
import org.apache.logging.log4j.message.Message;
import org.apache.logging.log4j.message.StructuredDataId;
import org.apache.logging.log4j.message.StructuredDataMessage;
import org.apache.logging.log4j.util.StringBuilders;
import org.apache.logging.log4j.util.Strings;

@Plugin(
   name = "Rfc5424Layout",
   category = "Core",
   elementType = "layout",
   printObject = true
)
public final class Rfc5424Layout extends AbstractStringLayout {
   public static final int DEFAULT_ENTERPRISE_NUMBER = 18060;
   public static final String DEFAULT_ID = "Audit";
   public static final Pattern NEWLINE_PATTERN = Pattern.compile("\\r?\\n");
   public static final Pattern PARAM_VALUE_ESCAPE_PATTERN = Pattern.compile("[\\\"\\]\\\\]");
   public static final String DEFAULT_MDCID = "mdc";
   private static final String LF = "\n";
   private static final int TWO_DIGITS = 10;
   private static final int THREE_DIGITS = 100;
   private static final int MILLIS_PER_MINUTE = 60000;
   private static final int MINUTES_PER_HOUR = 60;
   private static final String COMPONENT_KEY = "RFC5424-Converter";
   private final Facility facility;
   private final String defaultId;
   private final int enterpriseNumber;
   private final boolean includeMdc;
   private final String mdcId;
   private final StructuredDataId mdcSdId;
   private final String localHostName;
   private final String appName;
   private final String messageId;
   private final String configName;
   private final String mdcPrefix;
   private final String eventPrefix;
   private final List<String> mdcExcludes;
   private final List<String> mdcIncludes;
   private final List<String> mdcRequired;
   private final Rfc5424Layout.ListChecker listChecker;
   private final Rfc5424Layout.ListChecker noopChecker = new Rfc5424Layout.NoopChecker();
   private final boolean includeNewLine;
   private final String escapeNewLine;
   private final boolean useTlsMessageFormat;
   private long lastTimestamp = -1L;
   private String timestamppStr;
   private final List<PatternFormatter> exceptionFormatters;
   private final Map<String, Rfc5424Layout.FieldFormatter> fieldFormatters;
   private final String procId;

   private Rfc5424Layout(Configuration var1, Facility var2, String var3, int var4, boolean var5, boolean var6, String var7, String var8, String var9, String var10, String var11, String var12, String var13, String var14, String var15, Charset var16, String var17, boolean var18, LoggerFields[] var19) {
      super(var16);
      PatternParser var20 = createPatternParser(var1, ThrowablePatternConverter.class);
      this.exceptionFormatters = var17 == null ? null : var20.parse(var17);
      this.facility = var2;
      this.defaultId = var3 == null ? "Audit" : var3;
      this.enterpriseNumber = var4;
      this.includeMdc = var5;
      this.includeNewLine = var6;
      this.escapeNewLine = var7 == null ? null : Matcher.quoteReplacement(var7);
      this.mdcId = var8;
      this.mdcSdId = new StructuredDataId(var8, this.enterpriseNumber, (String[])null, (String[])null);
      this.mdcPrefix = var9;
      this.eventPrefix = var10;
      this.appName = var11;
      this.messageId = var12;
      this.useTlsMessageFormat = var18;
      this.localHostName = NetUtils.getLocalHostname();
      Object var21 = null;
      String[] var22;
      String[] var23;
      int var24;
      int var25;
      String var26;
      if (var13 != null) {
         var22 = var13.split(Patterns.COMMA_SEPARATOR);
         if (var22.length > 0) {
            var21 = new Rfc5424Layout.ExcludeChecker();
            this.mdcExcludes = new ArrayList(var22.length);
            var23 = var22;
            var24 = var22.length;

            for(var25 = 0; var25 < var24; ++var25) {
               var26 = var23[var25];
               this.mdcExcludes.add(var26.trim());
            }
         } else {
            this.mdcExcludes = null;
         }
      } else {
         this.mdcExcludes = null;
      }

      if (var14 != null) {
         var22 = var14.split(Patterns.COMMA_SEPARATOR);
         if (var22.length > 0) {
            var21 = new Rfc5424Layout.IncludeChecker();
            this.mdcIncludes = new ArrayList(var22.length);
            var23 = var22;
            var24 = var22.length;

            for(var25 = 0; var25 < var24; ++var25) {
               var26 = var23[var25];
               this.mdcIncludes.add(var26.trim());
            }
         } else {
            this.mdcIncludes = null;
         }
      } else {
         this.mdcIncludes = null;
      }

      if (var15 != null) {
         var22 = var15.split(Patterns.COMMA_SEPARATOR);
         if (var22.length > 0) {
            this.mdcRequired = new ArrayList(var22.length);
            var23 = var22;
            var24 = var22.length;

            for(var25 = 0; var25 < var24; ++var25) {
               var26 = var23[var25];
               this.mdcRequired.add(var26.trim());
            }
         } else {
            this.mdcRequired = null;
         }
      } else {
         this.mdcRequired = null;
      }

      this.listChecker = (Rfc5424Layout.ListChecker)(var21 != null ? var21 : this.noopChecker);
      String var27 = var1 == null ? null : var1.getName();
      this.configName = Strings.isNotEmpty(var27) ? var27 : null;
      this.fieldFormatters = this.createFieldFormatters(var19, var1);
      this.procId = "-";
   }

   private Map<String, Rfc5424Layout.FieldFormatter> createFieldFormatters(LoggerFields[] var1, Configuration var2) {
      HashMap var3 = new HashMap(var1 == null ? 0 : var1.length);
      if (var1 != null) {
         LoggerFields[] var4 = var1;
         int var5 = var1.length;

         for(int var6 = 0; var6 < var5; ++var6) {
            LoggerFields var7 = var4[var6];
            StructuredDataId var8 = var7.getSdId() == null ? this.mdcSdId : var7.getSdId();
            HashMap var9 = new HashMap();
            Map var10 = var7.getMap();
            if (!var10.isEmpty()) {
               PatternParser var11 = createPatternParser(var2, (Class)null);
               Iterator var12 = var10.entrySet().iterator();

               while(var12.hasNext()) {
                  Entry var13 = (Entry)var12.next();
                  List var14 = var11.parse((String)var13.getValue());
                  var9.put(var13.getKey(), var14);
               }

               Rfc5424Layout.FieldFormatter var15 = new Rfc5424Layout.FieldFormatter(var9, var7.getDiscardIfAllFieldsAreEmpty());
               var3.put(var8.toString(), var15);
            }
         }
      }

      return var3.size() > 0 ? var3 : null;
   }

   private static PatternParser createPatternParser(Configuration var0, Class<? extends PatternConverter> var1) {
      if (var0 == null) {
         return new PatternParser(var0, "Converter", LogEventPatternConverter.class, var1);
      } else {
         PatternParser var2 = (PatternParser)var0.getComponent("RFC5424-Converter");
         if (var2 == null) {
            var2 = new PatternParser(var0, "Converter", ThrowablePatternConverter.class);
            var0.addComponent("RFC5424-Converter", var2);
            var2 = (PatternParser)var0.getComponent("RFC5424-Converter");
         }

         return var2;
      }
   }

   public Map<String, String> getContentFormat() {
      HashMap var1 = new HashMap();
      var1.put("structured", "true");
      var1.put("formatType", "RFC5424");
      return var1;
   }

   public String toSerializable(LogEvent var1) {
      StringBuilder var2 = getStringBuilder();
      this.appendPriority(var2, var1.getLevel());
      this.appendTimestamp(var2, var1.getTimeMillis());
      this.appendSpace(var2);
      this.appendHostName(var2);
      this.appendSpace(var2);
      this.appendAppName(var2);
      this.appendSpace(var2);
      this.appendProcessId(var2);
      this.appendSpace(var2);
      this.appendMessageId(var2, var1.getMessage());
      this.appendSpace(var2);
      this.appendStructuredElements(var2, var1);
      this.appendMessage(var2, var1);
      return this.useTlsMessageFormat ? (new TlsSyslogFrame(var2.toString())).toString() : var2.toString();
   }

   private void appendPriority(StringBuilder var1, Level var2) {
      var1.append('<');
      var1.append(Priority.getPriority(this.facility, var2));
      var1.append(">1 ");
   }

   private void appendTimestamp(StringBuilder var1, long var2) {
      var1.append(this.computeTimeStampString(var2));
   }

   private void appendSpace(StringBuilder var1) {
      var1.append(' ');
   }

   private void appendHostName(StringBuilder var1) {
      var1.append(this.localHostName);
   }

   private void appendAppName(StringBuilder var1) {
      if (this.appName != null) {
         var1.append(this.appName);
      } else if (this.configName != null) {
         var1.append(this.configName);
      } else {
         var1.append('-');
      }

   }

   private void appendProcessId(StringBuilder var1) {
      var1.append(this.getProcId());
   }

   private void appendMessageId(StringBuilder var1, Message var2) {
      boolean var3 = var2 instanceof StructuredDataMessage;
      String var4 = var3 ? ((StructuredDataMessage)var2).getType() : null;
      if (var4 != null) {
         var1.append(var4);
      } else if (this.messageId != null) {
         var1.append(this.messageId);
      } else {
         var1.append('-');
      }

   }

   private void appendMessage(StringBuilder var1, LogEvent var2) {
      Message var3 = var2.getMessage();
      String var4 = var3 instanceof StructuredDataMessage ? var3.getFormat() : var3.getFormattedMessage();
      if (var4 != null && var4.length() > 0) {
         var1.append(' ').append(this.escapeNewlines(var4, this.escapeNewLine));
      }

      if (this.exceptionFormatters != null && var2.getThrown() != null) {
         StringBuilder var5 = new StringBuilder("\n");
         Iterator var6 = this.exceptionFormatters.iterator();

         while(var6.hasNext()) {
            PatternFormatter var7 = (PatternFormatter)var6.next();
            var7.format(var2, var5);
         }

         var1.append(this.escapeNewlines(var5.toString(), this.escapeNewLine));
      }

      if (this.includeNewLine) {
         var1.append("\n");
      }

   }

   private void appendStructuredElements(StringBuilder var1, LogEvent var2) {
      Message var3 = var2.getMessage();
      boolean var4 = var3 instanceof StructuredDataMessage;
      if (!var4 && this.fieldFormatters != null && this.fieldFormatters.isEmpty() && !this.includeMdc) {
         var1.append('-');
      } else {
         HashMap var5 = new HashMap();
         Map var6 = var2.getContextData().toMap();
         if (this.mdcRequired != null) {
            this.checkRequired(var6);
         }

         Iterator var7;
         Entry var8;
         if (this.fieldFormatters != null) {
            var7 = this.fieldFormatters.entrySet().iterator();

            while(var7.hasNext()) {
               var8 = (Entry)var7.next();
               String var9 = (String)var8.getKey();
               Rfc5424Layout.StructuredDataElement var10 = ((Rfc5424Layout.FieldFormatter)var8.getValue()).format(var2);
               var5.put(var9, var10);
            }
         }

         if (this.includeMdc && var6.size() > 0) {
            String var12 = this.mdcSdId.toString();
            Rfc5424Layout.StructuredDataElement var14 = (Rfc5424Layout.StructuredDataElement)var5.get(var12);
            if (var14 != null) {
               var14.union(var6);
               var5.put(var12, var14);
            } else {
               Rfc5424Layout.StructuredDataElement var16 = new Rfc5424Layout.StructuredDataElement(var6, false);
               var5.put(var12, var16);
            }
         }

         if (var4) {
            StructuredDataMessage var13 = (StructuredDataMessage)var3;
            Map var15 = var13.getData();
            StructuredDataId var17 = var13.getId();
            String var18 = this.getId(var17);
            Rfc5424Layout.StructuredDataElement var11;
            if (var5.containsKey(var18)) {
               var11 = (Rfc5424Layout.StructuredDataElement)var5.get(var17.toString());
               var11.union(var15);
               var5.put(var18, var11);
            } else {
               var11 = new Rfc5424Layout.StructuredDataElement(var15, false);
               var5.put(var18, var11);
            }
         }

         if (var5.isEmpty()) {
            var1.append('-');
         } else {
            var7 = var5.entrySet().iterator();

            while(var7.hasNext()) {
               var8 = (Entry)var7.next();
               this.formatStructuredElement((String)var8.getKey(), this.mdcPrefix, (Rfc5424Layout.StructuredDataElement)var8.getValue(), var1, this.listChecker);
            }

         }
      }
   }

   private String escapeNewlines(String var1, String var2) {
      return null == var2 ? var1 : NEWLINE_PATTERN.matcher(var1).replaceAll(var2);
   }

   protected String getProcId() {
      return this.procId;
   }

   protected List<String> getMdcExcludes() {
      return this.mdcExcludes;
   }

   protected List<String> getMdcIncludes() {
      return this.mdcIncludes;
   }

   private String computeTimeStampString(long var1) {
      long var3;
      synchronized(this) {
         var3 = this.lastTimestamp;
         if (var1 == this.lastTimestamp) {
            return this.timestamppStr;
         }
      }

      StringBuilder var5 = new StringBuilder();
      GregorianCalendar var6 = new GregorianCalendar();
      var6.setTimeInMillis(var1);
      var5.append(Integer.toString(var6.get(1)));
      var5.append('-');
      this.pad(var6.get(2) + 1, 10, var5);
      var5.append('-');
      this.pad(var6.get(5), 10, var5);
      var5.append('T');
      this.pad(var6.get(11), 10, var5);
      var5.append(':');
      this.pad(var6.get(12), 10, var5);
      var5.append(':');
      this.pad(var6.get(13), 10, var5);
      var5.append('.');
      this.pad(var6.get(14), 100, var5);
      int var7 = (var6.get(15) + var6.get(16)) / '\uea60';
      if (var7 == 0) {
         var5.append('Z');
      } else {
         if (var7 < 0) {
            var7 = -var7;
            var5.append('-');
         } else {
            var5.append('+');
         }

         int var8 = var7 / 60;
         var7 -= var8 * 60;
         this.pad(var8, 10, var5);
         var5.append(':');
         this.pad(var7, 10, var5);
      }

      synchronized(this) {
         if (var3 == this.lastTimestamp) {
            this.lastTimestamp = var1;
            this.timestamppStr = var5.toString();
         }
      }

      return var5.toString();
   }

   private void pad(int var1, int var2, StringBuilder var3) {
      for(; var2 > 1; var2 /= 10) {
         if (var1 < var2) {
            var3.append('0');
         }
      }

      var3.append(Integer.toString(var1));
   }

   private void formatStructuredElement(String var1, String var2, Rfc5424Layout.StructuredDataElement var3, StringBuilder var4, Rfc5424Layout.ListChecker var5) {
      if ((var1 != null || this.defaultId != null) && !var3.discard()) {
         var4.append('[');
         var4.append(var1);
         if (!this.mdcSdId.toString().equals(var1)) {
            this.appendMap(var2, var3.getFields(), var4, this.noopChecker);
         } else {
            this.appendMap(var2, var3.getFields(), var4, var5);
         }

         var4.append(']');
      }
   }

   private String getId(StructuredDataId var1) {
      StringBuilder var2 = new StringBuilder();
      if (var1 != null && var1.getName() != null) {
         var2.append(var1.getName());
      } else {
         var2.append(this.defaultId);
      }

      int var3 = var1 != null ? var1.getEnterpriseNumber() : this.enterpriseNumber;
      if (var3 < 0) {
         var3 = this.enterpriseNumber;
      }

      if (var3 >= 0) {
         var2.append('@').append(var3);
      }

      return var2.toString();
   }

   private void checkRequired(Map<String, String> var1) {
      Iterator var2 = this.mdcRequired.iterator();

      String var3;
      String var4;
      do {
         if (!var2.hasNext()) {
            return;
         }

         var3 = (String)var2.next();
         var4 = (String)var1.get(var3);
      } while(var4 != null);

      throw new LoggingException("Required key " + var3 + " is missing from the " + this.mdcId);
   }

   private void appendMap(String var1, Map<String, String> var2, StringBuilder var3, Rfc5424Layout.ListChecker var4) {
      TreeMap var5 = new TreeMap(var2);
      Iterator var6 = var5.entrySet().iterator();

      while(var6.hasNext()) {
         Entry var7 = (Entry)var6.next();
         if (var4.check((String)var7.getKey()) && var7.getValue() != null) {
            var3.append(' ');
            if (var1 != null) {
               var3.append(var1);
            }

            String var8 = this.escapeNewlines(this.escapeSDParams((String)var7.getKey()), this.escapeNewLine);
            String var9 = this.escapeNewlines(this.escapeSDParams((String)var7.getValue()), this.escapeNewLine);
            StringBuilders.appendKeyDqValue(var3, var8, var9);
         }
      }

   }

   private String escapeSDParams(String var1) {
      return PARAM_VALUE_ESCAPE_PATTERN.matcher(var1).replaceAll("\\\\$0");
   }

   public String toString() {
      StringBuilder var1 = new StringBuilder();
      var1.append("facility=").append(this.facility.name());
      var1.append(" appName=").append(this.appName);
      var1.append(" defaultId=").append(this.defaultId);
      var1.append(" enterpriseNumber=").append(this.enterpriseNumber);
      var1.append(" newLine=").append(this.includeNewLine);
      var1.append(" includeMDC=").append(this.includeMdc);
      var1.append(" messageId=").append(this.messageId);
      return var1.toString();
   }

   @PluginFactory
   public static Rfc5424Layout createLayout(@PluginAttribute(value = "facility",defaultString = "LOCAL0") Facility var0, @PluginAttribute("id") String var1, @PluginAttribute(value = "enterpriseNumber",defaultInt = 18060) int var2, @PluginAttribute(value = "includeMDC",defaultBoolean = true) boolean var3, @PluginAttribute(value = "mdcId",defaultString = "mdc") String var4, @PluginAttribute("mdcPrefix") String var5, @PluginAttribute("eventPrefix") String var6, @PluginAttribute("newLine") boolean var7, @PluginAttribute("newLineEscape") String var8, @PluginAttribute("appName") String var9, @PluginAttribute("messageId") String var10, @PluginAttribute("mdcExcludes") String var11, @PluginAttribute("mdcIncludes") String var12, @PluginAttribute("mdcRequired") String var13, @PluginAttribute("exceptionPattern") String var14, @PluginAttribute("useTlsMessageFormat") boolean var15, @PluginElement("LoggerFields") LoggerFields[] var16, @PluginConfiguration Configuration var17) {
      if (var12 != null && var11 != null) {
         LOGGER.error("mdcIncludes and mdcExcludes are mutually exclusive. Includes wil be ignored");
         var12 = null;
      }

      return new Rfc5424Layout(var17, var0, var1, var2, var3, var7, var8, var4, var5, var6, var9, var10, var11, var12, var13, StandardCharsets.UTF_8, var14, var15, var16);
   }

   public Facility getFacility() {
      return this.facility;
   }

   private class StructuredDataElement {
      private final Map<String, String> fields;
      private final boolean discardIfEmpty;

      public StructuredDataElement(Map<String, String> var2, boolean var3) {
         super();
         this.discardIfEmpty = var3;
         this.fields = var2;
      }

      boolean discard() {
         if (!this.discardIfEmpty) {
            return false;
         } else {
            boolean var1 = false;
            Iterator var2 = this.fields.entrySet().iterator();

            while(var2.hasNext()) {
               Entry var3 = (Entry)var2.next();
               if (Strings.isNotEmpty((CharSequence)var3.getValue())) {
                  var1 = true;
                  break;
               }
            }

            return !var1;
         }
      }

      void union(Map<String, String> var1) {
         this.fields.putAll(var1);
      }

      Map<String, String> getFields() {
         return this.fields;
      }
   }

   private class FieldFormatter {
      private final Map<String, List<PatternFormatter>> delegateMap;
      private final boolean discardIfEmpty;

      public FieldFormatter(Map<String, List<PatternFormatter>> var2, boolean var3) {
         super();
         this.discardIfEmpty = var3;
         this.delegateMap = var2;
      }

      public Rfc5424Layout.StructuredDataElement format(LogEvent var1) {
         HashMap var2 = new HashMap(this.delegateMap.size());
         Iterator var3 = this.delegateMap.entrySet().iterator();

         while(var3.hasNext()) {
            Entry var4 = (Entry)var3.next();
            StringBuilder var5 = new StringBuilder();
            Iterator var6 = ((List)var4.getValue()).iterator();

            while(var6.hasNext()) {
               PatternFormatter var7 = (PatternFormatter)var6.next();
               var7.format(var1, var5);
            }

            var2.put(var4.getKey(), var5.toString());
         }

         return Rfc5424Layout.this.new StructuredDataElement(var2, this.discardIfEmpty);
      }
   }

   private class NoopChecker implements Rfc5424Layout.ListChecker {
      private NoopChecker() {
         super();
      }

      public boolean check(String var1) {
         return true;
      }

      // $FF: synthetic method
      NoopChecker(Object var2) {
         this();
      }
   }

   private class ExcludeChecker implements Rfc5424Layout.ListChecker {
      private ExcludeChecker() {
         super();
      }

      public boolean check(String var1) {
         return !Rfc5424Layout.this.mdcExcludes.contains(var1);
      }

      // $FF: synthetic method
      ExcludeChecker(Object var2) {
         this();
      }
   }

   private class IncludeChecker implements Rfc5424Layout.ListChecker {
      private IncludeChecker() {
         super();
      }

      public boolean check(String var1) {
         return Rfc5424Layout.this.mdcIncludes.contains(var1);
      }

      // $FF: synthetic method
      IncludeChecker(Object var2) {
         this();
      }
   }

   private interface ListChecker {
      boolean check(String var1);
   }
}
