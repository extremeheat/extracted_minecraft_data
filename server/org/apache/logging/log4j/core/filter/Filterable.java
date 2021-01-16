package org.apache.logging.log4j.core.filter;

import org.apache.logging.log4j.core.Filter;
import org.apache.logging.log4j.core.LifeCycle;
import org.apache.logging.log4j.core.LogEvent;

public interface Filterable extends LifeCycle {
   void addFilter(Filter var1);

   void removeFilter(Filter var1);

   Filter getFilter();

   boolean hasFilter();

   boolean isFiltered(LogEvent var1);
}
