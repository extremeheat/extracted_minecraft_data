package net.minecraft.util.parsing.packrat;

import com.google.common.annotations.VisibleForTesting;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import net.minecraft.Util;
import org.jspecify.annotations.Nullable;

public final class Scope {
   private static final int NOT_FOUND = -1;
   private static final Object FRAME_START_MARKER = new Object() {
      public String toString() {
         return "frame";
      }
   };
   private static final int ENTRY_STRIDE = 2;
   private @Nullable Object[] stack = new Object[128];
   private int topEntryKeyIndex = 0;
   private int topMarkerKeyIndex = 0;

   public Scope() {
      super();
      this.stack[0] = FRAME_START_MARKER;
      this.stack[1] = null;
   }

   private int valueIndex(Atom<?> var1) {
      for(int var2 = this.topEntryKeyIndex; var2 > this.topMarkerKeyIndex; var2 -= 2) {
         Object var3 = this.stack[var2];

         assert var3 instanceof Atom;

         if (var3 == var1) {
            return var2 + 1;
         }
      }

      return -1;
   }

   public int valueIndexForAny(Atom<?>... var1) {
      for(int var2 = this.topEntryKeyIndex; var2 > this.topMarkerKeyIndex; var2 -= 2) {
         Object var3 = this.stack[var2];

         assert var3 instanceof Atom;

         for(Atom var7 : var1) {
            if (var7 == var3) {
               return var2 + 1;
            }
         }
      }

      return -1;
   }

   private void ensureCapacity(int var1) {
      int var2 = this.stack.length;
      int var3 = this.topEntryKeyIndex + 1;
      int var4 = var3 + var1 * 2;
      if (var4 >= var2) {
         int var5 = Util.growByHalf(var2, var4 + 1);
         Object[] var6 = new Object[var5];
         System.arraycopy(this.stack, 0, var6, 0, var2);
         this.stack = var6;
      }

      assert this.validateStructure();

   }

   private void setupNewFrame() {
      this.topEntryKeyIndex += 2;
      this.stack[this.topEntryKeyIndex] = FRAME_START_MARKER;
      this.stack[this.topEntryKeyIndex + 1] = this.topMarkerKeyIndex;
      this.topMarkerKeyIndex = this.topEntryKeyIndex;
   }

   public void pushFrame() {
      this.ensureCapacity(1);
      this.setupNewFrame();

      assert this.validateStructure();

   }

   private int getPreviousMarkerIndex(int var1) {
      return (Integer)this.stack[var1 + 1];
   }

   public void popFrame() {
      assert this.topMarkerKeyIndex != 0;

      this.topEntryKeyIndex = this.topMarkerKeyIndex - 2;
      this.topMarkerKeyIndex = this.getPreviousMarkerIndex(this.topMarkerKeyIndex);

      assert this.validateStructure();

   }

   public void splitFrame() {
      int var1 = this.topMarkerKeyIndex;
      int var2 = (this.topEntryKeyIndex - this.topMarkerKeyIndex) / 2;
      this.ensureCapacity(var2 + 1);
      this.setupNewFrame();
      int var3 = var1 + 2;
      int var4 = this.topEntryKeyIndex;

      for(int var5 = 0; var5 < var2; ++var5) {
         var4 += 2;
         Object var6 = this.stack[var3];

         assert var6 != null;

         this.stack[var4] = var6;
         this.stack[var4 + 1] = null;
         var3 += 2;
      }

      this.topEntryKeyIndex = var4;

      assert this.validateStructure();

   }

   public void clearFrameValues() {
      for(int var1 = this.topEntryKeyIndex; var1 > this.topMarkerKeyIndex; var1 -= 2) {
         assert this.stack[var1] instanceof Atom;

         this.stack[var1 + 1] = null;
      }

      assert this.validateStructure();

   }

   public void mergeFrame() {
      int var1 = this.getPreviousMarkerIndex(this.topMarkerKeyIndex);
      int var2 = var1;
      int var3 = this.topMarkerKeyIndex;

      while(var3 < this.topEntryKeyIndex) {
         var2 += 2;
         var3 += 2;
         Object var4 = this.stack[var3];

         assert var4 instanceof Atom;

         Object var5 = this.stack[var3 + 1];
         Object var6 = this.stack[var2];
         if (var6 != var4) {
            this.stack[var2] = var4;
            this.stack[var2 + 1] = var5;
         } else if (var5 != null) {
            this.stack[var2 + 1] = var5;
         }
      }

      this.topEntryKeyIndex = var2;
      this.topMarkerKeyIndex = var1;

      assert this.validateStructure();

   }

   public <T> void put(Atom<T> var1, @Nullable T var2) {
      int var3 = this.valueIndex(var1);
      if (var3 != -1) {
         this.stack[var3] = var2;
      } else {
         this.ensureCapacity(1);
         this.topEntryKeyIndex += 2;
         this.stack[this.topEntryKeyIndex] = var1;
         this.stack[this.topEntryKeyIndex + 1] = var2;
      }

      assert this.validateStructure();

   }

   public <T> @Nullable T get(Atom<T> var1) {
      int var2 = this.valueIndex(var1);
      return (T)(var2 != -1 ? this.stack[var2] : null);
   }

   public <T> T getOrThrow(Atom<T> var1) {
      int var2 = this.valueIndex(var1);
      if (var2 == -1) {
         throw new IllegalArgumentException("No value for atom " + String.valueOf(var1));
      } else {
         return (T)this.stack[var2];
      }
   }

   public <T> T getOrDefault(Atom<T> var1, T var2) {
      int var3 = this.valueIndex(var1);
      return var3 != -1 ? this.stack[var3] : var2;
   }

   @SafeVarargs
   public final <T> @Nullable T getAny(Atom<? extends T>... var1) {
      int var2 = this.valueIndexForAny(var1);
      return (T)(var2 != -1 ? this.stack[var2] : null);
   }

   @SafeVarargs
   public final <T> T getAnyOrThrow(Atom<? extends T>... var1) {
      int var2 = this.valueIndexForAny(var1);
      if (var2 == -1) {
         throw new IllegalArgumentException("No value for atoms " + Arrays.toString(var1));
      } else {
         return (T)this.stack[var2];
      }
   }

   public String toString() {
      StringBuilder var1 = new StringBuilder();
      boolean var2 = true;

      for(int var3 = 0; var3 <= this.topEntryKeyIndex; var3 += 2) {
         Object var4 = this.stack[var3];
         Object var5 = this.stack[var3 + 1];
         if (var4 == FRAME_START_MARKER) {
            var1.append('|');
            var2 = true;
         } else {
            if (!var2) {
               var1.append(',');
            }

            var2 = false;
            var1.append(var4).append(':').append(var5);
         }
      }

      return var1.toString();
   }

   @VisibleForTesting
   public Map<Atom<?>, ?> lastFrame() {
      HashMap var1 = new HashMap();

      for(int var2 = this.topEntryKeyIndex; var2 > this.topMarkerKeyIndex; var2 -= 2) {
         Object var3 = this.stack[var2];
         Object var4 = this.stack[var2 + 1];
         var1.put((Atom)var3, var4);
      }

      return var1;
   }

   public boolean hasOnlySingleFrame() {
      for(int var1 = this.topEntryKeyIndex; var1 > 0; --var1) {
         if (this.stack[var1] == FRAME_START_MARKER) {
            return false;
         }
      }

      if (this.stack[0] != FRAME_START_MARKER) {
         throw new IllegalStateException("Corrupted stack");
      } else {
         return true;
      }
   }

   private boolean validateStructure() {
      assert this.topMarkerKeyIndex >= 0;

      assert this.topEntryKeyIndex >= this.topMarkerKeyIndex;

      for(int var1 = 0; var1 <= this.topEntryKeyIndex; var1 += 2) {
         Object var2 = this.stack[var1];
         if (var2 != FRAME_START_MARKER && !(var2 instanceof Atom)) {
            return false;
         }
      }

      for(int var3 = this.topMarkerKeyIndex; var3 != 0; var3 = this.getPreviousMarkerIndex(var3)) {
         Object var4 = this.stack[var3];
         if (var4 != FRAME_START_MARKER) {
            return false;
         }
      }

      return true;
   }
}
