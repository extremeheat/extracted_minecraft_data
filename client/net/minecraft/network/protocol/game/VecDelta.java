package net.minecraft.network.protocol.game;

import io.netty.handler.codec.DecoderException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.PositionPath;
import net.minecraft.world.entity.PositionStep;
import net.minecraft.world.phys.Vec3;
import org.jspecify.annotations.Nullable;

public sealed interface VecDelta {
   VecDelta ZERO = new Linear((short)0, (short)0, (short)0);

   int stepCount();

   boolean hasDeltaX();

   boolean hasDeltaZ();

   PositionPath decode(VecDeltaCodec positionCodec);

   static VecDelta read(final FriendlyByteBuf input, final int stepCount) {
      if (stepCount <= 0) {
         short xa = input.readShort();
         short ya = input.readShort();
         short za = input.readShort();
         return new Linear(xa, ya, za);
      } else {
         int maxSteps = input.readableBytes() / 7;
         if (stepCount > maxSteps) {
            throw new DecoderException("VecDelta with size " + stepCount + " is bigger than allowed " + maxSteps);
         } else {
            List<Stepped.DeltaStep> steps = new ArrayList(stepCount);

            for(int i = 0; i < stepCount; ++i) {
               int ticks = input.readVarInt();
               short xa = input.readShort();
               short ya = input.readShort();
               short za = input.readShort();
               steps.add(new Stepped.DeltaStep(xa, ya, za, ticks));
            }

            return new Stepped(steps);
         }
      }
   }

   static void write(final FriendlyByteBuf output, final VecDelta delta) {
      Objects.requireNonNull(delta);
      VecDelta var2 = delta;
      byte var3 = 0;

      while(true) {
         label66: {
            //$FF: var3->value
            //0->net/minecraft/network/protocol/game/VecDelta$Linear
            //1->net/minecraft/network/protocol/game/VecDelta$Stepped
            switch (var2.typeSwitch<invokedynamic>(var2, var3)) {
               case 0:
                  Linear var4 = (Linear)var2;
                  Linear var21 = var4;

                  try {
                     var22 = var21.xa();
                  } catch (Throwable var15) {
                     throw new MatchException(var15.toString(), var15);
                  }

                  short za = var22;
                  if (false) {
                     break label66;
                  }

                  short xa = za;
                  var21 = var4;

                  try {
                     var24 = var21.ya();
                  } catch (Throwable var14) {
                     throw new MatchException(var14.toString(), var14);
                  }

                  za = var24;
                  if (false) {
                     break label66;
                  }

                  short ya = za;
                  var21 = var4;

                  try {
                     var26 = var21.za();
                  } catch (Throwable var13) {
                     throw new MatchException(var13.toString(), var13);
                  }

                  za = var26;
                  if (false) {
                     break label66;
                  }

                  output.writeShort(xa);
                  output.writeShort(ya);
                  output.writeShort(za);
                  break;
               case 1:
                  Stepped za = (Stepped)var2;
                  Stepped var10000 = za;

                  try {
                     var20 = var10000.steps();
                  } catch (Throwable var12) {
                     throw new MatchException(var12.toString(), var12);
                  }

                  for(Stepped.DeltaStep step : var20) {
                     output.writeVarInt(step.ticks);
                     output.writeShort(step.xa);
                     output.writeShort(step.ya);
                     output.writeShort(step.za);
                  }
                  break;
               default:
                  throw new MatchException((String)null, (Throwable)null);
            }

            return;
         }

         var3 = 1;
      }
   }

   public static record Linear(short xa, short ya, short za) implements VecDelta {
      public Linear {
         super();
      }

      public int stepCount() {
         return 0;
      }

      public boolean hasDeltaX() {
         return this.xa != 0;
      }

      public boolean hasDeltaZ() {
         return this.za != 0;
      }

      public PositionPath decode(final VecDeltaCodec positionCodec) {
         Vec3 pos = positionCodec.decode((long)this.xa, (long)this.ya, (long)this.za);
         return PositionPath.of(pos);
      }
   }

   public static record Stepped(List<DeltaStep> steps) implements VecDelta {
      private static final int MIN_BYTES_PER_STEP = 7;

      public Stepped {
         super();
      }

      public int stepCount() {
         return this.steps.size();
      }

      public boolean hasDeltaX() {
         for(DeltaStep step : this.steps) {
            if (step.xa != 0) {
               return true;
            }
         }

         return false;
      }

      public boolean hasDeltaZ() {
         for(DeltaStep step : this.steps) {
            if (step.za != 0) {
               return true;
            }
         }

         return false;
      }

      public PositionPath decode(final VecDeltaCodec positionCodec) {
         if (this.steps.isEmpty()) {
            return PositionPath.of(positionCodec.getBase());
         } else {
            List<PositionStep> output = new ArrayList(this.steps.size());
            Vec3 originalBase = positionCodec.getBase();

            for(DeltaStep e : this.steps) {
               Vec3 pos = positionCodec.decode((long)e.xa, (long)e.ya, (long)e.za);
               output.add(new PositionStep(pos, e.ticks));
               positionCodec.setBase(pos);
            }

            positionCodec.setBase(originalBase);
            return PositionPath.stepped(output);
         }
      }

      public static @Nullable Stepped tryEncode(final VecDeltaCodec positionCodec, final List<PositionStep> steps) {
         if (steps.isEmpty()) {
            return new Stepped(List.of());
         } else {
            List<DeltaStep> output = new ArrayList(steps.size());
            Vec3 originalBase = positionCodec.getBase();

            for(PositionStep step : steps) {
               Vec3 pos = step.position();
               long xa = positionCodec.encodeX(pos);
               long ya = positionCodec.encodeY(pos);
               long za = positionCodec.encodeZ(pos);
               if (VecDeltaCodec.isDeltaTooBig(xa, ya, za)) {
                  positionCodec.setBase(originalBase);
                  return null;
               }

               output.add(new DeltaStep((short)((int)xa), (short)((int)ya), (short)((int)za), step.tickOffset()));
               positionCodec.setBase(pos);
            }

            positionCodec.setBase(originalBase);
            return new Stepped(output);
         }
      }

      public static record DeltaStep(short xa, short ya, short za, int ticks) {
         public DeltaStep {
            super();
         }
      }
   }
}
