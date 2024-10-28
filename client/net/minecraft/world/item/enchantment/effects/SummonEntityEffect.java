package net.minecraft.world.item.enchantment.effects;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.Optional;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderSet;
import net.minecraft.core.RegistryCodecs;
import net.minecraft.core.registries.Registries;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LightningBolt;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.item.enchantment.EnchantedItemInUse;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

public record SummonEntityEffect(HolderSet<EntityType<?>> entityTypes, boolean joinTeam) implements EnchantmentEntityEffect {
   public static final MapCodec<SummonEntityEffect> CODEC = RecordCodecBuilder.mapCodec((var0) -> {
      return var0.group(RegistryCodecs.homogeneousList(Registries.ENTITY_TYPE).fieldOf("entity").forGetter(SummonEntityEffect::entityTypes), Codec.BOOL.optionalFieldOf("join_team", false).forGetter(SummonEntityEffect::joinTeam)).apply(var0, SummonEntityEffect::new);
   });

   public SummonEntityEffect(HolderSet<EntityType<?>> var1, boolean var2) {
      super();
      this.entityTypes = var1;
      this.joinTeam = var2;
   }

   public void apply(ServerLevel var1, int var2, EnchantedItemInUse var3, Entity var4, Vec3 var5) {
      BlockPos var6 = BlockPos.containing(var5);
      if (Level.isInSpawnableBounds(var6)) {
         Optional var7 = this.entityTypes().getRandomElement(var1.getRandom());
         if (!var7.isEmpty()) {
            Entity var8 = ((EntityType)((Holder)var7.get()).value()).spawn(var1, var6, MobSpawnType.TRIGGERED);
            if (var8 != null) {
               if (var8 instanceof LightningBolt) {
                  LightningBolt var9 = (LightningBolt)var8;
                  LivingEntity var11 = var3.owner();
                  if (var11 instanceof ServerPlayer) {
                     ServerPlayer var10 = (ServerPlayer)var11;
                     var9.setCause(var10);
                  }
               }

               if (this.joinTeam && var4.getTeam() != null) {
                  var1.getScoreboard().addPlayerToTeam(var8.getScoreboardName(), var4.getTeam());
               }

               var8.moveTo(var5.x, var5.y, var5.z, var8.getYRot(), var8.getXRot());
            }
         }
      }
   }

   public MapCodec<SummonEntityEffect> codec() {
      return CODEC;
   }

   public HolderSet<EntityType<?>> entityTypes() {
      return this.entityTypes;
   }

   public boolean joinTeam() {
      return this.joinTeam;
   }
}
