package com.ic2plus.ic2plus.entity;

import com.ic2plus.ic2plus.ModRegistry;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.Explosion;
import net.minecraft.world.World;

import java.util.HashSet;

import static com.ic2plus.ic2plus.block.StrangeQuarkBlock.ENERGY;

public class BeamAttack extends Entity {

    // Синхронизируем ID стрелка и вектор цели между сервером и клиентом
    private static final DataParameter<Integer> SHOOTER_ID = EntityDataManager.createKey(BeamAttack.class, DataSerializers.VARINT);
    private static final DataParameter<Float> TARGET_X = EntityDataManager.createKey(BeamAttack.class, DataSerializers.FLOAT);
    private static final DataParameter<Float> TARGET_Y = EntityDataManager.createKey(BeamAttack.class, DataSerializers.FLOAT);
    private static final DataParameter<Float> TARGET_Z = EntityDataManager.createKey(BeamAttack.class, DataSerializers.FLOAT);
    private static final DataParameter<String> TEXTURE_PATH = EntityDataManager.createKey(BeamAttack.class, DataSerializers.STRING);

    private int type = 0;
    private double range = 64.0D;
    private float damage = 15.0F;
    private float explosionPower = 3.0F;

    private int lifetime = 3;

    public BeamAttack(World world) {
        super(world);
        this.noClip = true;
        this.preventEntitySpawning = false;
        this.setSize(0.0F, 0.0F);
    }

    public BeamAttack(World worldIn, EntityLivingBase shooter, double range, float damage, float explosionPower, String texturePath, int type) {
        this(worldIn);
        this.range = range;
        this.damage = damage;
        this.explosionPower = explosionPower;
        this.type = type;

        this.setShooter(shooter);
        this.setTexturePath(texturePath);
        this.updatePositionAndTarget(shooter);
    }

    @Override
    protected void entityInit() {
        this.dataManager.register(SHOOTER_ID, -1);
        this.dataManager.register(TARGET_X, 0.0F);
        this.dataManager.register(TARGET_Y, 0.0F);
        this.dataManager.register(TARGET_Z, 0.0F);
        this.dataManager.register(TEXTURE_PATH, "ic2plus:textures/entity/beam.png");
    }

    @Override
    public void onUpdate() {
        super.onUpdate();

        if (!this.world.isRemote) {
            this.lifetime--;
            if (this.lifetime <= 0) {
                this.setDead();
                return;
            }
        }

        EntityLivingBase shooter = getShooter();
        if (shooter != null) {
            this.updatePositionAndTarget(shooter);

            if (!this.world.isRemote) {
                performLaserLogic(shooter);
            }
        } else if (!this.world.isRemote) {
            this.setDead();
        }
    }

    private void updatePositionAndTarget(EntityLivingBase shooter) {
        // Устанавливаем позицию энтити на уровне глаз игрока
        this.setPosition(
                shooter.posX,
                shooter.posY + shooter.getEyeHeight(),
                shooter.posZ
        );

        // Рассчитываем конечную точку (куда смотрим)
        Vec3d start = new Vec3d(this.posX, this.posY, this.posZ);
        Vec3d look = shooter.getLookVec();
        Vec3d end = start.add(look.scale(this.range));

        // Проверяем препятствия (блоки), чтобы луч упирался в них, а не шёл сквозь стены
        RayTraceResult result = this.world.rayTraceBlocks(start, end, false, true, false);
        if (result != null && result.typeOfHit == RayTraceResult.Type.BLOCK) {
            end = result.hitVec;
        }

        this.setTarget(end);
    }

    private void performLaserLogic(EntityLivingBase shooter) {
        Vec3d start = new Vec3d(this.posX, this.posY, this.posZ);
        Vec3d end = getTarget();

        AxisAlignedBB laserBox = new AxisAlignedBB(start, end).grow(0.5D);
        for (Entity entity : this.world.getEntitiesWithinAABB(Entity.class, laserBox)) {
            if (entity != this && entity != shooter && entity instanceof EntityLivingBase) {
                AxisAlignedBB entityBB = entity.getEntityBoundingBox();
                if (entityBB.calculateIntercept(start, end) != null) {
                    entity.attackEntityFrom(DamageSource.causeIndirectMagicDamage(this, shooter), this.damage);
                }
            }
        }

        switch (type) {
            case (0):
                if (this.explosionPower > 0 && start.squareDistanceTo(end) < (this.range * this.range) - 1) {
                    Explosion explosion = new Explosion(world, shooter, end.x, end.y, end.z, explosionPower, false, true);
                    explosion.doExplosionA();
                    for (net.minecraft.util.math.BlockPos pos : explosion.getAffectedBlockPositions()) {
                        this.world.setBlockToAir(pos);
                    }
                    explosion.doExplosionB(true);
                    explosion.getAffectedBlockPositions().clear();
                }
                break;
            case (1):
                double radius = 2.0;

                Vec3d dir = end.subtract(start);
                double lenSq = dir.lengthSquared();

                AxisAlignedBB box = new AxisAlignedBB(start, end).grow(radius);

                HashSet<BlockPos> destroyed = new HashSet<>();
                BlockPos.MutableBlockPos pos = new BlockPos.MutableBlockPos();

                // ---------- Первый проход ----------
                for (int x = (int) Math.floor(box.minX); x <= (int) Math.floor(box.maxX); x++) {
                    for (int y = (int) Math.floor(box.minY); y <= (int) Math.floor(box.maxY); y++) {
                        for (int z = (int) Math.floor(box.minZ); z <= (int) Math.floor(box.maxZ); z++) {

                            pos.setPos(x, y, z);

                            Vec3d p = new Vec3d(x + 0.5, y + 0.5, z + 0.5);

                            double t = p.subtract(start).dotProduct(dir) / lenSq;
                            t = Math.max(0.0, Math.min(1.0, t));

                            Vec3d closest = start.add(dir.scale(t));

                            if (closest.squareDistanceTo(p) <= radius * radius) {
                                destroyed.add(pos.toImmutable());
                            }
                        }
                    }
                }

                for (BlockPos block : destroyed) {
                    world.setBlockToAir(block);
                }
                for (BlockPos block : destroyed) {
                    for (EnumFacing side : EnumFacing.values()) {

                        BlockPos target = block.offset(side);

                        if (destroyed.contains(target))
                            continue;

                        if (world.isAirBlock(target))
                            continue;

                        if (world.getBlockState(target).getBlock() == ModRegistry.STRANGE_QUARK_BLOCK)
                            continue;

                        world.setBlockState(
                                target,
                                ModRegistry.STRANGE_QUARK_BLOCK.getDefaultState().withProperty(ENERGY, 3)
                        );

                        world.scheduleUpdate(target, ModRegistry.STRANGE_QUARK_BLOCK, 40);
                    }
                }

                break;
        }
    }

    // Обновляем время жизни, когда игрок удерживает кнопку
    public void renew() {
        this.lifetime = 3;
    }

    public EntityLivingBase getShooter() {
        int id = this.dataManager.get(SHOOTER_ID);
        Entity entity = this.world.getEntityByID(id);
        return entity instanceof EntityLivingBase ? (EntityLivingBase) entity : null;
    }

    // РАБОТА С DATA MANAGER (СИНХРОНИЗАЦИЯ)
    public void setShooter(EntityLivingBase shooter) {
        this.dataManager.set(SHOOTER_ID, shooter.getEntityId());
    }

    public Vec3d getTarget() {
        return new Vec3d(
                this.dataManager.get(TARGET_X),
                this.dataManager.get(TARGET_Y),
                this.dataManager.get(TARGET_Z)
        );
    }

    public void setTarget(Vec3d target) {
        this.dataManager.set(TARGET_X, (float) target.x);
        this.dataManager.set(TARGET_Y, (float) target.y);
        this.dataManager.set(TARGET_Z, (float) target.z);
    }

    @Override
    @net.minecraftforge.fml.relauncher.SideOnly(net.minecraftforge.fml.relauncher.Side.CLIENT)
    public boolean isInRangeToRenderDist(double distance) {
        double d0 = 256.0D;
        return distance < d0 * d0;
    }

    @Override
    @net.minecraftforge.fml.relauncher.SideOnly(net.minecraftforge.fml.relauncher.Side.CLIENT)
    public boolean isInRangeToRender3d(double x, double y, double z) {
        return true;
    }

    @Override
    public boolean canBeCollidedWith() {
        return false;
    }

    @Override
    public boolean canBePushed() {
        return false;
    }

    @Override
    public net.minecraft.util.math.AxisAlignedBB getCollisionBox(Entity entityIn) {
        return null;
    }

    @Override
    public net.minecraft.util.math.AxisAlignedBB getCollisionBoundingBox() {
        return null;
    }

    public String getTexturePath() {
        return this.dataManager.get(TEXTURE_PATH);
    }

    public void setTexturePath(String path) {
        this.dataManager.set(TEXTURE_PATH, path);
    }

    @Override
    protected void readEntityFromNBT(NBTTagCompound compound) {
    }

    @Override
    protected void writeEntityToNBT(NBTTagCompound compound) {
    }
}