package com.player_level_skills.mixin.player;

import com.player_level_skills.access.LevelManagerAccess;
import com.player_level_skills.access.PlayerDropAccess;
import com.player_level_skills.init.ConfigInit;
import com.player_level_skills.entity.LevelExperienceOrbEntity;
import com.player_level_skills.level.LevelManager;
import com.player_level_skills.util.BonusHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.tag.DamageTypeTags;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.storage.ReadView;
import net.minecraft.storage.WriteView;
import net.minecraft.world.World;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.rule.GameRules;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(PlayerEntity.class)
public abstract class PlayerEntityMixin extends LivingEntity implements LevelManagerAccess, PlayerDropAccess {

    private final PlayerEntity playerEntity = (PlayerEntity) (Object) this;
    @Unique
    private final LevelManager levelManager = new LevelManager(playerEntity);

    @Unique
    private int killedMobsInChunk;
    @Unique
    @Nullable
    private Chunk killedMobChunk;

    public PlayerEntityMixin(EntityType<? extends LivingEntity> entityType, World world) {
        super(entityType, world);
    }

    @Inject(method = "readCustomData", at = @At(value = "TAIL"))
    public void readCustomData(ReadView view, CallbackInfo info) {
        this.levelManager.readNbt(view);
    }

    @Inject(method = "writeCustomData", at = @At(value = "TAIL"))
    public void writeCustomData(WriteView view, CallbackInfo info) {
        this.levelManager.writeNbt(view);
    }

    @ModifyVariable(method = "addExhaustion", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/player/HungerManager;addExhaustion(F)V"), ordinal = 0, argsOnly = true)
    private float addExhaustionMixin(float original) {
        original *= BonusHelper.exhaustionReductionBonus(this.playerEntity);
        return original;
    }

    @ModifyVariable(method = "attack", at = @At(value = "INVOKE_ASSIGN", target = "Lnet/minecraft/item/Item;getBonusAttackDamage(Lnet/minecraft/entity/Entity;FLnet/minecraft/entity/damage/DamageSource;)F"), ordinal = 0)
    private boolean attackKnockbackkMixin(boolean original) {
        if (!original && BonusHelper.meleeKnockbackAttackChanceBonus(this.playerEntity)) {
            return true;
        }
        return original;
    }

    @ModifyVariable(method = "attack", at = @At(value = "INVOKE_ASSIGN", target = "Lnet/minecraft/item/Item;getBonusAttackDamage(Lnet/minecraft/entity/Entity;FLnet/minecraft/entity/damage/DamageSource;)F"), ordinal = 1)
    private boolean attackCriticalMixin(boolean original) {
        if (!original && BonusHelper.meleeCriticalAttackChanceBonus(this.playerEntity)) {
            return true;
        }
        return original;
    }

    @ModifyVariable(method = "attack", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/player/PlayerEntity;getWeaponStack()Lnet/minecraft/item/ItemStack;"), ordinal = 0)
    private float attackMixin(float original) {
        if (this.playerEntity.isCreative()) {
            return original;
        }
        if (!levelManager.hasRequiredItemLevel(getWeaponStack().getItem())) {
            return 0.0f;
        }
        return original;
    }

    //@ModifyVariable(method = "attack", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/World;playSound(Lnet/minecraft/entity/player/PlayerEntity;DDDLnet/minecraft/sound/SoundEvent;Lnet/minecraft/sound/SoundCategory;FF)V", ordinal = 0), ordinal = 0)
    //private float attackCriticalDamageMixin(float original) {
    //    original += BonusHelper.meleeCriticalDamageBonus(this.playerEntity);
    //    return original;
    //}

    @ModifyVariable(method = "attack", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/Entity;getVelocity()Lnet/minecraft/util/math/Vec3d;"), ordinal = 3)
    private float attackDoubleDamageMixin(float original) {
        if (BonusHelper.meleeDoubleDamageBonus(this.playerEntity)) {
            original *= 2f;
        }
        return original;
    }

    @Inject(method = "damage", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/player/PlayerEntity;dropShoulderEntities()V"), cancellable = true)
    private void damageMixin(ServerWorld world, DamageSource source, float amount, CallbackInfoReturnable<Boolean> cir) {
        BonusHelper.damageReflectionBonus(this.playerEntity, source, amount);
        if (!source.isIn(DamageTypeTags.BYPASSES_INVULNERABILITY) && BonusHelper.evadingDamageBonus(this.playerEntity)) {
            cir.setReturnValue(false);
        }
    }

    //@Inject(method = "eatFood", at = @At(value = "HEAD"))
    //private void eatFoodMixin(World world, ItemStack stack, FoodComponent foodComponent, CallbackInfoReturnable<ItemStack> info) {
    //    BonusHelper.foodIncreasionBonus(this.playerEntity, stack);
    //}

    @Shadow
    public abstract ItemStack getWeaponStack();


    @Override
    public LevelManager getLevelManager() {
        return this.levelManager;
    }

    @Override
    public void increaseKilledMobStat(Chunk chunk) {
        if (killedMobChunk != null && killedMobChunk == chunk) {
            killedMobsInChunk++;
        } else {
            killedMobChunk = chunk;
            killedMobsInChunk = 0;
        }
    }

    @Override
    public void resetKilledMobStat() {
        killedMobsInChunk = 0;
    }

    @Override
    public boolean allowMobDrop() {
        return killedMobsInChunk < ConfigInit.CONFIG.mobKillCount;
    }


//    @Override
//    protected void dropExperience(@Nullable Entity attacker) {
//        if (this.playerEntity.getEntityWorld() instanceof ServerWorld serverWorld && this.shouldDropExperience() && this.getEntityWorld().getGameRules().getBoolean(GameRules.DO_MOB_LOOT) && ConfigInit.CONFIG.resetCurrentXp) {
//            LevelExperienceOrbEntity.spawn(serverWorld, this.getEntityPos(), (int) (this.levelManager.getLevelProgress() * this.levelManager.getNextLevelExperience()));
//        }
//        super.getExperienceToDrop(attacker);
//    }


@Override
protected void dropExperience(ServerWorld serverWorld, @Nullable Entity attacker) {
    System.out.println("dropExperience chamado: " + this.getType());
    if (this.shouldDropExperience() && serverWorld.getGameRules().getValue(GameRules.DO_MOB_LOOT) && ConfigInit.CONFIG.resetCurrentXp) {
            LevelExperienceOrbEntity.spawn(serverWorld, this.getEntityPos(), (int) (this.levelManager.getLevelProgress() * this.levelManager.getNextLevelExperience()));
        System.out.println("orb custom spawnada");
        }
        super.dropExperience(serverWorld,attacker);
    }

}