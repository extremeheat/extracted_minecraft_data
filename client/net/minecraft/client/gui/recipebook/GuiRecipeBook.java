package net.minecraft.client.gui.recipebook;

import com.google.common.collect.Lists;
import it.unimi.dsi.fastutil.objects.ObjectLinkedOpenHashSet;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import javax.annotation.Nullable;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiButtonToggle;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.GuiTextField;
import net.minecraft.client.gui.IGuiEventListener;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.resources.I18n;
import net.minecraft.client.resources.Language;
import net.minecraft.client.resources.LanguageManager;
import net.minecraft.client.util.RecipeBookCategories;
import net.minecraft.client.util.RecipeBookClient;
import net.minecraft.client.util.SearchTreeManager;
import net.minecraft.inventory.ContainerRecipeBook;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.item.crafting.IRecipePlacer;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.item.crafting.RecipeItemHelper;
import net.minecraft.network.play.client.CPacketRecipeInfo;
import net.minecraft.util.ResourceLocation;

public class GuiRecipeBook extends Gui implements IGuiEventListener, IRecipeUpdateListener, IRecipePlacer<Ingredient> {
   protected static final ResourceLocation field_191894_a = new ResourceLocation("textures/gui/recipe_book.png");
   private int field_191903_n;
   private int field_191904_o;
   private int field_191905_p;
   protected final GhostRecipe field_191915_z = new GhostRecipe();
   private final List<GuiButtonRecipeTab> field_193018_j = Lists.newArrayList();
   private GuiButtonRecipeTab field_191913_x;
   protected GuiButtonToggle field_193960_m;
   protected ContainerRecipeBook field_201522_g;
   protected Minecraft field_191888_F;
   private GuiTextField field_193962_q;
   private String field_193963_r = "";
   protected RecipeBookClient field_193964_s;
   protected final RecipeBookPage field_193022_s = new RecipeBookPage();
   protected final RecipeItemHelper field_193965_u = new RecipeItemHelper();
   private int field_193966_v;
   private boolean field_199738_u;

   public GuiRecipeBook() {
      super();
   }

   public void func_201520_a(int var1, int var2, Minecraft var3, boolean var4, ContainerRecipeBook var5) {
      this.field_191888_F = var3;
      this.field_191904_o = var1;
      this.field_191905_p = var2;
      this.field_201522_g = var5;
      var3.field_71439_g.field_71070_bA = var5;
      this.field_193964_s = var3.field_71439_g.func_199507_B();
      this.field_193966_v = var3.field_71439_g.field_71071_by.func_194015_p();
      if (this.func_191878_b()) {
         this.func_201518_a(var4);
      }

      var3.field_195559_v.func_197967_a(true);
   }

   public void func_201518_a(boolean var1) {
      this.field_191903_n = var1 ? 0 : 86;
      int var2 = (this.field_191904_o - 147) / 2 - this.field_191903_n;
      int var3 = (this.field_191905_p - 166) / 2;
      this.field_193965_u.func_194119_a();
      this.field_191888_F.field_71439_g.field_71071_by.func_201571_a(this.field_193965_u);
      this.field_201522_g.func_201771_a(this.field_193965_u);
      String var4 = this.field_193962_q != null ? this.field_193962_q.func_146179_b() : "";
      this.field_193962_q = new GuiTextField(0, this.field_191888_F.field_71466_p, var2 + 25, var3 + 14, 80, this.field_191888_F.field_71466_p.field_78288_b + 5);
      this.field_193962_q.func_146203_f(50);
      this.field_193962_q.func_146185_a(false);
      this.field_193962_q.func_146189_e(true);
      this.field_193962_q.func_146193_g(16777215);
      this.field_193962_q.func_146180_a(var4);
      this.field_193022_s.func_194194_a(this.field_191888_F, var2, var3);
      this.field_193022_s.func_193732_a(this);
      this.field_193960_m = new GuiButtonToggle(0, var2 + 110, var3 + 12, 26, 16, this.field_193964_s.func_203432_a(this.field_201522_g));
      this.func_205702_a();
      this.field_193018_j.clear();
      Iterator var5 = RecipeBookClient.func_202888_a(this.field_201522_g).iterator();

      while(var5.hasNext()) {
         RecipeBookCategories var6 = (RecipeBookCategories)var5.next();
         this.field_193018_j.add(new GuiButtonRecipeTab(0, var6));
      }

      if (this.field_191913_x != null) {
         this.field_191913_x = (GuiButtonRecipeTab)this.field_193018_j.stream().filter((var1x) -> {
            return var1x.func_201503_d().equals(this.field_191913_x.func_201503_d());
         }).findFirst().orElse((Object)null);
      }

      if (this.field_191913_x == null) {
         this.field_191913_x = (GuiButtonRecipeTab)this.field_193018_j.get(0);
      }

      this.field_191913_x.func_191753_b(true);
      this.func_193003_g(false);
      this.func_193949_f();
   }

   protected void func_205702_a() {
      this.field_193960_m.func_191751_a(152, 41, 28, 18, field_191894_a);
   }

   public void func_191871_c() {
      this.field_193962_q = null;
      this.field_191913_x = null;
      this.field_191888_F.field_195559_v.func_197967_a(false);
   }

   public int func_193011_a(boolean var1, int var2, int var3) {
      int var4;
      if (this.func_191878_b() && !var1) {
         var4 = 177 + (var2 - var3 - 200) / 2;
      } else {
         var4 = (var2 - var3) / 2;
      }

      return var4;
   }

   public void func_191866_a() {
      this.func_193006_a(!this.func_191878_b());
   }

   public boolean func_191878_b() {
      return this.field_193964_s.func_192812_b();
   }

   protected void func_193006_a(boolean var1) {
      this.field_193964_s.func_192813_a(var1);
      if (!var1) {
         this.field_193022_s.func_194200_c();
      }

      this.func_193956_j();
   }

   public void func_191874_a(@Nullable Slot var1) {
      if (var1 != null && var1.field_75222_d < this.field_201522_g.func_203721_h()) {
         this.field_191915_z.func_192682_a();
         if (this.func_191878_b()) {
            this.func_193942_g();
         }
      }

   }

   private void func_193003_g(boolean var1) {
      List var2 = this.field_193964_s.func_202891_a(this.field_191913_x.func_201503_d());
      var2.forEach((var1x) -> {
         var1x.func_194210_a(this.field_193965_u, this.field_201522_g.func_201770_g(), this.field_201522_g.func_201772_h(), this.field_193964_s);
      });
      ArrayList var3 = Lists.newArrayList(var2);
      var3.removeIf((var0) -> {
         return !var0.func_194209_a();
      });
      var3.removeIf((var0) -> {
         return !var0.func_194212_c();
      });
      String var4 = this.field_193962_q.func_146179_b();
      if (!var4.isEmpty()) {
         ObjectLinkedOpenHashSet var5 = new ObjectLinkedOpenHashSet(this.field_191888_F.func_193987_a(SearchTreeManager.field_194012_b).func_194038_a(var4.toLowerCase(Locale.ROOT)));
         var3.removeIf((var1x) -> {
            return !var5.contains(var1x);
         });
      }

      if (this.field_193964_s.func_203432_a(this.field_201522_g)) {
         var3.removeIf((var0) -> {
            return !var0.func_192708_c();
         });
      }

      this.field_193022_s.func_194192_a(var3, var1);
   }

   private void func_193949_f() {
      int var1 = (this.field_191904_o - 147) / 2 - this.field_191903_n - 30;
      int var2 = (this.field_191905_p - 166) / 2 + 3;
      boolean var3 = true;
      int var4 = 0;
      Iterator var5 = this.field_193018_j.iterator();

      while(true) {
         while(var5.hasNext()) {
            GuiButtonRecipeTab var6 = (GuiButtonRecipeTab)var5.next();
            RecipeBookCategories var7 = var6.func_201503_d();
            if (var7 != RecipeBookCategories.SEARCH && var7 != RecipeBookCategories.FURNACE_SEARCH) {
               if (var6.func_199500_a(this.field_193964_s)) {
                  var6.func_191752_c(var1, var2 + 27 * var4++);
                  var6.func_193918_a(this.field_191888_F);
               }
            } else {
               var6.field_146125_m = true;
               var6.func_191752_c(var1, var2 + 27 * var4++);
            }
         }

         return;
      }
   }

   public void func_193957_d() {
      if (this.func_191878_b()) {
         if (this.field_193966_v != this.field_191888_F.field_71439_g.field_71071_by.func_194015_p()) {
            this.func_193942_g();
            this.field_193966_v = this.field_191888_F.field_71439_g.field_71071_by.func_194015_p();
         }

      }
   }

   private void func_193942_g() {
      this.field_193965_u.func_194119_a();
      this.field_191888_F.field_71439_g.field_71071_by.func_201571_a(this.field_193965_u);
      this.field_201522_g.func_201771_a(this.field_193965_u);
      this.func_193003_g(false);
   }

   public void func_191861_a(int var1, int var2, float var3) {
      if (this.func_191878_b()) {
         RenderHelper.func_74520_c();
         GlStateManager.func_179140_f();
         GlStateManager.func_179094_E();
         GlStateManager.func_179109_b(0.0F, 0.0F, 100.0F);
         this.field_191888_F.func_110434_K().func_110577_a(field_191894_a);
         GlStateManager.func_179131_c(1.0F, 1.0F, 1.0F, 1.0F);
         int var4 = (this.field_191904_o - 147) / 2 - this.field_191903_n;
         int var5 = (this.field_191905_p - 166) / 2;
         this.func_73729_b(var4, var5, 1, 1, 147, 166);
         this.field_193962_q.func_195608_a(var1, var2, var3);
         RenderHelper.func_74518_a();
         Iterator var6 = this.field_193018_j.iterator();

         while(var6.hasNext()) {
            GuiButtonRecipeTab var7 = (GuiButtonRecipeTab)var6.next();
            var7.func_194828_a(var1, var2, var3);
         }

         this.field_193960_m.func_194828_a(var1, var2, var3);
         this.field_193022_s.func_194191_a(var4, var5, var1, var2, var3);
         GlStateManager.func_179121_F();
      }
   }

   public void func_191876_c(int var1, int var2, int var3, int var4) {
      if (this.func_191878_b()) {
         this.field_193022_s.func_193721_a(var3, var4);
         if (this.field_193960_m.func_146115_a()) {
            String var5 = this.func_205703_f();
            if (this.field_191888_F.field_71462_r != null) {
               this.field_191888_F.field_71462_r.func_146279_a(var5, var3, var4);
            }
         }

         this.func_193015_d(var1, var2, var3, var4);
      }
   }

   protected String func_205703_f() {
      return I18n.func_135052_a(this.field_193960_m.func_191754_c() ? "gui.recipebook.toggleRecipes.craftable" : "gui.recipebook.toggleRecipes.all");
   }

   private void func_193015_d(int var1, int var2, int var3, int var4) {
      ItemStack var5 = null;

      for(int var6 = 0; var6 < this.field_191915_z.func_192684_b(); ++var6) {
         GhostRecipe.GhostIngredient var7 = this.field_191915_z.func_192681_a(var6);
         int var8 = var7.func_193713_b() + var1;
         int var9 = var7.func_193712_c() + var2;
         if (var3 >= var8 && var4 >= var9 && var3 < var8 + 16 && var4 < var9 + 16) {
            var5 = var7.func_194184_c();
         }
      }

      if (var5 != null && this.field_191888_F.field_71462_r != null) {
         this.field_191888_F.field_71462_r.func_146283_a(this.field_191888_F.field_71462_r.func_191927_a(var5), var3, var4);
      }

   }

   public void func_191864_a(int var1, int var2, boolean var3, float var4) {
      this.field_191915_z.func_194188_a(this.field_191888_F, var1, var2, var3, var4);
   }

   public boolean mouseClicked(double var1, double var3, int var5) {
      if (this.func_191878_b() && !this.field_191888_F.field_71439_g.func_175149_v()) {
         if (this.field_193022_s.func_198955_a(var1, var3, var5, (this.field_191904_o - 147) / 2 - this.field_191903_n, (this.field_191905_p - 166) / 2, 147, 166)) {
            IRecipe var9 = this.field_193022_s.func_194193_a();
            RecipeList var10 = this.field_193022_s.func_194199_b();
            if (var9 != null && var10 != null) {
               if (!var10.func_194213_a(var9) && this.field_191915_z.func_192686_c() == var9) {
                  return false;
               }

               this.field_191915_z.func_192682_a();
               this.field_191888_F.field_71442_b.func_203413_a(this.field_191888_F.field_71439_g.field_71070_bA.field_75152_c, var9, GuiScreen.func_146272_n());
               if (!this.func_191880_f()) {
                  this.func_193006_a(false);
               }
            }

            return true;
         } else if (this.field_193962_q.mouseClicked(var1, var3, var5)) {
            return true;
         } else if (this.field_193960_m.mouseClicked(var1, var3, var5)) {
            boolean var8 = this.func_201521_f();
            this.field_193960_m.func_191753_b(var8);
            this.func_193956_j();
            this.func_193003_g(false);
            return true;
         } else {
            Iterator var6 = this.field_193018_j.iterator();

            GuiButtonRecipeTab var7;
            do {
               if (!var6.hasNext()) {
                  return false;
               }

               var7 = (GuiButtonRecipeTab)var6.next();
            } while(!var7.mouseClicked(var1, var3, var5));

            if (this.field_191913_x != var7) {
               this.field_191913_x.func_191753_b(false);
               this.field_191913_x = var7;
               this.field_191913_x.func_191753_b(true);
               this.func_193003_g(true);
            }

            return true;
         }
      } else {
         return false;
      }
   }

   protected boolean func_201521_f() {
      boolean var1 = !this.field_193964_s.func_192815_c();
      this.field_193964_s.func_192810_b(var1);
      return var1;
   }

   public boolean func_195604_a(double var1, double var3, int var5, int var6, int var7, int var8, int var9) {
      if (!this.func_191878_b()) {
         return true;
      } else {
         boolean var10 = var1 < (double)var5 || var3 < (double)var6 || var1 >= (double)(var5 + var7) || var3 >= (double)(var6 + var8);
         boolean var11 = (double)(var5 - 147) < var1 && var1 < (double)var5 && (double)var6 < var3 && var3 < (double)(var6 + var8);
         return var10 && !var11 && !this.field_191913_x.func_146115_a();
      }
   }

   public boolean keyPressed(int var1, int var2, int var3) {
      this.field_199738_u = false;
      if (this.func_191878_b() && !this.field_191888_F.field_71439_g.func_175149_v()) {
         if (var1 == 256 && !this.func_191880_f()) {
            this.func_193006_a(false);
            return true;
         } else if (this.field_193962_q.keyPressed(var1, var2, var3)) {
            this.func_195603_h();
            return true;
         } else if (this.field_191888_F.field_71474_y.field_74310_D.func_197976_a(var1, var2) && !this.field_193962_q.func_146206_l()) {
            this.field_199738_u = true;
            this.field_193962_q.func_146195_b(true);
            return true;
         } else {
            return false;
         }
      } else {
         return false;
      }
   }

   public boolean keyReleased(int var1, int var2, int var3) {
      this.field_199738_u = false;
      return IGuiEventListener.super.keyReleased(var1, var2, var3);
   }

   public boolean charTyped(char var1, int var2) {
      if (this.field_199738_u) {
         return false;
      } else if (this.func_191878_b() && !this.field_191888_F.field_71439_g.func_175149_v()) {
         if (this.field_193962_q.charTyped(var1, var2)) {
            this.func_195603_h();
            return true;
         } else {
            return IGuiEventListener.super.charTyped(var1, var2);
         }
      } else {
         return false;
      }
   }

   private void func_195603_h() {
      String var1 = this.field_193962_q.func_146179_b().toLowerCase(Locale.ROOT);
      this.func_193716_a(var1);
      if (!var1.equals(this.field_193963_r)) {
         this.func_193003_g(false);
         this.field_193963_r = var1;
      }

   }

   private void func_193716_a(String var1) {
      if ("excitedze".equals(var1)) {
         LanguageManager var2 = this.field_191888_F.func_135016_M();
         Language var3 = var2.func_191960_a("en_pt");
         if (var2.func_135041_c().compareTo(var3) == 0) {
            return;
         }

         var2.func_135045_a(var3);
         this.field_191888_F.field_71474_y.field_74363_ab = var3.func_135034_a();
         this.field_191888_F.func_110436_a();
         this.field_191888_F.field_71466_p.func_78275_b(var2.func_135044_b());
         this.field_191888_F.field_71474_y.func_74303_b();
      }

   }

   private boolean func_191880_f() {
      return this.field_191903_n == 86;
   }

   public void func_193948_e() {
      this.func_193949_f();
      if (this.func_191878_b()) {
         this.func_193003_g(false);
      }

   }

   public void func_193001_a(List<IRecipe> var1) {
      Iterator var2 = var1.iterator();

      while(var2.hasNext()) {
         IRecipe var3 = (IRecipe)var2.next();
         this.field_191888_F.field_71439_g.func_193103_a(var3);
      }

   }

   public void func_193951_a(IRecipe var1, List<Slot> var2) {
      ItemStack var3 = var1.func_77571_b();
      this.field_191915_z.func_192685_a(var1);
      this.field_191915_z.func_194187_a(Ingredient.func_193369_a(var3), ((Slot)var2.get(0)).field_75223_e, ((Slot)var2.get(0)).field_75221_f);
      this.func_201501_a(this.field_201522_g.func_201770_g(), this.field_201522_g.func_201772_h(), this.field_201522_g.func_201767_f(), var1, var1.func_192400_c().iterator(), 0);
   }

   public void func_201500_a(Iterator<Ingredient> var1, int var2, int var3, int var4, int var5) {
      Ingredient var6 = (Ingredient)var1.next();
      if (!var6.func_203189_d()) {
         Slot var7 = (Slot)this.field_201522_g.field_75151_b.get(var2);
         this.field_191915_z.func_194187_a(var6, var7.field_75223_e, var7.field_75221_f);
      }

   }

   protected void func_193956_j() {
      if (this.field_191888_F.func_147114_u() != null) {
         this.field_191888_F.func_147114_u().func_147297_a(new CPacketRecipeInfo(this.field_193964_s.func_192812_b(), this.field_193964_s.func_192815_c(), this.field_193964_s.func_202883_c(), this.field_193964_s.func_202884_d()));
      }

   }
}
