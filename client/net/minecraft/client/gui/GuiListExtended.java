package net.minecraft.client.gui;

import com.google.common.collect.Lists;
import java.util.AbstractList;
import java.util.List;
import net.minecraft.client.Minecraft;
import net.minecraft.util.Util;

public abstract class GuiListExtended<E extends GuiListExtended.IGuiListEntry<E>> extends GuiSlot {
   private final List<E> field_195087_v = new GuiListExtended.UpdatingList();

   public GuiListExtended(Minecraft var1, int var2, int var3, int var4, int var5, int var6) {
      super(var1, var2, var3, var4, var5, var6);
   }

   protected boolean func_195078_a(int var1, int var2, double var3, double var5) {
      return this.func_148180_b(var1).mouseClicked(var3, var5, var2);
   }

   protected boolean func_148131_a(int var1) {
      return false;
   }

   protected void func_148123_a() {
   }

   protected void func_192637_a(int var1, int var2, int var3, int var4, int var5, int var6, float var7) {
      this.func_148180_b(var1).func_194999_a(this.func_148139_c(), var4, var5, var6, this.func_195079_b((double)var5, (double)var6) && this.func_195083_a((double)var5, (double)var6) == var1, var7);
   }

   protected void func_192639_a(int var1, int var2, int var3, float var4) {
      this.func_148180_b(var1).func_195000_a(var4);
   }

   public final List<E> func_195074_b() {
      return this.field_195087_v;
   }

   protected final void func_195086_c() {
      this.field_195087_v.clear();
   }

   private E func_148180_b(int var1) {
      return (GuiListExtended.IGuiListEntry)this.func_195074_b().get(var1);
   }

   protected final void func_195085_a(E var1) {
      this.field_195087_v.add(var1);
   }

   public void func_195080_b(int var1) {
      this.field_148168_r = var1;
      this.field_148167_s = Util.func_211177_b();
   }

   protected final int func_148127_b() {
      return this.func_195074_b().size();
   }

   class UpdatingList extends AbstractList<E> {
      private final List<E> field_198174_b;

      private UpdatingList() {
         super();
         this.field_198174_b = Lists.newArrayList();
      }

      public E get(int var1) {
         return (GuiListExtended.IGuiListEntry)this.field_198174_b.get(var1);
      }

      public int size() {
         return this.field_198174_b.size();
      }

      public E set(int var1, E var2) {
         GuiListExtended.IGuiListEntry var3 = (GuiListExtended.IGuiListEntry)this.field_198174_b.set(var1, var2);
         var2.field_195004_a = GuiListExtended.this;
         var2.field_195005_b = var1;
         return var3;
      }

      public void add(int var1, E var2) {
         this.field_198174_b.add(var1, var2);
         var2.field_195004_a = GuiListExtended.this;
         var2.field_195005_b = var1;

         for(int var3 = var1 + 1; var3 < this.size(); this.get(var3).field_195005_b = var3++) {
         }

      }

      public E remove(int var1) {
         GuiListExtended.IGuiListEntry var2 = (GuiListExtended.IGuiListEntry)this.field_198174_b.remove(var1);

         for(int var3 = var1; var3 < this.size(); this.get(var3).field_195005_b = var3++) {
         }

         return var2;
      }

      // $FF: synthetic method
      public Object remove(int var1) {
         return this.remove(var1);
      }

      // $FF: synthetic method
      public void add(int var1, Object var2) {
         this.add(var1, (GuiListExtended.IGuiListEntry)var2);
      }

      // $FF: synthetic method
      public Object set(int var1, Object var2) {
         return this.set(var1, (GuiListExtended.IGuiListEntry)var2);
      }

      // $FF: synthetic method
      public Object get(int var1) {
         return this.get(var1);
      }

      // $FF: synthetic method
      UpdatingList(Object var2) {
         this();
      }
   }

   public abstract static class IGuiListEntry<E extends GuiListExtended.IGuiListEntry<E>> implements IGuiEventListener {
      protected GuiListExtended<E> field_195004_a;
      protected int field_195005_b;

      public IGuiListEntry() {
         super();
      }

      protected GuiListExtended<E> func_194998_a() {
         return this.field_195004_a;
      }

      protected int func_195003_b() {
         return this.field_195005_b;
      }

      protected int func_195001_c() {
         return this.field_195004_a.field_148153_b + 4 - this.field_195004_a.func_148148_g() + this.field_195005_b * this.field_195004_a.field_148149_f + this.field_195004_a.field_148160_j;
      }

      protected int func_195002_d() {
         return this.field_195004_a.field_148152_e + this.field_195004_a.field_148155_a / 2 - this.field_195004_a.func_148139_c() / 2 + 2;
      }

      protected void func_195000_a(float var1) {
      }

      public abstract void func_194999_a(int var1, int var2, int var3, int var4, boolean var5, float var6);
   }
}
