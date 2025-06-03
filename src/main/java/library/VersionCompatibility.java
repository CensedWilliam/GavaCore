package library;

import org.bukkit.Particle;
import org.bukkit.potion.PotionEffectType;

/**
 * Version compatibility utility class for Minecraft 1.21.5
 * Provides static methods to get the correct particle types and potion effect types
 */
public class VersionCompatibility {

    // Particle types for 1.21.5
    public static Particle getExplosionParticle() {
        return Particle.EXPLOSION;
    }

    public static Particle getExplosionEmitterParticle() {
        return Particle.EXPLOSION_EMITTER;
    }

    public static Particle getBlockParticle() {
        return Particle.BLOCK;
    }

    public static Particle getDustParticle() {
        return Particle.DUST;
    }

    public static Particle getSmokeParticle() {
        return Particle.SMOKE;
    }

    public static Particle getItemParticle() {
        return Particle.ITEM;
    }

    // Potion effect types for 1.21.5 (with corrected names)
    public static PotionEffectType getSlownessEffect() {
        return PotionEffectType.SLOWNESS;
    }

    public static PotionEffectType getJumpBoostEffect() {
        return PotionEffectType.JUMP_BOOST;
    }

    public static PotionEffectType getNauseaEffect() {
        return PotionEffectType.NAUSEA;
    }

    public static PotionEffectType getHasteEffect() {
        return PotionEffectType.HASTE;
    }

    public static PotionEffectType getWeaknessEffect() {
        return PotionEffectType.WEAKNESS;
    }

    public static PotionEffectType getBlindnessEffect() {
        return PotionEffectType.BLINDNESS;
    }

    public static PotionEffectType getLevitationEffect() {
        return PotionEffectType.LEVITATION;
    }
} 