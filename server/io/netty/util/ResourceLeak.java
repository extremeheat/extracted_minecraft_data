package io.netty.util;

/** @deprecated */
@Deprecated
public interface ResourceLeak {
   void record();

   void record(Object var1);

   boolean close();
}
