package it.unimi.dsi.fastutil.io;

public class FastByteArrayInputStream extends MeasurableInputStream implements RepositionableStream {
   public byte[] array;
   public int offset;
   public int length;
   private int position;
   private int mark;

   public FastByteArrayInputStream(byte[] var1, int var2, int var3) {
      super();
      this.array = var1;
      this.offset = var2;
      this.length = var3;
   }

   public FastByteArrayInputStream(byte[] var1) {
      this(var1, 0, var1.length);
   }

   public boolean markSupported() {
      return true;
   }

   public void reset() {
      this.position = this.mark;
   }

   public void close() {
   }

   public void mark(int var1) {
      this.mark = this.position;
   }

   public int available() {
      return this.length - this.position;
   }

   public long skip(long var1) {
      if (var1 <= (long)(this.length - this.position)) {
         this.position += (int)var1;
         return var1;
      } else {
         var1 = (long)(this.length - this.position);
         this.position = this.length;
         return var1;
      }
   }

   public int read() {
      return this.length == this.position ? -1 : this.array[this.offset + this.position++] & 255;
   }

   public int read(byte[] var1, int var2, int var3) {
      if (this.length == this.position) {
         return var3 == 0 ? 0 : -1;
      } else {
         int var4 = Math.min(var3, this.length - this.position);
         System.arraycopy(this.array, this.offset + this.position, var1, var2, var4);
         this.position += var4;
         return var4;
      }
   }

   public long position() {
      return (long)this.position;
   }

   public void position(long var1) {
      this.position = (int)Math.min(var1, (long)this.length);
   }

   public long length() {
      return (long)this.length;
   }
}
