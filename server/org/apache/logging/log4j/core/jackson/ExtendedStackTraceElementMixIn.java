package org.apache.logging.log4j.core.jackson;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import java.io.Serializable;
import org.apache.logging.log4j.core.impl.ExtendedClassInfo;

@JsonPropertyOrder({"class", "method", "file", "line", "exact", "location", "version"})
abstract class ExtendedStackTraceElementMixIn implements Serializable {
   private static final long serialVersionUID = 1L;

   @JsonCreator
   public ExtendedStackTraceElementMixIn(@JsonProperty("class") String var1, @JsonProperty("method") String var2, @JsonProperty("file") String var3, @JsonProperty("line") int var4, @JsonProperty("exact") boolean var5, @JsonProperty("location") String var6, @JsonProperty("version") String var7) {
      super();
   }

   @JsonProperty("class")
   @JacksonXmlProperty(
      localName = "class",
      isAttribute = true
   )
   public abstract String getClassName();

   @JsonProperty
   @JacksonXmlProperty(
      isAttribute = true
   )
   public abstract boolean getExact();

   @JsonIgnore
   public abstract ExtendedClassInfo getExtraClassInfo();

   @JsonProperty("file")
   @JacksonXmlProperty(
      localName = "file",
      isAttribute = true
   )
   public abstract String getFileName();

   @JsonProperty("line")
   @JacksonXmlProperty(
      localName = "line",
      isAttribute = true
   )
   public abstract int getLineNumber();

   @JsonProperty
   @JacksonXmlProperty(
      isAttribute = true
   )
   public abstract String getLocation();

   @JsonProperty("method")
   @JacksonXmlProperty(
      localName = "method",
      isAttribute = true
   )
   public abstract String getMethodName();

   @JsonIgnore
   abstract StackTraceElement getStackTraceElement();

   @JsonProperty
   @JacksonXmlProperty(
      isAttribute = true
   )
   public abstract String getVersion();

   @JsonIgnore
   public abstract boolean isNativeMethod();
}
