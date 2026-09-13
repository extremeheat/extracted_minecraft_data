package net.minecraft.client.gui.inventory;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.init.Items;
import net.minecraft.inventory.ContainerBeacon;
import net.minecraft.item.ItemStack;
import net.minecraft.network.play.client.C17PacketCustomPayload;
import net.minecraft.tileentity.TileEntityBeacon;
import net.minecraft.util.ResourceLocation;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.lwjgl.opengl.GL11;

public class GuiBeacon extends GuiContainer {
   private static final Logger field_147026_u = LogManager.getLogger();
   private static final ResourceLocation field_147025_v = new ResourceLocation("textures/gui/container/beacon.png");
   private TileEntityBeacon field_147024_w;
   private GuiBeacon$ConfirmButton field_147028_x;
   private boolean field_147027_y;

   public GuiBeacon(InventoryPlayer var1, TileEntityBeacon var2) {
      super(new ContainerBeacon(var1, var2));
      this.field_147024_w = var2;
      this.field_146999_f = 230;
      this.field_147000_g = 219;
   }

   @Override
   public void func_73866_w_() {
      super.func_73866_w_();
      this.field_146292_n.add(this.field_147028_x = new GuiBeacon$ConfirmButton(this, -1, this.field_147003_i + 164, this.field_147009_r + 107));
      this.field_146292_n.add(new GuiBeacon$CancelButton(this, -2, this.field_147003_i + 190, this.field_147009_r + 107));
      this.field_147027_y = true;
      this.field_147028_x.field_146124_l = false;
   }

   @Override
   public void func_73876_c() {
      super.func_73876_c();
      if (this.field_147027_y && this.field_147024_w.func_145998_l() >= 0) {
         this.field_147027_y = false;

         for(int var1 = 0; var1 <= 2; ++var1) {
            int var2 = TileEntityBeacon.field_146009_a[var1].length;
            int var3 = var2 * 22 + (var2 - 1) * 2;

            for(int var4 = 0; var4 < var2; ++var4) {
               int var5 = TileEntityBeacon.field_146009_a[var1][var4].field_76415_H;
               GuiBeacon$PowerButton var6 = new GuiBeacon$PowerButton(
                  this, var1 << 8 | var5, this.field_147003_i + 76 + var4 * 24 - var3 / 2, this.field_147009_r + 22 + var1 * 25, var5, var1
               );
               this.field_146292_n.add(var6);
               if (var1 >= this.field_147024_w.func_145998_l()) {
                  var6.field_146124_l = false;
               } else if (var5 == this.field_147024_w.func_146007_j()) {
                  var6.func_146140_b(true);
               }
            }
         }

         byte var7 = 3;
         int var8 = TileEntityBeacon.field_146009_a[var7].length + 1;
         int var9 = var8 * 22 + (var8 - 1) * 2;

         for(int var10 = 0; var10 < var8 - 1; ++var10) {
            int var12 = TileEntityBeacon.field_146009_a[var7][var10].field_76415_H;
            GuiBeacon$PowerButton var13 = new GuiBeacon$PowerButton(
               this, var7 << 8 | var12, this.field_147003_i + 167 + var10 * 24 - var9 / 2, this.field_147009_r + 47, var12, var7
            );
            this.field_146292_n.add(var13);
            if (var7 >= this.field_147024_w.func_145998_l()) {
               var13.field_146124_l = false;
            } else if (var12 == this.field_147024_w.func_146006_k()) {
               var13.func_146140_b(true);
            }
         }

         if (this.field_147024_w.func_146007_j() > 0) {
            GuiBeacon$PowerButton var11 = new GuiBeacon$PowerButton(
               this,
               var7 << 8 | this.field_147024_w.func_146007_j(),
               this.field_147003_i + 167 + (var8 - 1) * 24 - var9 / 2,
               this.field_147009_r + 47,
               this.field_147024_w.func_146007_j(),
               var7
            );
            this.field_146292_n.add(var11);
            if (var7 >= this.field_147024_w.func_145998_l()) {
               var11.field_146124_l = false;
            } else if (this.field_147024_w.func_146007_j() == this.field_147024_w.func_146006_k()) {
               var11.func_146140_b(true);
            }
         }
      }

      this.field_147028_x.field_146124_l = this.field_147024_w.func_70301_a(0) != null && this.field_147024_w.func_146007_j() > 0;
   }

   @Override
   protected void func_146284_a(GuiButton var1) {
      if (var1.field_146127_k == -2) {
         this.field_146297_k.func_147108_a(null);
      } else if (var1.field_146127_k == -1) {
         String var2 = "MC|Beacon";
         ByteBuf var3 = Unpooled.buffer();

         try {
            var3.writeInt(this.field_147024_w.func_146007_j());
            var3.writeInt(this.field_147024_w.func_146006_k());
            this.field_146297_k.func_147114_u().func_147297_a(new C17PacketCustomPayload(var2, var3));
         } catch (Exception var8) {
            field_147026_u.error("Couldn't send beacon info", var8);
         } finally {
            var3.release();
         }

         this.field_146297_k.func_147108_a(null);
      } else if (var1 instanceof GuiBeacon$PowerButton) {
         if (((GuiBeacon$PowerButton)var1).func_146141_c()) {
            return;
         }

         int var10 = var1.field_146127_k;
         int var11 = var10 & 0xFF;
         int var4 = var10 >> 8;
         if (var4 < 3) {
            this.field_147024_w.func_146001_d(var11);
         } else {
            this.field_147024_w.func_146004_e(var11);
         }

         this.field_146292_n.clear();
         this.func_73866_w_();
         this.func_73876_c();
      }
   }

   @Override
   protected void func_146979_b(int var1, int var2) {
      RenderHelper.func_74518_a();
      this.func_73732_a(this.field_146289_q, I18n.func_135052_a("tile.beacon.primary"), 62, 10, 14737632);
      this.func_73732_a(this.field_146289_q, I18n.func_135052_a("tile.beacon.secondary"), 169, 10, 14737632);

      for(GuiButton var4 : this.field_146292_n) {
         if (var4.func_146115_a()) {
            var4.func_146111_b(var1 - this.field_147003_i, var2 - this.field_147009_r);
            break;
         }
      }

      RenderHelper.func_74520_c();
   }

   @Override
   protected void func_146976_a(float var1, int var2, int var3) {
      GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
      this.field_146297_k.func_110434_K().func_110577_a(field_147025_v);
      int var4 = (this.field_146294_l - this.field_146999_f) / 2;
      int var5 = (this.field_146295_m - this.field_147000_g) / 2;
      this.func_73729_b(var4, var5, 0, 0, this.field_146999_f, this.field_147000_g);
      field_146296_j.field_77023_b = 100.0F;
      field_146296_j.func_82406_b(this.field_146289_q, this.field_146297_k.func_110434_K(), new ItemStack(Items.field_151166_bC), var4 + 42, var5 + 109);
      field_146296_j.func_82406_b(this.field_146289_q, this.field_146297_k.func_110434_K(), new ItemStack(Items.field_151045_i), var4 + 42 + 22, var5 + 109);
      field_146296_j.func_82406_b(this.field_146289_q, this.field_146297_k.func_110434_K(), new ItemStack(Items.field_151043_k), var4 + 42 + 44, var5 + 109);
      field_146296_j.func_82406_b(this.field_146289_q, this.field_146297_k.func_110434_K(), new ItemStack(Items.field_151042_j), var4 + 42 + 66, var5 + 109);
      field_146296_j.field_77023_b = 0.0F;
   }
}
