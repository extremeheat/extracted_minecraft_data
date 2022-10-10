package net.minecraft.client.renderer.entity.layers;

import com.mojang.authlib.GameProfile;
import java.util.UUID;
import net.minecraft.block.BlockAbstractSkull;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.entity.model.ModelRenderer;
import net.minecraft.client.renderer.model.ItemCameraTransforms;
import net.minecraft.client.renderer.tileentity.TileEntitySkullRenderer;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.monster.EntityZombieVillager;
import net.minecraft.entity.passive.EntityVillager;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.Item;
import net.minecraft.item.ItemArmor;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTUtil;
import net.minecraft.tileentity.TileEntitySkull;
import net.minecraft.util.EnumFacing;
import org.apache.commons.lang3.StringUtils;

public class LayerCustomHead implements LayerRenderer<EntityLivingBase> {
   private final ModelRenderer field_177209_a;

   public LayerCustomHead(ModelRenderer var1) {
      super();
      this.field_177209_a = var1;
   }

   public void func_177141_a(EntityLivingBase var1, float var2, float var3, float var4, float var5, float var6, float var7, float var8) {
      ItemStack var9 = var1.func_184582_a(EntityEquipmentSlot.HEAD);
      if (!var9.func_190926_b()) {
         Item var10 = var9.func_77973_b();
         Minecraft var11 = Minecraft.func_71410_x();
         GlStateManager.func_179094_E();
         if (var1.func_70093_af()) {
            GlStateManager.func_179109_b(0.0F, 0.2F, 0.0F);
         }

         boolean var12 = var1 instanceof EntityVillager || var1 instanceof EntityZombieVillager;
         float var13;
         if (var1.func_70631_g_() && !(var1 instanceof EntityVillager)) {
            var13 = 2.0F;
            float var14 = 1.4F;
            GlStateManager.func_179109_b(0.0F, 0.5F * var8, 0.0F);
            GlStateManager.func_179152_a(0.7F, 0.7F, 0.7F);
            GlStateManager.func_179109_b(0.0F, 16.0F * var8, 0.0F);
         }

         this.field_177209_a.func_78794_c(0.0625F);
         GlStateManager.func_179131_c(1.0F, 1.0F, 1.0F, 1.0F);
         if (var10 instanceof ItemBlock && ((ItemBlock)var10).func_179223_d() instanceof BlockAbstractSkull) {
            var13 = 1.1875F;
            GlStateManager.func_179152_a(1.1875F, -1.1875F, -1.1875F);
            if (var12) {
               GlStateManager.func_179109_b(0.0F, 0.0625F, 0.0F);
            }

            GameProfile var17 = null;
            if (var9.func_77942_o()) {
               NBTTagCompound var15 = var9.func_77978_p();
               if (var15.func_150297_b("SkullOwner", 10)) {
                  var17 = NBTUtil.func_152459_a(var15.func_74775_l("SkullOwner"));
               } else if (var15.func_150297_b("SkullOwner", 8)) {
                  String var16 = var15.func_74779_i("SkullOwner");
                  if (!StringUtils.isBlank(var16)) {
                     var17 = TileEntitySkull.func_174884_b(new GameProfile((UUID)null, var16));
                     var15.func_74782_a("SkullOwner", NBTUtil.func_180708_a(new NBTTagCompound(), var17));
                  }
               }
            }

            TileEntitySkullRenderer.field_147536_b.func_199355_a(-0.5F, 0.0F, -0.5F, (EnumFacing)null, 180.0F, ((BlockAbstractSkull)((ItemBlock)var10).func_179223_d()).func_196292_N_(), var17, -1, var2);
         } else if (!(var10 instanceof ItemArmor) || ((ItemArmor)var10).func_185083_B_() != EntityEquipmentSlot.HEAD) {
            var13 = 0.625F;
            GlStateManager.func_179109_b(0.0F, -0.25F, 0.0F);
            GlStateManager.func_179114_b(180.0F, 0.0F, 1.0F, 0.0F);
            GlStateManager.func_179152_a(0.625F, -0.625F, -0.625F);
            if (var12) {
               GlStateManager.func_179109_b(0.0F, 0.1875F, 0.0F);
            }

            var11.func_175597_ag().func_178099_a(var1, var9, ItemCameraTransforms.TransformType.HEAD);
         }

         GlStateManager.func_179121_F();
      }
   }

   public boolean func_177142_b() {
      return false;
   }
}
