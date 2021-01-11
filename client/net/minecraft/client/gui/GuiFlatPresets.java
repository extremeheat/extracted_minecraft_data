package net.minecraft.client.gui;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import net.minecraft.block.BlockTallGrass;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.WorldRenderer;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.client.resources.I18n;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.world.biome.BiomeGenBase;
import net.minecraft.world.gen.FlatGeneratorInfo;
import net.minecraft.world.gen.FlatLayerInfo;
import org.lwjgl.input.Keyboard;

public class GuiFlatPresets extends GuiScreen {
   private static final List<GuiFlatPresets.LayerItem> field_146431_f = Lists.newArrayList();
   private final GuiCreateFlatWorld field_146432_g;
   private String field_146438_h;
   private String field_146439_i;
   private String field_146436_r;
   private GuiFlatPresets.ListSlot field_146435_s;
   private GuiButton field_146434_t;
   private GuiTextField field_146433_u;

   public GuiFlatPresets(GuiCreateFlatWorld var1) {
      super();
      this.field_146432_g = var1;
   }

   public void func_73866_w_() {
      this.field_146292_n.clear();
      Keyboard.enableRepeatEvents(true);
      this.field_146438_h = I18n.func_135052_a("createWorld.customize.presets.title");
      this.field_146439_i = I18n.func_135052_a("createWorld.customize.presets.share");
      this.field_146436_r = I18n.func_135052_a("createWorld.customize.presets.list");
      this.field_146433_u = new GuiTextField(2, this.field_146289_q, 50, 40, this.field_146294_l - 100, 20);
      this.field_146435_s = new GuiFlatPresets.ListSlot();
      this.field_146433_u.func_146203_f(1230);
      this.field_146433_u.func_146180_a(this.field_146432_g.func_146384_e());
      this.field_146292_n.add(this.field_146434_t = new GuiButton(0, this.field_146294_l / 2 - 155, this.field_146295_m - 28, 150, 20, I18n.func_135052_a("createWorld.customize.presets.select")));
      this.field_146292_n.add(new GuiButton(1, this.field_146294_l / 2 + 5, this.field_146295_m - 28, 150, 20, I18n.func_135052_a("gui.cancel")));
      this.func_146426_g();
   }

   public void func_146274_d() {
      super.func_146274_d();
      this.field_146435_s.func_178039_p();
   }

   public void func_146281_b() {
      Keyboard.enableRepeatEvents(false);
   }

   protected void func_73864_a(int var1, int var2, int var3) {
      this.field_146433_u.func_146192_a(var1, var2, var3);
      super.func_73864_a(var1, var2, var3);
   }

   protected void func_73869_a(char var1, int var2) {
      if (!this.field_146433_u.func_146201_a(var1, var2)) {
         super.func_73869_a(var1, var2);
      }

   }

   protected void func_146284_a(GuiButton var1) {
      if (var1.field_146127_k == 0 && this.func_146430_p()) {
         this.field_146432_g.func_146383_a(this.field_146433_u.func_146179_b());
         this.field_146297_k.func_147108_a(this.field_146432_g);
      } else if (var1.field_146127_k == 1) {
         this.field_146297_k.func_147108_a(this.field_146432_g);
      }

   }

   public void func_73863_a(int var1, int var2, float var3) {
      this.func_146276_q_();
      this.field_146435_s.func_148128_a(var1, var2, var3);
      this.func_73732_a(this.field_146289_q, this.field_146438_h, this.field_146294_l / 2, 8, 16777215);
      this.func_73731_b(this.field_146289_q, this.field_146439_i, 50, 30, 10526880);
      this.func_73731_b(this.field_146289_q, this.field_146436_r, 50, 70, 10526880);
      this.field_146433_u.func_146194_f();
      super.func_73863_a(var1, var2, var3);
   }

   public void func_73876_c() {
      this.field_146433_u.func_146178_a();
      super.func_73876_c();
   }

   public void func_146426_g() {
      boolean var1 = this.func_146430_p();
      this.field_146434_t.field_146124_l = var1;
   }

   private boolean func_146430_p() {
      return this.field_146435_s.field_148175_k > -1 && this.field_146435_s.field_148175_k < field_146431_f.size() || this.field_146433_u.func_146179_b().length() > 1;
   }

   private static void func_146425_a(String var0, Item var1, BiomeGenBase var2, FlatLayerInfo... var3) {
      func_175354_a(var0, var1, 0, var2, (List)null, var3);
   }

   private static void func_146421_a(String var0, Item var1, BiomeGenBase var2, List<String> var3, FlatLayerInfo... var4) {
      func_175354_a(var0, var1, 0, var2, var3, var4);
   }

   private static void func_175354_a(String var0, Item var1, int var2, BiomeGenBase var3, List<String> var4, FlatLayerInfo... var5) {
      FlatGeneratorInfo var6 = new FlatGeneratorInfo();

      for(int var7 = var5.length - 1; var7 >= 0; --var7) {
         var6.func_82650_c().add(var5[var7]);
      }

      var6.func_82647_a(var3.field_76756_M);
      var6.func_82645_d();
      if (var4 != null) {
         Iterator var9 = var4.iterator();

         while(var9.hasNext()) {
            String var8 = (String)var9.next();
            var6.func_82644_b().put(var8, Maps.newHashMap());
         }
      }

      field_146431_f.add(new GuiFlatPresets.LayerItem(var1, var2, var0, var6.toString()));
   }

   static {
      func_146421_a("Classic Flat", Item.func_150898_a(Blocks.field_150349_c), BiomeGenBase.field_76772_c, Arrays.asList("village"), new FlatLayerInfo(1, Blocks.field_150349_c), new FlatLayerInfo(2, Blocks.field_150346_d), new FlatLayerInfo(1, Blocks.field_150357_h));
      func_146421_a("Tunnelers' Dream", Item.func_150898_a(Blocks.field_150348_b), BiomeGenBase.field_76770_e, Arrays.asList("biome_1", "dungeon", "decoration", "stronghold", "mineshaft"), new FlatLayerInfo(1, Blocks.field_150349_c), new FlatLayerInfo(5, Blocks.field_150346_d), new FlatLayerInfo(230, Blocks.field_150348_b), new FlatLayerInfo(1, Blocks.field_150357_h));
      func_146421_a("Water World", Items.field_151131_as, BiomeGenBase.field_150575_M, Arrays.asList("biome_1", "oceanmonument"), new FlatLayerInfo(90, Blocks.field_150355_j), new FlatLayerInfo(5, Blocks.field_150354_m), new FlatLayerInfo(5, Blocks.field_150346_d), new FlatLayerInfo(5, Blocks.field_150348_b), new FlatLayerInfo(1, Blocks.field_150357_h));
      func_175354_a("Overworld", Item.func_150898_a(Blocks.field_150329_H), BlockTallGrass.EnumType.GRASS.func_177044_a(), BiomeGenBase.field_76772_c, Arrays.asList("village", "biome_1", "decoration", "stronghold", "mineshaft", "dungeon", "lake", "lava_lake"), new FlatLayerInfo(1, Blocks.field_150349_c), new FlatLayerInfo(3, Blocks.field_150346_d), new FlatLayerInfo(59, Blocks.field_150348_b), new FlatLayerInfo(1, Blocks.field_150357_h));
      func_146421_a("Snowy Kingdom", Item.func_150898_a(Blocks.field_150431_aC), BiomeGenBase.field_76774_n, Arrays.asList("village", "biome_1"), new FlatLayerInfo(1, Blocks.field_150431_aC), new FlatLayerInfo(1, Blocks.field_150349_c), new FlatLayerInfo(3, Blocks.field_150346_d), new FlatLayerInfo(59, Blocks.field_150348_b), new FlatLayerInfo(1, Blocks.field_150357_h));
      func_146421_a("Bottomless Pit", Items.field_151008_G, BiomeGenBase.field_76772_c, Arrays.asList("village", "biome_1"), new FlatLayerInfo(1, Blocks.field_150349_c), new FlatLayerInfo(3, Blocks.field_150346_d), new FlatLayerInfo(2, Blocks.field_150347_e));
      func_146421_a("Desert", Item.func_150898_a(Blocks.field_150354_m), BiomeGenBase.field_76769_d, Arrays.asList("village", "biome_1", "decoration", "stronghold", "mineshaft", "dungeon"), new FlatLayerInfo(8, Blocks.field_150354_m), new FlatLayerInfo(52, Blocks.field_150322_A), new FlatLayerInfo(3, Blocks.field_150348_b), new FlatLayerInfo(1, Blocks.field_150357_h));
      func_146425_a("Redstone Ready", Items.field_151137_ax, BiomeGenBase.field_76769_d, new FlatLayerInfo(52, Blocks.field_150322_A), new FlatLayerInfo(3, Blocks.field_150348_b), new FlatLayerInfo(1, Blocks.field_150357_h));
   }

   static class LayerItem {
      public Item field_148234_a;
      public int field_179037_b;
      public String field_148232_b;
      public String field_148233_c;

      public LayerItem(Item var1, int var2, String var3, String var4) {
         super();
         this.field_148234_a = var1;
         this.field_179037_b = var2;
         this.field_148232_b = var3;
         this.field_148233_c = var4;
      }
   }

   class ListSlot extends GuiSlot {
      public int field_148175_k = -1;

      public ListSlot() {
         super(GuiFlatPresets.this.field_146297_k, GuiFlatPresets.this.field_146294_l, GuiFlatPresets.this.field_146295_m, 80, GuiFlatPresets.this.field_146295_m - 37, 24);
      }

      private void func_178054_a(int var1, int var2, Item var3, int var4) {
         this.func_148173_e(var1 + 1, var2 + 1);
         GlStateManager.func_179091_B();
         RenderHelper.func_74520_c();
         GuiFlatPresets.this.field_146296_j.func_175042_a(new ItemStack(var3, 1, var4), var1 + 2, var2 + 2);
         RenderHelper.func_74518_a();
         GlStateManager.func_179101_C();
      }

      private void func_148173_e(int var1, int var2) {
         this.func_148171_c(var1, var2, 0, 0);
      }

      private void func_148171_c(int var1, int var2, int var3, int var4) {
         GlStateManager.func_179131_c(1.0F, 1.0F, 1.0F, 1.0F);
         this.field_148161_k.func_110434_K().func_110577_a(Gui.field_110323_l);
         float var5 = 0.0078125F;
         float var6 = 0.0078125F;
         boolean var7 = true;
         boolean var8 = true;
         Tessellator var9 = Tessellator.func_178181_a();
         WorldRenderer var10 = var9.func_178180_c();
         var10.func_181668_a(7, DefaultVertexFormats.field_181707_g);
         var10.func_181662_b((double)(var1 + 0), (double)(var2 + 18), (double)GuiFlatPresets.this.field_73735_i).func_181673_a((double)((float)(var3 + 0) * 0.0078125F), (double)((float)(var4 + 18) * 0.0078125F)).func_181675_d();
         var10.func_181662_b((double)(var1 + 18), (double)(var2 + 18), (double)GuiFlatPresets.this.field_73735_i).func_181673_a((double)((float)(var3 + 18) * 0.0078125F), (double)((float)(var4 + 18) * 0.0078125F)).func_181675_d();
         var10.func_181662_b((double)(var1 + 18), (double)(var2 + 0), (double)GuiFlatPresets.this.field_73735_i).func_181673_a((double)((float)(var3 + 18) * 0.0078125F), (double)((float)(var4 + 0) * 0.0078125F)).func_181675_d();
         var10.func_181662_b((double)(var1 + 0), (double)(var2 + 0), (double)GuiFlatPresets.this.field_73735_i).func_181673_a((double)((float)(var3 + 0) * 0.0078125F), (double)((float)(var4 + 0) * 0.0078125F)).func_181675_d();
         var9.func_78381_a();
      }

      protected int func_148127_b() {
         return GuiFlatPresets.field_146431_f.size();
      }

      protected void func_148144_a(int var1, boolean var2, int var3, int var4) {
         this.field_148175_k = var1;
         GuiFlatPresets.this.func_146426_g();
         GuiFlatPresets.this.field_146433_u.func_146180_a(((GuiFlatPresets.LayerItem)GuiFlatPresets.field_146431_f.get(GuiFlatPresets.this.field_146435_s.field_148175_k)).field_148233_c);
      }

      protected boolean func_148131_a(int var1) {
         return var1 == this.field_148175_k;
      }

      protected void func_148123_a() {
      }

      protected void func_180791_a(int var1, int var2, int var3, int var4, int var5, int var6) {
         GuiFlatPresets.LayerItem var7 = (GuiFlatPresets.LayerItem)GuiFlatPresets.field_146431_f.get(var1);
         this.func_178054_a(var2, var3, var7.field_148234_a, var7.field_179037_b);
         GuiFlatPresets.this.field_146289_q.func_78276_b(var7.field_148232_b, var2 + 18 + 5, var3 + 6, 16777215);
      }
   }
}
