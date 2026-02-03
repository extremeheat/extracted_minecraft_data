package net.minecraft.world.level.dimension.end;

import com.google.common.collect.ImmutableList;
import java.util.List;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.boss.enderdragon.EndCrystal;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.SpikeFeature;
import net.minecraft.world.level.levelgen.feature.configurations.SpikeConfiguration;

public enum DragonRespawnAnimation {
   START {
      public void tick(final ServerLevel level, final EndDragonFight fight, final List<EndCrystal> crystals, final int time, final BlockPos portal) {
         BlockPos beamPos = new BlockPos(0, 128, 0);

         for(EndCrystal respawnCrystal : crystals) {
            respawnCrystal.setBeamTarget(beamPos);
         }

         fight.setRespawnStage(PREPARING_TO_SUMMON_PILLARS);
      }
   },
   PREPARING_TO_SUMMON_PILLARS {
      public void tick(final ServerLevel level, final EndDragonFight fight, final List<EndCrystal> crystals, final int time, final BlockPos portal) {
         if (time < 100) {
            if (time == 0 || time == 50 || time == 51 || time == 52 || time >= 95) {
               level.levelEvent(3001, new BlockPos(0, 128, 0), 0);
            }
         } else {
            fight.setRespawnStage(SUMMONING_PILLARS);
         }

      }
   },
   SUMMONING_PILLARS {
      public void tick(final ServerLevel level, final EndDragonFight fight, final List<EndCrystal> crystals, final int time, final BlockPos portal) {
         int interval = 40;
         boolean startOfBeam = time % 40 == 0;
         boolean endOfBeam = time % 40 == 39;
         if (startOfBeam || endOfBeam) {
            List<SpikeFeature.EndSpike> spikes = SpikeFeature.getSpikesForLevel(level);
            int index = time / 40;
            if (index < spikes.size()) {
               SpikeFeature.EndSpike spike = (SpikeFeature.EndSpike)spikes.get(index);
               if (startOfBeam) {
                  for(EndCrystal respawnCrystal : crystals) {
                     respawnCrystal.setBeamTarget(new BlockPos(spike.getCenterX(), spike.getHeight() + 1, spike.getCenterZ()));
                  }
               } else {
                  int radius = 10;

                  for(BlockPos pos : BlockPos.betweenClosed(new BlockPos(spike.getCenterX() - 10, spike.getHeight() - 10, spike.getCenterZ() - 10), new BlockPos(spike.getCenterX() + 10, spike.getHeight() + 10, spike.getCenterZ() + 10))) {
                     level.removeBlock(pos, false);
                  }

                  level.explode((Entity)null, (double)((float)spike.getCenterX() + 0.5F), (double)spike.getHeight(), (double)((float)spike.getCenterZ() + 0.5F), 5.0F, Level.ExplosionInteraction.BLOCK);
                  SpikeConfiguration configuration = new SpikeConfiguration(true, ImmutableList.of(spike), new BlockPos(0, 128, 0));
                  Feature.END_SPIKE.place(configuration, level, level.getChunkSource().getGenerator(), RandomSource.create(), new BlockPos(spike.getCenterX(), 45, spike.getCenterZ()));
               }
            } else if (startOfBeam) {
               fight.setRespawnStage(SUMMONING_DRAGON);
            }
         }

      }
   },
   SUMMONING_DRAGON {
      public void tick(final ServerLevel level, final EndDragonFight fight, final List<EndCrystal> crystals, final int time, final BlockPos portal) {
         if (time >= 100) {
            fight.setRespawnStage(END);
            fight.resetSpikeCrystals();

            for(EndCrystal crystal : crystals) {
               crystal.setBeamTarget((BlockPos)null);
               level.explode(crystal, crystal.getX(), crystal.getY(), crystal.getZ(), 6.0F, Level.ExplosionInteraction.NONE);
               crystal.discard();
            }
         } else if (time >= 80) {
            level.levelEvent(3001, new BlockPos(0, 128, 0), 0);
         } else if (time == 0) {
            for(EndCrystal crystal : crystals) {
               crystal.setBeamTarget(new BlockPos(0, 128, 0));
            }
         } else if (time < 5) {
            level.levelEvent(3001, new BlockPos(0, 128, 0), 0);
         }

      }
   },
   END {
      public void tick(final ServerLevel level, final EndDragonFight fight, final List<EndCrystal> crystals, final int time, final BlockPos portal) {
      }
   };

   private DragonRespawnAnimation() {
   }

   public abstract void tick(ServerLevel level, EndDragonFight fight, List<EndCrystal> crystals, int time, BlockPos portal);

   // $FF: synthetic method
   private static DragonRespawnAnimation[] $values() {
      return new DragonRespawnAnimation[]{START, PREPARING_TO_SUMMON_PILLARS, SUMMONING_PILLARS, SUMMONING_DRAGON, END};
   }
}
