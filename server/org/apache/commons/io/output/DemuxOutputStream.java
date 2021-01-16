package org.apache.commons.io.output;

import java.io.IOException;
import java.io.OutputStream;

public class DemuxOutputStream extends OutputStream {
   private final InheritableThreadLocal<OutputStream> outputStreamThreadLocal = new InheritableThreadLocal();

   public DemuxOutputStream() {
      super();
   }

   public OutputStream bindStream(OutputStream var1) {
      OutputStream var2 = (OutputStream)this.outputStreamThreadLocal.get();
      this.outputStreamThreadLocal.set(var1);
      return var2;
   }

   public void close() throws IOException {
      OutputStream var1 = (OutputStream)this.outputStreamThreadLocal.get();
      if (null != var1) {
         var1.close();
      }

   }

   public void flush() throws IOException {
      OutputStream var1 = (OutputStream)this.outputStreamThreadLocal.get();
      if (null != var1) {
         var1.flush();
      }

   }

   public void write(int var1) throws IOException {
      OutputStream var2 = (OutputStream)this.outputStreamThreadLocal.get();
      if (null != var2) {
         var2.write(var1);
      }

   }
}
