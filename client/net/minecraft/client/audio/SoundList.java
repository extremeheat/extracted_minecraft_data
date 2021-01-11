package net.minecraft.client.audio;

import com.google.common.collect.Lists;
import java.util.List;

public class SoundList {
   private final List<SoundList.SoundEntry> field_148577_a = Lists.newArrayList();
   private boolean field_148575_b;
   private SoundCategory field_148576_c;

   public SoundList() {
      super();
   }

   public List<SoundList.SoundEntry> func_148570_a() {
      return this.field_148577_a;
   }

   public boolean func_148574_b() {
      return this.field_148575_b;
   }

   public void func_148572_a(boolean var1) {
      this.field_148575_b = var1;
   }

   public SoundCategory func_148573_c() {
      return this.field_148576_c;
   }

   public void func_148571_a(SoundCategory var1) {
      this.field_148576_c = var1;
   }

   public static class SoundEntry {
      private String field_148569_a;
      private float field_148567_b = 1.0F;
      private float field_148568_c = 1.0F;
      private int field_148565_d = 1;
      private SoundList.SoundEntry.Type field_148566_e;
      private boolean field_148564_f;

      public SoundEntry() {
         super();
         this.field_148566_e = SoundList.SoundEntry.Type.FILE;
         this.field_148564_f = false;
      }

      public String func_148556_a() {
         return this.field_148569_a;
      }

      public void func_148561_a(String var1) {
         this.field_148569_a = var1;
      }

      public float func_148558_b() {
         return this.field_148567_b;
      }

      public void func_148553_a(float var1) {
         this.field_148567_b = var1;
      }

      public float func_148560_c() {
         return this.field_148568_c;
      }

      public void func_148559_b(float var1) {
         this.field_148568_c = var1;
      }

      public int func_148555_d() {
         return this.field_148565_d;
      }

      public void func_148554_a(int var1) {
         this.field_148565_d = var1;
      }

      public SoundList.SoundEntry.Type func_148563_e() {
         return this.field_148566_e;
      }

      public void func_148562_a(SoundList.SoundEntry.Type var1) {
         this.field_148566_e = var1;
      }

      public boolean func_148552_f() {
         return this.field_148564_f;
      }

      public void func_148557_a(boolean var1) {
         this.field_148564_f = var1;
      }

      public static enum Type {
         FILE("file"),
         SOUND_EVENT("event");

         private final String field_148583_c;

         private Type(String var3) {
            this.field_148583_c = var3;
         }

         public static SoundList.SoundEntry.Type func_148580_a(String var0) {
            SoundList.SoundEntry.Type[] var1 = values();
            int var2 = var1.length;

            for(int var3 = 0; var3 < var2; ++var3) {
               SoundList.SoundEntry.Type var4 = var1[var3];
               if (var4.field_148583_c.equals(var0)) {
                  return var4;
               }
            }

            return null;
         }
      }
   }
}
