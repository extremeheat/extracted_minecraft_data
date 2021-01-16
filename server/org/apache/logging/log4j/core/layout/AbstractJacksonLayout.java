package org.apache.logging.log4j.core.layout;

import com.fasterxml.jackson.core.JsonGenerationException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectWriter;
import java.io.IOException;
import java.io.Writer;
import java.nio.charset.Charset;
import org.apache.logging.log4j.core.LogEvent;
import org.apache.logging.log4j.core.config.Configuration;
import org.apache.logging.log4j.core.config.plugins.PluginBuilderAttribute;
import org.apache.logging.log4j.core.impl.MutableLogEvent;
import org.apache.logging.log4j.core.util.StringBuilderWriter;

abstract class AbstractJacksonLayout extends AbstractStringLayout {
   protected static final String DEFAULT_EOL = "\r\n";
   protected static final String COMPACT_EOL = "";
   protected final String eol;
   protected final ObjectWriter objectWriter;
   protected final boolean compact;
   protected final boolean complete;

   protected AbstractJacksonLayout(Configuration var1, ObjectWriter var2, Charset var3, boolean var4, boolean var5, boolean var6, AbstractStringLayout.Serializer var7, AbstractStringLayout.Serializer var8) {
      super(var1, var3, var7, var8);
      this.objectWriter = var2;
      this.compact = var4;
      this.complete = var5;
      this.eol = var4 && !var6 ? "" : "\r\n";
   }

   public String toSerializable(LogEvent var1) {
      StringBuilderWriter var2 = new StringBuilderWriter();

      try {
         this.toSerializable(var1, var2);
         return var2.toString();
      } catch (IOException var4) {
         LOGGER.error((Object)var4);
         return "";
      }
   }

   private static LogEvent convertMutableToLog4jEvent(LogEvent var0) {
      return (LogEvent)(var0 instanceof MutableLogEvent ? ((MutableLogEvent)var0).createMemento() : var0);
   }

   public void toSerializable(LogEvent var1, Writer var2) throws JsonGenerationException, JsonMappingException, IOException {
      this.objectWriter.writeValue(var2, convertMutableToLog4jEvent(var1));
      var2.write(this.eol);
      this.markEvent();
   }

   public abstract static class Builder<B extends AbstractJacksonLayout.Builder<B>> extends AbstractStringLayout.Builder<B> {
      @PluginBuilderAttribute
      private boolean eventEol;
      @PluginBuilderAttribute
      private boolean compact;
      @PluginBuilderAttribute
      private boolean complete;

      public Builder() {
         super();
      }

      public boolean getEventEol() {
         return this.eventEol;
      }

      public boolean isCompact() {
         return this.compact;
      }

      public boolean isComplete() {
         return this.complete;
      }

      public B setEventEol(boolean var1) {
         this.eventEol = var1;
         return (AbstractJacksonLayout.Builder)this.asBuilder();
      }

      public B setCompact(boolean var1) {
         this.compact = var1;
         return (AbstractJacksonLayout.Builder)this.asBuilder();
      }

      public B setComplete(boolean var1) {
         this.complete = var1;
         return (AbstractJacksonLayout.Builder)this.asBuilder();
      }
   }
}
