package org.apache.logging.log4j.util;

import java.io.Serializable;
import java.util.Map;

public interface ReadOnlyStringMap extends Serializable {
   Map<String, String> toMap();

   boolean containsKey(String var1);

   <V> void forEach(BiConsumer<String, ? super V> var1);

   <V, S> void forEach(TriConsumer<String, ? super V, S> var1, S var2);

   <V> V getValue(String var1);

   boolean isEmpty();

   int size();
}
