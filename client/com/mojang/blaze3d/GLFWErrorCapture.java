package com.mojang.blaze3d;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import org.jspecify.annotations.Nullable;
import org.lwjgl.glfw.GLFWErrorCallbackI;
import org.lwjgl.system.MemoryUtil;

public class GLFWErrorCapture implements GLFWErrorCallbackI, Iterable<Error> {
   private @Nullable List<Error> errors;

   public GLFWErrorCapture() {
      super();
   }

   public void invoke(final int error, final long description) {
      if (this.errors == null) {
         this.errors = new ArrayList();
      }

      this.errors.add(new Error(error, MemoryUtil.memUTF8(description)));
   }

   public Iterator<Error> iterator() {
      return this.errors == null ? Collections.emptyIterator() : this.errors.iterator();
   }

   public @Nullable Error firstError() {
      return this.errors == null ? null : (Error)this.errors.getFirst();
   }

   public static record Error(int error, String description) {
      public Error {
         super();
      }

      public String toString() {
         return String.format(Locale.ROOT, "[GLFW 0x%X] %s", this.error, this.description);
      }
   }
}
