package net.minecraft.client.renderer;

import com.google.common.primitives.Floats;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.nio.ShortBuffer;
import java.util.Arrays;
import java.util.BitSet;
import java.util.Comparator;
import net.minecraft.client.renderer.vertex.VertexFormat;
import net.minecraft.client.renderer.vertex.VertexFormatElement;
import net.minecraft.util.MathHelper;
import org.apache.logging.log4j.LogManager;

public class WorldRenderer {
   private ByteBuffer field_179001_a;
   private IntBuffer field_178999_b;
   private ShortBuffer field_181676_c;
   private FloatBuffer field_179000_c;
   private int field_178997_d;
   private VertexFormatElement field_181677_f;
   private int field_181678_g;
   private boolean field_78939_q;
   private int field_179006_k;
   private double field_179004_l;
   private double field_179005_m;
   private double field_179002_n;
   private VertexFormat field_179011_q;
   private boolean field_179010_r;

   public WorldRenderer(int var1) {
      super();
      this.field_179001_a = GLAllocation.func_74524_c(var1 * 4);
      this.field_178999_b = this.field_179001_a.asIntBuffer();
      this.field_181676_c = this.field_179001_a.asShortBuffer();
      this.field_179000_c = this.field_179001_a.asFloatBuffer();
   }

   private void func_181670_b(int var1) {
      if (var1 > this.field_178999_b.remaining()) {
         int var2 = this.field_179001_a.capacity();
         int var3 = var2 % 2097152;
         int var4 = var3 + (((this.field_178999_b.position() + var1) * 4 - var3) / 2097152 + 1) * 2097152;
         LogManager.getLogger().warn("Needed to grow BufferBuilder buffer: Old size " + var2 + " bytes, new size " + var4 + " bytes.");
         int var5 = this.field_178999_b.position();
         ByteBuffer var6 = GLAllocation.func_74524_c(var4);
         this.field_179001_a.position(0);
         var6.put(this.field_179001_a);
         var6.rewind();
         this.field_179001_a = var6;
         this.field_179000_c = this.field_179001_a.asFloatBuffer().asReadOnlyBuffer();
         this.field_178999_b = this.field_179001_a.asIntBuffer();
         this.field_178999_b.position(var5);
         this.field_181676_c = this.field_179001_a.asShortBuffer();
         this.field_181676_c.position(var5 << 1);
      }
   }

   public void func_181674_a(float var1, float var2, float var3) {
      int var4 = this.field_178997_d / 4;
      final float[] var5 = new float[var4];

      for(int var6 = 0; var6 < var4; ++var6) {
         var5[var6] = func_181665_a(this.field_179000_c, (float)((double)var1 + this.field_179004_l), (float)((double)var2 + this.field_179005_m), (float)((double)var3 + this.field_179002_n), this.field_179011_q.func_181719_f(), var6 * this.field_179011_q.func_177338_f());
      }

      Integer[] var15 = new Integer[var4];

      for(int var7 = 0; var7 < var15.length; ++var7) {
         var15[var7] = var7;
      }

      Arrays.sort(var15, new Comparator<Integer>() {
         public int compare(Integer var1, Integer var2) {
            return Floats.compare(var5[var2], var5[var1]);
         }

         // $FF: synthetic method
         public int compare(Object var1, Object var2) {
            return this.compare((Integer)var1, (Integer)var2);
         }
      });
      BitSet var16 = new BitSet();
      int var8 = this.field_179011_q.func_177338_f();
      int[] var9 = new int[var8];

      for(int var10 = 0; (var10 = var16.nextClearBit(var10)) < var15.length; ++var10) {
         int var11 = var15[var10];
         if (var11 != var10) {
            this.field_178999_b.limit(var11 * var8 + var8);
            this.field_178999_b.position(var11 * var8);
            this.field_178999_b.get(var9);
            int var12 = var11;

            for(int var13 = var15[var11]; var12 != var10; var13 = var15[var13]) {
               this.field_178999_b.limit(var13 * var8 + var8);
               this.field_178999_b.position(var13 * var8);
               IntBuffer var14 = this.field_178999_b.slice();
               this.field_178999_b.limit(var12 * var8 + var8);
               this.field_178999_b.position(var12 * var8);
               this.field_178999_b.put(var14);
               var16.set(var12);
               var12 = var13;
            }

            this.field_178999_b.limit(var10 * var8 + var8);
            this.field_178999_b.position(var10 * var8);
            this.field_178999_b.put(var9);
         }

         var16.set(var10);
      }

   }

   public WorldRenderer.State func_181672_a() {
      this.field_178999_b.rewind();
      int var1 = this.func_181664_j();
      this.field_178999_b.limit(var1);
      int[] var2 = new int[var1];
      this.field_178999_b.get(var2);
      this.field_178999_b.limit(this.field_178999_b.capacity());
      this.field_178999_b.position(var1);
      return new WorldRenderer.State(var2, new VertexFormat(this.field_179011_q));
   }

   private int func_181664_j() {
      return this.field_178997_d * this.field_179011_q.func_181719_f();
   }

   private static float func_181665_a(FloatBuffer var0, float var1, float var2, float var3, int var4, int var5) {
      float var6 = var0.get(var5 + var4 * 0 + 0);
      float var7 = var0.get(var5 + var4 * 0 + 1);
      float var8 = var0.get(var5 + var4 * 0 + 2);
      float var9 = var0.get(var5 + var4 * 1 + 0);
      float var10 = var0.get(var5 + var4 * 1 + 1);
      float var11 = var0.get(var5 + var4 * 1 + 2);
      float var12 = var0.get(var5 + var4 * 2 + 0);
      float var13 = var0.get(var5 + var4 * 2 + 1);
      float var14 = var0.get(var5 + var4 * 2 + 2);
      float var15 = var0.get(var5 + var4 * 3 + 0);
      float var16 = var0.get(var5 + var4 * 3 + 1);
      float var17 = var0.get(var5 + var4 * 3 + 2);
      float var18 = (var6 + var9 + var12 + var15) * 0.25F - var1;
      float var19 = (var7 + var10 + var13 + var16) * 0.25F - var2;
      float var20 = (var8 + var11 + var14 + var17) * 0.25F - var3;
      return var18 * var18 + var19 * var19 + var20 * var20;
   }

   public void func_178993_a(WorldRenderer.State var1) {
      this.field_178999_b.clear();
      this.func_181670_b(var1.func_179013_a().length);
      this.field_178999_b.put(var1.func_179013_a());
      this.field_178997_d = var1.func_179014_c();
      this.field_179011_q = new VertexFormat(var1.func_179016_d());
   }

   public void func_178965_a() {
      this.field_178997_d = 0;
      this.field_181677_f = null;
      this.field_181678_g = 0;
   }

   public void func_181668_a(int var1, VertexFormat var2) {
      if (this.field_179010_r) {
         throw new IllegalStateException("Already building!");
      } else {
         this.field_179010_r = true;
         this.func_178965_a();
         this.field_179006_k = var1;
         this.field_179011_q = var2;
         this.field_181677_f = var2.func_177348_c(this.field_181678_g);
         this.field_78939_q = false;
         this.field_179001_a.limit(this.field_179001_a.capacity());
      }
   }

   public WorldRenderer func_181673_a(double var1, double var3) {
      int var5 = this.field_178997_d * this.field_179011_q.func_177338_f() + this.field_179011_q.func_181720_d(this.field_181678_g);
      switch(this.field_181677_f.func_177367_b()) {
      case FLOAT:
         this.field_179001_a.putFloat(var5, (float)var1);
         this.field_179001_a.putFloat(var5 + 4, (float)var3);
         break;
      case UINT:
      case INT:
         this.field_179001_a.putInt(var5, (int)var1);
         this.field_179001_a.putInt(var5 + 4, (int)var3);
         break;
      case USHORT:
      case SHORT:
         this.field_179001_a.putShort(var5, (short)((int)var3));
         this.field_179001_a.putShort(var5 + 2, (short)((int)var1));
         break;
      case UBYTE:
      case BYTE:
         this.field_179001_a.put(var5, (byte)((int)var3));
         this.field_179001_a.put(var5 + 1, (byte)((int)var1));
      }

      this.func_181667_k();
      return this;
   }

   public WorldRenderer func_181671_a(int var1, int var2) {
      int var3 = this.field_178997_d * this.field_179011_q.func_177338_f() + this.field_179011_q.func_181720_d(this.field_181678_g);
      switch(this.field_181677_f.func_177367_b()) {
      case FLOAT:
         this.field_179001_a.putFloat(var3, (float)var1);
         this.field_179001_a.putFloat(var3 + 4, (float)var2);
         break;
      case UINT:
      case INT:
         this.field_179001_a.putInt(var3, var1);
         this.field_179001_a.putInt(var3 + 4, var2);
         break;
      case USHORT:
      case SHORT:
         this.field_179001_a.putShort(var3, (short)var2);
         this.field_179001_a.putShort(var3 + 2, (short)var1);
         break;
      case UBYTE:
      case BYTE:
         this.field_179001_a.put(var3, (byte)var2);
         this.field_179001_a.put(var3 + 1, (byte)var1);
      }

      this.func_181667_k();
      return this;
   }

   public void func_178962_a(int var1, int var2, int var3, int var4) {
      int var5 = (this.field_178997_d - 4) * this.field_179011_q.func_181719_f() + this.field_179011_q.func_177344_b(1) / 4;
      int var6 = this.field_179011_q.func_177338_f() >> 2;
      this.field_178999_b.put(var5, var1);
      this.field_178999_b.put(var5 + var6, var2);
      this.field_178999_b.put(var5 + var6 * 2, var3);
      this.field_178999_b.put(var5 + var6 * 3, var4);
   }

   public void func_178987_a(double var1, double var3, double var5) {
      int var7 = this.field_179011_q.func_181719_f();
      int var8 = (this.field_178997_d - 4) * var7;

      for(int var9 = 0; var9 < 4; ++var9) {
         int var10 = var8 + var9 * var7;
         int var11 = var10 + 1;
         int var12 = var11 + 1;
         this.field_178999_b.put(var10, Float.floatToRawIntBits((float)(var1 + this.field_179004_l) + Float.intBitsToFloat(this.field_178999_b.get(var10))));
         this.field_178999_b.put(var11, Float.floatToRawIntBits((float)(var3 + this.field_179005_m) + Float.intBitsToFloat(this.field_178999_b.get(var11))));
         this.field_178999_b.put(var12, Float.floatToRawIntBits((float)(var5 + this.field_179002_n) + Float.intBitsToFloat(this.field_178999_b.get(var12))));
      }

   }

   private int func_78909_a(int var1) {
      return ((this.field_178997_d - var1) * this.field_179011_q.func_177338_f() + this.field_179011_q.func_177340_e()) / 4;
   }

   public void func_178978_a(float var1, float var2, float var3, int var4) {
      int var5 = this.func_78909_a(var4);
      int var6 = -1;
      if (!this.field_78939_q) {
         var6 = this.field_178999_b.get(var5);
         int var7;
         int var8;
         int var9;
         if (ByteOrder.nativeOrder() == ByteOrder.LITTLE_ENDIAN) {
            var7 = (int)((float)(var6 & 255) * var1);
            var8 = (int)((float)(var6 >> 8 & 255) * var2);
            var9 = (int)((float)(var6 >> 16 & 255) * var3);
            var6 &= -16777216;
            var6 |= var9 << 16 | var8 << 8 | var7;
         } else {
            var7 = (int)((float)(var6 >> 24 & 255) * var1);
            var8 = (int)((float)(var6 >> 16 & 255) * var2);
            var9 = (int)((float)(var6 >> 8 & 255) * var3);
            var6 &= 255;
            var6 |= var7 << 24 | var8 << 16 | var9 << 8;
         }
      }

      this.field_178999_b.put(var5, var6);
   }

   private void func_178988_b(int var1, int var2) {
      int var3 = this.func_78909_a(var2);
      int var4 = var1 >> 16 & 255;
      int var5 = var1 >> 8 & 255;
      int var6 = var1 & 255;
      int var7 = var1 >> 24 & 255;
      this.func_178972_a(var3, var4, var5, var6, var7);
   }

   public void func_178994_b(float var1, float var2, float var3, int var4) {
      int var5 = this.func_78909_a(var4);
      int var6 = MathHelper.func_76125_a((int)(var1 * 255.0F), 0, 255);
      int var7 = MathHelper.func_76125_a((int)(var2 * 255.0F), 0, 255);
      int var8 = MathHelper.func_76125_a((int)(var3 * 255.0F), 0, 255);
      this.func_178972_a(var5, var6, var7, var8, 255);
   }

   private void func_178972_a(int var1, int var2, int var3, int var4, int var5) {
      if (ByteOrder.nativeOrder() == ByteOrder.LITTLE_ENDIAN) {
         this.field_178999_b.put(var1, var5 << 24 | var4 << 16 | var3 << 8 | var2);
      } else {
         this.field_178999_b.put(var1, var2 << 24 | var3 << 16 | var4 << 8 | var5);
      }

   }

   public void func_78914_f() {
      this.field_78939_q = true;
   }

   public WorldRenderer func_181666_a(float var1, float var2, float var3, float var4) {
      return this.func_181669_b((int)(var1 * 255.0F), (int)(var2 * 255.0F), (int)(var3 * 255.0F), (int)(var4 * 255.0F));
   }

   public WorldRenderer func_181669_b(int var1, int var2, int var3, int var4) {
      if (this.field_78939_q) {
         return this;
      } else {
         int var5 = this.field_178997_d * this.field_179011_q.func_177338_f() + this.field_179011_q.func_181720_d(this.field_181678_g);
         switch(this.field_181677_f.func_177367_b()) {
         case FLOAT:
            this.field_179001_a.putFloat(var5, (float)var1 / 255.0F);
            this.field_179001_a.putFloat(var5 + 4, (float)var2 / 255.0F);
            this.field_179001_a.putFloat(var5 + 8, (float)var3 / 255.0F);
            this.field_179001_a.putFloat(var5 + 12, (float)var4 / 255.0F);
            break;
         case UINT:
         case INT:
            this.field_179001_a.putFloat(var5, (float)var1);
            this.field_179001_a.putFloat(var5 + 4, (float)var2);
            this.field_179001_a.putFloat(var5 + 8, (float)var3);
            this.field_179001_a.putFloat(var5 + 12, (float)var4);
            break;
         case USHORT:
         case SHORT:
            this.field_179001_a.putShort(var5, (short)var1);
            this.field_179001_a.putShort(var5 + 2, (short)var2);
            this.field_179001_a.putShort(var5 + 4, (short)var3);
            this.field_179001_a.putShort(var5 + 6, (short)var4);
            break;
         case UBYTE:
         case BYTE:
            if (ByteOrder.nativeOrder() == ByteOrder.LITTLE_ENDIAN) {
               this.field_179001_a.put(var5, (byte)var1);
               this.field_179001_a.put(var5 + 1, (byte)var2);
               this.field_179001_a.put(var5 + 2, (byte)var3);
               this.field_179001_a.put(var5 + 3, (byte)var4);
            } else {
               this.field_179001_a.put(var5, (byte)var4);
               this.field_179001_a.put(var5 + 1, (byte)var3);
               this.field_179001_a.put(var5 + 2, (byte)var2);
               this.field_179001_a.put(var5 + 3, (byte)var1);
            }
         }

         this.func_181667_k();
         return this;
      }
   }

   public void func_178981_a(int[] var1) {
      this.func_181670_b(var1.length);
      this.field_178999_b.position(this.func_181664_j());
      this.field_178999_b.put(var1);
      this.field_178997_d += var1.length / this.field_179011_q.func_181719_f();
   }

   public void func_181675_d() {
      ++this.field_178997_d;
      this.func_181670_b(this.field_179011_q.func_181719_f());
   }

   public WorldRenderer func_181662_b(double var1, double var3, double var5) {
      int var7 = this.field_178997_d * this.field_179011_q.func_177338_f() + this.field_179011_q.func_181720_d(this.field_181678_g);
      switch(this.field_181677_f.func_177367_b()) {
      case FLOAT:
         this.field_179001_a.putFloat(var7, (float)(var1 + this.field_179004_l));
         this.field_179001_a.putFloat(var7 + 4, (float)(var3 + this.field_179005_m));
         this.field_179001_a.putFloat(var7 + 8, (float)(var5 + this.field_179002_n));
         break;
      case UINT:
      case INT:
         this.field_179001_a.putInt(var7, Float.floatToRawIntBits((float)(var1 + this.field_179004_l)));
         this.field_179001_a.putInt(var7 + 4, Float.floatToRawIntBits((float)(var3 + this.field_179005_m)));
         this.field_179001_a.putInt(var7 + 8, Float.floatToRawIntBits((float)(var5 + this.field_179002_n)));
         break;
      case USHORT:
      case SHORT:
         this.field_179001_a.putShort(var7, (short)((int)(var1 + this.field_179004_l)));
         this.field_179001_a.putShort(var7 + 2, (short)((int)(var3 + this.field_179005_m)));
         this.field_179001_a.putShort(var7 + 4, (short)((int)(var5 + this.field_179002_n)));
         break;
      case UBYTE:
      case BYTE:
         this.field_179001_a.put(var7, (byte)((int)(var1 + this.field_179004_l)));
         this.field_179001_a.put(var7 + 1, (byte)((int)(var3 + this.field_179005_m)));
         this.field_179001_a.put(var7 + 2, (byte)((int)(var5 + this.field_179002_n)));
      }

      this.func_181667_k();
      return this;
   }

   public void func_178975_e(float var1, float var2, float var3) {
      int var4 = (byte)((int)(var1 * 127.0F)) & 255;
      int var5 = (byte)((int)(var2 * 127.0F)) & 255;
      int var6 = (byte)((int)(var3 * 127.0F)) & 255;
      int var7 = var4 | var5 << 8 | var6 << 16;
      int var8 = this.field_179011_q.func_177338_f() >> 2;
      int var9 = (this.field_178997_d - 4) * var8 + this.field_179011_q.func_177342_c() / 4;
      this.field_178999_b.put(var9, var7);
      this.field_178999_b.put(var9 + var8, var7);
      this.field_178999_b.put(var9 + var8 * 2, var7);
      this.field_178999_b.put(var9 + var8 * 3, var7);
   }

   private void func_181667_k() {
      ++this.field_181678_g;
      this.field_181678_g %= this.field_179011_q.func_177345_h();
      this.field_181677_f = this.field_179011_q.func_177348_c(this.field_181678_g);
      if (this.field_181677_f.func_177375_c() == VertexFormatElement.EnumUsage.PADDING) {
         this.func_181667_k();
      }

   }

   public WorldRenderer func_181663_c(float var1, float var2, float var3) {
      int var4 = this.field_178997_d * this.field_179011_q.func_177338_f() + this.field_179011_q.func_181720_d(this.field_181678_g);
      switch(this.field_181677_f.func_177367_b()) {
      case FLOAT:
         this.field_179001_a.putFloat(var4, var1);
         this.field_179001_a.putFloat(var4 + 4, var2);
         this.field_179001_a.putFloat(var4 + 8, var3);
         break;
      case UINT:
      case INT:
         this.field_179001_a.putInt(var4, (int)var1);
         this.field_179001_a.putInt(var4 + 4, (int)var2);
         this.field_179001_a.putInt(var4 + 8, (int)var3);
         break;
      case USHORT:
      case SHORT:
         this.field_179001_a.putShort(var4, (short)((int)var1 * 32767 & '\uffff'));
         this.field_179001_a.putShort(var4 + 2, (short)((int)var2 * 32767 & '\uffff'));
         this.field_179001_a.putShort(var4 + 4, (short)((int)var3 * 32767 & '\uffff'));
         break;
      case UBYTE:
      case BYTE:
         this.field_179001_a.put(var4, (byte)((int)var1 * 127 & 255));
         this.field_179001_a.put(var4 + 1, (byte)((int)var2 * 127 & 255));
         this.field_179001_a.put(var4 + 2, (byte)((int)var3 * 127 & 255));
      }

      this.func_181667_k();
      return this;
   }

   public void func_178969_c(double var1, double var3, double var5) {
      this.field_179004_l = var1;
      this.field_179005_m = var3;
      this.field_179002_n = var5;
   }

   public void func_178977_d() {
      if (!this.field_179010_r) {
         throw new IllegalStateException("Not building!");
      } else {
         this.field_179010_r = false;
         this.field_179001_a.position(0);
         this.field_179001_a.limit(this.func_181664_j() * 4);
      }
   }

   public ByteBuffer func_178966_f() {
      return this.field_179001_a;
   }

   public VertexFormat func_178973_g() {
      return this.field_179011_q;
   }

   public int func_178989_h() {
      return this.field_178997_d;
   }

   public int func_178979_i() {
      return this.field_179006_k;
   }

   public void func_178968_d(int var1) {
      for(int var2 = 0; var2 < 4; ++var2) {
         this.func_178988_b(var1, var2 + 1);
      }

   }

   public void func_178990_f(float var1, float var2, float var3) {
      for(int var4 = 0; var4 < 4; ++var4) {
         this.func_178994_b(var1, var2, var3, var4 + 1);
      }

   }

   public class State {
      private final int[] field_179019_b;
      private final VertexFormat field_179018_e;

      public State(int[] var2, VertexFormat var3) {
         super();
         this.field_179019_b = var2;
         this.field_179018_e = var3;
      }

      public int[] func_179013_a() {
         return this.field_179019_b;
      }

      public int func_179014_c() {
         return this.field_179019_b.length / this.field_179018_e.func_181719_f();
      }

      public VertexFormat func_179016_d() {
         return this.field_179018_e;
      }
   }
}
