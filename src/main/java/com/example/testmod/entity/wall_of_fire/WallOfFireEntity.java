package com.example.testmod.entity.wall_of_fire;

import com.example.testmod.TestMod;
import com.example.testmod.entity.AbstractShieldEntity;
import com.example.testmod.entity.ShieldPart;
import com.example.testmod.registries.EntityRegistry;
import com.example.testmod.util.ParticleHelper;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.Packet;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.entity.IEntityAdditionalSpawnData;
import net.minecraftforge.entity.PartEntity;
import net.minecraftforge.network.NetworkHooks;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class WallOfFireEntity extends AbstractShieldEntity implements IEntityAdditionalSpawnData {
    protected ShieldPart[] subEntities;
    protected List<Vec3> partPositions = new ArrayList<>();
    protected List<Vec3> anchorPoints = new ArrayList<>();

    @Nullable
    private UUID ownerUUID;
    @Nullable
    private Entity cachedOwner;
    protected float damage;

    protected int lifetime = 8 * 20;

    public WallOfFireEntity(EntityType<?> pEntityType, Level pLevel) {
        super(pEntityType, pLevel);
        TestMod.LOGGER.debug("WallOfFire.attempting to create sub entities");
        subEntities = new ShieldPart[0];

    }

    @Override
    public void takeDamage(DamageSource source, float amount, @Nullable Vec3 location) {

    }

    public WallOfFireEntity(Level level, Entity owner, List<Vec3> anchors, float damage) {
        this(EntityRegistry.WALL_OF_FIRE_ENTITY.get(), level);
        this.anchorPoints = anchors;
        createShield();
        this.damage = damage;
        setOwner(owner);
    }

    @Override
    public void tick() {
        for (int i = 0, subEntitiesLength = subEntities.length; i < subEntitiesLength; i++) {
            PartEntity<?> subEntity = subEntities[i];
            Vec3 pos = partPositions.get(i);
            subEntity.setPos(pos);
            subEntity.xo = pos.x;
            subEntity.yo = pos.y;
            subEntity.zo = pos.z;
            subEntity.xOld = pos.x;
            subEntity.yOld = pos.y;
            subEntity.zOld = pos.z;
            if (level.isClientSide) {
                PartEntity<?> nextEntity = subEntities[i + 1 < subEntitiesLength ? i + 1 : i - 1];

                for (int j = 0; j < 2; j++) {
                    double offset = .25;
                    double ox = (Math.random() * 2 * offset - offset);
                    double oy = Math.random() * 2 * offset - offset;
                    double oz = Math.random() * 2 * offset - offset;
                    Vec3 next = pos.subtract(nextEntity.position()).scale(.5f);
                    level.addParticle(ParticleHelper.FIRE, pos.x + ox + next.x * j, pos.y + oy - .25 + next.y * j, pos.z + oz + next.z * j, 0, Math.random() * .3, 0);
                }
            } else {
                for (LivingEntity livingentity : this.level.getEntitiesOfClass(LivingEntity.class, subEntity.getBoundingBox().inflate(0.2D, 0.0D, 0.2D))) {
                    this.dealDamageTo(livingentity);
                }
            }
        }
        if (!level.isClientSide && --lifetime < 0)
            discard();
    }

    private void dealDamageTo(LivingEntity pTarget) {
        Entity owner = this.getOwner();
        if (pTarget.isAlive() && !pTarget.isInvulnerable() && pTarget != owner) {
            if (owner == null) {
                pTarget.hurt(DamageSource.MAGIC, damage);
                pTarget.setSecondsOnFire(3);
            } else {
                if (owner.isAlliedTo(pTarget)) {
                    return;
                }

                pTarget.hurt(DamageSource.indirectMagic(this, owner), damage);
                pTarget.setSecondsOnFire(3);
            }

        }
    }

    @Override
    public void createShield() {
        TestMod.LOGGER.debug("Attempting to create shield, achor points length: {}", anchorPoints.size());
        float height = 3;
        float step = .8f;
        List<ShieldPart> entitiesList = new ArrayList<>();
        TestMod.LOGGER.debug("WallOfFire:Creating shield");
        for (int i = 0; i < anchorPoints.size() - 1; i++) {
            Vec3 start = anchorPoints.get(i);
            Vec3 end = anchorPoints.get(i + 1);
            Vec3 dirVec = end.subtract(start).normalize().scale(step);
            int steps = (int) ((start.distanceTo(end) + .5) / step);
            for (int currentStep = 0; currentStep < steps; currentStep++) {
                //MagicManager.spawnParticles(level, ParticleTypes.DRAGON_BREATH, start.x + dirVec.x * x, start.y + dirVec.y * x, start.z + dirVec.z * x, 1, 0, 0, 0, 0, true);
                ShieldPart part = new ShieldPart(this, "part" + i * steps + currentStep, .55f, height);
                double x = start.x + dirVec.x * currentStep;
                double y = start.y + dirVec.y * currentStep;
                double z = start.z + dirVec.z * currentStep;
                double groundY = level.getHeight(Heightmap.Types.MOTION_BLOCKING, (int) x, (int) z);
                //y += Math.min(5, Math.abs(y - groundY)) * y < groundY ? 1 : -1;

                if (Math.abs(y - groundY) < 2)
                    y += (groundY - y) * .75;
                //Vec3 pos = new Vec3(, start.y + dirVec.y * x, start.z + dirVec.z * x);

                Vec3 pos = new Vec3(x, y, z);

                partPositions.add(pos);
                TestMod.LOGGER.debug("WallOfFire:Creating shield: new sub entity {}", pos);
                entitiesList.add(part);
            }

        }
        //subEntities = new ShieldPart[entitiesList.size()];
        subEntities = entitiesList.toArray(subEntities);
        TestMod.LOGGER.debug("WallOfFire.createShield (array length: {}, real length: {}),", subEntities.length, entitiesList.size());

    }


    public void setOwner(@Nullable Entity pOwner) {
        if (pOwner != null) {
            this.ownerUUID = pOwner.getUUID();
            this.cachedOwner = pOwner;
        }

    }

    @Nullable
    public Entity getOwner() {
        if (this.cachedOwner != null && !this.cachedOwner.isRemoved()) {
            return this.cachedOwner;
        } else if (this.ownerUUID != null && this.level instanceof ServerLevel) {
            this.cachedOwner = ((ServerLevel) this.level).getEntity(this.ownerUUID);
            return this.cachedOwner;
        } else {
            return null;
        }
    }

    @Override
    public PartEntity<?>[] getParts() {
        return subEntities;
    }

    @Override
    protected void addAdditionalSaveData(CompoundTag compoundTag) {
        if (this.ownerUUID != null) {
            compoundTag.putUUID("Owner", this.ownerUUID);
        }
        compoundTag.putInt("lifetime", lifetime);
        ListTag anchors = new ListTag();
        for (Vec3 vec : anchorPoints) {
            CompoundTag anchor = new CompoundTag();
            anchor.putFloat("x", (float) vec.x);
            anchor.putFloat("y", (float) vec.y);
            anchor.putFloat("z", (float) vec.z);
            anchors.add(anchor);
        }
        compoundTag.put("Anchors", anchors);
        super.addAdditionalSaveData(compoundTag);
    }

    @Override
    protected void readAdditionalSaveData(CompoundTag compoundTag) {
        if (compoundTag.hasUUID("Owner")) {
            this.ownerUUID = compoundTag.getUUID("Owner");
        }
        if (compoundTag.contains("lifetime"))
            this.lifetime = compoundTag.getInt("lifetime");

        //9 is list tag id
        anchorPoints = new ArrayList<>();
        if (compoundTag.contains("Anchors", 9)) {
            ListTag anchors = (ListTag) compoundTag.get("Anchors");
            for (Tag tag : anchors) {
                if (tag instanceof CompoundTag anchor) {
                    anchorPoints.add(new Vec3(anchor.getDouble("x"), anchor.getDouble("y"), anchor.getDouble("z")));
                }
            }
        }
        super.readAdditionalSaveData(compoundTag);

    }

    public void writeSpawnData(FriendlyByteBuf buffer) {
        TestMod.LOGGER.debug("WallOfFire.writeSpawnData");
        buffer.writeInt(anchorPoints.size());
        for (Vec3 vec : anchorPoints) {
            buffer.writeFloat((float) vec.x);
            buffer.writeFloat((float) vec.y);
            buffer.writeFloat((float) vec.z);
        }
    }

    public void readSpawnData(FriendlyByteBuf additionalData) {
        TestMod.LOGGER.debug("WallOfFire.readSpawnData");

        anchorPoints = new ArrayList<>();
        int length = additionalData.readInt();
        for (int i = 0; i < length; i++) {
            anchorPoints.add(new Vec3(additionalData.readFloat(), additionalData.readFloat(), additionalData.readFloat()));
        }
        createShield();
    }

    @Override
    public Packet<?> getAddEntityPacket() {
        return NetworkHooks.getEntitySpawningPacket(this);
    }
}