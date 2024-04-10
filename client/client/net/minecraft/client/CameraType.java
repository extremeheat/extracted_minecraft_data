package net.minecraft.client;

public enum CameraType {
   FIRST_PERSON(true, false),
   THIRD_PERSON_BACK(false, false),
   THIRD_PERSON_FRONT(false, true);

   private static final CameraType[] VALUES = values();
   private final boolean firstPerson;
   private final boolean mirrored;

   private CameraType(final boolean param3, final boolean param4) {
      this.firstPerson = nullxx;
      this.mirrored = nullxxx;
   }

   public boolean isFirstPerson() {
      return this.firstPerson;
   }

   public boolean isMirrored() {
      return this.mirrored;
   }

   public CameraType cycle() {
      return VALUES[(this.ordinal() + 1) % VALUES.length];
   }
}
