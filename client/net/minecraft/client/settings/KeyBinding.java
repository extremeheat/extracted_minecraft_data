package net.minecraft.client.settings;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import net.minecraft.client.resources.I18n;
import net.minecraft.util.IntHashMap;

public class KeyBinding implements Comparable<KeyBinding> {
   private static final List<KeyBinding> field_74516_a = Lists.newArrayList();
   private static final IntHashMap<KeyBinding> field_74514_b = new IntHashMap();
   private static final Set<String> field_151473_c = Sets.newHashSet();
   private final String field_74515_c;
   private final int field_151472_e;
   private final String field_151471_f;
   private int field_74512_d;
   private boolean field_74513_e;
   private int field_151474_i;

   public static void func_74507_a(int var0) {
      if (var0 != 0) {
         KeyBinding var1 = (KeyBinding)field_74514_b.func_76041_a(var0);
         if (var1 != null) {
            ++var1.field_151474_i;
         }

      }
   }

   public static void func_74510_a(int var0, boolean var1) {
      if (var0 != 0) {
         KeyBinding var2 = (KeyBinding)field_74514_b.func_76041_a(var0);
         if (var2 != null) {
            var2.field_74513_e = var1;
         }

      }
   }

   public static void func_74506_a() {
      Iterator var0 = field_74516_a.iterator();

      while(var0.hasNext()) {
         KeyBinding var1 = (KeyBinding)var0.next();
         var1.func_74505_d();
      }

   }

   public static void func_74508_b() {
      field_74514_b.func_76046_c();
      Iterator var0 = field_74516_a.iterator();

      while(var0.hasNext()) {
         KeyBinding var1 = (KeyBinding)var0.next();
         field_74514_b.func_76038_a(var1.field_74512_d, var1);
      }

   }

   public static Set<String> func_151467_c() {
      return field_151473_c;
   }

   public KeyBinding(String var1, int var2, String var3) {
      super();
      this.field_74515_c = var1;
      this.field_74512_d = var2;
      this.field_151472_e = var2;
      this.field_151471_f = var3;
      field_74516_a.add(this);
      field_74514_b.func_76038_a(var2, this);
      field_151473_c.add(var3);
   }

   public boolean func_151470_d() {
      return this.field_74513_e;
   }

   public String func_151466_e() {
      return this.field_151471_f;
   }

   public boolean func_151468_f() {
      if (this.field_151474_i == 0) {
         return false;
      } else {
         --this.field_151474_i;
         return true;
      }
   }

   private void func_74505_d() {
      this.field_151474_i = 0;
      this.field_74513_e = false;
   }

   public String func_151464_g() {
      return this.field_74515_c;
   }

   public int func_151469_h() {
      return this.field_151472_e;
   }

   public int func_151463_i() {
      return this.field_74512_d;
   }

   public void func_151462_b(int var1) {
      this.field_74512_d = var1;
   }

   public int compareTo(KeyBinding var1) {
      int var2 = I18n.func_135052_a(this.field_151471_f).compareTo(I18n.func_135052_a(var1.field_151471_f));
      if (var2 == 0) {
         var2 = I18n.func_135052_a(this.field_74515_c).compareTo(I18n.func_135052_a(var1.field_74515_c));
      }

      return var2;
   }

   // $FF: synthetic method
   public int compareTo(Object var1) {
      return this.compareTo((KeyBinding)var1);
   }
}
