package net.minecraft.client.renderer.entity.layers;

import java.util.UUID;
import javax.annotation.Nullable;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.entity.RenderLivingBase;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.entity.RenderParrot;
import net.minecraft.client.renderer.entity.model.ModelBase;
import net.minecraft.client.renderer.entity.model.ModelParrot;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ResourceLocation;

public class LayerEntityOnShoulder implements LayerRenderer<EntityPlayer> {
   private final RenderManager field_192867_c;
   protected RenderLivingBase<? extends EntityLivingBase> field_192865_a;
   private ModelBase field_192868_d;
   private ResourceLocation field_192869_e;
   private UUID field_192870_f;
   private EntityType<?> field_192871_g;
   protected RenderLivingBase<? extends EntityLivingBase> field_192866_b;
   private ModelBase field_192872_h;
   private ResourceLocation field_192873_i;
   private UUID field_192874_j;
   private EntityType<?> field_192875_k;

   public LayerEntityOnShoulder(RenderManager var1) {
      super();
      this.field_192867_c = var1;
   }

   public void func_177141_a(EntityPlayer var1, float var2, float var3, float var4, float var5, float var6, float var7, float var8) {
      if (var1.func_192023_dk() != null || var1.func_192025_dl() != null) {
         GlStateManager.func_179091_B();
         GlStateManager.func_179131_c(1.0F, 1.0F, 1.0F, 1.0F);
         NBTTagCompound var9 = var1.func_192023_dk();
         if (!var9.isEmpty()) {
            LayerEntityOnShoulder.DataHolder var10 = this.func_200695_a(var1, this.field_192870_f, var9, this.field_192865_a, this.field_192868_d, this.field_192869_e, this.field_192871_g, var2, var3, var4, var5, var6, var7, var8, true);
            this.field_192870_f = var10.field_192882_a;
            this.field_192865_a = var10.field_192883_b;
            this.field_192869_e = var10.field_192885_d;
            this.field_192868_d = var10.field_192884_c;
            this.field_192871_g = var10.field_200698_e;
         }

         NBTTagCompound var12 = var1.func_192025_dl();
         if (!var12.isEmpty()) {
            LayerEntityOnShoulder.DataHolder var11 = this.func_200695_a(var1, this.field_192874_j, var12, this.field_192866_b, this.field_192872_h, this.field_192873_i, this.field_192875_k, var2, var3, var4, var5, var6, var7, var8, false);
            this.field_192874_j = var11.field_192882_a;
            this.field_192866_b = var11.field_192883_b;
            this.field_192873_i = var11.field_192885_d;
            this.field_192872_h = var11.field_192884_c;
            this.field_192875_k = var11.field_200698_e;
         }

         GlStateManager.func_179101_C();
      }
   }

   private LayerEntityOnShoulder.DataHolder func_200695_a(EntityPlayer var1, @Nullable UUID var2, NBTTagCompound var3, RenderLivingBase<? extends EntityLivingBase> var4, ModelBase var5, ResourceLocation var6, EntityType<?> var7, float var8, float var9, float var10, float var11, float var12, float var13, float var14, boolean var15) {
      if (var2 == null || !var2.equals(var3.func_186857_a("UUID"))) {
         var2 = var3.func_186857_a("UUID");
         var7 = EntityType.func_200713_a(var3.func_74779_i("id"));
         if (var7 == EntityType.field_200783_W) {
            var4 = new RenderParrot(this.field_192867_c);
            var5 = new ModelParrot();
            var6 = RenderParrot.field_192862_a[var3.func_74762_e("Variant")];
         }
      }

      ((RenderLivingBase)var4).func_110776_a(var6);
      GlStateManager.func_179094_E();
      float var16 = var1.func_70093_af() ? -1.3F : -1.5F;
      float var17 = var15 ? 0.4F : -0.4F;
      GlStateManager.func_179109_b(var17, var16, 0.0F);
      if (var7 == EntityType.field_200783_W) {
         var11 = 0.0F;
      }

      ((ModelBase)var5).func_78086_a(var1, var8, var9, var10);
      ((ModelBase)var5).func_78087_a(var8, var9, var11, var12, var13, var14, var1);
      ((ModelBase)var5).func_78088_a(var1, var8, var9, var11, var12, var13, var14);
      GlStateManager.func_179121_F();
      return new LayerEntityOnShoulder.DataHolder(var2, (RenderLivingBase)var4, (ModelBase)var5, var6, var7);
   }

   public boolean func_177142_b() {
      return false;
   }

   class DataHolder {
      public UUID field_192882_a;
      public RenderLivingBase<? extends EntityLivingBase> field_192883_b;
      public ModelBase field_192884_c;
      public ResourceLocation field_192885_d;
      public EntityType<?> field_200698_e;

      public DataHolder(UUID var2, RenderLivingBase<? extends EntityLivingBase> var3, ModelBase var4, ResourceLocation var5, EntityType<?> var6) {
         super();
         this.field_192882_a = var2;
         this.field_192883_b = var3;
         this.field_192884_c = var4;
         this.field_192885_d = var5;
         this.field_200698_e = var6;
      }
   }
}
