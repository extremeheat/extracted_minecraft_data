package com.mojang.realmsclient.gui.screens.configuration;

import java.time.Duration;
import java.time.Instant;
import net.minecraft.network.chat.Component;
import org.jspecify.annotations.Nullable;

public enum InviteCodeExpiration {
   THIRTY_MINUTES(Component.translatable("mco.configure.world.invite_codes.expiration.30_minutes"), Duration.ofMinutes(30L)),
   ONE_HOUR(Component.translatable("mco.configure.world.invite_codes.expiration.1_hour"), Duration.ofHours(1L)),
   SIX_HOURS(Component.translatable("mco.configure.world.invite_codes.expiration.6_hours"), Duration.ofHours(6L)),
   TWELVE_HOURS(Component.translatable("mco.configure.world.invite_codes.expiration.12_hours"), Duration.ofHours(12L)),
   ONE_DAY(Component.translatable("mco.configure.world.invite_codes.expiration.1_day"), Duration.ofDays(1L)),
   SEVEN_DAYS(Component.translatable("mco.configure.world.invite_codes.expiration.7_days"), Duration.ofDays(7L)),
   NEVER(Component.translatable("mco.configure.world.invite_codes.expiration.never"), (Duration)null);

   private final Component label;
   private final @Nullable Duration duration;

   private InviteCodeExpiration(final Component label, final Duration duration) {
      this.label = label;
      this.duration = duration;
   }

   public Component getLabel() {
      return this.label;
   }

   static @Nullable Instant resolveExpirationDate(final @Nullable Instant currentExpirationDate, final InviteCodeExpiration selectedExpiration, final boolean expirationChanged, final Instant now) {
      if (!expirationChanged) {
         return currentExpirationDate;
      } else {
         Duration duration = selectedExpiration.duration;
         return duration != null ? now.plus(duration) : null;
      }
   }

   public static InviteCodeExpiration closestTo(final Instant expirationDate) {
      long remainingMillis = Duration.between(Instant.now(), expirationDate).toMillis();
      InviteCodeExpiration closest = NEVER;
      long bestDiff = 9223372036854775807L;

      for(InviteCodeExpiration candidate : values()) {
         if (candidate.duration != null) {
            long diff = Math.abs(candidate.duration.toMillis() - remainingMillis);
            if (diff < bestDiff) {
               bestDiff = diff;
               closest = candidate;
            }
         }
      }

      return closest;
   }

   // $FF: synthetic method
   private static InviteCodeExpiration[] $values() {
      return new InviteCodeExpiration[]{THIRTY_MINUTES, ONE_HOUR, SIX_HOURS, TWELVE_HOURS, ONE_DAY, SEVEN_DAYS, NEVER};
   }
}
