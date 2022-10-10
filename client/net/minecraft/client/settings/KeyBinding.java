package net.minecraft.client.settings;

import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.function.Supplier;
import net.minecraft.client.resources.I18n;
import net.minecraft.client.util.InputMappings;
import net.minecraft.util.Util;

public class KeyBinding implements Comparable<KeyBinding> {
   private static final Map<String, KeyBinding> field_74516_a = Maps.newHashMap();
   private static final Map<InputMappings.Input, KeyBinding> field_74514_b = Maps.newHashMap();
   private static final Set<String> field_151473_c = Sets.newHashSet();
   private static final Map<String, Integer> field_193627_d = (Map)Util.func_200696_a(Maps.newHashMap(), (var0) -> {
      var0.put("key.categories.movement", 1);
      var0.put("key.categories.gameplay", 2);
      var0.put("key.categories.inventory", 3);
      var0.put("key.categories.creative", 4);
      var0.put("key.categories.multiplayer", 5);
      var0.put("key.categories.ui", 6);
      var0.put("key.categories.misc", 7);
   });
   private final String field_74515_c;
   private final InputMappings.Input field_151472_e;
   private final String field_151471_f;
   private InputMappings.Input field_74512_d;
   private boolean field_74513_e;
   private int field_151474_i;

   public static void func_197981_a(InputMappings.Input var0) {
      KeyBinding var1 = (KeyBinding)field_74514_b.get(var0);
      if (var1 != null) {
         ++var1.field_151474_i;
      }

   }

   public static void func_197980_a(InputMappings.Input var0, boolean var1) {
      KeyBinding var2 = (KeyBinding)field_74514_b.get(var0);
      if (var2 != null) {
         var2.field_74513_e = var1;
      }

   }

   public static void func_186704_a() {
      Iterator var0 = field_74516_a.values().iterator();

      while(var0.hasNext()) {
         KeyBinding var1 = (KeyBinding)var0.next();
         if (var1.field_74512_d.func_197938_b() == InputMappings.Type.KEYSYM && var1.field_74512_d.func_197937_c() != -1) {
            var1.field_74513_e = InputMappings.func_197956_a(var1.field_74512_d.func_197937_c());
         }
      }

   }

   public static void func_74506_a() {
      Iterator var0 = field_74516_a.values().iterator();

      while(var0.hasNext()) {
         KeyBinding var1 = (KeyBinding)var0.next();
         var1.func_74505_d();
      }

   }

   public static void func_74508_b() {
      field_74514_b.clear();
      Iterator var0 = field_74516_a.values().iterator();

      while(var0.hasNext()) {
         KeyBinding var1 = (KeyBinding)var0.next();
         field_74514_b.put(var1.field_74512_d, var1);
      }

   }

   public KeyBinding(String var1, int var2, String var3) {
      this(var1, InputMappings.Type.KEYSYM, var2, var3);
   }

   public KeyBinding(String var1, InputMappings.Type var2, int var3, String var4) {
      super();
      this.field_74515_c = var1;
      this.field_74512_d = var2.func_197944_a(var3);
      this.field_151472_e = this.field_74512_d;
      this.field_151471_f = var4;
      field_74516_a.put(var1, this);
      field_74514_b.put(this.field_74512_d, this);
      field_151473_c.add(var4);
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

   public InputMappings.Input func_197977_i() {
      return this.field_151472_e;
   }

   public void func_197979_b(InputMappings.Input var1) {
      this.field_74512_d = var1;
   }

   public int compareTo(KeyBinding var1) {
      return this.field_151471_f.equals(var1.field_151471_f) ? I18n.func_135052_a(this.field_74515_c).compareTo(I18n.func_135052_a(var1.field_74515_c)) : ((Integer)field_193627_d.get(this.field_151471_f)).compareTo((Integer)field_193627_d.get(var1.field_151471_f));
   }

   public static Supplier<String> func_193626_b(String var0) {
      KeyBinding var1 = (KeyBinding)field_74516_a.get(var0);
      return var1 == null ? () -> {
         return var0;
      } : var1::func_197978_k;
   }

   public boolean func_197983_b(KeyBinding var1) {
      return this.field_74512_d.equals(var1.field_74512_d);
   }

   public boolean func_197986_j() {
      return this.field_74512_d.equals(InputMappings.field_197958_a);
   }

   public boolean func_197976_a(int var1, int var2) {
      if (var1 == -1) {
         return this.field_74512_d.func_197938_b() == InputMappings.Type.SCANCODE && this.field_74512_d.func_197937_c() == var2;
      } else {
         return this.field_74512_d.func_197938_b() == InputMappings.Type.KEYSYM && this.field_74512_d.func_197937_c() == var1;
      }
   }

   public boolean func_197984_a(int var1) {
      return this.field_74512_d.func_197938_b() == InputMappings.Type.MOUSE && this.field_74512_d.func_197937_c() == var1;
   }

   public String func_197978_k() {
      return this.field_74512_d.func_197936_a();
   }

   public boolean func_197985_l() {
      return this.field_74512_d.equals(this.field_151472_e);
   }

   public String func_197982_m() {
      return this.field_74512_d.func_197935_d();
   }

   // $FF: synthetic method
   public int compareTo(Object var1) {
      return this.compareTo((KeyBinding)var1);
   }
}
