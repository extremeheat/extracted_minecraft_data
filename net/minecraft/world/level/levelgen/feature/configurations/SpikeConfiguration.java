package net.minecraft.world.level.levelgen.feature.configurations;

import com.google.common.collect.ImmutableMap;
import com.mojang.datafixers.Dynamic;
import com.mojang.datafixers.types.DynamicOps;
import java.util.List;
import java.util.stream.IntStream;
import java.util.stream.Stream;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.levelgen.feature.SpikeFeature;

public class SpikeConfiguration implements FeatureConfiguration {
   private final boolean crystalInvulnerable;
   private final List spikes;
   @Nullable
   private final BlockPos crystalBeamTarget;

   public SpikeConfiguration(boolean var1, List var2, @Nullable BlockPos var3) {
      this.crystalInvulnerable = var1;
      this.spikes = var2;
      this.crystalBeamTarget = var3;
   }

   public Dynamic serialize(DynamicOps var1) {
      Dynamic var10000 = new Dynamic;
      Object var10004 = var1.createString("crystalInvulnerable");
      Object var10005 = var1.createBoolean(this.crystalInvulnerable);
      Object var10006 = var1.createString("spikes");
      Object var10007 = var1.createList(this.spikes.stream().map((var1x) -> {
         return var1x.serialize(var1).getValue();
      }));
      Object var10008 = var1.createString("crystalBeamTarget");
      Object var10009;
      if (this.crystalBeamTarget == null) {
         var10009 = var1.createList(Stream.empty());
      } else {
         IntStream var10010 = IntStream.of(new int[]{this.crystalBeamTarget.getX(), this.crystalBeamTarget.getY(), this.crystalBeamTarget.getZ()});
         var1.getClass();
         var10009 = var1.createList(var10010.mapToObj(var1::createInt));
      }

      var10000.<init>(var1, var1.createMap(ImmutableMap.of(var10004, var10005, var10006, var10007, var10008, var10009)));
      return var10000;
   }

   public static SpikeConfiguration deserialize(Dynamic var0) {
      List var1 = var0.get("spikes").asList(SpikeFeature.EndSpike::deserialize);
      List var2 = var0.get("crystalBeamTarget").asList((var0x) -> {
         return var0x.asInt(0);
      });
      BlockPos var3;
      if (var2.size() == 3) {
         var3 = new BlockPos((Integer)var2.get(0), (Integer)var2.get(1), (Integer)var2.get(2));
      } else {
         var3 = null;
      }

      return new SpikeConfiguration(var0.get("crystalInvulnerable").asBoolean(false), var1, var3);
   }

   public boolean isCrystalInvulnerable() {
      return this.crystalInvulnerable;
   }

   public List getSpikes() {
      return this.spikes;
   }

   @Nullable
   public BlockPos getCrystalBeamTarget() {
      return this.crystalBeamTarget;
   }
}
