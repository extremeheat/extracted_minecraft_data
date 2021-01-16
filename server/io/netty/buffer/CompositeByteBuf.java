package io.netty.buffer;

import io.netty.util.internal.EmptyArrays;
import io.netty.util.internal.ObjectUtil;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.channels.FileChannel;
import java.nio.channels.GatheringByteChannel;
import java.nio.channels.ScatteringByteChannel;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.ConcurrentModificationException;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.NoSuchElementException;

public class CompositeByteBuf extends AbstractReferenceCountedByteBuf implements Iterable<ByteBuf> {
   private static final ByteBuffer EMPTY_NIO_BUFFER;
   private static final Iterator<ByteBuf> EMPTY_ITERATOR;
   private final ByteBufAllocator alloc;
   private final boolean direct;
   private final CompositeByteBuf.ComponentList components;
   private final int maxNumComponents;
   private boolean freed;

   public CompositeByteBuf(ByteBufAllocator var1, boolean var2, int var3) {
      super(2147483647);
      if (var1 == null) {
         throw new NullPointerException("alloc");
      } else {
         this.alloc = var1;
         this.direct = var2;
         this.maxNumComponents = var3;
         this.components = newList(var3);
      }
   }

   public CompositeByteBuf(ByteBufAllocator var1, boolean var2, int var3, ByteBuf... var4) {
      this(var1, var2, var3, var4, 0, var4.length);
   }

   CompositeByteBuf(ByteBufAllocator var1, boolean var2, int var3, ByteBuf[] var4, int var5, int var6) {
      super(2147483647);
      if (var1 == null) {
         throw new NullPointerException("alloc");
      } else if (var3 < 2) {
         throw new IllegalArgumentException("maxNumComponents: " + var3 + " (expected: >= 2)");
      } else {
         this.alloc = var1;
         this.direct = var2;
         this.maxNumComponents = var3;
         this.components = newList(var3);
         this.addComponents0(false, 0, var4, var5, var6);
         this.consolidateIfNeeded();
         this.setIndex(0, this.capacity());
      }
   }

   public CompositeByteBuf(ByteBufAllocator var1, boolean var2, int var3, Iterable<ByteBuf> var4) {
      super(2147483647);
      if (var1 == null) {
         throw new NullPointerException("alloc");
      } else if (var3 < 2) {
         throw new IllegalArgumentException("maxNumComponents: " + var3 + " (expected: >= 2)");
      } else {
         this.alloc = var1;
         this.direct = var2;
         this.maxNumComponents = var3;
         this.components = newList(var3);
         this.addComponents0(false, 0, var4);
         this.consolidateIfNeeded();
         this.setIndex(0, this.capacity());
      }
   }

   private static CompositeByteBuf.ComponentList newList(int var0) {
      return new CompositeByteBuf.ComponentList(Math.min(16, var0));
   }

   CompositeByteBuf(ByteBufAllocator var1) {
      super(2147483647);
      this.alloc = var1;
      this.direct = false;
      this.maxNumComponents = 0;
      this.components = null;
   }

   public CompositeByteBuf addComponent(ByteBuf var1) {
      return this.addComponent(false, var1);
   }

   public CompositeByteBuf addComponents(ByteBuf... var1) {
      return this.addComponents(false, var1);
   }

   public CompositeByteBuf addComponents(Iterable<ByteBuf> var1) {
      return this.addComponents(false, var1);
   }

   public CompositeByteBuf addComponent(int var1, ByteBuf var2) {
      return this.addComponent(false, var1, var2);
   }

   public CompositeByteBuf addComponent(boolean var1, ByteBuf var2) {
      ObjectUtil.checkNotNull(var2, "buffer");
      this.addComponent0(var1, this.components.size(), var2);
      this.consolidateIfNeeded();
      return this;
   }

   public CompositeByteBuf addComponents(boolean var1, ByteBuf... var2) {
      this.addComponents0(var1, this.components.size(), var2, 0, var2.length);
      this.consolidateIfNeeded();
      return this;
   }

   public CompositeByteBuf addComponents(boolean var1, Iterable<ByteBuf> var2) {
      this.addComponents0(var1, this.components.size(), var2);
      this.consolidateIfNeeded();
      return this;
   }

   public CompositeByteBuf addComponent(boolean var1, int var2, ByteBuf var3) {
      ObjectUtil.checkNotNull(var3, "buffer");
      this.addComponent0(var1, var2, var3);
      this.consolidateIfNeeded();
      return this;
   }

   private int addComponent0(boolean var1, int var2, ByteBuf var3) {
      assert var3 != null;

      boolean var4 = false;

      int var11;
      try {
         this.checkComponentIndex(var2);
         int var5 = var3.readableBytes();
         CompositeByteBuf.Component var6 = new CompositeByteBuf.Component(var3.order(ByteOrder.BIG_ENDIAN).slice());
         if (var2 == this.components.size()) {
            var4 = this.components.add(var6);
            if (var2 == 0) {
               var6.endOffset = var5;
            } else {
               CompositeByteBuf.Component var7 = (CompositeByteBuf.Component)this.components.get(var2 - 1);
               var6.offset = var7.endOffset;
               var6.endOffset = var6.offset + var5;
            }
         } else {
            this.components.add(var2, var6);
            var4 = true;
            if (var5 != 0) {
               this.updateComponentOffsets(var2);
            }
         }

         if (var1) {
            this.writerIndex(this.writerIndex() + var3.readableBytes());
         }

         var11 = var2;
      } finally {
         if (!var4) {
            var3.release();
         }

      }

      return var11;
   }

   public CompositeByteBuf addComponents(int var1, ByteBuf... var2) {
      this.addComponents0(false, var1, var2, 0, var2.length);
      this.consolidateIfNeeded();
      return this;
   }

   private int addComponents0(boolean var1, int var2, ByteBuf[] var3, int var4, int var5) {
      ObjectUtil.checkNotNull(var3, "buffers");
      int var6 = var4;
      boolean var16 = false;

      int var20;
      try {
         var16 = true;
         this.checkComponentIndex(var2);

         while(true) {
            if (var6 < var5) {
               ByteBuf var7 = var3[var6++];
               if (var7 != null) {
                  var2 = this.addComponent0(var1, var2, var7) + 1;
                  int var21 = this.components.size();
                  if (var2 > var21) {
                     var2 = var21;
                  }
                  continue;
               }
            }

            var20 = var2;
            var16 = false;
            break;
         }
      } finally {
         if (var16) {
            while(true) {
               if (var6 >= var5) {
                  ;
               } else {
                  ByteBuf var11 = var3[var6];
                  if (var11 != null) {
                     try {
                        var11.release();
                     } catch (Throwable var17) {
                     }
                  }

                  ++var6;
               }
            }
         }
      }

      for(; var6 < var5; ++var6) {
         ByteBuf var8 = var3[var6];
         if (var8 != null) {
            try {
               var8.release();
            } catch (Throwable var18) {
            }
         }
      }

      return var20;
   }

   public CompositeByteBuf addComponents(int var1, Iterable<ByteBuf> var2) {
      this.addComponents0(false, var1, var2);
      this.consolidateIfNeeded();
      return this;
   }

   private int addComponents0(boolean var1, int var2, Iterable<ByteBuf> var3) {
      if (var3 instanceof ByteBuf) {
         return this.addComponent0(var1, var2, (ByteBuf)var3);
      } else {
         ObjectUtil.checkNotNull(var3, "buffers");
         if (!(var3 instanceof Collection)) {
            ArrayList var4 = new ArrayList();
            boolean var15 = false;

            Iterator var5;
            ByteBuf var6;
            try {
               var15 = true;
               var5 = ((Iterable)var3).iterator();

               while(true) {
                  if (!var5.hasNext()) {
                     var3 = var4;
                     var15 = false;
                     break;
                  }

                  var6 = (ByteBuf)var5.next();
                  var4.add(var6);
               }
            } finally {
               if (var15) {
                  if (var3 != var4) {
                     Iterator var9 = ((Iterable)var3).iterator();

                     while(var9.hasNext()) {
                        ByteBuf var10 = (ByteBuf)var9.next();
                        if (var10 != null) {
                           try {
                              var10.release();
                           } catch (Throwable var16) {
                           }
                        }
                     }
                  }

               }
            }

            if (var3 != var4) {
               var5 = ((Iterable)var3).iterator();

               while(var5.hasNext()) {
                  var6 = (ByteBuf)var5.next();
                  if (var6 != null) {
                     try {
                        var6.release();
                     } catch (Throwable var17) {
                     }
                  }
               }
            }
         }

         Collection var19 = (Collection)var3;
         return this.addComponents0(var1, var2, (ByteBuf[])var19.toArray(new ByteBuf[var19.size()]), 0, var19.size());
      }
   }

   private void consolidateIfNeeded() {
      int var1 = this.components.size();
      if (var1 > this.maxNumComponents) {
         int var2 = ((CompositeByteBuf.Component)this.components.get(var1 - 1)).endOffset;
         ByteBuf var3 = this.allocBuffer(var2);

         for(int var4 = 0; var4 < var1; ++var4) {
            CompositeByteBuf.Component var5 = (CompositeByteBuf.Component)this.components.get(var4);
            ByteBuf var6 = var5.buf;
            var3.writeBytes(var6);
            var5.freeIfNecessary();
         }

         CompositeByteBuf.Component var7 = new CompositeByteBuf.Component(var3);
         var7.endOffset = var7.length;
         this.components.clear();
         this.components.add(var7);
      }

   }

   private void checkComponentIndex(int var1) {
      this.ensureAccessible();
      if (var1 < 0 || var1 > this.components.size()) {
         throw new IndexOutOfBoundsException(String.format("cIndex: %d (expected: >= 0 && <= numComponents(%d))", var1, this.components.size()));
      }
   }

   private void checkComponentIndex(int var1, int var2) {
      this.ensureAccessible();
      if (var1 < 0 || var1 + var2 > this.components.size()) {
         throw new IndexOutOfBoundsException(String.format("cIndex: %d, numComponents: %d (expected: cIndex >= 0 && cIndex + numComponents <= totalNumComponents(%d))", var1, var2, this.components.size()));
      }
   }

   private void updateComponentOffsets(int var1) {
      int var2 = this.components.size();
      if (var2 > var1) {
         CompositeByteBuf.Component var3 = (CompositeByteBuf.Component)this.components.get(var1);
         if (var1 == 0) {
            var3.offset = 0;
            var3.endOffset = var3.length;
            ++var1;
         }

         for(int var4 = var1; var4 < var2; ++var4) {
            CompositeByteBuf.Component var5 = (CompositeByteBuf.Component)this.components.get(var4 - 1);
            CompositeByteBuf.Component var6 = (CompositeByteBuf.Component)this.components.get(var4);
            var6.offset = var5.endOffset;
            var6.endOffset = var6.offset + var6.length;
         }

      }
   }

   public CompositeByteBuf removeComponent(int var1) {
      this.checkComponentIndex(var1);
      CompositeByteBuf.Component var2 = (CompositeByteBuf.Component)this.components.remove(var1);
      var2.freeIfNecessary();
      if (var2.length > 0) {
         this.updateComponentOffsets(var1);
      }

      return this;
   }

   public CompositeByteBuf removeComponents(int var1, int var2) {
      this.checkComponentIndex(var1, var2);
      if (var2 == 0) {
         return this;
      } else {
         int var3 = var1 + var2;
         boolean var4 = false;

         for(int var5 = var1; var5 < var3; ++var5) {
            CompositeByteBuf.Component var6 = (CompositeByteBuf.Component)this.components.get(var5);
            if (var6.length > 0) {
               var4 = true;
            }

            var6.freeIfNecessary();
         }

         this.components.removeRange(var1, var3);
         if (var4) {
            this.updateComponentOffsets(var1);
         }

         return this;
      }
   }

   public Iterator<ByteBuf> iterator() {
      this.ensureAccessible();
      return (Iterator)(this.components.isEmpty() ? EMPTY_ITERATOR : new CompositeByteBuf.CompositeByteBufIterator());
   }

   public List<ByteBuf> decompose(int var1, int var2) {
      this.checkIndex(var1, var2);
      if (var2 == 0) {
         return Collections.emptyList();
      } else {
         int var3 = this.toComponentIndex(var1);
         ArrayList var4 = new ArrayList(this.components.size());
         CompositeByteBuf.Component var5 = (CompositeByteBuf.Component)this.components.get(var3);
         ByteBuf var6 = var5.buf.duplicate();
         var6.readerIndex(var1 - var5.offset);
         ByteBuf var7 = var6;
         int var8 = var2;

         int var9;
         do {
            var9 = var7.readableBytes();
            if (var8 <= var9) {
               var7.writerIndex(var7.readerIndex() + var8);
               var4.add(var7);
               break;
            }

            var4.add(var7);
            var8 -= var9;
            ++var3;
            var7 = ((CompositeByteBuf.Component)this.components.get(var3)).buf.duplicate();
         } while(var8 > 0);

         for(var9 = 0; var9 < var4.size(); ++var9) {
            var4.set(var9, ((ByteBuf)var4.get(var9)).slice());
         }

         return var4;
      }
   }

   public boolean isDirect() {
      int var1 = this.components.size();
      if (var1 == 0) {
         return false;
      } else {
         for(int var2 = 0; var2 < var1; ++var2) {
            if (!((CompositeByteBuf.Component)this.components.get(var2)).buf.isDirect()) {
               return false;
            }
         }

         return true;
      }
   }

   public boolean hasArray() {
      switch(this.components.size()) {
      case 0:
         return true;
      case 1:
         return ((CompositeByteBuf.Component)this.components.get(0)).buf.hasArray();
      default:
         return false;
      }
   }

   public byte[] array() {
      switch(this.components.size()) {
      case 0:
         return EmptyArrays.EMPTY_BYTES;
      case 1:
         return ((CompositeByteBuf.Component)this.components.get(0)).buf.array();
      default:
         throw new UnsupportedOperationException();
      }
   }

   public int arrayOffset() {
      switch(this.components.size()) {
      case 0:
         return 0;
      case 1:
         return ((CompositeByteBuf.Component)this.components.get(0)).buf.arrayOffset();
      default:
         throw new UnsupportedOperationException();
      }
   }

   public boolean hasMemoryAddress() {
      switch(this.components.size()) {
      case 0:
         return Unpooled.EMPTY_BUFFER.hasMemoryAddress();
      case 1:
         return ((CompositeByteBuf.Component)this.components.get(0)).buf.hasMemoryAddress();
      default:
         return false;
      }
   }

   public long memoryAddress() {
      switch(this.components.size()) {
      case 0:
         return Unpooled.EMPTY_BUFFER.memoryAddress();
      case 1:
         return ((CompositeByteBuf.Component)this.components.get(0)).buf.memoryAddress();
      default:
         throw new UnsupportedOperationException();
      }
   }

   public int capacity() {
      int var1 = this.components.size();
      return var1 == 0 ? 0 : ((CompositeByteBuf.Component)this.components.get(var1 - 1)).endOffset;
   }

   public CompositeByteBuf capacity(int var1) {
      this.checkNewCapacity(var1);
      int var2 = this.capacity();
      int var3;
      if (var1 > var2) {
         var3 = var1 - var2;
         int var5 = this.components.size();
         ByteBuf var4;
         if (var5 < this.maxNumComponents) {
            var4 = this.allocBuffer(var3);
            var4.setIndex(0, var3);
            this.addComponent0(false, this.components.size(), var4);
         } else {
            var4 = this.allocBuffer(var3);
            var4.setIndex(0, var3);
            this.addComponent0(false, this.components.size(), var4);
            this.consolidateIfNeeded();
         }
      } else if (var1 < var2) {
         var3 = var2 - var1;
         ListIterator var7 = this.components.listIterator(this.components.size());

         while(var7.hasPrevious()) {
            CompositeByteBuf.Component var8 = (CompositeByteBuf.Component)var7.previous();
            if (var3 < var8.length) {
               CompositeByteBuf.Component var6 = new CompositeByteBuf.Component(var8.buf.slice(0, var8.length - var3));
               var6.offset = var8.offset;
               var6.endOffset = var6.offset + var6.length;
               var7.set(var6);
               break;
            }

            var3 -= var8.length;
            var7.remove();
         }

         if (this.readerIndex() > var1) {
            this.setIndex(var1, var1);
         } else if (this.writerIndex() > var1) {
            this.writerIndex(var1);
         }
      }

      return this;
   }

   public ByteBufAllocator alloc() {
      return this.alloc;
   }

   public ByteOrder order() {
      return ByteOrder.BIG_ENDIAN;
   }

   public int numComponents() {
      return this.components.size();
   }

   public int maxNumComponents() {
      return this.maxNumComponents;
   }

   public int toComponentIndex(int var1) {
      this.checkIndex(var1);
      int var2 = 0;
      int var3 = this.components.size();

      while(var2 <= var3) {
         int var4 = var2 + var3 >>> 1;
         CompositeByteBuf.Component var5 = (CompositeByteBuf.Component)this.components.get(var4);
         if (var1 >= var5.endOffset) {
            var2 = var4 + 1;
         } else {
            if (var1 >= var5.offset) {
               return var4;
            }

            var3 = var4 - 1;
         }
      }

      throw new Error("should not reach here");
   }

   public int toByteIndex(int var1) {
      this.checkComponentIndex(var1);
      return ((CompositeByteBuf.Component)this.components.get(var1)).offset;
   }

   public byte getByte(int var1) {
      return this._getByte(var1);
   }

   protected byte _getByte(int var1) {
      CompositeByteBuf.Component var2 = this.findComponent(var1);
      return var2.buf.getByte(var1 - var2.offset);
   }

   protected short _getShort(int var1) {
      CompositeByteBuf.Component var2 = this.findComponent(var1);
      if (var1 + 2 <= var2.endOffset) {
         return var2.buf.getShort(var1 - var2.offset);
      } else {
         return this.order() == ByteOrder.BIG_ENDIAN ? (short)((this._getByte(var1) & 255) << 8 | this._getByte(var1 + 1) & 255) : (short)(this._getByte(var1) & 255 | (this._getByte(var1 + 1) & 255) << 8);
      }
   }

   protected short _getShortLE(int var1) {
      CompositeByteBuf.Component var2 = this.findComponent(var1);
      if (var1 + 2 <= var2.endOffset) {
         return var2.buf.getShortLE(var1 - var2.offset);
      } else {
         return this.order() == ByteOrder.BIG_ENDIAN ? (short)(this._getByte(var1) & 255 | (this._getByte(var1 + 1) & 255) << 8) : (short)((this._getByte(var1) & 255) << 8 | this._getByte(var1 + 1) & 255);
      }
   }

   protected int _getUnsignedMedium(int var1) {
      CompositeByteBuf.Component var2 = this.findComponent(var1);
      if (var1 + 3 <= var2.endOffset) {
         return var2.buf.getUnsignedMedium(var1 - var2.offset);
      } else {
         return this.order() == ByteOrder.BIG_ENDIAN ? (this._getShort(var1) & '\uffff') << 8 | this._getByte(var1 + 2) & 255 : this._getShort(var1) & '\uffff' | (this._getByte(var1 + 2) & 255) << 16;
      }
   }

   protected int _getUnsignedMediumLE(int var1) {
      CompositeByteBuf.Component var2 = this.findComponent(var1);
      if (var1 + 3 <= var2.endOffset) {
         return var2.buf.getUnsignedMediumLE(var1 - var2.offset);
      } else {
         return this.order() == ByteOrder.BIG_ENDIAN ? this._getShortLE(var1) & '\uffff' | (this._getByte(var1 + 2) & 255) << 16 : (this._getShortLE(var1) & '\uffff') << 8 | this._getByte(var1 + 2) & 255;
      }
   }

   protected int _getInt(int var1) {
      CompositeByteBuf.Component var2 = this.findComponent(var1);
      if (var1 + 4 <= var2.endOffset) {
         return var2.buf.getInt(var1 - var2.offset);
      } else {
         return this.order() == ByteOrder.BIG_ENDIAN ? (this._getShort(var1) & '\uffff') << 16 | this._getShort(var1 + 2) & '\uffff' : this._getShort(var1) & '\uffff' | (this._getShort(var1 + 2) & '\uffff') << 16;
      }
   }

   protected int _getIntLE(int var1) {
      CompositeByteBuf.Component var2 = this.findComponent(var1);
      if (var1 + 4 <= var2.endOffset) {
         return var2.buf.getIntLE(var1 - var2.offset);
      } else {
         return this.order() == ByteOrder.BIG_ENDIAN ? this._getShortLE(var1) & '\uffff' | (this._getShortLE(var1 + 2) & '\uffff') << 16 : (this._getShortLE(var1) & '\uffff') << 16 | this._getShortLE(var1 + 2) & '\uffff';
      }
   }

   protected long _getLong(int var1) {
      CompositeByteBuf.Component var2 = this.findComponent(var1);
      if (var1 + 8 <= var2.endOffset) {
         return var2.buf.getLong(var1 - var2.offset);
      } else {
         return this.order() == ByteOrder.BIG_ENDIAN ? ((long)this._getInt(var1) & 4294967295L) << 32 | (long)this._getInt(var1 + 4) & 4294967295L : (long)this._getInt(var1) & 4294967295L | ((long)this._getInt(var1 + 4) & 4294967295L) << 32;
      }
   }

   protected long _getLongLE(int var1) {
      CompositeByteBuf.Component var2 = this.findComponent(var1);
      if (var1 + 8 <= var2.endOffset) {
         return var2.buf.getLongLE(var1 - var2.offset);
      } else {
         return this.order() == ByteOrder.BIG_ENDIAN ? (long)this._getIntLE(var1) & 4294967295L | ((long)this._getIntLE(var1 + 4) & 4294967295L) << 32 : ((long)this._getIntLE(var1) & 4294967295L) << 32 | (long)this._getIntLE(var1 + 4) & 4294967295L;
      }
   }

   public CompositeByteBuf getBytes(int var1, byte[] var2, int var3, int var4) {
      this.checkDstIndex(var1, var4, var3, var2.length);
      if (var4 == 0) {
         return this;
      } else {
         for(int var5 = this.toComponentIndex(var1); var4 > 0; ++var5) {
            CompositeByteBuf.Component var6 = (CompositeByteBuf.Component)this.components.get(var5);
            ByteBuf var7 = var6.buf;
            int var8 = var6.offset;
            int var9 = Math.min(var4, var7.capacity() - (var1 - var8));
            var7.getBytes(var1 - var8, var2, var3, var9);
            var1 += var9;
            var3 += var9;
            var4 -= var9;
         }

         return this;
      }
   }

   public CompositeByteBuf getBytes(int var1, ByteBuffer var2) {
      int var3 = var2.limit();
      int var4 = var2.remaining();
      this.checkIndex(var1, var4);
      if (var4 == 0) {
         return this;
      } else {
         int var5 = this.toComponentIndex(var1);

         try {
            while(var4 > 0) {
               CompositeByteBuf.Component var6 = (CompositeByteBuf.Component)this.components.get(var5);
               ByteBuf var7 = var6.buf;
               int var8 = var6.offset;
               int var9 = Math.min(var4, var7.capacity() - (var1 - var8));
               var2.limit(var2.position() + var9);
               var7.getBytes(var1 - var8, var2);
               var1 += var9;
               var4 -= var9;
               ++var5;
            }
         } finally {
            var2.limit(var3);
         }

         return this;
      }
   }

   public CompositeByteBuf getBytes(int var1, ByteBuf var2, int var3, int var4) {
      this.checkDstIndex(var1, var4, var3, var2.capacity());
      if (var4 == 0) {
         return this;
      } else {
         for(int var5 = this.toComponentIndex(var1); var4 > 0; ++var5) {
            CompositeByteBuf.Component var6 = (CompositeByteBuf.Component)this.components.get(var5);
            ByteBuf var7 = var6.buf;
            int var8 = var6.offset;
            int var9 = Math.min(var4, var7.capacity() - (var1 - var8));
            var7.getBytes(var1 - var8, var2, var3, var9);
            var1 += var9;
            var3 += var9;
            var4 -= var9;
         }

         return this;
      }
   }

   public int getBytes(int var1, GatheringByteChannel var2, int var3) throws IOException {
      int var4 = this.nioBufferCount();
      if (var4 == 1) {
         return var2.write(this.internalNioBuffer(var1, var3));
      } else {
         long var5 = var2.write(this.nioBuffers(var1, var3));
         return var5 > 2147483647L ? 2147483647 : (int)var5;
      }
   }

   public int getBytes(int var1, FileChannel var2, long var3, int var5) throws IOException {
      int var6 = this.nioBufferCount();
      if (var6 == 1) {
         return var2.write(this.internalNioBuffer(var1, var5), var3);
      } else {
         long var7 = 0L;
         ByteBuffer[] var9 = this.nioBuffers(var1, var5);
         int var10 = var9.length;

         for(int var11 = 0; var11 < var10; ++var11) {
            ByteBuffer var12 = var9[var11];
            var7 += (long)var2.write(var12, var3 + var7);
         }

         return var7 > 2147483647L ? 2147483647 : (int)var7;
      }
   }

   public CompositeByteBuf getBytes(int var1, OutputStream var2, int var3) throws IOException {
      this.checkIndex(var1, var3);
      if (var3 == 0) {
         return this;
      } else {
         for(int var4 = this.toComponentIndex(var1); var3 > 0; ++var4) {
            CompositeByteBuf.Component var5 = (CompositeByteBuf.Component)this.components.get(var4);
            ByteBuf var6 = var5.buf;
            int var7 = var5.offset;
            int var8 = Math.min(var3, var6.capacity() - (var1 - var7));
            var6.getBytes(var1 - var7, var2, var8);
            var1 += var8;
            var3 -= var8;
         }

         return this;
      }
   }

   public CompositeByteBuf setByte(int var1, int var2) {
      CompositeByteBuf.Component var3 = this.findComponent(var1);
      var3.buf.setByte(var1 - var3.offset, var2);
      return this;
   }

   protected void _setByte(int var1, int var2) {
      this.setByte(var1, var2);
   }

   public CompositeByteBuf setShort(int var1, int var2) {
      return (CompositeByteBuf)super.setShort(var1, var2);
   }

   protected void _setShort(int var1, int var2) {
      CompositeByteBuf.Component var3 = this.findComponent(var1);
      if (var1 + 2 <= var3.endOffset) {
         var3.buf.setShort(var1 - var3.offset, var2);
      } else if (this.order() == ByteOrder.BIG_ENDIAN) {
         this._setByte(var1, (byte)(var2 >>> 8));
         this._setByte(var1 + 1, (byte)var2);
      } else {
         this._setByte(var1, (byte)var2);
         this._setByte(var1 + 1, (byte)(var2 >>> 8));
      }

   }

   protected void _setShortLE(int var1, int var2) {
      CompositeByteBuf.Component var3 = this.findComponent(var1);
      if (var1 + 2 <= var3.endOffset) {
         var3.buf.setShortLE(var1 - var3.offset, var2);
      } else if (this.order() == ByteOrder.BIG_ENDIAN) {
         this._setByte(var1, (byte)var2);
         this._setByte(var1 + 1, (byte)(var2 >>> 8));
      } else {
         this._setByte(var1, (byte)(var2 >>> 8));
         this._setByte(var1 + 1, (byte)var2);
      }

   }

   public CompositeByteBuf setMedium(int var1, int var2) {
      return (CompositeByteBuf)super.setMedium(var1, var2);
   }

   protected void _setMedium(int var1, int var2) {
      CompositeByteBuf.Component var3 = this.findComponent(var1);
      if (var1 + 3 <= var3.endOffset) {
         var3.buf.setMedium(var1 - var3.offset, var2);
      } else if (this.order() == ByteOrder.BIG_ENDIAN) {
         this._setShort(var1, (short)(var2 >> 8));
         this._setByte(var1 + 2, (byte)var2);
      } else {
         this._setShort(var1, (short)var2);
         this._setByte(var1 + 2, (byte)(var2 >>> 16));
      }

   }

   protected void _setMediumLE(int var1, int var2) {
      CompositeByteBuf.Component var3 = this.findComponent(var1);
      if (var1 + 3 <= var3.endOffset) {
         var3.buf.setMediumLE(var1 - var3.offset, var2);
      } else if (this.order() == ByteOrder.BIG_ENDIAN) {
         this._setShortLE(var1, (short)var2);
         this._setByte(var1 + 2, (byte)(var2 >>> 16));
      } else {
         this._setShortLE(var1, (short)(var2 >> 8));
         this._setByte(var1 + 2, (byte)var2);
      }

   }

   public CompositeByteBuf setInt(int var1, int var2) {
      return (CompositeByteBuf)super.setInt(var1, var2);
   }

   protected void _setInt(int var1, int var2) {
      CompositeByteBuf.Component var3 = this.findComponent(var1);
      if (var1 + 4 <= var3.endOffset) {
         var3.buf.setInt(var1 - var3.offset, var2);
      } else if (this.order() == ByteOrder.BIG_ENDIAN) {
         this._setShort(var1, (short)(var2 >>> 16));
         this._setShort(var1 + 2, (short)var2);
      } else {
         this._setShort(var1, (short)var2);
         this._setShort(var1 + 2, (short)(var2 >>> 16));
      }

   }

   protected void _setIntLE(int var1, int var2) {
      CompositeByteBuf.Component var3 = this.findComponent(var1);
      if (var1 + 4 <= var3.endOffset) {
         var3.buf.setIntLE(var1 - var3.offset, var2);
      } else if (this.order() == ByteOrder.BIG_ENDIAN) {
         this._setShortLE(var1, (short)var2);
         this._setShortLE(var1 + 2, (short)(var2 >>> 16));
      } else {
         this._setShortLE(var1, (short)(var2 >>> 16));
         this._setShortLE(var1 + 2, (short)var2);
      }

   }

   public CompositeByteBuf setLong(int var1, long var2) {
      return (CompositeByteBuf)super.setLong(var1, var2);
   }

   protected void _setLong(int var1, long var2) {
      CompositeByteBuf.Component var4 = this.findComponent(var1);
      if (var1 + 8 <= var4.endOffset) {
         var4.buf.setLong(var1 - var4.offset, var2);
      } else if (this.order() == ByteOrder.BIG_ENDIAN) {
         this._setInt(var1, (int)(var2 >>> 32));
         this._setInt(var1 + 4, (int)var2);
      } else {
         this._setInt(var1, (int)var2);
         this._setInt(var1 + 4, (int)(var2 >>> 32));
      }

   }

   protected void _setLongLE(int var1, long var2) {
      CompositeByteBuf.Component var4 = this.findComponent(var1);
      if (var1 + 8 <= var4.endOffset) {
         var4.buf.setLongLE(var1 - var4.offset, var2);
      } else if (this.order() == ByteOrder.BIG_ENDIAN) {
         this._setIntLE(var1, (int)var2);
         this._setIntLE(var1 + 4, (int)(var2 >>> 32));
      } else {
         this._setIntLE(var1, (int)(var2 >>> 32));
         this._setIntLE(var1 + 4, (int)var2);
      }

   }

   public CompositeByteBuf setBytes(int var1, byte[] var2, int var3, int var4) {
      this.checkSrcIndex(var1, var4, var3, var2.length);
      if (var4 == 0) {
         return this;
      } else {
         for(int var5 = this.toComponentIndex(var1); var4 > 0; ++var5) {
            CompositeByteBuf.Component var6 = (CompositeByteBuf.Component)this.components.get(var5);
            ByteBuf var7 = var6.buf;
            int var8 = var6.offset;
            int var9 = Math.min(var4, var7.capacity() - (var1 - var8));
            var7.setBytes(var1 - var8, var2, var3, var9);
            var1 += var9;
            var3 += var9;
            var4 -= var9;
         }

         return this;
      }
   }

   public CompositeByteBuf setBytes(int var1, ByteBuffer var2) {
      int var3 = var2.limit();
      int var4 = var2.remaining();
      this.checkIndex(var1, var4);
      if (var4 == 0) {
         return this;
      } else {
         int var5 = this.toComponentIndex(var1);

         try {
            while(var4 > 0) {
               CompositeByteBuf.Component var6 = (CompositeByteBuf.Component)this.components.get(var5);
               ByteBuf var7 = var6.buf;
               int var8 = var6.offset;
               int var9 = Math.min(var4, var7.capacity() - (var1 - var8));
               var2.limit(var2.position() + var9);
               var7.setBytes(var1 - var8, var2);
               var1 += var9;
               var4 -= var9;
               ++var5;
            }
         } finally {
            var2.limit(var3);
         }

         return this;
      }
   }

   public CompositeByteBuf setBytes(int var1, ByteBuf var2, int var3, int var4) {
      this.checkSrcIndex(var1, var4, var3, var2.capacity());
      if (var4 == 0) {
         return this;
      } else {
         for(int var5 = this.toComponentIndex(var1); var4 > 0; ++var5) {
            CompositeByteBuf.Component var6 = (CompositeByteBuf.Component)this.components.get(var5);
            ByteBuf var7 = var6.buf;
            int var8 = var6.offset;
            int var9 = Math.min(var4, var7.capacity() - (var1 - var8));
            var7.setBytes(var1 - var8, var2, var3, var9);
            var1 += var9;
            var3 += var9;
            var4 -= var9;
         }

         return this;
      }
   }

   public int setBytes(int var1, InputStream var2, int var3) throws IOException {
      this.checkIndex(var1, var3);
      if (var3 == 0) {
         return var2.read(EmptyArrays.EMPTY_BYTES);
      } else {
         int var4 = this.toComponentIndex(var1);
         int var5 = 0;

         do {
            CompositeByteBuf.Component var6 = (CompositeByteBuf.Component)this.components.get(var4);
            ByteBuf var7 = var6.buf;
            int var8 = var6.offset;
            int var9 = Math.min(var3, var7.capacity() - (var1 - var8));
            if (var9 == 0) {
               ++var4;
            } else {
               int var10 = var7.setBytes(var1 - var8, var2, var9);
               if (var10 < 0) {
                  if (var5 == 0) {
                     return -1;
                  }
                  break;
               }

               if (var10 == var9) {
                  var1 += var9;
                  var3 -= var9;
                  var5 += var9;
                  ++var4;
               } else {
                  var1 += var10;
                  var3 -= var10;
                  var5 += var10;
               }
            }
         } while(var3 > 0);

         return var5;
      }
   }

   public int setBytes(int var1, ScatteringByteChannel var2, int var3) throws IOException {
      this.checkIndex(var1, var3);
      if (var3 == 0) {
         return var2.read(EMPTY_NIO_BUFFER);
      } else {
         int var4 = this.toComponentIndex(var1);
         int var5 = 0;

         do {
            CompositeByteBuf.Component var6 = (CompositeByteBuf.Component)this.components.get(var4);
            ByteBuf var7 = var6.buf;
            int var8 = var6.offset;
            int var9 = Math.min(var3, var7.capacity() - (var1 - var8));
            if (var9 == 0) {
               ++var4;
            } else {
               int var10 = var7.setBytes(var1 - var8, var2, var9);
               if (var10 == 0) {
                  break;
               }

               if (var10 < 0) {
                  if (var5 == 0) {
                     return -1;
                  }
                  break;
               }

               if (var10 == var9) {
                  var1 += var9;
                  var3 -= var9;
                  var5 += var9;
                  ++var4;
               } else {
                  var1 += var10;
                  var3 -= var10;
                  var5 += var10;
               }
            }
         } while(var3 > 0);

         return var5;
      }
   }

   public int setBytes(int var1, FileChannel var2, long var3, int var5) throws IOException {
      this.checkIndex(var1, var5);
      if (var5 == 0) {
         return var2.read(EMPTY_NIO_BUFFER, var3);
      } else {
         int var6 = this.toComponentIndex(var1);
         int var7 = 0;

         do {
            CompositeByteBuf.Component var8 = (CompositeByteBuf.Component)this.components.get(var6);
            ByteBuf var9 = var8.buf;
            int var10 = var8.offset;
            int var11 = Math.min(var5, var9.capacity() - (var1 - var10));
            if (var11 == 0) {
               ++var6;
            } else {
               int var12 = var9.setBytes(var1 - var10, var2, var3 + (long)var7, var11);
               if (var12 == 0) {
                  break;
               }

               if (var12 < 0) {
                  if (var7 == 0) {
                     return -1;
                  }
                  break;
               }

               if (var12 == var11) {
                  var1 += var11;
                  var5 -= var11;
                  var7 += var11;
                  ++var6;
               } else {
                  var1 += var12;
                  var5 -= var12;
                  var7 += var12;
               }
            }
         } while(var5 > 0);

         return var7;
      }
   }

   public ByteBuf copy(int var1, int var2) {
      this.checkIndex(var1, var2);
      ByteBuf var3 = this.allocBuffer(var2);
      if (var2 != 0) {
         this.copyTo(var1, var2, this.toComponentIndex(var1), var3);
      }

      return var3;
   }

   private void copyTo(int var1, int var2, int var3, ByteBuf var4) {
      int var5 = 0;

      for(int var6 = var3; var2 > 0; ++var6) {
         CompositeByteBuf.Component var7 = (CompositeByteBuf.Component)this.components.get(var6);
         ByteBuf var8 = var7.buf;
         int var9 = var7.offset;
         int var10 = Math.min(var2, var8.capacity() - (var1 - var9));
         var8.getBytes(var1 - var9, var4, var5, var10);
         var1 += var10;
         var5 += var10;
         var2 -= var10;
      }

      var4.writerIndex(var4.capacity());
   }

   public ByteBuf component(int var1) {
      return this.internalComponent(var1).duplicate();
   }

   public ByteBuf componentAtOffset(int var1) {
      return this.internalComponentAtOffset(var1).duplicate();
   }

   public ByteBuf internalComponent(int var1) {
      this.checkComponentIndex(var1);
      return ((CompositeByteBuf.Component)this.components.get(var1)).buf;
   }

   public ByteBuf internalComponentAtOffset(int var1) {
      return this.findComponent(var1).buf;
   }

   private CompositeByteBuf.Component findComponent(int var1) {
      this.checkIndex(var1);
      int var2 = 0;
      int var3 = this.components.size();

      while(var2 <= var3) {
         int var4 = var2 + var3 >>> 1;
         CompositeByteBuf.Component var5 = (CompositeByteBuf.Component)this.components.get(var4);
         if (var1 >= var5.endOffset) {
            var2 = var4 + 1;
         } else {
            if (var1 >= var5.offset) {
               assert var5.length != 0;

               return var5;
            }

            var3 = var4 - 1;
         }
      }

      throw new Error("should not reach here");
   }

   public int nioBufferCount() {
      switch(this.components.size()) {
      case 0:
         return 1;
      case 1:
         return ((CompositeByteBuf.Component)this.components.get(0)).buf.nioBufferCount();
      default:
         int var1 = 0;
         int var2 = this.components.size();

         for(int var3 = 0; var3 < var2; ++var3) {
            CompositeByteBuf.Component var4 = (CompositeByteBuf.Component)this.components.get(var3);
            var1 += var4.buf.nioBufferCount();
         }

         return var1;
      }
   }

   public ByteBuffer internalNioBuffer(int var1, int var2) {
      switch(this.components.size()) {
      case 0:
         return EMPTY_NIO_BUFFER;
      case 1:
         return ((CompositeByteBuf.Component)this.components.get(0)).buf.internalNioBuffer(var1, var2);
      default:
         throw new UnsupportedOperationException();
      }
   }

   public ByteBuffer nioBuffer(int var1, int var2) {
      this.checkIndex(var1, var2);
      switch(this.components.size()) {
      case 0:
         return EMPTY_NIO_BUFFER;
      case 1:
         ByteBuf var3 = ((CompositeByteBuf.Component)this.components.get(0)).buf;
         if (var3.nioBufferCount() == 1) {
            return ((CompositeByteBuf.Component)this.components.get(0)).buf.nioBuffer(var1, var2);
         }
      default:
         ByteBuffer var9 = ByteBuffer.allocate(var2).order(this.order());
         ByteBuffer[] var4 = this.nioBuffers(var1, var2);
         ByteBuffer[] var5 = var4;
         int var6 = var4.length;

         for(int var7 = 0; var7 < var6; ++var7) {
            ByteBuffer var8 = var5[var7];
            var9.put(var8);
         }

         var9.flip();
         return var9;
      }
   }

   public ByteBuffer[] nioBuffers(int var1, int var2) {
      this.checkIndex(var1, var2);
      if (var2 == 0) {
         return new ByteBuffer[]{EMPTY_NIO_BUFFER};
      } else {
         ArrayList var3 = new ArrayList(this.components.size());

         for(int var4 = this.toComponentIndex(var1); var2 > 0; ++var4) {
            CompositeByteBuf.Component var5 = (CompositeByteBuf.Component)this.components.get(var4);
            ByteBuf var6 = var5.buf;
            int var7 = var5.offset;
            int var8 = Math.min(var2, var6.capacity() - (var1 - var7));
            switch(var6.nioBufferCount()) {
            case 0:
               throw new UnsupportedOperationException();
            case 1:
               var3.add(var6.nioBuffer(var1 - var7, var8));
               break;
            default:
               Collections.addAll(var3, var6.nioBuffers(var1 - var7, var8));
            }

            var1 += var8;
            var2 -= var8;
         }

         return (ByteBuffer[])var3.toArray(new ByteBuffer[var3.size()]);
      }
   }

   public CompositeByteBuf consolidate() {
      this.ensureAccessible();
      int var1 = this.numComponents();
      if (var1 <= 1) {
         return this;
      } else {
         CompositeByteBuf.Component var2 = (CompositeByteBuf.Component)this.components.get(var1 - 1);
         int var3 = var2.endOffset;
         ByteBuf var4 = this.allocBuffer(var3);

         for(int var5 = 0; var5 < var1; ++var5) {
            CompositeByteBuf.Component var6 = (CompositeByteBuf.Component)this.components.get(var5);
            ByteBuf var7 = var6.buf;
            var4.writeBytes(var7);
            var6.freeIfNecessary();
         }

         this.components.clear();
         this.components.add(new CompositeByteBuf.Component(var4));
         this.updateComponentOffsets(0);
         return this;
      }
   }

   public CompositeByteBuf consolidate(int var1, int var2) {
      this.checkComponentIndex(var1, var2);
      if (var2 <= 1) {
         return this;
      } else {
         int var3 = var1 + var2;
         CompositeByteBuf.Component var4 = (CompositeByteBuf.Component)this.components.get(var3 - 1);
         int var5 = var4.endOffset - ((CompositeByteBuf.Component)this.components.get(var1)).offset;
         ByteBuf var6 = this.allocBuffer(var5);

         for(int var7 = var1; var7 < var3; ++var7) {
            CompositeByteBuf.Component var8 = (CompositeByteBuf.Component)this.components.get(var7);
            ByteBuf var9 = var8.buf;
            var6.writeBytes(var9);
            var8.freeIfNecessary();
         }

         this.components.removeRange(var1 + 1, var3);
         this.components.set(var1, new CompositeByteBuf.Component(var6));
         this.updateComponentOffsets(var1);
         return this;
      }
   }

   public CompositeByteBuf discardReadComponents() {
      this.ensureAccessible();
      int var1 = this.readerIndex();
      if (var1 == 0) {
         return this;
      } else {
         int var2 = this.writerIndex();
         int var3;
         int var4;
         if (var1 == var2 && var2 == this.capacity()) {
            var3 = this.components.size();

            for(var4 = 0; var4 < var3; ++var4) {
               ((CompositeByteBuf.Component)this.components.get(var4)).freeIfNecessary();
            }

            this.components.clear();
            this.setIndex(0, 0);
            this.adjustMarkers(var1);
            return this;
         } else {
            var3 = this.toComponentIndex(var1);

            for(var4 = 0; var4 < var3; ++var4) {
               ((CompositeByteBuf.Component)this.components.get(var4)).freeIfNecessary();
            }

            this.components.removeRange(0, var3);
            CompositeByteBuf.Component var6 = (CompositeByteBuf.Component)this.components.get(0);
            int var5 = var6.offset;
            this.updateComponentOffsets(0);
            this.setIndex(var1 - var5, var2 - var5);
            this.adjustMarkers(var5);
            return this;
         }
      }
   }

   public CompositeByteBuf discardReadBytes() {
      this.ensureAccessible();
      int var1 = this.readerIndex();
      if (var1 == 0) {
         return this;
      } else {
         int var2 = this.writerIndex();
         int var3;
         int var4;
         if (var1 == var2 && var2 == this.capacity()) {
            var3 = this.components.size();

            for(var4 = 0; var4 < var3; ++var4) {
               ((CompositeByteBuf.Component)this.components.get(var4)).freeIfNecessary();
            }

            this.components.clear();
            this.setIndex(0, 0);
            this.adjustMarkers(var1);
            return this;
         } else {
            var3 = this.toComponentIndex(var1);

            for(var4 = 0; var4 < var3; ++var4) {
               ((CompositeByteBuf.Component)this.components.get(var4)).freeIfNecessary();
            }

            CompositeByteBuf.Component var7 = (CompositeByteBuf.Component)this.components.get(var3);
            int var5 = var1 - var7.offset;
            if (var5 == var7.length) {
               ++var3;
            } else {
               CompositeByteBuf.Component var6 = new CompositeByteBuf.Component(var7.buf.slice(var5, var7.length - var5));
               this.components.set(var3, var6);
            }

            this.components.removeRange(0, var3);
            this.updateComponentOffsets(0);
            this.setIndex(0, var2 - var1);
            this.adjustMarkers(var1);
            return this;
         }
      }
   }

   private ByteBuf allocBuffer(int var1) {
      return this.direct ? this.alloc().directBuffer(var1) : this.alloc().heapBuffer(var1);
   }

   public String toString() {
      String var1 = super.toString();
      var1 = var1.substring(0, var1.length() - 1);
      return var1 + ", components=" + this.components.size() + ')';
   }

   public CompositeByteBuf readerIndex(int var1) {
      return (CompositeByteBuf)super.readerIndex(var1);
   }

   public CompositeByteBuf writerIndex(int var1) {
      return (CompositeByteBuf)super.writerIndex(var1);
   }

   public CompositeByteBuf setIndex(int var1, int var2) {
      return (CompositeByteBuf)super.setIndex(var1, var2);
   }

   public CompositeByteBuf clear() {
      return (CompositeByteBuf)super.clear();
   }

   public CompositeByteBuf markReaderIndex() {
      return (CompositeByteBuf)super.markReaderIndex();
   }

   public CompositeByteBuf resetReaderIndex() {
      return (CompositeByteBuf)super.resetReaderIndex();
   }

   public CompositeByteBuf markWriterIndex() {
      return (CompositeByteBuf)super.markWriterIndex();
   }

   public CompositeByteBuf resetWriterIndex() {
      return (CompositeByteBuf)super.resetWriterIndex();
   }

   public CompositeByteBuf ensureWritable(int var1) {
      return (CompositeByteBuf)super.ensureWritable(var1);
   }

   public CompositeByteBuf getBytes(int var1, ByteBuf var2) {
      return (CompositeByteBuf)super.getBytes(var1, var2);
   }

   public CompositeByteBuf getBytes(int var1, ByteBuf var2, int var3) {
      return (CompositeByteBuf)super.getBytes(var1, var2, var3);
   }

   public CompositeByteBuf getBytes(int var1, byte[] var2) {
      return (CompositeByteBuf)super.getBytes(var1, var2);
   }

   public CompositeByteBuf setBoolean(int var1, boolean var2) {
      return (CompositeByteBuf)super.setBoolean(var1, var2);
   }

   public CompositeByteBuf setChar(int var1, int var2) {
      return (CompositeByteBuf)super.setChar(var1, var2);
   }

   public CompositeByteBuf setFloat(int var1, float var2) {
      return (CompositeByteBuf)super.setFloat(var1, var2);
   }

   public CompositeByteBuf setDouble(int var1, double var2) {
      return (CompositeByteBuf)super.setDouble(var1, var2);
   }

   public CompositeByteBuf setBytes(int var1, ByteBuf var2) {
      return (CompositeByteBuf)super.setBytes(var1, var2);
   }

   public CompositeByteBuf setBytes(int var1, ByteBuf var2, int var3) {
      return (CompositeByteBuf)super.setBytes(var1, var2, var3);
   }

   public CompositeByteBuf setBytes(int var1, byte[] var2) {
      return (CompositeByteBuf)super.setBytes(var1, var2);
   }

   public CompositeByteBuf setZero(int var1, int var2) {
      return (CompositeByteBuf)super.setZero(var1, var2);
   }

   public CompositeByteBuf readBytes(ByteBuf var1) {
      return (CompositeByteBuf)super.readBytes(var1);
   }

   public CompositeByteBuf readBytes(ByteBuf var1, int var2) {
      return (CompositeByteBuf)super.readBytes(var1, var2);
   }

   public CompositeByteBuf readBytes(ByteBuf var1, int var2, int var3) {
      return (CompositeByteBuf)super.readBytes(var1, var2, var3);
   }

   public CompositeByteBuf readBytes(byte[] var1) {
      return (CompositeByteBuf)super.readBytes(var1);
   }

   public CompositeByteBuf readBytes(byte[] var1, int var2, int var3) {
      return (CompositeByteBuf)super.readBytes(var1, var2, var3);
   }

   public CompositeByteBuf readBytes(ByteBuffer var1) {
      return (CompositeByteBuf)super.readBytes(var1);
   }

   public CompositeByteBuf readBytes(OutputStream var1, int var2) throws IOException {
      return (CompositeByteBuf)super.readBytes(var1, var2);
   }

   public CompositeByteBuf skipBytes(int var1) {
      return (CompositeByteBuf)super.skipBytes(var1);
   }

   public CompositeByteBuf writeBoolean(boolean var1) {
      return (CompositeByteBuf)super.writeBoolean(var1);
   }

   public CompositeByteBuf writeByte(int var1) {
      return (CompositeByteBuf)super.writeByte(var1);
   }

   public CompositeByteBuf writeShort(int var1) {
      return (CompositeByteBuf)super.writeShort(var1);
   }

   public CompositeByteBuf writeMedium(int var1) {
      return (CompositeByteBuf)super.writeMedium(var1);
   }

   public CompositeByteBuf writeInt(int var1) {
      return (CompositeByteBuf)super.writeInt(var1);
   }

   public CompositeByteBuf writeLong(long var1) {
      return (CompositeByteBuf)super.writeLong(var1);
   }

   public CompositeByteBuf writeChar(int var1) {
      return (CompositeByteBuf)super.writeChar(var1);
   }

   public CompositeByteBuf writeFloat(float var1) {
      return (CompositeByteBuf)super.writeFloat(var1);
   }

   public CompositeByteBuf writeDouble(double var1) {
      return (CompositeByteBuf)super.writeDouble(var1);
   }

   public CompositeByteBuf writeBytes(ByteBuf var1) {
      return (CompositeByteBuf)super.writeBytes(var1);
   }

   public CompositeByteBuf writeBytes(ByteBuf var1, int var2) {
      return (CompositeByteBuf)super.writeBytes(var1, var2);
   }

   public CompositeByteBuf writeBytes(ByteBuf var1, int var2, int var3) {
      return (CompositeByteBuf)super.writeBytes(var1, var2, var3);
   }

   public CompositeByteBuf writeBytes(byte[] var1) {
      return (CompositeByteBuf)super.writeBytes(var1);
   }

   public CompositeByteBuf writeBytes(byte[] var1, int var2, int var3) {
      return (CompositeByteBuf)super.writeBytes(var1, var2, var3);
   }

   public CompositeByteBuf writeBytes(ByteBuffer var1) {
      return (CompositeByteBuf)super.writeBytes(var1);
   }

   public CompositeByteBuf writeZero(int var1) {
      return (CompositeByteBuf)super.writeZero(var1);
   }

   public CompositeByteBuf retain(int var1) {
      return (CompositeByteBuf)super.retain(var1);
   }

   public CompositeByteBuf retain() {
      return (CompositeByteBuf)super.retain();
   }

   public CompositeByteBuf touch() {
      return this;
   }

   public CompositeByteBuf touch(Object var1) {
      return this;
   }

   public ByteBuffer[] nioBuffers() {
      return this.nioBuffers(this.readerIndex(), this.readableBytes());
   }

   public CompositeByteBuf discardSomeReadBytes() {
      return this.discardReadComponents();
   }

   protected void deallocate() {
      if (!this.freed) {
         this.freed = true;
         int var1 = this.components.size();

         for(int var2 = 0; var2 < var1; ++var2) {
            ((CompositeByteBuf.Component)this.components.get(var2)).freeIfNecessary();
         }

      }
   }

   public ByteBuf unwrap() {
      return null;
   }

   static {
      EMPTY_NIO_BUFFER = Unpooled.EMPTY_BUFFER.nioBuffer();
      EMPTY_ITERATOR = Collections.emptyList().iterator();
   }

   private static final class ComponentList extends ArrayList<CompositeByteBuf.Component> {
      ComponentList(int var1) {
         super(var1);
      }

      public void removeRange(int var1, int var2) {
         super.removeRange(var1, var2);
      }
   }

   private final class CompositeByteBufIterator implements Iterator<ByteBuf> {
      private final int size;
      private int index;

      private CompositeByteBufIterator() {
         super();
         this.size = CompositeByteBuf.this.components.size();
      }

      public boolean hasNext() {
         return this.size > this.index;
      }

      public ByteBuf next() {
         if (this.size != CompositeByteBuf.this.components.size()) {
            throw new ConcurrentModificationException();
         } else if (!this.hasNext()) {
            throw new NoSuchElementException();
         } else {
            try {
               return ((CompositeByteBuf.Component)CompositeByteBuf.this.components.get(this.index++)).buf;
            } catch (IndexOutOfBoundsException var2) {
               throw new ConcurrentModificationException();
            }
         }
      }

      public void remove() {
         throw new UnsupportedOperationException("Read-Only");
      }

      // $FF: synthetic method
      CompositeByteBufIterator(Object var2) {
         this();
      }
   }

   private static final class Component {
      final ByteBuf buf;
      final int length;
      int offset;
      int endOffset;

      Component(ByteBuf var1) {
         super();
         this.buf = var1;
         this.length = var1.readableBytes();
      }

      void freeIfNecessary() {
         this.buf.release();
      }
   }
}
