package org.apache.commons.io.input;

import java.io.IOException;
import java.io.InputStream;

public class WindowsLineEndingInputStream extends InputStream {
   private boolean slashRSeen = false;
   private boolean slashNSeen = false;
   private boolean injectSlashN = false;
   private boolean eofSeen = false;
   private final InputStream target;
   private final boolean ensureLineFeedAtEndOfFile;

   public WindowsLineEndingInputStream(InputStream var1, boolean var2) {
      super();
      this.target = var1;
      this.ensureLineFeedAtEndOfFile = var2;
   }

   private int readWithUpdate() throws IOException {
      int var1 = this.target.read();
      this.eofSeen = var1 == -1;
      if (this.eofSeen) {
         return var1;
      } else {
         this.slashRSeen = var1 == 13;
         this.slashNSeen = var1 == 10;
         return var1;
      }
   }

   public int read() throws IOException {
      if (this.eofSeen) {
         return this.eofGame();
      } else if (this.injectSlashN) {
         this.injectSlashN = false;
         return 10;
      } else {
         boolean var1 = this.slashRSeen;
         int var2 = this.readWithUpdate();
         if (this.eofSeen) {
            return this.eofGame();
         } else if (var2 == 10 && !var1) {
            this.injectSlashN = true;
            return 13;
         } else {
            return var2;
         }
      }
   }

   private int eofGame() {
      if (!this.ensureLineFeedAtEndOfFile) {
         return -1;
      } else if (!this.slashNSeen && !this.slashRSeen) {
         this.slashRSeen = true;
         return 13;
      } else if (!this.slashNSeen) {
         this.slashRSeen = false;
         this.slashNSeen = true;
         return 10;
      } else {
         return -1;
      }
   }

   public void close() throws IOException {
      super.close();
      this.target.close();
   }

   public synchronized void mark(int var1) {
      throw new UnsupportedOperationException("Mark not supported");
   }
}
