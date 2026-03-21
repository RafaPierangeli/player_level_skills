package com.player_level_skills.util;

import net.fabricmc.fabric.api.tag.convention.v2.ConventionalBlockTags;
import net.fabricmc.fabric.api.tag.convention.v2.ConventionalItemTags;
import com.player_level_skills.access.LevelManagerAccess;
import com.player_level_skills.entity.LevelExperienceOrbEntity;
import com.player_level_skills.config.ConfigInit;
import com.player_level_skills.level.LevelManager;
import com.player_level_skills.level.SkillBonus;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.FoodComponent;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.passive.MerchantEntity;
import net.minecraft.entity.passive.PassiveEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.PersistentProjectileEntity;
import net.minecraft.entity.projectile.ProjectileEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.loot.context.LootContext;
import net.minecraft.loot.context.LootWorldContext;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Optional;

public class BonusHelper {

    public static void bowBonus(LivingEntity shooter, ProjectileEntity projectile) {
        if (shooter instanceof PlayerEntity playerEntity && projectile instanceof PersistentProjectileEntity persistentProjectileEntity) {
            LevelManager levelManager = ((LevelManagerAccess) playerEntity).getLevelManager();
            if (LevelManager.BONUSES.containsKey("bowDamage")) {
                SkillBonus skillBonus = LevelManager.BONUSES.get("bowDamage");
                int level = levelManager.getPlayerSkills().get(skillBonus.getId()).getLevel();
                if (level >= skillBonus.getLevel()) {
                    //persistentProjectileEntity.setDamage(persistentProjectileEntity.getDamageSources() + ConfigInit.CONFIG.bowDamageBonus * level);
                }
            }
            if (LevelManager.BONUSES.containsKey("bowDoubleDamageChance")) {
                SkillBonus skillBonus = LevelManager.BONUSES.get("bowDoubleDamageChance");
                int level = levelManager.getPlayerSkills().get(skillBonus.getId()).getLevel();
                if (level >= skillBonus.getLevel() && playerEntity.getRandom().nextFloat() <= ConfigInit.CONFIG.bowDoubleDamageChanceBonus) {
                    //persistentProjectileEntity.setDamage(persistentProjectileEntity.getDamage() * 2D);
                }
            }
        }
    }

    public static void crossbowBonus(LivingEntity shooter, ProjectileEntity projectile) {
        if (shooter instanceof PlayerEntity playerEntity && projectile instanceof PersistentProjectileEntity persistentProjectileEntity) {
            LevelManager levelManager = ((LevelManagerAccess) playerEntity).getLevelManager();
            if (LevelManager.BONUSES.containsKey("crossbowDamage")) {
                SkillBonus skillBonus = LevelManager.BONUSES.get("crossbowDamage");
                int level = levelManager.getPlayerSkills().get(skillBonus.getId()).getLevel();
                if (level >= skillBonus.getLevel()) {
                    //persistentProjectileEntity.setDamage(persistentProjectileEntity.getDamage() + ConfigInit.CONFIG.crossbowDamageBonus * level);
                }
            }
            if (LevelManager.BONUSES.containsKey("crossbowDoubleDamageChance")) {
                SkillBonus skillBonus = LevelManager.BONUSES.get("crossbowDoubleDamageChance");
                int level = levelManager.getPlayerSkills().get(skillBonus.getId()).getLevel();
                if (level >= skillBonus.getLevel() && playerEntity.getRandom().nextFloat() <= ConfigInit.CONFIG.crossbowDoubleDamageChanceBonus) {
                    //persistentProjectileEntity.setDamage(persistentProjectileEntity.getDamage() * 2D);
                }
            }
        }
    }

    public static boolean itemDamageChanceBonus(@Nullable PlayerEntity playerEntity) {
        if (playerEntity != null && LevelManager.BONUSES.containsKey("itemDamageChance")) {
            LevelManager levelManager = ((LevelManagerAccess) playerEntity).getLevelManager();
            SkillBonus skillBonus = LevelManager.BONUSES.get("itemDamageChance");
            int level = levelManager.getPlayerSkills().get(skillBonus.getId()).getLevel();
            if (level >= skillBonus.getLevel() && playerEntity.getRandom().nextFloat() <= ConfigInit.CONFIG.itemDamageChanceBonus * level) {
                return true;
            }
        }
        return false;
    }

    public static StatusEffectInstance potionEffectChanceBonus(@Nullable PlayerEntity playerEntity, StatusEffectInstance statusEffectInstance) {
        if (playerEntity != null && LevelManager.BONUSES.containsKey("potionEffectChance")) {
            LevelManager levelManager = ((LevelManagerAccess) playerEntity).getLevelManager();
            SkillBonus skillBonus = LevelManager.BONUSES.get("potionEffectChance");
            int level = levelManager.getPlayerSkills().get(skillBonus.getId()).getLevel();
            if (level >= skillBonus.getLevel() && playerEntity.getRandom().nextFloat() <= ConfigInit.CONFIG.potionEffectChanceBonus) {
                return new StatusEffectInstance(statusEffectInstance.getEffectType(), statusEffectInstance.getDuration(),
                        statusEffectInstance.getAmplifier() + 1, statusEffectInstance.isAmbient(),
                        statusEffectInstance.shouldShowParticles(), statusEffectInstance.shouldShowIcon());
            }
        }
        return statusEffectInstance;
    }

    public static void breedTwinChanceBonus(ServerWorld world, PlayerEntity playerEntity, PassiveEntity animalEntity, PassiveEntity otherAnimalEntity) {
        if (LevelManager.BONUSES.containsKey("breedTwinChance")) {
            LevelManager levelManager = ((LevelManagerAccess) playerEntity).getLevelManager();
            SkillBonus skillBonus = LevelManager.BONUSES.get("breedTwinChance");
            int level = levelManager.getPlayerSkills().get(skillBonus.getId()).getLevel();
            if (level >= skillBonus.getLevel() && playerEntity.getRandom().nextFloat() <= ConfigInit.CONFIG.twinBreedChanceBonus) {
                PassiveEntity extraPassiveEntity = animalEntity.createChild(world, otherAnimalEntity);
                extraPassiveEntity.setBaby(true);
                extraPassiveEntity.refreshPositionAndAngles(animalEntity.getX(), animalEntity.getY(), animalEntity.getZ(), playerEntity.getRandom().nextFloat() * 360F, 0.0F);
                world.spawnEntityAndPassengers(extraPassiveEntity);
            }
        }
    }

    public static float fallDamageReductionBonus(PlayerEntity playerEntity) {
        if (LevelManager.BONUSES.containsKey("fallDamageReduction")) {
            LevelManager levelManager = ((LevelManagerAccess) playerEntity).getLevelManager();
            SkillBonus skillBonus = LevelManager.BONUSES.get("fallDamageReduction");
            int level = levelManager.getPlayerSkills().get(skillBonus.getId()).getLevel();
            if (level >= skillBonus.getLevel()) {
                return level * ConfigInit.CONFIG.fallDamageReductionBonus;
            }
        }
        return 0.0f;
    }

    public static boolean deathGraceChanceBonus(PlayerEntity playerEntity) {
        if (LevelManager.BONUSES.containsKey("deathGraceChance")) {
            LevelManager levelManager = ((LevelManagerAccess) playerEntity).getLevelManager();
            SkillBonus skillBonus = LevelManager.BONUSES.get("deathGraceChance");
            int level = levelManager.getPlayerSkills().get(skillBonus.getId()).getLevel();
            if (level >= skillBonus.getLevel() && playerEntity.getRandom().nextFloat() <= ConfigInit.CONFIG.deathGraceChanceBonus) {
                playerEntity.setHealth(1.0F);
                playerEntity.clearStatusEffects();
                playerEntity.addStatusEffect(new StatusEffectInstance(StatusEffects.ABSORPTION, 100, 1));
                playerEntity.addStatusEffect(new StatusEffectInstance(StatusEffects.FIRE_RESISTANCE, 600, 0));
                return true;
            }
        }

        return false;
    }

    public static float tntStrengthBonus(PlayerEntity playerEntity) {
        if (LevelManager.BONUSES.containsKey("tntStrength")) {
            LevelManager levelManager = ((LevelManagerAccess) playerEntity).getLevelManager();
            SkillBonus skillBonus = LevelManager.BONUSES.get("tntStrength");
            int level = levelManager.getPlayerSkills().get(skillBonus.getId()).getLevel();
            if (level >= skillBonus.getLevel()) {
                return ConfigInit.CONFIG.tntStrengthBonus;
            }
        }
        return 0.0f;
    }

    public static float priceDiscountBonus(PlayerEntity playerEntity) {
        if (playerEntity.hasStatusEffect(StatusEffects.HERO_OF_THE_VILLAGE)) {
            return 1.0f;
        }
        if (LevelManager.BONUSES.containsKey("priceDiscount")) {
            LevelManager levelManager = ((LevelManagerAccess) playerEntity).getLevelManager();
            SkillBonus skillBonus = LevelManager.BONUSES.get("priceDiscount");
            int level = levelManager.getPlayerSkills().get(skillBonus.getId()).getLevel();
            if (level >= skillBonus.getLevel()) {
                return 1.0f - (level * ConfigInit.CONFIG.priceDiscountBonus);
            }
        }
        return 1.0f;
    }

    public static void tradeXpBonus(ServerWorld serverWorld, @Nullable PlayerEntity playerEntity, MerchantEntity merchantEntity, int amount) {
        amount = (int) (amount * ConfigInit.CONFIG.tradingXPMultiplier);
        if (amount > 0) {
            if (playerEntity != null) {
                if (LevelManager.BONUSES.containsKey("tradeXp")) {
                    LevelManager levelManager = ((LevelManagerAccess) playerEntity).getLevelManager();
                    SkillBonus skillBonus = LevelManager.BONUSES.get("tradeXp");
                    int level = levelManager.getPlayerSkills().get(skillBonus.getId()).getLevel();
                    if (level >= skillBonus.getLevel()) {
                        amount = (int) (amount * level * ConfigInit.CONFIG.tradeXpBonus);
                    }
                }
            }
            LevelExperienceOrbEntity.spawn(serverWorld, merchantEntity.getEntityPos().add(0.0D, 0.5D, 0.0D), amount);
            // Todo: HERE
            // ? 1.0F + ConfigInit.CONFIG.basedOnMultiplier * ((PlayerStatsManagerAccess) lastCustomer).getPlayerStatsManager().getOverallLevel()
        }
    }

    public static boolean merchantImmuneBonus(PlayerEntity playerEntity) {
        if (LevelManager.BONUSES.containsKey("merchantImmune")) {
            LevelManager levelManager = ((LevelManagerAccess) playerEntity).getLevelManager();
            SkillBonus skillBonus = LevelManager.BONUSES.get("merchantImmune");
            int level = levelManager.getPlayerSkills().get(skillBonus.getId()).getLevel();
            if (level >= skillBonus.getLevel()) {
                return true;
            }
        }
        return false;
    }



    public static boolean anvilXpCapBonus(PlayerEntity playerEntity) {
        if (LevelManager.BONUSES.containsKey("anvilXpCap")) {
            LevelManager levelManager = ((LevelManagerAccess) playerEntity).getLevelManager();
            SkillBonus skillBonus = LevelManager.BONUSES.get("anvilXpCap");
            int level = levelManager.getPlayerSkills().get(skillBonus.getId()).getLevel();
            if (level >= skillBonus.getLevel()) {
                return true;
            }
        }
        return false;
    }

    public static int anvilXpDiscountBonus(PlayerEntity playerEntity, int levelCost) {
        if (levelCost > ConfigInit.CONFIG.anvilXpCap && anvilXpCapBonus(playerEntity)) {
            return ConfigInit.CONFIG.anvilXpCap;
        }
        if (LevelManager.BONUSES.containsKey("anvilXpDiscount")) {
            LevelManager levelManager = ((LevelManagerAccess) playerEntity).getLevelManager();
            SkillBonus skillBonus = LevelManager.BONUSES.get("anvilXpDiscount");
            int level = levelManager.getPlayerSkills().get(skillBonus.getId()).getLevel();
            if (level >= skillBonus.getLevel()) {
                return (int) (levelCost * (1.0f - level * ConfigInit.CONFIG.anvilXpDiscountBonus));
            }
        }
        return levelCost;
    }

    public static boolean anvilXpChanceBonus(PlayerEntity playerEntity) {
        if (LevelManager.BONUSES.containsKey("anvilXpChance")) {
            LevelManager levelManager = ((LevelManagerAccess) playerEntity).getLevelManager();
            SkillBonus skillBonus = LevelManager.BONUSES.get("anvilXpChance");
            int level = levelManager.getPlayerSkills().get(skillBonus.getId()).getLevel();
            if (level >= skillBonus.getLevel() && playerEntity.getRandom().nextFloat() <= level * ConfigInit.CONFIG.anvilXpChanceBonus) {
                return true;
            }
        }
        return false;
    }

    public static void healthRegenBonus(PlayerEntity playerEntity) {
        if (LevelManager.BONUSES.containsKey("healthRegen")) {
            LevelManager levelManager = ((LevelManagerAccess) playerEntity).getLevelManager();
            SkillBonus skillBonus = LevelManager.BONUSES.get("healthRegen");
            int level = levelManager.getPlayerSkills().get(skillBonus.getId()).getLevel();
            if (level >= skillBonus.getLevel()) {
                playerEntity.heal(level * ConfigInit.CONFIG.healthRegenBonus);
            }
        }
    }

    public static void healthAbsorptionBonus(PlayerEntity playerEntity) {
        if (LevelManager.BONUSES.containsKey("healthAbsorption")) {
            LevelManager levelManager = ((LevelManagerAccess) playerEntity).getLevelManager();
            SkillBonus skillBonus = LevelManager.BONUSES.get("healthAbsorption");
            int level = levelManager.getPlayerSkills().get(skillBonus.getId()).getLevel();
            if (level >= skillBonus.getLevel()) {
                playerEntity.setAbsorptionAmount(ConfigInit.CONFIG.healthAbsorptionBonus);
            }
        }
    }

    public static float exhaustionReductionBonus(PlayerEntity playerEntity) {
        if (LevelManager.BONUSES.containsKey("exhaustionReduction")) {
            LevelManager levelManager = ((LevelManagerAccess) playerEntity).getLevelManager();
            SkillBonus skillBonus = LevelManager.BONUSES.get("exhaustionReduction");
            int level = levelManager.getPlayerSkills().get(skillBonus.getId()).getLevel();
            if (level >= skillBonus.getLevel()) {
                return 1.0f - (level * ConfigInit.CONFIG.exhaustionReductionBonus);
            }
        }
        return 0.0f;
    }

    public static boolean meleeKnockbackAttackChanceBonus(PlayerEntity playerEntity) {
        if (LevelManager.BONUSES.containsKey("knockbackAttackChance")) {
            LevelManager levelManager = ((LevelManagerAccess) playerEntity).getLevelManager();
            SkillBonus skillBonus = LevelManager.BONUSES.get("knockbackAttackChance");
            int level = levelManager.getPlayerSkills().get(skillBonus.getId()).getLevel();
            if (level >= skillBonus.getLevel() && playerEntity.getRandom().nextFloat() <= level * ConfigInit.CONFIG.meleeKnockbackAttackChanceBonus) {
                return true;
            }
        }
        return false;
    }

    public static boolean meleeCriticalAttackChanceBonus(PlayerEntity playerEntity) {
        if (LevelManager.BONUSES.containsKey("criticalAttackChance")) {
            LevelManager levelManager = ((LevelManagerAccess) playerEntity).getLevelManager();
            SkillBonus skillBonus = LevelManager.BONUSES.get("criticalAttackChance");
            int level = levelManager.getPlayerSkills().get(skillBonus.getId()).getLevel();
            if (level >= skillBonus.getLevel() && playerEntity.getRandom().nextFloat() <= level * ConfigInit.CONFIG.meleeCriticalAttackChanceBonus) {
                return true;
            }
        }
        return false;
    }

    public static float meleeCriticalDamageBonus(PlayerEntity playerEntity) {
        if (LevelManager.BONUSES.containsKey("meleeCriticalAttackDamage")) {
            LevelManager levelManager = ((LevelManagerAccess) playerEntity).getLevelManager();
            SkillBonus skillBonus = LevelManager.BONUSES.get("meleeCriticalAttackDamage");
            int level = levelManager.getPlayerSkills().get(skillBonus.getId()).getLevel();
            if (level >= skillBonus.getLevel()) {
                return level * ConfigInit.CONFIG.meleeCriticalAttackDamageBonus;
            }
        }
        return 0.0f;
    }

    public static boolean meleeDoubleDamageBonus(PlayerEntity playerEntity) {
        if (LevelManager.BONUSES.containsKey("meleeDoubleAttackDamageChance")) {
            LevelManager levelManager = ((LevelManagerAccess) playerEntity).getLevelManager();
            SkillBonus skillBonus = LevelManager.BONUSES.get("meleeDoubleAttackDamageChance");
            int level = levelManager.getPlayerSkills().get(skillBonus.getId()).getLevel();
            if (level >= skillBonus.getLevel() && playerEntity.getRandom().nextFloat() <= ConfigInit.CONFIG.meleeDoubleAttackDamageChanceBonus) {
                return true;
            }
        }
        return false;
    }


    public static void damageReflectionBonus(PlayerEntity playerEntity, DamageSource source, float amount) {
        if (source.getAttacker() != null
                && LevelManager.BONUSES.containsKey("damageReflection")
                && LevelManager.BONUSES.containsKey("damageReflectionChance")) {

            LevelManager levelManager = ((LevelManagerAccess) playerEntity).getLevelManager();

            SkillBonus skillBonus = LevelManager.BONUSES.get("damageReflectionChance");
            int level = levelManager.getPlayerSkills().get(skillBonus.getId()).getLevel();

            if (level >= skillBonus.getLevel()
                    && playerEntity.getRandom().nextFloat() <= level * ConfigInit.CONFIG.damageReflectionChanceBonus) {

                skillBonus = LevelManager.BONUSES.get("damageReflection");
                level = levelManager.getPlayerSkills().get(skillBonus.getId()).getLevel();

                if (level >= skillBonus.getLevel()) {
                    float reflectedDamage = amount * level * ConfigInit.CONFIG.damageReflectionBonus;

                    if (playerEntity.getEntityWorld() instanceof net.minecraft.server.world.ServerWorld serverWorld) {
                        source.getAttacker().damage(serverWorld, source, reflectedDamage);
                    }
                }
            }
        }
    }

    public static boolean evadingDamageBonus(PlayerEntity playerEntity) {
        if (LevelManager.BONUSES.containsKey("evadingDamageChance")) {
            LevelManager levelManager = ((LevelManagerAccess) playerEntity).getLevelManager();
            SkillBonus skillBonus = LevelManager.BONUSES.get("evadingDamageChance");
            int level = levelManager.getPlayerSkills().get(skillBonus.getId()).getLevel();
            if (level >= skillBonus.getLevel() && playerEntity.getRandom().nextFloat() <= ConfigInit.CONFIG.evadingDamageChanceBonus) {
                return true;
            }
        }
        return false;
    }


}

