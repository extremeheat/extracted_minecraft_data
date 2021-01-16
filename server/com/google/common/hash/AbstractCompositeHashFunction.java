package com.google.common.hash;

import com.google.common.base.Preconditions;
import java.nio.charset.Charset;

abstract class AbstractCompositeHashFunction extends AbstractStreamingHashFunction {
   final HashFunction[] functions;
   private static final long serialVersionUID = 0L;

   AbstractCompositeHashFunction(HashFunction... var1) {
      super();
      HashFunction[] var2 = var1;
      int var3 = var1.length;

      for(int var4 = 0; var4 < var3; ++var4) {
         HashFunction var5 = var2[var4];
         Preconditions.checkNotNull(var5);
      }

      this.functions = var1;
   }

   abstract HashCode makeHash(Hasher[] var1);

   public Hasher newHasher() {
      final Hasher[] var1 = new Hasher[this.functions.length];

      for(int var2 = 0; var2 < var1.length; ++var2) {
         var1[var2] = this.functions[var2].newHasher();
      }

      return new Hasher() {
         public Hasher putByte(byte var1x) {
            Hasher[] var2 = var1;
            int var3 = var2.length;

            for(int var4 = 0; var4 < var3; ++var4) {
               Hasher var5 = var2[var4];
               var5.putByte(var1x);
            }

            return this;
         }

         public Hasher putBytes(byte[] var1x) {
            Hasher[] var2 = var1;
            int var3 = var2.length;

            for(int var4 = 0; var4 < var3; ++var4) {
               Hasher var5 = var2[var4];
               var5.putBytes(var1x);
            }

            return this;
         }

         public Hasher putBytes(byte[] var1x, int var2, int var3) {
            Hasher[] var4 = var1;
            int var5 = var4.length;

            for(int var6 = 0; var6 < var5; ++var6) {
               Hasher var7 = var4[var6];
               var7.putBytes(var1x, var2, var3);
            }

            return this;
         }

         public Hasher putShort(short var1x) {
            Hasher[] var2 = var1;
            int var3 = var2.length;

            for(int var4 = 0; var4 < var3; ++var4) {
               Hasher var5 = var2[var4];
               var5.putShort(var1x);
            }

            return this;
         }

         public Hasher putInt(int var1x) {
            Hasher[] var2 = var1;
            int var3 = var2.length;

            for(int var4 = 0; var4 < var3; ++var4) {
               Hasher var5 = var2[var4];
               var5.putInt(var1x);
            }

            return this;
         }

         public Hasher putLong(long var1x) {
            Hasher[] var3 = var1;
            int var4 = var3.length;

            for(int var5 = 0; var5 < var4; ++var5) {
               Hasher var6 = var3[var5];
               var6.putLong(var1x);
            }

            return this;
         }

         public Hasher putFloat(float var1x) {
            Hasher[] var2 = var1;
            int var3 = var2.length;

            for(int var4 = 0; var4 < var3; ++var4) {
               Hasher var5 = var2[var4];
               var5.putFloat(var1x);
            }

            return this;
         }

         public Hasher putDouble(double var1x) {
            Hasher[] var3 = var1;
            int var4 = var3.length;

            for(int var5 = 0; var5 < var4; ++var5) {
               Hasher var6 = var3[var5];
               var6.putDouble(var1x);
            }

            return this;
         }

         public Hasher putBoolean(boolean var1x) {
            Hasher[] var2 = var1;
            int var3 = var2.length;

            for(int var4 = 0; var4 < var3; ++var4) {
               Hasher var5 = var2[var4];
               var5.putBoolean(var1x);
            }

            return this;
         }

         public Hasher putChar(char var1x) {
            Hasher[] var2 = var1;
            int var3 = var2.length;

            for(int var4 = 0; var4 < var3; ++var4) {
               Hasher var5 = var2[var4];
               var5.putChar(var1x);
            }

            return this;
         }

         public Hasher putUnencodedChars(CharSequence var1x) {
            Hasher[] var2 = var1;
            int var3 = var2.length;

            for(int var4 = 0; var4 < var3; ++var4) {
               Hasher var5 = var2[var4];
               var5.putUnencodedChars(var1x);
            }

            return this;
         }

         public Hasher putString(CharSequence var1x, Charset var2) {
            Hasher[] var3 = var1;
            int var4 = var3.length;

            for(int var5 = 0; var5 < var4; ++var5) {
               Hasher var6 = var3[var5];
               var6.putString(var1x, var2);
            }

            return this;
         }

         public <T> Hasher putObject(T var1x, Funnel<? super T> var2) {
            Hasher[] var3 = var1;
            int var4 = var3.length;

            for(int var5 = 0; var5 < var4; ++var5) {
               Hasher var6 = var3[var5];
               var6.putObject(var1x, var2);
            }

            return this;
         }

         public HashCode hash() {
            return AbstractCompositeHashFunction.this.makeHash(var1);
         }
      };
   }
}
