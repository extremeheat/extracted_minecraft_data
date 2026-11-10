package net.minecraft.client.renderer.debug;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.debug.DebugEntryEnvironmentAttribute;
import net.minecraft.client.renderer.culling.Frustum;
import net.minecraft.core.BlockPos;
import net.minecraft.gizmos.GizmoStyle;
import net.minecraft.gizmos.Gizmos;
import net.minecraft.gizmos.TextGizmo;
import net.minecraft.util.ARGB;
import net.minecraft.util.Mth;
import net.minecraft.util.debug.DebugValueAccess;
import net.minecraft.world.attribute.EnvironmentAttribute;
import net.minecraft.world.attribute.SpatialAttributeInterpolator;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import org.joml.Vector3fc;
import org.joml.Vector4fc;
import org.jspecify.annotations.Nullable;

public class EnvironmentAttributeDebugRenderer implements DebugRenderer.SimpleDebugRenderer {
   private static final int HEATMAP_RADIUS = 16;
   private static final float HEATMAP_CELL_HEIGHT = 0.0625F;
   private static final float HEATMAP_OPACITY = 0.25F;
   private final Minecraft minecraft;
   private @Nullable Level cachedLevel;
   private @Nullable EnvironmentAttribute<?> cachedAttribute;
   private @Nullable BlockPos cachedCenter;
   private long cachedGameTime = -9223372036854775808L;
   private List<HeatmapSample> cachedSamples = List.of();

   public EnvironmentAttributeDebugRenderer(final Minecraft minecraft) {
      super();
      this.minecraft = minecraft;
   }

   public void emitGizmos(final double camX, final double camY, final double camZ, final DebugValueAccess debugValues, final Frustum frustum, final float partialTicks) {
      Level level = this.minecraft.level;
      if (level != null && !this.minecraft.showOnlyReducedInfo()) {
         EnvironmentAttribute<?> attribute = DebugEntryEnvironmentAttribute.getAttribute();
         if (attribute != null) {
            this.renderGizmoHeatmap(level, attribute, BlockPos.containing(camX, camY, camZ).below());
         }
      } else {
         this.forgetCaches();
      }
   }

   private void renderGizmoHeatmap(final Level level, final EnvironmentAttribute<?> attribute, final BlockPos center) {
      for(HeatmapSample sample : this.heatmapSamples(level, attribute, center)) {
         BlockPos pos = sample.pos();
         if (!attribute.isSyncable() && this.minecraft.getSingleplayerServer() == null) {
            Gizmos.billboardText("!", new Vec3((double)pos.getX() + 0.5, (double)pos.getY() + 0.5, (double)pos.getZ() + 0.5), TextGizmo.Style.whiteAndCentered());
         } else {
            Gizmos.cuboid(new AABB((double)pos.getX() + 0.05, (double)pos.getY() + 0.05, (double)pos.getZ() + 0.05, (double)pos.getX() + 0.95, (double)pos.getY() + 0.05 + 0.0625, (double)pos.getZ() + 0.95), GizmoStyle.fill(sample.color()));
         }
      }

   }

   private List<HeatmapSample> heatmapSamples(final Level level, final EnvironmentAttribute<?> attribute, final BlockPos center) {
      long gameTime = level.getGameTime();
      if (this.cachedLevel == level && this.cachedAttribute == attribute && center.equals(this.cachedCenter) && this.cachedGameTime == gameTime) {
         return this.cachedSamples;
      } else {
         SpatialAttributeInterpolator biomeInterpolator = attribute.isSpatiallyInterpolated() ? new SpatialAttributeInterpolator() : null;
         List<HeatmapSample> samples = new ArrayList();

         for(BlockPos cursor : BlockPos.betweenClosed(center.offset(-16, 0, -16), center.offset(16, 0, 16))) {
            BlockPos pos = cursor.immutable();
            if (biomeInterpolator != null) {
               biomeInterpolator.sampleBiomes(level, Vec3.atCenterOf(pos));
            }

            Object value = sampleValue(level, attribute, pos, biomeInterpolator);
            samples.add(new HeatmapSample(pos, ARGB.multiplyAlpha(colorFromValue(value), 0.25F)));
         }

         this.cachedLevel = level;
         this.cachedAttribute = attribute;
         this.cachedCenter = center;
         this.cachedGameTime = gameTime;
         this.cachedSamples = List.copyOf(samples);
         return this.cachedSamples;
      }
   }

   private void forgetCaches() {
      this.cachedLevel = null;
      this.cachedAttribute = null;
      this.cachedCenter = null;
      this.cachedGameTime = -9223372036854775808L;
      this.cachedSamples = List.of();
   }

   private static <Value> Value sampleValue(final Level level, final EnvironmentAttribute<Value> attribute, final BlockPos pos, final @Nullable SpatialAttributeInterpolator biomeInterpolator) {
      return (Value)(biomeInterpolator == null ? level.environmentAttributes().getValue(attribute, pos) : level.environmentAttributes().getValue(attribute, Vec3.atCenterOf(pos), biomeInterpolator));
   }

   private static int colorFromValue(final Object value) {
      Objects.requireNonNull(value);
      Object var1 = value;
      byte var2 = 0;

      while(true) {
         int var10000;
         //$FF: var2->value
         //0->org/joml/Vector3fc
         //1->org/joml/Vector4fc
         //2->java/lang/Boolean
         //3->java/lang/Number
         //4->java/util/List
         switch (((Class)var1).typeSwitch<invokedynamic>(var1, var2)) {
            case 0:
               Vector3fc rgb = (Vector3fc)var1;
               var10000 = ARGB.colorFromVector3f(rgb);
               return var10000;
            case 1:
               Vector4fc argb = (Vector4fc)var1;
               var10000 = ARGB.colorFromVector4f(argb);
               return var10000;
            case 2:
               Boolean enabled = (Boolean)var1;
               var10000 = enabled ? -16711936 : -65536;
               return var10000;
            case 3:
            case 4:
               if (!(var1 instanceof Number) && !(var1 instanceof List)) {
                  var2 = 5;
                  break;
               }

               var10000 = -8355712;
               return var10000;
            default:
               int hash = String.valueOf(value).hashCode();
               float hue = (float)(hash & '\uffff') / 65535.0F;
               var10000 = Mth.hsvToArgb(hue, 0.65F, 1.0F, 255);
               return var10000;
         }
      }
   }

   private static record HeatmapSample(BlockPos pos, int color) {
      private HeatmapSample {
         super();
      }
   }
}
