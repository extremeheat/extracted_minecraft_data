package net.minecraft.client.renderer.entity.layers;

import com.mojang.blaze3d.vertex.PoseStack;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.objects.Object2ObjectMap;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import java.io.IOException;
import net.minecraft.Util;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.VillagerHeadModel;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.resources.metadata.animation.VillagerMetaDataSection;
import net.minecraft.core.DefaultedRegistry;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.Resource;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.npc.VillagerData;
import net.minecraft.world.entity.npc.VillagerDataHolder;
import net.minecraft.world.entity.npc.VillagerProfession;
import net.minecraft.world.entity.npc.VillagerType;

public class VillagerProfessionLayer<T extends LivingEntity & VillagerDataHolder, M extends EntityModel<T> & VillagerHeadModel> extends RenderLayer<T, M> {
   private static final Int2ObjectMap<ResourceLocation> LEVEL_LOCATIONS = (Int2ObjectMap)Util.make(new Int2ObjectOpenHashMap(), (var0) -> {
      var0.put(1, new ResourceLocation("stone"));
      var0.put(2, new ResourceLocation("iron"));
      var0.put(3, new ResourceLocation("gold"));
      var0.put(4, new ResourceLocation("emerald"));
      var0.put(5, new ResourceLocation("diamond"));
   });
   private final Object2ObjectMap<VillagerType, VillagerMetaDataSection.Hat> typeHatCache = new Object2ObjectOpenHashMap();
   private final Object2ObjectMap<VillagerProfession, VillagerMetaDataSection.Hat> professionHatCache = new Object2ObjectOpenHashMap();
   private final ResourceManager resourceManager;
   private final String path;

   public VillagerProfessionLayer(RenderLayerParent<T, M> var1, ResourceManager var2, String var3) {
      super(var1);
      this.resourceManager = var2;
      this.path = var3;
   }

   public void render(PoseStack var1, MultiBufferSource var2, int var3, T var4, float var5, float var6, float var7, float var8, float var9, float var10) {
      if (!var4.isInvisible()) {
         VillagerData var11 = ((VillagerDataHolder)var4).getVillagerData();
         VillagerType var12 = var11.getType();
         VillagerProfession var13 = var11.getProfession();
         VillagerMetaDataSection.Hat var14 = this.getHatData(this.typeHatCache, "type", Registry.VILLAGER_TYPE, var12);
         VillagerMetaDataSection.Hat var15 = this.getHatData(this.professionHatCache, "profession", Registry.VILLAGER_PROFESSION, var13);
         EntityModel var16 = this.getParentModel();
         ((VillagerHeadModel)var16).hatVisible(var15 == VillagerMetaDataSection.Hat.NONE || var15 == VillagerMetaDataSection.Hat.PARTIAL && var14 != VillagerMetaDataSection.Hat.FULL);
         ResourceLocation var17 = this.getResourceLocation("type", Registry.VILLAGER_TYPE.getKey(var12));
         renderColoredCutoutModel(var16, var17, var1, var2, var3, var4, 1.0F, 1.0F, 1.0F);
         ((VillagerHeadModel)var16).hatVisible(true);
         if (var13 != VillagerProfession.NONE && !var4.isBaby()) {
            ResourceLocation var18 = this.getResourceLocation("profession", Registry.VILLAGER_PROFESSION.getKey(var13));
            renderColoredCutoutModel(var16, var18, var1, var2, var3, var4, 1.0F, 1.0F, 1.0F);
            if (var13 != VillagerProfession.NITWIT) {
               ResourceLocation var19 = this.getResourceLocation("profession_level", (ResourceLocation)LEVEL_LOCATIONS.get(Mth.clamp((int)var11.getLevel(), (int)1, (int)LEVEL_LOCATIONS.size())));
               renderColoredCutoutModel(var16, var19, var1, var2, var3, var4, 1.0F, 1.0F, 1.0F);
            }
         }

      }
   }

   private ResourceLocation getResourceLocation(String var1, ResourceLocation var2) {
      return new ResourceLocation(var2.getNamespace(), "textures/entity/" + this.path + "/" + var1 + "/" + var2.getPath() + ".png");
   }

   public <K> VillagerMetaDataSection.Hat getHatData(Object2ObjectMap<K, VillagerMetaDataSection.Hat> var1, String var2, DefaultedRegistry<K> var3, K var4) {
      return (VillagerMetaDataSection.Hat)var1.computeIfAbsent(var4, (var4x) -> {
         try {
            Resource var5 = this.resourceManager.getResource(this.getResourceLocation(var2, var3.getKey(var4)));

            VillagerMetaDataSection.Hat var7;
            label50: {
               try {
                  VillagerMetaDataSection var6 = (VillagerMetaDataSection)var5.getMetadata(VillagerMetaDataSection.SERIALIZER);
                  if (var6 != null) {
                     var7 = var6.getHat();
                     break label50;
                  }
               } catch (Throwable var9) {
                  if (var5 != null) {
                     try {
                        var5.close();
                     } catch (Throwable var8) {
                        var9.addSuppressed(var8);
                     }
                  }

                  throw var9;
               }

               if (var5 != null) {
                  var5.close();
               }

               return VillagerMetaDataSection.Hat.NONE;
            }

            if (var5 != null) {
               var5.close();
            }

            return var7;
         } catch (IOException var10) {
            return VillagerMetaDataSection.Hat.NONE;
         }
      });
   }
}
