package io.netty.util;

public interface ResourceLeakTracker<T> {
   void record();

   void record(Object var1);

   boolean close(T var1);
}
