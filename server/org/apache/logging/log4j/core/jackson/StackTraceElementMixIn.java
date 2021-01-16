package org.apache.logging.log4j.core.jackson;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;

@JsonIgnoreProperties({"nativeMethod"})
abstract class StackTraceElementMixIn {
   @JsonCreator
   StackTraceElementMixIn(@JsonProperty("class") String var1, @JsonProperty("method") String var2, @JsonProperty("file") String var3, @JsonProperty("line") int var4) {
      super();
   }

   @JsonProperty("class")
   @JacksonXmlProperty(
      localName = "class",
      isAttribute = true
   )
   abstract String getClassName();

   @JsonProperty("file")
   @JacksonXmlProperty(
      localName = "file",
      isAttribute = true
   )
   abstract String getFileName();

   @JsonProperty("line")
   @JacksonXmlProperty(
      localName = "line",
      isAttribute = true
   )
   abstract int getLineNumber();

   @JsonProperty("method")
   @JacksonXmlProperty(
      localName = "method",
      isAttribute = true
   )
   abstract String getMethodName();
}
