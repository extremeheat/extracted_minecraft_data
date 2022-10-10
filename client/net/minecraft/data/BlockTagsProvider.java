package net.minecraft.data;

import java.nio.file.Path;
import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.TagCollection;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.registry.IRegistry;

public class BlockTagsProvider extends TagsProvider<Block> {
   public BlockTagsProvider(DataGenerator var1) {
      super(var1, IRegistry.field_212618_g);
   }

   protected void func_200432_c() {
      this.func_200426_a(BlockTags.field_199897_a).func_200573_a(Blocks.field_196556_aL, Blocks.field_196557_aM, Blocks.field_196558_aN, Blocks.field_196559_aO, Blocks.field_196560_aP, Blocks.field_196561_aQ, Blocks.field_196562_aR, Blocks.field_196563_aS, Blocks.field_196564_aT, Blocks.field_196565_aU, Blocks.field_196566_aV, Blocks.field_196567_aW, Blocks.field_196568_aX, Blocks.field_196569_aY, Blocks.field_196570_aZ, Blocks.field_196602_ba);
      this.func_200426_a(BlockTags.field_199898_b).func_200573_a(Blocks.field_196662_n, Blocks.field_196664_o, Blocks.field_196666_p, Blocks.field_196668_q, Blocks.field_196670_r, Blocks.field_196672_s);
      this.func_200426_a(BlockTags.field_200026_c).func_200573_a(Blocks.field_196696_di, Blocks.field_196698_dj, Blocks.field_196700_dk, Blocks.field_196702_dl);
      this.func_200426_a(BlockTags.field_200151_d).func_200573_a(Blocks.field_196689_eF, Blocks.field_196691_eG, Blocks.field_196693_eH, Blocks.field_196695_eI, Blocks.field_196697_eJ, Blocks.field_196699_eK);
      this.func_200426_a(BlockTags.field_200027_d).func_200574_a(BlockTags.field_200151_d).func_200048_a(Blocks.field_150430_aB);
      this.func_200426_a(BlockTags.field_200028_e).func_200573_a(Blocks.field_196724_fH, Blocks.field_196725_fI, Blocks.field_196727_fJ, Blocks.field_196729_fK, Blocks.field_196731_fL, Blocks.field_196733_fM, Blocks.field_196735_fN, Blocks.field_196737_fO, Blocks.field_196739_fP, Blocks.field_196741_fQ, Blocks.field_196743_fR, Blocks.field_196745_fS, Blocks.field_196747_fT, Blocks.field_196749_fU, Blocks.field_196751_fV, Blocks.field_196753_fW);
      this.func_200426_a(BlockTags.field_200152_g).func_200573_a(Blocks.field_180413_ao, Blocks.field_180414_ap, Blocks.field_180412_aq, Blocks.field_180411_ar, Blocks.field_180410_as, Blocks.field_180409_at);
      this.func_200426_a(BlockTags.field_202894_h).func_200573_a(Blocks.field_150476_ad, Blocks.field_150485_bF, Blocks.field_150487_bG, Blocks.field_150481_bH, Blocks.field_150400_ck, Blocks.field_150401_cl);
      this.func_200426_a(BlockTags.field_202895_i).func_200573_a(Blocks.field_196622_bq, Blocks.field_196624_br, Blocks.field_196627_bs, Blocks.field_196630_bt, Blocks.field_196632_bu, Blocks.field_196635_bv);
      this.func_200426_a(BlockTags.field_200029_f).func_200574_a(BlockTags.field_200152_g).func_200048_a(Blocks.field_150454_av);
      this.func_200426_a(BlockTags.field_200030_g).func_200573_a(Blocks.field_196674_t, Blocks.field_196675_u, Blocks.field_196676_v, Blocks.field_196678_w, Blocks.field_196679_x, Blocks.field_196680_y);
      this.func_200426_a(BlockTags.field_203285_n).func_200573_a(Blocks.field_196623_P, Blocks.field_196639_V, Blocks.field_203209_W, Blocks.field_209394_ag);
      this.func_200426_a(BlockTags.field_203286_o).func_200573_a(Blocks.field_196617_K, Blocks.field_196626_Q, Blocks.field_203204_R, Blocks.field_209389_ab);
      this.func_200426_a(BlockTags.field_203288_q).func_200573_a(Blocks.field_196621_O, Blocks.field_196637_U, Blocks.field_203208_V, Blocks.field_209393_af);
      this.func_200426_a(BlockTags.field_203287_p).func_200573_a(Blocks.field_196619_M, Blocks.field_196631_S, Blocks.field_203206_T, Blocks.field_209391_ad);
      this.func_200426_a(BlockTags.field_203289_r).func_200573_a(Blocks.field_196620_N, Blocks.field_196634_T, Blocks.field_203207_U, Blocks.field_209392_ae);
      this.func_200426_a(BlockTags.field_203290_s).func_200573_a(Blocks.field_196618_L, Blocks.field_196629_R, Blocks.field_203205_S, Blocks.field_209390_ac);
      this.func_200426_a(BlockTags.field_200031_h).func_200574_a(BlockTags.field_203285_n).func_200574_a(BlockTags.field_203286_o).func_200574_a(BlockTags.field_203288_q).func_200574_a(BlockTags.field_203287_p).func_200574_a(BlockTags.field_203289_r).func_200574_a(BlockTags.field_203290_s);
      this.func_200426_a(BlockTags.field_200572_k).func_200573_a(Blocks.field_150467_bQ, Blocks.field_196717_eY, Blocks.field_196718_eZ);
      this.func_200426_a(BlockTags.field_201151_l).func_200573_a(Blocks.field_196658_i, Blocks.field_150346_d, Blocks.field_196660_k, Blocks.field_196661_l, Blocks.field_150354_m, Blocks.field_196611_F, Blocks.field_150351_n, Blocks.field_196605_bc, Blocks.field_196606_bd, Blocks.field_196607_be, Blocks.field_196609_bf, Blocks.field_196610_bg, Blocks.field_196612_bh, Blocks.field_196613_bi, Blocks.field_196614_bj, Blocks.field_196615_bk, Blocks.field_196616_bl, Blocks.field_150338_P, Blocks.field_150337_Q, Blocks.field_150335_W, Blocks.field_150434_aF, Blocks.field_150435_aG, Blocks.field_150423_aK, Blocks.field_196625_cS, Blocks.field_150440_ba, Blocks.field_150391_bh, Blocks.field_150424_aL);
      this.func_200426_a(BlockTags.field_200032_i).func_200573_a(Blocks.field_150457_bL, Blocks.field_196726_ei, Blocks.field_196728_ej, Blocks.field_196730_ek, Blocks.field_196732_el, Blocks.field_196734_em, Blocks.field_196736_en, Blocks.field_196738_eo, Blocks.field_196740_ep, Blocks.field_196742_eq, Blocks.field_196744_er, Blocks.field_196746_es, Blocks.field_196748_et, Blocks.field_196750_eu, Blocks.field_196752_ev, Blocks.field_196754_ew, Blocks.field_196755_ex, Blocks.field_196756_ey, Blocks.field_196757_ez, Blocks.field_196681_eA, Blocks.field_196683_eB, Blocks.field_196685_eC);
      this.func_200426_a(BlockTags.field_202897_p).func_200573_a(Blocks.field_196784_gT, Blocks.field_196786_gU, Blocks.field_196788_gV, Blocks.field_196790_gW, Blocks.field_196792_gX, Blocks.field_196794_gY, Blocks.field_196796_gZ, Blocks.field_196826_ha, Blocks.field_196827_hb, Blocks.field_196829_hc, Blocks.field_196831_hd, Blocks.field_196833_he, Blocks.field_196835_hf, Blocks.field_196837_hg, Blocks.field_196839_hh, Blocks.field_196841_hi, Blocks.field_196843_hj, Blocks.field_196845_hk, Blocks.field_196847_hl, Blocks.field_196849_hm, Blocks.field_196851_hn, Blocks.field_196853_ho, Blocks.field_196855_hp, Blocks.field_196857_hq, Blocks.field_196859_hr, Blocks.field_196861_hs, Blocks.field_196863_ht, Blocks.field_196865_hu, Blocks.field_196867_hv, Blocks.field_196869_hw, Blocks.field_196871_hx, Blocks.field_196873_hy);
      this.func_200426_a(BlockTags.field_202896_j).func_200573_a(Blocks.field_196663_cq, Blocks.field_196665_cr, Blocks.field_196667_cs, Blocks.field_196669_ct, Blocks.field_196671_cu, Blocks.field_196673_cv);
      this.func_200426_a(BlockTags.field_203291_w).func_200573_a(Blocks.field_150476_ad, Blocks.field_196659_cl, Blocks.field_150485_bF, Blocks.field_150372_bz, Blocks.field_150400_ck, Blocks.field_150481_bH, Blocks.field_150487_bG, Blocks.field_150401_cl, Blocks.field_150387_bl, Blocks.field_150390_bg, Blocks.field_150389_bf, Blocks.field_185769_cV, Blocks.field_150370_cb, Blocks.field_180396_cN, Blocks.field_203211_hf, Blocks.field_203210_he, Blocks.field_203212_hg);
      this.func_200426_a(BlockTags.field_203292_x).func_200573_a(Blocks.field_150333_U, Blocks.field_196573_bB, Blocks.field_196640_bx, Blocks.field_196632_bu, Blocks.field_196627_bs, Blocks.field_196635_bv, Blocks.field_196630_bt, Blocks.field_196622_bq, Blocks.field_196624_br, Blocks.field_185771_cX, Blocks.field_196576_bD, Blocks.field_196578_bE, Blocks.field_196571_bA, Blocks.field_196646_bz, Blocks.field_196575_bC, Blocks.field_196643_by, Blocks.field_203200_bP, Blocks.field_203201_bQ, Blocks.field_203202_bR);
      this.func_200426_a(BlockTags.field_212742_K).func_200573_a(Blocks.field_204278_jJ, Blocks.field_204279_jK, Blocks.field_204280_jL, Blocks.field_204281_jM, Blocks.field_204282_jN);
      this.func_200426_a(BlockTags.field_204116_z).func_200574_a(BlockTags.field_212742_K).func_200573_a(Blocks.field_204743_jR, Blocks.field_204744_jS, Blocks.field_204745_jT, Blocks.field_204746_jU, Blocks.field_204747_jV);
      this.func_200426_a(BlockTags.field_211922_B).func_200573_a(Blocks.field_211891_jY, Blocks.field_211892_jZ, Blocks.field_211893_ka, Blocks.field_211894_kb, Blocks.field_211895_kc);
      this.func_200426_a(BlockTags.field_203436_u).func_200573_a(Blocks.field_150354_m, Blocks.field_196611_F);
      this.func_200426_a(BlockTags.field_203437_y).func_200573_a(Blocks.field_150448_aq, Blocks.field_196552_aC, Blocks.field_150319_E, Blocks.field_150408_cc);
      this.func_200426_a(BlockTags.field_205598_B).func_200573_a(Blocks.field_203963_jE, Blocks.field_203964_jF, Blocks.field_203965_jG, Blocks.field_203966_jH, Blocks.field_203967_jI);
      this.func_200426_a(BlockTags.field_205213_E).func_200573_a(Blocks.field_150432_aD, Blocks.field_150403_cj, Blocks.field_205164_gk, Blocks.field_185778_de);
      this.func_200426_a(BlockTags.field_205599_H).func_200573_a(Blocks.field_196658_i, Blocks.field_196661_l);
      this.func_200426_a(BlockTags.field_206952_E).func_200573_a(Blocks.field_196648_Z, Blocks.field_196642_W, Blocks.field_196645_X, Blocks.field_196574_ab, Blocks.field_196572_aa, Blocks.field_196647_Y);
      this.func_200426_a(BlockTags.field_211923_H).func_200573_a(Blocks.field_150359_w, Blocks.field_196807_gj, Blocks.field_196808_gk, Blocks.field_196809_gl, Blocks.field_196810_gm, Blocks.field_196811_gn, Blocks.field_196812_go, Blocks.field_196813_gp, Blocks.field_196815_gq, Blocks.field_196816_gr, Blocks.field_196818_gs, Blocks.field_196819_gt, Blocks.field_196820_gu, Blocks.field_196821_gv, Blocks.field_196822_gw, Blocks.field_196823_gx, Blocks.field_196824_gy);
      this.func_200426_a(BlockTags.field_212186_k).func_200573_a(Blocks.field_196682_da, Blocks.field_196641_cY, Blocks.field_196684_db, Blocks.field_196644_cZ, Blocks.field_196636_cW, Blocks.field_196638_cX);
      this.func_200426_a(BlockTags.field_212185_E).func_200574_a(BlockTags.field_212186_k).func_200048_a(Blocks.field_180400_cw);
      this.func_200426_a(BlockTags.field_212741_H).func_200048_a(Blocks.field_203198_aQ).func_200574_a(BlockTags.field_204116_z).func_200574_a(BlockTags.field_211922_B);
   }

   protected Path func_200431_a(ResourceLocation var1) {
      return this.field_200433_a.func_200391_b().resolve("data/" + var1.func_110624_b() + "/tags/blocks/" + var1.func_110623_a() + ".json");
   }

   public String func_200397_b() {
      return "Block Tags";
   }

   protected void func_200429_a(TagCollection<Block> var1) {
      BlockTags.func_199895_a(var1);
   }
}
