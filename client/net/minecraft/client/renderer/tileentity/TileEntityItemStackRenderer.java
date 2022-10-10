package net.minecraft.client.renderer.tileentity;

import com.mojang.authlib.GameProfile;
import java.util.Arrays;
import java.util.Comparator;
import java.util.UUID;
import net.minecraft.block.Block;
import net.minecraft.block.BlockAbstractSkull;
import net.minecraft.block.BlockBed;
import net.minecraft.block.BlockShulkerBox;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BannerTextures;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.ItemRenderer;
import net.minecraft.client.renderer.entity.model.ModelShield;
import net.minecraft.client.renderer.entity.model.ModelTrident;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.EnumDyeColor;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBanner;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemShield;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTUtil;
import net.minecraft.tileentity.TileEntityBanner;
import net.minecraft.tileentity.TileEntityBed;
import net.minecraft.tileentity.TileEntityChest;
import net.minecraft.tileentity.TileEntityConduit;
import net.minecraft.tileentity.TileEntityEnderChest;
import net.minecraft.tileentity.TileEntityShulkerBox;
import net.minecraft.tileentity.TileEntitySkull;
import net.minecraft.tileentity.TileEntityTrappedChest;
import net.minecraft.util.EnumFacing;
import org.apache.commons.lang3.StringUtils;

public class TileEntityItemStackRenderer {
   private static final TileEntityShulkerBox[] field_191274_b = (TileEntityShulkerBox[])Arrays.stream(EnumDyeColor.values()).sorted(Comparator.comparingInt(EnumDyeColor::func_196059_a)).map(TileEntityShulkerBox::new).toArray((var0) -> {
      return new TileEntityShulkerBox[var0];
   });
   private static final TileEntityShulkerBox field_204401_c = new TileEntityShulkerBox((EnumDyeColor)null);
   public static TileEntityItemStackRenderer field_147719_a = new TileEntityItemStackRenderer();
   private final TileEntityChest field_147717_b = new TileEntityChest();
   private final TileEntityChest field_147718_c = new TileEntityTrappedChest();
   private final TileEntityEnderChest field_147716_d = new TileEntityEnderChest();
   private final TileEntityBanner field_179024_e = new TileEntityBanner();
   private final TileEntityBed field_193843_g = new TileEntityBed();
   private final TileEntitySkull field_179023_f = new TileEntitySkull();
   private final TileEntityConduit field_205085_j = new TileEntityConduit();
   private final ModelShield field_187318_g = new ModelShield();
   private final ModelTrident field_203084_j = new ModelTrident();

   public TileEntityItemStackRenderer() {
      super();
   }

   public void func_179022_a(ItemStack var1) {
      Item var2 = var1.func_77973_b();
      if (var2 instanceof ItemBanner) {
         this.field_179024_e.func_195534_a(var1, ((ItemBanner)var2).func_195948_b());
         TileEntityRendererDispatcher.field_147556_a.func_203601_b(this.field_179024_e);
      } else if (var2 instanceof ItemBlock && ((ItemBlock)var2).func_179223_d() instanceof BlockBed) {
         this.field_193843_g.func_193052_a(((BlockBed)((ItemBlock)var2).func_179223_d()).func_196350_d());
         TileEntityRendererDispatcher.field_147556_a.func_203601_b(this.field_193843_g);
      } else if (var2 == Items.field_185159_cQ) {
         if (var1.func_179543_a("BlockEntityTag") != null) {
            this.field_179024_e.func_195534_a(var1, ItemShield.func_195979_f(var1));
            Minecraft.func_71410_x().func_110434_K().func_110577_a(BannerTextures.field_187485_b.func_187478_a(this.field_179024_e.func_175116_e(), this.field_179024_e.func_175114_c(), this.field_179024_e.func_175110_d()));
         } else {
            Minecraft.func_71410_x().func_110434_K().func_110577_a(BannerTextures.field_187486_c);
         }

         GlStateManager.func_179094_E();
         GlStateManager.func_179152_a(1.0F, -1.0F, -1.0F);
         this.field_187318_g.func_187062_a();
         if (var1.func_77962_s()) {
            ModelShield var10001 = this.field_187318_g;
            this.func_211271_a(var10001::func_187062_a);
         }

         GlStateManager.func_179121_F();
      } else if (var2 instanceof ItemBlock && ((ItemBlock)var2).func_179223_d() instanceof BlockAbstractSkull) {
         GameProfile var5 = null;
         if (var1.func_77942_o()) {
            NBTTagCompound var4 = var1.func_77978_p();
            if (var4.func_150297_b("SkullOwner", 10)) {
               var5 = NBTUtil.func_152459_a(var4.func_74775_l("SkullOwner"));
            } else if (var4.func_150297_b("SkullOwner", 8) && !StringUtils.isBlank(var4.func_74779_i("SkullOwner"))) {
               var5 = new GameProfile((UUID)null, var4.func_74779_i("SkullOwner"));
               var5 = TileEntitySkull.func_174884_b(var5);
               var4.func_82580_o("SkullOwner");
               var4.func_74782_a("SkullOwner", NBTUtil.func_180708_a(new NBTTagCompound(), var5));
            }
         }

         if (TileEntitySkullRenderer.field_147536_b != null) {
            GlStateManager.func_179094_E();
            GlStateManager.func_179129_p();
            TileEntitySkullRenderer.field_147536_b.func_199355_a(0.0F, 0.0F, 0.0F, (EnumFacing)null, 180.0F, ((BlockAbstractSkull)((ItemBlock)var2).func_179223_d()).func_196292_N_(), var5, -1, 0.0F);
            GlStateManager.func_179089_o();
            GlStateManager.func_179121_F();
         }
      } else if (var2 == Items.field_203184_eO) {
         Minecraft.func_71410_x().func_110434_K().func_110577_a(ModelTrident.field_203080_a);
         GlStateManager.func_179094_E();
         GlStateManager.func_179152_a(1.0F, -1.0F, -1.0F);
         this.field_203084_j.func_203079_a();
         if (var1.func_77962_s()) {
            ModelTrident var6 = this.field_203084_j;
            this.func_211271_a(var6::func_203079_a);
         }

         GlStateManager.func_179121_F();
      } else if (var2 instanceof ItemBlock && ((ItemBlock)var2).func_179223_d() == Blocks.field_205165_jY) {
         TileEntityRendererDispatcher.field_147556_a.func_203601_b(this.field_205085_j);
      } else if (var2 == Blocks.field_150477_bB.func_199767_j()) {
         TileEntityRendererDispatcher.field_147556_a.func_203601_b(this.field_147716_d);
      } else if (var2 == Blocks.field_150447_bR.func_199767_j()) {
         TileEntityRendererDispatcher.field_147556_a.func_203601_b(this.field_147718_c);
      } else if (Block.func_149634_a(var2) instanceof BlockShulkerBox) {
         EnumDyeColor var3 = BlockShulkerBox.func_190955_b(var2);
         if (var3 == null) {
            TileEntityRendererDispatcher.field_147556_a.func_203601_b(field_204401_c);
         } else {
            TileEntityRendererDispatcher.field_147556_a.func_203601_b(field_191274_b[var3.func_196059_a()]);
         }
      } else {
         TileEntityRendererDispatcher.field_147556_a.func_203601_b(this.field_147717_b);
      }

   }

   private void func_211271_a(Runnable var1) {
      GlStateManager.func_179124_c(0.5019608F, 0.2509804F, 0.8F);
      Minecraft.func_71410_x().func_110434_K().func_110577_a(ItemRenderer.field_110798_h);
      ItemRenderer.func_211128_a(Minecraft.func_71410_x().func_110434_K(), var1, 1);
   }
}
