package com.player_level_skills.util;

import com.player_level_skills.mixin.item.PersistentProjectileEntityAccessor;
import net.fabricmc.fabric.api.tag.convention.v2.ConventionalBlockTags;
import net.fabricmc.fabric.api.tag.convention.v2.ConventionalItemTags;
import com.player_level_skills.access.LevelManagerAccess;
import com.player_level_skills.entity.LevelExperienceOrbEntity;
import com.player_level_skills.init.ConfigInit;
import com.player_level_skills.level.LevelManager;
import com.player_level_skills.level.SkillBonus;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.enchantment.Enchantment;
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
import net.minecraft.loot.context.LootWorldContext;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class BonusHelper {

    public static void bowBonus(LivingEntity shooter, ProjectileEntity projectile) {
        if (shooter instanceof PlayerEntity playerEntity && projectile instanceof PersistentProjectileEntity arrow) {
            LevelManager levelManager = ((LevelManagerAccess) playerEntity).getLevelManager();

            // 1. Pegamos o dano base inicial (Ex: 2.0)
            double damageToApply = ((PersistentProjectileEntityAccessor) arrow).getDamage();
            System.out.println("[DEBUG] Dano Base Inicial: " + damageToApply);

            // 2. Aplicamos primeiro o bônus fixo de nível
            if (LevelManager.BONUSES.containsKey("bowDamage")) {
                SkillBonus skillBonus = LevelManager.BONUSES.get("bowDamage");
                int level = levelManager.getPlayerSkills().get(skillBonus.getId()).getLevel();
                if (level >= skillBonus.getLevel()) {
                    double fixBonus = ConfigInit.CONFIG.bowDamageBonus * level;
                    damageToApply += fixBonus; // SOMAMOS ao valor atual
                    System.out.println("[DEBUG] Dano + Bônus Fixo: " + damageToApply);
                }
            }

            // 3. Agora, sobre o valor já aumentado, tentamos dobrar
            if (LevelManager.BONUSES.containsKey("bowDoubleDamageChance")) {
                SkillBonus skillBonus = LevelManager.BONUSES.get("bowDoubleDamageChance");
                int level = levelManager.getPlayerSkills().get(skillBonus.getId()).getLevel();
                if (level >= skillBonus.getLevel() && playerEntity.getRandom().nextFloat() <= (ConfigInit.CONFIG.bowDoubleDamageChanceBonus * level)) {
                    damageToApply *= 2.0D; // DOBRAMOS o valor que já tem o bônus fixo
                    System.out.println("[DEBUG] SORTE! Dano Final Dobrado: " + damageToApply);
                }
            }

            // 4. APLICAMOS UMA ÚNICA VEZ NO FINAL
            arrow.setDamage(damageToApply);
        }
    }



    public static void crossbowBonus(LivingEntity shooter, ProjectileEntity projectile) {
        if (shooter instanceof PlayerEntity playerEntity && projectile instanceof PersistentProjectileEntity persistentProjectileEntity) {
            LevelManager levelManager = ((LevelManagerAccess) playerEntity).getLevelManager();
            // 1. Pegamos o dano base inicial (Ex: 2.0)
            double damageToApply = ((PersistentProjectileEntityAccessor) persistentProjectileEntity).getDamage();
            System.out.println("[DEBUG] Dano Base Inicial: " + damageToApply);

            // 2. Aplicamos primeiro o bônus fixo de nível
            if (LevelManager.BONUSES.containsKey("crossbowDamage")) {
                SkillBonus skillBonus = LevelManager.BONUSES.get("crossbowDamage");
                int level = levelManager.getPlayerSkills().get(skillBonus.getId()).getLevel();
                if (level >= skillBonus.getLevel()) {
                    double fixBonus = ConfigInit.CONFIG.crossbowDamageBonus * level;
                    damageToApply += fixBonus; // SOMAMOS ao valor atual
                    System.out.println("[DEBUG] Dano + Bônus Fixo: " + damageToApply);
                }
            }

            // 3. Agora, sobre o valor já aumentado, tentamos dobrar
            if (LevelManager.BONUSES.containsKey("crossbowDoubleDamageChance")) {
                SkillBonus skillBonus = LevelManager.BONUSES.get("crossbowDoubleDamageChance");
                int level = levelManager.getPlayerSkills().get(skillBonus.getId()).getLevel();
                if (level >= skillBonus.getLevel() && playerEntity.getRandom().nextFloat() <= (ConfigInit.CONFIG.crossbowDoubleDamageChanceBonus * level)) {
                    damageToApply *= 2.0D; // DOBRAMOS o valor que já tem o bônus fixo
                    System.out.println("[DEBUG] SORTE! Dano Final Dobrado: " + damageToApply);
                }
            }
            persistentProjectileEntity.setDamage(damageToApply);
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
            if (level >= skillBonus.getLevel() && playerEntity.getRandom().nextFloat() <= (level * ConfigInit.CONFIG.potionEffectChanceBonus)) {
                float amplifier = level * ConfigInit.CONFIG.potionEffectAmplifier;
                return new StatusEffectInstance(statusEffectInstance.getEffectType(), statusEffectInstance.getDuration(),
                        (int) (statusEffectInstance.getAmplifier() + amplifier), statusEffectInstance.isAmbient(),
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
            if (level >= skillBonus.getLevel() && playerEntity.getRandom().nextFloat() <= level * ConfigInit.CONFIG.deathGraceChanceBonus) {
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

//    public static void miningDropChanceBonus(PlayerEntity playerEntity, BlockState state, BlockPos pos, LootWorldContext.Builder builder) {
//        if (state.isIn(ConventionalBlockTags.ORES) && EnchantmentHelper.getEquipmentLevel(playerEntity.getEntityWorld().getRegistryManager().getOrThrow(RegistryKeys.ENCHANTMENT).getOrThrow(Enchantments.SILK_TOUCH), playerEntity) <= 0) {
//            if (LevelManager.BONUSES.containsKey("miningDropChance")) {
//                LevelManager levelManager = ((LevelManagerAccess) playerEntity).getLevelManager();
//                SkillBonus skillBonus = LevelManager.BONUSES.get("miningDropChance");
//                int level = levelManager.getPlayerSkills().get(skillBonus.getId()).getLevel();
//                if (level >= skillBonus.getLevel() && playerEntity.getRandom().nextFloat() <= level * ConfigInit.CONFIG.miningDropChanceBonus) {
//                    List<ItemStack> list = state.getDroppedStacks(builder);
//                    if (!list.isEmpty()) {
//                        Block.dropStack(playerEntity.getEntityWorld(), pos, state.getDroppedStacks(builder).getFirst().split(1));
//                    }
//                }
//            }
//        }
//    }

    public static void miningDropChanceBonus(PlayerEntity playerEntity, BlockState state, BlockPos pos, LootWorldContext.Builder builder) {
        if (state.isIn(ConventionalBlockTags.ORES)) {
            LevelManager levelManager = ((LevelManagerAccess) playerEntity).getLevelManager();

            RegistryEntry<Enchantment> silkEntry = playerEntity.getRegistryManager()
                    .getOrThrow(RegistryKeys.ENCHANTMENT)
                    .getEntry(Enchantments.SILK_TOUCH.getValue()).orElse(null);

            int silkLevel = EnchantmentHelper.getLevel(silkEntry, playerEntity.getMainHandStack());

            // Verificamos se o Silk Touch está ATIVO (tem o encanto E tem nível pra usar)
            boolean silkAtivoEReal = silkLevel > 0 && levelManager.hasRequiredEnchantmentLevel(silkEntry, silkLevel);

            // Se o Silk Touch NÃO estiver ativo (ou porque não tem, ou porque o nível é baixo), permitimos o bônus
            if (!silkAtivoEReal) {
                if (LevelManager.BONUSES.containsKey("miningDropChance")) {
                    SkillBonus skillBonus = LevelManager.BONUSES.get("miningDropChance");
                    int currentLevel = levelManager.getPlayerSkills().get(skillBonus.getId()).getLevel();

                    // 2. LÓGICA DE CHANCE ESCALÁVEL (Nível 10+)
                    if (currentLevel >= skillBonus.getLevel()) { // skillBonus.getLevel() é 10

                        // Cálculo solicitado: Nível atual * Valor da Configuração
                        // Ex: 10 * 0.02 = 0.2 (20%) | 25 * 0.02 = 0.5 (50%)
                        float chance = (float) currentLevel * ConfigInit.CONFIG.miningDropChanceBonus;

                        // Garante que a chance não ultrapasse 100% (1.0)
                        if (playerEntity.getRandom().nextFloat() <= Math.min(1.0f, chance)) {
                            List<ItemStack> list = state.getDroppedStacks(builder);
                            if (!list.isEmpty()) {
                                // Dropa 1 unidade extra do que o bloco soltaria
                                Block.dropStack(playerEntity.getEntityWorld(), pos, list.getFirst().copy().split(1));
                            }
                        }
                    }
                }
            }
        }
    }

    public static void plantDropChanceBonus(PlayerEntity playerEntity, BlockState state, BlockPos pos) {
        if (EnchantmentHelper.getEquipmentLevel(playerEntity.getEntityWorld().getRegistryManager().getOrThrow(RegistryKeys.ENCHANTMENT).getOrThrow(Enchantments.SILK_TOUCH), playerEntity) <= 0) {
            if (LevelManager.BONUSES.containsKey("plantDropChance")) {
                LevelManager levelManager = ((LevelManagerAccess) playerEntity).getLevelManager();
                SkillBonus skillBonus = LevelManager.BONUSES.get("plantDropChance");
                int level = levelManager.getPlayerSkills().get(skillBonus.getId()).getLevel();
                if (level >= skillBonus.getLevel() && playerEntity.getRandom().nextFloat() <= level * ConfigInit.CONFIG.plantDropChanceBonus) {
                    List<ItemStack> list = Block.getDroppedStacks(state, (ServerWorld) playerEntity.getEntityWorld(), pos, null);
                    for (ItemStack itemStack : list) {
                        if (itemStack.isIn(ConventionalItemTags.CROPS)) {
                            Block.dropStack(playerEntity.getEntityWorld(), pos, itemStack);
                            break;
                        }
                    }
                }
            }
        }
    }

//    public static void foodIncreasionBonus(PlayerEntity playerEntity, ItemStack itemStack) {
//        if (LevelManager.BONUSES.containsKey("foodIncreasion") && itemStack.get(DataComponentTypes.FOOD) != null) {
//            LevelManager levelManager = ((LevelManagerAccess) playerEntity).getLevelManager();
//            SkillBonus skillBonus = LevelManager.BONUSES.get("foodIncreasion");
//            int level = levelManager.getPlayerSkills().get(skillBonus.getId()).getLevel();
//            if (level >= skillBonus.getLevel()) {
//                FoodComponent foodComponent = itemStack.get(DataComponentTypes.FOOD);
//                float multiplier = level * ConfigInit.CONFIG.foodIncreasionBonus;
//                playerEntity.getHungerManager().eat(new FoodComponent((int) (foodComponent.nutrition() * multiplier), (int) (foodComponent.saturation() * multiplier), true));
//            }
//        }
//    }




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


    public static int anvilXpDiscountBonus(PlayerEntity playerEntity, int levelCost, boolean keepSecondSlot) {
    //        if (levelCost > ConfigInit.CONFIG.anvilXpCap && anvilXpCapBonus(playerEntity)) {
    //            return ConfigInit.CONFIG.anvilXpCap;
    //        }
        int custoFinal = levelCost;
        if (LevelManager.BONUSES.containsKey("anvilXpDiscount")) {
            LevelManager levelManager = ((LevelManagerAccess) playerEntity).getLevelManager();
            SkillBonus skillBonus = LevelManager.BONUSES.get("anvilXpDiscount");
            int level = levelManager.getPlayerSkills().get(skillBonus.getId()).getLevel();
            if (level >= skillBonus.getLevel()) {
                custoFinal = Math.round(levelCost * (1.0f - level * ConfigInit.CONFIG.anvilXpDiscountBonus));
            }

        }

        if (keepSecondSlot && custoFinal >= 40) {
            if (anvilXpCapBonus(playerEntity)) {
                return 39; // Trava em 39 para permitir renomear sem o erro "Muito Caro"
            }
        }
        return custoFinal ;
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
                playerEntity.addStatusEffect(new StatusEffectInstance(
                        StatusEffects.ABSORPTION,
                        400, 10, false, false, true));
                playerEntity.setAbsorptionAmount(level * ConfigInit.CONFIG.healthAbsorptionBonus);
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
        if (LevelManager.BONUSES.containsKey("meleeKockbackAttackChance")) {
            LevelManager levelManager = ((LevelManagerAccess) playerEntity).getLevelManager();
            SkillBonus skillBonus = LevelManager.BONUSES.get("meleeKockbackAttackChance");
            int level = levelManager.getPlayerSkills().get(skillBonus.getId()).getLevel();
            if (level >= skillBonus.getLevel() && playerEntity.getRandom().nextFloat() <= level * ConfigInit.CONFIG.meleeKnockbackAttackChanceBonus) {
                return true;
            }
        }
        return false;
    }

    public static boolean meleeCriticalAttackChanceBonus(PlayerEntity playerEntity) {
        if (LevelManager.BONUSES.containsKey("meleeCriticalAttackChance")) {
            LevelManager levelManager = ((LevelManagerAccess) playerEntity).getLevelManager();
            SkillBonus skillBonus = LevelManager.BONUSES.get("meleeCriticalAttackChance");
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

            if (level >= skillBonus.getLevel() && playerEntity.getRandom().nextFloat() <= level * ConfigInit.CONFIG.damageReflectionChanceBonus) {

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
            if (level >= skillBonus.getLevel() && playerEntity.getRandom().nextFloat() <= (level * ConfigInit.CONFIG.evadingDamageChanceBonus)) {
                return true;
            }
        }
        return false;
    }


}

