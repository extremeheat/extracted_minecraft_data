package net.minecraft.client.renderer.entity.layers;

import com.mojang.blaze3d.vertex.PoseStack;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.objects.Object2ObjectMap;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import java.io.IOException;
import java.util.Optional;
import java.util.function.UnaryOperator;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.VillagerLikeModel;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.state.LivingEntityRenderState;
import net.minecraft.client.renderer.entity.state.VillagerDataHolderRenderState;
import net.minecraft.client.renderer.texture.MissingTextureAtlasSprite;
import net.minecraft.client.resources.metadata.animation.VillagerMetadataSection;
import net.minecraft.core.Holder;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.util.Mth;
import net.minecraft.util.Util;
import net.minecraft.world.entity.npc.VillagerData;
import net.minecraft.world.entity.npc.VillagerProfession;
import net.minecraft.world.entity.npc.VillagerType;

public class VillagerProfessionLayer<S extends LivingEntityRenderState & VillagerDataHolderRenderState, M extends EntityModel<S> & VillagerLikeModel> extends RenderLayer<S, M> {
   private static final Int2ObjectMap<Identifier> LEVEL_LOCATIONS = (Int2ObjectMap)Util.make(new Int2ObjectOpenHashMap(), (var0) -> {
      var0.put(1, Identifier.withDefaultNamespace("stone"));
      var0.put(2, Identifier.withDefaultNamespace("iron"));
      var0.put(3, Identifier.withDefaultNamespace("gold"));
      var0.put(4, Identifier.withDefaultNamespace("emerald"));
      var0.put(5, Identifier.withDefaultNamespace("diamond"));
   });
   private final Object2ObjectMap<ResourceKey<VillagerType>, VillagerMetadataSection.Hat> typeHatCache = new Object2ObjectOpenHashMap();
   private final Object2ObjectMap<ResourceKey<VillagerProfession>, VillagerMetadataSection.Hat> professionHatCache = new Object2ObjectOpenHashMap();
   private final ResourceManager resourceManager;
   private final String path;
   private final M noHatModel;
   private final M noHatBabyModel;

   public VillagerProfessionLayer(RenderLayerParent<S, M> var1, ResourceManager var2, String var3, M var4, M var5) {
      super(var1);
      this.resourceManager = var2;
      this.path = var3;
      this.noHatModel = var4;
      this.noHatBabyModel = var5;
   }

   public void submit(PoseStack var1, SubmitNodeCollector var2, int var3, S var4, float var5, float var6) {
      if (!var4.isInvisible) {
         VillagerData var7 = ((VillagerDataHolderRenderState)var4).getVillagerData();
         if (var7 != null) {
            Holder var8 = var7.type();
            Holder var9 = var7.profession();
            VillagerMetadataSection.Hat var10 = this.getHatData(this.typeHatCache, "type", var8);
            VillagerMetadataSection.Hat var11 = this.getHatData(this.professionHatCache, "profession", var9);
            EntityModel var12 = this.getParentModel();
            Identifier var13 = this.getIdentifier("type", var8);
            boolean var14 = var11 == VillagerMetadataSection.Hat.NONE || var11 == VillagerMetadataSection.Hat.PARTIAL && var10 != VillagerMetadataSection.Hat.FULL;
            EntityModel var15 = var4.isBaby ? this.noHatBabyModel : this.noHatModel;
            renderColoredCutoutModel(var14 ? var12 : var15, var13, var1, var2, var3, var4, -1, 1);
            if (!var9.is(VillagerProfession.NONE) && !var4.isBaby) {
               Identifier var16 = this.getIdentifier("profession", var9);
               renderColoredCutoutModel(var12, var16, var1, var2, var3, var4, -1, 2);
               if (!var9.is(VillagerProfession.NITWIT)) {
                  Identifier var17 = this.getIdentifier("profession_level", (Identifier)LEVEL_LOCATIONS.get(Mth.clamp(var7.level(), 1, LEVEL_LOCATIONS.size())));
                  renderColoredCutoutModel(var12, var17, var1, var2, var3, var4, -1, 3);
               }
            }

         }
      }
   }

   private Identifier getIdentifier(String var1, Identifier var2) {
      return var2.withPath((UnaryOperator)((var2x) -> "textures/entity/" + this.path + "/" + var1 + "/" + var2x + ".png"));
   }

   private Identifier getIdentifier(String var1, Holder<?> var2) {
      return (Identifier)var2.unwrapKey().map((var2x) -> this.getIdentifier(var1, var2x.identifier())).orElse(MissingTextureAtlasSprite.getLocation());
   }

   public <K> VillagerMetadataSection.Hat getHatData(Object2ObjectMap<ResourceKey<K>, VillagerMetadataSection.Hat> var1, String var2, Holder<K> var3) {
      ResourceKey var4 = (ResourceKey)var3.unwrapKey().orElse((Object)null);
      return var4 == null ? VillagerMetadataSection.Hat.NONE : (VillagerMetadataSection.Hat)var1.computeIfAbsent(var4, (var3x) -> (VillagerMetadataSection.Hat)this.resourceManager.getResource(this.getIdentifier(var2, var4.identifier())).flatMap((var0) -> {
            try {
               return var0.metadata().getSection(VillagerMetadataSection.TYPE).map(VillagerMetadataSection::hat);
            } catch (IOException var2) {
               return Optional.empty();
            }
         }).orElse(VillagerMetadataSection.Hat.NONE));
   }
}
