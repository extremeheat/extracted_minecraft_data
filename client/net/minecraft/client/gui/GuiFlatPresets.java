package net.minecraft.client.gui;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import net.minecraft.client.renderer.entity.RenderItem;
import net.minecraft.client.resources.I18n;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.world.biome.BiomeGenBase;
import net.minecraft.world.gen.FlatGeneratorInfo;
import net.minecraft.world.gen.FlatLayerInfo;
import org.lwjgl.input.Keyboard;

public class GuiFlatPresets extends GuiScreen {
   private static RenderItem field_146437_a = new RenderItem();
   private static final List field_146431_f = new ArrayList();
   private final GuiCreateFlatWorld field_146432_g;
   private String field_146438_h;
   private String field_146439_i;
   private String field_146436_r;
   private GuiFlatPresets$ListSlot field_146435_s;
   private GuiButton field_146434_t;
   private GuiTextField field_146433_u;

   public GuiFlatPresets(GuiCreateFlatWorld var1) {
      super();
      this.field_146432_g = var1;
   }

   @Override
   public void func_73866_w_() {
      this.field_146292_n.clear();
      Keyboard.enableRepeatEvents(true);
      this.field_146438_h = I18n.func_135052_a("createWorld.customize.presets.title");
      this.field_146439_i = I18n.func_135052_a("createWorld.customize.presets.share");
      this.field_146436_r = I18n.func_135052_a("createWorld.customize.presets.list");
      this.field_146433_u = new GuiTextField(this.field_146289_q, 50, 40, this.field_146294_l - 100, 20);
      this.field_146435_s = new GuiFlatPresets$ListSlot(this);
      this.field_146433_u.func_146203_f(1230);
      this.field_146433_u.func_146180_a(this.field_146432_g.func_146384_e());
      this.field_146292_n
         .add(
            this.field_146434_t = new GuiButton(
               0, this.field_146294_l / 2 - 155, this.field_146295_m - 28, 150, 20, I18n.func_135052_a("createWorld.customize.presets.select")
            )
         );
      this.field_146292_n.add(new GuiButton(1, this.field_146294_l / 2 + 5, this.field_146295_m - 28, 150, 20, I18n.func_135052_a("gui.cancel")));
      this.func_146426_g();
   }

   @Override
   public void func_146281_b() {
      Keyboard.enableRepeatEvents(false);
   }

   @Override
   protected void func_73864_a(int var1, int var2, int var3) {
      this.field_146433_u.func_146192_a(var1, var2, var3);
      super.func_73864_a(var1, var2, var3);
   }

   @Override
   protected void func_73869_a(char var1, int var2) {
      if (!this.field_146433_u.func_146201_a(var1, var2)) {
         super.func_73869_a(var1, var2);
      }
   }

   @Override
   protected void func_146284_a(GuiButton var1) {
      if (var1.field_146127_k == 0 && this.func_146430_p()) {
         this.field_146432_g.func_146383_a(this.field_146433_u.func_146179_b());
         this.field_146297_k.func_147108_a(this.field_146432_g);
      } else if (var1.field_146127_k == 1) {
         this.field_146297_k.func_147108_a(this.field_146432_g);
      }
   }

   @Override
   public void func_73863_a(int var1, int var2, float var3) {
      this.func_146276_q_();
      this.field_146435_s.func_148128_a(var1, var2, var3);
      this.func_73732_a(this.field_146289_q, this.field_146438_h, this.field_146294_l / 2, 8, 16777215);
      this.func_73731_b(this.field_146289_q, this.field_146439_i, 50, 30, 10526880);
      this.func_73731_b(this.field_146289_q, this.field_146436_r, 50, 70, 10526880);
      this.field_146433_u.func_146194_f();
      super.func_73863_a(var1, var2, var3);
   }

   @Override
   public void func_73876_c() {
      this.field_146433_u.func_146178_a();
      super.func_73876_c();
   }

   public void func_146426_g() {
      boolean var1 = this.func_146430_p();
      this.field_146434_t.field_146124_l = var1;
   }

   private boolean func_146430_p() {
      return this.field_146435_s.field_148175_k > -1 && this.field_146435_s.field_148175_k < field_146431_f.size()
         || this.field_146433_u.func_146179_b().length() > 1;
   }

   private static void func_146425_a(String var0, Item var1, BiomeGenBase var2, FlatLayerInfo... var3) {
      func_146421_a(var0, var1, var2, null, var3);
   }

   private static void func_146421_a(String var0, Item var1, BiomeGenBase var2, List var3, FlatLayerInfo... var4) {
      FlatGeneratorInfo var5 = new FlatGeneratorInfo();

      for(int var6 = var4.length - 1; var6 >= 0; --var6) {
         var5.func_82650_c().add(var4[var6]);
      }

      var5.func_82647_a(var2.field_76756_M);
      var5.func_82645_d();
      if (var3 != null) {
         for(String var7 : var3) {
            var5.func_82644_b().put(var7, new HashMap());
         }
      }

      field_146431_f.add(new GuiFlatPresets$LayerItem(var1, var0, var5.toString()));
   }

   static {
      func_146421_a(
         "Classic Flat",
         Item.func_150898_a(Blocks.field_150349_c),
         BiomeGenBase.field_76772_c,
         Arrays.asList("village"),
         new FlatLayerInfo(1, Blocks.field_150349_c),
         new FlatLayerInfo(2, Blocks.field_150346_d),
         new FlatLayerInfo(1, Blocks.field_150357_h)
      );
      func_146421_a(
         "Tunnelers' Dream",
         Item.func_150898_a(Blocks.field_150348_b),
         BiomeGenBase.field_76770_e,
         Arrays.asList("biome_1", "dungeon", "decoration", "stronghold", "mineshaft"),
         new FlatLayerInfo(1, Blocks.field_150349_c),
         new FlatLayerInfo(5, Blocks.field_150346_d),
         new FlatLayerInfo(230, Blocks.field_150348_b),
         new FlatLayerInfo(1, Blocks.field_150357_h)
      );
      func_146421_a(
         "Water World",
         Item.func_150898_a(Blocks.field_150358_i),
         BiomeGenBase.field_76772_c,
         Arrays.asList("village", "biome_1"),
         new FlatLayerInfo(90, Blocks.field_150355_j),
         new FlatLayerInfo(5, Blocks.field_150354_m),
         new FlatLayerInfo(5, Blocks.field_150346_d),
         new FlatLayerInfo(5, Blocks.field_150348_b),
         new FlatLayerInfo(1, Blocks.field_150357_h)
      );
      func_146421_a(
         "Overworld",
         Item.func_150898_a(Blocks.field_150329_H),
         BiomeGenBase.field_76772_c,
         Arrays.asList("village", "biome_1", "decoration", "stronghold", "mineshaft", "dungeon", "lake", "lava_lake"),
         new FlatLayerInfo(1, Blocks.field_150349_c),
         new FlatLayerInfo(3, Blocks.field_150346_d),
         new FlatLayerInfo(59, Blocks.field_150348_b),
         new FlatLayerInfo(1, Blocks.field_150357_h)
      );
      func_146421_a(
         "Snowy Kingdom",
         Item.func_150898_a(Blocks.field_150431_aC),
         BiomeGenBase.field_76774_n,
         Arrays.asList("village", "biome_1"),
         new FlatLayerInfo(1, Blocks.field_150431_aC),
         new FlatLayerInfo(1, Blocks.field_150349_c),
         new FlatLayerInfo(3, Blocks.field_150346_d),
         new FlatLayerInfo(59, Blocks.field_150348_b),
         new FlatLayerInfo(1, Blocks.field_150357_h)
      );
      func_146421_a(
         "Bottomless Pit",
         Items.field_151008_G,
         BiomeGenBase.field_76772_c,
         Arrays.asList("village", "biome_1"),
         new FlatLayerInfo(1, Blocks.field_150349_c),
         new FlatLayerInfo(3, Blocks.field_150346_d),
         new FlatLayerInfo(2, Blocks.field_150347_e)
      );
      func_146421_a(
         "Desert",
         Item.func_150898_a(Blocks.field_150354_m),
         BiomeGenBase.field_76769_d,
         Arrays.asList("village", "biome_1", "decoration", "stronghold", "mineshaft", "dungeon"),
         new FlatLayerInfo(8, Blocks.field_150354_m),
         new FlatLayerInfo(52, Blocks.field_150322_A),
         new FlatLayerInfo(3, Blocks.field_150348_b),
         new FlatLayerInfo(1, Blocks.field_150357_h)
      );
      func_146425_a(
         "Redstone Ready",
         Items.field_151137_ax,
         BiomeGenBase.field_76769_d,
         new FlatLayerInfo(52, Blocks.field_150322_A),
         new FlatLayerInfo(3, Blocks.field_150348_b),
         new FlatLayerInfo(1, Blocks.field_150357_h)
      );
   }
}
