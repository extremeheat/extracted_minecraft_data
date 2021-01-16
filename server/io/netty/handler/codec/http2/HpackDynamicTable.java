package io.netty.handler.codec.http2;

final class HpackDynamicTable {
   HpackHeaderField[] hpackHeaderFields;
   int head;
   int tail;
   private long size;
   private long capacity = -1L;

   HpackDynamicTable(long var1) {
      super();
      this.setCapacity(var1);
   }

   public int length() {
      int var1;
      if (this.head < this.tail) {
         var1 = this.hpackHeaderFields.length - this.tail + this.head;
      } else {
         var1 = this.head - this.tail;
      }

      return var1;
   }

   public long size() {
      return this.size;
   }

   public long capacity() {
      return this.capacity;
   }

   public HpackHeaderField getEntry(int var1) {
      if (var1 > 0 && var1 <= this.length()) {
         int var2 = this.head - var1;
         return var2 < 0 ? this.hpackHeaderFields[var2 + this.hpackHeaderFields.length] : this.hpackHeaderFields[var2];
      } else {
         throw new IndexOutOfBoundsException();
      }
   }

   public void add(HpackHeaderField var1) {
      int var2 = var1.size();
      if ((long)var2 > this.capacity) {
         this.clear();
      } else {
         while(this.capacity - this.size < (long)var2) {
            this.remove();
         }

         this.hpackHeaderFields[this.head++] = var1;
         this.size += (long)var1.size();
         if (this.head == this.hpackHeaderFields.length) {
            this.head = 0;
         }

      }
   }

   public HpackHeaderField remove() {
      HpackHeaderField var1 = this.hpackHeaderFields[this.tail];
      if (var1 == null) {
         return null;
      } else {
         this.size -= (long)var1.size();
         this.hpackHeaderFields[this.tail++] = null;
         if (this.tail == this.hpackHeaderFields.length) {
            this.tail = 0;
         }

         return var1;
      }
   }

   public void clear() {
      while(this.tail != this.head) {
         this.hpackHeaderFields[this.tail++] = null;
         if (this.tail == this.hpackHeaderFields.length) {
            this.tail = 0;
         }
      }

      this.head = 0;
      this.tail = 0;
      this.size = 0L;
   }

   public void setCapacity(long var1) {
      if (var1 >= 0L && var1 <= 4294967295L) {
         if (this.capacity != var1) {
            this.capacity = var1;
            if (var1 == 0L) {
               this.clear();
            } else {
               while(this.size > var1) {
                  this.remove();
               }
            }

            int var3 = (int)(var1 / 32L);
            if (var1 % 32L != 0L) {
               ++var3;
            }

            if (this.hpackHeaderFields == null || this.hpackHeaderFields.length != var3) {
               HpackHeaderField[] var4 = new HpackHeaderField[var3];
               int var5 = this.length();
               int var6 = this.tail;

               for(int var7 = 0; var7 < var5; ++var7) {
                  HpackHeaderField var8 = this.hpackHeaderFields[var6++];
                  var4[var7] = var8;
                  if (var6 == this.hpackHeaderFields.length) {
                     var6 = 0;
                  }
               }

               this.tail = 0;
               this.head = this.tail + var5;
               this.hpackHeaderFields = var4;
            }
         }
      } else {
         throw new IllegalArgumentException("capacity is invalid: " + var1);
      }
   }
}
