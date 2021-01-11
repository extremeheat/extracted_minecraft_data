package net.minecraft.client.renderer.tileentity;

import com.mojang.authlib.GameProfile;
import java.util.UUID;
import net.minecraft.block.Block;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTUtil;
import net.minecraft.tileentity.TileEntityBanner;
import net.minecraft.tileentity.TileEntityChest;
import net.minecraft.tileentity.TileEntityEnderChest;
import net.minecraft.tileentity.TileEntitySkull;
import net.minecraft.util.EnumFacing;

public class TileEntityItemStackRenderer {
   public static TileEntityItemStackRenderer field_147719_a = new TileEntityItemStackRenderer();
   private TileEntityChest field_147717_b = new TileEntityChest(0);
   private TileEntityChest field_147718_c = new TileEntityChest(1);
   private TileEntityEnderChest field_147716_d = new TileEntityEnderChest();
   private TileEntityBanner field_179024_e = new TileEntityBanner();
   private TileEntitySkull field_179023_f = new TileEntitySkull();

   public TileEntityItemStackRenderer() {
      super();
   }

   public void func_179022_a(ItemStack var1) {
      if (var1.func_77973_b() == Items.field_179564_cE) {
         this.field_179024_e.func_175112_a(var1);
         TileEntityRendererDispatcher.field_147556_a.func_147549_a(this.field_179024_e, 0.0D, 0.0D, 0.0D, 0.0F);
      } else if (var1.func_77973_b() == Items.field_151144_bL) {
         GameProfile var2 = null;
         if (var1.func_77942_o()) {
            NBTTagCompound var3 = var1.func_77978_p();
            if (var3.func_150297_b("SkullOwner", 10)) {
               var2 = NBTUtil.func_152459_a(var3.func_74775_l("SkullOwner"));
            } else if (var3.func_150297_b("SkullOwner", 8) && var3.func_74779_i("SkullOwner").length() > 0) {
               var2 = new GameProfile((UUID)null, var3.func_74779_i("SkullOwner"));
               var2 = TileEntitySkull.func_174884_b(var2);
               var3.func_82580_o("SkullOwner");
               var3.func_74782_a("SkullOwner", NBTUtil.func_180708_a(new NBTTagCompound(), var2));
            }
         }

         if (TileEntitySkullRenderer.field_147536_b != null) {
            GlStateManager.func_179094_E();
            GlStateManager.func_179109_b(-0.5F, 0.0F, -0.5F);
            GlStateManager.func_179152_a(2.0F, 2.0F, 2.0F);
            GlStateManager.func_179129_p();
            TileEntitySkullRenderer.field_147536_b.func_180543_a(0.0F, 0.0F, 0.0F, EnumFacing.UP, 0.0F, var1.func_77960_j(), var2, -1);
            GlStateManager.func_179089_o();
            GlStateManager.func_179121_F();
         }
      } else {
         Block var4 = Block.func_149634_a(var1.func_77973_b());
         if (var4 == Blocks.field_150477_bB) {
            TileEntityRendererDispatcher.field_147556_a.func_147549_a(this.field_147716_d, 0.0D, 0.0D, 0.0D, 0.0F);
         } else if (var4 == Blocks.field_150447_bR) {
            TileEntityRendererDispatcher.field_147556_a.func_147549_a(this.field_147718_c, 0.0D, 0.0D, 0.0D, 0.0F);
         } else {
            TileEntityRendererDispatcher.field_147556_a.func_147549_a(this.field_147717_b, 0.0D, 0.0D, 0.0D, 0.0F);
         }
      }

   }
}
