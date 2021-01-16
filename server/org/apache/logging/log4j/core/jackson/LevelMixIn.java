package org.apache.logging.log4j.core.jackson;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonValue;
import org.apache.logging.log4j.Level;

@JsonIgnoreProperties({"name", "declaringClass", "standardLevel"})
abstract class LevelMixIn {
   LevelMixIn() {
      super();
   }

   @JsonCreator
   public static Level getLevel(@JsonProperty("name") String var0) {
      return null;
   }

   @JsonValue
   public abstract String name();
}
