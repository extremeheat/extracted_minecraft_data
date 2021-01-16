package org.apache.logging.log4j;

import java.io.Serializable;

public interface Marker extends Serializable {
   Marker addParents(Marker... var1);

   boolean equals(Object var1);

   String getName();

   Marker[] getParents();

   int hashCode();

   boolean hasParents();

   boolean isInstanceOf(Marker var1);

   boolean isInstanceOf(String var1);

   boolean remove(Marker var1);

   Marker setParents(Marker... var1);
}
