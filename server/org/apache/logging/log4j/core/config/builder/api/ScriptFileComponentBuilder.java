package org.apache.logging.log4j.core.config.builder.api;

public interface ScriptFileComponentBuilder extends ComponentBuilder<ScriptFileComponentBuilder> {
   ScriptFileComponentBuilder addLanguage(String var1);

   ScriptFileComponentBuilder addIsWatched(boolean var1);

   ScriptFileComponentBuilder addIsWatched(String var1);

   ScriptFileComponentBuilder addCharset(String var1);
}
