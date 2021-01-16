package org.apache.commons.io.filefilter;

import java.util.List;

public interface ConditionalFileFilter {
   void addFileFilter(IOFileFilter var1);

   List<IOFileFilter> getFileFilters();

   boolean removeFileFilter(IOFileFilter var1);

   void setFileFilters(List<IOFileFilter> var1);
}
