package org.apache.logging.log4j.core.layout;

import java.nio.charset.Charset;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.logging.log4j.core.LogEvent;
import org.apache.logging.log4j.core.config.Configuration;
import org.apache.logging.log4j.core.config.DefaultConfiguration;
import org.apache.logging.log4j.core.config.plugins.Plugin;
import org.apache.logging.log4j.core.config.plugins.PluginAttribute;
import org.apache.logging.log4j.core.config.plugins.PluginBuilderAttribute;
import org.apache.logging.log4j.core.config.plugins.PluginBuilderFactory;
import org.apache.logging.log4j.core.config.plugins.PluginConfiguration;
import org.apache.logging.log4j.core.config.plugins.PluginElement;
import org.apache.logging.log4j.core.config.plugins.PluginFactory;
import org.apache.logging.log4j.core.pattern.LogEventPatternConverter;
import org.apache.logging.log4j.core.pattern.PatternFormatter;
import org.apache.logging.log4j.core.pattern.PatternParser;
import org.apache.logging.log4j.core.pattern.RegexReplacement;
import org.apache.logging.log4j.util.Strings;

@Plugin(
   name = "PatternLayout",
   category = "Core",
   elementType = "layout",
   printObject = true
)
public final class PatternLayout extends AbstractStringLayout {
   public static final String DEFAULT_CONVERSION_PATTERN = "%m%n";
   public static final String TTCC_CONVERSION_PATTERN = "%r [%t] %p %c %notEmpty{%x }- %m%n";
   public static final String SIMPLE_CONVERSION_PATTERN = "%d [%t] %p %c - %m%n";
   public static final String KEY = "Converter";
   private final String conversionPattern;
   private final PatternSelector patternSelector;
   private final AbstractStringLayout.Serializer eventSerializer;

   private PatternLayout(Configuration var1, RegexReplacement var2, String var3, PatternSelector var4, Charset var5, boolean var6, boolean var7, boolean var8, String var9, String var10) {
      super(var1, var5, newSerializerBuilder().setConfiguration(var1).setReplace(var2).setPatternSelector(var4).setAlwaysWriteExceptions(var6).setDisableAnsi(var7).setNoConsoleNoAnsi(var8).setPattern(var9).build(), newSerializerBuilder().setConfiguration(var1).setReplace(var2).setPatternSelector(var4).setAlwaysWriteExceptions(var6).setDisableAnsi(var7).setNoConsoleNoAnsi(var8).setPattern(var10).build());
      this.conversionPattern = var3;
      this.patternSelector = var4;
      this.eventSerializer = newSerializerBuilder().setConfiguration(var1).setReplace(var2).setPatternSelector(var4).setAlwaysWriteExceptions(var6).setDisableAnsi(var7).setNoConsoleNoAnsi(var8).setPattern(var3).setDefaultPattern("%m%n").build();
   }

   public static PatternLayout.SerializerBuilder newSerializerBuilder() {
      return new PatternLayout.SerializerBuilder();
   }

   /** @deprecated */
   @Deprecated
   public static AbstractStringLayout.Serializer createSerializer(Configuration var0, RegexReplacement var1, String var2, String var3, PatternSelector var4, boolean var5, boolean var6) {
      PatternLayout.SerializerBuilder var7 = newSerializerBuilder();
      var7.setAlwaysWriteExceptions(var5);
      var7.setConfiguration(var0);
      var7.setDefaultPattern(var3);
      var7.setNoConsoleNoAnsi(var6);
      var7.setPattern(var2);
      var7.setPatternSelector(var4);
      var7.setReplace(var1);
      return var7.build();
   }

   public String getConversionPattern() {
      return this.conversionPattern;
   }

   public Map<String, String> getContentFormat() {
      HashMap var1 = new HashMap();
      var1.put("structured", "false");
      var1.put("formatType", "conversion");
      var1.put("format", this.conversionPattern);
      return var1;
   }

   public String toSerializable(LogEvent var1) {
      return this.eventSerializer.toSerializable(var1);
   }

   public void encode(LogEvent var1, ByteBufferDestination var2) {
      if (!(this.eventSerializer instanceof AbstractStringLayout.Serializer2)) {
         super.encode(var1, var2);
      } else {
         StringBuilder var3 = this.toText((AbstractStringLayout.Serializer2)this.eventSerializer, var1, getStringBuilder());
         Encoder var4 = this.getStringBuilderEncoder();
         var4.encode(var3, var2);
         trimToMaxSize(var3);
      }
   }

   private StringBuilder toText(AbstractStringLayout.Serializer2 var1, LogEvent var2, StringBuilder var3) {
      return var1.toSerializable(var2, var3);
   }

   public static PatternParser createPatternParser(Configuration var0) {
      if (var0 == null) {
         return new PatternParser(var0, "Converter", LogEventPatternConverter.class);
      } else {
         PatternParser var1 = (PatternParser)var0.getComponent("Converter");
         if (var1 == null) {
            var1 = new PatternParser(var0, "Converter", LogEventPatternConverter.class);
            var0.addComponent("Converter", var1);
            var1 = (PatternParser)var0.getComponent("Converter");
         }

         return var1;
      }
   }

   public String toString() {
      return this.patternSelector == null ? this.conversionPattern : this.patternSelector.toString();
   }

   /** @deprecated */
   @PluginFactory
   @Deprecated
   public static PatternLayout createLayout(@PluginAttribute(value = "pattern",defaultString = "%m%n") String var0, @PluginElement("PatternSelector") PatternSelector var1, @PluginConfiguration Configuration var2, @PluginElement("Replace") RegexReplacement var3, @PluginAttribute("charset") Charset var4, @PluginAttribute(value = "alwaysWriteExceptions",defaultBoolean = true) boolean var5, @PluginAttribute("noConsoleNoAnsi") boolean var6, @PluginAttribute("header") String var7, @PluginAttribute("footer") String var8) {
      return newBuilder().withPattern(var0).withPatternSelector(var1).withConfiguration(var2).withRegexReplacement(var3).withCharset(var4).withAlwaysWriteExceptions(var5).withNoConsoleNoAnsi(var6).withHeader(var7).withFooter(var8).build();
   }

   public static PatternLayout createDefaultLayout() {
      return newBuilder().build();
   }

   public static PatternLayout createDefaultLayout(Configuration var0) {
      return newBuilder().withConfiguration(var0).build();
   }

   @PluginBuilderFactory
   public static PatternLayout.Builder newBuilder() {
      return new PatternLayout.Builder();
   }

   // $FF: synthetic method
   PatternLayout(Configuration var1, RegexReplacement var2, String var3, PatternSelector var4, Charset var5, boolean var6, boolean var7, boolean var8, String var9, String var10, Object var11) {
      this(var1, var2, var3, var4, var5, var6, var7, var8, var9, var10);
   }

   public static class Builder implements org.apache.logging.log4j.core.util.Builder<PatternLayout> {
      @PluginBuilderAttribute
      private String pattern;
      @PluginElement("PatternSelector")
      private PatternSelector patternSelector;
      @PluginConfiguration
      private Configuration configuration;
      @PluginElement("Replace")
      private RegexReplacement regexReplacement;
      @PluginBuilderAttribute
      private Charset charset;
      @PluginBuilderAttribute
      private boolean alwaysWriteExceptions;
      @PluginBuilderAttribute
      private boolean disableAnsi;
      @PluginBuilderAttribute
      private boolean noConsoleNoAnsi;
      @PluginBuilderAttribute
      private String header;
      @PluginBuilderAttribute
      private String footer;

      private Builder() {
         super();
         this.pattern = "%m%n";
         this.charset = Charset.defaultCharset();
         this.alwaysWriteExceptions = true;
      }

      public PatternLayout.Builder withPattern(String var1) {
         this.pattern = var1;
         return this;
      }

      public PatternLayout.Builder withPatternSelector(PatternSelector var1) {
         this.patternSelector = var1;
         return this;
      }

      public PatternLayout.Builder withConfiguration(Configuration var1) {
         this.configuration = var1;
         return this;
      }

      public PatternLayout.Builder withRegexReplacement(RegexReplacement var1) {
         this.regexReplacement = var1;
         return this;
      }

      public PatternLayout.Builder withCharset(Charset var1) {
         if (var1 != null) {
            this.charset = var1;
         }

         return this;
      }

      public PatternLayout.Builder withAlwaysWriteExceptions(boolean var1) {
         this.alwaysWriteExceptions = var1;
         return this;
      }

      public PatternLayout.Builder withDisableAnsi(boolean var1) {
         this.disableAnsi = var1;
         return this;
      }

      public PatternLayout.Builder withNoConsoleNoAnsi(boolean var1) {
         this.noConsoleNoAnsi = var1;
         return this;
      }

      public PatternLayout.Builder withHeader(String var1) {
         this.header = var1;
         return this;
      }

      public PatternLayout.Builder withFooter(String var1) {
         this.footer = var1;
         return this;
      }

      public PatternLayout build() {
         if (this.configuration == null) {
            this.configuration = new DefaultConfiguration();
         }

         return new PatternLayout(this.configuration, this.regexReplacement, this.pattern, this.patternSelector, this.charset, this.alwaysWriteExceptions, this.disableAnsi, this.noConsoleNoAnsi, this.header, this.footer);
      }

      // $FF: synthetic method
      Builder(Object var1) {
         this();
      }
   }

   private static class PatternSelectorSerializer implements AbstractStringLayout.Serializer, AbstractStringLayout.Serializer2 {
      private final PatternSelector patternSelector;
      private final RegexReplacement replace;

      private PatternSelectorSerializer(PatternSelector var1, RegexReplacement var2) {
         super();
         this.patternSelector = var1;
         this.replace = var2;
      }

      public String toSerializable(LogEvent var1) {
         StringBuilder var2 = AbstractStringLayout.getStringBuilder();

         String var3;
         try {
            var3 = this.toSerializable(var1, var2).toString();
         } finally {
            AbstractStringLayout.trimToMaxSize(var2);
         }

         return var3;
      }

      public StringBuilder toSerializable(LogEvent var1, StringBuilder var2) {
         PatternFormatter[] var3 = this.patternSelector.getFormatters(var1);
         int var4 = var3.length;

         for(int var5 = 0; var5 < var4; ++var5) {
            var3[var5].format(var1, var2);
         }

         if (this.replace != null) {
            String var6 = var2.toString();
            var6 = this.replace.format(var6);
            var2.setLength(0);
            var2.append(var6);
         }

         return var2;
      }

      public String toString() {
         StringBuilder var1 = new StringBuilder();
         var1.append(super.toString());
         var1.append("[patternSelector=");
         var1.append(this.patternSelector);
         var1.append(", replace=");
         var1.append(this.replace);
         var1.append("]");
         return var1.toString();
      }

      // $FF: synthetic method
      PatternSelectorSerializer(PatternSelector var1, RegexReplacement var2, Object var3) {
         this(var1, var2);
      }
   }

   public static class SerializerBuilder implements org.apache.logging.log4j.core.util.Builder<AbstractStringLayout.Serializer> {
      private Configuration configuration;
      private RegexReplacement replace;
      private String pattern;
      private String defaultPattern;
      private PatternSelector patternSelector;
      private boolean alwaysWriteExceptions;
      private boolean disableAnsi;
      private boolean noConsoleNoAnsi;

      public SerializerBuilder() {
         super();
      }

      public AbstractStringLayout.Serializer build() {
         if (Strings.isEmpty(this.pattern) && Strings.isEmpty(this.defaultPattern)) {
            return null;
         } else if (this.patternSelector == null) {
            try {
               PatternParser var1 = PatternLayout.createPatternParser(this.configuration);
               List var2 = var1.parse(this.pattern == null ? this.defaultPattern : this.pattern, this.alwaysWriteExceptions, this.disableAnsi, this.noConsoleNoAnsi);
               PatternFormatter[] var3 = (PatternFormatter[])var2.toArray(new PatternFormatter[0]);
               return new PatternLayout.PatternSerializer(var3, this.replace);
            } catch (RuntimeException var4) {
               throw new IllegalArgumentException("Cannot parse pattern '" + this.pattern + "'", var4);
            }
         } else {
            return new PatternLayout.PatternSelectorSerializer(this.patternSelector, this.replace);
         }
      }

      public PatternLayout.SerializerBuilder setConfiguration(Configuration var1) {
         this.configuration = var1;
         return this;
      }

      public PatternLayout.SerializerBuilder setReplace(RegexReplacement var1) {
         this.replace = var1;
         return this;
      }

      public PatternLayout.SerializerBuilder setPattern(String var1) {
         this.pattern = var1;
         return this;
      }

      public PatternLayout.SerializerBuilder setDefaultPattern(String var1) {
         this.defaultPattern = var1;
         return this;
      }

      public PatternLayout.SerializerBuilder setPatternSelector(PatternSelector var1) {
         this.patternSelector = var1;
         return this;
      }

      public PatternLayout.SerializerBuilder setAlwaysWriteExceptions(boolean var1) {
         this.alwaysWriteExceptions = var1;
         return this;
      }

      public PatternLayout.SerializerBuilder setDisableAnsi(boolean var1) {
         this.disableAnsi = var1;
         return this;
      }

      public PatternLayout.SerializerBuilder setNoConsoleNoAnsi(boolean var1) {
         this.noConsoleNoAnsi = var1;
         return this;
      }
   }

   private static class PatternSerializer implements AbstractStringLayout.Serializer, AbstractStringLayout.Serializer2 {
      private final PatternFormatter[] formatters;
      private final RegexReplacement replace;

      private PatternSerializer(PatternFormatter[] var1, RegexReplacement var2) {
         super();
         this.formatters = var1;
         this.replace = var2;
      }

      public String toSerializable(LogEvent var1) {
         StringBuilder var2 = AbstractStringLayout.getStringBuilder();

         String var3;
         try {
            var3 = this.toSerializable(var1, var2).toString();
         } finally {
            AbstractStringLayout.trimToMaxSize(var2);
         }

         return var3;
      }

      public StringBuilder toSerializable(LogEvent var1, StringBuilder var2) {
         int var3 = this.formatters.length;

         for(int var4 = 0; var4 < var3; ++var4) {
            this.formatters[var4].format(var1, var2);
         }

         if (this.replace != null) {
            String var5 = var2.toString();
            var5 = this.replace.format(var5);
            var2.setLength(0);
            var2.append(var5);
         }

         return var2;
      }

      public String toString() {
         StringBuilder var1 = new StringBuilder();
         var1.append(super.toString());
         var1.append("[formatters=");
         var1.append(Arrays.toString(this.formatters));
         var1.append(", replace=");
         var1.append(this.replace);
         var1.append("]");
         return var1.toString();
      }

      // $FF: synthetic method
      PatternSerializer(PatternFormatter[] var1, RegexReplacement var2, Object var3) {
         this(var1, var2);
      }
   }
}
