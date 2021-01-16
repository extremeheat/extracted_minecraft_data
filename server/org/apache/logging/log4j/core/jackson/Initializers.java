package org.apache.logging.log4j.core.jackson;

import com.fasterxml.jackson.databind.Module.SetupContext;
import com.fasterxml.jackson.databind.module.SimpleModule;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.Marker;
import org.apache.logging.log4j.ThreadContext;
import org.apache.logging.log4j.core.LogEvent;
import org.apache.logging.log4j.core.impl.ExtendedStackTraceElement;
import org.apache.logging.log4j.core.impl.ThrowableProxy;

class Initializers {
   Initializers() {
      super();
   }

   static class SimpleModuleInitializer {
      SimpleModuleInitializer() {
         super();
      }

      void initialize(SimpleModule var1) {
         var1.addDeserializer(StackTraceElement.class, new Log4jStackTraceElementDeserializer());
         var1.addDeserializer(ThreadContext.ContextStack.class, new MutableThreadContextStackDeserializer());
      }
   }

   static class SetupContextJsonInitializer {
      SetupContextJsonInitializer() {
         super();
      }

      void setupModule(SetupContext var1, boolean var2) {
         var1.setMixInAnnotations(StackTraceElement.class, StackTraceElementMixIn.class);
         var1.setMixInAnnotations(Marker.class, MarkerMixIn.class);
         var1.setMixInAnnotations(Level.class, LevelMixIn.class);
         var1.setMixInAnnotations(LogEvent.class, LogEventJsonMixIn.class);
         var1.setMixInAnnotations(ExtendedStackTraceElement.class, ExtendedStackTraceElementMixIn.class);
         var1.setMixInAnnotations(ThrowableProxy.class, var2 ? ThrowableProxyMixIn.class : ThrowableProxyWithoutStacktraceMixIn.class);
      }
   }

   static class SetupContextInitializer {
      SetupContextInitializer() {
         super();
      }

      void setupModule(SetupContext var1, boolean var2) {
         var1.setMixInAnnotations(StackTraceElement.class, StackTraceElementMixIn.class);
         var1.setMixInAnnotations(Marker.class, MarkerMixIn.class);
         var1.setMixInAnnotations(Level.class, LevelMixIn.class);
         var1.setMixInAnnotations(LogEvent.class, LogEventWithContextListMixIn.class);
         var1.setMixInAnnotations(ExtendedStackTraceElement.class, ExtendedStackTraceElementMixIn.class);
         var1.setMixInAnnotations(ThrowableProxy.class, var2 ? ThrowableProxyMixIn.class : ThrowableProxyWithoutStacktraceMixIn.class);
      }
   }
}
