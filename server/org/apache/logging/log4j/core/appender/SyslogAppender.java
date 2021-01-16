package org.apache.logging.log4j.core.appender;

import java.io.Serializable;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import org.apache.logging.log4j.core.Filter;
import org.apache.logging.log4j.core.Layout;
import org.apache.logging.log4j.core.config.Configuration;
import org.apache.logging.log4j.core.config.plugins.Plugin;
import org.apache.logging.log4j.core.config.plugins.PluginBuilderAttribute;
import org.apache.logging.log4j.core.config.plugins.PluginBuilderFactory;
import org.apache.logging.log4j.core.config.plugins.PluginElement;
import org.apache.logging.log4j.core.layout.LoggerFields;
import org.apache.logging.log4j.core.layout.Rfc5424Layout;
import org.apache.logging.log4j.core.layout.SyslogLayout;
import org.apache.logging.log4j.core.net.AbstractSocketManager;
import org.apache.logging.log4j.core.net.Advertiser;
import org.apache.logging.log4j.core.net.Facility;
import org.apache.logging.log4j.core.net.Protocol;
import org.apache.logging.log4j.core.net.SocketOptions;
import org.apache.logging.log4j.core.net.ssl.SslConfiguration;
import org.apache.logging.log4j.core.util.Constants;
import org.apache.logging.log4j.util.EnglishEnums;

@Plugin(
   name = "Syslog",
   category = "Core",
   elementType = "appender",
   printObject = true
)
public class SyslogAppender extends SocketAppender {
   protected static final String RFC5424 = "RFC5424";

   protected SyslogAppender(String var1, Layout<? extends Serializable> var2, Filter var3, boolean var4, boolean var5, AbstractSocketManager var6, Advertiser var7) {
      super(var1, var2, var3, var6, var4, var5, var7);
   }

   /** @deprecated */
   @Deprecated
   public static <B extends SyslogAppender.Builder<B>> SyslogAppender createAppender(String var0, int var1, String var2, SslConfiguration var3, int var4, int var5, boolean var6, String var7, boolean var8, boolean var9, Facility var10, String var11, int var12, boolean var13, String var14, String var15, String var16, boolean var17, String var18, String var19, String var20, String var21, String var22, String var23, String var24, Filter var25, Configuration var26, Charset var27, String var28, LoggerFields[] var29, boolean var30) {
      return ((SyslogAppender.Builder)((SyslogAppender.Builder)((SyslogAppender.Builder)((SyslogAppender.Builder)((SyslogAppender.Builder)((SyslogAppender.Builder)((SyslogAppender.Builder)((SyslogAppender.Builder)((SyslogAppender.Builder)((SyslogAppender.Builder)((SyslogAppender.Builder)((SyslogAppender.Builder)((SyslogAppender.Builder)newSyslogAppenderBuilder().withHost(var0)).withPort(var1)).withProtocol((Protocol)EnglishEnums.valueOf(Protocol.class, var2))).withSslConfiguration(var3)).withConnectTimeoutMillis(var4)).withReconnectDelayMillis(var5)).withImmediateFail(var6)).withName(var19)).withImmediateFlush(var8)).withIgnoreExceptions(var9)).withFilter(var25)).setConfiguration(var26)).withAdvertise(var30)).setFacility(var10).setId(var11).setEnterpriseNumber(var12).setIncludeMdc(var13).setMdcId(var14).setMdcPrefix(var15).setEventPrefix(var16).setNewLine(var17).setAppName(var19).setMsgId(var20).setExcludes(var21).setIncludeMdc(var13).setRequired(var23).setFormat(var24).setCharsetName(var27).setExceptionPattern(var28).setLoggerFields(var29).build();
   }

   @PluginBuilderFactory
   public static <B extends SyslogAppender.Builder<B>> B newSyslogAppenderBuilder() {
      return (SyslogAppender.Builder)(new SyslogAppender.Builder()).asBuilder();
   }

   public static class Builder<B extends SyslogAppender.Builder<B>> extends SocketAppender.AbstractBuilder<B> implements org.apache.logging.log4j.core.util.Builder<SocketAppender> {
      @PluginBuilderAttribute("facility")
      private Facility facility;
      @PluginBuilderAttribute("id")
      private String id;
      @PluginBuilderAttribute("enterpriseNumber")
      private int enterpriseNumber;
      @PluginBuilderAttribute("includeMdc")
      private boolean includeMdc;
      @PluginBuilderAttribute("mdcId")
      private String mdcId;
      @PluginBuilderAttribute("mdcPrefix")
      private String mdcPrefix;
      @PluginBuilderAttribute("eventPrefix")
      private String eventPrefix;
      @PluginBuilderAttribute("newLine")
      private boolean newLine;
      @PluginBuilderAttribute("newLineEscape")
      private String escapeNL;
      @PluginBuilderAttribute("appName")
      private String appName;
      @PluginBuilderAttribute("messageId")
      private String msgId;
      @PluginBuilderAttribute("mdcExcludes")
      private String excludes;
      @PluginBuilderAttribute("mdcIncludes")
      private String includes;
      @PluginBuilderAttribute("mdcRequired")
      private String required;
      @PluginBuilderAttribute("format")
      private String format;
      @PluginBuilderAttribute("charset")
      private Charset charsetName;
      @PluginBuilderAttribute("exceptionPattern")
      private String exceptionPattern;
      @PluginElement("LoggerFields")
      private LoggerFields[] loggerFields;

      public Builder() {
         super();
         this.facility = Facility.LOCAL0;
         this.enterpriseNumber = 18060;
         this.includeMdc = true;
         this.charsetName = StandardCharsets.UTF_8;
      }

      public SyslogAppender build() {
         Protocol var1 = this.getProtocol();
         SslConfiguration var2 = this.getSslConfiguration();
         boolean var3 = var2 != null || var1 == Protocol.SSL;
         Configuration var4 = this.getConfiguration();
         Object var5 = this.getLayout();
         if (var5 == null) {
            var5 = "RFC5424".equalsIgnoreCase(this.format) ? Rfc5424Layout.createLayout(this.facility, this.id, this.enterpriseNumber, this.includeMdc, this.mdcId, this.mdcPrefix, this.eventPrefix, this.newLine, this.escapeNL, this.appName, this.msgId, this.excludes, this.includes, this.required, this.exceptionPattern, var3, this.loggerFields, var4) : ((SyslogLayout.Builder)SyslogLayout.newBuilder().setFacility(this.facility).setIncludeNewLine(this.newLine).setEscapeNL(this.escapeNL).setCharset(this.charsetName)).build();
         }

         String var6 = this.getName();
         if (var6 == null) {
            SyslogAppender.LOGGER.error("No name provided for SyslogAppender");
            return null;
         } else {
            AbstractSocketManager var7 = SocketAppender.createSocketManager(var6, var1, this.getHost(), this.getPort(), this.getConnectTimeoutMillis(), var2, this.getReconnectDelayMillis(), this.getImmediateFail(), (Layout)var5, Constants.ENCODER_BYTE_BUFFER_SIZE, (SocketOptions)null);
            return new SyslogAppender(var6, (Layout)var5, this.getFilter(), this.isIgnoreExceptions(), this.isImmediateFlush(), var7, this.getAdvertise() ? var4.getAdvertiser() : null);
         }
      }

      public Facility getFacility() {
         return this.facility;
      }

      public String getId() {
         return this.id;
      }

      public int getEnterpriseNumber() {
         return this.enterpriseNumber;
      }

      public boolean isIncludeMdc() {
         return this.includeMdc;
      }

      public String getMdcId() {
         return this.mdcId;
      }

      public String getMdcPrefix() {
         return this.mdcPrefix;
      }

      public String getEventPrefix() {
         return this.eventPrefix;
      }

      public boolean isNewLine() {
         return this.newLine;
      }

      public String getEscapeNL() {
         return this.escapeNL;
      }

      public String getAppName() {
         return this.appName;
      }

      public String getMsgId() {
         return this.msgId;
      }

      public String getExcludes() {
         return this.excludes;
      }

      public String getIncludes() {
         return this.includes;
      }

      public String getRequired() {
         return this.required;
      }

      public String getFormat() {
         return this.format;
      }

      public Charset getCharsetName() {
         return this.charsetName;
      }

      public String getExceptionPattern() {
         return this.exceptionPattern;
      }

      public LoggerFields[] getLoggerFields() {
         return this.loggerFields;
      }

      public B setFacility(Facility var1) {
         this.facility = var1;
         return (SyslogAppender.Builder)this.asBuilder();
      }

      public B setId(String var1) {
         this.id = var1;
         return (SyslogAppender.Builder)this.asBuilder();
      }

      public B setEnterpriseNumber(int var1) {
         this.enterpriseNumber = var1;
         return (SyslogAppender.Builder)this.asBuilder();
      }

      public B setIncludeMdc(boolean var1) {
         this.includeMdc = var1;
         return (SyslogAppender.Builder)this.asBuilder();
      }

      public B setMdcId(String var1) {
         this.mdcId = var1;
         return (SyslogAppender.Builder)this.asBuilder();
      }

      public B setMdcPrefix(String var1) {
         this.mdcPrefix = var1;
         return (SyslogAppender.Builder)this.asBuilder();
      }

      public B setEventPrefix(String var1) {
         this.eventPrefix = var1;
         return (SyslogAppender.Builder)this.asBuilder();
      }

      public B setNewLine(boolean var1) {
         this.newLine = var1;
         return (SyslogAppender.Builder)this.asBuilder();
      }

      public B setEscapeNL(String var1) {
         this.escapeNL = var1;
         return (SyslogAppender.Builder)this.asBuilder();
      }

      public B setAppName(String var1) {
         this.appName = var1;
         return (SyslogAppender.Builder)this.asBuilder();
      }

      public B setMsgId(String var1) {
         this.msgId = var1;
         return (SyslogAppender.Builder)this.asBuilder();
      }

      public B setExcludes(String var1) {
         this.excludes = var1;
         return (SyslogAppender.Builder)this.asBuilder();
      }

      public B setIncludes(String var1) {
         this.includes = var1;
         return (SyslogAppender.Builder)this.asBuilder();
      }

      public B setRequired(String var1) {
         this.required = var1;
         return (SyslogAppender.Builder)this.asBuilder();
      }

      public B setFormat(String var1) {
         this.format = var1;
         return (SyslogAppender.Builder)this.asBuilder();
      }

      public B setCharsetName(Charset var1) {
         this.charsetName = var1;
         return (SyslogAppender.Builder)this.asBuilder();
      }

      public B setExceptionPattern(String var1) {
         this.exceptionPattern = var1;
         return (SyslogAppender.Builder)this.asBuilder();
      }

      public B setLoggerFields(LoggerFields[] var1) {
         this.loggerFields = var1;
         return (SyslogAppender.Builder)this.asBuilder();
      }
   }
}
