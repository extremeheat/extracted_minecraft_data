package net.minecraft.client.gui;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import net.minecraft.item.ItemStack;
import net.minecraft.realms.RealmsButton;
import net.minecraft.realms.RealmsScreen;

public class GuiScreenRealmsProxy extends GuiScreen {
   private RealmsScreen field_154330_a;

   public GuiScreenRealmsProxy(RealmsScreen var1) {
      super();
      this.field_154330_a = var1;
      super.field_146292_n = Collections.synchronizedList(new ArrayList());
   }

   public RealmsScreen func_154321_a() {
      return this.field_154330_a;
   }

   @Override
   public void func_73866_w_() {
      this.field_154330_a.init();
      super.func_73866_w_();
   }

   public void func_154325_a(String var1, int var2, int var3, int var4) {
      super.func_73732_a(this.field_146289_q, var1, var2, var3, var4);
   }

   public void func_154322_b(String var1, int var2, int var3, int var4) {
      super.func_73731_b(this.field_146289_q, var1, var2, var3, var4);
   }

   @Override
   public void func_73729_b(int var1, int var2, int var3, int var4, int var5, int var6) {
      this.field_154330_a.blit(var1, var2, var3, var4, var5, var6);
      super.func_73729_b(var1, var2, var3, var4, var5, var6);
   }

   @Override
   public void func_73733_a(int var1, int var2, int var3, int var4, int var5, int var6) {
      super.func_73733_a(var1, var2, var3, var4, var5, var6);
   }

   @Override
   public void func_146276_q_() {
      super.func_146276_q_();
   }

   @Override
   public boolean func_73868_f() {
      return super.func_73868_f();
   }

   @Override
   public void func_146270_b(int var1) {
      super.func_146270_b(var1);
   }

   @Override
   public void func_73863_a(int var1, int var2, float var3) {
      this.field_154330_a.render(var1, var2, var3);
   }

   @Override
   public void func_146285_a(ItemStack var1, int var2, int var3) {
      super.func_146285_a(var1, var2, var3);
   }

   @Override
   public void func_146279_a(String var1, int var2, int var3) {
      super.func_146279_a(var1, var2, var3);
   }

   @Override
   public void func_146283_a(List var1, int var2, int var3) {
      super.func_146283_a(var1, var2, var3);
   }

   @Override
   public void func_73876_c() {
      this.field_154330_a.tick();
      super.func_73876_c();
   }

   public int func_154329_h() {
      return this.field_146289_q.field_78288_b;
   }

   public int func_154326_c(String var1) {
      return this.field_146289_q.func_78256_a(var1);
   }

   public void func_154319_c(String var1, int var2, int var3, int var4) {
      this.field_146289_q.func_78261_a(var1, var2, var3, var4);
   }

   public List func_154323_a(String var1, int var2) {
      return this.field_146289_q.func_78271_c(var1, var2);
   }

   @Override
   public final void func_146284_a(GuiButton var1) {
      this.field_154330_a.buttonClicked(((GuiButtonRealmsProxy)var1).func_154317_g());
   }

   public void func_154324_i() {
      super.field_146292_n.clear();
   }

   public void func_154327_a(RealmsButton var1) {
      super.field_146292_n.add(var1.getProxy());
   }

   public List func_154320_j() {
      ArrayList var1 = new ArrayList(super.field_146292_n.size());

      for(GuiButton var3 : super.field_146292_n) {
         var1.add(((GuiButtonRealmsProxy)var3).func_154317_g());
      }

      return var1;
   }

   public void func_154328_b(RealmsButton var1) {
      super.field_146292_n.remove(var1);
   }

   @Override
   public void func_73864_a(int var1, int var2, int var3) {
      this.field_154330_a.mouseClicked(var1, var2, var3);
      super.func_73864_a(var1, var2, var3);
   }

   @Override
   public void func_146274_d() {
      this.field_154330_a.mouseEvent();
      super.func_146274_d();
   }

   @Override
   public void func_146282_l() {
      this.field_154330_a.keyboardEvent();
      super.func_146282_l();
   }

   @Override
   public void func_146286_b(int var1, int var2, int var3) {
      this.field_154330_a.mouseReleased(var1, var2, var3);
   }

   @Override
   public void func_146273_a(int var1, int var2, int var3, long var4) {
      this.field_154330_a.mouseDragged(var1, var2, var3, var4);
   }

   @Override
   public void func_73869_a(char var1, int var2) {
      this.field_154330_a.keyPressed(var1, var2);
   }

   @Override
   public void func_73878_a(boolean var1, int var2) {
      this.field_154330_a.confirmResult(var1, var2);
   }

   @Override
   public void func_146281_b() {
      this.field_154330_a.removed();
      super.func_146281_b();
   }
}
