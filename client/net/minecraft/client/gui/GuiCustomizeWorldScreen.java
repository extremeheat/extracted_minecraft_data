package net.minecraft.client.gui;

import com.google.common.base.Predicate;
import com.google.common.primitives.Floats;
import java.util.Random;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.WorldRenderer;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.client.resources.I18n;
import net.minecraft.util.MathHelper;
import net.minecraft.world.biome.BiomeGenBase;
import net.minecraft.world.gen.ChunkProviderSettings;

public class GuiCustomizeWorldScreen extends GuiScreen implements GuiSlider.FormatHelper, GuiPageButtonList.GuiResponder {
   private GuiCreateWorld field_175343_i;
   protected String field_175341_a = "Customize World Settings";
   protected String field_175333_f = "Page 1 of 3";
   protected String field_175335_g = "Basic Settings";
   protected String[] field_175342_h = new String[4];
   private GuiPageButtonList field_175349_r;
   private GuiButton field_175348_s;
   private GuiButton field_175347_t;
   private GuiButton field_175346_u;
   private GuiButton field_175345_v;
   private GuiButton field_175344_w;
   private GuiButton field_175352_x;
   private GuiButton field_175351_y;
   private GuiButton field_175350_z;
   private boolean field_175338_A = false;
   private int field_175339_B = 0;
   private boolean field_175340_C = false;
   private Predicate<String> field_175332_D = new Predicate<String>() {
      public boolean apply(String var1) {
         Float var2 = Floats.tryParse(var1);
         return var1.length() == 0 || var2 != null && Floats.isFinite(var2) && var2 >= 0.0F;
      }

      // $FF: synthetic method
      public boolean apply(Object var1) {
         return this.apply((String)var1);
      }
   };
   private ChunkProviderSettings.Factory field_175334_E = new ChunkProviderSettings.Factory();
   private ChunkProviderSettings.Factory field_175336_F;
   private Random field_175337_G = new Random();

   public GuiCustomizeWorldScreen(GuiScreen var1, String var2) {
      super();
      this.field_175343_i = (GuiCreateWorld)var1;
      this.func_175324_a(var2);
   }

   public void func_73866_w_() {
      int var1 = 0;
      int var2 = 0;
      if (this.field_175349_r != null) {
         var1 = this.field_175349_r.func_178059_e();
         var2 = this.field_175349_r.func_148148_g();
      }

      this.field_175341_a = I18n.func_135052_a("options.customizeTitle");
      this.field_146292_n.clear();
      this.field_146292_n.add(this.field_175345_v = new GuiButton(302, 20, 5, 80, 20, I18n.func_135052_a("createWorld.customize.custom.prev")));
      this.field_146292_n.add(this.field_175344_w = new GuiButton(303, this.field_146294_l - 100, 5, 80, 20, I18n.func_135052_a("createWorld.customize.custom.next")));
      this.field_146292_n.add(this.field_175346_u = new GuiButton(304, this.field_146294_l / 2 - 187, this.field_146295_m - 27, 90, 20, I18n.func_135052_a("createWorld.customize.custom.defaults")));
      this.field_146292_n.add(this.field_175347_t = new GuiButton(301, this.field_146294_l / 2 - 92, this.field_146295_m - 27, 90, 20, I18n.func_135052_a("createWorld.customize.custom.randomize")));
      this.field_146292_n.add(this.field_175350_z = new GuiButton(305, this.field_146294_l / 2 + 3, this.field_146295_m - 27, 90, 20, I18n.func_135052_a("createWorld.customize.custom.presets")));
      this.field_146292_n.add(this.field_175348_s = new GuiButton(300, this.field_146294_l / 2 + 98, this.field_146295_m - 27, 90, 20, I18n.func_135052_a("gui.done")));
      this.field_175346_u.field_146124_l = this.field_175338_A;
      this.field_175352_x = new GuiButton(306, this.field_146294_l / 2 - 55, 160, 50, 20, I18n.func_135052_a("gui.yes"));
      this.field_175352_x.field_146125_m = false;
      this.field_146292_n.add(this.field_175352_x);
      this.field_175351_y = new GuiButton(307, this.field_146294_l / 2 + 5, 160, 50, 20, I18n.func_135052_a("gui.no"));
      this.field_175351_y.field_146125_m = false;
      this.field_146292_n.add(this.field_175351_y);
      if (this.field_175339_B != 0) {
         this.field_175352_x.field_146125_m = true;
         this.field_175351_y.field_146125_m = true;
      }

      this.func_175325_f();
      if (var1 != 0) {
         this.field_175349_r.func_181156_c(var1);
         this.field_175349_r.func_148145_f(var2);
         this.func_175328_i();
      }

   }

   public void func_146274_d() {
      super.func_146274_d();
      this.field_175349_r.func_178039_p();
   }

   private void func_175325_f() {
      GuiPageButtonList.GuiListEntry[] var1 = new GuiPageButtonList.GuiListEntry[]{new GuiPageButtonList.GuiSlideEntry(160, I18n.func_135052_a("createWorld.customize.custom.seaLevel"), true, this, 1.0F, 255.0F, (float)this.field_175336_F.field_177929_r), new GuiPageButtonList.GuiButtonEntry(148, I18n.func_135052_a("createWorld.customize.custom.useCaves"), true, this.field_175336_F.field_177927_s), new GuiPageButtonList.GuiButtonEntry(150, I18n.func_135052_a("createWorld.customize.custom.useStrongholds"), true, this.field_175336_F.field_177921_v), new GuiPageButtonList.GuiButtonEntry(151, I18n.func_135052_a("createWorld.customize.custom.useVillages"), true, this.field_175336_F.field_177919_w), new GuiPageButtonList.GuiButtonEntry(152, I18n.func_135052_a("createWorld.customize.custom.useMineShafts"), true, this.field_175336_F.field_177944_x), new GuiPageButtonList.GuiButtonEntry(153, I18n.func_135052_a("createWorld.customize.custom.useTemples"), true, this.field_175336_F.field_177942_y), new GuiPageButtonList.GuiButtonEntry(210, I18n.func_135052_a("createWorld.customize.custom.useMonuments"), true, this.field_175336_F.field_177940_z), new GuiPageButtonList.GuiButtonEntry(154, I18n.func_135052_a("createWorld.customize.custom.useRavines"), true, this.field_175336_F.field_177870_A), new GuiPageButtonList.GuiButtonEntry(149, I18n.func_135052_a("createWorld.customize.custom.useDungeons"), true, this.field_175336_F.field_177925_t), new GuiPageButtonList.GuiSlideEntry(157, I18n.func_135052_a("createWorld.customize.custom.dungeonChance"), true, this, 1.0F, 100.0F, (float)this.field_175336_F.field_177923_u), new GuiPageButtonList.GuiButtonEntry(155, I18n.func_135052_a("createWorld.customize.custom.useWaterLakes"), true, this.field_175336_F.field_177871_B), new GuiPageButtonList.GuiSlideEntry(158, I18n.func_135052_a("createWorld.customize.custom.waterLakeChance"), true, this, 1.0F, 100.0F, (float)this.field_175336_F.field_177872_C), new GuiPageButtonList.GuiButtonEntry(156, I18n.func_135052_a("createWorld.customize.custom.useLavaLakes"), true, this.field_175336_F.field_177866_D), new GuiPageButtonList.GuiSlideEntry(159, I18n.func_135052_a("createWorld.customize.custom.lavaLakeChance"), true, this, 10.0F, 100.0F, (float)this.field_175336_F.field_177867_E), new GuiPageButtonList.GuiButtonEntry(161, I18n.func_135052_a("createWorld.customize.custom.useLavaOceans"), true, this.field_175336_F.field_177868_F), new GuiPageButtonList.GuiSlideEntry(162, I18n.func_135052_a("createWorld.customize.custom.fixedBiome"), true, this, -1.0F, 37.0F, (float)this.field_175336_F.field_177869_G), new GuiPageButtonList.GuiSlideEntry(163, I18n.func_135052_a("createWorld.customize.custom.biomeSize"), true, this, 1.0F, 8.0F, (float)this.field_175336_F.field_177877_H), new GuiPageButtonList.GuiSlideEntry(164, I18n.func_135052_a("createWorld.customize.custom.riverSize"), true, this, 1.0F, 5.0F, (float)this.field_175336_F.field_177878_I)};
      GuiPageButtonList.GuiListEntry[] var2 = new GuiPageButtonList.GuiListEntry[]{new GuiPageButtonList.GuiLabelEntry(416, I18n.func_135052_a("tile.dirt.name"), false), null, new GuiPageButtonList.GuiSlideEntry(165, I18n.func_135052_a("createWorld.customize.custom.size"), false, this, 1.0F, 50.0F, (float)this.field_175336_F.field_177879_J), new GuiPageButtonList.GuiSlideEntry(166, I18n.func_135052_a("createWorld.customize.custom.count"), false, this, 0.0F, 40.0F, (float)this.field_175336_F.field_177880_K), new GuiPageButtonList.GuiSlideEntry(167, I18n.func_135052_a("createWorld.customize.custom.minHeight"), false, this, 0.0F, 255.0F, (float)this.field_175336_F.field_177873_L), new GuiPageButtonList.GuiSlideEntry(168, I18n.func_135052_a("createWorld.customize.custom.maxHeight"), false, this, 0.0F, 255.0F, (float)this.field_175336_F.field_177874_M), new GuiPageButtonList.GuiLabelEntry(417, I18n.func_135052_a("tile.gravel.name"), false), null, new GuiPageButtonList.GuiSlideEntry(169, I18n.func_135052_a("createWorld.customize.custom.size"), false, this, 1.0F, 50.0F, (float)this.field_175336_F.field_177875_N), new GuiPageButtonList.GuiSlideEntry(170, I18n.func_135052_a("createWorld.customize.custom.count"), false, this, 0.0F, 40.0F, (float)this.field_175336_F.field_177876_O), new GuiPageButtonList.GuiSlideEntry(171, I18n.func_135052_a("createWorld.customize.custom.minHeight"), false, this, 0.0F, 255.0F, (float)this.field_175336_F.field_177886_P), new GuiPageButtonList.GuiSlideEntry(172, I18n.func_135052_a("createWorld.customize.custom.maxHeight"), false, this, 0.0F, 255.0F, (float)this.field_175336_F.field_177885_Q), new GuiPageButtonList.GuiLabelEntry(418, I18n.func_135052_a("tile.stone.granite.name"), false), null, new GuiPageButtonList.GuiSlideEntry(173, I18n.func_135052_a("createWorld.customize.custom.size"), false, this, 1.0F, 50.0F, (float)this.field_175336_F.field_177888_R), new GuiPageButtonList.GuiSlideEntry(174, I18n.func_135052_a("createWorld.customize.custom.count"), false, this, 0.0F, 40.0F, (float)this.field_175336_F.field_177887_S), new GuiPageButtonList.GuiSlideEntry(175, I18n.func_135052_a("createWorld.customize.custom.minHeight"), false, this, 0.0F, 255.0F, (float)this.field_175336_F.field_177882_T), new GuiPageButtonList.GuiSlideEntry(176, I18n.func_135052_a("createWorld.customize.custom.maxHeight"), false, this, 0.0F, 255.0F, (float)this.field_175336_F.field_177881_U), new GuiPageButtonList.GuiLabelEntry(419, I18n.func_135052_a("tile.stone.diorite.name"), false), null, new GuiPageButtonList.GuiSlideEntry(177, I18n.func_135052_a("createWorld.customize.custom.size"), false, this, 1.0F, 50.0F, (float)this.field_175336_F.field_177884_V), new GuiPageButtonList.GuiSlideEntry(178, I18n.func_135052_a("createWorld.customize.custom.count"), false, this, 0.0F, 40.0F, (float)this.field_175336_F.field_177883_W), new GuiPageButtonList.GuiSlideEntry(179, I18n.func_135052_a("createWorld.customize.custom.minHeight"), false, this, 0.0F, 255.0F, (float)this.field_175336_F.field_177891_X), new GuiPageButtonList.GuiSlideEntry(180, I18n.func_135052_a("createWorld.customize.custom.maxHeight"), false, this, 0.0F, 255.0F, (float)this.field_175336_F.field_177890_Y), new GuiPageButtonList.GuiLabelEntry(420, I18n.func_135052_a("tile.stone.andesite.name"), false), null, new GuiPageButtonList.GuiSlideEntry(181, I18n.func_135052_a("createWorld.customize.custom.size"), false, this, 1.0F, 50.0F, (float)this.field_175336_F.field_177892_Z), new GuiPageButtonList.GuiSlideEntry(182, I18n.func_135052_a("createWorld.customize.custom.count"), false, this, 0.0F, 40.0F, (float)this.field_175336_F.field_177936_aa), new GuiPageButtonList.GuiSlideEntry(183, I18n.func_135052_a("createWorld.customize.custom.minHeight"), false, this, 0.0F, 255.0F, (float)this.field_175336_F.field_177937_ab), new GuiPageButtonList.GuiSlideEntry(184, I18n.func_135052_a("createWorld.customize.custom.maxHeight"), false, this, 0.0F, 255.0F, (float)this.field_175336_F.field_177934_ac), new GuiPageButtonList.GuiLabelEntry(421, I18n.func_135052_a("tile.oreCoal.name"), false), null, new GuiPageButtonList.GuiSlideEntry(185, I18n.func_135052_a("createWorld.customize.custom.size"), false, this, 1.0F, 50.0F, (float)this.field_175336_F.field_177935_ad), new GuiPageButtonList.GuiSlideEntry(186, I18n.func_135052_a("createWorld.customize.custom.count"), false, this, 0.0F, 40.0F, (float)this.field_175336_F.field_177941_ae), new GuiPageButtonList.GuiSlideEntry(187, I18n.func_135052_a("createWorld.customize.custom.minHeight"), false, this, 0.0F, 255.0F, (float)this.field_175336_F.field_177943_af), new GuiPageButtonList.GuiSlideEntry(189, I18n.func_135052_a("createWorld.customize.custom.maxHeight"), false, this, 0.0F, 255.0F, (float)this.field_175336_F.field_177938_ag), new GuiPageButtonList.GuiLabelEntry(422, I18n.func_135052_a("tile.oreIron.name"), false), null, new GuiPageButtonList.GuiSlideEntry(190, I18n.func_135052_a("createWorld.customize.custom.size"), false, this, 1.0F, 50.0F, (float)this.field_175336_F.field_177939_ah), new GuiPageButtonList.GuiSlideEntry(191, I18n.func_135052_a("createWorld.customize.custom.count"), false, this, 0.0F, 40.0F, (float)this.field_175336_F.field_177922_ai), new GuiPageButtonList.GuiSlideEntry(192, I18n.func_135052_a("createWorld.customize.custom.minHeight"), false, this, 0.0F, 255.0F, (float)this.field_175336_F.field_177924_aj), new GuiPageButtonList.GuiSlideEntry(193, I18n.func_135052_a("createWorld.customize.custom.maxHeight"), false, this, 0.0F, 255.0F, (float)this.field_175336_F.field_177918_ak), new GuiPageButtonList.GuiLabelEntry(423, I18n.func_135052_a("tile.oreGold.name"), false), null, new GuiPageButtonList.GuiSlideEntry(194, I18n.func_135052_a("createWorld.customize.custom.size"), false, this, 1.0F, 50.0F, (float)this.field_175336_F.field_177920_al), new GuiPageButtonList.GuiSlideEntry(195, I18n.func_135052_a("createWorld.customize.custom.count"), false, this, 0.0F, 40.0F, (float)this.field_175336_F.field_177930_am), new GuiPageButtonList.GuiSlideEntry(196, I18n.func_135052_a("createWorld.customize.custom.minHeight"), false, this, 0.0F, 255.0F, (float)this.field_175336_F.field_177932_an), new GuiPageButtonList.GuiSlideEntry(197, I18n.func_135052_a("createWorld.customize.custom.maxHeight"), false, this, 0.0F, 255.0F, (float)this.field_175336_F.field_177926_ao), new GuiPageButtonList.GuiLabelEntry(424, I18n.func_135052_a("tile.oreRedstone.name"), false), null, new GuiPageButtonList.GuiSlideEntry(198, I18n.func_135052_a("createWorld.customize.custom.size"), false, this, 1.0F, 50.0F, (float)this.field_175336_F.field_177928_ap), new GuiPageButtonList.GuiSlideEntry(199, I18n.func_135052_a("createWorld.customize.custom.count"), false, this, 0.0F, 40.0F, (float)this.field_175336_F.field_177908_aq), new GuiPageButtonList.GuiSlideEntry(200, I18n.func_135052_a("createWorld.customize.custom.minHeight"), false, this, 0.0F, 255.0F, (float)this.field_175336_F.field_177906_ar), new GuiPageButtonList.GuiSlideEntry(201, I18n.func_135052_a("createWorld.customize.custom.maxHeight"), false, this, 0.0F, 255.0F, (float)this.field_175336_F.field_177904_as), new GuiPageButtonList.GuiLabelEntry(425, I18n.func_135052_a("tile.oreDiamond.name"), false), null, new GuiPageButtonList.GuiSlideEntry(202, I18n.func_135052_a("createWorld.customize.custom.size"), false, this, 1.0F, 50.0F, (float)this.field_175336_F.field_177902_at), new GuiPageButtonList.GuiSlideEntry(203, I18n.func_135052_a("createWorld.customize.custom.count"), false, this, 0.0F, 40.0F, (float)this.field_175336_F.field_177916_au), new GuiPageButtonList.GuiSlideEntry(204, I18n.func_135052_a("createWorld.customize.custom.minHeight"), false, this, 0.0F, 255.0F, (float)this.field_175336_F.field_177914_av), new GuiPageButtonList.GuiSlideEntry(205, I18n.func_135052_a("createWorld.customize.custom.maxHeight"), false, this, 0.0F, 255.0F, (float)this.field_175336_F.field_177912_aw), new GuiPageButtonList.GuiLabelEntry(426, I18n.func_135052_a("tile.oreLapis.name"), false), null, new GuiPageButtonList.GuiSlideEntry(206, I18n.func_135052_a("createWorld.customize.custom.size"), false, this, 1.0F, 50.0F, (float)this.field_175336_F.field_177910_ax), new GuiPageButtonList.GuiSlideEntry(207, I18n.func_135052_a("createWorld.customize.custom.count"), false, this, 0.0F, 40.0F, (float)this.field_175336_F.field_177897_ay), new GuiPageButtonList.GuiSlideEntry(208, I18n.func_135052_a("createWorld.customize.custom.center"), false, this, 0.0F, 255.0F, (float)this.field_175336_F.field_177895_az), new GuiPageButtonList.GuiSlideEntry(209, I18n.func_135052_a("createWorld.customize.custom.spread"), false, this, 0.0F, 255.0F, (float)this.field_175336_F.field_177889_aA)};
      GuiPageButtonList.GuiListEntry[] var3 = new GuiPageButtonList.GuiListEntry[]{new GuiPageButtonList.GuiSlideEntry(100, I18n.func_135052_a("createWorld.customize.custom.mainNoiseScaleX"), false, this, 1.0F, 5000.0F, this.field_175336_F.field_177917_i), new GuiPageButtonList.GuiSlideEntry(101, I18n.func_135052_a("createWorld.customize.custom.mainNoiseScaleY"), false, this, 1.0F, 5000.0F, this.field_175336_F.field_177911_j), new GuiPageButtonList.GuiSlideEntry(102, I18n.func_135052_a("createWorld.customize.custom.mainNoiseScaleZ"), false, this, 1.0F, 5000.0F, this.field_175336_F.field_177913_k), new GuiPageButtonList.GuiSlideEntry(103, I18n.func_135052_a("createWorld.customize.custom.depthNoiseScaleX"), false, this, 1.0F, 2000.0F, this.field_175336_F.field_177893_f), new GuiPageButtonList.GuiSlideEntry(104, I18n.func_135052_a("createWorld.customize.custom.depthNoiseScaleZ"), false, this, 1.0F, 2000.0F, this.field_175336_F.field_177894_g), new GuiPageButtonList.GuiSlideEntry(105, I18n.func_135052_a("createWorld.customize.custom.depthNoiseScaleExponent"), false, this, 0.01F, 20.0F, this.field_175336_F.field_177915_h), new GuiPageButtonList.GuiSlideEntry(106, I18n.func_135052_a("createWorld.customize.custom.baseSize"), false, this, 1.0F, 25.0F, this.field_175336_F.field_177907_l), new GuiPageButtonList.GuiSlideEntry(107, I18n.func_135052_a("createWorld.customize.custom.coordinateScale"), false, this, 1.0F, 6000.0F, this.field_175336_F.field_177899_b), new GuiPageButtonList.GuiSlideEntry(108, I18n.func_135052_a("createWorld.customize.custom.heightScale"), false, this, 1.0F, 6000.0F, this.field_175336_F.field_177900_c), new GuiPageButtonList.GuiSlideEntry(109, I18n.func_135052_a("createWorld.customize.custom.stretchY"), false, this, 0.01F, 50.0F, this.field_175336_F.field_177909_m), new GuiPageButtonList.GuiSlideEntry(110, I18n.func_135052_a("createWorld.customize.custom.upperLimitScale"), false, this, 1.0F, 5000.0F, this.field_175336_F.field_177896_d), new GuiPageButtonList.GuiSlideEntry(111, I18n.func_135052_a("createWorld.customize.custom.lowerLimitScale"), false, this, 1.0F, 5000.0F, this.field_175336_F.field_177898_e), new GuiPageButtonList.GuiSlideEntry(112, I18n.func_135052_a("createWorld.customize.custom.biomeDepthWeight"), false, this, 1.0F, 20.0F, this.field_175336_F.field_177903_n), new GuiPageButtonList.GuiSlideEntry(113, I18n.func_135052_a("createWorld.customize.custom.biomeDepthOffset"), false, this, 0.0F, 20.0F, this.field_175336_F.field_177905_o), new GuiPageButtonList.GuiSlideEntry(114, I18n.func_135052_a("createWorld.customize.custom.biomeScaleWeight"), false, this, 1.0F, 20.0F, this.field_175336_F.field_177933_p), new GuiPageButtonList.GuiSlideEntry(115, I18n.func_135052_a("createWorld.customize.custom.biomeScaleOffset"), false, this, 0.0F, 20.0F, this.field_175336_F.field_177931_q)};
      GuiPageButtonList.GuiListEntry[] var4 = new GuiPageButtonList.GuiListEntry[]{new GuiPageButtonList.GuiLabelEntry(400, I18n.func_135052_a("createWorld.customize.custom.mainNoiseScaleX") + ":", false), new GuiPageButtonList.EditBoxEntry(132, String.format("%5.3f", this.field_175336_F.field_177917_i), false, this.field_175332_D), new GuiPageButtonList.GuiLabelEntry(401, I18n.func_135052_a("createWorld.customize.custom.mainNoiseScaleY") + ":", false), new GuiPageButtonList.EditBoxEntry(133, String.format("%5.3f", this.field_175336_F.field_177911_j), false, this.field_175332_D), new GuiPageButtonList.GuiLabelEntry(402, I18n.func_135052_a("createWorld.customize.custom.mainNoiseScaleZ") + ":", false), new GuiPageButtonList.EditBoxEntry(134, String.format("%5.3f", this.field_175336_F.field_177913_k), false, this.field_175332_D), new GuiPageButtonList.GuiLabelEntry(403, I18n.func_135052_a("createWorld.customize.custom.depthNoiseScaleX") + ":", false), new GuiPageButtonList.EditBoxEntry(135, String.format("%5.3f", this.field_175336_F.field_177893_f), false, this.field_175332_D), new GuiPageButtonList.GuiLabelEntry(404, I18n.func_135052_a("createWorld.customize.custom.depthNoiseScaleZ") + ":", false), new GuiPageButtonList.EditBoxEntry(136, String.format("%5.3f", this.field_175336_F.field_177894_g), false, this.field_175332_D), new GuiPageButtonList.GuiLabelEntry(405, I18n.func_135052_a("createWorld.customize.custom.depthNoiseScaleExponent") + ":", false), new GuiPageButtonList.EditBoxEntry(137, String.format("%2.3f", this.field_175336_F.field_177915_h), false, this.field_175332_D), new GuiPageButtonList.GuiLabelEntry(406, I18n.func_135052_a("createWorld.customize.custom.baseSize") + ":", false), new GuiPageButtonList.EditBoxEntry(138, String.format("%2.3f", this.field_175336_F.field_177907_l), false, this.field_175332_D), new GuiPageButtonList.GuiLabelEntry(407, I18n.func_135052_a("createWorld.customize.custom.coordinateScale") + ":", false), new GuiPageButtonList.EditBoxEntry(139, String.format("%5.3f", this.field_175336_F.field_177899_b), false, this.field_175332_D), new GuiPageButtonList.GuiLabelEntry(408, I18n.func_135052_a("createWorld.customize.custom.heightScale") + ":", false), new GuiPageButtonList.EditBoxEntry(140, String.format("%5.3f", this.field_175336_F.field_177900_c), false, this.field_175332_D), new GuiPageButtonList.GuiLabelEntry(409, I18n.func_135052_a("createWorld.customize.custom.stretchY") + ":", false), new GuiPageButtonList.EditBoxEntry(141, String.format("%2.3f", this.field_175336_F.field_177909_m), false, this.field_175332_D), new GuiPageButtonList.GuiLabelEntry(410, I18n.func_135052_a("createWorld.customize.custom.upperLimitScale") + ":", false), new GuiPageButtonList.EditBoxEntry(142, String.format("%5.3f", this.field_175336_F.field_177896_d), false, this.field_175332_D), new GuiPageButtonList.GuiLabelEntry(411, I18n.func_135052_a("createWorld.customize.custom.lowerLimitScale") + ":", false), new GuiPageButtonList.EditBoxEntry(143, String.format("%5.3f", this.field_175336_F.field_177898_e), false, this.field_175332_D), new GuiPageButtonList.GuiLabelEntry(412, I18n.func_135052_a("createWorld.customize.custom.biomeDepthWeight") + ":", false), new GuiPageButtonList.EditBoxEntry(144, String.format("%2.3f", this.field_175336_F.field_177903_n), false, this.field_175332_D), new GuiPageButtonList.GuiLabelEntry(413, I18n.func_135052_a("createWorld.customize.custom.biomeDepthOffset") + ":", false), new GuiPageButtonList.EditBoxEntry(145, String.format("%2.3f", this.field_175336_F.field_177905_o), false, this.field_175332_D), new GuiPageButtonList.GuiLabelEntry(414, I18n.func_135052_a("createWorld.customize.custom.biomeScaleWeight") + ":", false), new GuiPageButtonList.EditBoxEntry(146, String.format("%2.3f", this.field_175336_F.field_177933_p), false, this.field_175332_D), new GuiPageButtonList.GuiLabelEntry(415, I18n.func_135052_a("createWorld.customize.custom.biomeScaleOffset") + ":", false), new GuiPageButtonList.EditBoxEntry(147, String.format("%2.3f", this.field_175336_F.field_177931_q), false, this.field_175332_D)};
      this.field_175349_r = new GuiPageButtonList(this.field_146297_k, this.field_146294_l, this.field_146295_m, 32, this.field_146295_m - 32, 25, this, new GuiPageButtonList.GuiListEntry[][]{var1, var2, var3, var4});

      for(int var5 = 0; var5 < 4; ++var5) {
         this.field_175342_h[var5] = I18n.func_135052_a("createWorld.customize.custom.page" + var5);
      }

      this.func_175328_i();
   }

   public String func_175323_a() {
      return this.field_175336_F.toString().replace("\n", "");
   }

   public void func_175324_a(String var1) {
      if (var1 != null && var1.length() != 0) {
         this.field_175336_F = ChunkProviderSettings.Factory.func_177865_a(var1);
      } else {
         this.field_175336_F = new ChunkProviderSettings.Factory();
      }

   }

   public void func_175319_a(int var1, String var2) {
      float var3 = 0.0F;

      try {
         var3 = Float.parseFloat(var2);
      } catch (NumberFormatException var5) {
      }

      float var4 = 0.0F;
      switch(var1) {
      case 132:
         var4 = this.field_175336_F.field_177917_i = MathHelper.func_76131_a(var3, 1.0F, 5000.0F);
         break;
      case 133:
         var4 = this.field_175336_F.field_177911_j = MathHelper.func_76131_a(var3, 1.0F, 5000.0F);
         break;
      case 134:
         var4 = this.field_175336_F.field_177913_k = MathHelper.func_76131_a(var3, 1.0F, 5000.0F);
         break;
      case 135:
         var4 = this.field_175336_F.field_177893_f = MathHelper.func_76131_a(var3, 1.0F, 2000.0F);
         break;
      case 136:
         var4 = this.field_175336_F.field_177894_g = MathHelper.func_76131_a(var3, 1.0F, 2000.0F);
         break;
      case 137:
         var4 = this.field_175336_F.field_177915_h = MathHelper.func_76131_a(var3, 0.01F, 20.0F);
         break;
      case 138:
         var4 = this.field_175336_F.field_177907_l = MathHelper.func_76131_a(var3, 1.0F, 25.0F);
         break;
      case 139:
         var4 = this.field_175336_F.field_177899_b = MathHelper.func_76131_a(var3, 1.0F, 6000.0F);
         break;
      case 140:
         var4 = this.field_175336_F.field_177900_c = MathHelper.func_76131_a(var3, 1.0F, 6000.0F);
         break;
      case 141:
         var4 = this.field_175336_F.field_177909_m = MathHelper.func_76131_a(var3, 0.01F, 50.0F);
         break;
      case 142:
         var4 = this.field_175336_F.field_177896_d = MathHelper.func_76131_a(var3, 1.0F, 5000.0F);
         break;
      case 143:
         var4 = this.field_175336_F.field_177898_e = MathHelper.func_76131_a(var3, 1.0F, 5000.0F);
         break;
      case 144:
         var4 = this.field_175336_F.field_177903_n = MathHelper.func_76131_a(var3, 1.0F, 20.0F);
         break;
      case 145:
         var4 = this.field_175336_F.field_177905_o = MathHelper.func_76131_a(var3, 0.0F, 20.0F);
         break;
      case 146:
         var4 = this.field_175336_F.field_177933_p = MathHelper.func_76131_a(var3, 1.0F, 20.0F);
         break;
      case 147:
         var4 = this.field_175336_F.field_177931_q = MathHelper.func_76131_a(var3, 0.0F, 20.0F);
      }

      if (var4 != var3 && var3 != 0.0F) {
         ((GuiTextField)this.field_175349_r.func_178061_c(var1)).func_146180_a(this.func_175330_b(var1, var4));
      }

      ((GuiSlider)this.field_175349_r.func_178061_c(var1 - 132 + 100)).func_175218_a(var4, false);
      if (!this.field_175336_F.equals(this.field_175334_E)) {
         this.func_181031_a(true);
      }

   }

   private void func_181031_a(boolean var1) {
      this.field_175338_A = var1;
      this.field_175346_u.field_146124_l = var1;
   }

   public String func_175318_a(int var1, String var2, float var3) {
      return var2 + ": " + this.func_175330_b(var1, var3);
   }

   private String func_175330_b(int var1, float var2) {
      switch(var1) {
      case 100:
      case 101:
      case 102:
      case 103:
      case 104:
      case 107:
      case 108:
      case 110:
      case 111:
      case 132:
      case 133:
      case 134:
      case 135:
      case 136:
      case 139:
      case 140:
      case 142:
      case 143:
         return String.format("%5.3f", var2);
      case 105:
      case 106:
      case 109:
      case 112:
      case 113:
      case 114:
      case 115:
      case 137:
      case 138:
      case 141:
      case 144:
      case 145:
      case 146:
      case 147:
         return String.format("%2.3f", var2);
      case 116:
      case 117:
      case 118:
      case 119:
      case 120:
      case 121:
      case 122:
      case 123:
      case 124:
      case 125:
      case 126:
      case 127:
      case 128:
      case 129:
      case 130:
      case 131:
      case 148:
      case 149:
      case 150:
      case 151:
      case 152:
      case 153:
      case 154:
      case 155:
      case 156:
      case 157:
      case 158:
      case 159:
      case 160:
      case 161:
      default:
         return String.format("%d", (int)var2);
      case 162:
         if (var2 < 0.0F) {
            return I18n.func_135052_a("gui.all");
         } else {
            BiomeGenBase var3;
            if ((int)var2 >= BiomeGenBase.field_76778_j.field_76756_M) {
               var3 = BiomeGenBase.func_150565_n()[(int)var2 + 2];
               return var3 != null ? var3.field_76791_y : "?";
            } else {
               var3 = BiomeGenBase.func_150565_n()[(int)var2];
               return var3 != null ? var3.field_76791_y : "?";
            }
         }
      }
   }

   public void func_175321_a(int var1, boolean var2) {
      switch(var1) {
      case 148:
         this.field_175336_F.field_177927_s = var2;
         break;
      case 149:
         this.field_175336_F.field_177925_t = var2;
         break;
      case 150:
         this.field_175336_F.field_177921_v = var2;
         break;
      case 151:
         this.field_175336_F.field_177919_w = var2;
         break;
      case 152:
         this.field_175336_F.field_177944_x = var2;
         break;
      case 153:
         this.field_175336_F.field_177942_y = var2;
         break;
      case 154:
         this.field_175336_F.field_177870_A = var2;
         break;
      case 155:
         this.field_175336_F.field_177871_B = var2;
         break;
      case 156:
         this.field_175336_F.field_177866_D = var2;
         break;
      case 161:
         this.field_175336_F.field_177868_F = var2;
         break;
      case 210:
         this.field_175336_F.field_177940_z = var2;
      }

      if (!this.field_175336_F.equals(this.field_175334_E)) {
         this.func_181031_a(true);
      }

   }

   public void func_175320_a(int var1, float var2) {
      switch(var1) {
      case 100:
         this.field_175336_F.field_177917_i = var2;
         break;
      case 101:
         this.field_175336_F.field_177911_j = var2;
         break;
      case 102:
         this.field_175336_F.field_177913_k = var2;
         break;
      case 103:
         this.field_175336_F.field_177893_f = var2;
         break;
      case 104:
         this.field_175336_F.field_177894_g = var2;
         break;
      case 105:
         this.field_175336_F.field_177915_h = var2;
         break;
      case 106:
         this.field_175336_F.field_177907_l = var2;
         break;
      case 107:
         this.field_175336_F.field_177899_b = var2;
         break;
      case 108:
         this.field_175336_F.field_177900_c = var2;
         break;
      case 109:
         this.field_175336_F.field_177909_m = var2;
         break;
      case 110:
         this.field_175336_F.field_177896_d = var2;
         break;
      case 111:
         this.field_175336_F.field_177898_e = var2;
         break;
      case 112:
         this.field_175336_F.field_177903_n = var2;
         break;
      case 113:
         this.field_175336_F.field_177905_o = var2;
         break;
      case 114:
         this.field_175336_F.field_177933_p = var2;
         break;
      case 115:
         this.field_175336_F.field_177931_q = var2;
      case 116:
      case 117:
      case 118:
      case 119:
      case 120:
      case 121:
      case 122:
      case 123:
      case 124:
      case 125:
      case 126:
      case 127:
      case 128:
      case 129:
      case 130:
      case 131:
      case 132:
      case 133:
      case 134:
      case 135:
      case 136:
      case 137:
      case 138:
      case 139:
      case 140:
      case 141:
      case 142:
      case 143:
      case 144:
      case 145:
      case 146:
      case 147:
      case 148:
      case 149:
      case 150:
      case 151:
      case 152:
      case 153:
      case 154:
      case 155:
      case 156:
      case 161:
      case 188:
      default:
         break;
      case 157:
         this.field_175336_F.field_177923_u = (int)var2;
         break;
      case 158:
         this.field_175336_F.field_177872_C = (int)var2;
         break;
      case 159:
         this.field_175336_F.field_177867_E = (int)var2;
         break;
      case 160:
         this.field_175336_F.field_177929_r = (int)var2;
         break;
      case 162:
         this.field_175336_F.field_177869_G = (int)var2;
         break;
      case 163:
         this.field_175336_F.field_177877_H = (int)var2;
         break;
      case 164:
         this.field_175336_F.field_177878_I = (int)var2;
         break;
      case 165:
         this.field_175336_F.field_177879_J = (int)var2;
         break;
      case 166:
         this.field_175336_F.field_177880_K = (int)var2;
         break;
      case 167:
         this.field_175336_F.field_177873_L = (int)var2;
         break;
      case 168:
         this.field_175336_F.field_177874_M = (int)var2;
         break;
      case 169:
         this.field_175336_F.field_177875_N = (int)var2;
         break;
      case 170:
         this.field_175336_F.field_177876_O = (int)var2;
         break;
      case 171:
         this.field_175336_F.field_177886_P = (int)var2;
         break;
      case 172:
         this.field_175336_F.field_177885_Q = (int)var2;
         break;
      case 173:
         this.field_175336_F.field_177888_R = (int)var2;
         break;
      case 174:
         this.field_175336_F.field_177887_S = (int)var2;
         break;
      case 175:
         this.field_175336_F.field_177882_T = (int)var2;
         break;
      case 176:
         this.field_175336_F.field_177881_U = (int)var2;
         break;
      case 177:
         this.field_175336_F.field_177884_V = (int)var2;
         break;
      case 178:
         this.field_175336_F.field_177883_W = (int)var2;
         break;
      case 179:
         this.field_175336_F.field_177891_X = (int)var2;
         break;
      case 180:
         this.field_175336_F.field_177890_Y = (int)var2;
         break;
      case 181:
         this.field_175336_F.field_177892_Z = (int)var2;
         break;
      case 182:
         this.field_175336_F.field_177936_aa = (int)var2;
         break;
      case 183:
         this.field_175336_F.field_177937_ab = (int)var2;
         break;
      case 184:
         this.field_175336_F.field_177934_ac = (int)var2;
         break;
      case 185:
         this.field_175336_F.field_177935_ad = (int)var2;
         break;
      case 186:
         this.field_175336_F.field_177941_ae = (int)var2;
         break;
      case 187:
         this.field_175336_F.field_177943_af = (int)var2;
         break;
      case 189:
         this.field_175336_F.field_177938_ag = (int)var2;
         break;
      case 190:
         this.field_175336_F.field_177939_ah = (int)var2;
         break;
      case 191:
         this.field_175336_F.field_177922_ai = (int)var2;
         break;
      case 192:
         this.field_175336_F.field_177924_aj = (int)var2;
         break;
      case 193:
         this.field_175336_F.field_177918_ak = (int)var2;
         break;
      case 194:
         this.field_175336_F.field_177920_al = (int)var2;
         break;
      case 195:
         this.field_175336_F.field_177930_am = (int)var2;
         break;
      case 196:
         this.field_175336_F.field_177932_an = (int)var2;
         break;
      case 197:
         this.field_175336_F.field_177926_ao = (int)var2;
         break;
      case 198:
         this.field_175336_F.field_177928_ap = (int)var2;
         break;
      case 199:
         this.field_175336_F.field_177908_aq = (int)var2;
         break;
      case 200:
         this.field_175336_F.field_177906_ar = (int)var2;
         break;
      case 201:
         this.field_175336_F.field_177904_as = (int)var2;
         break;
      case 202:
         this.field_175336_F.field_177902_at = (int)var2;
         break;
      case 203:
         this.field_175336_F.field_177916_au = (int)var2;
         break;
      case 204:
         this.field_175336_F.field_177914_av = (int)var2;
         break;
      case 205:
         this.field_175336_F.field_177912_aw = (int)var2;
         break;
      case 206:
         this.field_175336_F.field_177910_ax = (int)var2;
         break;
      case 207:
         this.field_175336_F.field_177897_ay = (int)var2;
         break;
      case 208:
         this.field_175336_F.field_177895_az = (int)var2;
         break;
      case 209:
         this.field_175336_F.field_177889_aA = (int)var2;
      }

      if (var1 >= 100 && var1 < 116) {
         Gui var3 = this.field_175349_r.func_178061_c(var1 - 100 + 132);
         if (var3 != null) {
            ((GuiTextField)var3).func_146180_a(this.func_175330_b(var1, var2));
         }
      }

      if (!this.field_175336_F.equals(this.field_175334_E)) {
         this.func_181031_a(true);
      }

   }

   protected void func_146284_a(GuiButton var1) {
      if (var1.field_146124_l) {
         switch(var1.field_146127_k) {
         case 300:
            this.field_175343_i.field_146334_a = this.field_175336_F.toString();
            this.field_146297_k.func_147108_a(this.field_175343_i);
            break;
         case 301:
            for(int var2 = 0; var2 < this.field_175349_r.func_148127_b(); ++var2) {
               GuiPageButtonList.GuiEntry var3 = this.field_175349_r.func_148180_b(var2);
               Gui var4 = var3.func_178022_a();
               if (var4 instanceof GuiButton) {
                  GuiButton var5 = (GuiButton)var4;
                  if (var5 instanceof GuiSlider) {
                     float var6 = ((GuiSlider)var5).func_175217_d() * (0.75F + this.field_175337_G.nextFloat() * 0.5F) + (this.field_175337_G.nextFloat() * 0.1F - 0.05F);
                     ((GuiSlider)var5).func_175219_a(MathHelper.func_76131_a(var6, 0.0F, 1.0F));
                  } else if (var5 instanceof GuiListButton) {
                     ((GuiListButton)var5).func_175212_b(this.field_175337_G.nextBoolean());
                  }
               }

               Gui var8 = var3.func_178021_b();
               if (var8 instanceof GuiButton) {
                  GuiButton var9 = (GuiButton)var8;
                  if (var9 instanceof GuiSlider) {
                     float var7 = ((GuiSlider)var9).func_175217_d() * (0.75F + this.field_175337_G.nextFloat() * 0.5F) + (this.field_175337_G.nextFloat() * 0.1F - 0.05F);
                     ((GuiSlider)var9).func_175219_a(MathHelper.func_76131_a(var7, 0.0F, 1.0F));
                  } else if (var9 instanceof GuiListButton) {
                     ((GuiListButton)var9).func_175212_b(this.field_175337_G.nextBoolean());
                  }
               }
            }

            return;
         case 302:
            this.field_175349_r.func_178071_h();
            this.func_175328_i();
            break;
         case 303:
            this.field_175349_r.func_178064_i();
            this.func_175328_i();
            break;
         case 304:
            if (this.field_175338_A) {
               this.func_175322_b(304);
            }
            break;
         case 305:
            this.field_146297_k.func_147108_a(new GuiScreenCustomizePresets(this));
            break;
         case 306:
            this.func_175331_h();
            break;
         case 307:
            this.field_175339_B = 0;
            this.func_175331_h();
         }

      }
   }

   private void func_175326_g() {
      this.field_175336_F.func_177863_a();
      this.func_175325_f();
      this.func_181031_a(false);
   }

   private void func_175322_b(int var1) {
      this.field_175339_B = var1;
      this.func_175329_a(true);
   }

   private void func_175331_h() {
      switch(this.field_175339_B) {
      case 300:
         this.func_146284_a((GuiListButton)this.field_175349_r.func_178061_c(300));
         break;
      case 304:
         this.func_175326_g();
      }

      this.field_175339_B = 0;
      this.field_175340_C = true;
      this.func_175329_a(false);
   }

   private void func_175329_a(boolean var1) {
      this.field_175352_x.field_146125_m = var1;
      this.field_175351_y.field_146125_m = var1;
      this.field_175347_t.field_146124_l = !var1;
      this.field_175348_s.field_146124_l = !var1;
      this.field_175345_v.field_146124_l = !var1;
      this.field_175344_w.field_146124_l = !var1;
      this.field_175346_u.field_146124_l = this.field_175338_A && !var1;
      this.field_175350_z.field_146124_l = !var1;
      this.field_175349_r.func_181155_a(!var1);
   }

   private void func_175328_i() {
      this.field_175345_v.field_146124_l = this.field_175349_r.func_178059_e() != 0;
      this.field_175344_w.field_146124_l = this.field_175349_r.func_178059_e() != this.field_175349_r.func_178057_f() - 1;
      this.field_175333_f = I18n.func_135052_a("book.pageIndicator", this.field_175349_r.func_178059_e() + 1, this.field_175349_r.func_178057_f());
      this.field_175335_g = this.field_175342_h[this.field_175349_r.func_178059_e()];
      this.field_175347_t.field_146124_l = this.field_175349_r.func_178059_e() != this.field_175349_r.func_178057_f() - 1;
   }

   protected void func_73869_a(char var1, int var2) {
      super.func_73869_a(var1, var2);
      if (this.field_175339_B == 0) {
         switch(var2) {
         case 200:
            this.func_175327_a(1.0F);
            break;
         case 208:
            this.func_175327_a(-1.0F);
            break;
         default:
            this.field_175349_r.func_178062_a(var1, var2);
         }

      }
   }

   private void func_175327_a(float var1) {
      Gui var2 = this.field_175349_r.func_178056_g();
      if (var2 instanceof GuiTextField) {
         float var3 = var1;
         if (GuiScreen.func_146272_n()) {
            var3 = var1 * 0.1F;
            if (GuiScreen.func_146271_m()) {
               var3 *= 0.1F;
            }
         } else if (GuiScreen.func_146271_m()) {
            var3 = var1 * 10.0F;
            if (GuiScreen.func_175283_s()) {
               var3 *= 10.0F;
            }
         }

         GuiTextField var4 = (GuiTextField)var2;
         Float var5 = Floats.tryParse(var4.func_146179_b());
         if (var5 != null) {
            var5 = var5 + var3;
            int var6 = var4.func_175206_d();
            String var7 = this.func_175330_b(var4.func_175206_d(), var5);
            var4.func_146180_a(var7);
            this.func_175319_a(var6, var7);
         }
      }
   }

   protected void func_73864_a(int var1, int var2, int var3) {
      super.func_73864_a(var1, var2, var3);
      if (this.field_175339_B == 0 && !this.field_175340_C) {
         this.field_175349_r.func_148179_a(var1, var2, var3);
      }
   }

   protected void func_146286_b(int var1, int var2, int var3) {
      super.func_146286_b(var1, var2, var3);
      if (this.field_175340_C) {
         this.field_175340_C = false;
      } else if (this.field_175339_B == 0) {
         this.field_175349_r.func_148181_b(var1, var2, var3);
      }
   }

   public void func_73863_a(int var1, int var2, float var3) {
      this.func_146276_q_();
      this.field_175349_r.func_148128_a(var1, var2, var3);
      this.func_73732_a(this.field_146289_q, this.field_175341_a, this.field_146294_l / 2, 2, 16777215);
      this.func_73732_a(this.field_146289_q, this.field_175333_f, this.field_146294_l / 2, 12, 16777215);
      this.func_73732_a(this.field_146289_q, this.field_175335_g, this.field_146294_l / 2, 22, 16777215);
      super.func_73863_a(var1, var2, var3);
      if (this.field_175339_B != 0) {
         func_73734_a(0, 0, this.field_146294_l, this.field_146295_m, -2147483648);
         this.func_73730_a(this.field_146294_l / 2 - 91, this.field_146294_l / 2 + 90, 99, -2039584);
         this.func_73730_a(this.field_146294_l / 2 - 91, this.field_146294_l / 2 + 90, 185, -6250336);
         this.func_73728_b(this.field_146294_l / 2 - 91, 99, 185, -2039584);
         this.func_73728_b(this.field_146294_l / 2 + 90, 99, 185, -6250336);
         float var4 = 85.0F;
         float var5 = 180.0F;
         GlStateManager.func_179140_f();
         GlStateManager.func_179106_n();
         Tessellator var6 = Tessellator.func_178181_a();
         WorldRenderer var7 = var6.func_178180_c();
         this.field_146297_k.func_110434_K().func_110577_a(field_110325_k);
         GlStateManager.func_179131_c(1.0F, 1.0F, 1.0F, 1.0F);
         float var8 = 32.0F;
         var7.func_181668_a(7, DefaultVertexFormats.field_181709_i);
         var7.func_181662_b((double)(this.field_146294_l / 2 - 90), 185.0D, 0.0D).func_181673_a(0.0D, 2.65625D).func_181669_b(64, 64, 64, 64).func_181675_d();
         var7.func_181662_b((double)(this.field_146294_l / 2 + 90), 185.0D, 0.0D).func_181673_a(5.625D, 2.65625D).func_181669_b(64, 64, 64, 64).func_181675_d();
         var7.func_181662_b((double)(this.field_146294_l / 2 + 90), 100.0D, 0.0D).func_181673_a(5.625D, 0.0D).func_181669_b(64, 64, 64, 64).func_181675_d();
         var7.func_181662_b((double)(this.field_146294_l / 2 - 90), 100.0D, 0.0D).func_181673_a(0.0D, 0.0D).func_181669_b(64, 64, 64, 64).func_181675_d();
         var6.func_78381_a();
         this.func_73732_a(this.field_146289_q, I18n.func_135052_a("createWorld.customize.custom.confirmTitle"), this.field_146294_l / 2, 105, 16777215);
         this.func_73732_a(this.field_146289_q, I18n.func_135052_a("createWorld.customize.custom.confirm1"), this.field_146294_l / 2, 125, 16777215);
         this.func_73732_a(this.field_146289_q, I18n.func_135052_a("createWorld.customize.custom.confirm2"), this.field_146294_l / 2, 135, 16777215);
         this.field_175352_x.func_146112_a(this.field_146297_k, var1, var2);
         this.field_175351_y.func_146112_a(this.field_146297_k, var1, var2);
      }

   }
}
