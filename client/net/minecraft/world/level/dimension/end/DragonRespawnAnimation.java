package net.minecraft.world.level.dimension.end;

import com.google.common.collect.ImmutableList;
import java.util.Iterator;
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
      public void tick(ServerLevel var1, EndDragonFight var2, List<EndCrystal> var3, int var4, BlockPos var5) {
         BlockPos var6 = new BlockPos(0, 128, 0);
         Iterator var7 = var3.iterator();

         while(var7.hasNext()) {
            EndCrystal var8 = (EndCrystal)var7.next();
            var8.setBeamTarget(var6);
         }

         var2.setRespawnStage(PREPARING_TO_SUMMON_PILLARS);
      }
   },
   PREPARING_TO_SUMMON_PILLARS {
      public void tick(ServerLevel var1, EndDragonFight var2, List<EndCrystal> var3, int var4, BlockPos var5) {
         if (var4 < 100) {
            if (var4 == 0 || var4 == 50 || var4 == 51 || var4 == 52 || var4 >= 95) {
               var1.levelEvent(3001, new BlockPos(0, 128, 0), 0);
            }
         } else {
            var2.setRespawnStage(SUMMONING_PILLARS);
         }

      }
   },
   SUMMONING_PILLARS {
      public void tick(ServerLevel var1, EndDragonFight var2, List<EndCrystal> var3, int var4, BlockPos var5) {
         boolean var6 = true;
         boolean var7 = var4 % 40 == 0;
         boolean var8 = var4 % 40 == 39;
         if (var7 || var8) {
            List var9 = SpikeFeature.getSpikesForLevel(var1);
            int var10 = var4 / 40;
            if (var10 < var9.size()) {
               SpikeFeature.EndSpike var11 = (SpikeFeature.EndSpike)var9.get(var10);
               if (var7) {
                  Iterator var12 = var3.iterator();

                  while(var12.hasNext()) {
                     EndCrystal var13 = (EndCrystal)var12.next();
                     var13.setBeamTarget(new BlockPos(var11.getCenterX(), var11.getHeight() + 1, var11.getCenterZ()));
                  }
               } else {
                  boolean var15 = true;
                  Iterator var16 = BlockPos.betweenClosed(new BlockPos(var11.getCenterX() - 10, var11.getHeight() - 10, var11.getCenterZ() - 10), new BlockPos(var11.getCenterX() + 10, var11.getHeight() + 10, var11.getCenterZ() + 10)).iterator();

                  while(var16.hasNext()) {
                     BlockPos var14 = (BlockPos)var16.next();
                     var1.removeBlock(var14, false);
                  }

                  var1.explode((Entity)null, (double)((float)var11.getCenterX() + 0.5F), (double)var11.getHeight(), (double)((float)var11.getCenterZ() + 0.5F), 5.0F, Level.ExplosionInteraction.BLOCK);
                  SpikeConfiguration var17 = new SpikeConfiguration(true, ImmutableList.of(var11), new BlockPos(0, 128, 0));
                  Feature.END_SPIKE.place(var17, var1, var1.getChunkSource().getGenerator(), RandomSource.create(), new BlockPos(var11.getCenterX(), 45, var11.getCenterZ()));
               }
            } else if (var7) {
               var2.setRespawnStage(SUMMONING_DRAGON);
            }
         }

      }
   },
   SUMMONING_DRAGON {
      public void tick(ServerLevel var1, EndDragonFight var2, List<EndCrystal> var3, int var4, BlockPos var5) {
         Iterator var6;
         EndCrystal var7;
         if (var4 >= 100) {
            var2.setRespawnStage(END);
            var2.resetSpikeCrystals();
            var6 = var3.iterator();

            while(var6.hasNext()) {
               var7 = (EndCrystal)var6.next();
               var7.setBeamTarget((BlockPos)null);
               var1.explode(var7, var7.getX(), var7.getY(), var7.getZ(), 6.0F, Level.ExplosionInteraction.NONE);
               var7.discard();
            }
         } else if (var4 >= 80) {
            var1.levelEvent(3001, new BlockPos(0, 128, 0), 0);
         } else if (var4 == 0) {
            var6 = var3.iterator();

            while(var6.hasNext()) {
               var7 = (EndCrystal)var6.next();
               var7.setBeamTarget(new BlockPos(0, 128, 0));
            }
         } else if (var4 < 5) {
            var1.levelEvent(3001, new BlockPos(0, 128, 0), 0);
         }

      }
   },
   END {
      public void tick(ServerLevel var1, EndDragonFight var2, List<EndCrystal> var3, int var4, BlockPos var5) {
      }
   };

   DragonRespawnAnimation() {
   }

   public abstract void tick(ServerLevel var1, EndDragonFight var2, List<EndCrystal> var3, int var4, BlockPos var5);

   // $FF: synthetic method
   private static DragonRespawnAnimation[] $values() {
      return new DragonRespawnAnimation[]{START, PREPARING_TO_SUMMON_PILLARS, SUMMONING_PILLARS, SUMMONING_DRAGON, END};
   }
}
