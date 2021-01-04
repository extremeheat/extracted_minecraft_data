package net.minecraft.client.renderer.entity;

import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.objects.Object2ObjectMap;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import java.io.IOException;
import net.minecraft.Util;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.client.resources.metadata.animation.VillagerMetaDataSection;
import net.minecraft.core.DefaultedRegistry;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ReloadableResourceManager;
import net.minecraft.server.packs.resources.Resource;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.ResourceManagerReloadListener;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.npc.VillagerData;
import net.minecraft.world.entity.npc.VillagerDataHolder;
import net.minecraft.world.entity.npc.VillagerProfession;
import net.minecraft.world.entity.npc.VillagerType;

public class VillagerProfessionLayer<T extends LivingEntity & VillagerDataHolder, M extends EntityModel<T> & VillagerHeadModel> extends RenderLayer<T, M> implements ResourceManagerReloadListener {
   private static final Int2ObjectMap<ResourceLocation> LEVEL_LOCATIONS = (Int2ObjectMap)Util.make(new Int2ObjectOpenHashMap(), (var0) -> {
      var0.put(1, new ResourceLocation("stone"));
      var0.put(2, new ResourceLocation("iron"));
      var0.put(3, new ResourceLocation("gold"));
      var0.put(4, new ResourceLocation("emerald"));
      var0.put(5, new ResourceLocation("diamond"));
   });
   private final Object2ObjectMap<VillagerType, VillagerMetaDataSection.Hat> typeHatCache = new Object2ObjectOpenHashMap();
   private final Object2ObjectMap<VillagerProfession, VillagerMetaDataSection.Hat> professionHatCache = new Object2ObjectOpenHashMap();
   private final ReloadableResourceManager resourceManager;
   private final String path;

   public VillagerProfessionLayer(RenderLayerParent<T, M> var1, ReloadableResourceManager var2, String var3) {
      super(var1);
      this.resourceManager = var2;
      this.path = var3;
      var2.registerReloadListener(this);
   }

   public void render(T var1, float var2, float var3, float var4, float var5, float var6, float var7, float var8) {
      if (!var1.isInvisible()) {
         VillagerData var9 = ((VillagerDataHolder)var1).getVillagerData();
         VillagerType var10 = var9.getType();
         VillagerProfession var11 = var9.getProfession();
         VillagerMetaDataSection.Hat var12 = this.getHatData(this.typeHatCache, "type", Registry.VILLAGER_TYPE, var10);
         VillagerMetaDataSection.Hat var13 = this.getHatData(this.professionHatCache, "profession", Registry.VILLAGER_PROFESSION, var11);
         EntityModel var14 = this.getParentModel();
         this.bindTexture(this.getResourceLocation("type", Registry.VILLAGER_TYPE.getKey(var10)));
         ((VillagerHeadModel)var14).hatVisible(var13 == VillagerMetaDataSection.Hat.NONE || var13 == VillagerMetaDataSection.Hat.PARTIAL && var12 != VillagerMetaDataSection.Hat.FULL);
         var14.render(var1, var2, var3, var5, var6, var7, var8);
         ((VillagerHeadModel)var14).hatVisible(true);
         if (var11 != VillagerProfession.NONE && !var1.isBaby()) {
            this.bindTexture(this.getResourceLocation("profession", Registry.VILLAGER_PROFESSION.getKey(var11)));
            var14.render(var1, var2, var3, var5, var6, var7, var8);
            this.bindTexture(this.getResourceLocation("profession_level", (ResourceLocation)LEVEL_LOCATIONS.get(Mth.clamp(var9.getLevel(), 1, LEVEL_LOCATIONS.size()))));
            var14.render(var1, var2, var3, var5, var6, var7, var8);
         }

      }
   }

   public boolean colorsOnDamage() {
      return true;
   }

   private ResourceLocation getResourceLocation(String var1, ResourceLocation var2) {
      return new ResourceLocation(var2.getNamespace(), "textures/entity/" + this.path + "/" + var1 + "/" + var2.getPath() + ".png");
   }

   public <K> VillagerMetaDataSection.Hat getHatData(Object2ObjectMap<K, VillagerMetaDataSection.Hat> var1, String var2, DefaultedRegistry<K> var3, K var4) {
      return (VillagerMetaDataSection.Hat)var1.computeIfAbsent(var4, (var4x) -> {
         try {
            Resource var5 = this.resourceManager.getResource(this.getResourceLocation(var2, var3.getKey(var4)));
            Throwable var6 = null;

            VillagerMetaDataSection.Hat var8;
            try {
               VillagerMetaDataSection var7 = (VillagerMetaDataSection)var5.getMetadata(VillagerMetaDataSection.SERIALIZER);
               if (var7 == null) {
                  return VillagerMetaDataSection.Hat.NONE;
               }

               var8 = var7.getHat();
            } catch (Throwable var19) {
               var6 = var19;
               throw var19;
            } finally {
               if (var5 != null) {
                  if (var6 != null) {
                     try {
                        var5.close();
                     } catch (Throwable var18) {
                        var6.addSuppressed(var18);
                     }
                  } else {
                     var5.close();
                  }
               }

            }

            return var8;
         } catch (IOException var21) {
            return VillagerMetaDataSection.Hat.NONE;
         }
      });
   }

   public void onResourceManagerReload(ResourceManager var1) {
      this.professionHatCache.clear();
      this.typeHatCache.clear();
   }
}
