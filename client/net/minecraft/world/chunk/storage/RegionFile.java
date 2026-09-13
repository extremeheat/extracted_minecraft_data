package net.minecraft.world.chunk.storage;

import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.ArrayList;
import java.util.zip.DeflaterOutputStream;
import java.util.zip.GZIPInputStream;
import java.util.zip.InflaterInputStream;
import net.minecraft.server.MinecraftServer;

public class RegionFile {
   private static final byte[] field_76720_a = new byte[4096];
   private final File field_76718_b;
   private RandomAccessFile field_76719_c;
   private final int[] field_76716_d = new int[1024];
   private final int[] field_76717_e = new int[1024];
   private ArrayList field_76714_f;
   private int field_76715_g;
   private long field_76721_h;

   public RegionFile(File var1) {
      super();
      this.field_76718_b = var1;
      this.field_76715_g = 0;

      try {
         if (var1.exists()) {
            this.field_76721_h = var1.lastModified();
         }

         this.field_76719_c = new RandomAccessFile(var1, "rw");
         if (this.field_76719_c.length() < 4096L) {
            for(int var2 = 0; var2 < 1024; ++var2) {
               this.field_76719_c.writeInt(0);
            }

            for(int var7 = 0; var7 < 1024; ++var7) {
               this.field_76719_c.writeInt(0);
            }

            this.field_76715_g += 8192;
         }

         if ((this.field_76719_c.length() & 4095L) != 0L) {
            for(int var8 = 0; (long)var8 < (this.field_76719_c.length() & 4095L); ++var8) {
               this.field_76719_c.write(0);
            }
         }

         int var9 = (int)this.field_76719_c.length() / 4096;
         this.field_76714_f = new ArrayList(var9);

         for(int var3 = 0; var3 < var9; ++var3) {
            this.field_76714_f.add(true);
         }

         this.field_76714_f.set(0, false);
         this.field_76714_f.set(1, false);
         this.field_76719_c.seek(0L);

         for(int var10 = 0; var10 < 1024; ++var10) {
            int var4 = this.field_76719_c.readInt();
            this.field_76716_d[var10] = var4;
            if (var4 != 0 && (var4 >> 8) + (var4 & 0xFF) <= this.field_76714_f.size()) {
               for(int var5 = 0; var5 < (var4 & 0xFF); ++var5) {
                  this.field_76714_f.set((var4 >> 8) + var5, false);
               }
            }
         }

         for(int var11 = 0; var11 < 1024; ++var11) {
            int var12 = this.field_76719_c.readInt();
            this.field_76717_e[var11] = var12;
         }
      } catch (IOException var6) {
         var6.printStackTrace();
      }
   }

   public synchronized DataInputStream func_76704_a(int var1, int var2) {
      if (this.func_76705_d(var1, var2)) {
         return null;
      } else {
         try {
            int var3 = this.func_76707_e(var1, var2);
            if (var3 == 0) {
               return null;
            } else {
               int var4 = var3 >> 8;
               int var5 = var3 & 0xFF;
               if (var4 + var5 > this.field_76714_f.size()) {
                  return null;
               } else {
                  this.field_76719_c.seek((long)(var4 * 4096));
                  int var6 = this.field_76719_c.readInt();
                  if (var6 > 4096 * var5) {
                     return null;
                  } else if (var6 <= 0) {
                     return null;
                  } else {
                     byte var7 = this.field_76719_c.readByte();
                     if (var7 == 1) {
                        byte[] var10 = new byte[var6 - 1];
                        this.field_76719_c.read(var10);
                        return new DataInputStream(new BufferedInputStream(new GZIPInputStream(new ByteArrayInputStream(var10))));
                     } else if (var7 == 2) {
                        byte[] var8 = new byte[var6 - 1];
                        this.field_76719_c.read(var8);
                        return new DataInputStream(new BufferedInputStream(new InflaterInputStream(new ByteArrayInputStream(var8))));
                     } else {
                        return null;
                     }
                  }
               }
            }
         } catch (IOException var9) {
            return null;
         }
      }
   }

   public DataOutputStream func_76710_b(int var1, int var2) {
      return this.func_76705_d(var1, var2) ? null : new DataOutputStream(new DeflaterOutputStream(new RegionFile$ChunkBuffer(this, var1, var2)));
   }

   protected synchronized void func_76706_a(int var1, int var2, byte[] var3, int var4) {
      try {
         int var5 = this.func_76707_e(var1, var2);
         int var6 = var5 >> 8;
         int var7 = var5 & 0xFF;
         int var8 = (var4 + 5) / 4096 + 1;
         if (var8 >= 256) {
            return;
         }

         if (var6 != 0 && var7 == var8) {
            this.func_76712_a(var6, var3, var4);
         } else {
            for(int var9 = 0; var9 < var7; ++var9) {
               this.field_76714_f.set(var6 + var9, true);
            }

            int var15 = this.field_76714_f.indexOf(true);
            int var10 = 0;
            if (var15 != -1) {
               for(int var11 = var15; var11 < this.field_76714_f.size(); ++var11) {
                  if (var10 != 0) {
                     if (this.field_76714_f.get(var11)) {
                        ++var10;
                     } else {
                        var10 = 0;
                     }
                  } else if (this.field_76714_f.get(var11)) {
                     var15 = var11;
                     var10 = 1;
                  }

                  if (var10 >= var8) {
                     break;
                  }
               }
            }

            if (var10 >= var8) {
               var6 = var15;
               this.func_76711_a(var1, var2, var15 << 8 | var8);

               for(int var17 = 0; var17 < var8; ++var17) {
                  this.field_76714_f.set(var6 + var17, false);
               }

               this.func_76712_a(var6, var3, var4);
            } else {
               this.field_76719_c.seek(this.field_76719_c.length());
               var6 = this.field_76714_f.size();

               for(int var16 = 0; var16 < var8; ++var16) {
                  this.field_76719_c.write(field_76720_a);
                  this.field_76714_f.add(false);
               }

               this.field_76715_g += 4096 * var8;
               this.func_76712_a(var6, var3, var4);
               this.func_76711_a(var1, var2, var6 << 8 | var8);
            }
         }

         this.func_76713_b(var1, var2, (int)(MinecraftServer.func_130071_aq() / 1000L));
      } catch (IOException var12) {
         var12.printStackTrace();
      }
   }

   private void func_76712_a(int var1, byte[] var2, int var3) {
      this.field_76719_c.seek((long)(var1 * 4096));
      this.field_76719_c.writeInt(var3 + 1);
      this.field_76719_c.writeByte(2);
      this.field_76719_c.write(var2, 0, var3);
   }

   private boolean func_76705_d(int var1, int var2) {
      return var1 < 0 || var1 >= 32 || var2 < 0 || var2 >= 32;
   }

   private int func_76707_e(int var1, int var2) {
      return this.field_76716_d[var1 + var2 * 32];
   }

   public boolean func_76709_c(int var1, int var2) {
      return this.func_76707_e(var1, var2) != 0;
   }

   private void func_76711_a(int var1, int var2, int var3) {
      this.field_76716_d[var1 + var2 * 32] = var3;
      this.field_76719_c.seek((long)((var1 + var2 * 32) * 4));
      this.field_76719_c.writeInt(var3);
   }

   private void func_76713_b(int var1, int var2, int var3) {
      this.field_76717_e[var1 + var2 * 32] = var3;
      this.field_76719_c.seek((long)(4096 + (var1 + var2 * 32) * 4));
      this.field_76719_c.writeInt(var3);
   }

   public void func_76708_c() {
      if (this.field_76719_c != null) {
         this.field_76719_c.close();
      }
   }
}
