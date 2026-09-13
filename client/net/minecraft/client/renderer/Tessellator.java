package net.minecraft.client.renderer;

import java.nio.Buffer;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.nio.ShortBuffer;
import java.util.PriorityQueue;
import net.minecraft.client.shader.TesselatorVertexState;
import net.minecraft.client.util.QuadComparator;
import org.lwjgl.opengl.GL11;

public class Tessellator {
   private ByteBuffer field_78394_d;
   private IntBuffer field_147568_c;
   private FloatBuffer field_147566_d;
   private ShortBuffer field_147567_e;
   private int[] field_78405_h;
   private int field_78406_i;
   private double field_78403_j;
   private double field_78404_k;
   private int field_78401_l;
   private int field_78402_m;
   private boolean field_78399_n;
   private boolean field_78400_o;
   private boolean field_78414_p;
   private boolean field_78413_q;
   private int field_147569_p;
   private int field_78411_s;
   private boolean field_78410_t;
   private int field_78409_u;
   private double field_78408_v;
   private double field_78407_w;
   private double field_78417_x;
   private int field_78416_y;
   public static final Tessellator field_78398_a = new Tessellator(2097152);
   private boolean field_78415_z;
   private int field_78388_E;

   private Tessellator(int var1) {
      super();
      this.field_78388_E = var1;
      this.field_78394_d = GLAllocation.func_74524_c(var1 * 4);
      this.field_147568_c = this.field_78394_d.asIntBuffer();
      this.field_147566_d = this.field_78394_d.asFloatBuffer();
      this.field_147567_e = this.field_78394_d.asShortBuffer();
      this.field_78405_h = new int[var1];
   }

   public int func_78381_a() {
      if (!this.field_78415_z) {
         throw new IllegalStateException("Not tesselating!");
      } else {
         this.field_78415_z = false;
         if (this.field_78406_i > 0) {
            ((Buffer)this.field_147568_c).clear();
            this.field_147568_c.put(this.field_78405_h, 0, this.field_147569_p);
            ((Buffer)this.field_78394_d).position(0);
            ((Buffer)this.field_78394_d).limit(this.field_147569_p * 4);
            if (this.field_78400_o) {
               ((Buffer)this.field_147566_d).position(3);
               GL11.glTexCoordPointer(2, 32, this.field_147566_d);
               GL11.glEnableClientState(32888);
            }

            if (this.field_78414_p) {
               OpenGlHelper.func_77472_b(OpenGlHelper.field_77476_b);
               ((Buffer)this.field_147567_e).position(14);
               GL11.glTexCoordPointer(2, 32, this.field_147567_e);
               GL11.glEnableClientState(32888);
               OpenGlHelper.func_77472_b(OpenGlHelper.field_77478_a);
            }

            if (this.field_78399_n) {
               ((Buffer)this.field_78394_d).position(20);
               GL11.glColorPointer(4, true, 32, this.field_78394_d);
               GL11.glEnableClientState(32886);
            }

            if (this.field_78413_q) {
               ((Buffer)this.field_78394_d).position(24);
               GL11.glNormalPointer(32, this.field_78394_d);
               GL11.glEnableClientState(32885);
            }

            ((Buffer)this.field_147566_d).position(0);
            GL11.glVertexPointer(3, 32, this.field_147566_d);
            GL11.glEnableClientState(32884);
            GL11.glDrawArrays(this.field_78409_u, 0, this.field_78406_i);
            GL11.glDisableClientState(32884);
            if (this.field_78400_o) {
               GL11.glDisableClientState(32888);
            }

            if (this.field_78414_p) {
               OpenGlHelper.func_77472_b(OpenGlHelper.field_77476_b);
               GL11.glDisableClientState(32888);
               OpenGlHelper.func_77472_b(OpenGlHelper.field_77478_a);
            }

            if (this.field_78399_n) {
               GL11.glDisableClientState(32886);
            }

            if (this.field_78413_q) {
               GL11.glDisableClientState(32885);
            }
         }

         int var1 = this.field_147569_p * 4;
         this.func_78379_d();
         return var1;
      }
   }

   public TesselatorVertexState func_147564_a(float var1, float var2, float var3) {
      int[] var4 = new int[this.field_147569_p];
      PriorityQueue var5 = new PriorityQueue(
         this.field_147569_p,
         new QuadComparator(this.field_78405_h, var1 + (float)this.field_78408_v, var2 + (float)this.field_78407_w, var3 + (float)this.field_78417_x)
      );
      byte var6 = 32;

      for(int var7 = 0; var7 < this.field_147569_p; var7 += var6) {
         var5.add(var7);
      }

      for(int var10 = 0; !var5.isEmpty(); var10 += var6) {
         int var8 = var5.remove();

         for(int var9 = 0; var9 < var6; ++var9) {
            var4[var10 + var9] = this.field_78405_h[var8 + var9];
         }
      }

      System.arraycopy(var4, 0, this.field_78405_h, 0, var4.length);
      return new TesselatorVertexState(
         var4, this.field_147569_p, this.field_78406_i, this.field_78400_o, this.field_78414_p, this.field_78413_q, this.field_78399_n
      );
   }

   public void func_147565_a(TesselatorVertexState var1) {
      System.arraycopy(var1.func_147572_a(), 0, this.field_78405_h, 0, var1.func_147572_a().length);
      this.field_147569_p = var1.func_147576_b();
      this.field_78406_i = var1.func_147575_c();
      this.field_78400_o = var1.func_147573_d();
      this.field_78414_p = var1.func_147571_e();
      this.field_78399_n = var1.func_147574_g();
      this.field_78413_q = var1.func_147570_f();
   }

   private void func_78379_d() {
      this.field_78406_i = 0;
      ((Buffer)this.field_78394_d).clear();
      this.field_147569_p = 0;
      this.field_78411_s = 0;
   }

   public void func_78382_b() {
      this.func_78371_b(7);
   }

   public void func_78371_b(int var1) {
      if (this.field_78415_z) {
         throw new IllegalStateException("Already tesselating!");
      } else {
         this.field_78415_z = true;
         this.func_78379_d();
         this.field_78409_u = var1;
         this.field_78413_q = false;
         this.field_78399_n = false;
         this.field_78400_o = false;
         this.field_78414_p = false;
         this.field_78410_t = false;
      }
   }

   public void func_78385_a(double var1, double var3) {
      this.field_78400_o = true;
      this.field_78403_j = var1;
      this.field_78404_k = var3;
   }

   public void func_78380_c(int var1) {
      this.field_78414_p = true;
      this.field_78401_l = var1;
   }

   public void func_78386_a(float var1, float var2, float var3) {
      this.func_78376_a((int)(var1 * 255.0F), (int)(var2 * 255.0F), (int)(var3 * 255.0F));
   }

   public void func_78369_a(float var1, float var2, float var3, float var4) {
      this.func_78370_a((int)(var1 * 255.0F), (int)(var2 * 255.0F), (int)(var3 * 255.0F), (int)(var4 * 255.0F));
   }

   public void func_78376_a(int var1, int var2, int var3) {
      this.func_78370_a(var1, var2, var3, 255);
   }

   public void func_78370_a(int var1, int var2, int var3, int var4) {
      if (!this.field_78410_t) {
         if (var1 > 255) {
            var1 = 255;
         }

         if (var2 > 255) {
            var2 = 255;
         }

         if (var3 > 255) {
            var3 = 255;
         }

         if (var4 > 255) {
            var4 = 255;
         }

         if (var1 < 0) {
            var1 = 0;
         }

         if (var2 < 0) {
            var2 = 0;
         }

         if (var3 < 0) {
            var3 = 0;
         }

         if (var4 < 0) {
            var4 = 0;
         }

         this.field_78399_n = true;
         if (ByteOrder.nativeOrder() == ByteOrder.LITTLE_ENDIAN) {
            this.field_78402_m = var4 << 24 | var3 << 16 | var2 << 8 | var1;
         } else {
            this.field_78402_m = var1 << 24 | var2 << 16 | var3 << 8 | var4;
         }
      }
   }

   public void func_154352_a(byte var1, byte var2, byte var3) {
      this.func_78376_a(var1 & 255, var2 & 255, var3 & 255);
   }

   public void func_78374_a(double var1, double var3, double var5, double var7, double var9) {
      this.func_78385_a(var7, var9);
      this.func_78377_a(var1, var3, var5);
   }

   public void func_78377_a(double var1, double var3, double var5) {
      ++this.field_78411_s;
      if (this.field_78400_o) {
         this.field_78405_h[this.field_147569_p + 3] = Float.floatToRawIntBits((float)this.field_78403_j);
         this.field_78405_h[this.field_147569_p + 4] = Float.floatToRawIntBits((float)this.field_78404_k);
      }

      if (this.field_78414_p) {
         this.field_78405_h[this.field_147569_p + 7] = this.field_78401_l;
      }

      if (this.field_78399_n) {
         this.field_78405_h[this.field_147569_p + 5] = this.field_78402_m;
      }

      if (this.field_78413_q) {
         this.field_78405_h[this.field_147569_p + 6] = this.field_78416_y;
      }

      this.field_78405_h[this.field_147569_p + 0] = Float.floatToRawIntBits((float)(var1 + this.field_78408_v));
      this.field_78405_h[this.field_147569_p + 1] = Float.floatToRawIntBits((float)(var3 + this.field_78407_w));
      this.field_78405_h[this.field_147569_p + 2] = Float.floatToRawIntBits((float)(var5 + this.field_78417_x));
      this.field_147569_p += 8;
      ++this.field_78406_i;
      if (this.field_78406_i % 4 == 0 && this.field_147569_p >= this.field_78388_E - 32) {
         this.func_78381_a();
         this.field_78415_z = true;
      }
   }

   public void func_78378_d(int var1) {
      int var2 = var1 >> 16 & 0xFF;
      int var3 = var1 >> 8 & 0xFF;
      int var4 = var1 & 0xFF;
      this.func_78376_a(var2, var3, var4);
   }

   public void func_78384_a(int var1, int var2) {
      int var3 = var1 >> 16 & 0xFF;
      int var4 = var1 >> 8 & 0xFF;
      int var5 = var1 & 0xFF;
      this.func_78370_a(var3, var4, var5, var2);
   }

   public void func_78383_c() {
      this.field_78410_t = true;
   }

   public void func_78375_b(float var1, float var2, float var3) {
      this.field_78413_q = true;
      byte var4 = (byte)((int)(var1 * 127.0F));
      byte var5 = (byte)((int)(var2 * 127.0F));
      byte var6 = (byte)((int)(var3 * 127.0F));
      this.field_78416_y = var4 & 255 | (var5 & 255) << 8 | (var6 & 255) << 16;
   }

   public void func_78373_b(double var1, double var3, double var5) {
      this.field_78408_v = var1;
      this.field_78407_w = var3;
      this.field_78417_x = var5;
   }

   public void func_78372_c(float var1, float var2, float var3) {
      this.field_78408_v += (double)var1;
      this.field_78407_w += (double)var2;
      this.field_78417_x += (double)var3;
   }
}
