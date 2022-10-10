package net.minecraft.client.gui;

import com.google.common.collect.Lists;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import net.minecraft.client.Minecraft;
import net.minecraft.item.ItemStack;
import net.minecraft.realms.RealmsButton;
import net.minecraft.realms.RealmsGuiEventListener;
import net.minecraft.realms.RealmsScreen;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class GuiScreenRealmsProxy extends GuiScreen {
   private final RealmsScreen field_154330_a;
   private static final Logger field_212333_f = LogManager.getLogger();

   public GuiScreenRealmsProxy(RealmsScreen var1) {
      super();
      this.field_154330_a = var1;
   }

   public RealmsScreen func_154321_a() {
      return this.field_154330_a;
   }

   public void func_146280_a(Minecraft var1, int var2, int var3) {
      this.field_154330_a.init(var1, var2, var3);
      super.func_146280_a(var1, var2, var3);
   }

   protected void func_73866_w_() {
      this.field_154330_a.init();
      super.func_73866_w_();
   }

   public void func_154325_a(String var1, int var2, int var3, int var4) {
      super.func_73732_a(this.field_146289_q, var1, var2, var3, var4);
   }

   public void func_207734_a(String var1, int var2, int var3, int var4, boolean var5) {
      if (var5) {
         super.func_73731_b(this.field_146289_q, var1, var2, var3, var4);
      } else {
         this.field_146289_q.func_211126_b(var1, (float)var2, (float)var3, var4);
      }

   }

   public void func_73729_b(int var1, int var2, int var3, int var4, int var5, int var6) {
      this.field_154330_a.blit(var1, var2, var3, var4, var5, var6);
      super.func_73729_b(var1, var2, var3, var4, var5, var6);
   }

   public void func_73733_a(int var1, int var2, int var3, int var4, int var5, int var6) {
      super.func_73733_a(var1, var2, var3, var4, var5, var6);
   }

   public void func_146276_q_() {
      super.func_146276_q_();
   }

   public boolean func_73868_f() {
      return super.func_73868_f();
   }

   public void func_146270_b(int var1) {
      super.func_146270_b(var1);
   }

   public void func_73863_a(int var1, int var2, float var3) {
      this.field_154330_a.render(var1, var2, var3);
   }

   public void func_146285_a(ItemStack var1, int var2, int var3) {
      super.func_146285_a(var1, var2, var3);
   }

   public void func_146279_a(String var1, int var2, int var3) {
      super.func_146279_a(var1, var2, var3);
   }

   public void func_146283_a(List<String> var1, int var2, int var3) {
      super.func_146283_a(var1, var2, var3);
   }

   public void func_73876_c() {
      this.field_154330_a.tick();
      super.func_73876_c();
   }

   public int func_154329_h() {
      return this.field_146289_q.field_78288_b;
   }

   public int func_207731_c(String var1) {
      return this.field_146289_q.func_78256_a(var1);
   }

   public void func_207728_b(String var1, int var2, int var3, int var4) {
      this.field_146289_q.func_175063_a(var1, (float)var2, (float)var3, var4);
   }

   public List<String> func_154323_a(String var1, int var2) {
      return this.field_146289_q.func_78271_c(var1, var2);
   }

   public void func_207735_j() {
      this.field_195124_j.clear();
   }

   public void func_207730_a(RealmsGuiEventListener var1) {
      if (this.func_212332_c(var1) || !this.field_195124_j.add(var1.getProxy())) {
         field_212333_f.error("Tried to add the same widget multiple times: " + var1);
      }

   }

   public void func_207733_b(RealmsGuiEventListener var1) {
      if (!this.func_212332_c(var1) || !this.field_195124_j.remove(var1.getProxy())) {
         field_212333_f.error("Tried to add the same widget multiple times: " + var1);
      }

   }

   public boolean func_212332_c(RealmsGuiEventListener var1) {
      return this.field_195124_j.contains(var1.getProxy());
   }

   public void func_154327_a(RealmsButton var1) {
      this.func_189646_b(var1.getProxy());
   }

   public List<RealmsButton> func_154320_j() {
      ArrayList var1 = Lists.newArrayListWithExpectedSize(this.field_146292_n.size());
      Iterator var2 = this.field_146292_n.iterator();

      while(var2.hasNext()) {
         GuiButton var3 = (GuiButton)var2.next();
         var1.add(((GuiButtonRealmsProxy)var3).func_154317_g());
      }

      return var1;
   }

   public void func_207729_m() {
      HashSet var1 = new HashSet(this.field_146292_n);
      this.field_195124_j.removeIf(var1::contains);
      this.field_146292_n.clear();
   }

   public void func_207732_b(RealmsButton var1) {
      this.field_195124_j.remove(var1.getProxy());
      this.field_146292_n.remove(var1.getProxy());
   }

   public boolean mouseClicked(double var1, double var3, int var5) {
      return this.field_154330_a.mouseClicked(var1, var3, var5) ? true : func_205730_a(this, var1, var3, var5);
   }

   public boolean mouseReleased(double var1, double var3, int var5) {
      return this.field_154330_a.mouseReleased(var1, var3, var5);
   }

   public boolean mouseDragged(double var1, double var3, int var5, double var6, double var8) {
      return this.field_154330_a.mouseDragged(var1, var3, var5, var6, var8) ? true : super.mouseDragged(var1, var3, var5, var6, var8);
   }

   public boolean keyPressed(int var1, int var2, int var3) {
      return this.field_154330_a.keyPressed(var1, var2, var3) ? true : super.keyPressed(var1, var2, var3);
   }

   public boolean charTyped(char var1, int var2) {
      return this.field_154330_a.charTyped(var1, var2) ? true : super.charTyped(var1, var2);
   }

   public void confirmResult(boolean var1, int var2) {
      this.field_154330_a.confirmResult(var1, var2);
   }

   public void func_146281_b() {
      this.field_154330_a.removed();
      super.func_146281_b();
   }

   public int func_209208_b(String var1, int var2, int var3, int var4, boolean var5) {
      return var5 ? this.field_146289_q.func_175063_a(var1, (float)var2, (float)var3, var4) : this.field_146289_q.func_211126_b(var1, (float)var2, (float)var3, var4);
   }

   // $FF: synthetic method
   static boolean func_205730_a(GuiScreenRealmsProxy var0, double var1, double var3, int var5) {
      return var0.mouseClicked(var1, var3, var5);
   }
}
