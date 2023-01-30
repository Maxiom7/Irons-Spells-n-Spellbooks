package com.example.testmod.spells.evocation;

import com.example.testmod.capabilities.magic.PlayerMagicData;
import com.example.testmod.entity.EnhancedEvokerFang;
import com.example.testmod.spells.AbstractSpell;
import com.example.testmod.spells.SpellType;
import com.example.testmod.util.Utils;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec2;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;

public class FangWardSpell extends AbstractSpell {
    public FangWardSpell() {
        this(1);
    }

    public FangWardSpell(int level) {
        super(SpellType.FANG_WARD_SPELL);
        this.level = level;
        this.manaCostPerLevel = 5;
        this.baseSpellPower = 6;
        this.spellPowerPerLevel = 3;
        this.castTime = 20;
        this.baseManaCost = 5;
        this.cooldown = 0;

        uniqueInfo.add(Component.translatable("ui.testmod.fang_count", getRings()));
        uniqueInfo.add(Component.translatable("ui.testmod.damage", Utils.stringTruncation(getDamage(null), 1)));
    }

    @Override
    public void onClientPreCast(Level level, LivingEntity entity, InteractionHand hand, @Nullable PlayerMagicData playerMagicData) {
        entity.playSound(SoundEvents.EVOKER_PREPARE_ATTACK, 1.0f, 1.0f);
    }

    @Override
    public void onServerPreCast(Level level, LivingEntity entity, @Nullable PlayerMagicData playerMagicData) {
        entity.playSound(SoundEvents.EVOKER_PREPARE_ATTACK, 1.0f, 1.0f);
    }

    @Override
    public void onClientCast(Level level, LivingEntity entity, PlayerMagicData playerMagicData) {
    }

    @Override
    public void onCast(Level world, LivingEntity entity, PlayerMagicData playerMagicData) {
        int rings = getRings();
        int count = 5;
        Vec3 center = entity.getEyePosition();

        for (int r = 0; r < rings; r++) {
            float fangs = count + r * 2;
            for (int i = 0; i < fangs; i++) {
                Vec3 spawn = center.add(new Vec3(0, 0, 1.5 * (r + 1)).yRot(((6.281f / fangs) * i)));
                spawn = new Vec3(spawn.x, getGroundLevel(world, spawn, 5), spawn.z);
                if (!world.getBlockState(new BlockPos(spawn).below()).isAir()) {
                    EnhancedEvokerFang fang = new EnhancedEvokerFang(world, spawn.x, spawn.y, spawn.z, get2DAngle(center, spawn), r, entity, getDamage(entity));
                    world.addFreshEntity(fang);
                }
            }
        }
    }

    private float get2DAngle(Vec3 a, Vec3 b) {
        return Utils.getAngle(new Vec2((float) a.x, (float) a.z), new Vec2((float) b.x, (float) b.z));
    }

    private int getGroundLevel(Level level, Vec3 start, int maxSteps) {
        if (!level.getBlockState(new BlockPos(start)).isAir()) {
            for (int i = 0; i < maxSteps; i++) {
                start = start.add(0, 1, 0);
                if (level.getBlockState(new BlockPos(start)).isAir())
                    break;
            }
        }
        //Vec3 upper = level.clip(new ClipContext(start, start.add(0, maxSteps, 0), ClipContext.Block.COLLIDER, ClipContext.Fluid.NONE, null)).getLocation();
        Vec3 lower = level.clip(new ClipContext(start, start.add(0, maxSteps * -2, 0), ClipContext.Block.COLLIDER, ClipContext.Fluid.NONE, null)).getLocation();
        return (int) lower.y;
    }

    private float getDamage(LivingEntity entity) {
        return getSpellPower(entity);
    }

    private int getRings() {
        return 2 + (level - 1) / 3;
    }
}