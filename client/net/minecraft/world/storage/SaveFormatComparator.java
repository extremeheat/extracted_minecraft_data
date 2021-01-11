package net.minecraft.world.storage;

import net.minecraft.world.WorldSettings;

public class SaveFormatComparator implements Comparable<SaveFormatComparator> {
   private final String field_75797_a;
   private final String field_75795_b;
   private final long field_75796_c;
   private final long field_75793_d;
   private final boolean field_75794_e;
   private final WorldSettings.GameType field_75791_f;
   private final boolean field_75792_g;
   private final boolean field_75798_h;

   public SaveFormatComparator(String var1, String var2, long var3, long var5, WorldSettings.GameType var7, boolean var8, boolean var9, boolean var10) {
      super();
      this.field_75797_a = var1;
      this.field_75795_b = var2;
      this.field_75796_c = var3;
      this.field_75793_d = var5;
      this.field_75791_f = var7;
      this.field_75794_e = var8;
      this.field_75792_g = var9;
      this.field_75798_h = var10;
   }

   public String func_75786_a() {
      return this.field_75797_a;
   }

   public String func_75788_b() {
      return this.field_75795_b;
   }

   public long func_154336_c() {
      return this.field_75793_d;
   }

   public boolean func_75785_d() {
      return this.field_75794_e;
   }

   public long func_75784_e() {
      return this.field_75796_c;
   }

   public int compareTo(SaveFormatComparator var1) {
      if (this.field_75796_c < var1.field_75796_c) {
         return 1;
      } else {
         return this.field_75796_c > var1.field_75796_c ? -1 : this.field_75797_a.compareTo(var1.field_75797_a);
      }
   }

   public WorldSettings.GameType func_75790_f() {
      return this.field_75791_f;
   }

   public boolean func_75789_g() {
      return this.field_75792_g;
   }

   public boolean func_75783_h() {
      return this.field_75798_h;
   }

   // $FF: synthetic method
   public int compareTo(Object var1) {
      return this.compareTo((SaveFormatComparator)var1);
   }
}
