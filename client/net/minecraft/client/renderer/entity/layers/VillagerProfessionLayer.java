package net.minecraft.client.renderer.entity.layers;

import com.mojang.blaze3d.vertex.PoseStack;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.objects.Object2ObjectMap;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import java.io.IOException;
import java.util.Optional;
import net.minecraft.Util;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.VillagerHeadModel;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.state.LivingEntityRenderState;
import net.minecraft.client.renderer.entity.state.VillagerDataHolderRenderState;
import net.minecraft.client.resources.metadata.animation.VillagerMetaDataSection;
import net.minecraft.core.DefaultedRegistry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.npc.VillagerData;
import net.minecraft.world.entity.npc.VillagerProfession;
import net.minecraft.world.entity.npc.VillagerType;

public class VillagerProfessionLayer<S extends LivingEntityRenderState & VillagerDataHolderRenderState, M extends EntityModel<S> & VillagerHeadModel>
   extends RenderLayer<S, M> {
   private static final Int2ObjectMap<ResourceLocation> LEVEL_LOCATIONS = Util.make(new Int2ObjectOpenHashMap(), var0 -> {
      var0.put(1, ResourceLocation.withDefaultNamespace("stone"));
      var0.put(2, ResourceLocation.withDefaultNamespace("iron"));
      var0.put(3, ResourceLocation.withDefaultNamespace("gold"));
      var0.put(4, ResourceLocation.withDefaultNamespace("emerald"));
      var0.put(5, ResourceLocation.withDefaultNamespace("diamond"));
   });
   private final Object2ObjectMap<VillagerType, VillagerMetaDataSection.Hat> typeHatCache = new Object2ObjectOpenHashMap();
   private final Object2ObjectMap<VillagerProfession, VillagerMetaDataSection.Hat> professionHatCache = new Object2ObjectOpenHashMap();
   private final ResourceManager resourceManager;
   private final String path;

   public VillagerProfessionLayer(RenderLayerParent<S, M> var1, ResourceManager var2, String var3) {
      super(var1);
      this.resourceManager = var2;
      this.path = var3;
   }

   public void render(PoseStack var1, MultiBufferSource var2, int var3, S var4, float var5, float var6) {
      if (!var4.isInvisible) {
         VillagerData var7 = ((VillagerDataHolderRenderState)var4).getVillagerData();
         VillagerType var8 = var7.getType();
         VillagerProfession var9 = var7.getProfession();
         VillagerMetaDataSection.Hat var10 = this.getHatData(this.typeHatCache, "type", BuiltInRegistries.VILLAGER_TYPE, var8);
         VillagerMetaDataSection.Hat var11 = this.getHatData(this.professionHatCache, "profession", BuiltInRegistries.VILLAGER_PROFESSION, var9);
         EntityModel var12 = this.getParentModel();
         ((VillagerHeadModel)var12)
            .hatVisible(var11 == VillagerMetaDataSection.Hat.NONE || var11 == VillagerMetaDataSection.Hat.PARTIAL && var10 != VillagerMetaDataSection.Hat.FULL);
         ResourceLocation var13 = this.getResourceLocation("type", BuiltInRegistries.VILLAGER_TYPE.getKey(var8));
         renderColoredCutoutModel(var12, var13, var1, var2, var3, var4, -1);
         ((VillagerHeadModel)var12).hatVisible(true);
         if (var9 != VillagerProfession.NONE && !var4.isBaby) {
            ResourceLocation var14 = this.getResourceLocation("profession", BuiltInRegistries.VILLAGER_PROFESSION.getKey(var9));
            renderColoredCutoutModel(var12, var14, var1, var2, var3, var4, -1);
            if (var9 != VillagerProfession.NITWIT) {
               ResourceLocation var15 = this.getResourceLocation(
                  "profession_level", (ResourceLocation)LEVEL_LOCATIONS.get(Mth.clamp(var7.getLevel(), 1, LEVEL_LOCATIONS.size()))
               );
               renderColoredCutoutModel(var12, var15, var1, var2, var3, var4, -1);
            }
         }
      }
   }

   private ResourceLocation getResourceLocation(String var1, ResourceLocation var2) {
      return var2.withPath(var2x -> "textures/entity/" + this.path + "/" + var1 + "/" + var2x + ".png");
   }

   public <K> VillagerMetaDataSection.Hat getHatData(Object2ObjectMap<K, VillagerMetaDataSection.Hat> var1, String var2, DefaultedRegistry<K> var3, K var4) {
      return (VillagerMetaDataSection.Hat)var1.computeIfAbsent(
         var4, var4x -> this.resourceManager.getResource(this.getResourceLocation(var2, var3.getKey(var4))).flatMap(var0 -> {
               try {
                  return var0.metadata().getSection(VillagerMetaDataSection.SERIALIZER).map(VillagerMetaDataSection::getHat);
               } catch (IOException var2xx) {
                  return Optional.empty();
               }
            }).orElse(VillagerMetaDataSection.Hat.NONE)
      );
   }
}
