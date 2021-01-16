package org.apache.logging.log4j.core.lookup;

import java.util.Map;
import org.apache.logging.log4j.core.LogEvent;
import org.apache.logging.log4j.core.config.plugins.Plugin;

@Plugin(
   name = "main",
   category = "Lookup"
)
public class MainMapLookup extends MapLookup {
   static final MapLookup MAIN_SINGLETON = new MapLookup(MapLookup.newMap(0));

   public MainMapLookup() {
      super();
   }

   public MainMapLookup(Map<String, String> var1) {
      super(var1);
   }

   public static void setMainArguments(String... var0) {
      if (var0 != null) {
         initMap(var0, MAIN_SINGLETON.getMap());
      }
   }

   public String lookup(LogEvent var1, String var2) {
      return (String)MAIN_SINGLETON.getMap().get(var2);
   }

   public String lookup(String var1) {
      return (String)MAIN_SINGLETON.getMap().get(var1);
   }
}
